package de.xstampp.service.project.service.data.report;

import de.xstampp.service.project.data.dto.report.ReportConfigDTO;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportSvg;
import de.xstampp.service.project.service.dao.control_structure.iface.IVectorGraphicDAO;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProjectService;

import de.xstampp.service.project.service.data.report.xmlProcessor.XmlProcessor;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service responsible of generating a pdf with data from the database
 */
@Service
@Transactional
public class PdfReportService {

    @Autowired
    SpringTemplateEngine templateEngine;

    @Autowired
    XStamppProjectService xStamppProjectService;

    @Autowired
    IVectorGraphicDAO vectorGraphicDAO;

    private final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

    /**
     * starting point of report generation. Fills thyme templates with database data and converts data xsl-fo
     *
     * @param projectId project report is to be run for
     * @return pdf file
     */
    public File runReport(UUID projectId, ReportConfigDTO requestDTO) {
        //retrieve the entire XStampp project
        XStamppProject project = xStamppProjectService.findXStamppProjectByProjectId(projectId);

        //maps identifiers in template to functions, values or data structures
        Map<String, Object> templateMap = new HashMap<String, Object>();
        // FIXME: Insert project name via auth service instead of frontend
        // FIXME: Apply Locale-dynamic approach
        templateMap.put("currentDate", DateTimeFormatter.ofPattern("dd. MMM yyyy - HH:mm 'UTC'")
                .format(ZonedDateTime.now().withZoneSameLocal(ZoneId.of("UTC+00:00"))));
        templateMap.put("config", requestDTO);
        templateMap.put("scripts", new TemplateScripts());
        Object[] generatedSections = generateSections(project, requestDTO);
        templateMap.put("sections", generatedSections[0]);
        templateMap.put("entityIndex", generatedSections[1]);

        IContext context = new Context(Locale.ENGLISH, templateMap);

        String foCode;
        try {
            foCode = templateEngine.process("reportMaster", context);
        } catch (Exception ex) {
            throw new ReportConstructionException("Failed to apply Thymeleaf template", ex);
        }

        return generatePDF(foCode, projectId);
    }

    /**
     * creates a pdf file from received data
     *
     * @param text      text data
     * @param projectId project data belongs to
     * @return pdf file
     */
    private File generatePDF(String text, UUID projectId) {

        File baseDir = new File("classpath:/WEB-INF/");
        File outDir = new File(baseDir, "out");
        outDir.mkdirs();

        //create PDF file
        File pdfFile = new File(outDir, createFileName(projectId.toString()));

        File foFile = new File(outDir, "report.fo");
        try (FileWriter fileWriter = new FileWriter(foFile)) {
            fileWriter.write(text);
            fileWriter.close();

            return convertFO2PDF(foFile, pdfFile);

        } catch (IOException | FOPException e) {
            throw new ReportConstructionException("Failed to render XSL-FO to PDF", e);
        }

    }

    /**
     * creates a filename
     *
     * @param projectId projectId to be written into filename
     * @return
     */
    private String createFileName(String projectId) {

        String fileName = "XStamppProject_";
        fileName += projectId;
        fileName += "_report.pdf";
        return fileName;
    }


    /**
     * this method converts xml-fo data to pdf
     *
     * @param fo  xsl-fo file
     * @param pdf pdf file where data is to be written to
     * @return the pdf file
     * @throws IOException
     * @throws FOPException
     */
    public File convertFO2PDF(File fo, File pdf) throws IOException, FOPException {

        OutputStream out = null;

        try {
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output stream.  Note: Using BufferedOutputStream
            // for performance reasons (helpful with FileOutputStreams).
            out = new BufferedOutputStream(new FileOutputStream(pdf));

            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Setup input stream
            Source src = new StreamSource(fo);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            // Result processing
            FormattingResults foResults = fop.getResults();
            java.util.List pageSequences = foResults.getPageSequences();
            for (Object pageSequence : pageSequences) {
                PageSequenceResults pageSequenceResults = (PageSequenceResults) pageSequence;
                System.out.println("PageSequence "
                        + (String.valueOf(pageSequenceResults.getID()).length() > 0
                        ? pageSequenceResults.getID() : "<no id>")
                        + " generated " + pageSequenceResults.getPageCount() + " pages.");
            }
            System.out.println("Generated " + foResults.getPageCount() + " pages in total.");

        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            out.close();
            return pdf;
        }
    }

