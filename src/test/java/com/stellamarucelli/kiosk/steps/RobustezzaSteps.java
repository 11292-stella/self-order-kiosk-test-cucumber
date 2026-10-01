package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.pages.RiepilogoPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.ContestoTest;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import org.openqa.selenium.WebDriverException;

import static org.testng.Assert.assertTrue;

// Scenario 14: doppio tocco su "Conferma ordine" e ritorno automatico al menu
public class RobustezzaSteps {

    // Quando il cliente tocca due volte di fila il pulsante di conferma
    @Quando("il cliente tocca due volte di fila il pulsante di conferma")
    public void doppioTocco() {
        // mi segno l'ultimo ordine esistente PRIMA del tocco: dopo conterò solo quelli nuovi
        ContestoTest.ultimoIdOrdinePrima = ApiClient.ultimoIdOrdine();

        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        riepilogo.confermaOrdine();
        try {
            riepilogo.confermaOrdine();   // secondo tocco immediato
        } catch (WebDriverException e) {
            // il pulsante è già sparito perché l'app è passata alla conferma: comportamento corretto
            System.out.println("Secondo tocco non eseguito: il pulsante non c'era più");
        }
    }

    // E dopo qualche secondo il kiosk torna da solo al menu
    @E("dopo qualche secondo il kiosk torna da solo al menu")
    public void ritornoAutomatico() {
        // il timer del kiosk è di 8 secondi; MenuPage.isVisibile() aspetta fino a 15
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Senza toccare nulla il kiosk non è tornato al menu");
    }
}
