package de.xstampp.service.auth.data;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Theme implements Serializable {

    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "colors")
    private String colors;

    public Theme() {
    }

    public Theme(Integer id) {
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

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
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
        Theme other = (Theme) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Icon [id=" + id + ", colors=" + colors + "]";
    }

    public static final class EntityAttributes {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String COLORS = "colors";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(COLORS);
            return result;
        }
    }
}
