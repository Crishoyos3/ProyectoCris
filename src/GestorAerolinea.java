import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

// GestorAerolinea centraliza toda la lógica del negocio.
// El menú (Consola.java) solo se encarga de leer datos y llamar a este gestor.
// Esto separa la interfaz de la lógica, lo que hace el código más ordenado.

public class GestorAerolinea {

    private ArrayList<Vuelo> vuelos;
    private ArrayList<Reserva> reservas;

    // La pila guarda strings descriptivos de lo que pasó,
    // en el formato "ACCION;doc;codigoVuelo;fecha"
    private Deque<String> pilaAcciones;

    private int contadorPasajero;

    public GestorAerolinea() {
        vuelos = new ArrayList<>();
        reservas = new ArrayList<>();
        pilaAcciones = new ArrayDeque<>();
        contadorPasajero = 1;
    }

    // -------------------------------------------------------
    // GESTIÓN DE VUELOS
    // -------------------------------------------------------

    public boolean registrarVuelo(String codigo, String origen, String destino, int capacidad) {
        if (buscarVueloPorCodigo(vuelos, codigo, vuelos.size() - 1) != null)
            return false;
        vuelos.add(new Vuelo(codigo, origen, destino, capacidad));
        return true;
    }

    public ArrayList<Vuelo> getVuelos() {
        return vuelos;
    }

    // Búsqueda recursiva desde el final hacia el inicio
    // caso base: n < 0 → no se encontró
    // caso recursivo: compara el vuelo en posición n; si no coincide, busca en n-1
    public Vuelo buscarVueloPorCodigo(List<Vuelo> lista, String codigo, int n) {
        if (n < 0)
            return null;
        if (lista.get(n).getCodigo().equalsIgnoreCase(codigo))
            return lista.get(n);
        return buscarVueloPorCodigo(lista, codigo, n - 1);
    }

    // -------------------------------------------------------
    // GESTIÓN DE RESERVAS
    // -------------------------------------------------------

    // Retorna: 0 = ok confirmado, 1 = ok en espera, -1 = vuelo no existe, -2 = ya tiene reserva
    public int hacerReserva(String codigoVuelo, String nombre, String apellido,
                             String doc, String nac, String fecha) {
        if (vuelos.isEmpty()) return -1;
        Vuelo v = buscarVueloPorCodigo(vuelos, codigoVuelo, vuelos.size() - 1);
        if (v == null) return -1;
        if (v.buscarPorDocumento(doc) != null) return -2;

        Pasajero p = new Pasajero(contadorPasajero++, nombre, apellido, doc, nac);

        if (v.cupoDisponible()) {
            v.agregarPasajero(p);
            Reserva r = new Reserva(p, v, fecha);
            reservas.add(r);
            // registrar en pila: RESERVA;doc;codigo;fecha
            pilaAcciones.push("RESERVA;" + doc + ";" + codigoVuelo + ";" + fecha);
            return 0;
        } else {
            v.agregarAEspera(p);
            return 1;
        }
    }

    // Retorna: pasajero cancelado, o null si no existía
    public Pasajero cancelarReserva(String codigoVuelo, String doc, String fecha) {
        if (vuelos.isEmpty()) return null;
        Vuelo v = buscarVueloPorCodigo(vuelos, codigoVuelo, vuelos.size() - 1);
        if (v == null) return null;

        Pasajero eliminado = v.eliminarPasajero(doc);
        if (eliminado == null) return null;

        // actualizar el objeto Reserva correspondiente
        Reserva r = buscarReserva(doc, codigoVuelo);
        if (r != null) r.cancelar();

        pilaAcciones.push("CANCELACION;" + doc + ";" + codigoVuelo + ";"
                + eliminado.getNombre() + ";" + eliminado.getApellido()
                + ";" + eliminado.getNacionalidad() + ";" + eliminado.getId() + ";" + fecha);

        // intentar promover al siguiente de la espera automáticamente
        v.atenderSiguienteEnEspera();

        return eliminado;
    }

