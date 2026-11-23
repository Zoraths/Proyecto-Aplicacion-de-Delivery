package delivery.estructuras;

import delivery.model.Pedido;

public class MonticuloUrgentes {

    private Pedido[] arreglo;
    private int tamano;
    private int contadorLlegada;

    public MonticuloUrgentes() {
        this.arreglo = new Pedido[16];
        this.tamano = 0;
        this.contadorLlegada = 0;
    }

    public boolean estaVacio() {
        return tamano == 0;
    }

    public int getTamano() {
        return tamano;
    }

    public void insertar(Pedido pedido) {
        if (pedido == null) {
            return;
        }
        if (tamano == arreglo.length) {
            ampliarCapacidad();
        }
        // Se guarda el número de llegada al encolar para desempatar prioridades iguales
        contadorLlegada++;
        pedido.setOrdenLlegada(contadorLlegada);
        arreglo[tamano] = pedido;
        flotar(tamano);
        tamano++;
    }

    public Pedido extraerMaximo() {
        if (estaVacio()) {
            return null;
        }
        Pedido raiz = arreglo[0];
        tamano--;
        arreglo[0] = arreglo[tamano];
        arreglo[tamano] = null;
        if (tamano > 0) {
            hundir(0);
        }
        return raiz;
    }

    public Pedido verMaximo() {
        return estaVacio() ? null : arreglo[0];
    }

    /**
      
     Se compara primero la prioridad de mayor a menor (5 es más urgente que 1).
     
     */
    private boolean tieneMayorUrgencia(Pedido a, Pedido b) {
        if (a.getPrioridad() != b.getPrioridad()) {
            return a.getPrioridad() > b.getPrioridad();
        }
        return a.getOrdenLlegada() < b.getOrdenLlegada();
    }

    // Sube el elemento en 'indice' mientras supere a su padre según el criterio del montículo
    private void flotar(int indice) {
        int actual = indice;
        while (actual > 0) {
            int padre = (actual - 1) / 2;
            if (tieneMayorUrgencia(arreglo[actual], arreglo[padre])) {
                intercambiar(actual, padre);
                actual = padre;
            } else {
                break;
            }
        }
    }

    // Baja el elemento en 'indice' intercambiándolo con el hijo de mayor urgencia hasta restaurar el orden
    private void hundir(int indice) {
        int actual = indice;
        while (true) {
            int izq = 2 * actual + 1;
            int der = 2 * actual + 2;
            int mejor = actual;

            if (izq < tamano && tieneMayorUrgencia(arreglo[izq], arreglo[mejor])) {
                mejor = izq;
            }
            if (der < tamano && tieneMayorUrgencia(arreglo[der], arreglo[mejor])) {
                mejor = der;
            }
            if (mejor != actual) {
                intercambiar(actual, mejor);
                actual = mejor;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        Pedido temp = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = temp;
    }

    private void ampliarCapacidad() {
        Pedido[] nuevo = new Pedido[arreglo.length * 2];
        for (int i = 0; i < tamano; i++) {
            nuevo[i] = arreglo[i];
        }
        arreglo = nuevo;
    }

    public Pedido[] listarEnArreglo() {
        Pedido[] copia = new Pedido[tamano];
        for (int i = 0; i < tamano; i++) {
            copia[i] = arreglo[i];
        }
        return copia;
    }

    public String describir() {
        if (estaVacio()) {
            return "(Montículo vacío)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamano; i++) {
            Pedido p = arreglo[i];
            sb.append("[").append(i).append("] #").append(p.getId())
              .append(" (P:").append(p.getPrioridad())
              .append(", llegada:").append(p.getOrdenLlegada())
              .append(", ").append(p.getTipo()).append(")");
            if (i < tamano - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
}
