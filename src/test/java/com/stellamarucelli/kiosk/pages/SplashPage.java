package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;

public class SplashPage {

    private final FlutterAndroidDriver driver;

    // La Key
    private final By areaTocco = AppiumBy.flutterKey("splash_touch_area");

    public SplashPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    public boolean isVisibile() {
        // findElements (plurale): se non trova niente restituisce lista vuota, non eccezione
        return !driver.findElements(areaTocco).isEmpty();
    }

    public void toccaPerOrdinare() {
        driver.findElement(areaTocco).click();
    }
}