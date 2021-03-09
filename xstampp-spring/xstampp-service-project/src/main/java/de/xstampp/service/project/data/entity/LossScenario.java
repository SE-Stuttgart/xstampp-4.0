package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.report.ReportConstructionException;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "loss_scenario")
@Entity
public class LossScenario extends XStamppDependentEntity implements ReportableEntity {

    static final String HEAD_CATEGORY_1 = "Failures related to the controller (for physical controllers)";
	static final String SUB_CATEGORY_1_1 = "No sub category available";

    static final String HEAD_CATEGORY_2 = "Inadequate control algorithm";
    static final String SUB_CATEGORY_2_1 = "Flawed implementation of the specified control algorithm";
    static final String SUB_CATEGORY_2_2 = "The specified control algorithm is flawed";
    static final String SUB_CATEGORY_2_3 = "The specified control algorithm becomes inadequate over time due to changes or degradation";
    static final String SUB_CATEGORY_2_4 = "The control algorithm is changed by an attacker";

    static final String HEAD_CATEGORY_3 = "Unsafe control input";
    static final String SUB_CATEGORY_3_1 = "UCA received from another controller (already addressed when considering UCAs from other controllers)";

    static final String HEAD_CATEGORY_4 = "Inadequate process model";
    static final String SUB_CATEGORY_4_1 = "Controller receives incorrect feedback/information ";
    static final String SUB_CATEGORY_4_2 = "Controller receives correct feedback/information but interprets it incorrectly or ignores it";
    static final String SUB_CATEGORY_4_3 = "Controller does not receive feedback/information when needed (delayed or never received)";

    @EmbeddedId
    @JsonUnwrapped
    ProjectDependentKey id;

    @CheckState
    @Column(name = "name")
    String name;

    @CheckState
    @Column(name = "description")
    String description;

    @CheckState
    @Column(name = "uca_id")
    Integer ucaId;

    @CheckState
    @Column(name = "head_category")
    String headCategory;

    @CheckState
    @Column(name = "sub_category")
    String subCategory;

    @CheckState
    @Column(name = "controller1_id")
    Integer controller1Id;

    @CheckState
    @Column(name = "controller2_id")
    Integer controller2Id;

    @CheckState
    @Column(name = "control_algorithm")
    Integer controlAlgorithm;

    @CheckState
    @Column(name = "description1")
    String description1;

    @CheckState
    @Column(name = "description2")
    String description2;

    @CheckState
    @Column(name = "description3")
    String description3;

    @CheckState
    @Column(name = "control_action_id")
    Integer controlActionId;

    @CheckState
    @Column(name = "input_arrow_id")
    Integer inputArrowId;

    @CheckState
    @Column(name = "feedback_arrow_id")
    Integer feedbackArrowId;

    @CheckState
    @Column(name = "input_box_id")
    String inputBoxId;

    @CheckState
    @Column(name = "sensor_id")
    Integer sensorId;

    @CheckState
    @Column(name = "reason")
    String reason;

    @Column(name = "state")
    String state;

    public LossScenario() {
        // empty constructor for hibernate
    }

