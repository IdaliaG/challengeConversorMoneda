package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

public class ExchangeRateAPIService implements ExchangeService {
    private static final String API_KEY = System.getenv("5ec9a5bc32f5218ee34faefe");
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + "5ec9a5bc32f5218ee34faefe" + "/latest/USD";
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public Map<String, Double> obtenerTasasDeCambio() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error en la API: Código " + response.statusCode());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        if (!json.get("result").getAsString().equals("success")) {
            throw new IOException("Error en la respuesta de la API: " + json.get("result"));
        }

        JsonObject rates = json.getAsJsonObject("conversion_rates");
        Map<String, Double> tasas = new HashMap<>();
        rates.entrySet().forEach(entry -> {
            tasas.put(entry.getKey(), entry.getValue().getAsDouble());
        });

        return tasas;
    }

    @Override
    public Moneda crearMoneda(String codigo, double tasa) {
        return switch (codigo.toUpperCase()) {
            case "USD" -> new Dolar(tasa);
            case "ARS" -> new PesoArgentino(tasa);
            case "BRL" -> new RealBrasileno(tasa);
            case "COP" -> new PesoColombiano(tasa);
            default -> throw new IllegalArgumentException("Moneda no soportada: " + codigo);
        };
    }

    @Override
    public double obtenerTasa(String codigoMoneda) throws IOException, InterruptedException {
        Map<String, Double> tasas = obtenerTasasDeCambio();
        if (!tasas.containsKey(codigoMoneda.toUpperCase())) {
            throw new IllegalArgumentException("Moneda no encontrada: " + codigoMoneda);
        }
        return tasas.get(codigoMoneda.toUpperCase());
    }
}