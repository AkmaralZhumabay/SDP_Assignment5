package com.smartdelivery.selector;

import com.smartdelivery.model.DeliveryPriority;
import com.smartdelivery.model.DeliveryRequest;
import com.smartdelivery.strategy.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector() {
        strategies.put(DeliveryPriority.URGENT,
                new FastestRouteStrategy());

        strategies.put(DeliveryPriority.ECONOMY,
                new CheapestRouteStrategy());

        strategies.put(DeliveryPriority.ECO,
                new EcoFriendlyRouteStrategy());

        strategies.put(DeliveryPriority.STANDARD,
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
    }

    public RouteStrategy selectStrategy(DeliveryRequest request) {
        Objects.requireNonNull(request, "Delivery request cannot be null");

        // Urgent deliveries always prioritize speed
        if (request.priority() == DeliveryPriority.URGENT) {
            return strategies.get(DeliveryPriority.URGENT);
        }

        // Eco preference applies to non-urgent deliveries
        if (request.ecoPreferred()) {
            return strategies.get(DeliveryPriority.ECO);
        }

        RouteStrategy strategy = strategies.get(request.priority());

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy found for priority: " + request.priority());
        }

        return strategy;
    }

    public void registerStrategy(DeliveryPriority priority,
                                 RouteStrategy strategy) {
        strategies.put(
                Objects.requireNonNull(priority),
                Objects.requireNonNull(strategy)
        );
    }
}
