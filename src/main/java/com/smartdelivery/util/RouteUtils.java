package com.smartdelivery.util;

import com.smartdelivery.model.*;
import java.util.ArrayList;
import java.util.List;

public class RouteUtils {

    public static int calculateTime(Route route) {
        return route.estimatedMinutes() + route.trafficDelayMinutes();
    }

    public static double calculateCost(Route route, DeliveryRequest request) {
        double priorityFee = 0;

        if (request.priority() == DeliveryPriority.URGENT) {
            priorityFee = 1000;
        }

        return route.cost() + route.distanceKm() * 50 + priorityFee;
    }

    public static List<Route> getValidRoutes(
            List<Route> routes, DeliveryRequest request) {

        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be empty");
        }

        if (request == null) {
            throw new IllegalArgumentException("Delivery request cannot be null");
        }

        List<Route> validRoutes = new ArrayList<>();

        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            if (calculateCost(route, request) > request.maxBudget()) {
                continue;
            }

            if (calculateTime(route) > request.maxDeliveryMinutes()) {
                continue;
            }

            validRoutes.add(route);
        }

        return validRoutes;
    }

    public static String getRejectionReason(
            Route route, DeliveryRequest request) {

        if (!route.available()) {
            return "Route unavailable";
        }

        if (calculateCost(route, request) > request.maxBudget()) {
            return "Budget exceeded";
        }

        if (calculateTime(route) > request.maxDeliveryMinutes()) {
            return "Delivery deadline exceeded";
        }

        return null;
    }
}
