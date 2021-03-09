package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.LossScenarioRequestDTO;
import de.xstampp.service.project.data.entity.LossScenario;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.data.LossScenarioDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class LossScenarioRestController {

    @Autowired
    LossScenarioDataService lossScenarioDataService;

    @Autowired
    RequestPushService push;

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();
    Logger logger = LoggerFactory.getLogger(LossRestController.class);

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/loss-scenario", method = RequestMethod.POST)
    public String createLossScenario(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
        LossScenarioRequestDTO request = deSer.deserialize(body, LossScenarioRequestDTO.class);
        LossScenario lossScenario = lossScenarioDataService.createLossScenario(UUID.fromString(projectId), request);

        if (lossScenario != null) {
            push.notify(String.valueOf(lossScenario.getId().getId()), projectId, EntityNameConstants.LOSS_SCENARIO, RequestPushService.Method.ALTER);
        }
        return ser.serialize(lossScenario);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.LOSS_SCENARIO)
    @RequestMapping(value = "{id}/loss-scenario/{lossScenarioId}", method = RequestMethod.PUT)
    public String alterLossScenario(@PathVariable(value = "id") @XStamppProjectId String projectId,
                            @PathVariable(value = "lossScenarioId") @XStamppEntityId int lossScenarioId, @RequestBody String body) throws IOException {
        LossScenarioRequestDTO lossScenario = deSer.deserialize(body, LossScenarioRequestDTO.class);

        LossScenario result = lossScenarioDataService.alterLossScenario(UUID.fromString(projectId), lossScenarioId, lossScenario);

        if (result != null) {
            push.notify(String.valueOf(lossScenarioId), projectId, EntityNameConstants.LOSS_SCENARIO, RequestPushService.Method.ALTER);
        }
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.LOSS_SCENARIO)
    @RequestMapping(value = "/{id}/loss-scenario/{lossScenarioId}", method = RequestMethod.DELETE)
    public String deleteLossScenario(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("lossScenarioId") @XStamppEntityId int lossScenarioId)
            throws IOException {

        boolean result = lossScenarioDataService.deleteLossScenario(UUID.fromString(projectId), lossScenarioId);
        if (result) {
            push.notify(projectId, String.valueOf(lossScenarioId), EntityNameConstants.LOSS_SCENARIO, RequestPushService.Method.DELETE);
        }
        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/loss-scenario/{lossScenarioId}", method = RequestMethod.GET)
    public String getLossScenarioById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("lossScenarioId") @XStamppEntityId int lossScenarioId)
            throws IOException {

        return ser.serialize(lossScenarioDataService.getLossScenarioById(UUID.fromString(projectId), lossScenarioId));
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/loss-scenario/control-action/{caId}/uca/{ucaId}", method = RequestMethod.GET)
    public String getLossScenariosByUcaAndCAId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("ucaId") @XStamppEntityId int ucaId, @PathVariable("caId") @XStamppEntityId int caId) throws IOException {

        //PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);
        return ser.serialize(lossScenarioDataService.getLossScenariosByUcaId(UUID.fromString(projectId), ucaId, caId));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "{id}/loss-scenario/search", method = RequestMethod.POST)
    public String getAllLosses(@PathVariable(value = "id") @XStamppProjectId String projectId, @RequestBody String body)
            throws IOException {
        SearchRequestDTO searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
        return ser.serialize(lossScenarioDataService.getAllLossScenarios(UUID.fromString(projectId), searchRequest.getOrderBy(),
                Integer.valueOf(searchRequest.getAmount()), Integer.valueOf(searchRequest.getFrom())));
    }
}
