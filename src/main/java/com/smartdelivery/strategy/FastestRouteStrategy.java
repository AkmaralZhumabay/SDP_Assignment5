package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        Route fastest = null;
        int shortestTime = Integer.MAX_VALUE;

        for (Route route : routes) {
            if (!route.available()) {
                continue;
            }

            int totalTime = route.estimatedMinutes()
                    + route.trafficDelayMinutes();

            if (totalTime < shortestTime) {
                shortestTime = totalTime;
                fastest = route;
            }
        }

        return fastest;
    }

    @Override
    public String getStrategyName() {
        return "Fastest Route";
    }
}
