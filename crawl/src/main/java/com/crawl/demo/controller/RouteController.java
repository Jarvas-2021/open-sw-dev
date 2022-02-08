package com.crawl.demo.controller;

import com.crawl.demo.Input.InputRoad;
import com.crawl.demo.domain.Route;
import com.crawl.demo.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    //@GetMapping("android-api")
    @RequestMapping(value = "/android/api", method = RequestMethod.GET)
    @ResponseBody
    public List<Route> routeList() {
        routeService.crawling();
        return routeService.findRoutes();
    }

    @RequestMapping(value = "/android", method = RequestMethod.POST)
    @ResponseBody
    public String androidPage(HttpServletRequest req) {
        routeService.setClear();
        System.out.println("서버에서 안드로이드 접속 요청함");
        try {
            String start = req.getParameter("startAddressText");
            InputRoad.setStart(start);
            String end = req.getParameter("destinationAddressText");
            InputRoad.setEnd(end);
            System.out.println("안드로이드에서 받아온 출발지 : " + start);
            System.out.println("안드로이드에서 받아온 도착지 : " + end);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "android/api";
    }
}
