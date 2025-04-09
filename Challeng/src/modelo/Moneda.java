package modelo;


public interface Moneda {
    String getCodigo();
    String getNombre();
    double convertirA(Moneda destino, double cantidad);
}
