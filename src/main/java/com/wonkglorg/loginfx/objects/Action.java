package com.wonkglorg.loginfx.objects;

public record Action(String userID, String actionType, String actionStatus, String actionMessage, String actionTime) {

    public String getUserID() {
        return userID;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public String getActionTime() {
        return actionTime;
    }


}
