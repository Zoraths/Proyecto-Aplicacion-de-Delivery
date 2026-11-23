package delivery.ui;

import delivery.estructuras.ArbolRestaurantes;
import delivery.grafo.GrafoZonas;
import delivery.model.Pedido;
import delivery.model.Repartidor;
import delivery.model.Restaurante;
import delivery.servicio.SimuladorDelivery;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class VentanaPrincipal extends JFrame {

    private final SimuladorDelivery simulador;

    private JTextField txtId;
    private JTextField txtHora;
    private JComboBox<String> cmbTipo;
    private JComboBox<Integer> cmbPrioridad;
    private JTextField txtZona;

    private JTextArea areaCola;
    private JTextArea areaMonticulo;
    private JTextArea areaTabla;
    private JTextArea areaSinEntregar;

    private JComboBox<String> cmbRepartidores;
    private JTextArea areaRepartidores;
    private JTextArea areaCicloActivo;
    private JTextArea areaPila;

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextArea areaMapa;

    private JTextField txtRestNombre;
    private JTextField txtRestCalif;
    private JTextField txtRestBuscar;
    private JTextArea areaRestaurantes;

    private JTextArea areaReporte;
    private JTextArea areaMensajes;

    public VentanaPrincipal() {
        this.simulador = new SimuladorDelivery();
        configurarVentana();
        actualizarVistas();
    }

    private void configurarVentana() {
        setTitle("Aplicación de Delivery - Estructuras de Datos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("1. Pedidos y Cocina", crearPanelPedidos());
        pestanas.addTab("2. Reparto, Repartidores y Pila", crearPanelReparto());
        pestanas.addTab("3. Mapa de Zonas", crearPanelMapa());
        pestanas.addTab("4. Restaurantes (ABB)", crearPanelRestaurantes());
        pestanas.addTab("5. Reporte y Verificación", crearPanelReporte());

        add(pestanas, BorderLayout.CENTER);

        areaMensajes = crearAreaSoloLectura(4);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        scrollMensajes.setBorder(BorderFactory.createTitledBorder("Mensajes y Avisos del Sistema"));
        add(scrollMensajes, BorderLayout.SOUTH);
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formSuperior = new JPanel(new GridLayout(2, 1, 4, 4));
        formSuperior.setBorder(BorderFactory.createTitledBorder("Gestión de Pedidos"));

        JPanel filaCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        txtId = new JTextField("12", 5);
        txtHora = new JTextField("09:10", 6);
        cmbTipo = new JComboBox<>(new String[]{"ordinario", "urgente", "urgente, comida caliente"});
        cmbPrioridad = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        txtZona = new JTextField("Z8", 8);

        filaCampos.add(new JLabel("ID:"));
        filaCampos.add(txtId);
        filaCampos.add(new JLabel("Hora:"));
        filaCampos.add(txtHora);
        filaCampos.add(new JLabel("Tipo:"));
        filaCampos.add(cmbTipo);
        filaCampos.add(new JLabel("Prioridad:"));
        filaCampos.add(cmbPrioridad);
        filaCampos.add(new JLabel("Zona (Z1..Z10):"));
        filaCampos.add(txtZona);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnRegistrar = new JButton("Registrar Pedido");
        JButton btnConsultar = new JButton("Consultar por ID");
        JButton btnEliminar = new JButton("Eliminar de Tabla");
        JButton btnCargarPrueba = new JButton("Cargar 5 Pedidos Prueba");
        JButton btnAtenderCola = new JButton("Atender de Cola");
        JButton btnAtenderMonticulo = new JButton("Atender de Montículo");

        btnRegistrar.addActionListener(e -> accionRegistrarPedido());
        btnConsultar.addActionListener(e -> accionConsultarPedido());
        btnEliminar.addActionListener(e -> accionEliminarPedido());
        btnCargarPrueba.addActionListener(e -> {
            mostrarMensaje(simulador.cargarPedidosDePrueba());
            actualizarVistas();
        });
        btnAtenderCola.addActionListener(e -> {
            mostrarMensaje(simulador.atenderDesdeCola());
            actualizarVistas();
        });
        btnAtenderMonticulo.addActionListener(e -> {
            mostrarMensaje(simulador.atenderDesdeMonticulo());
            actualizarVistas();
        });

        filaBotones.add(btnRegistrar);
        filaBotones.add(btnConsultar);
        filaBotones.add(btnEliminar);
        filaBotones.add(btnCargarPrueba);
        filaBotones.add(btnAtenderCola);
        filaBotones.add(btnAtenderMonticulo);

        formSuperior.add(filaCampos);
        formSuperior.add(filaBotones);
        panel.add(formSuperior, BorderLayout.NORTH);

        JPanel gridEstructuras = new JPanel(new GridLayout(2, 2, 8, 8));
        areaCola = crearAreaSoloLectura(8);
        areaMonticulo = crearAreaSoloLectura(8);
        areaTabla = crearAreaSoloLectura(8);
        areaSinEntregar = crearAreaSoloLectura(8);

        gridEstructuras.add(envolverConTitulo(areaCola, "Cola Simple (Pedidos Ordinarios)"));
        gridEstructuras.add(envolverConTitulo(areaMonticulo, "Montículo (Pedidos Urgentes / Comida Caliente)"));
        gridEstructuras.add(envolverConTitulo(areaTabla, "Tabla Hash (5 Cubetas, id % 5)"));
        gridEstructuras.add(envolverConTitulo(areaSinEntregar, "Pedidos Sin Entregar"));

        panel.add(gridEstructuras, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelReparto() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel controles = new JPanel(new GridLayout(2, 1, 4, 4));
        controles.setBorder(BorderFactory.createTitledBorder("Ciclo de Reparto y Repartidores"));

        JPanel filaCiclo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnTomarCocina = new JButton("1. Tomar de Cocina (Preparación)");
        JButton btnAsignarRuta = new JButton("2. Asignar Repartidor + Dijkstra (En Camino)");
        JButton btnEntregar = new JButton("3. Marcar Entregado");
        JButton btnCicloCompleto = new JButton("Ciclo Completo");
        JButton btnDeshacer = new JButton("Deshacer Último Cambio (Pop Pila)");

        btnTomarCocina.addActionListener(e -> {
            mostrarMensaje(simulador.tomarSiguienteDeCocina());
            actualizarVistas();
        });
        btnAsignarRuta.addActionListener(e -> {
            mostrarMensaje(simulador.asignarRepartidorYDespachar());
            actualizarVistas();
        });
        btnEntregar.addActionListener(e -> {
            mostrarMensaje(simulador.marcarPedidoEntregado());
            actualizarVistas();
        });
        btnCicloCompleto.addActionListener(e -> {
            mostrarMensaje(simulador.ejecutarCicloCompletoSiguiente());
            actualizarVistas();
        });
        btnDeshacer.addActionListener(e -> {
            mostrarMensaje(simulador.deshacerUltimoCambio());
            actualizarVistas();
        });

        filaCiclo.add(btnTomarCocina);
        filaCiclo.add(btnAsignarRuta);
        filaCiclo.add(btnEntregar);
        filaCiclo.add(btnCicloCompleto);
        filaCiclo.add(btnDeshacer);

        JPanel filaRep = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cmbRepartidores = new JComboBox<>(new String[]{"R1", "R2", "R3"});
        JButton btnAlternarRep = new JButton("Alternar Libre / Ocupado");
        btnAlternarRep.addActionListener(e -> {
            String cod = (String) cmbRepartidores.getSelectedItem();
            Repartidor r = simulador.getListaRepartidores().buscar(cod);
            if (r != null) {
                r.setOcupado(!r.isOcupado());
                mostrarMensaje("Repartidor " + r.getNombre() + " ahora está " + (r.isOcupado() ? "ocupado" : "libre") + ".");
                actualizarVistas();
            }
        });
        filaRep.add(new JLabel("Repartidor:"));
        filaRep.add(cmbRepartidores);
        filaRep.add(btnAlternarRep);

        controles.add(filaCiclo);
        controles.add(filaRep);
        panel.add(controles, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        JPanel arribaCentro = new JPanel(new GridLayout(1, 2, 8, 8));
        areaRepartidores = crearAreaSoloLectura(6);
        areaCicloActivo = crearAreaSoloLectura(6);
        arribaCentro.add(envolverConTitulo(areaRepartidores, "Lista Doblemente Enlazada de Repartidores"));
        arribaCentro.add(envolverConTitulo(areaCicloActivo, "Pedido Activo en Ciclo"));

        areaPila = crearAreaSoloLectura(10);
        centro.add(arribaCentro, BorderLayout.NORTH);
        centro.add(envolverConTitulo(areaPila, "Pila de Cambios de Estado (Auditoría, máx 10)"), BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelMapa() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controles.setBorder(BorderFactory.createTitledBorder("Cálculo de Rutas y Árbol de Expansión Mínima"));
        txtOrigen = new JTextField("Z1", 6);
        txtDestino = new JTextField("Z8", 6);

        JButton btnDijkstra = new JButton("Dijkstra (Ruta Mínima)");
        JButton btnKruskal10 = new JButton("Kruskal (10 Zonas)");
        JButton btnKruskal5 = new JButton("Kruskal (Z1-Z5)");
        JButton btnPrim5 = new JButton("Prim (Z1-Z5)");
        JButton btnPrim10 = new JButton("Prim (10 Zonas)");

        btnDijkstra.addActionListener(e -> {
            GrafoZonas.ResultadoRuta r = simulador.getGrafoZonas().dijkstra(txtOrigen.getText(), txtDestino.getText());
            if (r.exito) {
                String res = "Dijkstra " + txtOrigen.getText().trim() + " -> " + txtDestino.getText().trim()
                        + ": " + r.caminoTexto + " (" + r.minutosTotales + " min)";
                areaMapa.setText(res);
                mostrarMensaje(res);
            } else {
                areaMapa.setText("Aviso: " + r.mensajeError);
                mostrarMensaje("Aviso: " + r.mensajeError);
            }
        });
        btnKruskal10.addActionListener(e -> areaMapa.setText(simulador.getGrafoZonas().kruskal(10).detalle));
        btnKruskal5.addActionListener(e -> areaMapa.setText(simulador.getGrafoZonas().kruskal(5).detalle));
        btnPrim5.addActionListener(e -> areaMapa.setText(simulador.getGrafoZonas().prim(5, "Z1").detalle));
        btnPrim10.addActionListener(e -> areaMapa.setText(simulador.getGrafoZonas().prim(10, "Z1").detalle));

        controles.add(new JLabel("Origen:"));
        controles.add(txtOrigen);
        controles.add(new JLabel("Destino:"));
        controles.add(txtDestino);
        controles.add(btnDijkstra);
        controles.add(btnKruskal10);
        controles.add(btnKruskal5);
        controles.add(btnPrim5);
        controles.add(btnPrim10);

        panel.add(controles, BorderLayout.NORTH);

        areaMapa = crearAreaSoloLectura(16);
        areaMapa.setText(simulador.getGrafoZonas().kruskal(10).detalle + "\n\n" + simulador.getGrafoZonas().prim(5, "Z1").detalle);
        panel.add(envolverConTitulo(areaMapa, "Resultado del Grafo de Zonas (Z1..Z10)"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelRestaurantes() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controles.setBorder(BorderFactory.createTitledBorder("Árbol Binario de Búsqueda de Restaurantes"));

        txtRestNombre = new JTextField(10);
        txtRestCalif = new JTextField(5);
        txtRestBuscar = new JTextField("60", 5);

        JButton btnInsertar = new JButton("Insertar Restaurante");
        JButton btnBuscar = new JButton("Buscar Calificación");
        JButton btnVerArbol = new JButton("Ver Inorden y Métricas");

        btnInsertar.addActionListener(e -> {
            String nombre = txtRestNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarMensaje("Error: indique el nombre del restaurante.");
                return;
            }
            try {
                int calif = Integer.parseInt(txtRestCalif.getText().trim());
                simulador.getArbolRestaurantes().insertar(new Restaurante(nombre, calif));
                mostrarMensaje("Restaurante '" + nombre + "' (" + calif + ") insertado en el ABB.");
                mostrarInfoRestaurantes(null);
            } catch (NumberFormatException ex) {
                mostrarMensaje("Error: la calificación debe ser un número entero.");
            }
        });

        btnBuscar.addActionListener(e -> {
            try {
                int calif = Integer.parseInt(txtRestBuscar.getText().trim());
                ArbolRestaurantes.ResultadoBusqueda res = simulador.getArbolRestaurantes().buscar(calif);
                mostrarInfoRestaurantes(res);
                if (res.encontrado == null) {
                    mostrarMensaje("Aviso: calificación " + calif + " no encontrada en el árbol.");
                } else {
                    mostrarMensaje("Encontrado: " + res.encontrado + " (nivel " + res.nivel + ", grado " + res.grado + ").");
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje("Error: la calificación a buscar debe ser un número entero.");
            }
        });

        btnVerArbol.addActionListener(e -> mostrarInfoRestaurantes(null));

        controles.add(new JLabel("Nombre:"));
        controles.add(txtRestNombre);
        controles.add(new JLabel("Calif:"));
        controles.add(txtRestCalif);
        controles.add(btnInsertar);
        controles.add(new JLabel("Buscar Calif:"));
        controles.add(txtRestBuscar);
        controles.add(btnBuscar);
        controles.add(btnVerArbol);

        panel.add(controles, BorderLayout.NORTH);

        areaRestaurantes = crearAreaSoloLectura(16);
        panel.add(envolverConTitulo(areaRestaurantes, "Ranking e Información del ABB"), BorderLayout.CENTER);
        mostrarInfoRestaurantes(simulador.getArbolRestaurantes().buscar(60));
        return panel;
    }

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton btnReporte = new JButton("Generar Reporte por Hora (Selección)");
        JButton btnGuardar = new JButton("Guardar pedidos.txt");
        JButton btnVerificacion = new JButton("Ejecutar Corrida de Verificación (Sección 9)");

        btnReporte.addActionListener(e -> areaReporte.setText(simulador.generarReporteTexto()));
        btnGuardar.addActionListener(e -> {
            String msg = simulador.guardarReporteArchivo("pedidos.txt");
            mostrarMensaje(msg);
            areaReporte.setText(simulador.generarReporteTexto() + "\n\n" + msg);
        });
        btnVerificacion.addActionListener(e -> areaReporte.setText(simulador.ejecutarCorridaVerificacion()));

        controles.add(btnReporte);
        controles.add(btnGuardar);
        controles.add(btnVerificacion);
        panel.add(controles, BorderLayout.NORTH);

        areaReporte = crearAreaSoloLectura(18);
        panel.add(envolverConTitulo(areaReporte, "Salida de Reporte y Verificación"), BorderLayout.CENTER);
        return panel;
    }

    private void accionRegistrarPedido() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String hora = txtHora.getText().trim();
            String tipo = (String) cmbTipo.getSelectedItem();
            int prioridad = (Integer) cmbPrioridad.getSelectedItem();
            String zona = txtZona.getText().trim();

            String resultado = simulador.registrarPedido(id, hora, tipo, prioridad, zona);
            mostrarMensaje(resultado);
            actualizarVistas();
        } catch (NumberFormatException ex) {
            mostrarMensaje("Error: el ID del pedido debe ser un número entero.");
        }
    }

    private void accionConsultarPedido() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            mostrarMensaje(simulador.consultarPedido(id));
        } catch (NumberFormatException ex) {
            mostrarMensaje("Error: el ID a consultar debe ser un número entero.");
        }
    }

    private void accionEliminarPedido() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            mostrarMensaje(simulador.eliminarPedido(id));
            actualizarVistas();
        } catch (NumberFormatException ex) {
            mostrarMensaje("Error: el ID a eliminar debe ser un número entero.");
        }
    }

    private void mostrarInfoRestaurantes(ArbolRestaurantes.ResultadoBusqueda busqueda) {
        ArbolRestaurantes abb = simulador.getArbolRestaurantes();
        StringBuilder sb = new StringBuilder();
        sb.append("Recorrido Inorden (menor a mayor): ").append(abb.inordenTexto()).append("\n");
        sb.append("Peso (sin raíz): ").append(abb.peso()).append("\n");
        sb.append("Altura (raíz = 0): ").append(abb.altura()).append("\n");
        sb.append("Cantidad de hojas: ").append(abb.contarHojas()).append("\n");
        sb.append("¿Es completo? (0 o 2 hijos por nodo): ").append(abb.esCompleto() ? "Sí" : "No").append("\n");
        if (busqueda != null) {
            sb.append("\n--- Resultado de Búsqueda ---\n");
            if (busqueda.encontrado != null) {
                sb.append("Encontrado: ").append(busqueda.encontrado).append("\n");
                sb.append("Camino recorrido: ").append(busqueda.camino).append("\n");
                sb.append("Nivel del nodo: ").append(busqueda.nivel).append("\n");
                sb.append("Grado del nodo: ").append(busqueda.grado);
            } else {
                sb.append("No encontrado.\nCamino recorrido: ")
                  .append(busqueda.camino.isEmpty() ? "(árbol vacío)" : busqueda.camino);
            }
        }
        areaRestaurantes.setText(sb.toString());
    }

    private void actualizarVistas() {
        areaCola.setText(simulador.getColaOrdinarios().describir());
        areaMonticulo.setText(simulador.getMonticuloUrgentes().describir());
        areaTabla.setText(simulador.getTablaPedidos().describirTodasCubetas());
        areaSinEntregar.setText(simulador.listarPedidosSinEntregarTexto());

        StringBuilder repSb = new StringBuilder();
        repSb.append("Ida    : ").append(simulador.getListaRepartidores().textoAdelante()).append("\n");
        repSb.append("Vuelta : ").append(simulador.getListaRepartidores().textoAtras()).append("\n");
        repSb.append("Libres : ").append(simulador.getListaRepartidores().textoLibres()).append("\n\n");
        Repartidor[] todos = simulador.getListaRepartidores().recorrerAdelante();
        for (int i = 0; i < todos.length; i++) {
            repSb.append(" - ").append(todos[i].toString()).append("\n");
        }
        areaRepartidores.setText(repSb.toString());

        Pedido activo = simulador.getPedidoEnCiclo();
        if (activo == null) {
            areaCicloActivo.setText("(Ningún pedido activo en preparación o camino)");
        } else {
            areaCicloActivo.setText("Pedido: " + activo.toString()
                    + "\nRepartidor: " + (activo.getRepartidorAsignado().isEmpty() ? "(sin asignar)" : activo.getRepartidorAsignado())
                    + "\nRuta: " + (activo.getRutaElegida().isEmpty() ? "(sin calcular)" : activo.getRutaElegida()));
        }

        areaPila.setText(simulador.getPilaAuditoria().describir());
    }

    private void mostrarMensaje(String texto) {
        areaMensajes.setText(texto);
        areaMensajes.setCaretPosition(0);
    }

    private JTextArea crearAreaSoloLectura(int filas) {
        JTextArea area = new JTextArea(filas, 30);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    private JScrollPane envolverConTitulo(JTextArea area, String titulo) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        return scroll;
    }
}
