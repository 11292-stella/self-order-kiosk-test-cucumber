package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MenuPage {

    private final FlutterAndroidDriver driver;

    private final By categoriaTutte = AppiumBy.flutterKey("cat_tutte");

    private final By btnCarrello = AppiumBy.flutterKey("btn_apri_carrello");

    public MenuPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    public boolean isVisibile() {
        try {
            // Riprova ogni mezzo secondo, per massimo 15 secondi, finché cat_tutte compare
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(categoriaTutte).isEmpty());
            return true;
        } catch (TimeoutException e) {
            // Dopo 15 secondi non è ancora comparso
            return false;
        }
    }

    public void apriProdotto(String nome) {
        By prodotto = AppiumBy.flutterText(nome);
        driver.findElement(prodotto).click();
    }

    public void apriCarrello(){

        driver.findElement(btnCarrello).click();

    }
}