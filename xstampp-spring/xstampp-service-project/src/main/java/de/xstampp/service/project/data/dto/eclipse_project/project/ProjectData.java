package de.xstampp.service.project.data.dto.eclipse_project.project;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement(name = "projectdata")
public class ProjectData {

    @XmlElement
    private String projectName;

    @XmlElement
    private String projectDescription;

    @XmlTransient
    private List<Object> styleRanges;

    @XmlTransient
    private List<Object> rangeObjects;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<Object> getStyleRanges() {
        return styleRanges;
    }

    public void setStyleRanges(List<Object> styleRanges) {
        this.styleRanges = styleRanges;
    }

    public List<Object> getRangeObjects() {
        return rangeObjects;
    }

    public void setRangeObjects(List<Object> rangeObjects) {
        this.rangeObjects = rangeObjects;
    }
}
