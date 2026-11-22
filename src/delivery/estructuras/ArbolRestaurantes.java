package delivery.estructuras;

import delivery.model.Restaurante;

public class ArbolRestaurantes {

    private static class Nodo {
        Restaurante dato;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(Restaurante dato) {
            this.dato = dato;
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    public static class ResultadoBusqueda {
        public final Restaurante encontrado;
        public final String camino;
        public final int nivel;
        public final int grado;

        public ResultadoBusqueda(Restaurante encontrado, String camino, int nivel, int grado) {
            this.encontrado = encontrado;
            this.camino = camino;
            this.nivel = nivel;
            this.grado = grado;
        }
    }

    private Nodo raiz;

    public ArbolRestaurantes() {
        this.raiz = null;
    }

    public void insertar(Restaurante restaurante) {
        if (restaurante == null) {
            return;
        }
        raiz = insertarRec(raiz, restaurante);
    }

    private Nodo insertarRec(Nodo actual, Restaurante r) {
        if (actual == null) {
            return new Nodo(r);
        }
        if (r.getCalificacion() < actual.dato.getCalificacion()) {
            actual.izquierdo = insertarRec(actual.izquierdo, r);
        } else if (r.getCalificacion() > actual.dato.getCalificacion()) {
            actual.derecho = insertarRec(actual.derecho, r);
        } else {
            actual.dato = r;
        }
        return actual;
    }

    public String inordenTexto() {
        if (raiz == null) {
            return "(Árbol vacío)";
        }
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.toString();
    }

    private void inordenRec(Nodo actual, StringBuilder sb) {
        if (actual == null) {
            return;
        }
        inordenRec(actual.izquierdo, sb);
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(actual.dato.getCalificacion()).append(" ").append(actual.dato.getNombre());
        inordenRec(actual.derecho, sb);
    }

    public ResultadoBusqueda buscar(int calificacion) {
        Nodo actual = raiz;
        int nivel = 0;
        StringBuilder camino = new StringBuilder();

        while (actual != null) {
            if (camino.length() > 0) {
                camino.append(" -> ");
            }
            camino.append(actual.dato.getCalificacion()).append(" ").append(actual.dato.getNombre());

            if (calificacion == actual.dato.getCalificacion()) {
                int grado = 0;
                if (actual.izquierdo != null) grado++;
                if (actual.derecho != null) grado++;
                return new ResultadoBusqueda(actual.dato, camino.toString(), nivel, grado);
            } else if (calificacion < actual.dato.getCalificacion()) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
            nivel++;
        }
        return new ResultadoBusqueda(null, camino.toString(), -1, -1);
    }

    public int peso() {
        int total = contarNodos(raiz);
        return total == 0 ? 0 : total - 1;
    }

    private int contarNodos(Nodo actual) {
        if (actual == null) {
            return 0;
        }
        return 1 + contarNodos(actual.izquierdo) + contarNodos(actual.derecho);
    }

    public int altura() {
        return alturaRec(raiz);
    }

    private int alturaRec(Nodo actual) {
        if (actual == null) {
            return -1;
        }
        int altIzq = alturaRec(actual.izquierdo);
        int altDer = alturaRec(actual.derecho);
        return 1 + Math.max(altIzq, altDer);
    }

    public int contarHojas() {
        return contarHojasRec(raiz);
    }

    private int contarHojasRec(Nodo actual) {
        if (actual == null) {
            return 0;
        }
        if (actual.izquierdo == null && actual.derecho == null) {
            return 1;
        }
        return contarHojasRec(actual.izquierdo) + contarHojasRec(actual.derecho);
    }

    public boolean esCompleto() {
        return esCompletoRec(raiz);
    }

    private boolean esCompletoRec(Nodo actual) {
        if (actual == null) {
            return true;
        }
        if (actual.izquierdo == null && actual.derecho == null) {
            return true;
        }
        if (actual.izquierdo != null && actual.derecho != null) {
            return esCompletoRec(actual.izquierdo) && esCompletoRec(actual.derecho);
        }
        return false;
    }
}
