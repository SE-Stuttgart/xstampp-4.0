package de.xstampp.service.project.service.data;

import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.LossScenarioRequestDTO;
import de.xstampp.service.project.data.entity.LossScenario;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.ILossScenarioDAO;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class LossScenarioDataService {

    @Autowired
    ILastIdDAO lastId;

    @Autowired
    ILossScenarioDAO lossScenarioDAO;

    @Autowired
    SecurityService securityService;

    @Autowired
    IUserDAO userDAO;

    public LossScenario createLossScenario(UUID projectId, LossScenarioRequestDTO lossScenarioRequestDTO) {
        // Create key & object for loss scenario
        ProjectDependentKey key = new ProjectDependentKey(projectId, lastId.getNewIdForEntity(projectId, LossScenario.class));
        LossScenario lossScenario = new LossScenario();

        // Set data for loss scenario
        lossScenario.setId(key);
        lossScenario.setUcaId(lossScenarioRequestDTO.getUcaId());
        lossScenario.setName(lossScenarioRequestDTO.getName());
        lossScenario.setDescription(lossScenarioRequestDTO.getDescription());

        lossScenario.setState(lossScenarioRequestDTO.getState());

        lossScenario.setHeadCategory(lossScenarioRequestDTO.getHeadCategory());
        lossScenario.setSubCategory(lossScenarioRequestDTO.getSubCategory());
        lossScenario.setController1Id(lossScenarioRequestDTO.getController1Id());
        lossScenario.setController2Id(lossScenarioRequestDTO.getController2Id());
        lossScenario.setControlAlgorithm(lossScenarioRequestDTO.getControlAlgorithm());
        lossScenario.setDescription1(lossScenarioRequestDTO.getDescription1());
        lossScenario.setDescription2(lossScenarioRequestDTO.getDescription2());
        lossScenario.setDescription3(lossScenarioRequestDTO.getDescription3());
        lossScenario.setControlActionId(lossScenarioRequestDTO.getControlActionId());
        lossScenario.setInputArrowId(lossScenarioRequestDTO.getInputArrowId());
        lossScenario.setFeedbackArrowId(lossScenarioRequestDTO.getFeedbackArrowId());
        lossScenario.setInputBoxId(lossScenarioRequestDTO.getInputBoxId());
        lossScenario.setSensorId(lossScenarioRequestDTO.getSensorId());
        lossScenario.setReason(lossScenarioRequestDTO.getReason());

        lossScenario.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

        LossScenario result = lossScenarioDAO.makePersistent(lossScenario);
        return result;
    }

    public LossScenario alterLossScenario(UUID projectId, int lossScenarioId, LossScenarioRequestDTO lossScenarioRequestDTO) {
        LossScenario lossScenario = new LossScenario();
        lossScenario.setId(new ProjectDependentKey(projectId, lossScenarioId));
        lossScenario.setUcaId(lossScenarioRequestDTO.getUcaId());
        lossScenario.setName(lossScenarioRequestDTO.getName());
        lossScenario.setDescription(lossScenarioRequestDTO.getDescription());

        lossScenario.setState(lossScenarioRequestDTO.getState());

        lossScenario.setHeadCategory(lossScenarioRequestDTO.getHeadCategory());
        lossScenario.setSubCategory(lossScenarioRequestDTO.getSubCategory());
        lossScenario.setController1Id(lossScenarioRequestDTO.getController1Id());
        lossScenario.setController2Id(lossScenarioRequestDTO.getController2Id());
        lossScenario.setControlAlgorithm(lossScenarioRequestDTO.getControlAlgorithm());
        lossScenario.setDescription1(lossScenarioRequestDTO.getDescription1());
        lossScenario.setDescription2(lossScenarioRequestDTO.getDescription2());
        lossScenario.setDescription3(lossScenarioRequestDTO.getDescription3());
        lossScenario.setControlActionId(lossScenarioRequestDTO.getControlActionId());
        lossScenario.setInputArrowId(lossScenarioRequestDTO.getInputArrowId());
        lossScenario.setFeedbackArrowId(lossScenarioRequestDTO.getFeedbackArrowId());
        lossScenario.setInputBoxId(lossScenarioRequestDTO.getInputBoxId());
        lossScenario.setSensorId(lossScenarioRequestDTO.getSensorId());
        lossScenario.setReason(lossScenarioRequestDTO.getReason());

        lossScenario.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

        lossScenario.setLockExpirationTime(Timestamp.from(Instant.now()));
        LossScenario result = lossScenarioDAO.updateExisting(lossScenario);
        return result;
    }

    public boolean deleteLossScenario(UUID projectId, int lossScenarioId) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, lossScenarioId);
        LossScenario lossScenario = lossScenarioDAO.findById(key, false);

        if (lossScenario != null) {
            lossScenarioDAO.makeTransient(lossScenario);
            return true;
        }
        return false;
    }

    public LossScenario getLossScenarioById(UUID projectId, int lossScenarioId) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, lossScenarioId);
        return lossScenarioDAO.findById(key, false);
    }

    public List<LossScenario> getLossScenariosByUcaId(UUID projectId, int ucaId, int lossScenarioId) {
        List<LossScenario> lossScenarios;
        lossScenarios = lossScenarioDAO.getLossScenariosByUcaId(projectId, ucaId, lossScenarioId);
        return lossScenarios;
    }

    public List<LossScenario> getAllLossScenarios(UUID projectId, String orderBy,
                                                  Integer amount, Integer from) {

        Map<String, SortOrder> sortOrder = Map.of(orderBy, SortOrder.ASC);
        if (amount != null && from != null) {
            return lossScenarioDAO.findFromTo(from, amount, sortOrder, projectId);
        } else {
            return lossScenarioDAO.findFromTo(0, 1000, sortOrder, projectId);
        }
    }
}
