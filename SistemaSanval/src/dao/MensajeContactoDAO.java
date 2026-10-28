package dao;

import java.util.List;
import modelo.MensajeContacto;

public interface MensajeContactoDAO {
    /** Crea la tabla MensajeContacto si todavía no existe en SanvalDB. */
    void crearTablaSiNoExiste();

    /** Guarda un nuevo mensaje enviado desde la web. */
    boolean guardar(MensajeContacto mensaje);

    /** Lista todos los mensajes (los más recientes primero). */
    List<MensajeContacto> listar();

    /** Cambia el estado de un mensaje (ej. "Atendido"). */
    boolean cambiarEstado(int idMensaje, String estado);
}
