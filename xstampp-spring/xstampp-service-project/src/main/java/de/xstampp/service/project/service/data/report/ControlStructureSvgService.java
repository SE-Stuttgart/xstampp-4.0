package de.xstampp.service.project.service.data.report;

import de.xstampp.service.project.service.dao.control_structure.iface.IVectorGraphicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
public class ControlStructureSvgService {

    @Autowired
    IVectorGraphicDAO vectorGraphicDAO;

    public File getControlStructure(UUID projectId, boolean coloured) {
        return generateSvg(vectorGraphicDAO.getVectorGraphic(projectId, coloured).getGraphic(), projectId);
    }

    private File generateSvg(String text, UUID projectId) {

        File baseDir = new File("classpath:/WEB-INF/");
        File outDir = new File(baseDir, "out");
        outDir.mkdirs();

        //create PDF file
        File svgFile = new File(outDir, createFileName(projectId.toString()));
        try (FileWriter fileWriter = new FileWriter(svgFile)) {
            fileWriter.write(text);
            fileWriter.close();

            return svgFile;

        } catch (IOException e) {
            throw new ReportConstructionException("Failed to create SVG file", e);
        }

    }

    private String createFileName(String projectId) {

        String fileName = "XStamppProject_";
        fileName += projectId;
        fileName += "_cs.svg";
        return fileName;
    }
}
