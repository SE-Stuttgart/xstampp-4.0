package de.xstampp.service.project.data.entity.report;

import de.xstampp.service.project.data.entity.report.structureElements.ReportStructureElement;

import java.util.ArrayList;

/**
 * List of ReportStructureElements with Prefix definition for all Entities
 */
public class ReportSegment extends ArrayList<ReportStructureElement> implements Comparable<ReportSegment> {

    //Prefixes for all Existing Entities
    public static final String LOSS_ID_PREFIX =                      "L-";
    public static final String HAZARD_ID_PREFIX =                    "H-";
    public static final String SYSTEM_CONSTRAINT_ID_PREFIX =         "SC-";
    public static final String SUBHAZARD_ID_PREFIX =                 "sH-";
    public static final String SUBSYSTEM_CONSTRAINT_ID_PREFIX =      "sSC-";

    public static final String CONTROLLER_ID_PREFIX =                "C-";
    public static final String ACTUATOR_ID_PREFIX =                  "A-";
    public static final String SENSOR_ID_PREFIX =                    "S-";
    public static final String CONTROLLED_PROCESS_ID_PREFIX =        "CP-";
    public static final String CONTROL_ACTION_ID_PREFIX =            "CA-";
    public static final String FEEDBACK_ID_PREFIX =                  "F-";
    public static final String INPUT_ID_PREFIX =                     "I-";
    public static final String OUTPUT_ID_PREFIX =                    "O-";
    public static final String RESPONSIBILITY_ID_PREFIX =            "R-";

    public static final String UCA_ID_PREFIX =                       "UCA-";
    public static final String CONTROLLER_CONSTRAINTS_ID_PREFIX =    "CC-";

    public static final String PROCESS_MODEL_ID_PREFIX =             "PM-";
    public static final String PROCESS_VARIABLE_PREFIX =             "PV-";
    public static final String CONTROL_ALGORITHM_ID_PREFIX =         "CAlg-";
    public static final String CONVERSION_ID_PREFIX =                "Cv-";
    public static final String LOSS_SCENARIO_ID_PREFIX =             "LS-";
    public static final String IMPLEMENTATION_CONSTRAINT_ID_PREFIX = "IC-";

    public final String referenceId;

    public ReportSegment(String referenceId) {
        this.referenceId = referenceId;
    }

    //Comparable implementation so Segments can be sorted -> Segments usually represent an Entity 
    @Override
    public int compareTo(ReportSegment foreignSegment) {
        String[] idPartsThis = this.referenceId.split("-");
        String[] idPartsForeign = foreignSegment.referenceId.split("-");

        if (idPartsThis[0].compareTo(idPartsForeign[0]) != 0) {
            return idPartsThis[0].compareTo(idPartsForeign[0]);
        } else {
            if (!idPartsThis[1].contains(".")) {
                Integer idThis = Integer.valueOf(idPartsThis[1]);
                Integer idForeign = Integer.valueOf(idPartsForeign[1]);
                return idThis.compareTo(idForeign);
            } else {
                String[] lastIdPartsThis = idPartsThis[1].split("\\.");
                String[] lastIdPartsForeign = idPartsForeign[1].split("\\.");
                int beforeDot = Integer.valueOf(lastIdPartsThis[0]).compareTo(Integer.valueOf(lastIdPartsForeign[0]));
                if (beforeDot != 0) {
                    return beforeDot;
                } else {
                    return Integer.valueOf(lastIdPartsThis[1]).compareTo(Integer.valueOf(lastIdPartsForeign[1]));
                }
            }
        }
    }
}

