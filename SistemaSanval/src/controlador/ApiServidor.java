package controlador;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.MensajeContactoDAO;
import dao.MensajeContactoDAOImpl;
import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import modelo.MensajeContacto;

public class ApiServidor {
    
    public static void main(String[] args) throws Exception {
        // Asegura que exista la tabla donde se guardan los mensajes de Contacto
        new MensajeContactoDAOImpl().crearTablaSiNoExiste();

        // Crea un servidor web local en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/Comprar", new ComprarHandler());
        server.createContext("/Contacto", new ContactoHandler());   // recibe el formulario de la web
        server.createContext("/Mensajes", new MensajesHandler());   // panel para que la empresa vea los pedidos
        server.setExecutor(null);
        server.start();
        System.out.println("[SISTEMA SANVAL] Servidor API iniciado en el puerto 8080.");
        System.out.println("[SISTEMA SANVAL] Esperando compras desde la pagina web...");
        System.out.println("[SISTEMA SANVAL] Mensajes de clientes: http://localhost:8080/Mensajes");
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

    /* ==========================================================
       CONTACTO: recibe los mensajes del formulario y los guarda
       en la tabla MensajeContacto de MySQL.
       ========================================================== */
    static class ContactoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.sendResponseHeaders(204, -1);
                return;
            }
            if (!t.getRequestMethod().equalsIgnoreCase("POST")) {
                responder(t, 405, "text/plain", "Metodo no permitido");
                return;
            }

            // Lee el cuerpo enviado por la web (formato nombre=...&correo=...)
            String cuerpo = new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> datos = leerFormulario(cuerpo);

            String nombre = recortar(datos.get("nombre"), 100);
            String correo = recortar(datos.get("correo"), 100);
            String asunto = recortar(datos.get("asunto"), 150);
            String mensaje = recortar(datos.get("mensaje"), 5000);

            // Validación básica en el servidor
            if (nombre.isEmpty() || asunto.isEmpty() || mensaje.isEmpty()
                    || !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                responder(t, 400, "text/plain", "DATOS_INVALIDOS");
                return;
            }

            MensajeContactoDAO dao = new MensajeContactoDAOImpl();
            boolean exito = dao.guardar(new MensajeContacto(nombre, correo, asunto, mensaje));

            responder(t, exito ? 200 : 500, "text/plain", exito ? "OK" : "ERROR");

