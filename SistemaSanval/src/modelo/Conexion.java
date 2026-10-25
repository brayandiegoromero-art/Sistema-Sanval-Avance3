package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/SanvalDB?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[OK] Conexion exitosa a la base de datos SanvalDB.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[ERROR] Error de conexion: " + e.getMessage());
        }
        return conexion;
    }
    
    // Método rápido para probar la conexión directamente ejecutando este archivo
    public static void main(String[] args) {
        conectar();
    }
}