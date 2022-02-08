package com.crawl.demo.repository;

import com.crawl.demo.domain.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryRouteRepository implements RouteRepository{

    private static List<Route> routes = new ArrayList<>();

    @Override
    public Route save(Route route) {
        routes.add(route);
        return route;
    }

    @Override
    public Optional<Route> findByTime(String time) {
        return routes.stream()
                .filter(route -> route.getTime().equals(time))
                .findAny();
    }

    @Override
    public Optional<Route> findByWalkTime(String walkTime) {
        return routes.stream()
                .filter(route -> route.getWalkTime().equals(walkTime))
                .findAny();
    }

    @Override
    public Optional<Route> findByPath(String path) {
        return routes.stream()
                .filter(route -> route.getPath().equals(path))
                .findAny();
    }

    @Override
    public List<Route> findAll() {
        return new ArrayList<>(routes);
    }

    public void clearRoutes() {
        routes.clear();
    }
}
