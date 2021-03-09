package de.xstampp.service.project.service.data;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import de.xstampp.service.project.data.entity.control_structure.VectorGraphic;
import de.xstampp.service.project.service.dao.control_structure.iface.IVectorGraphicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.service.project.data.dto.ControlStructureDTO;
import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.data.entity.Box;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.control_structure.iface.IArrowDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IBoxDAO;
import de.xstampp.service.project.service.dao.iface.IControlStructureDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Control structure Service is used to save, alter and load the control
 * structure.
 * 
 * Warn: This does not use the generic hibernate implementation due to the very
 * special usecase.
 * 
 * @author Tobias Weiß
 *
 */
@Service
@Transactional
public class ControlStructureDataService {

	@Autowired
	IControlStructureDAO controlStructureDAO;

	@Autowired
	IBoxDAO boxDAO;

	@Autowired
	IArrowDAO arrowDAO;

	@Autowired
	IProjectDAO projectData;

	@Autowired
	IVectorGraphicDAO vectorGraphicDAO;

	@Autowired
	SessionFactory sessionFactory;

	private final String ARROW_LABEL_SEP_OPEN = "@?!@";

	private final String ARROW_LABEL_SEP_CLOSE = "@!?@";

	public ControlStructureDTO getRootControlStructure(String projectId) {
		return getGenericControlStructure(projectId, null);
	}

	public ControlStructureDTO getDetailedControlStructure(String projectId, Integer parentId) {
		return getGenericControlStructure(projectId, parentId);
	}

	public boolean alterRootControlStructure(String projectId, ControlStructureDTO controlStructure) {

		return alterGenericControlStructure(projectId, null, controlStructure);
	}


	public boolean alterDetailedCotrolStructure(String projectId, Integer parent, ControlStructureDTO controlStructure) {

		return alterGenericControlStructure(projectId, parent, controlStructure);
	}

	private boolean alterGenericControlStructure(String projectId, Integer parent,
			ControlStructureDTO controlStructure) {
		resetLock(projectId);
		VectorGraphic vectorGraphic = new VectorGraphic(UUID.fromString(controlStructure.getProjectId()), controlStructure.getSvg(),true);
		vectorGraphicDAO.saveVectorGraphic(vectorGraphic);
		VectorGraphic blackAndWhiteVectorGraphic = new VectorGraphic(UUID.fromString(controlStructure.getProjectId()), controlStructure.getBlackAndWhiteSVG(),false);
		vectorGraphicDAO.saveVectorGraphic(blackAndWhiteVectorGraphic);
		return controlStructureDAO.alterControlStructure(projectId, parent, controlStructure);
	}

	private ControlStructureDTO getGenericControlStructure(String projectId, Integer parent) {

		return controlStructureDAO.getControlStructure(projectId, parent);
	}

	public void resetLock(String projectId) {
		Project project = projectData.findById(UUID.fromString(projectId), false);

		if (project != null) {
			project.setLockExpirationTime(Timestamp.from(Instant.now()));
		}
		projectData.makePersistent(project);
	}

	/**
	 * Updates the name of every box which is a child of given parent
	 * @param projectId The project id
	 * @param parentId The parent
	 * @param type The box type (i.e. controller)
	 * @param name The new name to set
	 * @return True whether edit was successful, false if something went wrong
	 */
	public boolean alterBox(UUID projectId, int parentId, String type, String name) {
		if (type == null)
			return false;
		
		List<Box> boxList = boxDAO.getBoxByTypeAndParent(projectId, parentId, type);

		for (Box box : boxList) {
			box.setName(name);
			sessionFactory.getCurrentSession().saveOrUpdate(box);
		}

		return true;
	}

