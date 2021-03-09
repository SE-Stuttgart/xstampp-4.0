package de.xstampp.service.project.controller.report;

import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.project.service.data.report.ControlStructureSvgService;
import de.xstampp.service.project.service.data.report.ReportConstructionException;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class ControlStructureSvgController {

    @Autowired
    ControlStructureSvgService controlStructureService;

    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "{id}/control-structure-svg/{coloured}", method = RequestMethod.POST)
    public ResponseEntity<Object> getReport(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("coloured") boolean coloured) {

        File file;
        try {
            file = controlStructureService.getControlStructure(UUID.fromString(projectId), coloured);
        } catch (ReportConstructionException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(ex.getHttpStatus()).body(ex.getUserMessage().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown control structure export error".getBytes());
        }

        //add svg file as attachment to HTTP
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
                    .contentType(MediaType.parseMediaType("image/svg+xml"))
                    .body(inputStreamResource);

            return responseEntity;

        } catch (Exception e) {
            return new ResponseEntity<>("error occurred:\n" + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
