package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.RiepilogoPage;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertTrue;

public class RiepilogoNegativoSteps {

    // Il testo dell'avviso, uguale a quello in riepilogo_screen.dart
    private static final String AVVISO_NOME = "Inserisci il nome del cliente";

    // Dato che il riepilogo è aperto
    @Dato("che il riepilogo è aperto")
    public void riepilogoAperto() {
        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        assertTrue(riepilogo.isVisibile(), "Il riepilogo non è aperto");
        // evita il falso positivo: aspetto che sparisca l'avviso dello scenario precedente
        riepilogo.aspettaCheAvvisoSparisca(AVVISO_NOME);
    }

    // Quando il cliente conferma senza scrivere il nome
    @Quando("il cliente conferma senza scrivere il nome")
    public void confermaSenzaNome() {
        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        riepilogo.confermaOrdine();
    }

    // Quando il cliente scrive solo spazi come nome e conferma
    @Quando("il cliente scrive solo spazi come nome e conferma")
    public void soloSpazi() {
        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        riepilogo.inserisciNome("   ");
        riepilogo.confermaOrdine();
    }

    // Allora vede l'avviso "Inserisci il nome del cliente"
    @Allora("vede l'avviso {string}")
    public void vedeAvviso(String testo) {
        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        assertTrue(riepilogo.avvisoVisibile(testo), "L'avviso \"" + testo + "\" non è comparso");
    }

    // E resta sul riepilogo
    @E("resta sul riepilogo")
    public void restaSulRiepilogo() {
        RiepilogoPage riepilogo = new RiepilogoPage(DriverManager.getDriver());
        assertTrue(riepilogo.isVisibile(), "Il cliente non è più sul riepilogo: l'ordine è partito?");
    }
}
