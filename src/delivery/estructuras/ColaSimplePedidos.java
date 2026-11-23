package delivery.estructuras;

import delivery.model.Pedido;


public class ColaSimplePedidos {

    private static class Nodo {
        Pedido dato;
        Nodo siguiente;

        Nodo(Pedido dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo frente;
    private Nodo fin;
    private int tamano;

    public ColaSimplePedidos() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }

    public void encolar(Pedido pedido) {
        if (pedido == null) {
            return;
        }
        Nodo nuevo = new Nodo(pedido);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        tamano++;
    }

    public Pedido desencolar() {
        if (estaVacia()) {
            return null;
        }
        Pedido atendido = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return atendido;
    }

    public Pedido verFrente() {
        return estaVacia() ? null : frente.dato;
    }

    public Pedido[] listar() {
        Pedido[] resultado = new Pedido[tamano];
        Nodo actual = frente;
        int i = 0;
        while (actual != null) {
            resultado[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return resultado;
    }

    public String describir() {
        if (estaVacia()) {
            return "(Cola vacía)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = frente;
        while (actual != null) {
            sb.append("#").append(actual.dato.getId())
              .append(" [").append(actual.dato.getHora())
              .append(", P:").append(actual.dato.getPrioridad())
              .append(", ").append(actual.dato.getEstado()).append("]");
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return sb.toString();
    }
}
