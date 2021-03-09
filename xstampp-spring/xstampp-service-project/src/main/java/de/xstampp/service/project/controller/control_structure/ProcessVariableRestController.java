package de.xstampp.service.project.controller.control_structure;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.control_structure.ProcessVariableRequestDTO;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.ProcessVariableDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class ProcessVariableRestController {

    private SerializationUtil ser = new SerializationUtil();
    private DeserializationUtil deSer = new DeserializationUtil();

    @Autowired
    ProcessVariableDataService processVariableDataService;

    @Autowired
    RequestPushService push;

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/controller/{controllerId}/process-variable", method = RequestMethod.POST)
    public String create(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") int controllerId, @RequestBody String body)
            throws IOException {
        ProcessVariableRequestDTO processVariableDTO = deSer.deserialize(body, ProcessVariableRequestDTO.class);
        List<ProcessVariableRequestDTO> result = processVariableDataService.createProcessVariable(UUID.fromString(projectId), controllerId,
                processVariableDTO);

        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.PROCESS_VARIABLE)
    @RequestMapping(value = "/{id}/controller/{controllerId}/process-model/{pmId}/process-variable/{processVariableId}", method = RequestMethod.PUT)
    public String alter(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") int controllerId, @PathVariable("pmId") int processModelId,
                        @PathVariable("processVariableId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
        ProcessVariableRequestDTO processVariableDTO = deSer.deserialize(body, ProcessVariableRequestDTO.class);
        List<ProcessVariableRequestDTO> result = processVariableDataService.alterProcessVariable(UUID.fromString(projectId), controllerId, processModelId, id,
                processVariableDTO);

        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.PROCESS_VARIABLE)
    @RequestMapping(value = "/{id}/process-variable/{processVariableId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") @XStamppProjectId String projectId,
                         @PathVariable("processVariableId") @XStamppEntityId Integer id) throws IOException {
        boolean result = processVariableDataService.deleteProcessVariable(UUID.fromString(projectId), id);

        if (result) {
            push.notify(String.valueOf(id), projectId, EntityNameConstants.PROCESS_VARIABLE, Method.DELETE);
        }

        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-model/{pmId}/process-variable/{processVariableId}", method = RequestMethod.GET)
    public String getById(@PathVariable("id") @XStamppProjectId String projectId,
                          @PathVariable("processVariableId") int id, @PathVariable("pmId") int processModelId) throws IOException {
        return ser.serialize(processVariableDataService.getProcessVariableById(UUID.fromString(projectId), id, processModelId));
    }


    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-model/{pmId}/process-variable/source/{source}", method = RequestMethod.GET)
    public String getProcessVariablesBySourceAndProcessModel(@PathVariable("id") @XStamppProjectId String projectId,
                                                             @PathVariable("source") String source, @PathVariable("pmId") Integer processModelId) throws IOException {

        return ser.serialize(processVariableDataService.getProcessVariableBySourceAndProcessModel(UUID.fromString(projectId), source, processModelId));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-variables/all", method = RequestMethod.GET)
    public String getAllProcessVariables(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
        return ser.serialize(processVariableDataService.getAllProcessVariables(UUID.fromString(projectId)));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/process-variable/unlinked/{filter}", method = RequestMethod.GET)
    public String getAllUnlinkedVariables(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("filter") String filter) throws IOException {
        return ser.serialize(processVariableDataService.getAllUnlinkedProcessVariables(UUID.fromString(projectId), filter));
    }
}
