package de.xstampp.service.project.data;

import java.io.Serializable;

/**
 * specialisation of XStamppEntity
 */
public abstract class XStamppDependentEntity extends XStamppEntity implements Serializable {
    public final static String DEPENDENT_KEY_ATTRIBUTE = "id";
}
