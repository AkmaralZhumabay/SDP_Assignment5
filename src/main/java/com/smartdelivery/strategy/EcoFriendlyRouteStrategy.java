package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        Route bestRoute = null;
        double lowestScore = Double.MAX_VALUE;

        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            double ecoScore = route.carbonEmission() * 0.7
                    + route.distanceKm() * 0.3;

            if (ecoScore < lowestScore) {
                lowestScore = ecoScore;
                bestRoute = route;
            }
        }

        return bestRoute;
    }

    @Override
    public String getStrategyName() {
        return "Eco-Friendly Route";
    }

    @Override
    public String explain(Route route, DeliveryRequest request) {
        double score = route.carbonEmission() * 0.7
                + route.distanceKm() * 0.3;

        return "Lowest eco score: " + score;
    }
}
