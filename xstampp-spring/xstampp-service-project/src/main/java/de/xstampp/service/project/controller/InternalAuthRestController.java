package de.xstampp.service.project.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.IgnorePrivilegeCheck;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.data.ProjectDataService;
import de.xstampp.service.project.service.data.SystemDescriptionDataService;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@RestController
@IgnorePrivilegeCheck
@RequestMapping("/internal/project")
public class InternalAuthRestController {

	ProjectDataService projectData;
	SystemDescriptionDataService systemDescriptionDataService;

	SerializationUtil ser;
	DeserializationUtil deSer;
	Logger logger;

	@Autowired
	public InternalAuthRestController(ProjectDataService projectData, SystemDescriptionDataService systemDescriptionDataService) {
		this.projectData = projectData;
		this.systemDescriptionDataService = systemDescriptionDataService;
		this.ser = new SerializationUtil();
		this.deSer = new DeserializationUtil();
		this.logger = LoggerFactory.getLogger(InternalAuthRestController.class);
	}

	@RequestMapping(path = "{id}", method = RequestMethod.POST)
	public String onCreateProject(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
		Project project = projectData.createProject(UUID.fromString(projectId));
		systemDescriptionDataService.create(project.getId());
		return ser.serialize(project);
	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	public String onDeleteProject(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
		return ser.serialize(new Response(projectData.deleteProject(UUID.fromString(projectId))));
	}

	@RequestMapping(path = "{newProjectId}/clone/{clonedProjectId}", method = RequestMethod.POST)
	public String onCloneProject(@PathVariable("newProjectId") @XStamppProjectId String newProjectId,
								 @PathVariable("clonedProjectId") @XStamppProjectId String clonedProjectId) throws IOException {

		return ser.serialize(projectData.cloneProject(UUID.fromString(clonedProjectId), UUID.fromString(newProjectId)));
	}

	@RequestMapping(path = "{projectId}/import", method = RequestMethod.POST)
	public String onImportProject(@PathVariable("projectId") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {

		XStamppProject xstamppProject = deSer.deserialize(body, XStamppProject.class);
		return ser.serialize(projectData.importProject(UUID.fromString(projectId), xstamppProject));
	}

	@RequestMapping(path = "{projectId}/import/eclipse", method = RequestMethod.POST)
	public String onImportEclipseProject(@PathVariable("projectId") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {

		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.registerModule(new JaxbAnnotationModule());
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		EclipseProjectDTO eclipseProjectDTO = xmlMapper.readValue(body, EclipseProjectDTO.class);

		return ser.serialize(projectData.importEclipseProject(UUID.fromString(projectId), eclipseProjectDTO));
	}

	@RequestMapping(path = "{projectId}/example", method = RequestMethod.POST)
	public String onExampleProject(@PathVariable("projectId") @XStamppProjectId String projectId) throws IOException {

		return ser.serialize(projectData.createExampleProject(UUID.fromString(projectId)));
	}
}
