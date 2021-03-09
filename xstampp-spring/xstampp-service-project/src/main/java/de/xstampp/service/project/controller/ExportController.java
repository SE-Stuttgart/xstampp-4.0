package de.xstampp.service.project.controller;

import de.xstampp.common.utils.*;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProjectService;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class ExportController {

    @Autowired
    XStamppProjectService XStamppProjectService;

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "/{projectId}/export", method = RequestMethod.GET)
    public ResponseEntity<Object> exportXStamppProjectAsFile(
            @PathVariable("projectId") @XStamppProjectId String projectId) throws IOException {

        XStamppProject xstamppProject = XStamppProjectService.findXStamppProjectByProjectId(UUID.fromString(projectId));

        File file = toExportFile(createFileName(projectId), xstamppProject);

        try {
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            ResponseEntity<Object> responseEntity = ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/json"))
                    .body(inputStreamResource);

            return responseEntity;

        } catch (Exception e ) {
            return new ResponseEntity<>("error occurred:\n" + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createFileName(String projectId) {
        String fileName = "XStamppProject_";
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z");
        Date date = new Date(System.currentTimeMillis());
        fileName += formatter.format(date);
        fileName += "_" + projectId;
        return fileName;
    }

    private File toExportFile(String fileName, XStamppProject xstamppProject) throws IOException {
        File exportFile = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(exportFile)) {
            fileWriter.write(serializeXStamppProject(xstamppProject));
        }
        return exportFile;
    }

    private String serializeXStamppProject(XStamppProject xstamppProject) throws IOException {
        SerializationUtil ser = new SerializationUtil();
        String serializedProject = "";
        serializedProject = ser.serialize(xstamppProject);
        return serializedProject;
    }
}