package com.smartdelivery.comparison;

import com.smartdelivery.model.*;
import com.smartdelivery.planner.RoutePlanner;
import com.smartdelivery.strategy.*;
import com.smartdelivery.exception.NoSuitableRouteException;
import com.smartdelivery.util.RouteUtils;

import java.util.List;

public class StrategyComparison {

    private final List<RouteStrategy> strategies;

    public StrategyComparison(List<RouteStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    public void compare(List<Route> routes, DeliveryRequest request) {

        System.out.println(" Strategy Comparison");
        System.out.println("Priority: " + request.priority());
        System.out.println("Maximum Budget: " + request.maxBudget() + " KZT");
        System.out.println("Deadline: " + request.maxDeliveryMinutes() + " min");
        System.out.println();

        for (RouteStrategy strategy : strategies) {

            RoutePlanner planner = new RoutePlanner(strategy);

            System.out.println("Strategy: " + strategy.getStrategyName());

            try {
                Route selected = planner.plan(routes, request);

                System.out.println("Selected: " + selected.id());
                System.out.println("Time: "
                        + RouteUtils.calculateTime(selected) + " min");
                System.out.println("Total Cost: "
                        + RouteUtils.calculateCost(selected, request) + " KZT");
                System.out.println("Distance: "
                        + selected.distanceKm() + " km");
                System.out.println("CO2: "
                        + selected.carbonEmission() + " kg");

            } catch (NoSuitableRouteException e) {
                System.out.println("Result: " + e.getMessage());
            }

            System.out.println("---");
        }
    }
}
