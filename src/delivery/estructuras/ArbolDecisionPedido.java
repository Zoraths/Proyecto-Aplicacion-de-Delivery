package delivery.estructuras;

import delivery.model.Pedido;

/**
 * Árbol binario de decisión para clasificar dónde debe esperar un pedido antes de encolarlo.
 */
public class ArbolDecisionPedido {

    public enum DestinoEspera {
        MONTICULO,
        COLA_SIMPLE,
        SOLO_TABLA_PENDIENTE
    }

    private static class NodoDecision {
        final String pregunta;
        final String conclusion;
        final DestinoEspera destino;
        final NodoDecision ramaSi;
        final NodoDecision ramaNo;

        NodoDecision(String pregunta, NodoDecision ramaSi, NodoDecision ramaNo) {
            this.pregunta = pregunta;
            this.conclusion = null;
            this.destino = null;
            this.ramaSi = ramaSi;
            this.ramaNo = ramaNo;
        }

        NodoDecision(String conclusion, DestinoEspera destino) {
            this.pregunta = null;
            this.conclusion = conclusion;
            this.destino = destino;
            this.ramaSi = null;
            this.ramaNo = null;
        }

        boolean esHoja() {
            return destino != null;
        }
    }

    public static class ResultadoDecision {
        public final DestinoEspera destino;
        public final String conclusion;
        public final String traza;

        public ResultadoDecision(DestinoEspera destino, String conclusion, String traza) {
            this.destino = destino;
            this.conclusion = conclusion;
            this.traza = traza;
        }
    }

    private final NodoDecision raiz;

    public ArbolDecisionPedido() {
        NodoDecision hojaMonticulo = new NodoDecision(
                "entra al montículo y queda pendiente",
                DestinoEspera.MONTICULO
        );
        NodoDecision hojaCola = new NodoDecision(
                "entra a la cola simple",
                DestinoEspera.COLA_SIMPLE
        );
        NodoDecision hojaTabla = new NodoDecision(
                "sigue pendiente en la tabla, sin repartidor",
                DestinoEspera.SOLO_TABLA_PENDIENTE
        );
        NodoDecision nodoRepartidor = new NodoDecision(
                "¿Hay un repartidor libre?",
                hojaCola,
                hojaTabla
        );
        this.raiz = new NodoDecision(
                "¿El pedido es urgente o es comida caliente?",
                hojaMonticulo,
                nodoRepartidor
        );
    }

    public ResultadoDecision evaluar(Pedido pedido, boolean hayRepartidorLibre) {
        StringBuilder traza = new StringBuilder();
        NodoDecision actual = raiz;

        while (actual != null && !actual.esHoja()) {
            boolean respuesta;
            if (actual == raiz) {
                respuesta = pedido.esUrgenteOComidaCaliente();
            } else {
                respuesta = hayRepartidorLibre;
            }
            traza.append(actual.pregunta)
                 .append(" -> ")
                 .append(respuesta ? "sí" : "no")
                 .append(" -> ");
            actual = respuesta ? actual.ramaSi : actual.ramaNo;
        }

        String conclusion = actual != null ? actual.conclusion : "";
        DestinoEspera destino = actual != null ? actual.destino : DestinoEspera.SOLO_TABLA_PENDIENTE;
        traza.append(conclusion);
        return new ResultadoDecision(destino, conclusion, traza.toString());
    }
}
