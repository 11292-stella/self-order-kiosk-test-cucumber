package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DettaglioProdottoPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (Key viste in DevTools) ----
    private final By btnAggiungi = AppiumBy.flutterKey("btn_aggiungi_al_carrello");
    private final By btnPiu      = AppiumBy.flutterKey("btn_piu_quantita_dettaglio");
    private final By txtQuantita = AppiumBy.flutterKey("txt_quantita_dettaglio");
    private final By campoNote   = AppiumBy.flutterKey("campo_note_prodotto");   // NUOVO

    public DettaglioProdottoPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(btnAggiungi).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String leggiQuantita() {
        return driver.findElement(txtQuantita).getText();
    }

    // NUOVO: legge cosa c'è scritto nella casella note
    public String leggiNota() {
        return driver.findElement(campoNote).getText();
    }

    // ---- COSA FAI ----

    public void aumentaQuantita() {
        driver.findElement(btnPiu).click();
    }

    // scrive una nota nella casella
    public void scriviNota(String nota) {
        driver.findElement(campoNote).sendKeys(nota);
    }

    public void aggiungiAlCarrello() {
        driver.findElement(btnAggiungi).click();
    }
}