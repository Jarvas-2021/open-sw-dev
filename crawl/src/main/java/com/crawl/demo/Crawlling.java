package com.crawl.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class Crawlling {

    private static WebDriver driver;
    private static WebElement element;
    private static List<WebElement> road;
    private static List<WebElement> time;
    private static List<WebElement> walkTime;

    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver/chromedriver(2).exe";
    public static String TEST_URL = "https://map.kakao.com/?map_type=TYPE_MAP&target=transportation&rt=%2C%2C523953%2C1084098&rt1=%EC%8A%A4%ED%83%80%EB%B2%85%EC%8A%A4&rt2=%EC%B9%B4%EC%B9%B4%EC%98%A4%ED%8C%90%EA%B5%90%EC%98%A4%ED%94%BC%EC%8A%A4&rtIds=%2C&rtTypes=%2C#";

    public Crawlling() {
        //System Property SetUp
        try {
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Driver SetUp
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
    }

    public void activate() {
        try {
            driver.get(TEST_URL);
            Thread.sleep(1000);

            element = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[2]/div[2]/div/a[2]"));
            element.sendKeys(Keys.ENTER);
            System.out.println(element);
            Thread.sleep(1000);

        }catch (Exception e) {
            e.printStackTrace();
        }

        road = driver.findElements(By.className("SummaryDetail"));
        time = driver.findElements(By.className("time"));
        walkTime = driver.findElements(By.className("walkTime"));


        for (int i = 0; i < walkTime.size(); i++) {
            System.out.println(time.get(i).getText());
            System.out.println(walkTime.get(i).getText());
            System.out.println(road.get(i).getText());
            System.out.println();
        }

    }

    public static List<WebElement> getRoad() {
        return road;
    }

    public static List<WebElement> getTime() {
        return time;
    }

    public static List<WebElement> getWalkTime() {
        return walkTime;
    }
}
