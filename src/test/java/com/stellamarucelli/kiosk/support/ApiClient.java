package com.stellamarucelli.kiosk.support;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

// Parla direttamente con il backend .NET: serve a PREPARARE dati e a VERIFICARE
// ciò che l'app ha fatto (approccio ibrido UI + API).
public class ApiClient {

    // ---- CONFIGURAZIONE: locale di default, sovrascrivibile per il deploy ----
    // Priorità: -DCHIAVE=valore  >  variabile d'ambiente CHIAVE  >  valore predefinito
    private static final String BASE_URL = config("API_BASE_URL", "http://localhost:5231");
    private static final String USERNAME = config("API_USER", "mrossi");          // utente di test del seed locale
    private static final String PASSWORD = config("API_PASSWORD", "password123");

    private static String config(String chiave, String predefinito) {
        String valore = System.getProperty(chiave);
        if (valore == null || valore.isBlank()) {
            valore = System.getenv(chiave);
        }
        if (valore == null || valore.isBlank()) {
            return predefinito;
        }
        return valore;
    }

    // ---- CHIAMATE BASE ----

    // POST /api/Auth/login → token JWT
    public static String login() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of("username", USERNAME, "password", PASSWORD))
        .when()
                .post("/api/Auth/login")
        .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // GET /api/Ordine/{id}
    public static Response getOrdine(int id) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
        .when()
                .get("/api/Ordine/{id}", id)
        .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // GET /api/Prodotto/{id}
    public static Response getProdotto(int id) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
        .when()
                .get("/api/Prodotto/{id}", id)
        .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // GET /api/Prodotto → lista di tutti i prodotti, pronta da interrogare
    private static JsonPath prodotti() {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
        .when()
                .get("/api/Prodotto")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }

    // GET /api/Ordine → lista di tutti gli ordini
    private static JsonPath ordini() {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
        .when()
                .get("/api/Ordine")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }

    // ---- DOMANDE SUI PRODOTTI ----
    // Nota: i valori si passano con .param(...) e NON incollandoli nella stringa:
    // un nome con l'apostrofo (es. "D'Angelo") romperebbe la query.

    // "Cappuccino" → 5
    public static int idProdotto(String nome) {
        Integer id = prodotti().param("nome", nome).get("find { it.nome == nome }.id");
        if (id == null) {
            throw new IllegalArgumentException("Prodotto non trovato nel backend: " + nome);
        }
        return id;
    }

    // "Cappuccino" → la sua categoria (es. 1)
    public static int categoriaDi(String nomeProdotto) {
        Integer categoria = prodotti().param("nome", nomeProdotto).get("find { it.nome == nome }.categoriaId");
        if (categoria == null) {
            throw new IllegalArgumentException("Prodotto non trovato nel backend: " + nomeProdotto);
        }
        return categoria;
    }

    // Una categoria diversa da quella indicata, con almeno un prodotto attivo
    public static int altraCategoriaConProdotti(int categoriaDaEscludere) {
        Integer categoria = prodotti().param("esclusa", categoriaDaEscludere)
                .get("find { it.attivo && it.categoriaId != esclusa }.categoriaId");
        if (categoria == null) {
            throw new IllegalStateException("Nel backend non c'è un'altra categoria con prodotti attivi");
        }
        return categoria;
    }

    // Il nome del primo prodotto attivo di una categoria (= la prima card del menu filtrato)
    public static String primoProdottoDellaCategoria(int categoriaId) {
        String nome = prodotti().param("cat", categoriaId)
                .get("find { it.attivo && it.categoriaId == cat }.nome");
        if (nome == null) {
            throw new IllegalStateException("Nessun prodotto attivo nella categoria " + categoriaId);
        }
        return nome;
    }

    // Prezzo di listino di un prodotto
    public static double prezzo(int prodottoId) {
        return getProdotto(prodottoId).jsonPath().getDouble("prezzo");
    }

    // id → prezzo di tutti i prodotti attivi (quelli che il kiosk mostra)
    public static Map<Integer, Double> prezziProdottiAttivi() {
        List<Map<String, Object>> attivi = prodotti().getList("findAll { it.attivo }");
        Map<Integer, Double> prezzi = new HashMap<>();
        for (Map<String, Object> p : attivi) {
            prezzi.put(((Number) p.get("id")).intValue(), ((Number) p.get("prezzo")).doubleValue());
        }
        return prezzi;
    }

    // ---- DOMANDE SUGLI ORDINI ----

    // L'id più alto tra gli ordini esistenti (0 se non ce ne sono)
    public static int ultimoIdOrdine() {
        List<Integer> ids = ordini().getList("id", Integer.class);
        int max = 0;
        for (Integer id : ids) {
            if (id != null && id > max) {
                max = id;
            }
        }
        return max;
    }

    // Quanti ordini di quel cliente sono stati creati DOPO l'ordine con id "dopoId"
    public static int contaOrdiniDelCliente(String cliente, int dopoId) {
        Integer quanti = ordini().param("cliente", cliente).param("dopo", dopoId)
                .get("findAll { it.cliente == cliente && it.id > dopo }.size()");
        return quanti == null ? 0 : quanti;
    }

    // ---- MODIFICHE (usate solo dai test @bug, con ripristino nell'hook @After) ----

    // PUT /api/Prodotto/{id}: rilegge il prodotto, cambia solo "esaurito" e lo rimanda.
    // Restituisce lo status code, così lo step può dire chiaramente se il backend ha rifiutato.
    public static int impostaEsaurito(int prodottoId, boolean esaurito) {
        Map<String, Object> prodotto = new HashMap<>(getProdotto(prodottoId).jsonPath().getMap(""));
        prodotto.remove("categoria");          // oggetto annidato, non fa parte del DTO di modifica
        prodotto.put("esaurito", esaurito);

        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
                .contentType(ContentType.JSON)
                .body(prodotto)
        .when()
                .put("/api/Prodotto/{id}", prodottoId)
        .then()
                .extract()
                .statusCode();
    }
}
