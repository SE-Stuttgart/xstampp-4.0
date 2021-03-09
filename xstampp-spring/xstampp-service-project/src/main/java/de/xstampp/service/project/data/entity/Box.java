package de.xstampp.service.project.data.entity;

import de.xstampp.service.project.data.dto.ControlStructureDTO.BoxDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Box implements Serializable {

    private static final long serialVersionUID = 1218926065187405620L;

    @Id
    private String id;

    @Column(name = "parent_id")
    private Integer parent;

    private String name;

    @Column(name = "project_id")
    @Id
    private UUID projectId;

    @Column(name = "box_type")
    private String boxType;

    private int x;

    private int y;

    private int width;

    private int height;

    public Box() {
        // Used for Hibernate Object Mapper
    }

    public Box(BoxDTO dto, UUID projectId) {
        this.boxType = dto.getType();
        this.height = dto.getHeight();
        this.id = dto.getId();
        this.name = dto.getName();
        this.parent = dto.getParent();
        this.width = dto.getWidth();
        this.projectId = projectId;
        this.x = dto.getX();
        this.y = dto.getY();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
        Box other = (Box) obj;
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
        public static final String ID = "id";
        public static final String PROJECT_ID = "projectId";
        public static final String BOX_TYPE = "boxType";
        public static final String NAME = "name";
        public static final String PARENT = "parent";


        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(PROJECT_ID);
            result.add(BOX_TYPE);
            result.add(PARENT);
            return result;
        }
    }

    public static final class BoxBuilder {
        private String id;
        private Integer parent;
        private String name;
        private UUID projectId;
        private String boxType;
        private int x;
        private int y;
        private int width;
        private int height;

        private BoxBuilder() {
        }

        public static BoxBuilder aBox() {
            return new BoxBuilder();
        }

        public BoxBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public BoxBuilder withParent(Integer parent) {
            this.parent = parent;
            return this;
        }

        public BoxBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public BoxBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public BoxBuilder withBoxType(String boxType) {
            this.boxType = boxType;
            return this;
        }

        public BoxBuilder withX(int x) {
            this.x = x;
            return this;
        }

        public BoxBuilder withY(int y) {
            this.y = y;
            return this;
        }

        public BoxBuilder withWidth(int width) {
            this.width = width;
            return this;
        }

        public BoxBuilder withHeight(int height) {
            this.height = height;
            return this;
        }

        public BoxBuilder from(Box box) {
            this.id = box.getId();
            this.parent = box.getParent();
            this.name = box.getName();
            this.projectId = box.getProjectId();
            this.boxType = box.getBoxType();
            this.x = box.getX();
            this.y = box.getY();
            this.width = box.getWidth();
            this.height = box.getHeight();
            return this;
        }

        public Box build() {
            Box box = new Box();
            box.setId(id);
            box.setParent(parent);
            box.setName(name);
            box.setProjectId(projectId);
            box.setBoxType(boxType);
            box.setX(x);
            box.setY(y);
            box.setWidth(width);
            box.setHeight(height);
            return box;
        }
    }
}
