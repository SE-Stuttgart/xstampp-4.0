package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportColumns;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;

@Entity
public class Loss extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 6168870258512964249L;

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;

    @CheckState
    @Column(name = "name")
    private String name;

    @CheckState
    @Column(name = "description")
    private String description;

    @Column(name = "state")
    private String state;

    public Loss() {

    }

    public Loss(ProjectDependentKey id) {
        super();
        this.id = id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public ProjectDependentKey getId() {
        return id;
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
        Loss other = (Loss) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Loss [id=" + id + ", name=" + name + ", description=" + description + "]";
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject project) {
        ReportSegment resultSegment = new ReportSegment(getReportReferenceId());
        resultSegment.add(new ReportHeader(name, getReportReferenceId()));
        resultSegment.add(new ReportPlainText(description));

        ReportColumns columns = new ReportColumns();
        List<Integer> hazardReferences = new ArrayList<>();
        List<HazardLossLink> hazardLossLinks = project.getHazardLossLinks();
        if(hazardLossLinks != null) {
            for (HazardLossLink link: hazardLossLinks) {
                if (link.getLossId() == id.getId()){
                    hazardReferences.add(link.getHazardId());
                }
            }
        }
        columns.addColumn("Causing Hazards", hazardReferences, ReportSegment.HAZARD_ID_PREFIX);
        resultSegment.add(columns);

        return resultSegment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.LOSS_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(STATE);
            return result;
        }
    }

    public static final class LossBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private Timestamp lockExpirationTime;
        private String name;
        private String description;
        private String state;

        private LossBuilder() {
        }

        public static LossBuilder aLoss() {
            return new LossBuilder();
        }

        public LossBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public LossBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public LossBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public LossBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public LossBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public LossBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public LossBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LossBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LossBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public LossBuilder from(Loss loss) {
            this.lastEditorId = loss.getLastEditorId();
            this.lastEdited = loss.getLastEdited();
            this.lockHolderId = loss.getLockHolderId();
            this.lockHolderDisplayName = loss.getLockHolderDisplayName();
            this.lockExpirationTime = loss.getLockExpirationTime();

            this.id = loss.getId();
            this.name = loss.getName();
            this.description = loss.getDescription();
            this.state = loss.state;
            return this;
        }

        public Loss build() {
            Loss loss = new Loss(id);
            loss.setLastEditorId(lastEditorId);
            loss.setLastEdited(lastEdited);
            loss.setLockHolderId(lockHolderId);
            loss.setLockHolderDisplayName(lockHolderDisplayName);
            loss.setLockExpirationTime(lockExpirationTime);
            loss.setName(name);
            loss.setDescription(description);
            loss.setState(state);
            return loss;
        }
    }
}
