package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.report.ReportNameIdPair;
import de.xstampp.service.project.data.entity.report.ReportSegment;
import de.xstampp.service.project.data.entity.report.ReportableEntity;
import de.xstampp.service.project.data.entity.report.structureElements.ReportColumns;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "unsafe_control_action")
@Entity
public class UnsafeControlAction extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = -8398809951545445348L;

    @EmbeddedId
    @JsonUnwrapped
    private EntityDependentKey id;

    @CheckState
    @Column(name = "name")
    private String name;

    @CheckState
    @Column(name = "description")
    private String description;

    @CheckState
    @Column(name = "category")
    private String category;

    @Column(name = "state")
    private String state;

    public UnsafeControlAction() {
        /* default onstructor for Hibernate */
    }

    public UnsafeControlAction(EntityDependentKey id) {
        this.id = id;
    }

    public EntityDependentKey getId() {
        return id;
    }

    public void setId(EntityDependentKey id) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        UnsafeControlAction other = (UnsafeControlAction) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("{name: %s, description: %s, category: %s, state: %s}",
                name, description, category, state);
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportTitledReference("Causing Control Action", ReportSegment.CONTROL_ACTION_ID_PREFIX + id.getParentId(), true));
        segment.add(new ReportTitledReference("Category", category,false));
        segment.add(new ReportPlainText(description));

        ReportColumns columns = new ReportColumns();
        List<Integer> hazardReferences = new ArrayList<>();
        List<String> subHazardReferences = new ArrayList<>();

        List<UnsafeControlActionHazardLink> hazardLinks = xstamppProject.getUnsafeControlActionHazardLinks();
        List<UnsafeControlActionSubHazardLink> subHazardLinks = xstamppProject.getUnsafeControlActionSubHazardLinks();

        if( hazardLinks != null){
            for (UnsafeControlActionHazardLink link: hazardLinks) {
                if (link.getUnsafeControlActionId() == id.getId() && !hazardReferences.contains(link.getHazardId())){
                    hazardReferences.add(link.getHazardId());
                }
            }
        }

        if( subHazardLinks != null){
            for (UnsafeControlActionSubHazardLink link: subHazardLinks) {
                if (link.getUnsafeControlActionId() == id.getId() && !subHazardReferences.contains(link.getSubHazardId())){
                    subHazardReferences.add(ReportSegment.SUBHAZARD_ID_PREFIX + link.getSubHazardId() + "." + link.getHazardId());
                }
            }
        }

        columns.addColumn("Causes Hazards", hazardReferences, ReportSegment.HAZARD_ID_PREFIX);
        columns.addColumn("Causes Sub Hazards",subHazardReferences);
        segment.add(columns);

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.UCA_ID_PREFIX + id.getParentId() + "." + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String CATEGORY = "category";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(CATEGORY);
            result.add(STATE);
            return result;
        }
    }

    public static final class UnsafeControlActionBuilder {
        EntityDependentKey id;
        String name;
        String description;
        String category;
        String state;
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;

        private UnsafeControlActionBuilder() {
        }

        public static UnsafeControlActionBuilder anUnsafeControlAction() {
            return new UnsafeControlActionBuilder();
        }

        public UnsafeControlActionBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public UnsafeControlActionBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public UnsafeControlActionBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public UnsafeControlActionBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public UnsafeControlActionBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public UnsafeControlActionBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public UnsafeControlActionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UnsafeControlActionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UnsafeControlActionBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public UnsafeControlActionBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public UnsafeControlActionBuilder from(UnsafeControlAction unsafeControlAction) {
            this.lastEditorId = unsafeControlAction.getLastEditorId();
            this.lastEdited = unsafeControlAction.getLastEdited();
            this.lockHolderId = unsafeControlAction.getLockHolderId();
            this.lockHolderDisplayName = unsafeControlAction.getLockHolderDisplayName();
            this.lockExpirationTime = unsafeControlAction.getLockExpirationTime();

            this.id = unsafeControlAction.getId();
            this.name = unsafeControlAction.getName();
            this.description = unsafeControlAction.getDescription();
            this.category = unsafeControlAction.getCategory();
            this.state = unsafeControlAction.state;
            return this;
        }

        public UnsafeControlAction build() {
            UnsafeControlAction unsafeControlAction = new UnsafeControlAction();
            unsafeControlAction.setLastEditorId(lastEditorId);
            unsafeControlAction.setLastEdited(lastEdited);
            unsafeControlAction.setLockHolderId(lockHolderId);
            unsafeControlAction.setLockHolderDisplayName(lockHolderDisplayName);
            unsafeControlAction.setLockExpirationTime(lockExpirationTime);
            unsafeControlAction.setId(id);
            unsafeControlAction.setName(name);
            unsafeControlAction.setDescription(description);
            unsafeControlAction.setCategory(category);
            unsafeControlAction.setState(state);
            return unsafeControlAction;
        }
    }
}
