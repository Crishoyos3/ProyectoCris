// Clase Persona - base para Pasajero
// Tiene los datos comunes de cualquier persona en el sistema

public class Persona {

    protected String nombre;
    protected String apellido;
    protected String documento;

    public Persona(String nombre, String apellido, String documento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }
}
