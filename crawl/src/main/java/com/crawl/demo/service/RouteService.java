package com.crawl.demo.service;

import com.crawl.demo.Input.InputRoad;
import com.crawl.demo.domain.Route;
import com.crawl.demo.repository.RouteRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class RouteService {
    private final RouteRepository routeRepository;

    private static WebDriver driver;

    private static List<WebElement> road;
    private static List<WebElement> time;
    private static List<WebElement> walkTime;

    private static WebElement clear;
    private static WebElement startingPoint;
    private static WebElement destination;
    private static WebElement element;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver/chromedriver(3).exe";
    public static String TEST_URL = "https://map.kakao.com/?map_type=TYPE_MAP&target=transportation&rt=%2C%2C523953%2C1084098&rt1=%20&rt2=%20&rtIds=%2C&rtTypes=%2C#";


    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> findRoutes() {
        return routeRepository.findAll();
    }

    private Route createRoute(int index) {
        Route route = new Route();

        route.setTime(time.get(index).getText());
        route.setWalkTime(walkTime.get(index).getText());
        route.setPath(road.get(index).getText());

        return route;
    }

    public void crawling() {
        //System Property SetUp
        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Driver SetUp
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=300,600");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);

        try {
            driver.get(TEST_URL);
            Thread.sleep(500);

            clear = driver.findElement(By.xpath("//*[@id=\"info.route.searchBox.clearVia\"]"));
            clear.sendKeys(Keys.ENTER);

            startingPoint = driver.findElement(By.id("info.route.waypointSuggest.input0"));
            startingPoint.clear();
            startingPoint.sendKeys(InputRoad.getStart());
            startingPoint.sendKeys(Keys.ENTER);

            Thread.sleep(300);

            destination = driver.findElement(By.id("info.route.waypointSuggest.input1"));
            destination.clear();
            destination.sendKeys(InputRoad.getEnd());
            destination.sendKeys(Keys.ENTER);
            Thread.sleep(300);

            element = driver.findElement(By.cssSelector("#transittab"));
            element.sendKeys(Keys.ENTER);
            System.out.println(element);
            Thread.sleep(300);

        }catch (Exception e) {
            e.printStackTrace();
        }

        road = driver.findElements(By.className("SummaryDetail"));
        time = driver.findElements(By.className("time"));
        walkTime = driver.findElements(By.className("walkTime"));

        for (int i = 0; i < walkTime.size(); i++) {
            routeRepository.save(createRoute(i));
        }

    }
}
