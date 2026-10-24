package com.smartdelivery.model;

import java.util.List;

public record RouteDecision(
        Route selectedRoute,
        String strategyName,
        String explanation,
        List<String> rejectedRoutes
) {}
