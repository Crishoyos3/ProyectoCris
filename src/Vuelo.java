import java.util.LinkedList;

// Vuelo gestiona dos estructuras:
//  - LinkedList<Pasajero> para los confirmados (se eligió LinkedList
//    porque las inserciones y eliminaciones son frecuentes)
//  - LinkedList usada como Queue para la lista de espera

public class Vuelo {

    private String codigo;
    private String origen;
    private String destino;
    private int capacidad;

    // Usamos LinkedList para la lista de pasajeros confirmados.
    // Permite eliminar en cualquier posición eficientemente.
    private LinkedList<Pasajero> pasajeros;

    // La cola de espera también es una LinkedList pero se usa como Queue
    // (solo se agrega al final y se saca del inicio)
    private LinkedList<Pasajero> colaEspera;

    public Vuelo(String codigo, String origen, String destino, int capacidad) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.capacidad = capacidad;
        this.pasajeros = new LinkedList<>();
        this.colaEspera = new LinkedList<>();
    }

    public boolean cupoDisponible() {
        return pasajeros.size() < capacidad;
    }

    public void agregarPasajero(Pasajero p) {
        pasajeros.add(p);
    }

    public void agregarAEspera(Pasajero p) {
        colaEspera.addLast(p);
    }

    // Elimina el pasajero de la lista confirmada.
    // Retorna el objeto eliminado, o null si no existía.
    public Pasajero eliminarPasajero(String documento) {
        Pasajero objetivo = buscarPorDocumento(documento);
        if (objetivo != null)
            pasajeros.remove(objetivo);
        return objetivo;
    }

    // Toma al primero de la cola de espera y lo confirma.
    // Retorna null si la cola estaba vacía o no hay cupo.
    public Pasajero atenderSiguienteEnEspera() {
        if (colaEspera.isEmpty() || !cupoDisponible())
            return null;
        Pasajero siguiente = colaEspera.removeFirst();
        pasajeros.add(siguiente);
        return siguiente;
    }

    public Pasajero buscarPorDocumento(String documento) {
        for (Pasajero p : pasajeros) {
            if (p.getDocumento().equals(documento))
                return p;
        }
        return null;
    }

    public String getCodigo() { return codigo; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public int getCapacidad() { return capacidad; }
    public LinkedList<Pasajero> getPasajeros() { return pasajeros; }
    public LinkedList<Pasajero> getColaEspera() { return colaEspera; }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s  |  %d/%d ocupado",
                codigo, origen, destino, pasajeros.size(), capacidad);
    }
}
