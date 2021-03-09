package de.xstampp.service.project.service.data;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.UnsafeControlActionRequestDTO;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.service.dao.iface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class UnsafeControlActionDataService {

    private SecurityService security;

    private IUserDAO userDAO;
    private IUnsafeControlActionDAO unsafeControlActionDAO;
    private IUnsafeControlActionHazardLinkDAO unsafeControlActionHazardLinkDAO;
    private IUnsafeControlActionSubHazardLinkDAO unsafeControlActionSubHazardLinkDAO;
    private ILastIdDAO lastIdDAO;

    @Autowired
    public UnsafeControlActionDataService(IUserDAO userDAO,
                                          IUnsafeControlActionDAO unsafeControlActionDAO,
                                          IUnsafeControlActionHazardLinkDAO unsafeControlActionHazardLinkDAO,
                                          IUnsafeControlActionSubHazardLinkDAO unsafeControlActionSubHazardLinkDAO,
                                          ILastIdDAO lastIdDAO, SecurityService security) {
        this.userDAO = userDAO;
        this.unsafeControlActionDAO = unsafeControlActionDAO;
        this.unsafeControlActionHazardLinkDAO = unsafeControlActionHazardLinkDAO;
        this.unsafeControlActionSubHazardLinkDAO = unsafeControlActionSubHazardLinkDAO;
        this.lastIdDAO = lastIdDAO;
        this.security = security;
    }

	public UnsafeControlAction createUnsafeControlAction(UnsafeControlActionRequestDTO request, UUID projectId, int controllerId) {
		UnsafeControlAction entity =  new UnsafeControlAction();
		entity.setId(new EntityDependentKey(projectId, controllerId, lastIdDAO.getNewIdForUnsafeControlAction(projectId, controllerId)));
		entity.setDescription(request.getDescription());
		entity.setName(request.getName());
		entity.setCategory(request.getCategory());
		entity.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		entity.setState(request.getState());
		unsafeControlActionDAO.makePersistent(entity);
		return entity;
	}
	
	public boolean deleteUnsafeControlAction(UUID projectId, int controlActionId, int unsafeControlActionId) {
		UnsafeControlAction entity = unsafeControlActionDAO.findById(new EntityDependentKey(projectId, controlActionId, unsafeControlActionId), false);
		unsafeControlActionDAO.makeTransient(entity);
		return true;
	}
	
	public UnsafeControlAction getUnsafeControlAction(UUID projectId, int controlActionId, int unsafeControlActionId) {
		return unsafeControlActionDAO
				.findById(new EntityDependentKey(projectId, controlActionId, unsafeControlActionId), false);
	}
	
	public List<UnsafeControlAction> getUnsafeControlActionsByControlActionId(UUID projectId, int controlActionId, Integer amount, Integer from) {
		//kanst du da was mit entity depended key machen/abschauen?
		if(amount != null && from != null) {
			return unsafeControlActionDAO.getUnsafeControlActionsByControlActionId(projectId, controlActionId, amount.intValue(), from.intValue());
		} else {
			return unsafeControlActionDAO.getUnsafeControlActionsByControlActionId(projectId, controlActionId, 1000, 0);
		}
	}
	
	public boolean createUnsafeControlActionHazardLink(UUID projectId, int controlActionId, int ucaId, int hazardId) {
		UnsafeControlActionHazardLink ucaHazardLink = new UnsafeControlActionHazardLink();
		ucaHazardLink.setControlActionId(controlActionId);
		ucaHazardLink.setHazardId(hazardId);
		ucaHazardLink.setProjectId(projectId);
		ucaHazardLink.setUnsafeControlActionId(ucaId);
		return  unsafeControlActionHazardLinkDAO.addLink(ucaHazardLink);
	}

	public UnsafeControlAction alterUnsafeControlAction(UnsafeControlActionRequestDTO request, UUID projectId, int controlActionId,
			int ucaId) {
		UnsafeControlAction entity = unsafeControlActionDAO.findById(new EntityDependentKey(projectId, controlActionId, ucaId), false);
		entity.setDescription(request.getDescription());
		entity.setName(request.getName());
		entity.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		entity.setState(request.getState());
		entity.setLockExpirationTime(Timestamp.from(Instant.now()));
		unsafeControlActionDAO.updateExisting(entity);
		return entity;
	}

    public boolean deleteUnsafeControlActionHazardLink(UUID projectId, int controlActionId, int ucaId, int hazardId) {
        return unsafeControlActionHazardLinkDAO.deleteLink(projectId, ucaId, hazardId, controlActionId);
    }

    public List<UnsafeControlAction> getUnsafeControlActionsByHazard(UUID projectId, int hazardId, Integer amount, Integer from) {
        List<UnsafeControlActionHazardLink> links;
        links = unsafeControlActionHazardLinkDAO.getLinksByHazardId(projectId, hazardId);

        if (links.size() == 0) {
            return null;
        }

        List<EntityDependentKey> ucaPKeys = new ArrayList<>();

        for (UnsafeControlActionHazardLink link : links) {
            ucaPKeys.add(new EntityDependentKey(projectId, link.getControlActionId(), link.getUnsafeControlActionId()));
        }

        List<UnsafeControlAction> requestResult = unsafeControlActionDAO.findUnsafeControlActionsByIds(ucaPKeys, from, amount);

        if (requestResult.size() > 0) {
            return requestResult;
        } else {
            return null;
        }
    }

    public List<UnsafeControlAction> getUnsafeControlActionsBySubHazard(UUID projectId, int hazardId, int subHazardId,
                                                                        Integer amount, Integer from) {
        List<UnsafeControlActionSubHazardLink> links;

        links = unsafeControlActionSubHazardLinkDAO.getLinksBySubHazardId(projectId, hazardId, subHazardId);

        if (links.size() == 0) {
            return null;
        }

        List<EntityDependentKey> ucaPKeys = new ArrayList<>();

        for (UnsafeControlActionSubHazardLink link : links) {
            ucaPKeys.add(new EntityDependentKey(projectId, link.getControlActionId(), link.getUnsafeControlActionId()));
        }

        List<UnsafeControlAction> requestResult = unsafeControlActionDAO.findUnsafeControlActionsByIds(ucaPKeys, from, amount);

        if (requestResult.size() > 0) {
            return requestResult;
        } else {
            return null;
        }
    }

    public List<UnsafeControlAction> getUnsafeControlActionsByControlActionIdAndType(UUID projectId, int controlActionId, Integer amount,
                                                                                     Integer from, Filter type) {
        List<UnsafeControlAction> ucaList;

        if (amount != null && from != null) {
            ucaList = unsafeControlActionDAO.findUnsafeControlActionsByControlActionAndType(projectId, controlActionId, amount, from, type);
        } else {
            ucaList = unsafeControlActionDAO.findUnsafeControlActionsByControlActionAndType(projectId, controlActionId, 1000, 0, type);
        }

        return ucaList;
    }

    public Integer getUnsafeControlActionsCountByControlActionIdAndType(UUID projectId, int controlActionId, Filter type) {
        int ucaCount = 0;
        ucaCount = unsafeControlActionDAO.countUnsafeControlActionsByControlActionAndType(projectId, controlActionId, type);
        return ucaCount;
    }

    public boolean createUnsafeControlActionSubHazardLink(UUID projectId, int controlActionId, int ucaId, int hazardId,
                                                          int subHazardId) {
        UnsafeControlActionSubHazardLink ucaSubHazardLink = new UnsafeControlActionSubHazardLink();
        ucaSubHazardLink.setControlActionId(controlActionId);
        ucaSubHazardLink.setHazardId(hazardId);
        ucaSubHazardLink.setProjectId(projectId);
        ucaSubHazardLink.setUnsafeControlActionId(ucaId);
        ucaSubHazardLink.setSubHazardId(subHazardId);
        return unsafeControlActionSubHazardLinkDAO.addLink(ucaSubHazardLink);
    }

    public boolean deleteUnsafeControlSubActionHazardLink(UUID projectId, int controlActionId, int ucaId, int hazardId,
                                                          int subHazardId) {
        return unsafeControlActionSubHazardLinkDAO.deleteLink(projectId, ucaId, hazardId, subHazardId, controlActionId);
    }

    /**
     * @param projectId      id of project
     * @param filter
     * @param orderBy        attribute ordering is based on
     * @param orderDirection ascending or descending
     * @param amount         paging param
     * @param from           paging param
     * @return list of all UCA entitites contained in project
     */
    public List<UnsafeControlAction> getAllUCA(UUID projectId, Filter filter,
                                               String orderBy, String orderDirection, Integer amount, Integer from) {
        Map<String, SortOrder> map = new LinkedHashMap<>();
        if (orderBy != null && orderDirection != null) {
            map.put(orderBy, SortOrder.ASC);
        }
        if (amount != null && from != null) {
            return unsafeControlActionDAO.findFromTo(from.intValue(), amount.intValue(), map, projectId);
        } else {
            return unsafeControlActionDAO.findFromTo(0, 100, map, projectId);
        }
    }

    /**
     * There is at most one controller constraint associated with an UCA. This method retrieves it.
     *
     * @param projectId id of a project
     * @param caId      control action id
     * @param ucaId     id of an UCA belonging to the control action with id equal to caId.
     * @return matching controller constraint entity
     */
    public ControllerConstraint getControllerConstraintByUnsafeControlAction(UUID projectId, int caId, int ucaId) {
        return unsafeControlActionDAO.getControllerConstraintByUnsafeControlAction(projectId, caId, ucaId);
    }
}
