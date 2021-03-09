package de.xstampp.service.project.data.entity.control_structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.*;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "process_variable")
@Entity
public class ProcessVariable extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;

    @Column(name = "variable_type")
    private String variable_type;


    @Column(name = "arrow_id")
    private String arrowId;

    @Column(name = "state")
    private String state;

    public ProcessVariable() {
        // default constructor for hibernate
    }

    public ProjectDependentKey getId() {
        return id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArrowId() {
        return arrowId;
    }

    public void setArrowId(java.lang.String arrowId) {
        this.arrowId = arrowId;
    }

    public void setVariable_type(String vType) {
        this.variable_type = vType.toUpperCase();
    }

    public String getVariable_type() {
        return variable_type;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessVariable other = (ProcessVariable) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportPlainText(description));
        segment.add(new ReportTitledReference("Variable Type", variable_type, false));

        List <String> tableColumns = new ArrayList<>();
        tableColumns.add("Process Model");
        tableColumns.add("Variable Value");

        ReportTable table = new ReportTable(tableColumns, 50);

        ReportColumns columns = new ReportColumns();

        List<Integer> responsibilityReferences = new ArrayList<>();
        List<Integer> processModelReferences = new ArrayList<>();

        List<ProcessVariableResponsibilityLink> responsibilityLinks = xstamppProject.getProcessVariableResponsibilityLinks();
        List<ProcessModelProcessVariableLink> processModelProcessVariableLinks = xstamppProject.getProcessModelProcessVariableLinks();
        List<DiscreteProcessVariableValue> alldiscreteValues = xstamppProject.getDiscreteProcessVariableValues();

        List<Arrow> arrows = xstamppProject.getArrows();
        List<Box> boxes = xstamppProject.getBoxes();
        List<Controller> controllers = xstamppProject.getControllers();
        List<Input> inputs = xstamppProject.getInputs();
        List<Actuator> actuators = xstamppProject.getActuators();
        List<Sensor> sensors = xstamppProject.getSensors();
        List<ControlledProcess> controlledProcesses = xstamppProject.getControlledProcesses();

        if (arrows != null) {
            for (Arrow arrow : arrows) {
                if (arrow.getId().equals(arrowId)) {
                    for (Controller controller : controllers) {
                        if (arrow.getDestination().equals(controller.getBoxId())) {
                            segment.add(new ReportTitledReference("Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller.getId().getId(),true));
                        }
                    }
                    for (Box box : boxes) {
                        if (arrow.getSource().equals(box.getId())) {
                            switch (box.getBoxType()) {
                                case "InputBox":
                                    for (Input input : inputs) {
                                        if (arrowId.equals(input.getArrowId())) {
                                            segment.add(new ReportTitledReference("Source", ReportSegment.INPUT_ID_PREFIX + input.getId().getId(),true));
                                        }
                                    }
                                    break;
                                case "Controller":
                                    for (Controller controller : controllers) {
                                        if (box.getId().equals(controller.getBoxId())) {
                                            segment.add(new ReportTitledReference("Source", ReportSegment.CONTROLLER_ID_PREFIX + controller.getId().getId(),true));
                                        }
                                    }
                                    break;
                                case "Actuator":
                                    for (Actuator actuator : actuators) {
                                        if (box.getId().equals(actuator.getBoxId())) {
                                            segment.add(new ReportTitledReference("Source", ReportSegment.ACTUATOR_ID_PREFIX + actuator.getId().getId(),true));
                                        }
                                    }
                                    break;
                                case "Sensor":
                                    for (Sensor sensor : sensors) {
                                        if (box.getId().equals(sensor.getBoxId())) {
                                            segment.add(new ReportTitledReference("Source", ReportSegment.SENSOR_ID_PREFIX + sensor.getId().getId(),true));
                                        }
                                    }
                                    break;
                                case "ControlledProcess":
                                    for (ControlledProcess controlledProcess : controlledProcesses) {
                                        if (box.getId().equals(controlledProcess.getBoxId())) {
                                            segment.add(new ReportTitledReference("Source", ReportSegment.CONTROLLED_PROCESS_ID_PREFIX + controlledProcess.getId().getId(), true));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }

        if (responsibilityLinks != null) {
            for (ProcessVariableResponsibilityLink link : responsibilityLinks) {
                if (link.getProcessVariableId() == id.getId()) {
                    responsibilityReferences.add(link.getResponsibilityId());
                }
            }
        }

        if (processModelProcessVariableLinks != null) {
            for (ProcessModelProcessVariableLink link : processModelProcessVariableLinks) {
                if (link.getProcessVariableId() == id.getId()) {

                    processModelReferences.add(link.getProcessModelId());
                    List<String> tableRow = new ArrayList<>();

                    //Value of Process Variable in current Process Model
                    tableRow.add(ReportSegment.PROCESS_MODEL_ID_PREFIX + link.getProcessModelId());
                    tableRow.add(link.getProcessVariableValue());
                    table.addRow(tableRow);
                }
            }
        }


        columns.addColumn("Responsibilities", responsibilityReferences, ReportSegment.RESPONSIBILITY_ID_PREFIX);
        columns.addColumn("ProcessModels", processModelReferences, ReportSegment.PROCESS_MODEL_ID_PREFIX);

        if(variable_type.equals("DISCREET")){

            segment.add(new ReportList("Discrete Variable Values",
                    alldiscreteValues.stream().filter(value -> id.getProjectId().equals(value.getProjectId()) && id.getId() == value.getProcessVariableId())
                            .map(DiscreteProcessVariableValue::getVariableValue)
                            .collect(Collectors.toList())));
        }

        segment.add(columns);

        if(table.size() != 0){
            segment.add(table);
        }

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.PROCESS_VARIABLE_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ARROW_ID = "arrowId";
        public static final String VARIABLE_TYPE = "variable_type";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(ARROW_ID);
            result.add(VARIABLE_TYPE);
            return result;
        }
    }

    public static final class ProcessVariableBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private Timestamp lockExpirationTime;
        private String variable_type;
        private String arrowId;
        private String state;

        private ProcessVariableBuilder() {
        }

        public static ProcessVariableBuilder aProcessVariable() {
            return new ProcessVariableBuilder();
        }

        public ProcessVariableBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ProcessVariableBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ProcessVariableBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ProcessVariableBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ProcessVariableBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ProcessVariableBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProcessVariableBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProcessVariableBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ProcessVariableBuilder withVariable_type(String variable_type) {
            this.variable_type = variable_type;
            return this;
        }

        public ProcessVariableBuilder withArrowId(String arrowId) {
            this.arrowId = arrowId;
            return this;
        }

        public ProcessVariableBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ProcessVariableBuilder from(ProcessVariable processVariable) {
            this.lastEditorId = processVariable.getLastEditorId();
            this.lastEdited = processVariable.getLastEdited();
            this.lockHolderId = processVariable.getLockHolderId();
            this.lockHolderDisplayName = processVariable.getLockHolderDisplayName();
            this.lockExpirationTime = processVariable.getLockExpirationTime();

            this.id = processVariable.getId();
            this.name = processVariable.getName();
            this.description = processVariable.getDescription();
            this.variable_type = processVariable.getVariable_type();
            this.arrowId = processVariable.getArrowId();
            this.state = processVariable.state;
            return this;
        }

        public ProcessVariable build() {
            ProcessVariable processVariable = new ProcessVariable();
            processVariable.setLastEditorId(lastEditorId);
            processVariable.setLastEdited(lastEdited);
            processVariable.setLockHolderId(lockHolderId);
            processVariable.setLockHolderDisplayName(lockHolderDisplayName);
            processVariable.setId(id);
            processVariable.setName(name);
            processVariable.setDescription(description);
            processVariable.setLockExpirationTime(lockExpirationTime);
            processVariable.setVariable_type(variable_type);
            processVariable.setArrowId(arrowId);
            processVariable.setState(state);
            return processVariable;
        }
    }
}
