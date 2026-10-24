package com.smartdelivery.planner;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import com.smartdelivery.strategy.RouteStrategy;

import java.util.List;
import java.util.Objects;

public class RoutePlanner {

    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(
                strategy, "Strategy cannot be null");
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(
                strategy, "Strategy cannot be null");
    }

    public Route plan(List<Route> routes, DeliveryRequest request) {
        return strategy.selectRoute(routes, request);
    }

    public String getStrategyName() {
        return strategy.getStrategyName();
    }
}
