package com.example.monitoreoacua.service.request;

import java.util.List;

public class AssignUsersRequest {
    private String action;
    private List<Integer> monitorIds;

    public AssignUsersRequest(String action, List<Integer> monitorIds) {
        this.action = action;
        this.monitorIds = monitorIds;
    }

    public String getAction() {
        return action;
    }

    public List<Integer> getMonitorIds() {
        return monitorIds;
    }
}