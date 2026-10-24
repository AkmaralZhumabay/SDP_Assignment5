package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;

import java.util.List;

public class DistanceFirstRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        Route shortest = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            if (route.distanceKm() < shortestDistance) {
                shortestDistance = route.distanceKm();
                shortest = route;
            }
        }

        return shortest;
    }

    @Override
    public String getStrategyName() {
        return "Distance First Route";
    }

    @Override
    public String explain(Route route, DeliveryRequest request) {
        return "Shortest distance: " + route.distanceKm() + " km";
    }
}
