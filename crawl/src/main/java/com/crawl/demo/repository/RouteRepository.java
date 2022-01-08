package com.crawl.demo.repository;

import com.crawl.demo.domain.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findByTime(String time);
    Optional<Route> findByWalkTime(String walkTime);
    Optional<Route> findByPath(String path);
    List<Route> findAll();
}
