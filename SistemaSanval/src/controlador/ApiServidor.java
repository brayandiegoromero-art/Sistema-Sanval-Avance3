package controlador;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import dao.ProductoDAO;
import dao.ProductoDAOImpl;

public class ApiServidor {
    
    public static void main(String[] args) throws Exception {
        // Crea un servidor web local en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/Comprar", new ComprarHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("[SISTEMA SANVAL] Servidor API iniciado en el puerto 8080.");
        System.out.println("[SISTEMA SANVAL] Esperando compras desde la pagina web...");
    }

    static class ComprarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Permite que el HTML se conecte sin bloqueos de seguridad (CORS)
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.sendResponseHeaders(204, -1);
                return;
            }

            // Captura el SKU que envía la web desde el botón HTML
            String query = t.getRequestURI().getQuery();
            String sku = query.split("=")[1];

            // Llama al DAO para restar 1 unidad en MySQL
            ProductoDAO dao = new ProductoDAOImpl();
            boolean exito = dao.reducirStock(sku, 1);

            // Responde a la página web si fue exitoso o no
            String respuesta = exito ? "OK" : "ERROR";
            t.sendResponseHeaders(exito ? 200 : 500, respuesta.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(respuesta.getBytes());
            os.close();
            
            if(exito) {
                System.out.println("[VENTA EXITOSA] Se descontó 1 unidad de " + sku + " en MySQL.");
            } else {
                System.err.println("[ERROR] No se pudo descontar stock de " + sku + ". Verifica que exista en la tabla Inventario.");
            }
        }
    }
}