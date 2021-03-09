package de.xstampp.service.auth.service;

import de.xstampp.service.auth.dto.IconRequestDTO;
import de.xstampp.service.auth.data.Icon;
import de.xstampp.service.auth.service.dao.IIconDAO;
import de.xstampp.service.auth.service.dao.ILastIdDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

/**
 * This service is used to retrieve and manipulate data related to themes.
 */


@Service
@Transactional
public class IconDataService {

    @Autowired
    IIconDAO iconDAO;

    @Autowired
    ILastIdDAO lastIdDAO;

    /**
     * Generates a new icon with a new ID and the given parameters
     *
     * @param request   the icon request DTO with the parameters to set
     * @return returns the new icon
     */
    public Icon createIcon(IconRequestDTO request) {
      //  int iconId = lastIdDAO.getNewIdforEntityWithoutPid(Icon.class);

      ArrayList<Icon> themes = new ArrayList<Icon>();
       themes.addAll(this.getAllIcons());
       int iconId = themes.size();
        Icon icon = new Icon(iconId);
        icon.setName(request.getName());
        icon.setImage(request.getImage());

        Icon result = iconDAO.makePersistent(icon);
        return result;
    }

    /**
     * Alters an existing icon by its id
     *
     * @param request      the new icon which contains all attributes including the
     *                  altered values
     * @param iconId    the id of the existing icon which should be altered
     * @return returns the altered icon including all changes
     */
    public Icon alterIcon(IconRequestDTO request, int iconId) {
        Icon icon = new Icon(iconId);
        icon.setName(request.getName());
        icon.setName(request.getName());
        icon.setImage(request.getImage());
        Icon result = iconDAO.updateExisting(icon);
        return result;
    }

    /**
     * deletes a icon by its id
     *
     * @param iconId    the icon id
     * @return returns true if the icon could be deleted successfully
     */
    public boolean deleteIcon(int iconId) {
        Icon icon = iconDAO.findById(iconId, false);
        if (icon != null) {
            iconDAO.makeTransient(icon);
            return true;
        }
        return false;
    }

    /**
     * Get icon by id
     *
     * @param iconId    the icon id
     * @return the icon for the given id
     */
    public Icon getIconById(int iconId) {
        return iconDAO.findById(iconId, false);
    }

    /**
     * Returns a list of icons paged by the given parameters
     *
     * @return returns a list of icons reduced by the given criteria
     */
    public List<Icon> getAllIcons() {
        return iconDAO.getAllIcons();
    }
}
