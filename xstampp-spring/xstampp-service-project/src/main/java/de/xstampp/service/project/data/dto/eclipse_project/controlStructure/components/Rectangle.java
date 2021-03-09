package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import javax.xml.bind.annotation.XmlElement;

public class Rectangle {

    @XmlElement(name = "height")
    private int height;

    @XmlElement(name = "width")
    private int width;

    @XmlElement(name = "x")
    private int x;

    @XmlElement(name = "y")
    private int y;

    @XmlElement(name = "location")
    private Location location;

    @XmlElement(name = "size")
    private Size size;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