    /**
     * generates all predefined sections of the pdf document
     *
     * @param project current xstampp project
     * @param config  definition of all sections to be added
     * @return
     */
    private Object[] generateSections(XStamppProject project, ReportConfigDTO config) {
        final List<ReportSection> sections = new ArrayList<>();
        final Map<String, IndexEntry> entityIndex = new HashMap<>();

        /* Sections, that contain neither segments nor subsections (that have segments in their structure)
         * will get deleted after all sections got declared. */

        // Step 1
        final ReportSection step1 = new ReportSection("Step 1");
        if (config.isSystemDescription()) {
            ReportSection systemDescSection = new ReportSection("System Description");
            systemDescSection.segments.add(project.getSystemDescription().createReportSegment(project));
            step1.subsections.add(systemDescSection);
        }
        SectionGeneratorElement[] sectionProperties = {
                new SectionGeneratorElement("Losses", project.getLosses(),
                        config.isLosses()),
                new SectionGeneratorElement("Hazards", project.getHazards(),
                        config.isHazards()),
                new SectionGeneratorElement("Subhazards", project.getSubHazards(),
                        config.isSubHazards()),
                new SectionGeneratorElement("System Constraints", project.getSystemConstraints(),
                        config.isSystemConstraints()),
                new SectionGeneratorElement("Sub System Constraints", project.getSubSystemConstraints(),
                        config.isSubSystemConstraints())
        };
        generateBulkSections(step1, sectionProperties, project, entityIndex);
        sections.add(step1);


        // Step 2
        final ReportSection step2 = new ReportSection("Step 2");

        if (config.isControlStructure()) {
            ReportSection controlStructureSection = new ReportSection("Control Structure");
            ReportSegment controlStructureSegment = new ReportSegment("CS-1");
            controlStructureSegment.add(new ReportSvg(XmlProcessor.convertSvg(
                    vectorGraphicDAO.getVectorGraphic(project.getProjectUUID(), config.isControlStructureHasColour()).getGraphic())));
            controlStructureSection.segments.add(controlStructureSegment);
            step2.subsections.add(controlStructureSection);
        }

        final ReportSection systemComponents = new ReportSection("System Components");
        sectionProperties = new SectionGeneratorElement[]{
                new SectionGeneratorElement("Controllers", project.getControllers(), config.isControllers()),
                new SectionGeneratorElement("Actuators", project.getActuators(), config.isActuators()),
                new SectionGeneratorElement("Sensors", project.getSensors(), config.isSensors()),
                new SectionGeneratorElement("Controlled Processes", project.getControlledProcesses(),
                        config.isControlledProcesses())
        };
        generateBulkSections(systemComponents, sectionProperties, project, entityIndex);
        step2.subsections.add(systemComponents);

        final ReportSection informationFlow = new ReportSection("Information Flow");
        sectionProperties = new SectionGeneratorElement[]{
                new SectionGeneratorElement("Control Actions", project.getControlActions(),
                        config.isControlActions()),
                new SectionGeneratorElement("Feedback", project.getFeedback(), config.isFeedback()),
                new SectionGeneratorElement("Inputs", project.getInputs(), config.isInputs()),
                new SectionGeneratorElement("Outputs", project.getOutputs(), config.isOutputs())
        };
        generateBulkSections(informationFlow, sectionProperties, project, entityIndex);
        step2.subsections.add(informationFlow);

        sectionProperties = new SectionGeneratorElement[]{
                new SectionGeneratorElement("Responsibilities", project.getResponsibilities(),
                        config.isResponsibilities())
        };
        generateBulkSections(step2, sectionProperties, project, entityIndex);

        sections.add(step2);


        // Step 3
        final ReportSection step3 = new ReportSection("Step 3");
        sectionProperties = new SectionGeneratorElement[]{
                new SectionGeneratorElement("UCAs", project.getUnsafeControlActions(),
                        config.isUcas()),
                new SectionGeneratorElement("Controller Constraints", project.getControllerConstraints(),
                        config.isControllerConstraints())
        };
        generateBulkSections(step3, sectionProperties, project, entityIndex);
        sections.add(step3);

        // Step 4
        final ReportSection step4 = new ReportSection("Step 4");
        sectionProperties = new SectionGeneratorElement[]{
                new SectionGeneratorElement("Process Models", project.getProcessModels(),
                        config.isProcessModels()),
                new SectionGeneratorElement("Process Variables", project.getProcessVariables(),
                        config.isProcessVariables()),
                new SectionGeneratorElement("Control Algorithms", project.getRules(),
                        config.isControlAlgorithms()),
                new SectionGeneratorElement("Conversions", project.getConversions(),
                        config.isConversions()),
                new SectionGeneratorElement("Loss Scenarios", project.getLossScenarios(),
                        config.isLossScenarios()),
                new SectionGeneratorElement("Implementation Constraints",
                        project.getImplementationConstraints(), config.isImplementationConstraints())

        };
        generateBulkSections(step4, sectionProperties, project, entityIndex);
        sections.add(step4);

        // Determine the layer of each component in the structure and delete all sections without content
        Iterator<ReportSection> sectionIterator = sections.iterator();
        while (sectionIterator.hasNext()) {
            final ReportSection section = sectionIterator.next();
            section.initializeTree(1);
            if (section.toDelete()) {
                sectionIterator.remove();
            }
        }
        if (sections.isEmpty()) {
            throw new ReportConstructionException("No content. There are no entities that can be included.", "All sections got excluded from the report.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new Object[]{sections, entityIndex};
    }

    public static class IndexEntry {
        public String entityName;
        public boolean alive;

        public IndexEntry(String entityName, boolean alive) {
            this.entityName = entityName;
            this.alive = alive;
        }
    }

    private class SectionGeneratorElement {
        final String sectionName;
        final List<? extends ReportableEntity> sectionContent;
        final boolean includeSection;

        SectionGeneratorElement(String sectionName, List<? extends ReportableEntity> sectionContent,
                                boolean includeSection) {
            this.sectionName = sectionName;
            this.sectionContent = sectionContent;
            this.includeSection = includeSection;
        }
    }

    /**
     * fills each section with data from the database
     *
     * @param superSection parent section all sections belong to
     * @param sections     selected section
     * @param project      xstampp project
     * @param entityIndex
     */
    private static void generateBulkSections(ReportSection superSection, SectionGeneratorElement[] sections,
                                             XStamppProject project, Map<String, IndexEntry> entityIndex) {
        for (SectionGeneratorElement sectionProperties : sections) {
            ReportSection section = new ReportSection(sectionProperties.sectionName);
            for (ReportableEntity entity : sectionProperties.sectionContent) {
                if (sectionProperties.includeSection) {
                    section.segments.add(entity.createReportSegment(project));
                }
                ReportNameIdPair nameIdPair = entity.createNameIdPair();
                if (entityIndex.containsKey(nameIdPair.id)) {
                    throw new ReportConstructionException("At least two ReportableEntities stated the same reference id: '"
                            + nameIdPair.id + "' First declared name: '" + entityIndex.get(nameIdPair.id).entityName
                            + "' Second declared name: '" + nameIdPair.name + "'");
                }
                entityIndex.put(nameIdPair.id, new IndexEntry(nameIdPair.name, sectionProperties.includeSection));
            }
            Collections.sort(section.segments);
            superSection.subsections.add(section);
        }
    }
}

