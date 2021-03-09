package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "location")
public class Location {

    @XmlElement(name = "x")
    private int x;

    @XmlElement(name = "y")
    private int y;

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
}
