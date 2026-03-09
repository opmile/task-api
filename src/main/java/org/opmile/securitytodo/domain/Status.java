package org.opmile.securitytodo.domain;

import lombok.Getter;

@Getter
public enum Status {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    COMPLETED("completed"),;

    private final String statusApi;

    Status(String statusApi) {
        this.statusApi = statusApi;
    }

    public static Status fromString(String statusApi) {
        for (Status status : Status.values()) {
            if (status.getStatusApi().equals(statusApi)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status not found: " + statusApi);
    }

    public static String toString(Status status) {
        return status.getStatusApi();
    }

}
