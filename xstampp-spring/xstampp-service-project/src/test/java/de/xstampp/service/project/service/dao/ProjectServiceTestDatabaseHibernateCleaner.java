package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.data.entity.Box;
import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.HazardLossLink;
import de.xstampp.service.project.data.entity.HazardSystemConstraintLink;
import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.Responsibility;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.data.entity.UnsafeControlAction;
import de.xstampp.service.project.data.entity.UnsafeControlActionHazardLink;
import de.xstampp.service.project.data.entity.UnsafeControlActionSubHazardLink;
import de.xstampp.service.project.data.entity.control_structure.Actuator;
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.data.entity.ControlAlgorithm;
import de.xstampp.service.project.data.entity.control_structure.ControlledProcess;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.data.entity.control_structure.Feedback;
import de.xstampp.service.project.data.entity.control_structure.Input;
import de.xstampp.service.project.data.entity.control_structure.Output;
import de.xstampp.service.project.data.entity.control_structure.ProcessVariable;
import de.xstampp.service.project.data.entity.control_structure.Sensor;

public class ProjectServiceTestDatabaseHibernateCleaner extends TestDatabaseHibernateCleaner {
	
	/**
	 * Ensures the order of the delete operations.
	 */
	private static final Class<?>[] ENTITY_TYPES = {
			Input.class,
			Output.class,
			Feedback.class,
			ProcessVariable.class,
			ControlAlgorithm.class,
			ControllerConstraint.class,
			UnsafeControlActionSubHazardLink.class,
			UnsafeControlActionHazardLink.class,
			UnsafeControlAction.class,
			ControlAction.class,
			Responsibility.class,
			Controller.class,
			ControlledProcess.class,
			Sensor.class,
			Actuator.class,
			Arrow.class,
			Box.class,
			HazardSystemConstraintLink.class,
			HazardLossLink.class,
			SubSystemConstraint.class,
			SystemConstraint.class,
			SubHazard.class,
			Hazard.class,
			Loss.class,
			SystemDescription.class,
			Project.class
	};

	@Override
	protected Class<?>[] getRegisteredEntityTypes() {
		return ENTITY_TYPES;
	}

}
