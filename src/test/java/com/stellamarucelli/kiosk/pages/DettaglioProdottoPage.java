package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Schermata DETTAGLIO PRODOTTO (dettaglio_prodotto_screen.dart)
public class DettaglioProdottoPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi (Key viste in DevTools / nel codice Dart) ----
    private final By btnAggiungi = AppiumBy.flutterKey("btn_aggiungi_al_carrello");
    private final By btnPiu      = AppiumBy.flutterKey("btn_piu_quantita_dettaglio");
    private final By btnMeno     = AppiumBy.flutterKey("btn_meno_quantita_dettaglio");
    private final By txtQuantita = AppiumBy.flutterKey("txt_quantita_dettaglio");
    private final By campoNote   = AppiumBy.flutterKey("campo_note_prodotto");
    // Il testo del pulsante cambia con la quantità ("Aggiungi al carrello · € 6.00") → cerco la parte fissa
    private final By testoPulsanteAggiungi = AppiumBy.flutterTextContaining("Aggiungi al carrello");
    // La freccia ← della barra in alto non ha Key: è il BackButton standard di Flutter
    private final By frecciaIndietro = AppiumBy.flutterType("BackButton");

    public DettaglioProdottoPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    // Il dettaglio è "aperto" quando compare il pulsante Aggiungi
    public boolean isVisibile() {
        return aspettaCheCompaia(btnAggiungi, 15);
    }

    // Il dettaglio NON si apre entro qualche secondo (es. prodotto esaurito)
    public boolean restaChiuso() {
        return !aspettaCheCompaia(btnAggiungi, 5);
    }

    public String leggiQuantita() {
        return driver.findElement(txtQuantita).getText();
    }

    public String leggiNota() {
        return driver.findElement(campoNote).getText();
    }

    // "Aggiungi al carrello · € 6.00" → 6.00
    public double prezzoNelPulsante() {
        String testo = driver.findElement(testoPulsanteAggiungi).getText();
        String dopoEuro = testo.substring(testo.indexOf('€') + 1).trim();
        return Double.parseDouble(dopoEuro);
    }

    // ---- COSA FAI ----

    public void aumentaQuantita() {
        driver.findElement(btnPiu).click();
    }

    // Se la quantità è 1 il pulsante "−" è disabilitato: il tocco non deve fare nulla
    public void diminuisciQuantita() {
        driver.findElement(btnMeno).click();
    }

    // Porta la quantità al valore richiesto partendo da quella mostrata (di solito 1)
    public void impostaQuantita(int quantita) {
        int attuale = Integer.parseInt(leggiQuantita());
        while (attuale < quantita) {
            aumentaQuantita();
            attuale++;
        }
        while (attuale > quantita && attuale > 1) {
            diminuisciQuantita();
            attuale--;
        }
    }

    // Prima svuota la casella, poi scrive (sendKeys da solo AGGIUNGEREBBE al testo esistente)
    public void scriviNota(String nota) {
        driver.findElement(campoNote).clear();
        driver.findElement(campoNote).sendKeys(nota);
    }

    public void aggiungiAlCarrello() {
        driver.findElement(btnAggiungi).click();
    }

    // Torna al menu senza aggiungere nulla
    public void tornaIndietro() {
        driver.findElement(frecciaIndietro).click();
    }

    // ---- AIUTO INTERNO ----

    private boolean aspettaCheCompaia(By elemento, int secondi) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(secondi))
                    .until(d -> !d.findElements(elemento).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
