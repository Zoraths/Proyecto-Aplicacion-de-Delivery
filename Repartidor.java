package delivery.model;

public class Repartidor {
    private final String codigo;
    private final String nombre;
    private boolean ocupado;

    public Repartidor(String codigo, String nombre, boolean ocupado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ocupado = ocupado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    @Override
    public String toString() {
        return codigo + " " + nombre + " (" + (ocupado ? "ocupado" : "libre") + ")";
    }
}
