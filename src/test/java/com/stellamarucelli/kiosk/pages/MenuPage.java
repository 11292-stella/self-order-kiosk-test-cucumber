package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MenuPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (da menu_screen.dart) ----
    private final By categoriaTutte = AppiumBy.flutterKey("cat_tutte");
    private final By btnCarrello    = AppiumBy.flutterKey("btn_apri_carrello");

    public MenuPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    public boolean isVisibile() {
        return aspettaCheCompaia(categoriaTutte);
    }

    // la card di quel prodotto è nel menu?
    public boolean vedeProdotto(String nome) {
        return aspettaCheCompaia(AppiumBy.flutterText(nome));
    }

    // la card di quel prodotto è sparita dal menu?
    public boolean nonVedeProdotto(String nome) {
        return aspettaCheSparisca(AppiumBy.flutterText(nome));
    }

    // ---- COSA FAI ----

    public void apriProdotto(String nome) {
        driver.findElement(AppiumBy.flutterText(nome)).click();
    }

    public void apriCarrello() {
        driver.findElement(btnCarrello).click();
    }

    // tocca il chip di una categoria (Key "cat_<id>")
    public void selezionaCategoria(int categoriaId) {
        driver.findElement(AppiumBy.flutterKey("cat_" + categoriaId)).click();
    }

    // tocca il chip "Tutte"
    public void selezionaTutte() {
        driver.findElement(categoriaTutte).click();
    }

    // ---- AIUTO INTERNO ----

    // true se l'elemento compare entro 15s
    private boolean aspettaCheCompaia(By elemento) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(elemento).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // true se l'elemento sparisce entro 10s
    private boolean aspettaCheSparisca(By elemento) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.findElements(elemento).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}