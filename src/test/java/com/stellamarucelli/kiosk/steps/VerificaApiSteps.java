package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.ContestoTest;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import io.restassured.path.json.JsonPath;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
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

    // ---- Usati dagli scenari 10, 13, 14 e dai bug noti ----

    // E il backend ha registrato 3 "Cappuccino" al prezzo di listino
    @E("il backend ha registrato {int} {string} al prezzo di listino")
    public void backendHaRegistrato(int quantita, String nomeProdotto) {
        JsonPath o = ApiClient.getOrdine(ContestoTest.numeroOrdine).jsonPath();
        int idAtteso = ApiClient.idProdotto(nomeProdotto);
        double listino = ApiClient.prezzo(idAtteso);

        assertEquals(o.getString("cliente"), ContestoTest.nomeCliente, "Cliente salvato non corretto");
        assertEquals(o.getList("righe").size(), 1, "L'ordine dovrebbe avere una sola riga");
        assertEquals(o.getInt("righe[0].prodottoId"), idAtteso, "Prodotto salvato non corretto");
        assertEquals(o.getInt("righe[0].quantita"), quantita, "Quantità salvata non corretta");
        assertEquals(o.getDouble("righe[0].prezzoUnitario"), listino, 0.001,
                "Il prezzo unitario salvato non è quello di listino");
        assertEquals(o.getDouble("totale"), listino * quantita, 0.001,
                "Il totale salvato non è prezzo × quantità");
    }

    // E nel backend il cliente dell'ordine è "Anna-Maria D'Angelo"
    @E("nel backend il cliente dell'ordine è {string}")
    public void clienteNelBackend(String nomeAtteso) {
        String salvato = ApiClient.getOrdine(ContestoTest.numeroOrdine).jsonPath().getString("cliente");
        assertEquals(salvato, nomeAtteso, "Il nome cliente è stato modificato durante il salvataggio");
    }

    // E nel backend esiste un solo ordine per quel cliente
    @E("nel backend esiste un solo ordine per quel cliente")
    public void unSoloOrdine() {
        int quanti = ApiClient.contaOrdiniDelCliente(ContestoTest.nomeCliente, ContestoTest.ultimoIdOrdinePrima);
        assertEquals(quanti, 1, "Il doppio tocco ha creato " + quanti + " ordini invece di 1");
    }

    // Allora la risposta non contiene il campo "costoProduzione"   (@bug)
    @Allora("la risposta non contiene il campo {string}")
    public void rispostaSenzaCampo(String campo) {
        assertFalse(ordine.prettify().contains("\"" + campo + "\""),
                "BUG: la risposta dell'ordine espone il campo interno \"" + campo + "\" al kiosk dei clienti");
    }
}
