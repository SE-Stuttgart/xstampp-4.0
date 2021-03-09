package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "size")
public class Size {

    @XmlElement(name = "height")
    private int height;

    @XmlElement(name = "width")
    private int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
