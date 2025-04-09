package service;

import modelo.Moneda;
import java.io.IOException;
import java.util.Map;

public interface ExchangeService {
    Map<String, Double> obtenerTasasDeCambio() throws IOException, InterruptedException;
    Moneda crearMoneda(String codigo, double tasa);
    double obtenerTasa(String codigoMoneda) throws IOException, InterruptedException;
}