    public ProjectDependentKey getId() {
        return this.id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public Integer getUcaId() {
        return this.ucaId;
    }

    public void setUcaId(Integer ucaId) {
        this.ucaId = ucaId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getController1Id() {
        return this.controller1Id;
    }

    public void setController1Id(Integer controller1Id) {
        this.controller1Id = controller1Id;
    }

    public Integer getController2Id() {
        return this.controller2Id;
    }

    public void setController2Id(Integer controller2Id) {
        this.controller2Id = controller2Id;
    }

    public Integer getControlAlgorithm() {
        return this.controlAlgorithm;
    }

    public void setControlAlgorithm(Integer controlAlgorithm) {
        this.controlAlgorithm = controlAlgorithm;
    }

    public String getDescription1() {
        return this.description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return this.description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return this.description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public Integer getControlActionId() {
        return this.controlActionId;
    }

    public void setControlActionId(Integer controlActionId) {
        this.controlActionId = controlActionId;
    }

    public Integer getInputArrowId() {
        return this.inputArrowId;
    }

    public void setInputArrowId(Integer inputArrowId) {
        this.inputArrowId = inputArrowId;
    }

    public Integer getFeedbackArrowId() {
        return this.feedbackArrowId;
    }

    public void setFeedbackArrowId(Integer feedbackArrowId) {
        this.feedbackArrowId = feedbackArrowId;
    }

    public String getInputBoxId() {
        return this.inputBoxId;
    }

    public void setInputBoxId(String inputBoxId) {
        this.inputBoxId = inputBoxId;
    }

    public Integer getSensorId() {
        return this.sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public String getHeadCategory() {
        return headCategory;
    }

    public void setHeadCategory(String headCategory) {
        this.headCategory = headCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportPlainText(description));

        if (controlActionId != null){
            segment.add(new ReportTitledReference("Control Action", ReportSegment.CONTROL_ACTION_ID_PREFIX + controlActionId, true));

            if (ucaId != null){
                segment.add(new ReportTitledReference("Unsafe Control Action", ReportSegment.UCA_ID_PREFIX + controlActionId + "." + ucaId, true));
            }
        }

        segment.add(new ReportTitledReference("Head-Category", headCategory, false));
        if(subCategory != null){
            if (!subCategory.trim().equals("")){
                segment.add(new ReportTitledReference("Sub-Category", subCategory, false));
            }
        }

        switch (headCategory) {
            case HEAD_CATEGORY_1:
                if (subCategory != null && !subCategory.trim().equals("") && !subCategory.equals(SUB_CATEGORY_1_1)) {
                    throw new ReportConstructionException("Loss Scenario [" + getReportReferenceId() + "] has " +
                            "head-category '" + HEAD_CATEGORY_1 + "'. In the definition, this head-category is " +
                            "not subdivided into sub-categories, but the Loss Scenario states the following " +
                            "sub-Category nevertheless: '" + subCategory + "'");
                }
                segment.add(new ReportTitledReference("Faulty Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller1Id, true));
                segment.add(new ReportTitledReference("Description of the failure", description1, false));
                break;
            case HEAD_CATEGORY_2:
                segment.add(new ReportTitledReference("Faulty Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller1Id, true));
                segment.add(new ReportTitledReference("Description of algorithm failure", description1, false));
                switch (subCategory) {
                    case SUB_CATEGORY_2_1:
                    case SUB_CATEGORY_2_2:
                        break;
                    case SUB_CATEGORY_2_3:
                        segment.add(new ReportTitledReference("Change / Degradation in the system", description2, false));
                        break;
                    case SUB_CATEGORY_2_4:
                        segment.add(new ReportTitledReference("Attacker", description2, false));
                        segment.add(new ReportTitledReference("Attacker procedure", description3, false));
                        break;
                    default:
                        throw new ReportConstructionException("Loss Scenario [" + getReportReferenceId() + "] has " +
                                "head-category '" + HEAD_CATEGORY_1 + "'. In the definition, this head category " +
                                "is subdivided into 4 sub-categories, but the stated sub-category is not one of " +
                                "them: '" + subCategory + "'");
                }
                break;
            case HEAD_CATEGORY_3:
                if (!subCategory.equals(SUB_CATEGORY_3_1)) {
                    throw new ReportConstructionException("Loss Scenario [ + getReportReferenceId() + ] has " +
                            "head-category '" + HEAD_CATEGORY_1 + "'. In the definition, this head category " +
                            "is subdivided in one certain sub-category, but the stated sub-category is not it: '" +
                            subCategory + "'");
                }
                segment.add(new ReportTitledReference("Source Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller1Id, true));
                segment.add(new ReportTitledReference("Target Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller2Id, true));
                break;
            case HEAD_CATEGORY_4:
                segment.add(new ReportTitledReference("Controller", ReportSegment.CONTROLLER_ID_PREFIX + controller1Id, true));
                segment.add(new ReportTitledReference("Feedback", ReportSegment.FEEDBACK_ID_PREFIX + feedbackArrowId, true));
                segment.add(new ReportTitledReference("Sensor", ReportSegment.SENSOR_ID_PREFIX + sensorId, true));
                segment.add(new ReportTitledReference("Controller Input", ReportSegment.INPUT_ID_PREFIX + inputArrowId, true));


                switch (subCategory) {
                    case SUB_CATEGORY_4_1:
                        segment.add(new ReportTitledReference("Reason", reason, false));
                        segment.add(new ReportTitledReference("Reason description", description2, false));
                        break;
                    case SUB_CATEGORY_4_2:
                    case SUB_CATEGORY_4_3:
                        String inputBoxName = xstamppProject.getBoxes().stream()
                                .filter(box -> box.getId().equals(inputBoxId)).collect(Collectors.toList()).get(0).getName();
                        segment.add(new ReportTitledReference("System Input", inputBoxName, false));
                        segment.add(new ReportTitledReference("Wrong controller internal belief", description1, false));
                        break;
                    default:
                        throw new ReportConstructionException("Loss Scenario [" + getReportReferenceId() + "] has " +
                                "head-category '" + HEAD_CATEGORY_4 + "'. In the definition, this head category " +
                                "is subdivided into 3 sub-categories, but the stated sub-category is not one of " +
                                "them: '" + subCategory + "'");
                }
                break;
            default:
                throw new ReportConstructionException("Loss Scenario [" + getReportReferenceId() + "] has an " +
                        "undefined head-category: '" + headCategory + "'");
        }


        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.LOSS_SCENARIO_ID_PREFIX + id.getId();

    }

    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String UCA_ID = "ucaId";

        public static final String HEAD_CATEGORY = "headCategory";
        public static final String SUB_CATEGORY = "subCategory";
        public static final String CONTROLLER1_ID = "controller1Id";
        public static final String CONTROLLER2_ID = "controller2Id";
        public static final String CONTROL_ALGORITHM = "controlAlgorithm";
        public static final String DESCRIPTION1 = "description1";
        public static final String DESCRIPTION2 = "description2";
        public static final String DESCRIPTION3 = "description3";
        public static final String CONTROL_ACTION_ID = "controlActionId";
        public static final String INPUT_ARROW_ID = "inputArrowId";
        public static final String FEEDBACK_ARROW_ID = "feedbackArrowId";
        public static final String INPUT_BOX_ID = "inputBoxId";
        public static final String SENSOR_ID = "sensorId";
        public static final String REASON = "reason";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(UCA_ID);

            result.add(HEAD_CATEGORY);
            result.add(SUB_CATEGORY);
            result.add(CONTROLLER1_ID);
            result.add(CONTROLLER2_ID);
            result.add(CONTROL_ALGORITHM);
            result.add(DESCRIPTION1);
            result.add(DESCRIPTION2);
            result.add(DESCRIPTION3);
            result.add(CONTROL_ACTION_ID);
            result.add(INPUT_ARROW_ID);
            result.add(FEEDBACK_ARROW_ID);
            result.add(INPUT_BOX_ID);
            result.add(SENSOR_ID);
            result.add(REASON);
            result.add(STATE);
            return result;
        }
    }

    public static final class LossScenarioBuilder {
        ProjectDependentKey id;
        String name;
        String description;
        Integer ucaId;
        String headCategory;
        String subCategory;
        Integer controller1Id;
        Integer controller2Id;
        Integer controlAlgorithm;
        String description1;
        String description2;
        String description3;
        Integer controlActionId;
        Integer inputArrowId;
        Integer feedbackArrowId;
        String inputBoxId;
        Integer sensorId;
        String reason;
        String state;
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;

        private LossScenarioBuilder() {
        }

        public static LossScenarioBuilder aLossScenario() {
            return new LossScenarioBuilder();
        }

        public LossScenarioBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public LossScenarioBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public LossScenarioBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public LossScenarioBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public LossScenarioBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public LossScenarioBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LossScenarioBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public LossScenarioBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LossScenarioBuilder withUcaId(Integer ucaId) {
            this.ucaId = ucaId;
            return this;
        }

        public LossScenarioBuilder withHeadCategory(String headCategory) {
            this.headCategory = headCategory;
            return this;
        }

        public LossScenarioBuilder withSubCategory(String subCategory) {
            this.subCategory = subCategory;
            return this;
        }

        public LossScenarioBuilder withController1Id(Integer controller1Id) {
            this.controller1Id = controller1Id;
            return this;
        }

        public LossScenarioBuilder withController2Id(Integer controller2Id) {
            this.controller2Id = controller2Id;
            return this;
        }

        public LossScenarioBuilder withControlAlgorithm(Integer controlAlgorithm) {
            this.controlAlgorithm = controlAlgorithm;
            return this;
        }

        public LossScenarioBuilder withDescription1(String description1) {
            this.description1 = description1;
            return this;
        }

        public LossScenarioBuilder withDescription2(String description2) {
            this.description2 = description2;
            return this;
        }

        public LossScenarioBuilder withDescription3(String description3) {
            this.description3 = description3;
            return this;
        }

        public LossScenarioBuilder withControlActionId(Integer controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public LossScenarioBuilder withInputArrowId(Integer inputArrowId) {
            this.inputArrowId = inputArrowId;
            return this;
        }

        public LossScenarioBuilder withFeedbackArrowId(Integer feedbackArrowId) {
            this.feedbackArrowId = feedbackArrowId;
            return this;
        }

        public LossScenarioBuilder withInputBoxId(String inputBoxId) {
            this.inputBoxId = inputBoxId;
            return this;
        }

        public LossScenarioBuilder withSensorId(Integer sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public LossScenarioBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public LossScenarioBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public LossScenarioBuilder from(LossScenario lossScenario) {
            this.id = lossScenario.getId();
            this.name = lossScenario.getName();
            this.description = lossScenario.getDescription();
            this.ucaId = lossScenario.getUcaId();
            this.headCategory = lossScenario.getHeadCategory();
            this.subCategory = lossScenario.getSubCategory();
            this.controller1Id = lossScenario.getController1Id();
            this.controller2Id = lossScenario.getController2Id();
            this.controlAlgorithm = lossScenario.getControlAlgorithm();
            this.description1 = lossScenario.getDescription1();
            this.description2 = lossScenario.getDescription2();
            this.description3 = lossScenario.getDescription3();
            this.controlActionId = lossScenario.getControlActionId();
            this.inputArrowId = lossScenario.getInputArrowId();
            this.feedbackArrowId = lossScenario.getFeedbackArrowId();
            this.inputBoxId = lossScenario.getInputBoxId();
            this.sensorId = lossScenario.getSensorId();
            this.reason = lossScenario.getReason();
            this.lastEditorId = lossScenario.getLastEditorId();
            this.lastEdited = lossScenario.getLastEdited();
            this.lockHolderId = lossScenario.getLockHolderId();
            this.lockHolderDisplayName = lossScenario.getLockHolderDisplayName();
            this.lockExpirationTime = lossScenario.getLockExpirationTime();
            this.state = lossScenario.state;
            return this;
        }

        public LossScenario build() {
            LossScenario lossScenario = new LossScenario();
            lossScenario.setLastEditorId(lastEditorId);
            lossScenario.setLastEdited(lastEdited);
            lossScenario.setLockHolderId(lockHolderId);
            lossScenario.setId(id);
            lossScenario.setLockHolderDisplayName(lockHolderDisplayName);
            lossScenario.setName(name);
            lossScenario.setLockExpirationTime(lockExpirationTime);
            lossScenario.setDescription(description);
            lossScenario.setUcaId(ucaId);
            lossScenario.setHeadCategory(headCategory);
            lossScenario.setSubCategory(subCategory);
            lossScenario.setController1Id(controller1Id);
            lossScenario.setController2Id(controller2Id);
            lossScenario.setControlAlgorithm(controlAlgorithm);
            lossScenario.setDescription1(description1);
            lossScenario.setDescription2(description2);
            lossScenario.setDescription3(description3);
            lossScenario.setControlActionId(controlActionId);
            lossScenario.setInputArrowId(inputArrowId);
            lossScenario.setFeedbackArrowId(feedbackArrowId);
            lossScenario.setInputBoxId(inputBoxId);
            lossScenario.setSensorId(sensorId);
            lossScenario.setReason(reason);
            lossScenario.setState(state);
            return lossScenario;
        }
    }
}
