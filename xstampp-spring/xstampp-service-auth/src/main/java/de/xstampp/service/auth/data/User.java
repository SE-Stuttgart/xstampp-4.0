package de.xstampp.service.auth.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Table(name = "user", schema = "public")
@Entity
public class User {

	@Column(name = "id")
	@Id
	private UUID id;

	@Column(name = "email_address")
	private String email;

	@Column(name = "password")
	private String passwordHash;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "is_system_admin")
	private boolean isSystemAdmin;

	@Column(name = "num_unsuccessful_attempts")
	private int numUnsuccessfulAttempts;

	@Column(name = "locked_until")
	private Timestamp lockedUntil;

	@Column(name = "unlock_token")
	private UUID unlockToken;

	@Column(name = "theme")
	private Integer theme;

	@Column(name = "icon")
	private String iconPath;

	@Column(name = "last_login")
	private Timestamp lastLogin;

	public User() {
		/* default constructor for hibernate */
	}

	public User(String email, String displayName, String password) {
		this();
		this.email = email;
		this.displayName = displayName;
		this.passwordHash = password;
		this.theme = 0;
		this.iconPath = "";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isSystemAdmin() {
		return isSystemAdmin;
	}

	public void setSystemAdmin(boolean isSystemAdmin) {
		this.isSystemAdmin = isSystemAdmin;
	}

	public int getNumUnsuccessfulAttempts() {
		return numUnsuccessfulAttempts;
	}

	public void setNumUnsuccessfulAttempts(int numUnsuccessfulAttempts) {
		this.numUnsuccessfulAttempts = numUnsuccessfulAttempts;
	}

	public Timestamp getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(Timestamp lockedUntil) {
		this.lockedUntil = lockedUntil;
	}
	
	public UUID getUnlockToken() {
		return unlockToken;
	}

	public void setUnlockToken(UUID unlockToken) {
		this.unlockToken = unlockToken;
	}

	public Integer getTheme() {
		return this.theme;
	}

	public void setTheme(Integer theme) {
		this.theme = theme;
	}

	public String getIconPath() {
		return this.iconPath;
	}

	public void setIcon(String iconPath) {
		this.iconPath = iconPath;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID uid) {
		this.id = uid;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void permaLockUser (UUID unlockToken) {
		this.unlockToken = unlockToken;
	}

	public void unlockPermalockedUser(UUID unlockToken) {
		//TODO Consistency with database? 
		if(this.unlockToken.equals(unlockToken)) {
			this.unlockToken = null;
			this.lockedUntil = null;
			this.numUnsuccessfulAttempts = 0;
		}
	}
	
	public boolean isUserPermaLocked() {
		return unlockToken != null;
	}
	
	public static final class EntityAttributes {
		public static final String ID = "id";
		public static final String EMAIL = "email";
		public static final String PASSWORD_HASH = "passwordHash";
		public static final String DISPLAY_NAME = "displayName";
		public static final String IS_SYSTEM_ADMIN = "isSystemAdmin";
		public static final String NUM_UNSUCCESSFUL_ATTEMPTS = "numUnsuccessfulAttempts";
		public static final String LOCKED_UNTIL = "lockedUntil";
		public static final String UNLOCK_TOKEN = "unlockToken";
		public static final String THEME = "theme";
		public static final String ICON = "iconPath";
	}
}