            if (exito) {
                System.out.println("[NUEVO MENSAJE] " + nombre + " <" + correo + "> - " + asunto);
            } else {
                System.err.println("[ERROR] No se pudo guardar el mensaje de " + correo + ". Verifica la conexion a MySQL.");
            }
        }
    }

    /* ==========================================================
       MENSAJES: panel web para que la empresa revise los
       pedidos/consultas. Abrir: http://localhost:8080/Mensajes
       Marcar atendido: /Mensajes?atender=ID
       ========================================================== */
    static class MensajesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            MensajeContactoDAO dao = new MensajeContactoDAOImpl();

            // Acción para marcar un mensaje como atendido o pendiente
            Map<String, String> params = leerFormulario(t.getRequestURI().getRawQuery());
            try {
                if (params.containsKey("atender")) {
                    dao.cambiarEstado(Integer.parseInt(params.get("atender")), "Atendido");
                    redirigir(t);
                    return;
                }
                if (params.containsKey("pendiente")) {
                    dao.cambiarEstado(Integer.parseInt(params.get("pendiente")), "Pendiente");
                    redirigir(t);
                    return;
                }
            } catch (NumberFormatException e) {
                // id inválido: se ignora y se muestra la lista
            }

            List<MensajeContacto> lista = dao.listar();
            long pendientes = lista.stream().filter(m -> "Pendiente".equals(m.getEstado())).count();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>")
                .append("<meta http-equiv='refresh' content='60'>")
                .append("<title>Sanval | Mensajes de clientes</title><style>")
                .append("body{font-family:Poppins,Arial,sans-serif;background:#111;color:#eee;margin:0;padding:30px 5%;}")
                .append("h1{margin:0 0 5px;} h1 span{color:#cc0000;} .sub{color:#aaa;margin-bottom:25px;}")
                .append("table{width:100%;border-collapse:collapse;background:#1b1d22;}")
                .append("th,td{padding:12px;border-bottom:1px solid #333;text-align:left;vertical-align:top;}")
                .append("th{background:#cc0000;color:#fff;} td.msg{white-space:pre-wrap;max-width:420px;}")
                .append(".Pendiente{color:#ffb300;font-weight:bold;} .Atendido{color:#4caf50;font-weight:bold;}")
                .append("a.btn{color:#fff;background:#333;padding:6px 10px;border-radius:4px;text-decoration:none;font-size:13px;}")
                .append("a.btn:hover{background:#cc0000;} a{color:#ff6666;}")
                .append("</style></head><body>")
                .append("<h1>SANVAL <span>S.A.C.</span> - Mensajes de clientes</h1>")
                .append("<div class='sub'>Total: ").append(lista.size())
                .append(" | Pendientes: <b class='Pendiente'>").append(pendientes).append("</b>")
                .append(" | La página se actualiza cada 60 segundos.</div>");

            if (lista.isEmpty()) {
                html.append("<p>Aún no hay mensajes. Cuando un cliente envíe el formulario de Contacto aparecerá aquí.</p>");
            } else {
                html.append("<table><tr><th>#</th><th>Fecha</th><th>Cliente</th><th>Asunto</th><th>Mensaje</th><th>Estado</th><th>Acción</th></tr>");
                for (MensajeContacto m : lista) {
                    String correo = escapar(m.getCorreo());
                    html.append("<tr>")
                        .append("<td>").append(m.getIdMensaje()).append("</td>")
                        .append("<td>").append(m.getFechaEnvio() == null ? "-" : formato.format(m.getFechaEnvio())).append("</td>")
                        .append("<td><b>").append(escapar(m.getNombre())).append("</b><br>")
                        .append("<a href='mailto:").append(correo).append("'>").append(correo).append("</a></td>")
                        .append("<td>").append(escapar(m.getAsunto())).append("</td>")
                        .append("<td class='msg'>").append(escapar(m.getMensaje())).append("</td>")
                        .append("<td class='").append(escapar(m.getEstado())).append("'>").append(escapar(m.getEstado())).append("</td>")
                        .append("<td>");
                    if ("Pendiente".equals(m.getEstado())) {
                        html.append("<a class='btn' href='/Mensajes?atender=").append(m.getIdMensaje()).append("'>Marcar atendido</a>");
                    } else {
                        html.append("<a class='btn' href='/Mensajes?pendiente=").append(m.getIdMensaje()).append("'>Volver a pendiente</a>");
                    }
                    html.append("</td></tr>");
                }
                html.append("</table>");
            }
            html.append("</body></html>");

            responder(t, 200, "text/html; charset=UTF-8", html.toString());
        }
    }

    /* ------------------ Utilidades ------------------ */

    private static Map<String, String> leerFormulario(String texto) {
        Map<String, String> mapa = new HashMap<>();
        if (texto == null || texto.isEmpty()) return mapa;
        for (String par : texto.split("&")) {
            int i = par.indexOf('=');
            if (i <= 0) continue;
            String clave = URLDecoder.decode(par.substring(0, i), StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(par.substring(i + 1), StandardCharsets.UTF_8);
            mapa.put(clave, valor);
        }
        return mapa;
    }

    private static String recortar(String valor, int max) {
        if (valor == null) return "";
        valor = valor.trim();
        return valor.length() > max ? valor.substring(0, max) : valor;
    }

    // Evita que un mensaje con código HTML rompa o ataque el panel
    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static void responder(HttpExchange t, int codigo, String tipo, String cuerpo) throws IOException {
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().set("Content-Type", tipo);
        t.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirigir(HttpExchange t) throws IOException {
        t.getResponseHeaders().set("Location", "/Mensajes");
        t.sendResponseHeaders(303, -1);
        t.close();
    }
}
