package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.UnsafeControlActionSubHazardLink;

import java.util.List;
import java.util.UUID;

public interface IUnsafeControlActionSubHazardLinkDAO {

	public boolean deleteLink(UUID projectId, int unsafeControlActionId, int hazardId, int subHazardId, int controlActionId);
	public List<UnsafeControlActionSubHazardLink> getAllLinks(UUID projectId);
	public List<UnsafeControlActionSubHazardLink> saveAll(List<UnsafeControlActionSubHazardLink> links);
	public List<UnsafeControlActionSubHazardLink> getLinksByUnsafeControlActionId(UUID projectId, int controlActionId, int ucaId);
	public List<UnsafeControlActionSubHazardLink> getLinksBySubHazardId(UUID projectId, int hazardId, int subHazardId);
	public boolean addLink(UnsafeControlActionSubHazardLink ucaSubHazardLink);	
}
