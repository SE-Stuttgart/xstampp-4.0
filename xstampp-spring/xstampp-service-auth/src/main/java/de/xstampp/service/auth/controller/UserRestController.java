package de.xstampp.service.auth.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.service.auth.dto.LoginRequestDTO;
import de.xstampp.service.auth.dto.UserDisplayRequestDTO;
import de.xstampp.service.auth.dto.UserIconDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.auth.dto.AdminSetPasswordRequestDTO;
import de.xstampp.service.auth.dto.AdminUserdataRequestDTO;
import de.xstampp.service.auth.service.AuthenticationService;
import de.xstampp.service.auth.service.UserDataService;
import de.xstampp.service.auth.util.PrivilegeCheck;

@RestController
@RequestMapping("/api/auth")
public class UserRestController {
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	private UserDataService userData;

	@Autowired
	private SecurityService security;

	@Autowired
	private PrivilegeCheck check;

	@Autowired
	private AuthenticationService auth;

	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public String deleteUser(@PathVariable(value = "id") String id) throws IOException {
		if (security.getContext() == null || !check.checkSystemAdminPrivilege(security.getContext().getUserId())) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		return ser.serialize(new Response(userData.deleteUser(UUID.fromString(id))));
	}

	/**
	 * This method implements deletion of a user by herself. We use PUT as request
	 * method since DELETE doesn't allow request bodies. The request body is used to
	 * supply a LoginRequestDTO comprised of email and password. This is necessary
	 * to implement the additional password authentication required for user
	 * deletion.
	 * 
	 * @param id   UUID of the user to be deleted
	 * @param body a json object holding the email and correct password of the
	 *             requesting user
	 * @return A json object indicating success or an error. In case of an error an
	 *         appropriate status code an a message explaining its reason is
	 *         provided. A summary of status codes and their reasons follows: 403 -
	 *         id of requesting user doesn't match id of user who would be deleted
	 *         403 - user didn't provide correct password in request body 409 - user
	 *         is member in groups which block his deletion 404 - user wasn't found,
	 *         maybe he has been deleted already
	 * @throws IOException in case serialization of the response object fails
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public String deleteUserByUser(@PathVariable(value = "id") String id, @RequestBody String body) throws IOException {
		UUID userId = UUID.fromString(id);
		LoginRequestDTO request = deSer.deserialize(body, LoginRequestDTO.class);
		if (!check.checkIfUserIdIsAccessor(userId)) {
			throw ErrorsPerm.PERMISSION_DENIED.exc();
		}
		if (!auth.checkPassword(security.getContext().getUserId(), request)) {
			throw ErrorsAuth.PASSWORD_INCORRECT.exc();
		}
		// user requests deletion of herself and has supplied correct password
		return ser.serialize(new Response(userData.deleteUserByUser(userId)));
	}

	@RequestMapping(value = "/user/{id}/compare", method = RequestMethod.PUT)
	public String confirmUserForEdit(@PathVariable(value = "id") String id, @RequestBody String body)
			throws IOException {
		UUID userId = UUID.fromString(id);
		LoginRequestDTO request = deSer.deserialize(body, LoginRequestDTO.class);
		if (!check.checkIfUserIdIsAccessor(userId)) {
			throw ErrorsPerm.PERMISSION_DENIED.exc();
		}
		if (!auth.checkPassword(security.getContext().getUserId(), request)) {
			throw ErrorsAuth.PASSWORD_INCORRECT.exc();
		}
		// user requests deletion of herself and has supplied correct password
		return ser.serialize(new Response(auth.checkPassword(security.getContext().getUserId(), request)));
	}

	@RequestMapping(value = "/user/{id}/password", method = RequestMethod.PUT)
	public String setPassword(@PathVariable("id") String id, @RequestBody String body) throws IOException {
		if (security.getContext() == null || !check.checkSystemAdminPrivilege(security.getContext().getUserId())) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		AdminSetPasswordRequestDTO request = deSer.deserialize(body, AdminSetPasswordRequestDTO.class);
		return ser.serialize(new Response(
				auth.setPasswordforAdmin(UUID.fromString(id), security.getContext().getUserId(), request)));
	}

	@RequestMapping(value = "/user/search", method = RequestMethod.POST)
	public String listUsers(@RequestBody String body) throws IOException {
		if (security.getContext() == null || !check.checkSystemAdminPrivilege(security.getContext().getUserId())) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);
		return ser.serialize(userData.listUsers(request.getAmount().intValue(), request.getFrom().intValue()));
	}

	@RequestMapping(value = "/user/{id}/icon", method = RequestMethod.PUT)
	public String setIcon(@PathVariable("id") String id, @RequestBody String body) throws IOException {
		UserIconDTO request = deSer.deserialize(body, UserIconDTO.class);
		String iconPath = request.getPath();
		return ser.serialize(new Response(userData.setUserIcon(UUID.fromString(id), iconPath)));
	}

	@RequestMapping(value = "/user/{id}/theme/{themeId}", method = RequestMethod.PUT)
	public String setTheme(@PathVariable("id") String id, @PathVariable("themeId") String themeId) throws IOException {
		return ser.serialize(new Response(userData.setUserTheme(UUID.fromString(id), themeId)));
	}

	@RequestMapping(value = "/user/{id}/icon", method = RequestMethod.GET)
	public String getIcon(@PathVariable("id") String id) throws IOException {
		return ser.serialize(userData.getById(UUID.fromString(id)).getIconPath());
	}

	@RequestMapping(value = "/user/{id}/theme", method = RequestMethod.GET)
	public String getTheme(@PathVariable("id") String id) throws IOException {
		return ser.serialize(userData.getById(UUID.fromString(id)).getTheme());
	}

	@RequestMapping(value = "/user/display", method = RequestMethod.POST)
	public Map<UUID, String[]> getUsersDisplay(@RequestBody String body) throws IOException {
		UserDisplayRequestDTO request = deSer.deserialize(body, UserDisplayRequestDTO.class);
		List<UUID> userIds = request.getUserIds();
		//if(security.getContext() == null || !check.checkIfAllUsersInAccessorsProject(userIds)) {
		//	throw ErrorsPerm.PERMISSION_DENIED.exc();
		//}
		return userData.getUsersDisplay(userIds);
	}

	@RequestMapping(value = "/user/{id}/email", method = RequestMethod.GET)
	public String getEmail(@PathVariable("id") String id) throws IOException {
		return ser.serialize(userData.getById(UUID.fromString(id)).getEmail());
	}

	@RequestMapping(value = "/user/{id}/setEmail", method = RequestMethod.PUT)
	public String setEmail(@PathVariable("id") String id, @RequestBody String body) throws IOException {
		if (!check.checkIfUserIdIsAccessor(UUID.fromString(id))) {
			throw ErrorsPerm.PERMISSION_DENIED.exc();
		}
		String email = deSer.deserialize(body, LoginRequestDTO.class).getEmail();
		return ser.serialize(new Response(userData.setEmail(UUID.fromString(id), email)));
	}

	/*
	 * @RequestMapping(value = "/user/{id}/checkEmail/{email}", method =
	 * RequestMethod.PUT) public String checkEmail(@PathVariable("id") String
	 * id, @PathVariable("email") String email, @RequestBody String body) throws
	 * IOException { if(!check.checkIfUserIdIsAccessor(UUID.fromString(id))) { throw
	 * ErrorsPerm.PERMISSION_DENIED.exc(); } SearchRequestDTO search =
	 * deSer.deserialize(body, SearchRequestDTO.class); return ser.serialize(new
	 * Response(userData.checkEmail(email, search.getAmount(), search.getFrom())));
	 * }
	 */
	@RequestMapping(value = "/user/{id}/user/both", method = RequestMethod.PUT)
	public String setPasswordEmailByUser(@PathVariable("id") String id, @RequestBody String body) throws IOException {
		if (!check.checkIfUserIdIsAccessor(UUID.fromString(id))) {
			throw ErrorsPerm.PERMISSION_DENIED.exc();
		}
		LoginRequestDTO user = deSer.deserialize(body, LoginRequestDTO.class);

		return ser.serialize(new Response(userData.setPasswordEmailByUser(UUID.fromString(id), user)));
	}

	@RequestMapping(value = "/user/{id}/user", method = RequestMethod.PUT)
	public String setPasswordByUser(@PathVariable("id") String id, @RequestBody String body) throws IOException {
		if (!check.checkIfUserIdIsAccessor(UUID.fromString(id))) {
			throw ErrorsPerm.PERMISSION_DENIED.exc();
		}
		LoginRequestDTO user = deSer.deserialize(body, LoginRequestDTO.class);
		String password = user.getPassword();
		return ser.serialize(new Response(userData.setPassword(UUID.fromString(id), password)));
	}

	@RequestMapping(value = "/user/{id}/displayName", method = RequestMethod.POST)
	public String setDisplayName(@PathVariable("id") String id, @RequestBody String body) throws IOException {

		AdminUserdataRequestDTO request =  deSer.deserialize(body, AdminUserdataRequestDTO.class);
		return ser.serialize(new Response(userData.updateDisplayNameAndEmail(UUID.fromString(id),request)));
	}

}
