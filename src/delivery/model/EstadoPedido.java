package delivery.model;

public enum EstadoPedido {
    PENDIENTE("pendiente"),
    EN_PREPARACION("en preparación"),
    EN_CAMINO("en camino"),
    ENTREGADO("entregado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
