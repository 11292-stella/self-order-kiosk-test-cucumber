package com.stellamarucelli.kiosk.support;

// Dati condivisi tra scenari diversi: Cucumber ricrea gli Steps a ogni scenario,
// quindi i campi normali si perdono. Questi sono static → durano tutta l'esecuzione.
public class ContestoTest {

    public static String prodotto;       // scritto in 02_menu      (es. "Cappuccino")
    public static int quantita;          // scritto in 03_carrello  (es. 2)
    public static String nota;           // scritto in 03_carrello  (es. "Poca schiuma")
    public static String nomeCliente;    // scritto in 04_ordine    (es. "Giulia")
    public static int numeroOrdine;      // scritto in 04_ordine    (es. 27)
}