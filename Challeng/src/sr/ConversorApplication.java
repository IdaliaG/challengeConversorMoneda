package sr;

import modelo.*;
import service.ExchangeRateAPIService;
import service.ExchangeService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class ConversorApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExchangeService exchangeService = new ExchangeRateAPIService();
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static void main(String[] args) {
        scanner.useLocale(Locale.US);
        try {
            Moneda dolar = exchangeService.crearMoneda("USD", 1.0);

            int opcion;
            do {
                mostrarMenuPrincipal();
                opcion = obtenerOpcionValida(1, 5);

                switch(opcion) {
                    case 1:
                        convertirDolarAPesos(dolar);
                        break;
                    case 2:
                        convertirPesosADolar();
                        break;
                    case 3:
                        convertirEntreMonedas();
                        break;
                    case 4:
                        mostrarTasasActuales();
                        break;
                    case 5:
                        System.out.println("Saliendo del programa...");
                        break;
                }
            } while(opcion != 5);

        } catch (Exception e) {
            System.out.println("Error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== CONVERSOR DE MONEDAS ===");
        System.out.println("1. Dólar → Pesos (ARS/COP/BRL)");
        System.out.println("2. Pesos (ARS/COP/BRL) → Dólar");
        System.out.println("3. Conversión entre monedas");
        System.out.println("4. Mostrar tasas actuales");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void convertirDolarAPesos(Moneda dolar) throws IOException, InterruptedException {
        System.out.println("\n=== CONVERTIR DÓLAR A ===");
        System.out.println("1. Pesos Argentinos (ARS)");
        System.out.println("2. Pesos Colombianos (COP)");
        System.out.println("3. Reales Brasileños (BRL)");
        System.out.print("Seleccione moneda destino: ");

        int opcion = obtenerOpcionValida(1, 3);
        String codigo = switch(opcion) {
            case 1 -> "ARS";
            case 2 -> "COP";
            case 3 -> "BRL";
            default -> throw new IllegalStateException();
        };

        double cantidad = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            try {
                System.out.print("\nIngrese cantidad en dólares: ");
                cantidad = scanner.nextDouble();
                entradaValida = true;
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido (ej. 1500.50)");
                scanner.nextLine(); // limpiar buffer
            }
        }
        scanner.nextLine(); // limpiar buffer

        Moneda destino = exchangeService.crearMoneda(codigo, exchangeService.obtenerTasa(codigo));
        double resultado = dolar.convertirA(destino, cantidad);

        System.out.println("\nResultado: $" + df.format(cantidad) + " USD = " +
                df.format(resultado) + " " + destino.getCodigo());
    }

    private static void convertirPesosADolar() throws IOException, InterruptedException {
        System.out.println("\n=== CONVERTIR A DÓLAR ===");
        System.out.println("1. Pesos Argentinos (ARS) → USD");
        System.out.println("2. Pesos Colombianos (COP) → USD");
        System.out.println("3. Reales Brasileños (BRL) → USD");
        System.out.print("Seleccione moneda origen: ");

        int opcion = obtenerOpcionValida(1, 3);
        String codigo = switch(opcion) {
            case 1 -> "ARS";
            case 2 -> "COP";
            case 3 -> "BRL";
            default -> throw new IllegalStateException();
        };

        double cantidad = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            try {
                System.out.print("\nIngrese cantidad en " + codigo + ": ");
                cantidad = scanner.nextDouble();
                entradaValida = true;
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido (ej. 1500.50)");
                scanner.nextLine(); // Limpiar el buffer del scanner
            }
        }

        Moneda origen = exchangeService.crearMoneda(codigo, exchangeService.obtenerTasa(codigo));
        Moneda dolar = exchangeService.crearMoneda("USD", 1.0);
        double resultado = origen.convertirA(dolar, cantidad);

        System.out.println("\nResultado: " + df.format(cantidad) + " " + codigo + " = $" +
                df.format(resultado) + " USD");

        // Limpiar el buffer después de la conversión
        scanner.nextLine();
    }

    private static void convertirEntreMonedas() throws IOException, InterruptedException {
        System.out.println("\n=== CONVERSIÓN ENTRE MONEDAS ===");
        System.out.println("Monedas disponibles: ARS, COP, BRL, USD");

        System.out.print("Ingrese moneda origen (ej. ARS): ");
        String origenCodigo = scanner.next().toUpperCase();
        double tasaOrigen = exchangeService.obtenerTasa(origenCodigo);

        System.out.print("Ingrese moneda destino (ej. COP): ");
        String destinoCodigo = scanner.next().toUpperCase();
        double tasaDestino = exchangeService.obtenerTasa(destinoCodigo);

        System.out.print("Ingrese cantidad a convertir: ");
        double cantidad = scanner.nextDouble();

        Moneda origen = exchangeService.crearMoneda(origenCodigo, tasaOrigen);
        Moneda destino = exchangeService.crearMoneda(destinoCodigo, tasaDestino);
        double resultado = origen.convertirA(destino, cantidad);

        System.out.println("\nResultado: " + df.format(cantidad) + " " + origenCodigo + " = " +
                df.format(resultado) + " " + destinoCodigo);
    }

    private static void mostrarTasasActuales() throws IOException, InterruptedException {
        System.out.println("\n=== TASAS DE CAMBIO ACTUALES ===");
        System.out.println("1 USD = " + df.format(exchangeService.obtenerTasa("ARS")) + " ARS");
        System.out.println("1 USD = " + df.format(exchangeService.obtenerTasa("COP")) + " COP");
        System.out.println("1 USD = " + df.format(exchangeService.obtenerTasa("BRL")) + " BRL");
    }

    private static int obtenerOpcionValida(int min, int max) {
        while (true) {
            try {
                int opcion = scanner.nextInt();
                if (opcion >= min && opcion <= max) {
                    scanner.nextLine(); // Limpiar el buffer
                    return opcion;
                } else {
                    System.out.print("Opción fuera de rango. Ingrese un número entre " + min + " y " + max + ": ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Entrada inválida. Ingrese un número: ");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
}
