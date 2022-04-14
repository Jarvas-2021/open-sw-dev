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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RouteService {
    private final RouteRepository routeRepository;

    private static WebDriver driver;

    private static List<WebElement> path;
    private static List<WebElement> time;
    private static List<WebElement> transType = null;
    private static List<WebElement> elements;

    private static WebElement clear;
    private static WebElement startingPoint;
    private static WebElement destination;
    private static WebElement busImage;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver/chromedriver (2).exe";
    public static String TEST_URL = "https://map.kakao.com/?map_type=TYPE_MAP&target=transportation&rt=%2C%2C523953%2C1084098&rt1=%20&rt2=%20&rtIds=%2C&rtTypes=%2C#";

    private String price;

    private String walkTime;
    private String transfer;
    private String distance;

    private String interTime;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> findRoutes() {
        return routeRepository.findAll();
    }

    public void setClear() {
        routeRepository.clearRoutes();
    }

    private Route createRoute(int index) {
        Route route = new Route();

        route.setTime(time.get(index).getText());
        route.setPath(path.get(index).getText());
        route.setPrice(price);

        route.setWalkTime(walkTime);
        route.setTransfer(transfer);
        route.setDistance(distance);

        route.setId(0);
        if ((transType != null) && !transType.isEmpty()) {
            route.setId(1);
            route.setTransType(transType.get(index).getText());
        }
        route.setInterTime(interTime);

        System.out.println(price);

        price = null;
        walkTime = null;
        transfer = null;
        distance = null;
        interTime = null;


        return route;
    }

    private void splitDowntownElements(int index) {
        StringTokenizer stringTokenizer = new StringTokenizer(elements.get(index).getText(), "도보환승요금원", false);

        if (stringTokenizer.hasMoreTokens()) {
            walkTime = stringTokenizer.nextToken();
            transfer = stringTokenizer.nextToken();
            if (stringTokenizer.countTokens() == 3) {
                price = stringTokenizer.nextToken() + "원" + stringTokenizer.nextToken() + "원";
                distance = stringTokenizer.nextToken();
            } else {
                price = stringTokenizer.nextToken("요금원음");
                if (price.contains("없")) {
                    price += "음";
                } else {
                    price += "원";
                }
                distance = stringTokenizer.nextToken();
            }

        }
    }

    private void splitOutOfTownElements(int index) {
        StringTokenizer stringTokenizer = new StringTokenizer(elements.get(index).getText(), "요금", false);

        if (stringTokenizer.hasMoreTokens()) {
            interTime = stringTokenizer.nextToken();
            price = stringTokenizer.nextToken();
        }
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
        //options.addArguments("headless");
        options.addArguments("--window-size=300,600");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);

        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            driver.get(TEST_URL);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"info.route.searchBox.clearVia\"]")));

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

            busImage = driver.findElement(By.cssSelector("#transittab"));
            busImage.sendKeys(Keys.ENTER);
            System.out.println(busImage);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("SummaryDetail")));

        }catch (Exception e) {
            e.printStackTrace();
        }

        Boolean isCity = driver.findElements(By.className("walkTime")).size() > 0;

        path = driver.findElements(By.className("SummaryDetail"));
        time = driver.findElements(By.className("time"));

        if (isCity) {
            elements = driver.findElements(By.className("walkTime"));
        } else {
            transType = driver.findElements(By.className("trans_type"));
            elements = driver.findElements(By.className("inter_time"));
        }

        for (int i = 0; i < elements.size(); i++) {
            if (isCity) {
                splitDowntownElements(i);
            } else {
                splitOutOfTownElements(i);
            }

            if (price != null) {
                routeRepository.save(createRoute(i));
            }

        }

    }
}
