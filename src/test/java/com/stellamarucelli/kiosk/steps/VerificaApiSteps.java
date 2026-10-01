package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.ContestoTest;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import io.restassured.path.json.JsonPath;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class VerificaApiSteps {

    JsonPath ordine;   // il JSON dell'ordine letto dal backend

    // Dato che il kiosk ha confermato un ordine
    @Dato("che il kiosk ha confermato un ordine")
    public void ordineConfermato() {
        assertTrue(ContestoTest.numeroOrdine > 0,
                "Nessun ordine nel contesto: vanno eseguiti prima gli scenari 01-04");
    }

    // Quando leggo l'ordine dal backend
    @Quando("leggo l'ordine dal backend")
    public void leggoOrdine() {
        ordine = ApiClient.getOrdine(ContestoTest.numeroOrdine).jsonPath();
        System.out.println("Ordine dal backend: " + ordine.prettify());
    }

    // Allora il cliente è quello inserito nel kiosk
    @Allora("il cliente è quello inserito nel kiosk")
    public void clienteCorretto() {
        assertEquals(ordine.getString("cliente"), ContestoTest.nomeCliente,
                "Il nome del cliente salvato non corrisponde");
    }

    // E l'ordine contiene il prodotto con la quantità scelta
    @E("l'ordine contiene il prodotto con la quantità scelta")
    public void prodottoEQuantita() {
        assertEquals(ordine.getList("righe").size(), 1, "L'ordine dovrebbe avere una sola riga");

        int prodottoId = ordine.getInt("righe[0].prodottoId");
        String nomeProdotto = ApiClient.getProdotto(prodottoId).jsonPath().getString("nome");

        assertEquals(nomeProdotto, ContestoTest.prodotto, "Il prodotto nell'ordine non è quello scelto");
        assertEquals(ordine.getInt("righe[0].quantita"), ContestoTest.quantita, "La quantità salvata non è corretta");
    }

    // E il totale è uguale a prezzo per quantità
    @E("il totale è uguale a prezzo per quantità")
    public void totaleCorretto() {
        int prodottoId = ordine.getInt("righe[0].prodottoId");
        double prezzo = ApiClient.getProdotto(prodottoId).jsonPath().getDouble("prezzo");

        double atteso = prezzo * ContestoTest.quantita;
        double totale = ordine.getDouble("totale");

        assertEquals(totale, atteso, 0.001, "Il totale non è prezzo × quantità");
    }

    // Allora la nota del cliente è salvata nell'ordine   (@bug: oggi fallisce)
    @Allora("la nota del cliente è salvata nell'ordine")
    public void notaSalvata() {
        String notaNelBackend = ordine.getString("righe[0].note");
        assertEquals(notaNelBackend, ContestoTest.nota,
                "BUG: la nota scritta nel kiosk non arriva al backend");
    }
}
