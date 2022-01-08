package com.crawl.demo;

import com.crawl.demo.Input.InputRoad;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        /*InputRoad.setStart("인천대학교 공과대학");
        InputRoad.setEnd("인천대학교 정보기술대학");
        TestCrawling crawlling = new TestCrawling();
        crawlling.activate();*/
        SpringApplication.run(DemoApplication.class, args);
    }
}
