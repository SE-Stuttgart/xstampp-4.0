package de.xstampp.service.project.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.ConversionRequestDTO;
import de.xstampp.service.project.data.entity.Conversion;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.ConversionDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppEntityParentId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

/**
 * Rest controller for handling all incoming requests for Conversions
 */
@RestController
@RequestMapping("/api/project")
public class ConversionRestController {

    @Autowired
    ConversionDataService conversionData;

    @Autowired
    RequestPushService push;

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @RequestMapping(value = "/{id}/actuator/{actuatorId}/conversion", method = RequestMethod.POST)
    public String createConversion(@PathVariable("id") @XStamppProjectId String projectId,
                                   @PathVariable("actuatorId") int actuatorId, @RequestBody String body) throws IOException {
        ConversionRequestDTO request = deSer.deserialize(body, ConversionRequestDTO.class);

        Conversion result = conversionData.createConversion(request, UUID.fromString(projectId), actuatorId);
        if (result != null) {
            push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONVERSION, Method.CREATE);
        }
        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.CONVERSION)
    @RequestMapping(value = "/{id}/actuator/{actuatorId}/conversion/{conversionId}", method = RequestMethod.PUT)
    public String editConversion(@PathVariable("id") @XStamppProjectId String projectId,
                                 @PathVariable("actuatorId") @XStamppEntityParentId int actuatorId, @PathVariable("conversionId") @XStamppEntityId int conversionId,
                                 @RequestBody String body) throws IOException {
        ConversionRequestDTO request = deSer.deserialize(body, ConversionRequestDTO.class);

        Conversion result = conversionData.editConversion(request, UUID.fromString(projectId), actuatorId, conversionId);
        if (result != null) {
            push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONVERSION, Method.ALTER);
        }

        return ser.serialize(result);
    }

    @PrivilegeCheck(privilege = Privileges.DEVELOPER)
    @CheckLock(entity = EntityNameConstants.CONVERSION)
    @RequestMapping(value = "/{id}/actuator/{actuatorId}/conversion/{conversionId}", method = RequestMethod.DELETE)
    public String deleteConversion(@PathVariable("id") @XStamppProjectId String projectId,
                                   @PathVariable("actuatorId") @XStamppEntityParentId int actuatorId,
                                   @PathVariable("conversionId") @XStamppEntityId int conversionId) throws IOException {

        boolean result = conversionData.deleteConversion(UUID.fromString(projectId), actuatorId, conversionId);
        if (result) {
            push.notify(String.valueOf(conversionId), projectId, EntityNameConstants.CONVERSION, Method.DELETE);
        }
        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/actuator/{actuatorId}/conversion/{conversionId}", method = RequestMethod.GET)
    public String getConversionById(@PathVariable("id") @XStamppProjectId String projectId,
                                    @PathVariable("actuatorId") @XStamppEntityId int actuatorId,
                                    @PathVariable("conversionId") int conversionId) throws IOException {
        return ser.serialize(conversionData.getConversionById(UUID.fromString(projectId), actuatorId, conversionId));
    }

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{id}/actuator/{actuatorId}/conversion/search", method = RequestMethod.POST)
    public String getAllConversions(@PathVariable("id") @XStamppProjectId String projectId,
                                    @PathVariable("actuatorId") @XStamppEntityId int actuatorId,
                                    @RequestBody String body)
            throws IOException {
        SearchRequestDTO searchRequest;
        searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
        SortOrder order = SortOrder.valueOfIgnoreCase(searchRequest.getOrderDirection());
        return ser.serialize(conversionData.getAllConversions(UUID.fromString(projectId), actuatorId, searchRequest.getFilter(),
                searchRequest.getOrderBy(), order, searchRequest.getAmount(), searchRequest.getFrom()));
    }

}
