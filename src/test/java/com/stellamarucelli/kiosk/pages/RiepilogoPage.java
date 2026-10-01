package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RiepilogoPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (da riepilogo_screen.dart) ----
    private final By campoNome   = AppiumBy.flutterKey("campo_nome_cliente");
    private final By btnConferma = AppiumBy.flutterKey("btn_conferma_ordine");

    public RiepilogoPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    // Il riepilogo è aperto quando compare la casella del nome
    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(campoNome).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Legge il nome scritto nella casella
    public String leggiNome() {
        return driver.findElement(campoNome).getText();
    }

    // ---- COSA FAI ----

    // Scrive il nome del cliente
    public void inserisciNome(String nome) {
        driver.findElement(campoNome).sendKeys(nome);
    }

    // Preme "Conferma ordine" → l'ordine viene inviato al backend
    public void confermaOrdine() {
        driver.findElement(btnConferma).click();
    }
}
