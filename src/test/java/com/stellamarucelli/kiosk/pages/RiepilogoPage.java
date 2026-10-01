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

    public boolean isVisibile() {
        return aspetta(campoNome);
    }

    public String leggiNome() {
        return driver.findElement(campoNome).getText();
    }

    // NUOVO: l'avviso in basso (SnackBar) con quel testo è comparso?
    public boolean avvisoVisibile(String testo) {
        return aspetta(AppiumBy.flutterText(testo));
    }

    // NUOVO: aspetta che l'avviso sparisca (la SnackBar resta qualche secondo)
    public void aspettaCheAvvisoSparisca(String testo) {
        By avviso = AppiumBy.flutterText(testo);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.findElements(avviso).isEmpty());
        } catch (TimeoutException e) {
            // se dopo 10s è ancora lì, andiamo avanti: lo segnalerà l'assert successivo
        }
    }

    // ---- COSA FAI ----

    // MODIFICATO: prima svuota il campo, poi scrive
    public void inserisciNome(String nome) {
        driver.findElement(campoNome).clear();
        driver.findElement(campoNome).sendKeys(nome);
    }

    public void confermaOrdine() {
        driver.findElement(btnConferma).click();
    }

    // ---- AIUTO INTERNO ----

    private boolean aspetta(By elemento) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(elemento).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
