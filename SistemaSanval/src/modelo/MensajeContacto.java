package modelo;

import java.sql.Timestamp;

/**
 * Representa un mensaje enviado por un cliente desde el formulario
 * de la sección Contacto de la página web.
 */
public class MensajeContacto {
    private int idMensaje;
    private String nombre;
    private String correo;
    private String asunto;
    private String mensaje;
    private Timestamp fechaEnvio;
    private String estado; // Pendiente | Atendido

    public MensajeContacto() {
    }

    public MensajeContacto(String nombre, String correo, String asunto, String mensaje) {
        this.nombre = nombre;
        this.correo = correo;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.estado = "Pendiente";
    }

    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Timestamp getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Timestamp fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
