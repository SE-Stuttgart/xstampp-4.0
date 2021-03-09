package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.UnsafeControlActionHazardLink;

import java.util.List;
import java.util.UUID;

public interface IUnsafeControlActionHazardLinkDAO {

	public boolean addLink(UnsafeControlActionHazardLink ucaHazardLink );
	public boolean deleteLink(UUID projectId, int unsafeControlActionId, int hazardId, int controlActionId);
	public List<UnsafeControlActionHazardLink> saveAll(List<UnsafeControlActionHazardLink> unsafeControlActionHazardLinks);
	public List<UnsafeControlActionHazardLink> getAllLinks(UUID projectId);
	public List<UnsafeControlActionHazardLink> getLinksByUnsafeControlActionId(UUID projectId, int controlActionId, int ucaId);
	public List<UnsafeControlActionHazardLink> getLinksByHazardId(UUID projectId, int hazardId);	
}
