package com.crawl.demo;

import com.crawl.demo.domain.Route;

import java.util.ArrayList;
import java.util.List;

public class Routes {
    private List<Route> routes = new ArrayList<>();

    public Routes() {}

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void print() {
        for (int i = 0; i < routes.size(); i++) {
            System.out.println(routes.get(i).getTime());
            System.out.println(routes.get(i).getWalkTime());
            System.out.println(routes.get(i).getPath());
            System.out.println();
        };
    }
}
