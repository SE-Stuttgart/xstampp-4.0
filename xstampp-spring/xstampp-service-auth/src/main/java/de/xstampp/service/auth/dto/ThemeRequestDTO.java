package de.xstampp.service.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ThemeRequestDTO {

    private Integer id;

    private String name;

    private String colors;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColors() {
        return this.colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }
}
