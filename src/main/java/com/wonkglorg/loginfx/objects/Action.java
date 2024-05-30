package com.wonkglorg.loginfx.objects;

public class Action {

    private String userID;
    private String actionType;
    private String actionStatus;
    private String actionMessage;

    public Action(String userID, String actionType, String actionStatus, String actionMessage) {
        this.userID = userID;
        this.actionType = actionType;
        this.actionStatus = actionStatus;
        this.actionMessage = actionMessage;
    }

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
}
