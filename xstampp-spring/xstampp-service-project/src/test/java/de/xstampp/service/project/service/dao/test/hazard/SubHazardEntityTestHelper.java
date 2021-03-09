package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;

import java.sql.Timestamp;
import java.util.UUID;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;

/**
 * Wraps the functionality of the {@link XStamppEntityTestHelper} and provides
 * methods to instantiate subHazards and hazards for the subHazardDAO tests with
 * several entities.
 */
final class SubHazardEntityTestHelper {
	
	private final XStamppEntityTestHelper xstamppHelper;

	SubHazardEntityTestHelper(Timestamp now, Timestamp afterFewMinutes) {
		super();
		xstamppHelper = new XStamppEntityTestHelper(now, afterFewMinutes);
	}
	
	SubHazard getSubHazardFor(UUID projectId, int hazardId, int id, String name, String description) {
		SubHazard result = null;
		EntityDependentKey entityDependentKey = new EntityDependentKey(projectId, hazardId, id);
		result = new SubHazard(entityDependentKey);
		result.setName(name);
		result.setDescription(description);
		xstamppHelper.init(result);
		return result;
	}
	
	Hazard getHazardFor(UUID projectId, int hazardId) {
		Hazard result = null;
		ProjectDependentKey projectDependentKey = new ProjectDependentKey(projectId, hazardId);
		result = new Hazard(projectDependentKey);
		result.setName(TEST_HAZARD_NAME_1);
		result.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(result);
		return result;
	}
	
	void initXStamppAttributes(SubHazard entity) {
		xstamppHelper.init(entity);
	}
	
	void checkXStamppAttributes(SubHazard entity) {
		xstamppHelper.check(entity);
	}

}