	/**
	 * Updates the name of every arrow which is a child of given parent
	 * @param projectId The project id
	 * @param parentId The parent
	 * @param type The arrow type (i.e. control action)
	 * @param oldName The old name of parent
	 * @param newName The new name of parent
	 * @return True whether edit was successful, false if something went wrong
	 */
	public boolean alterArrow(UUID projectId, int parentId, String type, String oldName, String newName) {
		if (type == null) {
			return false;
		}

		List<Arrow> arrowList = arrowDAO.getArrowsByType(projectId, type);

		// Iterate over every arrow with given type
		for (Arrow arrow : arrowList) {
			String parents = arrow.getParents();
			if (parents == null) {
				continue;
			}

			parents = parents.replaceAll("\n", " ");
			String[] idArrayDB = parents.trim().split(Pattern.quote(this.ARROW_LABEL_SEP_OPEN));
			// Split label + delete empty ones as they're useless
			String[] splitArray = this.splitArrowLabel(arrow.getLabel());

			// Check whether current arrow is children of given parent id
			for (String idDB : idArrayDB) {
				if (idDB == null || idDB.isEmpty()) {
					continue;
				}

				if (parentId == Integer.parseInt(idDB)) {
					// Update name for current arrow
					if (oldName != null && !oldName.isEmpty()) {
						// If oldName isn't empty, normally replace label
						// Here, we do not care whether newName is empty or not
						int idx = this.searchEntryInArray(splitArray, oldName);
						splitArray[idx] = newName;
					} else if (newName != null && !newName.isEmpty()) {
						// If oldName AND newName are empty, do nothing
						// If old Name is empty and newName is not, simply replace first empty label name with newName
						splitArray = this.replaceFirstEmptyLabel(splitArray, newName);
					}

					// Safe updated arrow in database
					arrow.setLabel(this.concatArrayToString(splitArray));
					sessionFactory.getCurrentSession().saveOrUpdate(arrow);
				}
			}
		}
		return true;
	}

	/**
	 * Deletes the given name + coordinates in the arrow label
	 * @param projectId The project id
	 * @param type The arrow type (i.e. control action)
	 * @param name The name to delete
	 * @return True whether edit was successful, false if something went wrong
	 */
	public boolean deleteNameInArrowLabel(UUID projectId, int parentId, String type, String name) {
		if (type == null)
			return false;

		List<Arrow> arrowList = arrowDAO.getArrowsByType(projectId, type);

		// Iterate over every arrow with given type
		for (Arrow arrow : arrowList) {
			String parents = arrow.getParents();
			if (parents == null) {
				continue;
			}

			parents = parents.replaceAll("\n", " ");
			String[] idArrayDB = parents.trim().split(Pattern.quote(this.ARROW_LABEL_SEP_OPEN));
			String[] splitArray = this.splitArrowLabel(arrow.getLabel());

			int idx = this.searchEntryInArray(splitArray, name);

			// Check whether current arrow is children of given parent id
			for (String idDB : idArrayDB) {
				if (idDB == null || idDB.isEmpty()) {
					continue;
				}

				if (parentId == Integer.parseInt(idDB)) {
					// If so, delete name & coordinates
					splitArray[idx] = ""; // delete entry
					splitArray[idx + 1] = ""; // delete coordinates

					// Safe arrow
					arrow.setLabel(this.concatArrayToString(splitArray));
					sessionFactory.getCurrentSession().saveOrUpdate(arrow);
				}
			}
		}
		return true;
	}

	/**
	 * Replaces the first empty element in given array with given newName
	 * @param array The array to replace the element in
	 * @param newName The element to insert into array
	 * @return An array in which first empty element is replaced with newName
	 */
	private String[] replaceFirstEmptyLabel(String[] array, String newName) {
		for (int i = 0; i < array.length; i += 2) {
			String current = array[i];
			if (current != null && !current.isEmpty()) {
				array[i] = newName;
				break;
			}
		}
		return array;
	}

	/**
	 * Splits a arrow label by "@?!@" and "@!?@"
	 * @param label The label string to split
	 * @return A string array containing the split version of label
	 */
	private String[] splitArrowLabel(String label) {
		// Quote method is used to escape special characters
		return label.trim().split(Pattern.quote(this.ARROW_LABEL_SEP_OPEN) + "|" + Pattern.quote(this.ARROW_LABEL_SEP_CLOSE));
	}

	/**
	 * Searches the given entry in the given array
	 * @param array The array to search in
	 * @param entry The entry to search for
	 * @return The index of the given entry in given array or -1 if entry doesn't exist
	 */
	private int searchEntryInArray(String[] array, String entry) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(entry)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Concatenates every entry in given array
	 * @param array The array to concatenate
	 * @return A string with concatenated array entries + split symbols for arrow labels
	 */
	private String concatArrayToString(String[] array) {
		// Note: We do NOT use += to concat strings, as this would send GC through the roof because
		// you'd creating and throwing away as many string objects as you have items in your array - Timo

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			builder.append(i % 2 == 0 ? this.ARROW_LABEL_SEP_OPEN : this.ARROW_LABEL_SEP_CLOSE);
		}

		return builder.toString();
	}
}
