package delivery.estructuras;

import delivery.model.CambioEstado;


public class PilaCambiosEstado {

    public static final int MAX_ELEMENTOS = 10;

    private static class Nodo {
        CambioEstado dato;
        Nodo siguiente;

        Nodo(CambioEstado dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo cima;
    private int tamano;

    public PilaCambiosEstado() {
        this.cima = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamano() {
        return tamano;
    }

    public void apilar(CambioEstado cambio) {
        if (cambio == null) {
            return;
        }
        Nodo nuevo = new Nodo(cambio);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamano++;

        // Se conservan hasta 10 cambios; si excede, se descarta el más antiguo del fondo
        if (tamano > MAX_ELEMENTOS) {
            recortarFondo();
        }
    }

    private void recortarFondo() {
        if (cima == null) {
            return;
        }
        Nodo actual = cima;
        int contador = 1;
        while (actual.siguiente != null && contador < MAX_ELEMENTOS) {
            actual = actual.siguiente;
            contador++;
        }
        if (actual.siguiente != null) {
            actual.siguiente = null;
            tamano = MAX_ELEMENTOS;
        }
    }

    public CambioEstado desapilar() {
        if (estaVacia()) {
            return null;
        }
        CambioEstado retirado = cima.dato;
        cima = cima.siguiente;
        tamano--;
        return retirado;
    }

    public CambioEstado verCima() {
        return estaVacia() ? null : cima.dato;
    }

    public CambioEstado[] listar() {
        CambioEstado[] resultado = new CambioEstado[tamano];
        Nodo actual = cima;
        int i = 0;
        while (actual != null) {
            resultado[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return resultado;
    }

    public String describir() {
        if (estaVacia()) {
            return "(Pila vacía)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = cima;
        int idx = 1;
        while (actual != null) {
            sb.append(idx == 1 ? "[CIMA] " : "  [" + idx + "] ");
            sb.append(actual.dato.toString()).append("\n");
            actual = actual.siguiente;
            idx++;
        }
        return sb.toString();
    }
}
