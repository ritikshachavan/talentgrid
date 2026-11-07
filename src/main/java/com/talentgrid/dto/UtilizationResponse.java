package com.talentgrid.dto;

public record UtilizationResponse(
        Long employeeId, String empCode, String fullName,
        Long allocatedPercent, Long availablePercent) {

    // Used by the HQL "SELECT new ..." query
    public UtilizationResponse(Long employeeId, String empCode, String fullName, Long allocatedPercent) {
        this(employeeId, empCode, fullName, allocatedPercent, 100 - allocatedPercent);
    }
}