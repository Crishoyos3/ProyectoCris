// Clase Reserva - vincula un pasajero con un vuelo.
// Incluye la fecha de la reserva y el estado (activa o cancelada).

public class Reserva {

    private Pasajero pasajero;
    private Vuelo vuelo;
    private String fecha;      // formato libre, ej: "2026-05-16"
    private String estado;     // "ACTIVA" o "CANCELADA"

    public Reserva(Pasajero pasajero, Vuelo vuelo, String fecha) {
        this.pasajero = pasajero;
        this.vuelo = vuelo;
        this.fecha = fecha;
        this.estado = "ACTIVA";
    }

    public void cancelar() {
        this.estado = "CANCELADA";
    }

    public Pasajero getPasajero() { return pasajero; }
    public Vuelo getVuelo() { return vuelo; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return String.format("Reserva[%s] %s en vuelo %s  fecha:%s",
                estado, pasajero.getNombreCompleto(), vuelo.getCodigo(), fecha);
    }
}
