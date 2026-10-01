package com.stellamarucelli.kiosk.support;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;

public class DriverManager {

    // Un solo driver condiviso da hooks, steps e pages
    private static FlutterAndroidDriver driver;

    public static FlutterAndroidDriver getDriver() {
        return driver;
    }

    public static void avvia() {
        // Percorso dell'APK dentro il progetto
        String apk = Paths.get(System.getProperty("user.dir"),
                "src", "test", "resources", "self_order_kiosk-test.apk").toString();

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Pixel_4")
                .setAutomationName("FlutterIntegration")
                .setApp(apk)
                .setEnforceAppInstall(true)                // reinstalla sempre l'APK di test
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(60));

        // Tempo massimo per far partire il server Flutter dentro l'app
        options.setCapability("appium:flutterServerLaunchTimeout", 60000);
        // Attesa implicita: ogni find aspetta fino a 10s che il widget compaia
        options.setCapability("appium:flutterElementWaitTimeout", 10000);

        try {
            driver = new FlutterAndroidDriver(
                    URI.create("http://127.0.0.1:4723").toURL(), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL di Appium non valido", e);
        }
    }

    public static void chiudi() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}