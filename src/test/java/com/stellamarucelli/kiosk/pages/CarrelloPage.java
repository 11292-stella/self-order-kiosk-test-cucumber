package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Schermata CARRELLO (carrello_screen.dart)
public class CarrelloPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi ----
    private final By txtTotale       = AppiumBy.flutterKey("txt_carrello_totale");
    private final By btnVaiRiepilogo = AppiumBy.flutterKey("btn_vai_al_riepilogo");
    private final By txtVuoto        = AppiumBy.flutterKey("txt_carrello_vuoto");
    private final By frecciaIndietro = AppiumBy.flutterType("BackButton");

    // Le righe hanno Key con l'id del prodotto: cart_item_5, txt_quantita_5, btn_piu_5, ...
    private By riga(int prodottoId)         { return AppiumBy.flutterKey("cart_item_" + prodottoId); }
    private By quantita(int prodottoId)     { return AppiumBy.flutterKey("txt_quantita_" + prodottoId); }
    private By nota(int prodottoId)         { return AppiumBy.flutterKey("campo_note_" + prodottoId); }
    private By btnPiu(int prodottoId)       { return AppiumBy.flutterKey("btn_piu_" + prodottoId); }
    private By btnMeno(int prodottoId)      { return AppiumBy.flutterKey("btn_meno_" + prodottoId); }
    private By btnRimuovi(int prodottoId)   { return AppiumBy.flutterKey("btn_rimuovi_" + prodottoId); }

    public CarrelloPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    public boolean isVisibile() {
        return aspettaCheCompaia(txtTotale);
    }

    public boolean contieneProdotto(String nome) {
        return aspettaCheCompaia(AppiumBy.flutterText(nome));
    }

    // La riga di quel prodotto è sparita (dopo "rimuovi" o "−" a quantità 1)
    public boolean rigaSparita(int prodottoId) {
        return aspettaCheSparisca(riga(prodottoId));
    }

    // Controllo immediato, senza attese: c'è una riga per questo prodotto?
    public boolean haRiga(int prodottoId) {
        return !driver.findElements(riga(prodottoId)).isEmpty();
    }

    public boolean isVuoto() {
        return aspettaCheCompaia(txtVuoto);
    }

    public String leggiQuantita(int prodottoId) {
        return driver.findElement(quantita(prodottoId)).getText();
    }

    public String leggiNota(int prodottoId) {
        return driver.findElement(nota(prodottoId)).getText();
    }

    // "€ 4.00" → 4.00
    public double leggiTotale() {
        String testo = driver.findElement(txtTotale).getText();
        return Double.parseDouble(testo.replace("€", "").trim());
    }

    // ---- COSA FAI ----

    public void vaiAlRiepilogo() {
        driver.findElement(btnVaiRiepilogo).click();
    }

    public void aumenta(int prodottoId) {
        driver.findElement(btnPiu(prodottoId)).click();
    }

    public void diminuisci(int prodottoId) {
        driver.findElement(btnMeno(prodottoId)).click();
    }

    public void rimuovi(int prodottoId) {
        driver.findElement(btnRimuovi(prodottoId)).click();
    }

    // Cambia la nota direttamente dal carrello (prima svuota, poi scrive)
    public void scriviNota(int prodottoId, String testo) {
        driver.findElement(nota(prodottoId)).clear();
        driver.findElement(nota(prodottoId)).sendKeys(testo);
    }

    // Freccia ← dell'app (NON il tasto indietro di Android: con Appium apre il selettore tastiera)
    public void tornaIndietro() {
        driver.findElement(frecciaIndietro).click();
    }

    // ---- AIUTO INTERNO ----

    private boolean aspettaCheCompaia(By elemento) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(elemento).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

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
