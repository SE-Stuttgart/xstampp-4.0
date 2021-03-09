package de.xstampp.service.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.auth.TokenConstants;
import de.xstampp.common.dto.Response.Status;
import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.auth.controller.UserRestController;
import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.dto.*;
import de.xstampp.service.auth.util.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
	public final static String MESSAGE_PASSWORDS_DO_NOT_MATCH = "The passwords you supplied do not match.";
	public final static String MESSAGE_REGISTER_FAILED = "Registration failed.";

	private static final long PW_LOCK_AT_ATTEMPT = 3;
	/** The duration that gets doubled for every failed attempt */
	private static final Duration PW_LOCK_BASE_TIME = Duration.ofSeconds(30);
	/**
	 * duration would be 64 minutes after the 10th attempt, exceeds one hour. MUST
	 * be smaller than 50-ish to avoid integer overflows.
	 */
	private static final int PW_COMPLETELY_LOCK_AT_ATTEMPT = 10;
	private static final int MAIL_LOCK_AT_ATTEMPT = 4;
	

	private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private ConfigurationService config;
	@Autowired
	private SecurityService security;
	@Autowired
	private MailService mailService;
	@Autowired
	private UserDataService userData;
	@Autowired
	private ProjectDataService projectData;
	@Autowired
	private GroupDataService groupData;

	@Autowired
	private ThemeDataService themeDataService;

	private String secret;

	@PostConstruct
	public void init() {
		secret = config.getStringProperty("constants.system.secret");
	}

	public RegisterResponseDTO register(RegisterRequestDTO request) {
		/*
		 * TODO for database connection:
		 * 
		 * Let DB create ids, add new user to db, check for existing user in the db
		 */
		RegisterResponseDTO response = new RegisterResponseDTO();
		if (!request.getPassword().equals(request.getPasswordRepeat())) {
			throw ErrorsAuth.PASSWORDS_NOMATCH.exc();
		}
		User newUser = new User(request.getEmail(), request.getDisplayName(),
				Hashing.createPbkdf2hash(request.getPassword()));

		if (themeDataService.getAllThemes().size() == 0) {
			ThemeRequestDTO themeRequestDTO = new ThemeRequestDTO();
			themeRequestDTO.setId(themeDataService.getNewThemeId());
			themeRequestDTO.setName("default");
			themeRequestDTO.setColors("#2699FB_#ffffff_#e30909");

			themeDataService.createTheme(themeRequestDTO);
		}

		userData.addUser(newUser);
		logger.debug("Registered user {}.", newUser.getId());

		response.setStatus(Status.SUCCESS);
		return response;
	}

	// TODO: Complete Documentation @Rico

	/** authenticate user with email and password, return long-lived token */
	public LoginResponseDTO login(LoginRequestDTO request) {
		/*
		 * TODO for database connection:
		 * 
		 * Look up user in DB
		 */

		Optional<User> userLookup = Optional.ofNullable(userData.getByEmail(request.getEmail()));

		if (userLookup.isPresent()) {
			User user = userLookup.get();
			if (user.getNumUnsuccessfulAttempts() < PW_COMPLETELY_LOCK_AT_ATTEMPT && !isUserLocked(user)) {
				if (Hashing.checkPasswordAgainstPbkdf2Hash(request.getPassword(), user.getPasswordHash())) {
					String token = JWT.create()
							.withClaim(TokenConstants.CLAIM_UID, user.getId().toString())
							.withClaim(TokenConstants.CLAIM_DISPLAY_NAME, user.getDisplayName())
							.withClaim(TokenConstants.CLAIM_TOKEN_TYPE, TokenConstants.VALUE_CLAIM_TYPE_LONGLIVED)
							.withClaim(TokenConstants.CLAIM_IS_SYSTEM_ADMIN, user.isSystemAdmin())
							.withExpiresAt(Date.from(Instant.now().plusSeconds(TokenConstants.TIME_LONGLIVED_TOKEN_SECONDS)))
							.sign(Algorithm.HMAC512(secret));

					user.setNumUnsuccessfulAttempts(0); // was successful, reset counter
					user.setLockedUntil(null);
					user.setLastLogin(Timestamp.from(Instant.now()));

					userData.updateUser(user);
					logger.debug("Logged in user {} successfully.", user.getId());
					return new LoginResponseDTO(token);
				} else {
					user.setNumUnsuccessfulAttempts(user.getNumUnsuccessfulAttempts() + 1);
					userData.updateUser(user);
					logger.debug("Unsuccessful attempt #{} for user {}.", user.getNumUnsuccessfulAttempts(),
							user.getId());

					if (user.getNumUnsuccessfulAttempts() >= PW_LOCK_AT_ATTEMPT) {
						long lockDoubling = user.getNumUnsuccessfulAttempts() - PW_LOCK_AT_ATTEMPT;
						/*
						 * 1 << lockDoubling: Bitshift to represent power of 2, because Math.pow uses
						 * doubles and we'd need to cast. This is fine as long as we allow fewer than 64
						 * tries.
						 */
						Duration lockDuration = PW_LOCK_BASE_TIME.multipliedBy(1 << lockDoubling);
						logger.debug("-> Locking user {} for {}", user.getId(), lockDuration);
						user.setLockedUntil(Timestamp.from(Instant.now().plus(lockDuration)));
						userData.updateUser(user);
						
						if (user.getNumUnsuccessfulAttempts() == MAIL_LOCK_AT_ATTEMPT) {
							mailService.sendAttemptLockMail(user.getDisplayName(), user.getEmail());
						}
						if (user.getNumUnsuccessfulAttempts() >= PW_COMPLETELY_LOCK_AT_ATTEMPT) {
							UUID unlockToken = UUID.randomUUID();
							user.permaLockUser(unlockToken);
							mailService.sendPermaLockMail(user.getDisplayName(), user.getEmail(), unlockToken, user.getId());

						}
					}
				}
			}  else {
				logger.debug("Not attempting to log in user {} because they're locked.", user.getId());
			}
		}
		throw ErrorsAuth.LOGIN_FAILED.exc();
	}

	/**
	 * Used for additional password verification before user is allowed to delete herself
	 * @param userId UUID of user
	 * @param request containing email and password
	 * @return true iff the user with id userId has the email request.email and his password is reqeust.password and
	 * the user is not locked
	 */
	public boolean checkPassword(UUID userId, LoginRequestDTO request) {
		Optional<User> userLookup = Optional.ofNullable(userData.getById(userId));
		if (userLookup.isPresent()) {
			User user = userLookup.get();
			if(!user.getId().equals(userId)) {
				logger.debug("User {} implied by request doesn't match the user id {} we want to verify", user.getId(), userId);
				return false;
			}
			if (!isUserLocked(user)) {
				if (Hashing.checkPasswordAgainstPbkdf2Hash(request.getPassword(), user.getPasswordHash())) {
					logger.debug("User {} provided the correct password", user.getId());
					return true;
				} else {
					logger.debug("user {} provided wrong password",
							user.getId());
					return false;
				}
			}  else {
				logger.debug("Not attempting to log in user {} because they're locked.", user.getId());
				return false;
			}
		}
		throw ErrorsAuth.LOGIN_FAILED.exc();
	}



	private boolean isUserLocked(User user) {
		return user.getLockedUntil() != null && user.getLockedUntil().toInstant().isAfter(Instant.now())
				|| user.isUserPermaLocked();
	}

	public void unlockPermalockedUser(UUID unlockToken, UUID userId) {
		User user = userData.getById(userId);
		user.unlockPermalockedUser(unlockToken);
		userData.updateUser(user);
	}

	public ProjectTokenResponseDTO projectToken(ProjectTokenRequestDTO request) {
		SecurityContext ctx = security.getContext();
		
		String projectId = request.getProjectId();
		UUID projectUuid = UUID.fromString(projectId);
		UUID groupId = projectData.getProjectById(projectUuid).getGroupId();
		GroupMembership membership = groupData.getMember(groupId, ctx.getUserId());
		if (membership == null) {
			throw ErrorsPerm.NEED_PROJ_VIEW.exc();
		}

		String tokenType = ctx.getTokenType();
		if (!TokenConstants.VALUE_CLAIM_TYPE_LONGLIVED.equals(tokenType)) {
			throw ErrorsAuth.NEED_LONG_TOKEN.exc();
		}
		
		String groupAccessLevel = membership.getAccessLevel();
		String token = JWT.create().withClaim(TokenConstants.CLAIM_UID, ctx.getUserId().toString())
				.withClaim(TokenConstants.CLAIM_DISPLAY_NAME, ctx.getDisplayName())
				.withClaim(TokenConstants.CLAIM_TOKEN_TYPE, TokenConstants.VALUE_CLAIM_TYPE_LONGLIVED)
				.withClaim(TokenConstants.CLAIM_PROJECT_ID, projectId)
				.withClaim(TokenConstants.CLAIM_PROJECT_ROLE, groupAccessLevel)
				.withExpiresAt(Date.from(Instant.now().plusSeconds(TokenConstants.TIME_SHORTLIVED_TOKEN_SECONDS)))
				.sign(Algorithm.HMAC512(secret));

		return new ProjectTokenResponseDTO(token);
	}

	/**
	 * Changes a user (request from an admin)
	 * <p>
	 * <strong>Note:</strong> the "is admin?" check is done in
	 * {@link UserRestController#setPassword(String, String)}. This means we know
	 * the admin is already logged in and can give them verbose error messages. This
	 * method must NOT be used for users requesting a password change on their own
	 * account or similar.
	 * 
	 * @param userId  target user
	 * @param adminId admin user (required for password check)
	 * @param request change request DTO
	 * @return true iff the change succeeded
	 */
	public boolean setPasswordforAdmin(UUID userId, UUID adminId, AdminSetPasswordRequestDTO request) {
		User user = userData.getById(userId);
		User admin = userData.getById(adminId);

		if (!Hashing.checkPasswordAgainstPbkdf2Hash(request.getAdminPassword(), admin.getPasswordHash())) {
			throw ErrorsAuth.LOGIN_FAILED.exc();
		}
		String newPassword = request.getNewPassword();
		String newPasswordVerify = request.getNewPasswordRepeat();
		if (!newPassword.equals(newPasswordVerify)) {
			throw ErrorsAuth.PASSWORDS_NOMATCH.exc();
		}
		String newPasswordHash = Hashing.createPbkdf2hash(newPassword);
		user.setPasswordHash(newPasswordHash);
		userData.updateUser(user);
		return true;
	}
}
