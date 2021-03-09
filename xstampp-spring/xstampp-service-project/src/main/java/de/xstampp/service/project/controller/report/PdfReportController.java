package de.xstampp.service.project.controller.report;

import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.project.data.dto.report.ReportConfigDTO;
import de.xstampp.service.project.service.data.report.PdfReportService;
import de.xstampp.service.project.service.data.report.ReportConstructionException;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.UUID;

/**
 * Rest Controller responsible of handling all incoming requests, related to generating a pdf report
 */
@RestController
@RequestMapping("/api/project")
public class PdfReportController {

    @Autowired
    PdfReportService pdfReportService;

    DeserializationUtil deSer = new DeserializationUtil();

    /**
     * Generate a printable PDF containing all entities of the specified project.
     *
     * @param projectId The project to be exported to PDF
     * @param body The JSON-DTO containing report configuration options and information.
     *             Must follow this scheme:{@link ReportConfigDTO}
     * @return The generated PDF inside a HTTP response
     * @throws IOException Thrown if the JSON is not correctly formatted
     */
    @PrivilegeCheck(privilege = Privileges.GUEST)
    @RequestMapping(value = "{id}/report", method = RequestMethod.POST)
    public ResponseEntity<Object> getReport(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {

        ReportConfigDTO requestDTO = deSer.deserialize(body, ReportConfigDTO.class);

        File file;
        try {
            // Generate the PDF
            file = pdfReportService.runReport(UUID.fromString(projectId), requestDTO);
        } catch (ReportConstructionException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(ex.getHttpStatus()).body(ex.getUserMessage().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown report generation error".getBytes());
        }

        // Add pdf file as attachment to HTTP
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
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                    .body(inputStreamResource);

            return responseEntity;

        } catch (Exception e) {
            return new ResponseEntity<>("error occurred:\n" + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
