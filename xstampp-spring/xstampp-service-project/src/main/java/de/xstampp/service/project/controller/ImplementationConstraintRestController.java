package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.ImplementationConstraintRequestDTO;
import de.xstampp.service.project.data.entity.ImplementationConstraint;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.data.ImplementationConstraintDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Standard rest controller supporting CRUD operations for implementation constraints.
 */
@RestController
@RequestMapping("/api/project")
public class ImplementationConstraintRestController {

    @Autowired
    ImplementationConstraintDataService implementationConstraintDataService;
    @Autowired
    RequestPushService push;

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/implementation-constraint", method = RequestMethod.POST)
    public String createImplementationConstraint(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
        ImplementationConstraintRequestDTO request = deSer.deserialize(body, ImplementationConstraintRequestDTO.class);
        ImplementationConstraint implementationConstraint = implementationConstraintDataService.createImplementationConstraint(UUID.fromString(projectId), request);
        if (implementationConstraint != null) {
            push.notify(String.valueOf(implementationConstraint.getId().getId()), projectId, EntityNameConstants.IMPLEMENTATION_CONSTRAINT, RequestPushService.Method.CREATE);
        }
        return ser.serialize(implementationConstraint);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.IMPLEMENTATION_CONSTRAINT)
    @RequestMapping(value = "{id}/implementation-constraint/{implementationConstraintId}", method = RequestMethod.PUT)
    public String alterImplementationConstraint(@PathVariable(value = "id") @XStamppProjectId String projectId,
                            @PathVariable(value = "implementationConstraintId") @XStamppEntityId int implementationConstraintId, @RequestBody String body) throws IOException {
        ImplementationConstraintRequestDTO implementationConstraint = deSer.deserialize(body, ImplementationConstraintRequestDTO.class);
        ImplementationConstraint result = implementationConstraintDataService.alterImplementationConstraint(UUID.fromString(projectId), implementationConstraintId, implementationConstraint);
        if (result != null) {
            push.notify(String.valueOf(implementationConstraintId), projectId, EntityNameConstants.IMPLEMENTATION_CONSTRAINT, RequestPushService.Method.ALTER);
        }
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.IMPLEMENTATION_CONSTRAINT)
    @RequestMapping(value = "/{id}/implementation-constraint/{implementationConstraintId}", method = RequestMethod.DELETE)
    public String deleteImplementationConstraint(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("implementationConstraintId") @XStamppEntityId int implementationConstraintId)
            throws IOException {
        boolean result = implementationConstraintDataService.deleteImplementationConstraint(UUID.fromString(projectId), implementationConstraintId);
        if (result) {
            push.notify(projectId, String.valueOf(implementationConstraintId), EntityNameConstants.IMPLEMENTATION_CONSTRAINT, RequestPushService.Method.DELETE);
        }
        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/implementation-constraint/{implementationConstraintId}", method = RequestMethod.GET)
    public String getImplementationConstraintById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("implementationConstraintId") @XStamppEntityId int implementationConstraintId)
            throws IOException {
        return ser.serialize(implementationConstraintDataService.getImplementationConstraintById(UUID.fromString(projectId), implementationConstraintId));
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/implementation-constraint/loss-scenario/{lossScenarioId}", method = RequestMethod.GET)
    public String getImplementationConstraintsByLossScenarioId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("lossScenarioId") @XStamppEntityId int lossScenarioId) throws IOException {
        return ser.serialize(implementationConstraintDataService.getImplementationConstraintsByLossScenarioId(UUID.fromString(projectId), lossScenarioId));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "{id}/implementation-constraint/search", method = RequestMethod.POST)
    public String getAllLosses(@PathVariable(value = "id") @XStamppProjectId String projectId, @RequestBody String body)
            throws IOException {
        SearchRequestDTO searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
        return ser.serialize(implementationConstraintDataService.getAllImplementationConstraints(UUID.fromString(projectId), searchRequest.getOrderBy(),
                Integer.valueOf(searchRequest.getAmount()), Integer.valueOf(searchRequest.getFrom())));
    }
}
