package delivery.grafo;

public class Arista {
    private final int origen;
    private final int destino;
    private final int minutos;

    public Arista(int origen, int destino, int minutos) {
        this.origen = origen;
        this.destino = destino;
        this.minutos = minutos;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public int getMinutos() {
        return minutos;
    }
    
}
