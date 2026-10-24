package com.smartdelivery;

import com.smartdelivery.exception.NoSuitableRouteException;
import com.smartdelivery.model.*;
import com.smartdelivery.planner.RoutePlanner;
import com.smartdelivery.selector.StrategySelector;
import com.smartdelivery.strategy.*;
import com.smartdelivery.util.RouteUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoutePlannerTest {

    private List<Route> routes;
    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        Location warehouse = new Location("Warehouse", 51.1605, 71.4704);
        Location customer = new Location("Customer", 51.1280, 71.4300);

        routes = List.of(
                new Route("A", warehouse, customer,
                        12, 25, 3000, 2.5, 10, true),
                new Route("B", warehouse, customer,
                        15, 20, 4500, 3.2, 5, true),
                new Route("C", warehouse, customer,
                        10, 35, 2000, 1.8, 8, true),
                new Route("D", warehouse, customer,
                        18, 30, 2800, 4.0, 12, true),
                new Route("E", warehouse, customer,
                        9, 40, 1800, 1.5, 6, true),
                new Route("F", warehouse, customer,
                        14, 22, 5000, 2.8, 3, true),
                new Route("G", warehouse, customer,
                        11, 28, 3200, 2.0, 7, true),
                new Route("H", warehouse, customer,
                        8, 18, 5500, 1.2, 15, false)
        );

        request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 60, false);
    }

    // 1. Fastest Strategy
    @Test
    void fastestSelectsRouteB() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertEquals("B", planner.plan(routes, request).id());
    }

    // 2. Cheapest Strategy
    @Test
    void cheapestSelectsRouteE() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 3. Eco-Friendly Strategy
    @Test
    void ecoSelectsRouteE() {
        RoutePlanner planner = new RoutePlanner(new EcoFriendlyRouteStrategy());

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 4. Balanced Strategy
    @Test
    void balancedSelectsRouteE() {
        RoutePlanner planner = new RoutePlanner(
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 5. Runtime switching
    @Test
    void runtimeSwitchingChangesResult() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertEquals("B", planner.plan(routes, request).id());

        planner.setStrategy(new CheapestRouteStrategy());

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 6. Automatic strategy selection
    @Test
    void selectorChoosesFastestForUrgent() {
        StrategySelector selector = new StrategySelector();

        DeliveryRequest urgent = new DeliveryRequest(
                DeliveryPriority.URGENT, 10000, 60, false);

        assertInstanceOf(FastestRouteStrategy.class,
                selector.selectStrategy(urgent));
    }

    // 7. Automatic economy selection
    @Test
    void selectorChoosesCheapestForEconomy() {
        StrategySelector selector = new StrategySelector();

        DeliveryRequest economy = new DeliveryRequest(
                DeliveryPriority.ECONOMY, 10000, 60, false);

        assertInstanceOf(CheapestRouteStrategy.class,
                selector.selectStrategy(economy));
    }

    // 8. Budget restriction
    @Test
    void budgetRestrictionChangesFastestRoute() {
        DeliveryRequest limited = new DeliveryRequest(
                DeliveryPriority.STANDARD, 3000, 60, false);

        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertEquals("C", planner.plan(routes, limited).id());
    }

    // 9. Deadline restriction
    @Test
    void deadlineRestrictionRejectsSlowRoutes() {
        DeliveryRequest limited = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 30, false);

        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());

        assertEquals("B", planner.plan(routes, limited).id());
    }

    // 10. Unavailable route
    @Test
    void unavailableRouteIsNotSelected() {
        RoutePlanner planner = new RoutePlanner(
                new DistanceFirstRouteStrategy());

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 11. No suitable route
    @Test
    void noSuitableRouteThrowsException() {
        DeliveryRequest limited = new DeliveryRequest(
                DeliveryPriority.STANDARD, 100, 60, false);

        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertThrows(NoSuitableRouteException.class,
                () -> planner.plan(routes, limited));
    }

    // 12. Empty route list
    @Test
    void emptyRouteListThrowsException() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertThrows(IllegalArgumentException.class,
                () -> planner.plan(List.of(), request));
    }

    // 13. Invalid Balanced weights
    @Test
    void invalidBalancedWeightsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(0.5, 0.4, 0.3, 0.2));
    }

    // 14. Negative weights
    @Test
    void negativeBalancedWeightsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(-0.1, 0.5, 0.4, 0.2));
    }

    // 15. Fifth strategy
    @Test
    void distanceFirstSelectsRouteE() {
        RoutePlanner planner = new RoutePlanner(
                new DistanceFirstRouteStrategy());

        assertEquals("E", planner.plan(routes, request).id());
    }

    // 16. Comparison of two strategies
    @Test
    void fastestAndCheapestProduceDifferentResults() {
        RoutePlanner fastest = new RoutePlanner(new FastestRouteStrategy());
        RoutePlanner cheapest = new RoutePlanner(new CheapestRouteStrategy());

        assertNotEquals(
                fastest.plan(routes, request).id(),
                cheapest.plan(routes, request).id()
        );
    }

    // 17. Boundary case: exact budget
    @Test
    void routeAtExactBudgetIsAllowed() {
        DeliveryRequest exactBudget = new DeliveryRequest(
                DeliveryPriority.STANDARD, 2250, 60, false);

        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());

        assertEquals("E", planner.plan(routes, exactBudget).id());
    }

    // 18. Route explanation
    @Test
    void explanationContainsRejectionReason() {
        DeliveryRequest limited = new DeliveryRequest(
                DeliveryPriority.STANDARD, 3000, 45, false);

        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        RouteDecision decision =
                planner.planWithExplanation(routes, limited);

        assertEquals("C", decision.selectedRoute().id());
        assertTrue(decision.rejectedRoutes()
                .contains("E: Delivery deadline exceeded"));
    }

    @Test
    void negativeDistanceIsRejected() {
        Location location = new Location("Test", 51.0, 71.0);

        assertThrows(IllegalArgumentException.class, () ->
                new Route("Invalid", location, location,
                        -5, 20, 1000, 1.0, 0, true));
    }

    @Test
    void negativeBudgetIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeliveryRequest(
                        DeliveryPriority.STANDARD, -100, 60, false));
    }

    @Test
    void missingPriorityIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeliveryRequest(null, 5000, 60, false));
    }
}
