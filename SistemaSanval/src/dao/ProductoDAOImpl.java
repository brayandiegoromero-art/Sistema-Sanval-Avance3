package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Conexion;
import modelo.Producto;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public boolean registrar(Producto producto) {
        String sql = "INSERT INTO Producto (codigoSKU, descripcion, idCategoria, talla, color, precioUnitario, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, producto.getCodigoSKU());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getIdCategoria());
            ps.setString(4, producto.getTalla());
            ps.setString(5, producto.getColor());
            ps.setDouble(6, producto.getPrecioUnitario());
            ps.setString(7, producto.getEstado());
            
            ps.execute();
            return true;
            
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] No se pudo registrar el producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto";
        
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setIdProducto(rs.getInt("idProducto"));
                prod.setCodigoSKU(rs.getString("codigoSKU"));
                prod.setDescripcion(rs.getString("descripcion"));
                prod.setIdCategoria(rs.getInt("idCategoria"));
                prod.setTalla(rs.getString("talla"));
                prod.setColor(rs.getString("color"));
                prod.setPrecioUnitario(rs.getDouble("precioUnitario"));
                prod.setEstado(rs.getString("estado"));
                lista.add(prod);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean reducirStock(String sku, int cantidadRestar) {
        String sql = "UPDATE Inventario SET cantidadActual = cantidadActual - ? " +
                     "WHERE idProducto = (SELECT idProducto FROM Producto WHERE codigoSKU = ?)";
        
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cantidadRestar);
            ps.setString(2, sku);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] No se pudo actualizar el stock: " + e.getMessage());
            return false;
        }
    }
}