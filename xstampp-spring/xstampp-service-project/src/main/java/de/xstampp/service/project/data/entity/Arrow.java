package de.xstampp.service.project.data.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import de.xstampp.service.project.data.dto.ControlStructureDTO.ArrowDTO;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@TypeDefs({
        @TypeDef(
                typeClass = IntArrayType.class,
                defaultForType = int[].class
        )
})
public class Arrow implements Serializable {
	
	private static final long serialVersionUID = 2389402875916439571L;

	@Id
	private String id;
	
	private String source;
	private String destination;
	@Column(name = "arrow_type")
	private String type;
	@Column(name = "project_id")
	@Id
	private UUID projectId;
	@Column(name = "parent_id")
	private String parents;
	private String label;
	private int[] parts;
	
	public Arrow() {
		// Used for Hibernate Object mapper
	}
	
	public Arrow(ArrowDTO dto, UUID projectId) {
		this.destination = dto.getDestination();
		this.id = dto.getId();
		this.label = dto.getLabel();
		this.parents = dto.getParent();
		this.projectId = projectId;
		this.source = dto.getSource();
		this.type = dto.getType();
		
		if (dto.getParts() != null && !dto.getParts().isEmpty()) {
			parts = new int[dto.getParts().size()*2];
			for (int i=0; i < dto.getParts().size(); i++) {
				int index = i*2;
				parts[index] = dto.getParts().get(i).getX();
				parts[index+1] = dto.getParts().get(i).getY();
			}
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getParents() {
		return parents;
	}

	public void setParents(String parents) {
		this.parents = parents;
	}
	
	public int[] getParts() {
		return parts;
	}

	public void setParts(int[] parts) {
		this.parts = parts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (projectId == null ? 0 : projectId.hashCode());
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
		Arrow other = (Arrow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

	public static final class EntityAttributes {
		public static final String ID= "id";
		public static final String PROJECT_ID = "projectId";
		public static final String SOURCE = "source";
		public static final String DESTINATION = "destination";
		public static final String LABEL = "label";
		public static final String ARROW_TYPE = "type";
		public static final String PARENT = "parents";
		public static final String PARTS = "parts";


		public static List<String> getAllAttributes() {
			List<String> result = new ArrayList<>();
			result.add(ID);
			result.add(PROJECT_ID);
			result.add(SOURCE);
			result.add(DESTINATION);
			result.add(LABEL);
			result.add(ARROW_TYPE);
			result.add(PARENT);
			result.add(PARTS);
			return result;
		}
	}

    public static final class ArrowBuilder {
        private String id;
        private String source;
        private String destination;
        private String type;
        private UUID projectId;
        private String parents;
        private String label;
        private int[] parts;

        private ArrowBuilder() {
        }

        public static ArrowBuilder anArrow() {
            return new ArrowBuilder();
        }

        public ArrowBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ArrowBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public ArrowBuilder withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public ArrowBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ArrowBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ArrowBuilder withParents(String parents) {
            this.parents = parents;
            return this;
        }

        public ArrowBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public ArrowBuilder withParts(int[] parts) {
            this.parts = parts;
            return this;
        }

        public ArrowBuilder from(Arrow arrow) {
            this.id = arrow.getId();
            this.source = arrow.getSource();
            this.destination = arrow.getDestination();
            this.type = arrow.getType();
            this.projectId = arrow.getProjectId();
            this.parents = arrow.getParents();
            this.label = arrow.getLabel();
            this.parts = arrow.getParts();
            return this;
        }

        public Arrow build() {
            Arrow arrow = new Arrow();
            arrow.setId(id);
            arrow.setSource(source);
            arrow.setDestination(destination);
            arrow.setType(type);
            arrow.setProjectId(projectId);
            arrow.setParents(parents);
            arrow.setLabel(label);
            arrow.setParts(parts);
            return arrow;
        }
    }
}
