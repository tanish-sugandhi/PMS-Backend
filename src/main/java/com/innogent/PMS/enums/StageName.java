package com.innogent.PMS.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StageName {
    PERFORMANCE_CYCLE("Performance Cycle"),
    GOAL_SETTING("Goal Setting"),
    GOAL_APPROVAL("Goal Approval"),
    GOAL_EXECUTION("Goal Execution"),
    PEER_FEEDBACK("Peer Feedback"),
    SELF_FEEDBACK("Self Feedback"),
    MANAGER_FEEDBACK("Manager Feedback");

    private final String displayName;

    StageName(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
