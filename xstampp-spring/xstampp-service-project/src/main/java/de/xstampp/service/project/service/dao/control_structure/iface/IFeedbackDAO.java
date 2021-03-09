package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Feedback;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IFeedbackDAO extends IProjectDependentGenericDAO<Feedback, ProjectDependentKey>{

	public List<Feedback> getByArrowId (UUID projectId, String arrowId);
	public List<Feedback> getAllUnlinkedFeedbacks (UUID projectId);
	public Feedback setArrowId (UUID projectID, int feedbackId, String arrowId);
}
