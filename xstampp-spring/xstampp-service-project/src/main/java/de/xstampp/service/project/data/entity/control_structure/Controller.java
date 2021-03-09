package de.xstampp.service.project.data.entity.control_structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "controller")
@Entity
public class Controller extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;

    @CheckState
    private String name;

    @CheckState
    private String description;

    @Column(name = "box_id")
    private String boxId;

    @Column(name = "state")
    private String state;

    public Controller() {
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

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
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
        Controller other = (Controller) obj;
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
        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.CONTROLLER_ID_PREFIX+ id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String BOXID = "boxId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(BOXID);
            result.add(NAME);
            result.add(DESCRIPTION);
            return result;
        }
    }


    public static final class ControllerBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private Timestamp lockExpirationTime;
        private String name;
        private String description;
        private String boxId;
        private String state;

        private ControllerBuilder() {
        }

        public static ControllerBuilder aController() {
            return new ControllerBuilder();
        }

        public ControllerBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ControllerBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ControllerBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ControllerBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ControllerBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ControllerBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ControllerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ControllerBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ControllerBuilder withBoxId(String boxId) {
            this.boxId = boxId;
            return this;
        }

        public ControllerBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ControllerBuilder from(Controller controller) {
            this.lastEditorId = controller.getLastEditorId();
            this.lastEdited = controller.getLastEdited();
            this.lockHolderId = controller.getLockHolderId();
            this.lockHolderDisplayName = controller.getLockHolderDisplayName();
            this.lockExpirationTime = controller.getLockExpirationTime();

            this.id = controller.getId();
            this.name = controller.getName();
            this.description = controller.getDescription();
            this.boxId = controller.getBoxId();
            this.state = controller.state;
            return this;
        }

        public Controller build() {
            Controller controller = new Controller();
            controller.setLastEditorId(lastEditorId);
            controller.setLastEdited(lastEdited);
            controller.setLockHolderId(lockHolderId);
            controller.setLockHolderDisplayName(lockHolderDisplayName);
            controller.setId(id);
            controller.setLockExpirationTime(lockExpirationTime);
            controller.setName(name);
            controller.setDescription(description);
            controller.setBoxId(boxId);
            controller.setState(state);
            return controller;
        }
    }
}
