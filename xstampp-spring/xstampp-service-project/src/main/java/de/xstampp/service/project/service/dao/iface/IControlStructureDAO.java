package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.dto.ControlStructureDTO;

public interface IControlStructureDAO {

	public ControlStructureDTO getControlStructure(String projectId, Integer parentId);
	
	public boolean alterControlStructure (String projectId, Integer parentId, ControlStructureDTO controlStructureDTO);
	
}
