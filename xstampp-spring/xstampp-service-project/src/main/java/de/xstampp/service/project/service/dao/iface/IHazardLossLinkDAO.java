package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.HazardLossLink;

import java.util.List;
import java.util.UUID;

public interface IHazardLossLinkDAO {

	public boolean addLink(UUID projectId, int hazardId, int lossId);
	public boolean addLink(HazardLossLink hazardLossLink);
	public boolean deleteLink (UUID projectId, int hazardId, int lossId);
	public List<HazardLossLink> saveAll(List<HazardLossLink> hazardLossLinks);
	public List<HazardLossLink> getAllLinks(UUID projectId);
	public List<HazardLossLink> getLinksByLossId (UUID projectId, int lossId, int amount, int from);
	public List<HazardLossLink> getLinksByHazardId (UUID projectId, int hazardId, int amount, int from);
	
}
