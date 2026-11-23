package delivery.grafo;


public class GrafoZonas {

    private static final int INF = 1_000_000_000;

    private final Zona[] zonas;
    private final int[][] matrizAdyacencia;
    private final Arista[] aristas;
    private int numZonas;
    private int numAristas;

    public GrafoZonas(int maxZonas, int maxAristas) {
        this.zonas = new Zona[maxZonas];
        this.matrizAdyacencia = new int[maxZonas][maxZonas];
        this.aristas = new Arista[maxAristas];
        this.numZonas = 0;
        this.numAristas = 0;

        for (int i = 0; i < maxZonas; i++) {
            for (int j = 0; j < maxZonas; j++) {
                matrizAdyacencia[i][j] = (i == j) ? 0 : INF;
            }
        }
    }

    public void agregarZona(String codigo, String nombre) {
        if (numZonas < zonas.length) {
            zonas[numZonas++] = new Zona(codigo, nombre);
        }
    }

    public void agregarArista(String codigoA, String codigoB, int minutos) {
        int u = buscarIndiceZona(codigoA);
        int v = buscarIndiceZona(codigoB);
        if (u == -1 || v == -1) {
            return;
        }
        int menor = Math.min(u, v);
        int mayor = Math.max(u, v);
        matrizAdyacencia[u][v] = minutos;
        matrizAdyacencia[v][u] = minutos;
        if (numAristas < aristas.length) {
            aristas[numAristas++] = new Arista(menor, mayor, minutos);
        }
    }

    public int buscarIndiceZona(String codigoONombre) {
        if (codigoONombre == null) {
            return -1;
        }
        String limpio = codigoONombre.trim();
        if (limpio.isEmpty()) {
            return -1;
        }
        String tokenCodigo = limpio.contains(" ") ? limpio.substring(0, limpio.indexOf(' ')).trim() : limpio;
        for (int i = 0; i < numZonas; i++) {
            if (zonas[i].getCodigo().equalsIgnoreCase(tokenCodigo)
                    || zonas[i].getNombre().equalsIgnoreCase(limpio)
                    || zonas[i].toString().equalsIgnoreCase(limpio)) {
                return i;
            }
        }
        return -1;
    }

    public boolean existeZona(String codigo) {
        return buscarIndiceZona(codigo) != -1;
    }

    public Zona[] getZonas() {
        Zona[] copia = new Zona[numZonas];
        for (int i = 0; i < numZonas; i++) {
            copia[i] = zonas[i];
        }
        return copia;
    }

    public static class ResultadoRuta {
        public final boolean exito;
        public final String mensajeError;
        public final String caminoTexto;
        public final int minutosTotales;

        public ResultadoRuta(boolean exito, String mensajeError, String caminoTexto, int minutosTotales) {
            this.exito = exito;
            this.mensajeError = mensajeError;
            this.caminoTexto = caminoTexto;
            this.minutosTotales = minutosTotales;
        }
    }

    public ResultadoRuta dijkstra(String origenCodigo, String destinoCodigo) {
        int origen = buscarIndiceZona(origenCodigo);
        if (origen == -1) {
            return new ResultadoRuta(false, "La zona de origen '" + origenCodigo + "' no está en el mapa.", "", -1);
        }
        int destino = buscarIndiceZona(destinoCodigo);
        if (destino == -1) {
            return new ResultadoRuta(false, "La zona '" + destinoCodigo + "' no está en el mapa. No se puede calcular ruta.", "", -1);
        }

        int[] dist = new int[numZonas];
        int[] prev = new int[numZonas];
        boolean[] visitado = new boolean[numZonas];

        for (int i = 0; i < numZonas; i++) {
            dist[i] = INF;
            prev[i] = -1;
            visitado[i] = false;
        }
        dist[origen] = 0;

        for (int paso = 0; paso < numZonas; paso++) {
            int u = -1;
            int mejorDist = INF;
            for (int i = 0; i < numZonas; i++) {
                if (!visitado[i] && dist[i] < mejorDist) {
                    mejorDist = dist[i];
                    u = i;
                }
            }
            if (u == -1) {
                break;
            }
            visitado[u] = true;
            if (u == destino) {
                break;
            }
            for (int v = 0; v < numZonas; v++) {
                if (!visitado[v] && matrizAdyacencia[u][v] < INF) {
                    int nuevaDist = dist[u] + matrizAdyacencia[u][v];
                    if (nuevaDist < dist[v]) {
                        dist[v] = nuevaDist;
                        prev[v] = u;
                    }
                }
            }
        }

        if (dist[destino] >= INF) {
            return new ResultadoRuta(false, "No existe camino entre " + zonas[origen].getCodigo() + " y " + zonas[destino].getCodigo() + ".", "", -1);
        }

        // Reconstrucción del camino: retrocede desde el destino siguiendo prev[] hasta el origen y luego invierte el orden
        int[] pilaCamino = new int[numZonas];
        int longitud = 0;
        int actual = destino;
        while (actual != -1) {
            pilaCamino[longitud++] = actual;
            actual = prev[actual];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = longitud - 1; i >= 0; i--) {
            sb.append(zonas[pilaCamino[i]].getCodigo());
            if (i > 0) {
                sb.append(" -> ");
            }
        }
        return new ResultadoRuta(true, "", sb.toString(), dist[destino]);
        
    }

