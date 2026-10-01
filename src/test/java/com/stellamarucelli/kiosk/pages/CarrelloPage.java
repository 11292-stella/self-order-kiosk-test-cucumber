package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CarrelloPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (da carrello_screen.dart) ----
    private final By txtTotale       = AppiumBy.flutterKey("txt_carrello_totale");
    private final By btnVaiRiepilogo = AppiumBy.flutterKey("btn_vai_al_riepilogo");
    private final By txtVuoto        = AppiumBy.flutterKey("txt_carrello_vuoto");   // NUOVO

    public CarrelloPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    public boolean isVisibile() {
        return aspetta(txtTotale);
    }

    public boolean contieneProdotto(String nome) {
        return aspetta(AppiumBy.flutterText(nome));
    }

    // il carrello mostra "Il carrello è vuoto"
    public boolean isVuoto() {
        return aspetta(txtVuoto);
    }

    // legge la quantità di una riga, es. "2"
    public String leggiQuantita(int prodottoId) {
        return driver.findElement(AppiumBy.flutterKey("txt_quantita_" + prodottoId)).getText();
    }

    // legge la nota di una riga del carrello, es. "Ben caldo"
    public String leggiNota(int prodottoId) {
        return driver.findElement(AppiumBy.flutterKey("campo_note_" + prodottoId)).getText();
    }

    // legge il totale "€ 4.00" e lo trasforma nel numero 4.00
    public double leggiTotale() {
        String testo = driver.findElement(txtTotale).getText();      // "€ 4.00"
        return Double.parseDouble(testo.replace("€", "").trim());    // 4.00
    }

    // ---- COSA FAI ----

    public void vaiAlRiepilogo() {
        driver.findElement(btnVaiRiepilogo).click();
    }

    // preme "+" sulla riga di quel prodotto
    public void aumenta(int prodottoId) {
        driver.findElement(AppiumBy.flutterKey("btn_piu_" + prodottoId)).click();
    }

    // preme "−" sulla riga di quel prodotto
    public void diminuisci(int prodottoId) {
        driver.findElement(AppiumBy.flutterKey("btn_meno_" + prodottoId)).click();
    }

    // preme il cestino/"rimuovi" sulla riga di quel prodotto
    public void rimuovi(int prodottoId) {
        driver.findElement(AppiumBy.flutterKey("btn_rimuovi_" + prodottoId)).click();
    }

    // Tocca la freccia ← nella barra in alto (il BackButton di Flutter)
    public void tornaIndietro() {
        driver.findElement(AppiumBy.flutterType("BackButton")).click();
    }

    // ---- AIUTO INTERNO ----

    // Aspetta max 15s che l'elemento compaia: true se c'è, false se no
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