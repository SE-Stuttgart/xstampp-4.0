package de.xstampp.service.project.service.data;

import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.ImplementationConstraintRequestDTO;
import de.xstampp.service.project.data.entity.ImplementationConstraint;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.*;
import de.xstampp.service.project.service.dao.iface.IImplementationConstraintDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Service called by rest controller and using dao to provide CRUD for implementation constraint entities.
 */
@Service
@Transactional
public class ImplementationConstraintDataService {

    @Autowired
    ILastIdDAO lastId;

    @Autowired
    IImplementationConstraintDAO iCDAO;

    @Autowired
    SecurityService securityService;

    @Autowired
    IUserDAO userDAO;

    public ImplementationConstraint createImplementationConstraint(UUID projectId, ImplementationConstraintRequestDTO implementationConstraintRequestDTO) {
        // Create key & object for implementation constraint
        ProjectDependentKey key = new ProjectDependentKey(projectId, lastId.getNewIdForEntity(projectId, ImplementationConstraint.class));
        ImplementationConstraint implementationConstraint = new ImplementationConstraint();
        // Set data for implementation constraint
        implementationConstraint.setId(key);
        implementationConstraint.setLossScenarioId(implementationConstraintRequestDTO.getLossScenarioId());
        implementationConstraint.setName(implementationConstraintRequestDTO.getName());
        implementationConstraint.setDescription(implementationConstraintRequestDTO.getDescription());
        implementationConstraint.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
        implementationConstraint.setState(implementationConstraintRequestDTO.getState());
        implementationConstraint.setLastEdited(Timestamp.from(Instant.now()));
        ImplementationConstraint result = iCDAO.makePersistent(implementationConstraint);
        return result;
    }

    public ImplementationConstraint alterImplementationConstraint(UUID projectId, int implementationConstraintId, ImplementationConstraintRequestDTO implementationConstraintRequestDTO) {
        ImplementationConstraint implementationConstraint = this.getImplementationConstraintById(projectId, implementationConstraintId);
        implementationConstraint.setLossScenarioId(implementationConstraintRequestDTO.getLossScenarioId());
        implementationConstraint.setName(implementationConstraintRequestDTO.getName());
        implementationConstraint.setDescription(implementationConstraintRequestDTO.getDescription());
        implementationConstraint.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
        implementationConstraint.setState(implementationConstraintRequestDTO.getState());
        implementationConstraint.setLastEdited(Timestamp.from(Instant.now()));
        ImplementationConstraint result = iCDAO.updateExisting(implementationConstraint);
        return result;
    }

    public boolean deleteImplementationConstraint(UUID projectId, int implementationConstraintId) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, implementationConstraintId);
        ImplementationConstraint implementationConstraint = iCDAO.findById(key, false);
        if (implementationConstraint != null) {
            iCDAO.makeTransient(implementationConstraint);
            return true;
        }
        return false;
    }

    public ImplementationConstraint getImplementationConstraintById(UUID projectId, int implementationConstraintId) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, implementationConstraintId);
        return iCDAO.findById(key, false);
    }

    /**
     * There are 0..n implementation constraints associated with a loss scenario. This method retrieves them given
     * a loss scenario's id.
     * @param projectId id of project
     * @param lossScenarioId id of loss scenario
     * @return
     */
    public List<ImplementationConstraint> getImplementationConstraintsByLossScenarioId(UUID projectId, int lossScenarioId) {
        List<ImplementationConstraint> implementationConstraints;
        implementationConstraints = iCDAO.getImplementationConstraintsByLossScenarioId(projectId, lossScenarioId);
        return implementationConstraints;
    }

    public List<ImplementationConstraint> getAllImplementationConstraints(UUID projectId, String orderBy,
                                                  Integer amount, Integer from) {
        Map<String, SortOrder> sortOrder = Map.of(orderBy, SortOrder.ASC);
        if (amount != null && from != null) {
            return iCDAO.findFromTo(from, amount, sortOrder, projectId);
        } else {
            return iCDAO.findFromTo(0, 1000, sortOrder, projectId);
        }
    }
}
