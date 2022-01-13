package com.crawl.demo;

import com.crawl.demo.repository.MemoryRouteRepository;
import com.crawl.demo.repository.RouteRepository;
import com.crawl.demo.service.RouteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public RouteService routeService() {
        return new RouteService(routeRepository());
    }

    @Bean
    public RouteRepository routeRepository() {
        return new MemoryRouteRepository();
    }
}
