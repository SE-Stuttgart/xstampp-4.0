package de.xstampp.service.project.util;

import de.xstampp.service.project.util.annotation.CheckState;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class StateControl {

    public enum STATE {
        TODO, DOING, DONE
    }

    /**
     * Searches all empty fields of given entity which are annotated with @CheckState
     * @param entity The entity to check the fields of
     * @param group The group to check
     * @return A list of all names of attributes which are empty
     */
    public List<String> validateEntity(Object entity, int group) {
        // Get all attributes of entity class
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<Object> values = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        // Store all field values to values list which are annotated with @CheckState
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            try {
                if (field.isAnnotationPresent(CheckState.class)) {
                    // Check whether annotation has correct group
                    // If group is not equal to given group parameter, do not check field
                    List<Integer> groupList = new ArrayList(Arrays.asList(field.getAnnotation(CheckState.class).group()));
                    if (groupList.contains(group)) {
                        field.setAccessible(true);
                        values.add(field.get(entity));
                        indices.add(i);
                    }
                }
            } catch (IllegalAccessException e) {
                // Thrown if field does not exist in class
                // Should never happen because we retrieve fields directly from class
                e.printStackTrace();
            }
        }

        // Search all empty fields in values list and store to emptyFields
        List<String> emptyFields = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            Object obj = values.get(i);

            if (obj == null || obj.toString().isEmpty()) {
                emptyFields.add(fields[indices.get(i)].getName());
            }
        }

        return emptyFields;
    }

    public List<String> validateEntity(Object entity) {
        return this.validateEntity(entity, 0);
    }

    /**
     * Checks whether the state is DOING
     * @return True if work on entity exists, false if not
     */
    public boolean isStateDoing(Object entity) {
        Class<?> clazz = entity.getClass();
        int numFields = clazz.getDeclaredFields().length;
        return this.validateEntity(entity).size() != numFields;
    }
}
