package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Conexion;
import modelo.MensajeContacto;

public class MensajeContactoDAOImpl implements MensajeContactoDAO {

    @Override
    public void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS MensajeContacto ("
                + " idMensaje INT AUTO_INCREMENT PRIMARY KEY,"
                + " nombre VARCHAR(100) NOT NULL,"
                + " correo VARCHAR(100) NOT NULL,"
                + " asunto VARCHAR(150) NOT NULL,"
                + " mensaje TEXT NOT NULL,"
                + " fechaEnvio DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + " estado VARCHAR(20) DEFAULT 'Pendiente'"
                + ")";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException | NullPointerException e) {
            System.err.println("[ERROR DAO] No se pudo crear la tabla MensajeContacto: " + e.getMessage());
        }
    }

    @Override
    public boolean guardar(MensajeContacto m) {
        String sql = "INSERT INTO MensajeContacto (nombre, correo, asunto, mensaje, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getNombre());
            ps.setString(2, m.getCorreo());
            ps.setString(3, m.getAsunto());
            ps.setString(4, m.getMensaje());
            ps.setString(5, m.getEstado() == null ? "Pendiente" : m.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException | NullPointerException e) {
            System.err.println("[ERROR DAO] No se pudo guardar el mensaje: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<MensajeContacto> listar() {
        List<MensajeContacto> lista = new ArrayList<>();
        String sql = "SELECT * FROM MensajeContacto ORDER BY fechaEnvio DESC";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MensajeContacto m = new MensajeContacto();
                m.setIdMensaje(rs.getInt("idMensaje"));
                m.setNombre(rs.getString("nombre"));
                m.setCorreo(rs.getString("correo"));
                m.setAsunto(rs.getString("asunto"));
                m.setMensaje(rs.getString("mensaje"));
                m.setFechaEnvio(rs.getTimestamp("fechaEnvio"));
                m.setEstado(rs.getString("estado"));
                lista.add(m);
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("[ERROR DAO] No se pudo listar los mensajes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean cambiarEstado(int idMensaje, String estado) {
        String sql = "UPDATE MensajeContacto SET estado = ? WHERE idMensaje = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idMensaje);
            return ps.executeUpdate() > 0;

        } catch (SQLException | NullPointerException e) {
            System.err.println("[ERROR DAO] No se pudo actualizar el mensaje: " + e.getMessage());
            return false;
        }
    }
}
