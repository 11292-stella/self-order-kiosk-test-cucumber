package com.stellamarucelli.kiosk.support;

// Dati condivisi tra scenari diversi: Cucumber ricrea gli Steps a ogni scenario,
// quindi i campi normali si perdono. Questi sono static → durano tutta l'esecuzione.
public class ContestoTest {

    public static String prodotto;       // scritto in 02_menu      (es. "Cappuccino")
    public static int quantita;          // scritto in 03_carrello  (es. 2)
    public static String nota;           // scritto in 03_carrello  (es. "Poca schiuma")
    public static String nomeCliente;    // scritto da "inserisce il suo nome" / "inserisce il nome ..."
    public static int numeroOrdine;      // scritto da "vede la conferma dell'ordine" (es. 27)

    public static int ultimoIdOrdinePrima;        // id dell'ultimo ordine PRIMA di un'azione (doppio tocco)
    public static Integer prodottoDaRipristinare; // id del prodotto modificato da un test @bug, da rimettere com'era
}
