package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.data.entity.Box;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.control_structure.iface.IArrowDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IBoxDAO;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ControllerRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.service.dao.control_structure.iface.IControllerDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class ControllerDataService {

	//Data Access Objects
	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;

	@Autowired
	IControllerDAO controllerData;

	@Autowired
	IBoxDAO boxData;

	@Autowired
	IArrowDAO arrowData;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	@Autowired
	IUserDAO userDAO;

	/**
	 * creates a new controller
	 * 
	 * @param projectId     the project id
	 * @param controllerDTO the controller dto object
	 * @return returns the new created controller
	 */
	public Controller createController(UUID projectId, ControllerRequestDTO controllerDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, Controller.class));
		Controller controller = new Controller();
		controller.setId(key);
		controller.setName(controllerDTO.getName());
		controller.setDescription(controllerDTO.getDescription());

		if (controllerDTO.getState() != null) {
			controller.setState(controllerDTO.getState());
		} else {
			controller.setState("DOING");
		}

		controller.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		Controller result = controllerData.makePersistent(controller);
		return result;
	}

	/**
	 * alters an existing controller
	 * 
	 * @param projectId     the project id
	 * @param controllerId  the target controller id
	 * @param controllerDTO the controller dto with the new values
	 * @return returns the altered controller
	 */
	public Controller alterController(UUID projectId, int controllerId, ControllerRequestDTO controllerDTO) {
		Controller controller = controllerData.findById(new ProjectDependentKey(projectId, controllerId), false);

		if (controller != null) {
			controller.setName(controllerDTO.getName());
			controller.setDescription(controllerDTO.getDescription());
			controller.setState(controllerDTO.getState());

			controller.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			controller.setLockExpirationTime(Timestamp.from(Instant.now()));
			Controller result = controllerData.makePersistent(controller);

			controlStructureDataService.alterBox(projectId, controllerId, "Controller", result.getName());
			return result;
		}
		return null;
	}

	/**
	 * deletes an existing controller
	 * 
	 * @param projectId    the project id
	 * @param controllerId the target controller id
	 * @return returns true if the deletion was successfully
	 */
	public boolean deleteController(UUID projectId, int controllerId) {
		Controller controller = controllerData.findById(new ProjectDependentKey(projectId, controllerId), false);
		if (controller != null) {
			controllerData.makeTransient(controller);
			controlStructureDataService.alterBox(projectId, controllerId, "Controller", "");
			return true;
		}
		return false;
	}

	/**
	 * returns a controller by its id
	 * 
	 * @param projectId    the project id
	 * @param controllerId the target controller id
	 * @return returns the controller
	 */
	public Controller getControllerById(UUID projectId, int controllerId) {
		return controllerData.findById(new ProjectDependentKey(projectId, controllerId), false);
	}

	/**
	 * get all controller with paging options
	 * 
	 * @param projectId the project id
	 * @param amount    the amount of results
	 * @param from      the elements which will be skipped
	 * @return returns the list of found controllers
	 */
	public List<Controller> getAllController(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Controller.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return controllerData.getAllUnlinkedControllers(projectId);
			} 
		}
		
		return controllerData.findFromTo(from, amount, order, projectId);
	}
	
	public Controller getControllerByBoxId (UUID projectId, String boxId) {
		return controllerData.getByBoxId(projectId, boxId);
	}
	
	public Controller setControllerBoxId (UUID projectId, int controllerId, String boxId) {
		return controllerData.setBoxId(projectId, controllerId, boxId);
	}

	/**
	 * retrieves a List of Box Entities from the Database that have an Arrow Entity pointing to the Controller, specified by its ID.
	 * @param projectId	project data should be retrieved from.
	 * @param controllerId	ID of a given controller
	 * @return	returns a List of Box Entities
	 */
	public List<Box> getSourceBoxByControllerId (UUID projectId, int controllerId){

		List<String> boxIdList = new ArrayList<String>();

		Controller controller = controllerData.findById(new ProjectDependentKey(projectId, controllerId), false);
		List<Arrow> arrowList = arrowData.AllArrowsByDestination(projectId, controller.getBoxId());


		//gets all sources from the arrows
		for (Arrow arrow : arrowList){
			boxIdList.add(arrow.getSource());
		}

		if (boxIdList.size() > 0) {
			return boxData.getBoxByIds(projectId, boxIdList);
		} else {
			return new ArrayList<>();
		}
	}
}
