package modelo;

public abstract class MonedaBase implements Moneda {
    protected final String codigo;
    protected final String nombre;
    protected final double tasaBase;

    public MonedaBase(String codigo, String nombre, double tasaBase) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tasaBase = tasaBase;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public double convertirA(Moneda destino, double cantidad) {
        if (!(destino instanceof MonedaBase)) {
            throw new IllegalArgumentException("El destino debe ser una MonedaBase");
        }
        MonedaBase destinoBase = (MonedaBase) destino;
        return (cantidad / this.tasaBase) * destinoBase.tasaBase;
    }
}