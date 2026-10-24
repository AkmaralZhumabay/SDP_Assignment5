package com.smartdelivery.strategy;

import com.smartdelivery.model.Route;
import com.smartdelivery.model.DeliveryRequest;
import java.util.List;

public interface RouteStrategy {

    Route selectRoute(List<Route> routes, DeliveryRequest request);

    String getStrategyName();
}
