package delivery.model;

public class CambioEstado {
    private final int pedidoId;
    private final EstadoPedido estadoAnterior;
    private final EstadoPedido estadoNuevo;
    private final String estructuraIntervino;
    private final String detalle;

    public CambioEstado(int pedidoId, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo, String estructuraIntervino, String detalle) {
        this.pedidoId = pedidoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.estructuraIntervino = estructuraIntervino;
        this.detalle = detalle;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public EstadoPedido getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoPedido getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getEstructuraIntervino() {
        return estructuraIntervino;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        String prev = estadoAnterior == null ? "nuevo" : estadoAnterior.getEtiqueta();
        return "Pedido #" + pedidoId + ": " + prev + " -> " + estadoNuevo.getEtiqueta()
                + " | Estructura: " + estructuraIntervino
                + (detalle != null && !detalle.isEmpty() ? " (" + detalle + ")" : "");
    }
}
