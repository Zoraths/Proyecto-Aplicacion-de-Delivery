package delivery.estructuras;

import delivery.model.Repartidor;


public class ListaRepartidores {

    private static class Nodo {
        Repartidor dato;
        Nodo anterior;
        Nodo siguiente;

        Nodo(Repartidor dato) {
            this.dato = dato;
            this.anterior = null;
            this.siguiente = null;
        }
    }

    private Nodo cabeza;
    private Nodo cola;
    private int tamano;

    public ListaRepartidores() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getTamano() {
        return tamano;
    }

    public void insertarAlFinal(Repartidor repartidor) {
        if (repartidor == null) {
            return;
        }
        Nodo nuevo = new Nodo(repartidor);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        tamano++;
    }

    public Repartidor buscarPrimerLibre() {
        Nodo actual = cabeza;
        while (actual != null) {
            if (!actual.dato.isOcupado()) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean hayLibre() {
        return buscarPrimerLibre() != null;
    }

    public Repartidor buscar(String codigoONombre) {
        if (codigoONombre == null) {
            return null;
        }
        String buscado = codigoONombre.trim();
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.dato.getCodigo().equalsIgnoreCase(buscado)
                    || actual.dato.getNombre().equalsIgnoreCase(buscado)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean marcarOcupado(String codigoONombre, boolean ocupado) {
        Repartidor r = buscar(codigoONombre);
        if (r == null) {
            return false;
        }
        r.setOcupado(ocupado);
        return true;
    }

    public Repartidor[] recorrerAdelante() {
        Repartidor[] arr = new Repartidor[tamano];
        Nodo actual = cabeza;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return arr;
    }

    public Repartidor[] recorrerAtras() {
        Repartidor[] arr = new Repartidor[tamano];
        Nodo actual = cola;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.dato;
            actual = actual.anterior;
        }
        return arr;
    }

    public String textoAdelante() {
        if (estaVacia()) {
            return "(Sin repartidores)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato.getNombre());
            if (actual.siguiente != null) {
                sb.append(" <-> ");
            }
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    public String textoAtras() {
        if (estaVacia()) {
            return "(Sin repartidores)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = cola;
        while (actual != null) {
            sb.append(actual.dato.getNombre());
            if (actual.anterior != null) {
                sb.append(" <-> ");
            }
            actual = actual.anterior;
        }
        return sb.toString();
    }

    public String textoLibres() {
        StringBuilder sb = new StringBuilder();
        Nodo actual = cabeza;
        while (actual != null) {
            if (!actual.dato.isOcupado()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(actual.dato.getNombre());
            }
            actual = actual.siguiente;
        }
        return sb.length() == 0 ? "(Ninguno libre)" : sb.toString();
    }
}
