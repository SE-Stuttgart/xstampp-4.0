package de.xstampp.service.project.data.dto.eclipse_project.abstracts;

import javax.xml.bind.annotation.XmlElement;
import java.util.UUID;

public abstract class AbstractDataModel {

    @XmlElement
    private UUID id;

    @XmlElement
    private String title;

    @XmlElement
    private String description;

    @XmlElement
    private int number;

    public AbstractDataModel() {}

    public AbstractDataModel(UUID id, String title, String description, int number) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.number = number;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
