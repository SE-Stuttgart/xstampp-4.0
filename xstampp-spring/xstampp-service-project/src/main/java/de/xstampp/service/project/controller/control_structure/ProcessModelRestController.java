package de.xstampp.service.project.controller.control_structure;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.control_structure.ProcessModelRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.ProcessModel;
import de.xstampp.service.project.service.data.control_structure.ProcessModelDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class ProcessModelRestController {

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();

    @Autowired
    ProcessModelDataService processModelDataService;

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/process-model", method = RequestMethod.POST)
    public String createProcessModel(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body)
            throws IOException {

        ProcessModelRequestDTO processModel = deSer.deserialize(body, ProcessModelRequestDTO.class);
        ProcessModel result = processModelDataService.createProcessModel(UUID.fromString(projectId), processModel);
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.PROCESS_MODEL)
    @RequestMapping(value = "/{id}/process-model/{process-model}", method = RequestMethod.POST)
    public String alterProcessModel(@PathVariable("id") @XStamppProjectId String projectId,
                                    @PathVariable("process-model") @XStamppEntityId int processModelId, @RequestBody String body) throws IOException {

        ProcessModelRequestDTO processModel = deSer.deserialize(body, ProcessModelRequestDTO.class);
        ProcessModel result = processModelDataService.alterProcessModel(UUID.fromString(projectId), processModelId,
                processModel);
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.PROCESS_MODEL)
    @RequestMapping(value = "/{id}/process-model/{process-model}", method = RequestMethod.DELETE)
    public String deleteProcessModel(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("process-model") @XStamppEntityId Integer processModelId) throws IOException {
        boolean result = processModelDataService.deleteProcessModel(UUID.fromString(projectId), processModelId);

        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-model/{process-model}", method = RequestMethod.GET)
    public String getProcessModel(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("process-model") int processModelId) throws IOException {
        ProcessModel result = processModelDataService.getProcessModel(UUID.fromString(projectId), processModelId);
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-model/all/controllerId/{controllerId}", method = RequestMethod.GET)
    public String getAllProcessModelsForController(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") int controllerId) throws IOException {
        return ser.serialize(processModelDataService.getProcessModelsByControllerId(UUID.fromString(projectId), controllerId));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-model/unlinked", method = RequestMethod.GET)
    public String getAllUnlinkedProcessModels(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
        return ser.serialize(processModelDataService.getUnlinkedProcessModels(UUID.fromString(projectId)));
    }
}
