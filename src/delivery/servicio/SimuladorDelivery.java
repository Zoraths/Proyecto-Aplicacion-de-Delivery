package delivery.servicio;

import delivery.estructuras.ArbolDecisionPedido;
import delivery.estructuras.ArbolRestaurantes;
import delivery.estructuras.ColaSimplePedidos;
import delivery.estructuras.ListaRepartidores;
import delivery.estructuras.MonticuloUrgentes;
import delivery.estructuras.PilaCambiosEstado;
import delivery.estructuras.TablaHashPedidos;
import delivery.grafo.GrafoZonas;
import delivery.model.CambioEstado;
import delivery.model.EstadoPedido;
import delivery.model.Pedido;
import delivery.model.Repartidor;
import delivery.model.Restaurante;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SimuladorDelivery {

    private final ColaSimplePedidos colaOrdinarios;
    private final MonticuloUrgentes monticuloUrgentes;
    private final PilaCambiosEstado pilaAuditoria;
    private final ListaRepartidores listaRepartidores;
    private final TablaHashPedidos tablaPedidos;
    private final ArbolRestaurantes arbolRestaurantes;
    private final ArbolDecisionPedido arbolDecision;
    private final GrafoZonas grafoZonas;

    private Pedido pedidoEnCiclo;

    public SimuladorDelivery() {
        this.colaOrdinarios = new ColaSimplePedidos();
        this.monticuloUrgentes = new MonticuloUrgentes();
        this.pilaAuditoria = new PilaCambiosEstado();
        this.listaRepartidores = new ListaRepartidores();
        this.tablaPedidos = new TablaHashPedidos();
        this.arbolRestaurantes = new ArbolRestaurantes();
        this.arbolDecision = new ArbolDecisionPedido();
        this.grafoZonas = new GrafoZonas(10, 20);
        this.pedidoEnCiclo = null;
        cargarDatosBase();
    }

    private void cargarDatosBase() {
        // Zonas Z1..Z10
        grafoZonas.agregarZona("Z1", "Plaza");
        grafoZonas.agregarZona("Z2", "Parque");
        grafoZonas.agregarZona("Z3", "Hospital");
        grafoZonas.agregarZona("Z4", "Universidad");
        grafoZonas.agregarZona("Z5", "Estadio");
        grafoZonas.agregarZona("Z6", "Mercado");
        grafoZonas.agregarZona("Z7", "Puerto");
        grafoZonas.agregarZona("Z8", "Colina");
        grafoZonas.agregarZona("Z9", "Río");
        grafoZonas.agregarZona("Z10", "Aeropuerto");

        // 15 aristas con pesos en minutos (Sección 7)
        grafoZonas.agregarArista("Z1", "Z2", 4);
        grafoZonas.agregarArista("Z1", "Z3", 8);
        grafoZonas.agregarArista("Z2", "Z4", 7);
        grafoZonas.agregarArista("Z3", "Z5", 6);
        grafoZonas.agregarArista("Z6", "Z8", 4);
        grafoZonas.agregarArista("Z7", "Z9", 6);
        grafoZonas.agregarArista("Z2", "Z3", 3);
        grafoZonas.agregarArista("Z4", "Z6", 9);
        grafoZonas.agregarArista("Z3", "Z6", 10);
        grafoZonas.agregarArista("Z4", "Z5", 2);
        grafoZonas.agregarArista("Z5", "Z7", 5);
        grafoZonas.agregarArista("Z6", "Z7", 1);
        grafoZonas.agregarArista("Z8", "Z9", 3);
        grafoZonas.agregarArista("Z8", "Z10", 8);
        grafoZonas.agregarArista("Z9", "Z10", 2);

        // Repartidores iniciales (Sección 7)
        listaRepartidores.insertarAlFinal(new Repartidor("R1", "Ana", false));
        listaRepartidores.insertarAlFinal(new Repartidor("R2", "Luis", false));
        listaRepartidores.insertarAlFinal(new Repartidor("R3", "Marta", true));

        // Restaurantes iniciales en orden exacto (Sección 6)
        arbolRestaurantes.insertar(new Restaurante("Plaza", 90));
        arbolRestaurantes.insertar(new Restaurante("Nube", 70));
        arbolRestaurantes.insertar(new Restaurante("Fuego", 95));
        arbolRestaurantes.insertar(new Restaurante("Mar", 60));
        arbolRestaurantes.insertar(new Restaurante("Sabor", 80));
    }

    public ColaSimplePedidos getColaOrdinarios() {
        return colaOrdinarios;
    }

    public MonticuloUrgentes getMonticuloUrgentes() {
        return monticuloUrgentes;
    }

    public PilaCambiosEstado getPilaAuditoria() {
        return pilaAuditoria;
    }

    public ListaRepartidores getListaRepartidores() {
        return listaRepartidores;
    }

    public TablaHashPedidos getTablaPedidos() {
        return tablaPedidos;
    }

    public ArbolRestaurantes getArbolRestaurantes() {
        return arbolRestaurantes;
    }

    public ArbolDecisionPedido getArbolDecision() {
        return arbolDecision;
    }

    public GrafoZonas getGrafoZonas() {
        return grafoZonas;
    }

    public Pedido getPedidoEnCiclo() {
        return pedidoEnCiclo;
    }

    public String registrarPedido(int id, String hora, String tipo, int prioridad, String zonaDestino) {
        if (hora == null || hora.trim().isEmpty()) {
            return "Error: debe indicar la hora del pedido (ej. 09:10).";
        }
        if (prioridad < 1 || prioridad > 5) {
            return "Error: la prioridad debe estar entre 1 y 5.";
        }
        int idxZona = grafoZonas.buscarIndiceZona(zonaDestino);
        if (idxZona == -1) {
            return "Aviso: la zona '" + zonaDestino + "' no está en el mapa (zonas válidas: Z1 a Z10).";
        }
        if (tablaPedidos.consultar(id) != null) {
            return "Aviso: el pedido #" + id + " ya está registrado en la tabla hash (cubeta "
                    + tablaPedidos.calcularCubeta(id) + ").";
        }

        String nombreZona = grafoZonas.getZonas()[idxZona].toString();
        String codigoZona = grafoZonas.getZonas()[idxZona].getCodigo();
        Pedido pedido = new Pedido(id, hora.trim(), tipo, prioridad, codigoZona, nombreZona);

        tablaPedidos.insertar(pedido);

        boolean hayLibre = listaRepartidores.hayLibre();
        ArbolDecisionPedido.ResultadoDecision decision = arbolDecision.evaluar(pedido, hayLibre);

        String estructuraDestino;
        if (decision.destino == ArbolDecisionPedido.DestinoEspera.MONTICULO) {
            monticuloUrgentes.insertar(pedido);
            estructuraDestino = "MonticuloUrgentes";
        } else if (decision.destino == ArbolDecisionPedido.DestinoEspera.COLA_SIMPLE) {
            colaOrdinarios.encolar(pedido);
            estructuraDestino = "ColaSimplePedidos";
        } else {
            estructuraDestino = "TablaHashPedidos (sin cola/montículo)";
        }

        pilaAuditoria.apilar(new CambioEstado(
                pedido.getId(),
                null,
                EstadoPedido.PENDIENTE,
                "TablaHashPedidos + ArbolDecision -> " + estructuraDestino,
                decision.conclusion
        ));

        return "Pedido #" + id + " registrado en cubeta " + tablaPedidos.calcularCubeta(id) + ".\n"
                + "Árbol de decisión: " + decision.traza;
    }

    public String cargarPedidosDePrueba() {
        StringBuilder sb = new StringBuilder();
        sb.append(registrarPedido(12, "09:10", "ordinario", 1, "Z8 Colina")).append("\n");
        sb.append(registrarPedido(17, "09:05", "urgente", 5, "Z10 Aeropuerto")).append("\n");
        sb.append(registrarPedido(3, "09:40", "ordinario", 2, "Z5 Estadio")).append("\n");
        sb.append(registrarPedido(8, "09:12", "urgente, comida caliente", 4, "Z7 Puerto")).append("\n");
        sb.append(registrarPedido(21, "09:30", "ordinario", 1, "Z4 Universidad"));
        return sb.toString();
    }

    public String consultarPedido(int id) {
        Pedido p = tablaPedidos.consultar(id);
        if (p == null) {
            return "Aviso: pedido #" + id + " no registrado.";
        }
        int cubeta = tablaPedidos.calcularCubeta(id);
        return "Encontrado en cubeta " + cubeta + ": " + p.toString()
                + (p.getRepartidorAsignado().isEmpty() ? "" : " | Repartidor: " + p.getRepartidorAsignado())
                + (p.getRutaElegida().isEmpty() ? "" : " | Ruta: " + p.getRutaElegida());
    }

    public String eliminarPedido(int id) {
        int cubeta = tablaPedidos.calcularCubeta(id);
        boolean eliminado = tablaPedidos.eliminar(id);
        if (!eliminado) {
            return "Aviso: pedido #" + id + " no registrado. No se eliminó nada.";
        }
        return "Pedido #" + id + " eliminado de la cubeta " + cubeta
                + ". Estado actual de esa cubeta -> " + tablaPedidos.describirCubeta(cubeta);
    }

    public String atenderDesdeCola() {
        if (colaOrdinarios.estaVacia()) {
            return "Aviso: la cola de pedidos ordinarios está vacía. Nada que desencolar.";
        }
        Pedido p = colaOrdinarios.desencolar();
        return iniciarPreparacion(p, "ColaSimplePedidos");
    }

    public String atenderDesdeMonticulo() {
        if (monticuloUrgentes.estaVacio()) {
            return "Aviso: el montículo de pedidos urgentes está vacío. Nada que extraer.";
        }
        Pedido p = monticuloUrgentes.extraerMaximo();
        return iniciarPreparacion(p, "MonticuloUrgentes");
    }

    public String tomarSiguienteDeCocina() {
        if (!monticuloUrgentes.estaVacio()) {
            return atenderDesdeMonticulo();
        }
        if (!colaOrdinarios.estaVacia()) {
            return atenderDesdeCola();
        }
        return "Aviso: la cocina está vacía. No hay pedidos en el montículo ni en la cola simple.";
    }

    private String iniciarPreparacion(Pedido p, String estructura) {
        EstadoPedido anterior = p.getEstado();
        p.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoEnCiclo = p;
        pilaAuditoria.apilar(new CambioEstado(
                p.getId(),
                anterior,
                EstadoPedido.EN_PREPARACION,
                estructura,
                "Cocina inicia preparación"
        ));
        return "Pedido #" + p.getId() + " tomado de " + estructura + " -> estado: " + p.getEstado() + ".";
    }

    public String asignarRepartidorYDespachar() {
        if (pedidoEnCiclo == null) {
            return "Aviso: primero tome un pedido de cocina para pasarlo a preparación.";
        }
        if (pedidoEnCiclo.getEstado() != EstadoPedido.EN_PREPARACION) {
            return "Aviso: el pedido activo #" + pedidoEnCiclo.getId() + " está en estado '"
                    + pedidoEnCiclo.getEstado() + "', no en preparación.";
        }
        Repartidor libre = listaRepartidores.buscarPrimerLibre();
        if (libre == null) {
            return "Aviso: no hay repartidores libres en este momento. El pedido #"
                    + pedidoEnCiclo.getId() + " sigue en preparación.";
        }

        GrafoZonas.ResultadoRuta ruta = grafoZonas.dijkstra("Z1", pedidoEnCiclo.getZonaDestino());
        if (!ruta.exito) {
            return "Aviso: " + ruta.mensajeError;
        }

        libre.setOcupado(true);
        pedidoEnCiclo.setRepartidorAsignado(libre.getNombre());
        pedidoEnCiclo.setRutaElegida(ruta.caminoTexto + " (" + ruta.minutosTotales + " min)");
        EstadoPedido anterior = pedidoEnCiclo.getEstado();
        pedidoEnCiclo.setEstado(EstadoPedido.EN_CAMINO);

        pilaAuditoria.apilar(new CambioEstado(
                pedidoEnCiclo.getId(),
                anterior,
                EstadoPedido.EN_CAMINO,
                "ListaRepartidores + GrafoZonas (Dijkstra)",
                "Repartidor: " + libre.getNombre() + ", Ruta: " + pedidoEnCiclo.getRutaElegida()
        ));

        return "Pedido #" + pedidoEnCiclo.getId() + " asignado a " + libre.getNombre()
                + " (ahora ocupado). Siguiente libre: " + listaRepartidores.textoLibres()
                + "\nRuta Dijkstra: " + pedidoEnCiclo.getRutaElegida();
    }

    public String marcarPedidoEntregado() {
        if (pedidoEnCiclo == null) {
            return "Aviso: no hay ningún pedido activo en ciclo.";
        }
        if (pedidoEnCiclo.getEstado() != EstadoPedido.EN_CAMINO) {
            return "Aviso: el pedido activo #" + pedidoEnCiclo.getId() + " debe estar 'en camino' antes de entregarse.";
        }
        EstadoPedido anterior = pedidoEnCiclo.getEstado();
        pedidoEnCiclo.setEstado(EstadoPedido.ENTREGADO);
        pilaAuditoria.apilar(new CambioEstado(
                pedidoEnCiclo.getId(),
                anterior,
                EstadoPedido.ENTREGADO,
                "TablaHashPedidos (cierre de reparto)",
                "Entregado por " + pedidoEnCiclo.getRepartidorAsignado()
        ));
        return "Pedido #" + pedidoEnCiclo.getId() + " marcado como 'entregado'.";
    }

    public String ejecutarCicloCompletoSiguiente() {
        if (monticuloUrgentes.estaVacio() && colaOrdinarios.estaVacia()) {
            return "Aviso: la cocina está vacía. No hay pedidos pendientes en montículo ni cola.";
        }
        if (!listaRepartidores.hayLibre()) {
            return "Aviso: no hay repartidores libres para completar el ciclo de reparto.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tomarSiguienteDeCocina()).append("\n");
        sb.append(asignarRepartidorYDespachar()).append("\n");
        if (pedidoEnCiclo != null && pedidoEnCiclo.getEstado() == EstadoPedido.EN_CAMINO) {
            sb.append(marcarPedidoEntregado());
        }
        return sb.toString();
    }

    public String deshacerUltimoCambio() {
        if (pilaAuditoria.estaVacia()) {
            return "Aviso: la pila de auditoría está vacía. No hay cambios para deshacer.";
        }
        CambioEstado retirado = pilaAuditoria.desapilar();
        Pedido p = tablaPedidos.consultar(retirado.getPedidoId());
        if (p != null && retirado.getEstadoAnterior() != null) {
            if (retirado.getEstadoNuevo() == EstadoPedido.EN_CAMINO
                    && retirado.getEstadoAnterior() == EstadoPedido.EN_PREPARACION
                    && !p.getRepartidorAsignado().isEmpty()) {
                listaRepartidores.marcarOcupado(p.getRepartidorAsignado(), false);
                p.setRepartidorAsignado("");
                p.setRutaElegida("");
            }
            p.setEstado(retirado.getEstadoAnterior());
        }
        CambioEstado nuevaCima = pilaAuditoria.verCima();
        return "Se retiró de la pila: " + retirado.toString() + "\n"
                + "Nueva cima: " + (nuevaCima == null ? "(Pila vacía)" : nuevaCima.toString());
    }

    /**
     * Ordenamiento por selección por hora (HH:mm), sin usar métodos de ordenación de biblioteca.
     */
    public Pedido[] ordenarPedidosPorHoraSeleccion() {
        Pedido[] pedidos = tablaPedidos.listarTodos();
        int n = pedidos.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (pedidos[j].getHora().compareTo(pedidos[minIdx].getHora()) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Pedido temp = pedidos[i];
                pedidos[i] = pedidos[minIdx];
                pedidos[minIdx] = temp;
            }
        }
        return pedidos;
    }

    public String generarReporteTexto() {
        Pedido[] ordenados = ordenarPedidosPorHoraSeleccion();
        if (ordenados.length == 0) {
            return "(No hay pedidos registrados en la tabla para generar el reporte)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DEL DÍA (Ordenamiento por selección según hora)\n");
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < ordenados.length; i++) {
            Pedido p = ordenados[i];
            sb.append("  ").append(p.getHora())
              .append(" -> #").append(p.getId())
              .append(" | ").append(p.getTipo())
              .append(" | P:").append(p.getPrioridad())
              .append(" | ").append(p.getClienteDireccion())
              .append(" | Estado: ").append(p.getEstado())
              .append("\n");
            if (i > 0) ids.append(", ");
            ids.append(p.getId());
        }
        sb.append("Orden de IDs: ").append(ids);
        return sb.toString();
    }

    public String guardarReporteArchivo(String rutaArchivo) {
        String contenido = generarReporteTexto();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write(contenido);
            bw.newLine();
            return "Archivo guardado correctamente en: " + rutaArchivo;
        } catch (IOException e) {
            return "Error al guardar el archivo: " + e.getMessage();
        }
    }

    public String listarPedidosSinEntregarTexto() {
        Pedido[] todos = tablaPedidos.listarTodos();
        StringBuilder sb = new StringBuilder();
        int cont = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getEstado() != EstadoPedido.ENTREGADO) {
                sb.append("  ").append(todos[i].toString()).append("\n");
                cont++;
            }
        }
        if (cont == 0) {
            return "(Todos los pedidos han sido entregados o no hay pedidos registrados)";
        }
        return sb.toString();
    }

    /**
     * Ejecuta en estructuras aisladas la corrida exacta de verificación (Secciones 7, 8 y 9)
     * para comprobar cada resultado esperado sin alterar la sesión actual del usuario.
     */
    public String ejecutarCorridaVerificacion() {
        StringBuilder out = new StringBuilder();
        out.append("=== CORRIDA DE VERIFICACIÓN (SECCIÓN 9) ===\n\n");

        // 1. Cola simple (solo ordinarios: 12, 3, 21)
        ColaSimplePedidos colaPrueba = new ColaSimplePedidos();
        Pedido p12Cola = new Pedido(12, "09:10", "ordinario", 1, "Z8", "Z8 Colina");
        Pedido p3Cola = new Pedido(3, "09:40", "ordinario", 2, "Z5", "Z5 Estadio");
        Pedido p21Cola = new Pedido(21, "09:30", "ordinario", 1, "Z4", "Z4 Universidad");
        colaPrueba.encolar(p12Cola);
        colaPrueba.encolar(p3Cola);
        colaPrueba.encolar(p21Cola);
        Pedido primeroCola = colaPrueba.desencolar();
        out.append("Cola simple       : sale primero el ").append(primeroCola.getId())
           .append(", todavía ").append(primeroCola.getEstado()).append("\n");

        // 2. Montículo (los cinco pedidos: 12, 17, 3, 8, 21)
        MonticuloUrgentes montPrueba = new MonticuloUrgentes();
        montPrueba.insertar(new Pedido(12, "09:10", "ordinario", 1, "Z8", "Z8 Colina"));
        montPrueba.insertar(new Pedido(17, "09:05", "urgente", 5, "Z10", "Z10 Aeropuerto"));
        montPrueba.insertar(new Pedido(3, "09:40", "ordinario", 2, "Z5", "Z5 Estadio"));
        montPrueba.insertar(new Pedido(8, "09:12", "urgente, comida caliente", 4, "Z7", "Z7 Puerto"));
        montPrueba.insertar(new Pedido(21, "09:30", "ordinario", 1, "Z4", "Z4 Universidad"));
        out.append("Montículo         : sale ");
        int[] ordenMont = new int[5];
        for (int i = 0; i < 5; i++) {
            ordenMont[i] = montPrueba.extraerMaximo().getId();
        }
        out.append(ordenMont[0]).append(", luego ").append(ordenMont[1])
           .append(", luego ").append(ordenMont[2]).append(", luego ").append(ordenMont[3])
           .append(" y al final ").append(ordenMont[4]).append("\n");

        // 3. Tabla hash (cubeta 2: 12 -> 17, consultar 17, eliminar 12, re-registrar 12)
        TablaHashPedidos tablaPrueba = new TablaHashPedidos();
        Pedido t12 = new Pedido(12, "09:10", "ordinario", 1, "Z8", "Z8 Colina");
        Pedido t17 = new Pedido(17, "09:05", "urgente", 5, "Z10", "Z10 Aeropuerto");
        Pedido t3 = new Pedido(3, "09:40", "ordinario", 2, "Z5", "Z5 Estadio");
        Pedido t8 = new Pedido(8, "09:12", "urgente, comida caliente", 4, "Z7", "Z7 Puerto");
        Pedido t21 = new Pedido(21, "09:30", "ordinario", 1, "Z4", "Z4 Universidad");
        tablaPrueba.insertar(t12);
        tablaPrueba.insertar(t17);
        tablaPrueba.insertar(t3);
        tablaPrueba.insertar(t8);
        tablaPrueba.insertar(t21);
        out.append("Tabla             : ").append(tablaPrueba.describirCubeta(2)).append(". ");
        out.append("El 17 ").append(tablaPrueba.consultar(17) != null ? "se encuentra" : "no se encuentra").append(".\n");
        tablaPrueba.eliminar(12);
        out.append("                    Al eliminar el 12 queda -> ").append(tablaPrueba.describirCubeta(2)).append(".");
        tablaPrueba.insertar(t12);
        out.append(" Luego el 12 se registra de nuevo.\n");

        // 4. Repartidores
        ListaRepartidores repPrueba = new ListaRepartidores();
        repPrueba.insertarAlFinal(new Repartidor("R1", "Ana", false));
        repPrueba.insertarAlFinal(new Repartidor("R2", "Luis", false));
        repPrueba.insertarAlFinal(new Repartidor("R3", "Marta", true));
        out.append("Repartidores      : ida: ").append(repPrueba.textoAdelante())
           .append(" | vuelta: ").append(repPrueba.textoAtras())
           .append(". Libres: ").append(repPrueba.textoLibres()).append(".\n");
        Repartidor asignado = repPrueba.buscarPrimerLibre();
        asignado.setOcupado(true);
        out.append("                    Se asigna ").append(asignado.getNombre())
           .append(" y queda ocupada. El siguiente libre es ").append(repPrueba.textoLibres()).append(".\n");

        // 5. Árbol de decisión
        ArbolDecisionPedido decPrueba = new ArbolDecisionPedido();
        out.append("Decisión del 17   : ").append(decPrueba.evaluar(t17, true).conclusion).append("\n");
        out.append("Decisión del 12   : (con Ana libre) ").append(decPrueba.evaluar(t12, true).conclusion).append("\n");
        out.append("Decisión del 21   : (si los tres están ocupados) ").append(decPrueba.evaluar(t21, false).conclusion).append("\n");

        // 6. Árbol de restaurantes
        ArbolRestaurantes restPrueba = new ArbolRestaurantes();
        restPrueba.insertar(new Restaurante("Plaza", 90));
        restPrueba.insertar(new Restaurante("Nube", 70));
        restPrueba.insertar(new Restaurante("Fuego", 95));
        restPrueba.insertar(new Restaurante("Mar", 60));
        restPrueba.insertar(new Restaurante("Sabor", 80));
        ArbolRestaurantes.ResultadoBusqueda busq60 = restPrueba.buscar(60);
        out.append("Restaurantes      : inorden ").append(restPrueba.inordenTexto())
           .append(". Completo: ").append(restPrueba.esCompleto() ? "sí" : "no")
           .append(". Peso: ").append(restPrueba.peso())
           .append(". Altura: ").append(restPrueba.altura())
           .append(". Hojas: ").append(restPrueba.contarHojas()).append(".\n");
        out.append("                    Búsqueda 60: camino [").append(busq60.camino)
           .append("], nivel ").append(busq60.nivel).append(", grado ").append(busq60.grado).append(".\n");

        // 7. Grafo: Dijkstra, Kruskal y Prim
        GrafoZonas.ResultadoRuta ruta12 = grafoZonas.dijkstra("Z1", "Z8");
        GrafoZonas.ResultadoMST kruskal10 = grafoZonas.kruskal(10);
        GrafoZonas.ResultadoMST prim5 = grafoZonas.prim(5, "Z1");
        out.append("Ruta del 12       : ").append(ruta12.caminoTexto).append(", ").append(ruta12.minutosTotales).append(" min\n");
        out.append("Kruskal (10 zonas): costo ").append(kruskal10.costoTotal).append(". Rechaza Z3-Z6 (10), que la ruta sí usa.\n");
        out.append("Prim (Z1 a Z5)    : costo ").append(prim5.costoTotal).append("\n");

        // 8. Ciclo del 12 y Pila
        PilaCambiosEstado pilaPrueba = new PilaCambiosEstado();
        t12.setEstado(EstadoPedido.PENDIENTE);
        pilaPrueba.apilar(new CambioEstado(12, null, EstadoPedido.PENDIENTE, "TablaHashPedidos", "Registro"));
        t12.setEstado(EstadoPedido.EN_PREPARACION);
        pilaPrueba.apilar(new CambioEstado(12, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, "ColaSimplePedidos", "Cocina"));
        t12.setEstado(EstadoPedido.EN_CAMINO);
        pilaPrueba.apilar(new CambioEstado(12, EstadoPedido.EN_PREPARACION, EstadoPedido.EN_CAMINO, "ListaRepartidores + Dijkstra", "En ruta"));
        t12.setEstado(EstadoPedido.ENTREGADO);
        pilaPrueba.apilar(new CambioEstado(12, EstadoPedido.EN_CAMINO, EstadoPedido.ENTREGADO, "TablaHashPedidos", "Entrega"));
        out.append("Ciclo del 12      : pendiente -> en preparación -> en camino -> entregado\n");
        out.append("Pila              : la cima queda en ").append(pilaPrueba.verCima().getEstadoNuevo()).append(". ");
        pilaPrueba.desapilar();
        out.append("Al retirarla, la cima es ").append(pilaPrueba.verCima().getEstadoNuevo()).append(".\n");

        // 9. Reporte ordenado por hora (selección)
        Pedido[] arr = tablaPrueba.listarTodos();
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].getHora().compareTo(arr[minIdx].getHora()) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Pedido tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
            }
        }
        out.append("Reporte (hora)    : ");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) out.append(", ");
            out.append(arr[i].getId());
        }
        out.append("\n\n=== CASOS DE BORDE ===\n");
        ColaSimplePedidos colaVacia = new ColaSimplePedidos();
        out.append("- Cocina vacía: ").append(colaVacia.estaVacia() ? "Cola vacía avisada con claridad, sin excepción." : "Error").append("\n");
        GrafoZonas.ResultadoRuta rutaZ11 = grafoZonas.dijkstra("Z1", "Z11");
        out.append("- Zona Z11: ").append(rutaZ11.mensajeError).append("\n");
        out.append("- Id inexistente (999): ").append(tablaPrueba.consultar(999) == null ? "Pedido #999 no registrado." : "Error");

        return out.toString();
    }
}
