package com.stellamarucelli.kiosk.hooks;

import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

public class Hooks {

    // Una volta sola, prima di TUTTI gli scenari
    @BeforeAll
    public static void apriApp() {
        DriverManager.avvia();
    }

    // Dopo OGNI scenario: solo screenshot se è fallito, l'app resta aperta
    @After
    public void screenshotSeFallito(Scenario scenario) {
        if (scenario.isFailed() && DriverManager.getDriver() != null) {
            byte[] foto = DriverManager.getDriver().getScreenshotAs(OutputType.BYTES);
            scenario.attach(foto, "image/png", "screenshot errore");
        }
    }

    // Una volta sola, dopo TUTTI gli scenari
    @AfterAll
    public static void chiudiApp() {
        DriverManager.chiudi();
    }
}