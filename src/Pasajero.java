// Pasajero hereda de Persona y agrega la nacionalidad.
// Se usa el id para identificar internamente al pasajero dentro del sistema.

public class Pasajero extends Persona {

    private int id;
    private String nacionalidad;

    public Pasajero(int id, String nombre, String apellido, String documento, String nacionalidad) {
        super(nombre, apellido, documento);
        this.id = id;
        this.nacionalidad = nacionalidad;
    }

    public int getId() {
        return id;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    @Override
    public String toString() {
        return String.format("Pasajero{id=%d, nombre='%s', doc='%s', nac='%s'}",
                id, getNombreCompleto(), getDocumento(), nacionalidad);
    }
}
