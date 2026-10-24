package com.smartdelivery.planner;

import com.smartdelivery.exception.NoSuitableRouteException;
import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import com.smartdelivery.model.RouteDecision;
import com.smartdelivery.strategy.RouteStrategy;
import com.smartdelivery.util.RouteUtils;

import java.util.ArrayList;
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

        List<Route> validRoutes = RouteUtils.getValidRoutes(routes, request);

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery requirements");
        }

        return strategy.selectRoute(validRoutes, request);
    }

    public String getStrategyName() {
        return strategy.getStrategyName();
    }

    public RouteDecision planWithExplanation(
            List<Route> routes, DeliveryRequest request) {

        Route selected = plan(routes, request);

        List<String> rejectedRoutes = new ArrayList<>();

        for (Route route : routes) {
            String reason = RouteUtils.getRejectionReason(route, request);

            if (reason != null) {
                rejectedRoutes.add(route.id() + ": " + reason);
            }
        }

        String explanation = strategy.explain(selected, request);

        return new RouteDecision(
                selected,
                strategy.getStrategyName(),
                explanation,
                rejectedRoutes
        );
    }
}
