package de.xstampp.service.project.service.dao.control_structure.iface;

import de.xstampp.service.project.data.entity.control_structure.VectorGraphic;

import java.util.UUID;

public interface IVectorGraphicDAO {

    /**
     * saves a vector graphic to the database
     * @param vectorGraphic vector graphic as string
     */
    public void saveVectorGraphic(VectorGraphic vectorGraphic);

    /**
     *
     * @param projectId project graphic belongs to
     * @return vector graphic for a project
     */
    public VectorGraphic getVectorGraphic(UUID projectId, boolean colored);
}
