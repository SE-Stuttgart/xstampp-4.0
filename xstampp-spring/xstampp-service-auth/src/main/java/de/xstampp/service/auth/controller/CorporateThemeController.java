package de.xstampp.service.auth.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.*;

import de.xstampp.service.auth.service.ThemeDataService;
import de.xstampp.service.auth.service.RequestPushService;
import de.xstampp.service.auth.dto.ThemeRequestDTO;
import de.xstampp.service.auth.data.Theme;

import de.xstampp.service.auth.dto.IconRequestDTO;
import de.xstampp.service.auth.data.Icon;
import de.xstampp.service.auth.service.IconDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Rest Controller for corporate themes, including CRUD operations for themes and icons
 */
@RestController
@RequestMapping("/api/auth")
public class CorporateThemeController {

    @Autowired
    ThemeDataService themeDataService;


    @Autowired
  IconDataService iconDataService;

    @Autowired
    RequestPushService push;

    SerializationUtil ser = new SerializationUtil();
    DeserializationUtil deSer = new DeserializationUtil();
    Logger logger = LoggerFactory.getLogger(CorporateThemeController.class);

    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "theme", method = RequestMethod.POST)
    public String createTheme(@RequestBody String body) throws IOException {
        ThemeRequestDTO request = deSer.deserialize(body, ThemeRequestDTO.class);
        Theme theme = themeDataService.createTheme(request);
        if (theme != null) {
            push.notify(String.valueOf(theme.getId()), null, EntityNameConstants.THEME, RequestPushService.Method.ALTER);
        }
        return ser.serialize(theme);
    }

    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "icon", method = RequestMethod.POST)
    public String updateLogo(@RequestBody String body) throws IOException {
        IconRequestDTO request = new IconRequestDTO();
        request.setId(0);
        request.setName("Logo");
        request.setImage(body.getBytes(StandardCharsets.UTF_8));

        Icon icon;
        if (iconDataService.getAllIcons().size() == 0) {
            icon = iconDataService.createIcon(request);
        } else {
            icon = iconDataService.alterIcon(request, 0);
        }

        if (icon != null) {
            push.notify(String.valueOf(icon.getId()), null, EntityNameConstants.ICON, RequestPushService.Method.ALTER);
        }
        return ser.serialize(icon);
    }

    //@PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "icon", method = RequestMethod.GET)
    public String getLogo() throws IOException {
        String iconStr = new String(iconDataService.getIconById(0).getImage());
        if(iconStr == null){
            return ser.serialize(null);
        }
        return ser.serialize(iconStr);
    }

   
    @PrivilegeCheck(privilege = Privileges.ANALYST)
   // @CheckLock(entity = EntityNameConstants.THEME)
    @RequestMapping(value = "theme/{themeId}", method = RequestMethod.PUT)
    public String alterTheme(@PathVariable(value = "themeId") int themeId, @RequestBody String body) throws IOException {
        ThemeRequestDTO requestDTO = deSer.deserialize(body, ThemeRequestDTO.class);

        Theme theme = themeDataService.alterTheme(requestDTO, themeId);

        if (theme != null) {
            push.notify(String.valueOf(themeId), null, EntityNameConstants.THEME, RequestPushService.Method.ALTER);
        }
        return ser.serialize(theme);
    }

 
    @PrivilegeCheck(privilege = Privileges.ANALYST)
   // @CheckLock(entity = EntityNameConstants.THEME)
    @RequestMapping(value = "theme/{themeId}", method = RequestMethod.DELETE)
    public String deleteTheme(@PathVariable(value = "themeId") int themeId) throws IOException {

        logger.debug("delete theme {}", themeId);
        boolean result = themeDataService.deleteTheme(themeId);

        if (result) {
            push.notify(String.valueOf(themeId), null, EntityNameConstants.THEME, RequestPushService.Method.DELETE);
        }

        return ser.serialize(new Response(result));
    }

    @PrivilegeCheck(privilege = Privileges.ANALYST)
    //@CheckLock(entity = EntityNameConstants.ICON)
    @RequestMapping(value = "icon/delete", method = RequestMethod.DELETE)
    public String deleteLogo() throws IOException {
        boolean result = iconDataService.deleteIcon(0);
        return ser.serialize(result);
    }
    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "theme/{themeId}", method = RequestMethod.GET)
    public String getThemeById(@PathVariable(value = "themeId") int themeId)
            throws IOException {

        logger.debug("get theme by id {}", themeId);
        return ser.serialize(themeDataService.getThemeById(themeId));
    }

    /*
    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "icon/{iconId}", method = RequestMethod.GET)
    public String getIconById(@PathVariable(value = "iconId") int iconId)
            throws IOException {

        logger.debug("get icon by id {}", iconId);
        return ser.serialize(iconDataService.getIconById(iconId));
    }
*/
    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "theme/search", method = RequestMethod.POST)
    public String getAllThemes(@RequestBody String body)
            throws IOException {

        logger.debug("search for themes");
        return ser.serialize(themeDataService.getAllThemes());
    }
/*
    @PrivilegeCheck(privilege = Privileges.ANALYST)
    @RequestMapping(value = "icon/search", method = RequestMethod.POST)
    public String getAllIcons(@RequestBody String body)
            throws IOException {

        logger.debug("search for icons");
        return ser.serialize(iconDataService.getAllIcons());
    }

    */
}
