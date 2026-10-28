package dao;

import java.util.List;
import modelo.Producto;

public interface ProductoDAO {
    // Aquí solo declaramos los métodos que vamos a usar (como reglas a cumplir)
    boolean registrar(Producto producto);
    List<Producto> listar();
    boolean reducirStock(String sku, int cantidadRestar);
    
    // Métodos adicionales para el CRUD del panel administrativo
    boolean actualizarStock(String sku, int nuevoStock);
    boolean eliminar(String sku);
}
