package delivery.model;

public class Restaurante {
    private final String nombre;
    private final int calificacion;

    public Restaurante(String nombre, int calificacion) {
        this.nombre = nombre;
        this.calificacion = calificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCalificacion() {
        return calificacion;
    }

    @Override
    public String toString() {
        return calificacion + " " + nombre;
    }
}
