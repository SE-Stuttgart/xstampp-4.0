package de.xstampp.service.project.service.data;

import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProjectService;
import de.xstampp.service.project.service.data.eclipse_project.EclipseProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Operates on the project data.
 */
@Service
public class ProjectDataService {

    private ResourceLoader resourceLoader;
    private DeserializationUtil deSer;

    private IProjectDAO projectDAO;

    private XStamppProjectService xstamppProjectService;
    private EclipseProjectService eclipseProjectService;

    @Autowired
    public ProjectDataService(IProjectDAO projectDAO, XStamppProjectService xstamppProjectService,
                              ResourceLoader resourceLoader, EclipseProjectService eclipseProjectService) {
        this.projectDAO = projectDAO;
        this.xstamppProjectService = xstamppProjectService;
        this.eclipseProjectService = eclipseProjectService;
        this.resourceLoader = resourceLoader;
        this.deSer = new DeserializationUtil();
    }

    @Transactional
    public Project createProject (UUID projectId) {
        Project p = new Project(projectId);
        return projectDAO.makePersistent(p);
    }

    @Transactional
    public boolean deleteProject (UUID projectId) {
        Project p = projectDAO.findById(projectId, false);
        if (p != null) {
            projectDAO.makeTransient(p);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteProjects(Set<UUID> projectsIds) {
		for(UUID projectId:projectsIds) {
			deleteProject(projectId);
		}
		return true;
	}

    @Transactional
    public Project getProjectById(UUID projectId) {
        return projectDAO.findById(projectId, false);
    }

    public Project cloneProject (UUID projectIdSource, UUID newProjectId) {
        XStamppProject xstamppProject = xstamppProjectService.findXStamppProjectByProjectId(projectIdSource);
        XStamppProject clonedXStamppProject = xstamppProjectService.setProjectUUID(newProjectId, xstamppProject);
        return xstamppProjectService.saveXStamppProject(clonedXStamppProject);
    }

    public Project importProject(UUID projectId, XStamppProject xstamppProject) {
        XStamppProject importedXStamppProject = xstamppProjectService.setProjectUUID(projectId, xstamppProject);
        return xstamppProjectService.saveXStamppProject(importedXStamppProject);
    }

    public Project importEclipseProject(UUID projectId, EclipseProjectDTO eclipseProjectDTO) {
        XStamppProject xstamppProject = eclipseProjectService.mapToXStamppProject(projectId, eclipseProjectDTO);
        return xstamppProjectService.saveXStamppProject(xstamppProject);
    }

    public Project createExampleProject(UUID projectId) throws IOException {
        Resource resource = this.resourceLoader
                .getResource("classpath:/example/project/template.json");
        InputStream inputStream = resource.getInputStream();

        String exampleProjectJsonString = "";
        try (java.util.Scanner scanner = new java.util.Scanner(inputStream)) {
            scanner.useDelimiter("\\A");
            exampleProjectJsonString =  scanner.hasNext() ? scanner.next() : "";
        }

        XStamppProject xstamppProject = deSer.deserialize(exampleProjectJsonString, XStamppProject.class);
        XStamppProject xstamppProjectWithNewProjectId = xstamppProjectService.setProjectUUID(projectId, xstamppProject);

        return xstamppProjectService.saveXStamppProject(xstamppProjectWithNewProjectId);
    }
}