    private Reserva buscarReserva(String doc, String codigoVuelo) {
        for (Reserva r : reservas) {
            if (r.getPasajero().getDocumento().equals(doc)
                    && r.getVuelo().getCodigo().equals(codigoVuelo)
                    && r.getEstado().equals("ACTIVA"))
                return r;
        }
        return null;
    }

    // -------------------------------------------------------
    // HISTORIAL Y DESHACER
    // -------------------------------------------------------

    public Deque<String> getPilaAcciones() {
        return pilaAcciones;
    }

    // Deshace la última acción registrada en la pila.
    // Retorna un mensaje con lo que hizo (para mostrarlo en consola).
    public String deshacerUltimaAccion() {
        if (pilaAcciones.isEmpty())
            return "No hay acciones para deshacer.";

        String accion = pilaAcciones.pop();
        String[] p = accion.split(";");
        String tipo = p[0];

        if (tipo.equals("RESERVA")) {
            // deshacer reserva = eliminar al pasajero
            String doc = p[1];
            String cod = p[2];
            if (vuelos.isEmpty()) return "El vuelo ya no existe.";
            Vuelo v = buscarVueloPorCodigo(vuelos, cod, vuelos.size() - 1);
            if (v == null) return "El vuelo " + cod + " ya no existe.";
            Pasajero eliminado = v.eliminarPasajero(doc);
            if (eliminado == null)
                return "El pasajero ya no estaba en el vuelo.";
            return "Se anuló la reserva de " + eliminado.getNombreCompleto() + " en " + cod + ".";

        } else if (tipo.equals("CANCELACION")) {
            // deshacer cancelación = restaurar al pasajero
            // campos: doc, cod, nombre, apellido, nac, id, fecha
            String doc = p[1];
            String cod = p[2];
            String nombre = p[3];
            String apellido = p[4];
            String nac = p[5];
            int id = Integer.parseInt(p[6]);

            if (vuelos.isEmpty()) return "El vuelo ya no existe.";
            Vuelo v = buscarVueloPorCodigo(vuelos, cod, vuelos.size() - 1);
            if (v == null) return "El vuelo " + cod + " ya no existe.";

            Pasajero restaurado = new Pasajero(id, nombre, apellido, doc, nac);
            if (v.cupoDisponible()) {
                v.agregarPasajero(restaurado);
                return "Se restauró la reserva de " + restaurado.getNombreCompleto() + " en " + cod + ".";
            } else {
                v.agregarAEspera(restaurado);
                return restaurado.getNombreCompleto() + " fue enviado a lista de espera (vuelo lleno).";
            }
        }
        return "Tipo de acción desconocido.";
    }

    // -------------------------------------------------------
    // REPORTES (RECURSIVIDAD)
    // -------------------------------------------------------

    // Cuenta los pasajeros de un vuelo desde el índice n hacia el inicio
    // caso base: n < 0 → 0
    // caso recursivo: 1 + contar(n-1)
    public int contarPasajerosVuelo(List<Pasajero> lista, int n) {
        if (n < 0)
            return 0;
        return 1 + contarPasajerosVuelo(lista, n - 1);
    }

    // Suma los pasajeros de todos los vuelos que van a un destino dado
    // caso base: n < 0 → 0
    // caso recursivo: acumula si el vuelo n coincide + llama con n-1
    public int contarPasajerosPorDestino(List<Vuelo> lista, String destino, int n) {
        if (n < 0)
            return 0;
        int suma = lista.get(n).getDestino().equalsIgnoreCase(destino)
                ? lista.get(n).getPasajeros().size()
                : 0;
        return suma + contarPasajerosPorDestino(lista, destino, n - 1);
    }

    // Busca un pasajero por documento en todos los vuelos, desde el vuelo n hacia atrás
    // caso base: n < 0 → null
    // caso recursivo: busca en el vuelo n; si no está, intenta con n-1
    public Pasajero buscarPasajeroPorDoc(List<Vuelo> lista, String doc, int n) {
        if (n < 0)
            return null;
        Pasajero encontrado = lista.get(n).buscarPorDocumento(doc);
        if (encontrado != null)
            return encontrado;
        return buscarPasajeroPorDoc(lista, doc, n - 1);
    }
}
