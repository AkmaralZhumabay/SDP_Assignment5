package com.smartdelivery.demo;

import com.smartdelivery.model.*;
import com.smartdelivery.planner.RoutePlanner;
import com.smartdelivery.strategy.*;

import java.util.List;

public class FailureDemo {

    public static void run(List<Route> routes) {

        System.out.println();
        System.out.println(" Failure Scenarios ");

        RoutePlanner planner = new RoutePlanner(
                new FastestRouteStrategy()
        );

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD,
                10000,
                60,
                false
        );

        // Test 1: Empty route list
        System.out.println("\nTest 1: Empty route list");

        try {
            planner.plan(List.of(), request);
        } catch (IllegalArgumentException e) {
            System.out.println("Handled: " + e.getMessage());
        }

        // Test 2: No route satisfies the budget
        System.out.println("\nTest 2: Insufficient budget");

        DeliveryRequest lowBudgetRequest = new DeliveryRequest(
                DeliveryPriority.ECONOMY,
                100,
                60,
                false
        );

        try {
            planner.plan(routes, lowBudgetRequest);
        } catch (com.smartdelivery.exception.NoSuitableRouteException e) {
            System.out.println("Handled: " + e.getMessage());
        }

        // Test 3: Invalid Balanced weights
        System.out.println("\nTest 3: Invalid Balanced weights");

        try {
            new BalancedRouteStrategy(0.5, 0.4, 0.3, 0.2);
        } catch (IllegalArgumentException e) {
            System.out.println("Handled: " + e.getMessage());
        }

        // Test 4: All routes unavailable
        System.out.println("\nTest 4: All routes unavailable");

        List<Route> unavailableRoutes = routes.stream()
                .map(route -> new Route(
                        route.id(),
                        route.origin(),
                        route.destination(),
                        route.distanceKm(),
                        route.estimatedMinutes(),
                        route.cost(),
                        route.carbonEmission(),
                        route.trafficDelayMinutes(),
                        false
                ))
                .toList();

        try {
            planner.plan(unavailableRoutes, request);
        } catch (com.smartdelivery.exception.NoSuitableRouteException e) {
            System.out.println("Handled: " + e.getMessage());
        }

        System.out.println("---");
    }
}
