package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CarrelloPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi ----
    // Il totale esiste solo nella schermata carrello → ci dice che siamo davvero lì
    private final By txtTotale = AppiumBy.flutterKey("txt_carrello_totale");

    public CarrelloPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    // Il carrello è aperto quando compare il totale
    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(txtTotale).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Il prodotto è nel carrello se compare il suo nome
    public boolean contieneProdotto(String nome) {
        By prodotto = AppiumBy.flutterText(nome);   // costruito qui: dipende dal nome
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(prodotto).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}