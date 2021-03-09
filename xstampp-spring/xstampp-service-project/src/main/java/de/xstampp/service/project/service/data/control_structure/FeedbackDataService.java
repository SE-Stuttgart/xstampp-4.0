package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.FeedbackRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Feedback;
import de.xstampp.service.project.service.dao.control_structure.iface.IFeedbackDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class FeedbackDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;

	@Autowired
	IFeedbackDAO feedbackData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	/**
	 * creates a new feedback
	 * 
	 * @param projectId   the project id
	 * @param feedbackDTO the feedback dto with the data
	 * @return returns the new created feedback
	 */
	public Feedback createFeedback(UUID projectId, FeedbackRequestDTO feedbackDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, Feedback.class));

		Feedback feedback = new Feedback();
		feedback.setId(key);
		feedback.setName(feedbackDTO.getName());
		feedback.setDescription(feedbackDTO.getDescription());

		if (feedbackDTO.getState() != null) {
			feedback.setState(feedbackDTO.getState());
		} else {
			feedback.setState("DOING");
		}

		feedback.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		Feedback result = feedbackData.makePersistent(feedback);
		return result;
	}

	/**
	 * alters an existing feedback
	 * 
	 * @param projectId   the project id
	 * @param feedbackId  the feedback id
	 * @param feedbackDTO the feedback dto with the new data
	 * @return returns the altered feedback data
	 */
	public Feedback alterFeedback(UUID projectId, int feedbackId, FeedbackRequestDTO feedbackDTO) {
		Feedback feedback = feedbackData.findById(new ProjectDependentKey(projectId, feedbackId), false);
		String oldName = feedback.getName();

		if (feedback != null) {
			feedback.setName(feedbackDTO.getName());
			feedback.setDescription(feedbackDTO.getDescription());
			feedback.setState(feedbackDTO.getState());

			feedback.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			feedback.setLockExpirationTime(Timestamp.from(Instant.now()));
			Feedback result = feedbackData.makePersistent(feedback);

			controlStructureDataService.alterArrow(projectId, feedbackId, "Feedback", oldName, result.getName());
			return result;
		}

		return feedback;
	}

	/**
	 * deletes a feedback by id
	 * 
	 * @param projectId  the project id
	 * @param feedbackId the feedback id
	 * @return returns true if the feedback was deleted successfully
	 */
	public boolean deleteFeedback(UUID projectId, int feedbackId) {
		Feedback feedback = feedbackData.findById(new ProjectDependentKey(projectId, feedbackId), false);
		String name = feedback.getName();

		if (feedback != null) {
			feedbackData.makeTransient(feedback);
			controlStructureDataService.deleteNameInArrowLabel(projectId, feedbackId, "Feedback", name);
			return true;
		}

		return false;
	}

	/**
	 * returns a feedback by its id
	 * 
	 * @param projectId  the project id
	 * @param feedbackId the feedback id
	 * @return returns the feedback by its id
	 */
	public Feedback getFeedbackById(UUID projectId, int feedbackId) {
		return feedbackData.findById(new ProjectDependentKey(projectId, feedbackId), false);
	}

	// TODO: Complete Documentation (filter) @Rico
	/**
	 * returns a list if feedbacks with the given parameters
	 * 
	 * @param projectId the project id
	 * @param amount    the amount of results
	 * @param from      skips the first x results
	 * @return returns a list of feedbacks
	 */
	public List<Feedback> getAllFeedbacks(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Feedback.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return feedbackData.getAllUnlinkedFeedbacks(projectId);
			} 
		}
		
		return feedbackData.findFromTo(from, amount, order, projectId);
	}
	
	public List<Feedback> getFeedbackByArrowId (UUID projectId, String arrowId) {
		return feedbackData.getByArrowId(projectId, arrowId);
	}
	
	public Feedback setFeedbackArrowId (UUID projectId, int feedbackId, String arrowId) {
		return feedbackData.setArrowId(projectId, feedbackId, arrowId);
	}
}
