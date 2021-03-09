package de.xstampp.common.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define access right levels for rest endpoints.
 * Therefore a method has to be annotated and the right level has to be set as
 * parameter.
 * 
 * @author Tobias Weiß
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivilegeCheck {

	Privileges privilege() default Privileges.GUEST;
}
