package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ConfermaPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (da conferma_finale_screen.dart) ----
    private final By btnTornaMenu = AppiumBy.flutterKey("btn_torna_al_menu");

    // Il testo "Ordine #12 confermato" NON ha una Key → lo cerco per parte del testo
    private final By txtConferma = AppiumBy.flutterTextContaining("confermato");

    public ConfermaPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    // La conferma è visibile quando compare il pulsante "Torna al menu"
    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(btnTornaMenu).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Legge il messaggio intero, es. "Ordine #12 confermato"
    public String leggiMessaggio() {
        return driver.findElement(txtConferma).getText();
    }

    // Estrae solo il numero dell'ordine, es. 12
    public int numeroOrdine() {
        String soloCifre = leggiMessaggio().replaceAll("\\D", "");   // tiene solo le cifre
        return Integer.parseInt(soloCifre);
    }

    // ---- COSA FAI ----

    public void tornaAlMenu() {
        driver.findElement(btnTornaMenu).click();
    }
}