package com.smartdelivery;

import com.smartdelivery.selector.StrategySelector;
import com.smartdelivery.model.*;
import com.smartdelivery.planner.RoutePlanner;
import com.smartdelivery.strategy.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Delivery locations
        Location warehouse = new Location("Warehouse", 51.1605, 71.4704);
        Location customer = new Location("Customer", 51.1280, 71.4300);

        // Candidate delivery routes
        List<Route> routes = List.of(
                new Route("Route A", warehouse, customer,
                        12, 25, 3000, 2.5, 10, true),

                new Route("Route B", warehouse, customer,
                        15, 20, 4500, 3.2, 5, true),

                new Route("Route C", warehouse, customer,
                        10, 35, 2000, 1.8, 8, true),

                new Route("Route D", warehouse, customer,
                        18, 30, 2800, 4.0, 12, true),

                new Route("Route E", warehouse, customer,
                        9, 40, 1800, 1.5, 6, true),

                new Route("Route F", warehouse, customer,
                        14, 22, 5000, 2.8, 3, true),

                new Route("Route G", warehouse, customer,
                        11, 28, 3200, 2.0, 7, true),

                new Route("Route H", warehouse, customer,
                        8, 18, 5500, 1.2, 15, false)
        );

        // Delivery request
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD,
                10000,
                60,
                false
        );

        // Create planner with initial strategy
        RoutePlanner planner = new RoutePlanner(
                new FastestRouteStrategy()
        );

        System.out.println(" Smart Delivery Engine ");
        System.out.println("Delivery Priority: " + request.priority());
        System.out.println();

        // First strategy: Fastest
        showResult(planner, routes, request);

        // Switch 1: Cheapest
        planner.setStrategy(new CheapestRouteStrategy());
        showResult(planner, routes, request);

        // Switch 2: Eco-Friendly
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        showResult(planner, routes, request);

        // Switch 3: Balanced
        planner.setStrategy(
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2)
        );
        showResult(planner, routes, request);

        // Automatic strategy selection
        System.out.println();
        System.out.println(" Automatic Strategy Selection ");

        StrategySelector selector = new StrategySelector();

        DeliveryRequest urgentRequest = new DeliveryRequest(
                DeliveryPriority.URGENT,
                10000,
                60,
                false
        );

        RouteStrategy selectedStrategy =
                selector.selectStrategy(urgentRequest);

        planner.setStrategy(selectedStrategy);

        System.out.println("Request Priority: " + urgentRequest.priority());
        showResult(planner, routes, urgentRequest);
    }

    private static void showResult(RoutePlanner planner,
                                   List<Route> routes,
                                   DeliveryRequest request) {

        Route selected = planner.plan(routes, request);

        System.out.println("Strategy: " + planner.getStrategyName());

        if (selected != null) {
            System.out.println("Selected: " + selected.id());
            System.out.println("Distance: " + selected.distanceKm() + " km");
            System.out.println("Time: " + (selected.estimatedMinutes()
                    + selected.trafficDelayMinutes()) + " min");
            System.out.println("Base Cost: " + selected.cost() + " KZT");
            System.out.println("CO2: " + selected.carbonEmission() + " kg");
        } else {
            System.out.println("No available route found.");
        }

        System.out.println("---");
    }
}
