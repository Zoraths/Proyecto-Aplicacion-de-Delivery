package delivery.model;

public class Pedido {
    private final int id;
    private final String hora;
    private final String tipo;
    private final boolean urgente;
    private final boolean comidaCaliente;
    private final int prioridad;
    private final String zonaDestino;
    private final String clienteDireccion;
    private EstadoPedido estado;
    private int ordenLlegada;
    private String repartidorAsignado;
    private String rutaElegida;

    public Pedido(int id, String hora, String tipo, int prioridad, String zonaDestino, String clienteDireccion) {
        this.id = id;
        this.hora = hora;
        this.tipo = tipo;
        String tipoLower = tipo == null ? "" : tipo.toLowerCase();
        this.urgente = tipoLower.contains("urgente");
        this.comidaCaliente = tipoLower.contains("caliente");
        this.prioridad = prioridad;
        this.zonaDestino = zonaDestino;
        this.clienteDireccion = clienteDireccion;
        this.estado = EstadoPedido.PENDIENTE;
        this.ordenLlegada = 0;
        this.repartidorAsignado = "";
        this.rutaElegida = "";
    }

    public int getId() {
        return id;
    }

    public String getHora() {
        return hora;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public boolean isComidaCaliente() {
        return comidaCaliente;
    }

    public boolean esUrgenteOComidaCaliente() {
        return urgente || comidaCaliente;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public String getZonaDestino() {
        return zonaDestino;
    }

    public String getClienteDireccion() {
        return clienteDireccion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public int getOrdenLlegada() {
        return ordenLlegada;
    }

    public void setOrdenLlegada(int ordenLlegada) {
        this.ordenLlegada = ordenLlegada;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public void setRepartidorAsignado(String repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
    }

    public String getRutaElegida() {
        return rutaElegida;
    }

    public void setRutaElegida(String rutaElegida) {
        this.rutaElegida = rutaElegida;
    }

    @Override
    public String toString() {
        return "#" + id + " [" + hora + ", " + tipo + ", P:" + prioridad + ", " + clienteDireccion + ", " + estado + "]";
    }
}
