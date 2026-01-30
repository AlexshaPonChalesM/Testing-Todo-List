package com.alexsha.taskapp;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.*;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Change this path to your local file URL for index.html
    private final String baseUrl = "file:///C:/Users/Admin/todo-list-project/app/index.html";

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();  // Maximized window instead of fullscreen
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Helper: type text slowly (simulate user typing)
    private void slowType(WebElement element, String text, long delayMillis) throws InterruptedException {
        element.clear();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            Thread.sleep(delayMillis);
        }
    }

    @Test
    public void testFullSignupLoginAndTaskFlow() throws InterruptedException {
        driver.get(baseUrl);
        Thread.sleep(1000); // Wait after loading page

        // 1. Try login with non-existing user -> alert "User does not exist."
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement authButton = driver.findElement(By.tagName("button"));

        slowType(usernameInput, "Alexsha", 150);
        slowType(passwordInput, "Password@123", 150);
        Thread.sleep(5000);

        authButton.click();
        Thread.sleep(5000);

        // Handle alert - User does not exist
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().toLowerCase().contains("user does not exist"));
        Thread.sleep(5000);
        alert.accept();
        Thread.sleep(5000);

        // 2. Switch to Sign Up form
        WebElement toggleText = driver.findElement(By.cssSelector(".toggle span"));
        toggleText.click(); // click "Sign Up"
        Thread.sleep(5000);

        // 3. Fill sign up details
        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        authButton = driver.findElement(By.tagName("button"));

        slowType(usernameInput, "Alexsha", 150);
        slowType(passwordInput, "Password@123", 150);
        Thread.sleep(5000);

        authButton.click();
        Thread.sleep(5000);

        // Handle alert - Account created successfully! Please login.
        alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().toLowerCase().contains("account created successfully"));
        Thread.sleep(5000);
        alert.accept();
        Thread.sleep(5000);

        // 4. After alert, it should switch to login form automatically
        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        authButton = driver.findElement(By.tagName("button"));

        // 5. Login with new user
        slowType(usernameInput, "Alexsha", 150);
        slowType(passwordInput, "Password@123", 150);
        Thread.sleep(5000);
        authButton.click();
        Thread.sleep(5000);

        // 6. After login, redirected to tasks.html
        wait.until(ExpectedConditions.urlContains("tasks.html"));
        assertTrue(driver.getCurrentUrl().contains("tasks.html"));
        Thread.sleep(5000);

        // 7. Add new list
        WebElement newListInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newList")));
        WebElement addListBtn = driver.findElement(By.xpath("//button[text()='Add List']"));

        slowType(newListInput, "My Tasks", 100);
        Thread.sleep(5000);
        addListBtn.click();
        Thread.sleep(5000);

        // 8. Select the created list
        WebElement listItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='lists']/div[text()='My Tasks']")));
        listItem.click();
        Thread.sleep(5000);

        // 9. Add new task
        WebElement taskTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskTitle")));
        WebElement taskDescription = driver.findElement(By.id("taskDescription"));
        WebElement taskPriority = driver.findElement(By.id("taskPriority"));
        WebElement taskState = driver.findElement(By.id("taskState"));
        WebElement addTaskBtn = driver.findElement(By.xpath("//button[text()='Add Task']"));

        slowType(taskTitle, "Finish Selenium Test", 100);
        slowType(taskDescription, "Write and run full Selenium test for app", 100);
        Thread.sleep(5000);

        taskPriority.click();
        WebElement highOption = taskPriority.findElement(By.xpath(".//option[@value='High']"));
        highOption.click();
        Thread.sleep(500);

        taskState.click();
        WebElement pendingOption = taskState.findElement(By.xpath(".//option[@value='Pending']"));
        pendingOption.click();
        Thread.sleep(500);

        addTaskBtn.click();
        Thread.sleep(5000);

        // 10. Edit the task: click Edit button on task
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='tasks']/div[contains(@class,'task')]//button[text()='Edit']")));
        editBtn.click();
        Thread.sleep(5000);

        // Change task title and description
        taskTitle = driver.findElement(By.id("taskTitle"));
        taskDescription = driver.findElement(By.id("taskDescription"));

        slowType(taskTitle, "Finish Selenium Test Edited", 100);
        slowType(taskDescription, "Edited task description", 100);
        Thread.sleep(5000);

        // Change priority and state
        taskPriority = driver.findElement(By.id("taskPriority"));
        taskState = driver.findElement(By.id("taskState"));

        taskPriority.click();
        WebElement mediumOption = taskPriority.findElement(By.xpath(".//option[@value='Medium']"));
        mediumOption.click();
        Thread.sleep(500);

        taskState.click();
        WebElement inProgressOption = taskState.findElement(By.xpath(".//option[@value='In Progress']"));
        inProgressOption.click();
        Thread.sleep(500);

        addTaskBtn = driver.findElement(By.xpath("//button[text()='Add Task']"));
        addTaskBtn.click();
        Thread.sleep(1500);

        // 11. Delete the task
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='tasks']/div[contains(@class,'task')]//button[text()='Delete']")));
        deleteBtn.click();
        Thread.sleep(1500);

        // 12. Logout
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".logout-btn")));
        logoutBtn.click();
        Thread.sleep(1500);

        // Check redirected to login page
        wait.until(ExpectedConditions.urlContains("index.html"));
        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
