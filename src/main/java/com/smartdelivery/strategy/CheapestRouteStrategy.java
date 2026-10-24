package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import com.smartdelivery.model.DeliveryPriority;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        Route cheapest = null;
        double lowestCost = Double.MAX_VALUE;

        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            double distanceCost = route.distanceKm() * 50;

            double priorityFee = 0;
            if (request.priority() == DeliveryPriority.URGENT) {
                priorityFee = 1000;
            }

            double totalCost = route.cost() + distanceCost + priorityFee;

            if (totalCost < lowestCost) {
                lowestCost = totalCost;
                cheapest = route;
            }
        }

        return cheapest;
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Route";
    }
}
