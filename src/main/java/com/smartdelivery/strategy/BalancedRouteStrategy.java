package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight,
                                 double distanceWeight, double emissionWeight) {

        double total = timeWeight + costWeight + distanceWeight
                + emissionWeight;

        if (!Double.isFinite(timeWeight) || !Double.isFinite(costWeight)
                || !Double.isFinite(distanceWeight)
                || !Double.isFinite(emissionWeight)
                || timeWeight < 0 || costWeight < 0
                || distanceWeight < 0 || emissionWeight < 0
                || Math.abs(total - 1.0) > 0.000001) {

            throw new IllegalArgumentException(
                    "Weights must be non-negative and sum to 1.0");
        }

        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {

        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be empty");
        }

        double maxTime = 0;
        double maxCost = 0;
        double maxDistance = 0;
        double maxEmission = 0;

        // Find maximum values for normalization
        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            double totalTime = route.estimatedMinutes()
                    + route.trafficDelayMinutes();

            double totalCost = calculateCost(route, request);

            maxTime = Math.max(maxTime, totalTime);
            maxCost = Math.max(maxCost, totalCost);
            maxDistance = Math.max(maxDistance, route.distanceKm());
            maxEmission = Math.max(maxEmission, route.carbonEmission());
        }

        Route bestRoute = null;
        double lowestScore = Double.MAX_VALUE;

        // Calculate the weighted score for each route
        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            double totalTime = route.estimatedMinutes()
                    + route.trafficDelayMinutes();

            double totalCost = calculateCost(route, request);

            double normalizedTime = maxTime == 0 ? 0 : totalTime / maxTime;
            double normalizedCost = maxCost == 0 ? 0 : totalCost / maxCost;
            double normalizedDistance = maxDistance == 0
                    ? 0 : route.distanceKm() / maxDistance;
            double normalizedEmission = maxEmission == 0
                    ? 0 : route.carbonEmission() / maxEmission;

            double score = normalizedTime * timeWeight
                    + normalizedCost * costWeight
                    + normalizedDistance * distanceWeight
                    + normalizedEmission * emissionWeight;

            if (score < lowestScore) {
                lowestScore = score;
                bestRoute = route;
            }
        }

        return bestRoute;
    }

    private double calculateCost(Route route, DeliveryRequest request) {
        double priorityFee = 0;

        if (request.priority() == com.smartdelivery.model.DeliveryPriority.URGENT) {
            priorityFee = 1000;
        }

        return route.cost() + route.distanceKm() * 50 + priorityFee;
    }

    @Override
    public String getStrategyName() {
        return "Balanced Route";
    }
}
