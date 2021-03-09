package de.xstampp.common.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used in the RestController context to ignore the
 * methods in this class for the default privilege check
 * 
 * @author Tobias Weiß
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnorePrivilegeCheck {

}
