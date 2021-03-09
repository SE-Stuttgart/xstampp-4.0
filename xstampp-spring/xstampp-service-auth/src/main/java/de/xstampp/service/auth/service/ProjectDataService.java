package de.xstampp.service.auth.service;

import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.auth.data.Group;
import de.xstampp.service.auth.data.Project;
import de.xstampp.service.auth.data.remote.RemoteProject;
import de.xstampp.service.auth.service.dao.remote.IRemoteProjectDAO;
import de.xstampp.service.auth.dto.ProjectRequestDTO;
import de.xstampp.service.auth.service.RequestPushService.Method;
import de.xstampp.service.auth.service.dao.IGroupDAO;
import de.xstampp.service.auth.service.dao.IProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ProjectDataService {
    private IProjectDAO projectDAO;
    private IGroupDAO groupDAO;
	private IRemoteProjectDAO remoteProjectDAO;

    private SecurityService securityService;
    private RequestProjectService requestProject;
    private RequestPushService pushService;

    @Autowired
    public ProjectDataService(IProjectDAO projectDAO, IRemoteProjectDAO remoteProjectDAO, IGroupDAO groupDAO, SecurityService securityService,
                              RequestProjectService requestProject, RequestPushService pushService) {
        this.projectDAO = projectDAO;
        this.remoteProjectDAO = remoteProjectDAO;
        this.groupDAO = groupDAO;
        this.securityService = securityService;
        this.requestProject = requestProject;
        this.pushService = pushService;
    }

    public Project createProject(ProjectRequestDTO request) {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCreatedAt(Timestamp.from(Instant.now()));
        project.setReferenceNumber(request.getReferenceNumber());
        if (request.getGroupId() == null) {
            Group target = groupDAO.findById(securityService.getContext().getUserId(), false);
            project.setGroupId(target.getId());
        } else {
            project.setGroupId(request.getGroupId());
        }
        projectDAO.makePersistent(project);
        requestProject.createProjectRemote(projectId);
        pushService.notify(projectId.toString(), projectId.toString(), "project", Method.CREATE);
        return project;
    }

    public boolean deleteProject(UUID id) {
        Project entity = projectDAO.findById(id, false);
        if (entity != null) {
            projectDAO.makeTransient(entity);
            requestProject.deleteProjectRemote(id);
            pushService.notify(id.toString(), id.toString(), "project", Method.DELETE);
            return true;
        } else {
            return false;
        }
    }

    /**
	 * Offers deletion of a project across services.
	 * @param id UUID of a project
	 * @return true iff a project with UUID id exists on auth and project service and was successfully deleted on both
	 */
	public boolean deleteProjectFully(UUID id) {
		Project project = projectDAO.findById(id, false);
		RemoteProject remoteProject = remoteProjectDAO.findById(id, false);
		if (project != null) {
			projectDAO.makeTransient(project);
			//in case there was a problem while creating a project (done via http) there might not exist a corresponding
			//project on the project service
			if(remoteProject != null) {
				remoteProjectDAO.makeTransient(remoteProject);
			}
			pushService.notify(id.toString(), id.toString(), "project", Method.DELETE);
			return true;
		}
		return false;
	}


    public Project getProjectById(UUID projectId) {
        return projectDAO.findById(projectId, false);
    }

    public Project alterProject(UUID id, ProjectRequestDTO request) {
        Project entity = projectDAO.findById(id, false);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setCreatedAt(Timestamp.from(Instant.now()));
        entity.setReferenceNumber(request.getReferenceNumber());
        if (request.getGroupId() != null && !request.getGroupId().equals(entity.getGroupId())) {
            throw ErrorsAuth.CANNOT_MOVE_PROJECT.exc();
        }

        Project result = projectDAO.updateExisting(entity);
        if (result != null) {
            pushService.notify(id.toString(), id.toString(), "project", Method.ALTER);
            return result;
        } else {
            return null;
        }
    }

    public Project cloneProject(UUID projectIdSource, ProjectRequestDTO request) {
        Project clonedProject = projectDAO.findById(projectIdSource, false);

        request.setName(clonedProject.getName() + " (Clone)");
        request.setDescription(clonedProject.getDescription());
        request.setReferenceNumber(request.getReferenceNumber() + "-Clone");

        Project newProject = createProject(request);
        requestProject.exampleProjectRemote(newProject.getId(), clonedProject.getId());
        return newProject;
    }

    public Project importProject(ProjectRequestDTO projectRequestDTO, String importString) {
        Project project = createProject(projectRequestDTO);
        requestProject.importProjectRemote(project.getId(), importString);
        return project;
    }

    public Project importEclipseProject(ProjectRequestDTO projectRequestDTO, String importString) {
        Project project = createProject(projectRequestDTO);
        requestProject.importEclipseProjectRemote(project.getId(), importString);
        return project;
    }

    public Project exampleProject(ProjectRequestDTO projectRequestDTO) {
        projectRequestDTO.setName("Example Project");
        projectRequestDTO.setReferenceNumber("1337");
        projectRequestDTO.setDescription("This is an example Project...");

        Project project = createProject(projectRequestDTO);
        requestProject.exampleProjectRemote(project.getId());
        return project;
    }
}
