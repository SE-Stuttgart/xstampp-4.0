package de.xstampp.service.project.data.entity.control_structure;

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
@Table(name = "vectorgraphic")
public class VectorGraphic implements Serializable {
    private static final long serialVersionUID = 2219437727740024734L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    private String graphic;

    @Id
    @Column(name = "has_colour")
    private boolean hasColour;


    public VectorGraphic(){
        // default constructor for hibernate
    }

    public VectorGraphic(UUID projectId, String graphic, boolean hasColour){
        this.projectId = projectId;
        this.graphic = graphic;
        this.hasColour = hasColour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VectorGraphic that = (VectorGraphic) o;

        return projectId.equals(that.projectId)
                && graphic.equals(that.graphic)
                && hasColour == that.hasColour;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + graphic.hashCode();
        result = prime * result + Objects.hashCode(hasColour);
        return result;
    }

    @Override
    public String toString() {
        return "VectorGraphic [projectId=" + projectId + ", graphic=" + graphic + "]";
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String GRAPHIC = "graphic";
        public static final String HAS_COLOUR = "hasColour";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(GRAPHIC);
            result.add(HAS_COLOUR);

            return result;
        }
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getGraphic() {
        return graphic;
    }

    public void setGraphic(String graphic) {
        this.graphic = graphic;
    }

    public boolean isHasColour() {
        return hasColour;
    }

    public void setHasColour(boolean hasColour) {
        this.hasColour = hasColour;
    }
}