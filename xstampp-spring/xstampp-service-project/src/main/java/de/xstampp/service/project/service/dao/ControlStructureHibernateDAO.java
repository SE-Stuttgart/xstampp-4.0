package de.xstampp.service.project.service.dao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.dto.ControlStructureDTO;
import de.xstampp.service.project.data.dto.ControlStructureDTO.ArrowDTO;
import de.xstampp.service.project.data.dto.ControlStructureDTO.BoxDTO;
import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.data.entity.Box;
import de.xstampp.service.project.service.dao.iface.IControlStructureDAO;

@Repository
public class ControlStructureHibernateDAO implements IControlStructureDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public ControlStructureDTO getControlStructure(String projectId, Integer parentId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();

		/*
		 * Load all Boxes for control structure
		 */
		CriteriaQuery<Box> boxQuery = builder.createQuery(Box.class);
		Root<Box> rootBox = boxQuery.from(Box.class);
		boxQuery.select(rootBox);
		// select boxes where box.projectId = this.projectId and box.parent and
		// this.parent

		if (parentId != null) {
			boxQuery.where(builder.and(builder.equal(rootBox.get("projectId"), UUID.fromString(projectId)),
				builder.equal(rootBox.get("parent"), parentId)));
		} else {
			boxQuery.where(builder.equal(rootBox.get("projectId"), UUID.fromString(projectId)));
		}

		/*
		 * Load all Arrows for control structure
		 */
		CriteriaQuery<Arrow> arrowQuery = builder.createQuery(Arrow.class);
		Root<Arrow> rootArrow = arrowQuery.from(Arrow.class);
		arrowQuery.select(rootArrow);
		// select arrows where arrow.projectId = this.projectId and arrow.parent =
		// this.parent

		if (parentId != null) {
			arrowQuery.where(builder.and(builder.equal(rootArrow.get("projectId"), UUID.fromString(projectId)),
					builder.equal(rootArrow.get("parent"), parentId)));
		} else {
			arrowQuery.where(builder.equal(rootArrow.get("projectId"), UUID.fromString(projectId)));
		}
		
		List<Box> boxes = getSession().createQuery(boxQuery).getResultList();
		List<Arrow> arrows = getSession().createQuery(arrowQuery).getResultList();

		ControlStructureDTO controlStructure = new ControlStructureDTO();
		controlStructure.setProjectId(projectId);
		//TODO add parts
		controlStructure.setArrows(arrows.stream().map(ArrowDTO::new).collect(Collectors.toList()));
		controlStructure.setBoxes(boxes.stream().map(BoxDTO::new).collect(Collectors.toList()));

		return controlStructure;
	}

	@Override
	public boolean alterControlStructure(String projectId, Integer parentId, ControlStructureDTO controlStructure) {
		UUID projectUUID = UUID.fromString(projectId);

		// Delete Existing Entities
		Session s = getSession();
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaDelete<Box> boxDelete = builder.createCriteriaDelete(Box.class);
		Root<Box> rootBox = boxDelete.from(Box.class);

		if (parentId != null) {
			boxDelete.where(builder.and(builder.equal(rootBox.get("projectId"), projectUUID),
					builder.equal(rootBox.get("parent"), parentId)));
		} else {
			boxDelete.where(builder.equal(rootBox.get("projectId"), projectUUID));
		}
		getSession().createQuery(boxDelete).executeUpdate();

		CriteriaDelete<Arrow> arrowDelete = builder.createCriteriaDelete(Arrow.class);
		Root<Arrow> rootArrow = arrowDelete.from(Arrow.class);

		if (parentId != null) {
			arrowDelete.where(builder.and(builder.equal(rootArrow.get("projectId"), projectUUID),
					builder.equal(rootArrow.get("parent"), parentId)));
		} else {
			arrowDelete.where(builder.equal(rootArrow.get("projectId"), projectUUID));
		}
		
		getSession().createQuery(arrowDelete).executeUpdate();

		// Insert new Entities
		controlStructure.getBoxes().stream().forEach(box -> s.save(new Box(box, projectUUID)));
		controlStructure.getArrows().stream().forEach(arrow -> s.save(new Arrow(arrow, projectUUID)));

		return true;
	}

}
