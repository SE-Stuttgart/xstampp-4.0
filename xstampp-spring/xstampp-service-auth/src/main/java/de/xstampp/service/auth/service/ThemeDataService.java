package de.xstampp.service.auth.service;

import de.xstampp.service.auth.dto.ThemeRequestDTO;
import de.xstampp.service.auth.data.Theme;

import de.xstampp.service.auth.service.dao.IThemeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.xstampp.service.auth.service.dao.ILastIdDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * This service is used to retrieve and manipulate data related to themes.
 */

@Service
@Transactional
public class ThemeDataService {

    @Autowired
    IThemeDAO themeDAO;

    @Autowired
    ILastIdDAO lastIdDAO;

    /**
     * Generates a new theme with a new ID and the given parameters
     *
     * @param request   the theme request DTO with the parameters to set
     * @return returns the new theme
     */
    public Theme createTheme(ThemeRequestDTO request) {
       int themeId = this.getNewThemeId();

        Theme theme = new Theme(themeId);
        theme.setName(request.getName());
        theme.setColors(request.getColors());

        Theme result = themeDAO.makePersistent(theme);
        return result;
    }

    /**
     * Alters an existing theme by its id
     *
     * @param request      the new theme which contains all attributes including the
     *                  altered values
     * @param themeId    the id of the existing theme which should be altered
     * @return returns the altered theme including all changes
     */
    public Theme alterTheme(ThemeRequestDTO request, int themeId) {
        Theme theme = new Theme(themeId);
        theme.setName(request.getName());
        theme.setColors(request.getColors());
        Theme result = themeDAO.updateExisting(theme);
        return result;
    }

    /**
     * deletes a theme by its id
     *
     * @param themeId    the theme id
     * @return returns true if the theme could be deleted successfully
     */
    public boolean deleteTheme(int themeId) {
        Theme theme = themeDAO.findById(themeId, false);
        if (theme != null) {
            themeDAO.makeTransient(theme);
            return true;
        }
        return false;
    }

    /**
     * Get theme by id
     *
     * @param themeId    the theme id
     * @return the theme for the given id
     */
    public Theme getThemeById(int themeId) {
        return themeDAO.findById(themeId, false);
    }

    /**
     * Returns a list of themes paged by the given parameters
     *
     * @return returns a list of themes reduced by the given criteria
     */
    public List<Theme> getAllThemes() {
        return themeDAO.getAllThemes();
    }

    public int getNewThemeId() {
        // TODO: FIX @Timo
        // int themeId = lastIdDAO.getNewIdforEntityWithoutPid(Theme.class);

        ArrayList<Theme> themes = new ArrayList<Theme>();
        themes.addAll(this.getAllThemes());
        return themes.size();
    }
}
