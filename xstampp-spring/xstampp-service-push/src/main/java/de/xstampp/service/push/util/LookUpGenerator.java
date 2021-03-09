package de.xstampp.service.push.util;

import java.util.Arrays;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import de.xstampp.common.utils.EntityNameConstants;

/**
 * generates the lookup map for the topic selction. This is used to infer the
 * needed push topics
 *
 */
public class LookUpGenerator {

	public static Multimap<String, String> getLockUpMap() {
		SetMultimap<String, String> lookup = HashMultimap.create();

		lookup.putAll(EntityNameConstants.PROJECT, Arrays.asList(EntityNameConstants.PROJECT));

		lookup.putAll(EntityNameConstants.SYSTEM_DESCRIPTION, Arrays.asList(EntityNameConstants.SYSTEM_DESCRIPTION));

		lookup.putAll(EntityNameConstants.HAZARD, Arrays.asList(EntityNameConstants.HAZARD, EntityNameConstants.LOSS, EntityNameConstants.SUB_HAZARD,
				EntityNameConstants.SYSTEM_CONSTRAINT, EntityNameConstants.SUB_SYSTEM_CONSTRAINT));

		lookup.putAll(EntityNameConstants.SUB_HAZARD,
				Arrays.asList(EntityNameConstants.SUB_HAZARD, EntityNameConstants.HAZARD, EntityNameConstants.SUB_SYSTEM_CONSTRAINT, EntityNameConstants.SYSTEM_CONSTRAINT));

		lookup.putAll(EntityNameConstants.LOSS, Arrays.asList(EntityNameConstants.LOSS, EntityNameConstants.HAZARD));

		lookup.putAll(EntityNameConstants.SYSTEM_CONSTRAINT, Arrays.asList(EntityNameConstants.SYSTEM_CONSTRAINT,
				EntityNameConstants.HAZARD, EntityNameConstants.SUB_HAZARD, EntityNameConstants.SUB_SYSTEM_CONSTRAINT));

		lookup.putAll(EntityNameConstants.UNSAFE_CONTROL_ACTION, Arrays.asList(EntityNameConstants.UNSAFE_CONTROL_ACTION, EntityNameConstants.HAZARD,
				EntityNameConstants.CONTROL_ACTION, EntityNameConstants.SUB_HAZARD));

		lookup.putAll(EntityNameConstants.CONTROLLER_CONSTRAINT, Arrays.asList(EntityNameConstants.CONTROLLER_CONSTRAINT));

		lookup.putAll(EntityNameConstants.CONTROL_STRUCTURE, Arrays.asList(EntityNameConstants.CONTROL_STRUCTURE));

		lookup.putAll(EntityNameConstants.SUB_SYSTEM_CONSTRAINT, Arrays.asList(EntityNameConstants.SUB_SYSTEM_CONSTRAINT,
				EntityNameConstants.SYSTEM_CONSTRAINT, EntityNameConstants.SUB_HAZARD, EntityNameConstants.HAZARD));

		lookup.putAll(EntityNameConstants.CONTROLLER,
				Arrays.asList(EntityNameConstants.CONTROLLER, EntityNameConstants.CONTROL_STRUCTURE, EntityNameConstants.RESPONSIBILITY));
		lookup.putAll(EntityNameConstants.CONTROL_ACTION, Arrays.asList(EntityNameConstants.CONTROL_ACTION, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.FEEDBACK, Arrays.asList(EntityNameConstants.FEEDBACK, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.ACTUATOR, Arrays.asList(EntityNameConstants.ACTUATOR, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.SENSOR, Arrays.asList(EntityNameConstants.SENSOR, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.CONTROLLED_PROCESS,
				Arrays.asList(EntityNameConstants.CONTROLLED_PROCESS, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.PROCESS_VARIABLE,
				Arrays.asList(EntityNameConstants.PROCESS_VARIABLE, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.CONTROL_ALGORITHM,
				Arrays.asList(EntityNameConstants.CONTROL_ALGORITHM, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.INPUT, Arrays.asList(EntityNameConstants.INPUT, EntityNameConstants.CONTROL_STRUCTURE));
		lookup.putAll(EntityNameConstants.OUTPUT, Arrays.asList(EntityNameConstants.OUTPUT, EntityNameConstants.CONTROL_STRUCTURE));

		lookup.putAll(EntityNameConstants.RESPONSIBILITY, Arrays.asList(EntityNameConstants.RESPONSIBILITY, EntityNameConstants.CONTROLLER, EntityNameConstants.CONTROLLER));
		
		lookup.putAll(EntityNameConstants.RULE, Arrays.asList(EntityNameConstants.RULE, EntityNameConstants.CONTROL_ACTION));
		
		lookup.putAll(EntityNameConstants.CONVERSION, Arrays.asList(EntityNameConstants.CONVERSION, EntityNameConstants.CONTROL_ACTION));

		lookup.putAll(EntityNameConstants.USER, Arrays.asList(EntityNameConstants.USER, EntityNameConstants.GROUP));
		lookup.putAll(EntityNameConstants.GROUP, Arrays.asList(EntityNameConstants.GROUP));
		lookup.putAll(EntityNameConstants.GROUP_MEMBERSHIP, Arrays.asList(EntityNameConstants.GROUP_MEMBERSHIP, EntityNameConstants.GROUP));

		return lookup;
	}
}
