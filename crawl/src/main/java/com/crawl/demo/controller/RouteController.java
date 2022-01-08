package com.crawl.demo.controller;

import com.crawl.demo.Crawlling;
import com.crawl.demo.Input.InputRoad;
import com.crawl.demo.TestCrawling;
import com.crawl.demo.domain.Route;
import com.crawl.demo.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("android-api")
    @ResponseBody
    public List<Route> androidPage() {
        InputRoad.setStart("인천대입구역");
        InputRoad.setEnd("인천대학교 정보기술대학");
        routeService.crawling();
        /*
        for(int i = 0; i < crawlling.getWalkTime().size(); i++) {
            route.addRoute(crawlling.getRoad().get(i).getText());
            route.addTime(crawlling.getTime().get(i).getText());
            route.addWalkTime(crawlling.getWalkTime().get(i).getText());
        }*/
        return routeService.findRoutes();
    }
    /*
    @RequestMapping(value = "/android", method = RequestMethod.POST)
    public String androidPage(HttpServletRequest req, Model model) {
        System.out.println("서버에서 안드로이드 접속 요청함");
        try {
            String start = req.getParameter("출발지");
            InputRoad.setStart(start);
            String end = req.getParameter("도착지");
            InputRoad.setEnd(end);
            System.out.println("안드로이드에서 받아온 출발지 : " + start);
            System.out.println("안드로이드에서 받아온 도착지" + end);
            model.addAllAttributes("android", start);
            return "android";
        }catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }*/
}
