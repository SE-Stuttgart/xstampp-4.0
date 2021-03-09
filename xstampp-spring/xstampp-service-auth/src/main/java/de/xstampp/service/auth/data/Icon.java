package de.xstampp.service.auth.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Icon implements Serializable {

    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private byte[] image;

    public Icon() {
    }

    public Icon(Integer id) {
        super();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
        Icon other = (Icon) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Icon [id=" + this.id + ", name=" + this.name + "]";
    }

    public static final class EntityAttributes {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IMAGE = "image";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(IMAGE);
            return result;
        }
    }
}
