package de.xstampp.service.project.data.entity.lastId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversion_last_id")
public class ConversionLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "actuator_id")
    private int actuatorId;

    @Column(name = "last_id")
    private int lastId;

    public ConversionLastId() {
    }

    public ConversionLastId(UUID projectId, int hazardId, int lastId) {
        this.projectId = projectId;
        this.actuatorId = hazardId;
        this.lastId = lastId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getActuatorId() {
        return actuatorId;
    }

    public void setActuatorId(int actuatorId) {
        this.actuatorId = actuatorId;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, actuatorId, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof ConversionLastId))
            return false;

        ConversionLastId conversionLastId = (ConversionLastId) obj;
        boolean sameUUID    = this.projectId.equals(conversionLastId.projectId);
        boolean sameEntity  = this.actuatorId == conversionLastId.actuatorId;
        boolean sameLastId  = this.lastId == conversionLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, actuator_id = %s, last_id = %d}", projectId, actuatorId, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "actuator_id";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(LAST_ID);
            return result;
        }
    }


    public static final class ConversionLastIdBuilder {
        private UUID projectId;
        private int actuatorId;
        private int lastId;

        private ConversionLastIdBuilder() {
        }

        public static ConversionLastIdBuilder aConversionLastId() {
            return new ConversionLastIdBuilder();
        }

        public ConversionLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ConversionLastIdBuilder withActuatorId(int actuatorId) {
            this.actuatorId = actuatorId;
            return this;
        }

        public ConversionLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public ConversionLastIdBuilder from(ConversionLastId conversionLastId) {
            this.projectId = conversionLastId.projectId;
            this.actuatorId = conversionLastId.actuatorId;
            this.lastId = conversionLastId.lastId;
            return this;
        }

        public ConversionLastId build() {
            ConversionLastId conversionLastId = new ConversionLastId();
            conversionLastId.setProjectId(projectId);
            conversionLastId.setActuatorId(actuatorId);
            conversionLastId.setLastId(lastId);
            return conversionLastId;
        }
    }
}
