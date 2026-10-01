package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Schermata SPLASH (splash_screen.dart)
public class SplashPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi ----
    private final By areaTocco = AppiumBy.flutterKey("splash_touch_area");

    public SplashPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    // Aspetta max 15s: su un emulatore lento l'app può metterci un po' a disegnare la splash
    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(areaTocco).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---- COSA FAI ----

    public void toccaPerOrdinare() {
        driver.findElement(areaTocco).click();
    }
}