    public static class ResultadoMST {
        public final int costoTotal;
        public final String detalle;

        public ResultadoMST(int costoTotal, String detalle) {
            this.costoTotal = costoTotal;
            this.detalle = detalle;
        }
    }

    public ResultadoMST kruskal(int limiteZonas) {
        int n = Math.min(limiteZonas, numZonas);
        Arista[] candidatas = new Arista[numAristas];
        int totalCand = 0;
        for (int i = 0; i < numAristas; i++) {
            Arista a = aristas[i];
            if (a.getOrigen() < n && a.getDestino() < n) {
                candidatas[totalCand++] = a;
            }
        }

        // Ordenamiento por selección propio (sin Arrays.sort)
        for (int i = 0; i < totalCand - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < totalCand; j++) {
                if (esMenorArista(candidatas[j], candidatas[minIdx])) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Arista tmp = candidatas[i];
                candidatas[i] = candidatas[minIdx];
                candidatas[minIdx] = tmp;
            }
        }

        int[] padre = new int[n];
        int[] rango = new int[n];
        for (int i = 0; i < n; i++) {
            padre[i] = i;
            rango[i] = 0;
        }

        StringBuilder aceptadas = new StringBuilder();
        StringBuilder rechazadas = new StringBuilder();
        int costo = 0;
        int contAceptadas = 0;

        for (int i = 0; i < totalCand; i++) {
            Arista a = candidatas[i];
            int raizU = encontrar(padre, a.getOrigen());
            int raizV = encontrar(padre, a.getDestino());
            String etiqueta = zonas[a.getOrigen()].getCodigo() + "-" + zonas[a.getDestino()].getCodigo()
                    + " (" + a.getMinutos() + ")";

            if (raizU != raizV) {
                unir(padre, rango, raizU, raizV);
                costo += a.getMinutos();
                contAceptadas++;
                if (aceptadas.length() > 0) aceptadas.append(", ");
                aceptadas.append(etiqueta);
            } else {
                if (rechazadas.length() > 0) rechazadas.append(", ");
                rechazadas.append(etiqueta);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Kruskal (Z1..Z").append(n).append("):\n");
        sb.append("  Aceptadas (").append(contAceptadas).append("): ").append(aceptadas).append("\n");
        sb.append("  Rechazadas: ").append(rechazadas.length() == 0 ? "(ninguna)" : rechazadas).append("\n");
        sb.append("  Costo total: ").append(costo).append(" min");
        return new ResultadoMST(costo, sb.toString());
    }

    private boolean esMenorArista(Arista a, Arista b) {
        if (a.getMinutos() != b.getMinutos()) {
            return a.getMinutos() < b.getMinutos();
        }
        if (a.getOrigen() != b.getOrigen()) {
            return a.getOrigen() < b.getOrigen();
        }
        return a.getDestino() < b.getDestino();
    }

    private int encontrar(int[] padre, int x) {
        while (padre[x] != x) {
            padre[x] = padre[padre[x]];
            x = padre[x];
        }
        return x;
    }

    private void unir(int[] padre, int[] rango, int r1, int r2) {
        if (rango[r1] < rango[r2]) {
            padre[r1] = r2;
        } else if (rango[r1] > rango[r2]) {
            padre[r2] = r1;
        } else {
            padre[r2] = r1;
            rango[r1]++;
        }
    }

    public ResultadoMST prim(int limiteZonas, String inicioCodigo) {
        int n = Math.min(limiteZonas, numZonas);
        int inicio = buscarIndiceZona(inicioCodigo);
        if (inicio == -1 || inicio >= n) {
            return new ResultadoMST(-1, "Zona inicial inválida para Prim.");
        }

        boolean[] enArbol = new boolean[n];
        int[] minCosto = new int[n];
        int[] padre = new int[n];

        for (int i = 0; i < n; i++) {
            enArbol[i] = false;
            minCosto[i] = INF;
            padre[i] = -1;
        }
        minCosto[inicio] = 0;

        StringBuilder aristasTexto = new StringBuilder();
        int costoTotal = 0;

        for (int paso = 0; paso < n; paso++) {
            int u = -1;
            int mejor = INF;
            for (int i = 0; i < n; i++) {
                if (!enArbol[i] && minCosto[i] < mejor) {
                    mejor = minCosto[i];
                    u = i;
                }
            }
            if (u == -1) {
                break;
            }
            enArbol[u] = true;
            if (padre[u] != -1) {
                costoTotal += minCosto[u];
                if (aristasTexto.length() > 0) aristasTexto.append(", ");
                int a = Math.min(padre[u], u);
                int b = Math.max(padre[u], u);
                aristasTexto.append(zonas[a].getCodigo()).append("-").append(zonas[b].getCodigo())
                            .append(" (").append(minCosto[u]).append(")");
            }

            for (int v = 0; v < n; v++) {
                if (!enArbol[v] && matrizAdyacencia[u][v] < minCosto[v]) {
                    minCosto[v] = matrizAdyacencia[u][v];
                    padre[v] = u;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Prim desde ").append(zonas[inicio].getCodigo()).append(" (Z1..Z").append(n).append("):\n");
        sb.append("  Aristas: ").append(aristasTexto).append("\n");
        sb.append("  Costo total: ").append(costoTotal).append(" min");
        return new ResultadoMST(costoTotal, sb.toString());
    }
}
