package de.xstampp.common.errors;

public class ErrorsGroup {
    private ErrorsGroup() {
        throw new IllegalAccessError("Utility class");
    }

    public static final ErrorCondition DELETE_GROUP_WITH_PROJECTS =
            new ErrorCondition("GRP-1000", 409, "Group containing projects can not be deleted");
}
