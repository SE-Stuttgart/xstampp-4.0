package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.dto.IncompleteEntityDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.data.IncompleteEntitiesDataService;
import de.xstampp.service.project.util.StateControl;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class IncompleteEntitiesRestController {

    @Autowired
    IncompleteEntitiesDataService incompleteEntitiesDataService;

    @Autowired
    StateControl stateControl;

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();

    private enum KeyType {
        PROJECT_DEPENDENT, ENTITY_DEPENDENT, SINGLE
    }

    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "/{projectId}/mark-entity/{id}/", method = RequestMethod.PUT)
    public String updateState(@PathVariable("projectId") @XStamppProjectId String projectId,
            @PathVariable("id") @XStamppEntityId String id, @RequestBody String body) throws IOException {
        IncompleteEntityDTO incompleteEntityDTO = deSer.deserialize(body, IncompleteEntityDTO.class);
        Class<?> clazz = this.getClassByEntityName(incompleteEntityDTO.getEntityName());

        KeyType keyType;
        if (clazz != null) {
            keyType = this.getKeyTypeByEntityClassName(clazz);
        } else {
            return ser.serialize(new Response(false));
        }

        // Update state of entity in database
        StateControl.STATE newState = StateControl.STATE.valueOf(incompleteEntityDTO.getState());
        switch (keyType) {
        case PROJECT_DEPENDENT:
            ProjectDependentKey prjKey = new ProjectDependentKey(UUID.fromString(projectId), Integer.parseInt(id));
            incompleteEntitiesDataService.updateStateForEntity(clazz, prjKey, newState);
            break;

        case ENTITY_DEPENDENT:
            EntityDependentKey entKey = new EntityDependentKey(UUID.fromString(projectId), incompleteEntityDTO.getParentId(), Integer.parseInt(id));
            incompleteEntitiesDataService.updateStateForEntity(clazz, entKey, newState);
            break;

        default:
            // If id isn't given as project-/entity dependent key, it must be a string
            incompleteEntitiesDataService.updateStateForEntity(clazz, UUID.fromString(projectId), id, newState);
            break;
        }

        return ser.serialize(new Response(true));
    }

    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "/{projectId}/entity-state/{id}/", method = RequestMethod.GET)
    public String getState(@PathVariable("projectId") @XStamppProjectId String projectId,
            @PathVariable("id") @XStamppEntityId String id, @RequestBody String body) throws IOException {
        IncompleteEntityDTO incompleteEntityDTO = deSer.deserialize(body, IncompleteEntityDTO.class);

        // Iterate over every entry of received entities
        Class<?> clazz = this.getClassByEntityName(incompleteEntityDTO.getEntityName());

        KeyType keyType;
        if (clazz != null) {
            keyType = this.getKeyTypeByEntityClassName(clazz);
        } else {
            return null;
        }

        XStamppDependentEntity object = this.getEntityFromDatabase(clazz, projectId, keyType, id,
                incompleteEntityDTO.getParentId());

        String state;
        if (!stateControl.isStateDoing(object)) {
            state = "TODO";
        } else if (stateControl.validateEntity(object).isEmpty()) {
            state = "DONE";
        } else {
            state = "DOING";
        }

        return ser.serialize(state);
    }

    /**
     * Retrieve class object by entity name
     * 
     * @param name The name of the entity
     * @return The class object for given entity or null if class does not exist
     */
    private Class<?> getClassByEntityName(String name) {
        Class<?> clazz;
        try {
            clazz = Class.forName("de.xstampp.service.project.data.entity." + this.getClassNameForEntity(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return clazz;
    }

    /**
     * Search whether the id of the entity is given as project dependent key, entity
     * dependent key or simply as string (like in arrow or box entities)
     * 
     * @param clazz The clazz to get the key type of
     * @return The
     */
    private KeyType getKeyTypeByEntityClassName(Class<?> clazz) {

        KeyType keyType = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(ProjectDependentKey.class)) {
                keyType = KeyType.PROJECT_DEPENDENT;
                break;
            } else if (field.getType().equals(EntityDependentKey.class)) {
                keyType = KeyType.ENTITY_DEPENDENT;
                break;
            }
        }
        return keyType;
    }

    /**
     * Retrieves an entity from database by given parameters
     * 
     * @param clazz     The entity class
     * @param projectId The project to search in
     * @param keyType   The key type of entity
     * @param id        The id of the entity
     * @param parentId  Only to be set when key type is entity dependent key
     * @return The entity from database
     */
    private XStamppDependentEntity getEntityFromDatabase(Class<?> clazz, String projectId, KeyType keyType, String id,
            Integer parentId) {
        XStamppDependentEntity object;
        // Retrieve entity from database
        switch (keyType) {
        case PROJECT_DEPENDENT:
            ProjectDependentKey prjKey = new ProjectDependentKey(UUID.fromString(projectId), Integer.parseInt(id));
            object = incompleteEntitiesDataService.findEntityByNameAndId(clazz, prjKey);
            break;

        case ENTITY_DEPENDENT:
            EntityDependentKey entKey = new EntityDependentKey(UUID.fromString(projectId), parentId,
                    Integer.parseInt(id));
            object = incompleteEntitiesDataService.findEntityByNameAndId(clazz, entKey);
            break;

        default:
            // If id isn't given as project-/entity dependent key, it must be a string
            object = incompleteEntitiesDataService.findEntityByNameAndId(clazz, UUID.fromString(projectId), id);
            break;
        }
        return object;
    }

    private String getClassNameForEntity(String entityName) {
        switch (entityName) {
        case "loss":
            return "Loss";

        case "hazard":
            return "Hazard";

        case "sub_hazard":
            return "SubHazard";

        case "system_constraint":
            return "SystemConstraint";

        case "sub_system_constraint":
            return "SubSystemConstraint";

        case "controller":
            return "control_structure.Controller";

        case "controlled_process":
            return "control_structure.ControlledProcess";

        case "control_algorithm":
            return "control_structure.ControlAlgorithm";

        case "process_variable":
            return "control_structure.ProcessVariable";

        case "process_model":
            return "control_structure.ProcessModel";

        case "control_action":
            return "control_structure.ControlAction";

        case "unsafe_control_action":
            return "UnsafeControlAction";

        case "controller_constraint":
            return "ControllerConstraint";

        case "feedback":
            return "control_structure.Feedback";

        case "input":
            return "control_structure.Input";

        case "output":
            return "control_structure.Output";

        case "actuator":
            return "control_structure.Actuator";

        case "sensor":
            return "control_structure.Sensor";

        case "responsibility":
            return "Responsibility";

        case "LossScenario":
            return "LossScenario";

        case "conversion":
            return "Conversion";

        case "rule":
            return "Rule";

        case "implementation_constraint":
            return "ImplementationConstraint";

        default:
            return "";
        }
    }
}
