import java.util.Scanner;




public class Consola {

    private static GestorAerolinea gestor = new GestorAerolinea();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  Sistema de Gestion de Aerolinea");
        System.out.println("=============================================");

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = pedirEntero("Seleccione: ");
            switch (opcion) {
                case 1:
                    menuVuelos();
                    break;
                case 2:
                    menuReservas();
                    break;
                case 3:
                    menuEspera();
                    break;
                case 4:
                    menuHistorial();
                    break;
                case 5:
                    menuReportes();
                    break;
                case 0:
                    salir = true;
                    System.out.println("Cerrando sistema. Hasta luego.");
                    break;
                default:
                    System.out.println("Opcion no reconocida, intente de nuevo.");
            }
        }
    }

    // --------------------------- MENÚ PRINCIPAL----------------------------

    static void mostrarMenuPrincipal() {
        System.out.println("\n=============================================");
        System.out.println("  1. Gestion de vuelos");
        System.out.println("  2. Reservas de pasajeros");
        System.out.println("  3. Lista de espera");
        System.out.println("  4. Historial y deshacer");
        System.out.println("  5. Reportes");
        System.out.println("  0. Salir");
        System.out.println("=============================================");
    }


    // --------------------------MENÚ VUELOS-----------------------------

    static void menuVuelos() {
        System.out.println("\n--- Gestion de Vuelos ---");
        System.out.println("  1. Registrar vuelo");
        System.out.println("  2. Listar vuelos");
        System.out.println("  3. Buscar vuelo por codigo");
        int op = pedirEntero("Opcion: ");

        switch (op) {
            case 1:
                registrarVuelo();
                break;
            case 2:
                listarVuelos();
                break;
            case 3:
                buscarVuelo();
                break;
            default:
                System.out.println("Opcion no valida.");
        }
    }

    static void registrarVuelo() {
        System.out.println("\nNuevo Vuelo:");
        String codigo = pedirTexto("  Codigo: ");
        String origen = pedirTexto("  Origen: ");
        String destino = pedirTexto("  Destino: ");
        int capacidad = pedirEntero("  Capacidad: ");

        if (capacidad < 1) {
            System.out.println("La capacidad debe ser al menos 1.");
            return;
        }

        boolean ok = gestor.registrarVuelo(codigo, origen, destino, capacidad);
        if (ok)
            System.out.println("Vuelo " + codigo + " registrado exitosamente.");
        else
            System.out.println("Ya existe un vuelo con el codigo " + codigo + ".");
    }

    static void listarVuelos() {
        if (gestor.getVuelos().isEmpty()) {
            System.out.println("No hay vuelos registrados en el sistema.");
            return;
        }
        System.out.println("\nVuelos registrados:");
        for (Vuelo v : gestor.getVuelos())
            System.out.println("  " + v);
    }

    static void buscarVuelo() {
        String codigo = pedirTexto("Codigo a buscar: ");
        if (gestor.getVuelos().isEmpty()) {
            System.out.println("No hay vuelos en el sistema.");
            return;
        }
        // llama al método recursivo del gestor
        Vuelo resultado = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
        if (resultado == null)
            System.out.println("No se encontro ningun vuelo con el codigo: " + codigo);
        else
            System.out.println("Vuelo encontrado: " + resultado);
    }


    // ----------------------------MENÚ RESERVAS---------------------------

    static void menuReservas() {
        System.out.println("\n--- Reservas ---");
        System.out.println("  1. Nueva reserva");
        System.out.println("  2. Cancelar reserva");
        System.out.println("  3. Ver pasajeros de un vuelo");
        System.out.println("  4. Ver lista de espera de un vuelo");
        int op = pedirEntero("Opcion: ");

        switch (op) {
            case 1:
                nuevaReserva();
                break;
            case 2:
                cancelarReserva();
                break;
            case 3:
                verPasajeros();
                break;
            case 4:
                verEspera();
                break;
            default:
                System.out.println("Opcion no valida.");
        }
    }

    static void nuevaReserva() {
        System.out.println("\nNueva Reserva:");
        String codigo = pedirTexto("  Codigo del vuelo: ");
        String nombre = pedirTexto("  Nombre del pasajero: ");
        String apellido = pedirTexto("  Apellido: ");
        String doc = pedirTexto("  Documento: ");
        String nac = pedirTexto("  Nacionalidad: ");
        String fecha = pedirTexto("  Fecha de reserva (ej: 2026-05-16): ");

        int resultado = gestor.hacerReserva(codigo, nombre, apellido, doc, nac, fecha);
        switch (resultado) {
            case 0:
                System.out.println("Reserva confirmada para " + nombre + " " + apellido + " en vuelo " + codigo + ".");
                break;
            case 1:
                System.out.println("Vuelo sin cupo. " + nombre + " " + apellido + " fue agregado a lista de espera.");
                break;
            case -1:
                System.out.println("El vuelo " + codigo + " no existe en el sistema.");
                break;
            case -2:
                System.out.println("El pasajero con documento " + doc + " ya tiene reserva en ese vuelo.");
                break;
        }
    }

    static void cancelarReserva() {
        String codigo = pedirTexto("Codigo del vuelo: ");
        String doc = pedirTexto("Documento del pasajero: ");
        String fecha = pedirTexto("Fecha de cancelacion: ");

        Pasajero p = gestor.cancelarReserva(codigo, doc, fecha);
        if (p == null)
            System.out.println("No se encontro un pasajero con ese documento en el vuelo " + codigo + ".");
        else
            System.out.println("Reserva cancelada para: " + p.getNombreCompleto());
    }

    static void verPasajeros() {
        String codigo = pedirTexto("Codigo del vuelo: ");
        if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
        Vuelo v = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
        if (v == null) { System.out.println("Vuelo no encontrado."); return; }
        if (v.getPasajeros().isEmpty()) { System.out.println("No hay pasajeros confirmados en " + codigo + "."); return; }

        System.out.println("\nPasajeros confirmados en vuelo " + codigo + ":");
        for (Pasajero p : v.getPasajeros())
            System.out.println("  " + p);
    }

    static void verEspera() {
        String codigo = pedirTexto("Codigo del vuelo: ");
        if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
        Vuelo v = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
        if (v == null) { System.out.println("Vuelo no encontrado."); return; }
        if (v.getColaEspera().isEmpty()) { System.out.println("Lista de espera vacia para " + codigo + "."); return; }

        System.out.println("\nLista de espera vuelo " + codigo + ":");
        int pos = 1;
        for (Pasajero p : v.getColaEspera())
            System.out.println("  " + pos++ + ". " + p);
    }


    // --------------------------MENÚ LISTA DE ESPERA-----------------------------

    static void menuEspera() {
        System.out.println("\n--- Lista de Espera ---");
        System.out.println("  1. Agregar pasajero a lista de espera");
        System.out.println("  2. Procesar siguiente en espera");
        int op = pedirEntero("Opcion: ");

        if (op == 1) {
            String codigo = pedirTexto("Codigo del vuelo: ");
            if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
            Vuelo v = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
            if (v == null) { System.out.println("Vuelo no encontrado."); return; }

            String nombre = pedirTexto("Nombre: ");
            String apellido = pedirTexto("Apellido: ");
            String doc = pedirTexto("Documento: ");
            String nac = pedirTexto("Nacionalidad: ");

            // Creamos el pasajero con un id provisional y lo metemos a la cola
            Pasajero nuevo = new Pasajero(0, nombre, apellido, doc, nac);
            v.agregarAEspera(nuevo);
            System.out.println(nombre + " " + apellido + " agregado a lista de espera de " + codigo + ".");

        } else if (op == 2) {
            String codigo = pedirTexto("Codigo del vuelo: ");
            if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
            Vuelo v = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
            if (v == null) { System.out.println("Vuelo no encontrado."); return; }

            if (!v.cupoDisponible()) { System.out.println("El vuelo aun esta lleno."); return; }
            Pasajero promovido = v.atenderSiguienteEnEspera();
            if (promovido == null)
                System.out.println("No hay nadie en lista de espera para " + codigo + ".");
            else
                System.out.println(promovido.getNombreCompleto() + " fue promovido a confirmado.");
        }
    }

    // ------------------------MENÚ HISTORIAL-------------------------------

    static void menuHistorial() {
        System.out.println("\n--- Historial ---");
        System.out.println("  1. Ver historial");
        System.out.println("  2. Deshacer ultima accion");
        int op = pedirEntero("Opcion: ");

        if (op == 1) {
            if (gestor.getPilaAcciones().isEmpty()) {
                System.out.println("El historial esta vacio.");
                return;
            }
            System.out.println("\nHistorial de acciones (la mas reciente primero):");
            int n = 1;
            for (String s : gestor.getPilaAcciones()) {
                String[] partes = s.split(";");
                System.out.println("  " + n++ + ". " + partes[0] + " | doc: " + partes[1] + " | vuelo: " + partes[2]);
            }

        } else if (op == 2) {
            String msg = gestor.deshacerUltimaAccion();
            System.out.println(msg);
        }
    }


    // -------------------------MENÚ REPORTES------------------------------

    static void menuReportes() {
        System.out.println("\n--- Reportes ---");
        System.out.println("  1. Contar pasajeros en un vuelo (recursivo)");
        System.out.println("  2. Contar pasajeros hacia un destino (recursivo)");
        System.out.println("  3. Buscar pasajero por documento (recursivo)");
        int op = pedirEntero("Opcion: ");

        switch (op) {
            case 1:
                reporteContarPasajeros();
                break;
            case 2:
                reporteContarDestino();
                break;
            case 3:
                reporteBuscarPasajero();
                break;
            default:
                System.out.println("Opcion no valida.");
        }
    }

    static void reporteContarPasajeros() {
        String codigo = pedirTexto("Codigo del vuelo: ");
        if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
        Vuelo v = gestor.buscarVueloPorCodigo(gestor.getVuelos(), codigo, gestor.getVuelos().size() - 1);
        if (v == null) { System.out.println("Vuelo no encontrado."); return; }
        int total = gestor.contarPasajerosVuelo(v.getPasajeros(), v.getPasajeros().size() - 1);
        System.out.println("El vuelo " + codigo + " tiene " + total + " pasajero(s) confirmado(s).");
    }

    static void reporteContarDestino() {
        String destino = pedirTexto("Destino: ");
        if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
        int total = gestor.contarPasajerosPorDestino(gestor.getVuelos(), destino, gestor.getVuelos().size() - 1);
        System.out.println("Total de pasajeros hacia " + destino + ": " + total);
    }

    static void reporteBuscarPasajero() {
        String doc = pedirTexto("Documento: ");
        if (gestor.getVuelos().isEmpty()) { System.out.println("No hay vuelos."); return; }
        Pasajero p = gestor.buscarPasajeroPorDoc(gestor.getVuelos(), doc, gestor.getVuelos().size() - 1);
        if (p == null)
            System.out.println("No se encontro ningun pasajero con documento: " + doc);
        else
            System.out.println("Pasajero encontrado: " + p);
    }

    // -------------------------UTILIDADES DE LECTURA------------------------------

    static String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    static int pedirEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -99;
        }
    }
}
