package de.xstampp.service.project.service.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.ConversionRequestDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Conversion;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.IConversionDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class ConversionDataService {

    @Autowired
    IConversionDAO conversionDAO;

    @Autowired
    SecurityService security;

    @Autowired
    ILastIdDAO lastIdDAO;

    @Autowired
    IUserDAO userDAO;

    /**
     * Generates a new Conversion with a new ID and the given parameters
     *
     * @param request    the conversion request DTO with the parameters to set
     * @param projectId  the assigned project ID
     * @param actuatorId the assigned actuator ID
     * @return returns the new Conversion
     */
    public Conversion createConversion(ConversionRequestDTO request, UUID projectId, int actuatorId) {
        int conversionId = lastIdDAO.getNewIdForConversion(projectId, actuatorId);
        Conversion conversion = new Conversion(new EntityDependentKey(projectId, actuatorId, conversionId));
        conversion.setConversion(request.getConversion());
        conversion.setControlActionId(request.getControlActionId());
        conversion.setLastEditNow(security.getContext().getUserId());
        userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
        conversion.setState(request.getState());

        Conversion result = conversionDAO.makePersistent(conversion);
        return result;
    }

    /**
     * Edits an existing conversion
     *
     * @param request      the new conversion containing all attributes including the
     *                     edited values
     * @param conversionId the ID of the existing conversion to be edited
     * @param actuatorId   the ID of the assigned actuator
     * @param projectId    the project ID of the assigned process
     * @return returns the edited conversion including all changes
     */
    public Conversion editConversion(ConversionRequestDTO request, UUID projectId, int actuatorId, int conversionId) {
        Conversion conversion = new Conversion(new EntityDependentKey(projectId, actuatorId, conversionId));
        conversion.setConversion(request.getConversion());
        conversion.setControlActionId(request.getControlActionId());
        conversion.setLastEditNow(security.getContext().getUserId());
        userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
        conversion.setState(request.getState());
        conversion.setLockExpirationTime(Timestamp.from(Instant.now()));

        Conversion result = conversionDAO.updateExisting(conversion);
        return result;
    }

    /**
     * deletes a conversion
     *
     * @param conversionId the conversion ID
     * @param actuatorId   the actuator ID
     * @param projectId    the assigned project ID
     * @return returns true if the conversion was deleted successfully
     */
    public boolean deleteConversion(UUID projectId, int actuatorId, int conversionId) {
        EntityDependentKey key = new EntityDependentKey(projectId, actuatorId, conversionId);
        Conversion conversion = conversionDAO.findById(key, false);
        if (conversion != null) {
            conversionDAO.makeTransient(conversion);
            return true;
        }
        return false;
    }

    /**
     * Get Conversion by id
     *
     * @param conversionId the conversion ID
     * @param projectId    the project ID
     * @param actuatorId   the actuator ID
     * @return the conversion for the given ID
     */
    public Conversion getConversionById(UUID projectId, int actuatorId, int conversionId) {
        EntityDependentKey key = new EntityDependentKey(projectId, actuatorId, conversionId);
        return conversionDAO.findById(key, false);
    }

    /**
     * returns a list of conversions paged by the given parameters
     *
     * @param projectId      the assigned project ID
     * @param actuatorId     the assigned actuator ID
     * @param filter         currently not used
     * @param orderBy        the parameter name for which the result should be
     *                       ordered
     * @param orderDirection the order direction (ASC or DESC)
     * @param amount         returns only the first X results
     * @param from           skips the first X results
     * @return returns a list of conversions filtered by the given criteria
     */
    public List<Conversion> getAllConversions(UUID projectId, int actuatorId, Filter filter, String orderBy, SortOrder orderDirection,
                                              Integer amount, Integer from) {

        Map<String, SortOrder> sortOrder = Map.of(orderBy, SortOrder.ASC);
        if (amount != null && from != null) {
            return conversionDAO.getConversionsByActuatorId(projectId, actuatorId, amount, from);
        } else {
            return conversionDAO.getConversionsByActuatorId(projectId, actuatorId, 0, 1000);
        }
    }

}
