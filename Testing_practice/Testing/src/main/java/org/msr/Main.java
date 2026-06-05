package org.msr;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://cognizant.tekstac.com/my/");
        Thread.sleep(6000);
        WebElement sel = driver.findElement(By.className("font-16"));
        driver.getTitle();






    }
}