package de.xstampp.service.project.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CheckState {

    /**
     * Can be used to group attributes (important for i.e. loss scenarios)
     * @return The group(s) of an attribute
     */
    int[] group() default {0};
}
