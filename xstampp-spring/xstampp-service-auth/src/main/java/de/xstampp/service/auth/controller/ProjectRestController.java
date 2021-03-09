package de.xstampp.service.auth.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.auth.dto.ImportDTO;
import de.xstampp.service.auth.dto.ProjectRequestDTO;
import de.xstampp.service.auth.service.ProjectDataService;
import de.xstampp.service.auth.util.PrivilegeCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class ProjectRestController {

    ProjectDataService projectDataService;
    SecurityService securityService;
    PrivilegeCheck privilegeCheck;

    SerializationUtil ser;
    DeserializationUtil deSer;

    @Autowired
    public ProjectRestController(ProjectDataService projectDataService, SecurityService securityService, PrivilegeCheck privilegeCheck) {
        this.projectDataService = projectDataService;
        this.securityService = securityService;
        this.privilegeCheck = privilegeCheck;
        this.ser = new SerializationUtil();
        this.deSer = new DeserializationUtil();
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public String createProject(@RequestBody String body) throws IOException {
        ProjectRequestDTO request = deSer.deserialize(body, ProjectRequestDTO.class);
        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectDeleteOrCreatePrivilegeByGroup(
                securityService.getContext().getUserId(), request.getGroupId())) {
            throw ErrorsPerm.NEED_GROUP_ADMPROJ.exc();
        }
        return ser.serialize(projectDataService.createProject(request));
    }


    @RequestMapping(value = "/project/{id}", method = RequestMethod.DELETE)
    public String deleteProject(@PathVariable(value = "id") String id) throws IOException {
        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectDeleteOrCreatePrivilegeByProject(
                securityService.getContext().getUserId(), UUID.fromString(id))) {
            throw ErrorsPerm.NEED_GROUP_ADMPROJ.exc();
        }
        return ser.serialize(new Response(projectDataService.deleteProject(UUID.fromString(id))));
    }

    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public String getProjectById(@PathVariable(value = "id") String id) throws IOException {
        if (securityService.getContext() == null
                || !privilegeCheck.checkIfUserHasAccessToProject(
                securityService.getContext().getUserId(), UUID.fromString(id))) {
            throw ErrorsPerm.NEED_PROJ_VIEW.exc();
        }
        return ser.serialize(projectDataService.getProjectById(UUID.fromString(id)));
    }

    @RequestMapping(value = "/project/{id}", method = RequestMethod.PUT)
    public String alterProject(@PathVariable(value = "id") String id, @RequestBody String body) throws IOException {
        ProjectRequestDTO request = deSer.deserialize(body, ProjectRequestDTO.class);
        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectEditPrivilege(
                securityService.getContext().getUserId(), UUID.fromString(id))) {
            throw ErrorsPerm.NEED_PROJ_EDIT.exc();
        }
        return ser.serialize(projectDataService.alterProject(UUID.fromString(id), request));
    }

    @RequestMapping(value = "/project/clone/{id}", method = RequestMethod.POST)
    public String cloneProject(@PathVariable(value = "id") String id, @RequestBody String body) throws IOException {
        ProjectRequestDTO projectRequestDTO = deSer.deserialize(body, ProjectRequestDTO.class);
        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectDeleteOrCreatePrivilegeByGroup(
                securityService.getContext().getUserId(), projectRequestDTO.getGroupId())) {
            throw ErrorsPerm.NEED_GROUP_ADMPROJ.exc();
        }
        if (securityService.getContext() == null
                || !privilegeCheck.checkIfUserHasAccessToProject(
                securityService.getContext().getUserId(), UUID.fromString(id))) {
            throw ErrorsPerm.NEED_PROJ_VIEW.exc();
        }
        return ser.serialize(projectDataService.cloneProject(UUID.fromString(id), projectRequestDTO));
    }

    @RequestMapping(value = "/project/example", method = RequestMethod.POST)
    public String exampleProject(@RequestBody String body)
            throws IOException {
        ProjectRequestDTO projectRequestDTO = deSer.deserialize(body, ProjectRequestDTO.class);
        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectDeleteOrCreatePrivilegeByGroup(
                securityService.getContext().getUserId(), projectRequestDTO.getGroupId())) {
            throw ErrorsPerm.NEED_GROUP_ADMPROJ.exc();
        }
        return ser.serialize(projectDataService.exampleProject(projectRequestDTO));
    }

    @RequestMapping(value = "/project/import", method = RequestMethod.POST)
    public String importProject(@RequestBody String body)
            throws IOException {

        ImportDTO importDTO = deSer.deserialize(body, ImportDTO.class);
        ProjectRequestDTO projectRequestDTO = importDTO.getProjectRequest();

        if (securityService.getContext() == null
                || !privilegeCheck.checkProjectDeleteOrCreatePrivilegeByGroup(
                securityService.getContext().getUserId(), projectRequestDTO.getGroupId())) {
            throw ErrorsPerm.NEED_GROUP_ADMPROJ.exc();
        }

        switch (importDTO.getType()) {
            case "hz":
            case "hazx":
                return ser.serialize(projectDataService.importEclipseProject(importDTO.getProjectRequest(), importDTO.getFile()));

            case "hazx4":
                return ser.serialize(projectDataService.importProject(importDTO.getProjectRequest(), importDTO.getFile()));

            default:
                throw new IOException("Unknown Project Type");
        }
    }
}
