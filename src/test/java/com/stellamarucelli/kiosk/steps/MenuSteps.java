package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertTrue;

public class MenuSteps {

    MenuPage menu;
    DettaglioProdottoPage dettaglio;

    @Dato("che il menu è visibile")
    public void menuVisibile() {
        menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Il menu non è visibile");
    }

    @Quando("il cliente apre il prodotto {string}")
    public void apreProdotto(String nome) {
        menu.apriProdotto(nome);                 // ← nome = "Cappuccino" dal feature
    }

    @Allora("vede il dettaglio del prodotto")
    public void vedeDettaglio() {
        dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.isVisibile(), "Il dettaglio del prodotto non si è aperto");
    }
}