package de.xstampp.service.project.controller.control_structure;

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
import de.xstampp.service.project.data.dto.control_structure.FeedbackRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.Feedback;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.FeedbackDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class FeedbackRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	FeedbackDataService feedbackDataService;
	
	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/feedback", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		FeedbackRequestDTO feedbackDTO = deSer.deserialize(body, FeedbackRequestDTO.class);
		Feedback result = feedbackDataService.createFeedback(UUID.fromString(projectId), feedbackDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.FEEDBACK, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.FEEDBACK)
	@RequestMapping(value = "/{id}/feedback/{feedbackId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("feedbackId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		FeedbackRequestDTO feedbackDTO = deSer.deserialize(body, FeedbackRequestDTO.class);
		Feedback result = feedbackDataService.alterFeedback(UUID.fromString(projectId), id, feedbackDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.FEEDBACK, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.FEEDBACK)
	@RequestMapping(value = "/{id}/feedback/{feedbackId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("feedbackId") @XStamppEntityId int id) throws IOException {
		boolean result = feedbackDataService.deleteFeedback(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.FEEDBACK, Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/feedback/{feedbackId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("feedbackId") int id) throws IOException {
		return ser.serialize(feedbackDataService.getFeedbackById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/feedback/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(feedbackDataService.getAllFeedbacks(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/feedback/arrow/{arrowId}", method = RequestMethod.GET)
	public String getByArrowId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("arrowId") String arrowId) throws IOException {
		return ser.serialize(feedbackDataService.getFeedbackByArrowId(UUID.fromString(projectId), arrowId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/feedback/{feedbackId}/arrow/{arrowId}", method = RequestMethod.PUT)
	public String setArrowId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("feedbackId") int feedbackId,
			@PathVariable("arrowId") String arrowId) throws IOException {
		// set to real null
		if (arrowId.equals("null")) {
			arrowId = null;
		}
		Feedback result = this.feedbackDataService.setFeedbackArrowId(UUID.fromString(projectId), feedbackId, arrowId);
		return ser.serialize(result);
	}
}
