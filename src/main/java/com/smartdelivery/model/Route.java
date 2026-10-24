package com.smartdelivery.model;

import java.util.Objects;

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
) {

    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be empty");
        }

        Objects.requireNonNull(origin, "Origin cannot be null");
        Objects.requireNonNull(destination, "Destination cannot be null");

        if (!Double.isFinite(distanceKm) || distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative or invalid");
        }

        if (estimatedMinutes < 0 || trafficDelayMinutes < 0) {
            throw new IllegalArgumentException("Delivery time cannot be negative");
        }

        if (!Double.isFinite(cost) || cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative or invalid");
        }

        if (!Double.isFinite(carbonEmission) || carbonEmission < 0) {
            throw new IllegalArgumentException("Emissions cannot be negative or invalid");
        }
    }
}
