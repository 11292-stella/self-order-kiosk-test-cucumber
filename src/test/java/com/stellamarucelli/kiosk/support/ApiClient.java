package com.stellamarucelli.kiosk.support;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiClient {

    // ---- CONFIGURAZIONE: locale di default, sovrascrivibile per il deploy ----
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

    // NUOVO (privato): GET /api/Prodotto → la lista di tutti i prodotti, pronta da interrogare
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

    // ---- DOMANDE SUI PRODOTTI (usano tutte la lista) ----

    // "Cappuccino" → 5
    public static int idProdotto(String nome) {
        Integer id = prodotti().get("find { it.nome == '" + nome + "' }.id");
        if (id == null) {
            throw new IllegalArgumentException("Prodotto non trovato nel backend: " + nome);
        }
        return id;
    }

    //"Cappuccino" → la sua categoria (es. 1)
    public static int categoriaDi(String nomeProdotto) {
        Integer categoria = prodotti().get("find { it.nome == '" + nomeProdotto + "' }.categoriaId");
        if (categoria == null) {
            throw new IllegalArgumentException("Prodotto non trovato nel backend: " + nomeProdotto);
        }
        return categoria;
    }

    // una categoria diversa da quella indicata, che abbia almeno un prodotto attivo
    public static int altraCategoriaConProdotti(int categoriaDaEscludere) {
        Integer categoria = prodotti().get(
                "find { it.attivo && it.categoriaId != " + categoriaDaEscludere + " }.categoriaId");
        if (categoria == null) {
            throw new IllegalStateException("Nel backend non c'è un'altra categoria con prodotti attivi");
        }
        return categoria;
    }

    // il nome del primo prodotto attivo di una categoria (= la prima card del menu filtrato)
    public static String primoProdottoDellaCategoria(int categoriaId) {
        String nome = prodotti().get("find { it.attivo && it.categoriaId == " + categoriaId + " }.nome");
        if (nome == null) {
            throw new IllegalStateException("Nessun prodotto attivo nella categoria " + categoriaId);
        }
        return nome;
    }
}