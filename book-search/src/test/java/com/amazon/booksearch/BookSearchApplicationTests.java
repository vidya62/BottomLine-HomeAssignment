package com.amazon.booksearch;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookSearchApplicationTests {

    WebDriver driver = null;
    public static String bookName = "the Lost World by Arthur Conan Doyle";

    void setup() {
        WebDriverManager.chromedriver().browserVersion("101.0.4951.64").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
    }

    @Test
    void contextLoads() throws InterruptedException {
        this.setup();
        driver.get("https://www.amazon.com/");
        if (driver.findElements(By.id("nav-bb-logo")).size() > 0) {
            driver.findElement(By.id("nav-bb-logo")).click();
        }
        Thread.sleep(2000);
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(bookName);
        driver.findElement(By.id("nav-search-submit-button")).click();
        String totalSearchResults = driver.findElement(By.className("rush-component")).getText().split(" ")[2];
        System.out.println("Total search results for the book is:" + totalSearchResults);

        driver.findElement(By.xpath("//div[@id='filters']//ul[@aria-labelledby='p_n_feature_nine_browse-bin-title']//a")).click();
        String totalEnglishSearchResults = driver.findElement(By.className("rush-component")).getText().split(" ")[2];
        System.out.println("Total search results filtering with English Language " + totalEnglishSearchResults);
        ArrayList<String> bookNames = new ArrayList();
        Boolean isNextButtonDisabled = true;
        Integer counter = 1;
        while (isNextButtonDisabled) {
            waitForElementToBeVisible(By.className("s-pagination-next"), 10000);
            isNextButtonDisabled = driver.findElements(By.xpath("//span[@class='s-pagination-item s-pagination-next s-pagination-disabled ']")).size() != 1;
            List<WebElement> searchResultOnPage = driver.findElements(By.className("s-title-instructions-style"));
            searchResultOnPage.forEach(book -> {
                if (!book.getText().contains("Sponsored")) {
                    bookNames.add(book.findElement(By.tagName("h2")).getText());
                }
            });
            System.out.println("Searching page " + counter++);
            driver.findElement(By.className("s-pagination-next")).click();
        }
        List<String> maxBookName1 = bookNames.stream()
                .filter(bookName -> bookName.toLowerCase().contains(this.bookName.toLowerCase()))
                .distinct().collect(Collectors.toList());
        Optional maxBookName = maxBookName1.stream()
                .max(Comparator.comparingInt(String::length));
        System.out.println("The max book name is '" + maxBookName.get() + "' and its length is " + maxBookName.get().toString().length());
        assertTrue(maxBookName.get().toString().length() < 71, "Longest book name is more than 70 characters");
        driver.quit();
    }

    public void waitForElementToBeVisible(By locator, int timeout) {
        new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
