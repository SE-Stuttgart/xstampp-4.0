package de.xstampp.service.project.data.dto;

import de.xstampp.service.project.service.data.ResponsibilityFilterPreviewDataService;

import java.util.Map;
import java.util.UUID;

/**
 * <p>This object is the result of
 * {@link ResponsibilityFilterPreviewDataService#getResponsibilityFilterPreview(UUID, Integer, Integer)
 * ResponsibilityFilterPreviewDataService.getResponsibilityFilterPreview(UUID, Integer, Integer)}. It provides preview
 * numbers specifically for a certain Responsibility filtering feature.
 * </p><br>
 * <h3>The Responsibility filtering feature</h3><p>
 * This feature is designed to allow flexible filtering while displaying responsibilities. It provides two lists, one
 * with all System Constraints and one with all Controllers that exist in the project. Additionally, both lists contain
 * a "Select all" element at the very top. The user can select any single element in both lists. The displayed
 * Responsibilities get filtered depending on the selected elements.
 * </p><p>
 * If a specific System Constraint is selected, only Responsibilities which are linked to that System Constraint get
 * displayed. If instead the "Select all" element in the System Constraint table is selected, Responsibilities will
 * <i>not</i> get filtered by their linked System Constraints. The same goes for the Controller list and its elements.
 * </p><br>
 * <h3>Preview numbers</h3><p>
 * Each element in both lists also is assigned a number. This number provides information about how many
 * Responsibilities would get displayed if you would click this element from the current configuration. The only purpose
 * of this object is to transfer those preview numbers.
 * </p><br>
 * <h3>Example</h3><p>
 * The currently selected System Constraint is "Avoid explosions" and in the Controller table, the "Select all" element
 * is selected. Currently, there are 5 Responsibilities that are linked to the System Constraint "Avoid explosions"
 * and therefore those 5 are displayed right now.
 * </p><p>
 * If you would click any element which is already selected, nothing would change, the same filtering operation would be
 * applied and therefore the number of displayed Responsibilities would still be 5. This means the already selected
 * elements from both tables show the preview number 5.
 * </p><p>
 * If you would change the Controller element from "Select all" to "Gas tank", only Responsibilities would be displayed
 * that are both linked to the System Constraint "Avoid Explosions" and to the Responsibility "Gas tank". If only 2
 * Responsibilities fit those criteria, those 2 Responsibilities would be displayed.
 * </p><p>
 * This means while the "Avoid Explosions" System Constraint and the "Select all" element in the Controller list are
 * selected, the "Gas tank" element in the Controller has the preview number 2 because 2 Responsibilities would be
 * displayed when applying the new filter criteria when clicking the "Gas tank" element.
 * </p><br>
 * <h3>How to use</h3><p>
 * getResponsibilityFilterPreview(UUID, Integer, Integer) has the parameters <i>systemConstraintId</i> and
 * <i>controllerId</i>. You pass the id of the currently selected elements to those parameters. If somewhere
 * "Select all" is selected, pass null instead. The method will then return this object which contains all (non 0)
 * preview numbers for every element that you can click on in both lists.
 * </p><br>
 * <h3>Object attributes</h3>
 * <ul>
 * <li>{@link #allSystemConstraintsPreview}: The preview number for the "Select All" element in the System
 * Constraint list</li>
 * <li>{@link #systemConstraintIdToPreviewMap}: A map with System Constraint ids as its keys and the corresponding
 * preview number as its values. System Constraints which would have a preview number of 0 are not included.</li>
 * <li>{@link #allControllersPreview}: The preview number for the "Select All" element in the Controller list</li>
 * <li>{@link #controllerIdToPreviewMap}: A map with Controller ids as its keys and the corresponding preview number as
 * its values. Controllers which would have a preview number of 0 are not included.</li>
 * </ul>
 */
public class ResponsibilityFilterPreviewResponseDTO {

    /**
     * The preview number for the "Select All" element in the System Constraint list.
     *
     * @see ResponsibilityFilterPreviewResponseDTO
     */
    private Integer allSystemConstraintsPreview;
    /**
     * A map with System Constraint ids as its keys and the corresponding preview number as its values. System
     * Constraints which would have a preview number of 0 are not included.
     *
     * @see ResponsibilityFilterPreviewResponseDTO
     */
    private Map<Integer, Integer> systemConstraintIdToPreviewMap;
    /**
     * The preview number for the "Select All" element in the Controller list.
     *
     * @see ResponsibilityFilterPreviewResponseDTO
     */
    private Integer allControllersPreview;
    /**
     * A map with Controller ids as its keys and the corresponding preview number as its values. Controllers which would
     * have a preview number of 0 are not included.
     *
     * @see ResponsibilityFilterPreviewResponseDTO
     */
    private Map<Integer, Integer> controllerIdToPreviewMap;

    public Integer getAllSystemConstraintsPreview() {
        return allSystemConstraintsPreview;
    }

    public void setAllSystemConstraintsPreview(Integer allSystemConstraintsPreview) {
        this.allSystemConstraintsPreview = allSystemConstraintsPreview;
    }

    public Map<Integer, Integer> getSystemConstraintIdToPreviewMap() {
        return systemConstraintIdToPreviewMap;
    }

    public void setSystemConstraintIdToPreviewMap(Map<Integer, Integer> systemConstraintIdToPreviewMap) {
        this.systemConstraintIdToPreviewMap = systemConstraintIdToPreviewMap;
    }

    public Integer getAllControllersPreview() {
        return allControllersPreview;
    }

    public void setAllControllersPreview(Integer allControllersPreview) {
        this.allControllersPreview = allControllersPreview;
    }

    public Map<Integer, Integer> getControllerIdToPreviewMap() {
        return controllerIdToPreviewMap;
    }

    public void setControllerIdToPreviewMap(Map<Integer, Integer> controllerIdToPreviewMap) {
        this.controllerIdToPreviewMap = controllerIdToPreviewMap;
    }
}
