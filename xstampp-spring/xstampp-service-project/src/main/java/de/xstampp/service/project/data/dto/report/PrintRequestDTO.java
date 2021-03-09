package de.xstampp.service.project.data.dto.report;

import java.util.List;

/**
 * This DTO contains all entities and configurations that are to be included in the report file.
 */
public class PrintRequestDTO {

    private List<String> entities;

    public PrintRequestDTO() {
        //empty constructor for jackson
    }
}
