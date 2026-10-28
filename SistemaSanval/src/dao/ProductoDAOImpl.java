package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Conexion;
import modelo.Producto;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public boolean registrar(Producto producto) {
        String sqlProducto = "INSERT INTO Producto (codigoSKU, descripcion, idCategoria, talla, color, precioUnitario, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO Inventario (idProducto, cantidadActual, ubicacion) VALUES (?, ?, ?)";
        
        Connection con = Conexion.conectar();
        if (con == null) {
            return false;
        }
        
        try {
            con.setAutoCommit(false);
            
            int idCategoria = producto.getIdCategoria() > 0 ? producto.getIdCategoria() : inferirCategoria(producto.getCodigoSKU());
            String color = (producto.getColor() != null && !producto.getColor().trim().isEmpty()) ? producto.getColor().trim() : "Negro";
            double precio = producto.getPrecioUnitario() > 0 ? producto.getPrecioUnitario() : 45.00;
            String estado = (producto.getEstado() != null && !producto.getEstado().trim().isEmpty()) ? producto.getEstado().trim() : "Activo";
            int stockInicial = Math.max(0, producto.getStock());

            int idGenerado = -1;
            try (PreparedStatement psProd = con.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS)) {
                psProd.setString(1, producto.getCodigoSKU().trim());
                psProd.setString(2, producto.getDescripcion().trim());
                psProd.setInt(3, idCategoria);
                psProd.setString(4, producto.getTalla().trim());
                psProd.setString(5, color);
                psProd.setDouble(6, precio);
                psProd.setString(7, estado);
                psProd.executeUpdate();
                
                try (ResultSet rsKeys = psProd.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        idGenerado = rsKeys.getInt(1);
                    }
                }
            }

            if (idGenerado > 0) {
                try (PreparedStatement psInv = con.prepareStatement(sqlInventario)) {
                    psInv.setInt(1, idGenerado);
                    psInv.setInt(2, stockInicial);
                    psInv.setString(3, "Almacén General");
                    psInv.executeUpdate();
                }
            }

            con.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("[ERROR DAO] Error en rollback: " + ex.getMessage());
            }
            System.err.println("[ERROR DAO] No se pudo registrar el producto: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                System.err.println("[ERROR DAO] Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.idProducto, p.codigoSKU, p.descripcion, p.idCategoria, p.talla, p.color, " +
                     "p.precioUnitario, p.estado, COALESCE(i.cantidadActual, 0) AS stock " +
                     "FROM Producto p " +
                     "LEFT JOIN Inventario i ON p.idProducto = i.idProducto " +
                     "ORDER BY p.idProducto ASC";
        
        Connection con = Conexion.conectar();
        if (con == null) {
            return lista;
        }

        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(sql);
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
                prod.setStock(rs.getInt("stock"));
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
        
        Connection con = Conexion.conectar();
        if (con == null) {
            return false;
        }

        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setInt(1, cantidadRestar);
            ps.setString(2, sku);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] No se pudo actualizar el stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarStock(String sku, int nuevoStock) {
        String sqlUpdate = "UPDATE Inventario SET cantidadActual = ? " +
                           "WHERE idProducto = (SELECT idProducto FROM Producto WHERE codigoSKU = ?)";
        String sqlBuscarProducto = "SELECT idProducto FROM Producto WHERE codigoSKU = ?";
        String sqlInsertarInventario = "INSERT INTO Inventario (idProducto, cantidadActual, ubicacion) VALUES (?, ?, 'Almacén General')";

        Connection con = Conexion.conectar();
        if (con == null) {
            return false;
        }

        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(sqlUpdate)) {
            
            ps.setInt(1, nuevoStock);
            ps.setString(2, sku.trim());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }

            // Si el producto existe pero aún no tenía registro en Inventario, lo creamos
            try (PreparedStatement psProd = c.prepareStatement(sqlBuscarProducto)) {
                psProd.setString(1, sku.trim());
                try (ResultSet rs = psProd.executeQuery()) {
                    if (rs.next()) {
                        int idProducto = rs.getInt("idProducto");
                        try (PreparedStatement psInv = c.prepareStatement(sqlInsertarInventario)) {
                            psInv.setInt(1, idProducto);
                            psInv.setInt(2, nuevoStock);
                            return psInv.executeUpdate() > 0;
                        }
                    }
                }
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] No se pudo modificar el stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String sku) {
        String sqlObtenerId = "SELECT idProducto FROM Producto WHERE codigoSKU = ?";
        String sqlEliminarMovimientos = "DELETE FROM Movimiento WHERE idProducto = ?";
        String sqlEliminarInventario = "DELETE FROM Inventario WHERE idProducto = ?";
        String sqlEliminarProducto = "DELETE FROM Producto WHERE idProducto = ?";

        Connection con = Conexion.conectar();
        if (con == null) {
            return false;
        }

        try {
            con.setAutoCommit(false);

            int idProducto = -1;
            try (PreparedStatement psId = con.prepareStatement(sqlObtenerId)) {
                psId.setString(1, sku.trim());
                try (ResultSet rs = psId.executeQuery()) {
                    if (rs.next()) {
                        idProducto = rs.getInt("idProducto");
                    }
                }
            }

            if (idProducto == -1) {
                con.rollback();
                return false;
            }

            try (PreparedStatement psMov = con.prepareStatement(sqlEliminarMovimientos)) {
                psMov.setInt(1, idProducto);
                psMov.executeUpdate();
            }

            try (PreparedStatement psInv = con.prepareStatement(sqlEliminarInventario)) {
                psInv.setInt(1, idProducto);
                psInv.executeUpdate();
            }

            int filasEliminadas;
            try (PreparedStatement psProd = con.prepareStatement(sqlEliminarProducto)) {
                psProd.setInt(1, idProducto);
                filasEliminadas = psProd.executeUpdate();
            }

            con.commit();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("[ERROR DAO] Error en rollback al eliminar: " + ex.getMessage());
            }
            System.err.println("[ERROR DAO] No se pudo eliminar el producto: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                System.err.println("[ERROR DAO] Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    private int inferirCategoria(String sku) {
        if (sku == null) return 1;
        String upper = sku.trim().toUpperCase();
        if (upper.startsWith("SOM")) return 3;
        if (upper.startsWith("ACC")) return 4;
        return 1;
    }
}
