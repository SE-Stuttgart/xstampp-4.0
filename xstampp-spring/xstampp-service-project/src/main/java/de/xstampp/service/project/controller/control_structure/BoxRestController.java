package de.xstampp.service.project.controller.control_structure;

import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.ControlStructureDTO;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/project")
public class BoxRestController {

	private SerializationUtil ser = new SerializationUtil();

	@Autowired
	ControlStructureDataService controlStructureDataService;


	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/box/input/search", method = RequestMethod.GET)
	public String getAllInputBoxes(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
		// Load all boxes using control structure data service
		ControlStructureDTO controlStructureDTO = controlStructureDataService.getRootControlStructure(projectId);
		List<ControlStructureDTO.BoxDTO> boxes = controlStructureDTO.getBoxes();

		// Remove all boxes which are not input boxes & return
		boxes.removeIf(box -> (!box.getType().equals("InputBox")));
		return ser.serialize(boxes);
	}
}
