package com.smartdelivery.model;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double cost,
        double carbonEmission,
        int trafficDelayMinutes,
        boolean available
) {}
