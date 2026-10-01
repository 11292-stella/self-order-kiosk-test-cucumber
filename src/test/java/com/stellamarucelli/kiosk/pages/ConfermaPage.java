package com.stellamarucelli.kiosk.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Schermata CONFERMA FINALE (conferma_finale_screen.dart)
// Attenzione: dopo 8 secondi torna DA SOLA al menu (timer del kiosk)
public class ConfermaPage {

    private final FlutterAndroidDriver driver;

    // ---- DOVE sono gli elementi ----
    private final By btnTornaMenu = AppiumBy.flutterKey("btn_torna_al_menu");
    // "Ordine #12 confermato" non ha Key → cerco la parte fissa del testo
    private final By txtConferma = AppiumBy.flutterTextContaining("confermato");

    public ConfermaPage(FlutterAndroidDriver driver) {
        this.driver = driver;
    }

    // ---- COSA VEDI ----

    public boolean isVisibile() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !d.findElements(btnTornaMenu).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String leggiMessaggio() {
        return driver.findElement(txtConferma).getText();
    }

    // "Ordine #12 confermato" → 12
    public int numeroOrdine() {
        String soloCifre = leggiMessaggio().replaceAll("\\D", "");
        return Integer.parseInt(soloCifre);
    }

    // ---- COSA FAI ----

    public void tornaAlMenu() {
        driver.findElement(btnTornaMenu).click();
    }

    // Come tornaAlMenu, ma se il timer di 8s ha già riportato l'app al menu non fallisce
    public void tornaAlMenuSeAncoraQui() {
        if (driver.findElements(btnTornaMenu).isEmpty()) {
            return;   // già tornati al menu da soli
        }
        try {
            driver.findElement(btnTornaMenu).click();
        } catch (WebDriverException e) {
            // il timer è scattato proprio adesso: va bene lo stesso
        }
    }
}
