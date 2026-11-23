package delivery.estructuras;

import delivery.model.Pedido;

public class TablaHashPedidos {

    public static final int NUM_CUBETAS = 5;

    private static class Nodo {
        int id;
        Pedido pedido;
        Nodo siguiente;

        Nodo(Pedido pedido) {
            this.id = pedido.getId();
            this.pedido = pedido;
            this.siguiente = null;
        }
    }

    private final Nodo[] cubetas;
    private int totalPedidos;

    public TablaHashPedidos() {
        this.cubetas = new Nodo[NUM_CUBETAS];
        this.totalPedidos = 0;
    }

    public int calcularCubeta(int id) {
        int mod = id % NUM_CUBETAS;
        return mod < 0 ? mod + NUM_CUBETAS : mod;
    }

    public int getTotalPedidos() {
        return totalPedidos;
    }

    public void insertar(Pedido pedido) {
        if (pedido == null) {
            return;
        }
        int indice = calcularCubeta(pedido.getId());
        Nodo actual = cubetas[indice];
        Nodo anterior = null;

        while (actual != null) {
            if (actual.id == pedido.getId()) {
                actual.pedido = pedido;
                return;
            }
            anterior = actual;
            actual = actual.siguiente;
        }

        Nodo nuevo = new Nodo(pedido);
        if (anterior == null) {
            cubetas[indice] = nuevo;
        } else {
            anterior.siguiente = nuevo;
        }
        totalPedidos++;
    }

    public Pedido consultar(int id) {
        int indice = calcularCubeta(id);
        Nodo actual = cubetas[indice];
        while (actual != null) {
            if (actual.id == id) {
                return actual.pedido;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean eliminar(int id) {
        int indice = calcularCubeta(id);
        Nodo actual = cubetas[indice];
        Nodo anterior = null;

        while (actual != null) {
            if (actual.id == id) {
                if (anterior == null) {
                    cubetas[indice] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                totalPedidos--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public Pedido[] listarTodos() {
        Pedido[] lista = new Pedido[totalPedidos];
        int k = 0;
        for (int i = 0; i < NUM_CUBETAS; i++) {
            Nodo actual = cubetas[i];
            while (actual != null) {
                lista[k++] = actual.pedido;
                actual = actual.siguiente;
            }
        }
        return lista;
    }

    public String describirCubeta(int indice) {
        if (indice < 0 || indice >= NUM_CUBETAS) {
            return "";
        }
        if (cubetas[indice] == null) {
            return "cubeta " + indice + ": (vacía)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("cubeta ").append(indice).append(": ");
        Nodo actual = cubetas[indice];
        while (actual != null) {
            sb.append(actual.id);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    public String describirTodasCubetas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_CUBETAS; i++) {
            sb.append("Cubeta ").append(i).append(": ");
            if (cubetas[i] == null) {
                sb.append("(vacía)");
            } else {
                Nodo actual = cubetas[i];
                while (actual != null) {
                    sb.append("#").append(actual.id)
                      .append(" (").append(actual.pedido.getEstado()).append(")");
                    if (actual.siguiente != null) {
                        sb.append(" -> ");
                    }
                    actual = actual.siguiente;
                }
            }
            if (i < NUM_CUBETAS - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
