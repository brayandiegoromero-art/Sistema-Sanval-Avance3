package vista;

import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import modelo.Producto;
import java.util.List;

public class PruebaRegistro {
    public static void main(String[] args) {
        
        ProductoDAO dao = new ProductoDAOImpl();
        
        Producto nuevoGorro = new Producto();
        nuevoGorro.setCodigoSKU("GOR-07");
        nuevoGorro.setDescripcion("Gorro de Cuero Piloto");
        nuevoGorro.setIdCategoria(1); 
        nuevoGorro.setTalla("M");
        nuevoGorro.setColor("Marron Oscuro");
        nuevoGorro.setPrecioUnitario(75.50);
        nuevoGorro.setEstado("Activo");
        
        boolean exito = dao.registrar(nuevoGorro);
        
        if (exito) {
            System.out.println("[RESULTADO] ¡Exito! El gorro GOR-07 se guardo correctamente en MySQL.");
        } else {
            System.out.println("[RESULTADO] Hubo un error al guardar el producto.");
        }
        
        System.out.println("\n--- LISTA ACTUAL DE PRODUCTOS EN BD ---");
        List<Producto> lista = dao.listar();
        for (Producto p : lista) {
            System.out.println(p.getCodigoSKU() + " - " + p.getDescripcion() + " - S/." + p.getPrecioUnitario());
        }
    }
}