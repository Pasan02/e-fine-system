package com.slpolice.trafficfines.fine.entity;

/**
 * Represents the lifecycle status of a traffic fine,
 * as defined in the ER diagram: PENDING | PAID | EXPIRED
 */
public enum FineStatus {
    PENDING,
    PAID,
    EXPIRED
}
