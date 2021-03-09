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
public class Hazard extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 2261902383927845161L;

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

    public Hazard() {

    }

    public Hazard(ProjectDependentKey id) {
        super();
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
        Hazard other = (Hazard) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Hazard [id=" + id + ", name=" + name + ", description=" + description + "]";
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(this.getReportReferenceId());
        segment.add(new ReportHeader(name,ReportSegment.HAZARD_ID_PREFIX + id.getId()));
        segment.add(new ReportPlainText(description));

        ReportColumns reportColumns = new ReportColumns();
        List<Integer> lossReferences = new ArrayList<>();
        List<Integer> systemConstraintReferences = new ArrayList<>();
        List<String> subHazardReferences = new ArrayList<>();

        List<HazardLossLink> hazardLossLinks = xstamppProject.getHazardLossLinks();
        List<HazardSystemConstraintLink> hazardSystemConstraintLinks = xstamppProject.getHazardSystemConstraintLinks();
        List<SubHazard> subHazards = xstamppProject.getSubHazards();

        if( hazardLossLinks != null){
            for (HazardLossLink link: hazardLossLinks) {
                if (link.getHazardId() == id.getId()){
                    lossReferences.add(link.getLossId());
                }
            }
        }


        if( hazardSystemConstraintLinks != null){
            for (HazardSystemConstraintLink sysLink : hazardSystemConstraintLinks){
                if (sysLink.getHazardId() == id.getId()){
                    systemConstraintReferences.add(id.getId());
                }
            }
        }

        if(subHazards != null){
            for (SubHazard subHazard: subHazards) {
                if(subHazard.getId().getParentId() == id.getId()){
                    subHazardReferences.add(ReportSegment.SUBHAZARD_ID_PREFIX + subHazard.getId().getParentId() + "." + subHazard.getId().getId());
                }
            }
        }

        reportColumns.addColumn("Losses causing Hazard",lossReferences, ReportSegment.LOSS_ID_PREFIX);
        reportColumns.addColumn("System Constraints",systemConstraintReferences, ReportSegment.SYSTEM_CONSTRAINT_ID_PREFIX);
        reportColumns.addColumn("Sub Hazards",subHazardReferences);
        segment.add(reportColumns);

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, this.getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.HAZARD_ID_PREFIX + id.getId();
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


    public static final class HazardBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private String state;

        private HazardBuilder() {
        }

        public static HazardBuilder aHazard() {
            return new HazardBuilder();
        }

        public HazardBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public HazardBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public HazardBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public HazardBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public HazardBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public HazardBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public HazardBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public HazardBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public HazardBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public HazardBuilder from(Hazard hazard) {
            this.lastEditorId = hazard.getLastEditorId();
            this.lastEdited = hazard.getLastEdited();
            this.lockHolderId = hazard.getLockHolderId();
            this.lockHolderDisplayName = hazard.getLockHolderDisplayName();
            this.lockExpirationTime = hazard.getLockExpirationTime();

            this.id = hazard.getId();
            this.name = hazard.getName();
            this.description = hazard.getDescription();
            this.state = hazard.state;
            return this;
        }

        public Hazard build() {
            Hazard hazard = new Hazard(id);
            hazard.setLastEditorId(lastEditorId);
            hazard.setLastEdited(lastEdited);
            hazard.setLockHolderId(lockHolderId);
            hazard.setLockHolderDisplayName(lockHolderDisplayName);
            hazard.setLockExpirationTime(lockExpirationTime);
            hazard.setName(name);
            hazard.setDescription(description);
            hazard.setState(state);
            return hazard;
        }
    }
}
