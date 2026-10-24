package com.smartdelivery.model;

public record DeliveryRequest(
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred
) {

    public DeliveryRequest {
        if (priority == null) {
            throw new IllegalArgumentException("Delivery priority cannot be null");
        }

        if (!Double.isFinite(maxBudget) || maxBudget < 0) {
            throw new IllegalArgumentException("Budget cannot be negative or invalid");
        }

        if (maxDeliveryMinutes < 0) {
            throw new IllegalArgumentException("Deadline cannot be negative");
        }
    }
}
