package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

public enum ComponentType {
    /**
     * This enum represents a container component
     */
    CONTAINER("?"),
    /**
     * This constant is used to create Text Fields
     */
    TEXTFIELD,

    /**
     * This constant is used to create Dashed Boxes
     */
    DASHEDBOX,

    /**
     * This type is used to create/store a visualization of a cointroloAction
     */
    CONTROLACTION,

    /**
     * This constant is used to create Controller
     */
    CONTROLLER,

    /**
     * This constant is used to create Actuator
     */
    ACTUATOR,

    FEEDBACK("Feedback"),

    UNDEFINED("Component"),
    /**
     * This constant is used to create a new Process
     */
    CONTROLLED_PROCESS,

    /**
     * This constant is used to create a new Process Model
     */
    PROCESS_MODEL,

    /**
     * This constant is used to create a new Process Variable
     */
    PROCESS_VARIABLE,

    /**
     * This constant is used to create a new Process State
     */
    PROCESS_VALUE,

    /**
     * This constant is used to create a new Sensor
     */
    SENSOR,

    OTHER_COMPONENT,
    /**
     * This Constant is used to create a root
     */
    ROOT,

    /**
     * This constant is used to create a new Connection
     */
    CONNECTION;

    private String title;

    ComponentType() {
        this.title = "";
    }
    ComponentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}