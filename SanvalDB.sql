-- 1. CREACIÓN DE LA BASE DE DATOS
DROP DATABASE IF EXISTS SanvalDB;
CREATE DATABASE SanvalDB;
USE SanvalDB;

-- 2. TABLAS DE SEGURIDAD (Para el Login y Auditoría)
CREATE TABLE Rol (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    nombreRol VARCHAR(50) NOT NULL
);

CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombreCompleto VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    idRol INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activo',
    FOREIGN KEY (idRol) REFERENCES Rol(idRol)
);

-- 3. TABLAS DE CATÁLOGO
CREATE TABLE Categoria (
    idCategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombreCategoria VARCHAR(50) NOT NULL
);

CREATE TABLE Proveedor (
    idProveedor INT AUTO_INCREMENT PRIMARY KEY,
    razonSocial VARCHAR(100) NOT NULL,
    ruc VARCHAR(11) UNIQUE NOT NULL,
    telefono VARCHAR(15)
);

CREATE TABLE Producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    codigoSKU VARCHAR(20) UNIQUE NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    idCategoria INT NOT NULL,
    talla VARCHAR(10) NOT NULL,
    color VARCHAR(30) NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activo',
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria)
);

-- 4. TABLAS DE INVENTARIO Y MOVIMIENTOS
CREATE TABLE Inventario (
    idInventario INT AUTO_INCREMENT PRIMARY KEY,
    idProducto INT NOT NULL UNIQUE,
    cantidadActual INT NOT NULL DEFAULT 0,
    ubicacion VARCHAR(50),
    ultimaActualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

CREATE TABLE Movimiento (
    idMovimiento INT AUTO_INCREMENT PRIMARY KEY,
    idProducto INT NOT NULL,
    idUsuario INT NOT NULL,
    idProveedor INT NULL,
    tipoMovimiento ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL,
    cantidad INT NOT NULL,
    fechaMovimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idProveedor) REFERENCES Proveedor(idProveedor)
);

-- 5. INSERCIÓN DE DATOS REALES (MÁS DE 10 PRODUCTOS)

-- Insertar Roles y Usuarios
INSERT INTO Rol (nombreRol) VALUES ('Administrador'), ('Almacenero');
INSERT INTO Usuario (nombreCompleto, correo, password, idRol) VALUES 
('Diego Romero', 'admin@sanval.com', '123456', 1),
('Luis Arias', 'almacen@sanval.com', '123456', 2);

-- Insertar Categorías y Proveedores
INSERT INTO Categoria (nombreCategoria) VALUES 
('Gorros Clásicos'), ('Gorros Deportivos'), ('Sombreros'), ('Accesorios de Cuero');
INSERT INTO Proveedor (razonSocial, ruc, telefono) VALUES 
('Curtiembre del Sur S.A.', '20123456789', '01-555-1234'),
('Taller Interno Sanval', '20987654321', '01-555-9876');

-- Insertar 12 Productos Reales
INSERT INTO Producto (codigoSKU, descripcion, idCategoria, talla, color, precioUnitario) VALUES 
('GOR-01', 'Gorro de Cuero Clásico', 1, 'L', 'Negro', 45.00),
('GOR-02', 'Gorro Vintage', 1, 'M', 'Marrón Oscuro', 50.00),
('GOR-03', 'Gorro Deportivo Visera', 2, 'S', 'Negro', 35.00),
('GOR-04', 'Gorro Aviador con Orejeras', 1, 'L', 'Marrón Claro', 65.00),
('GOR-05', 'Boina Francesa de Cuero', 1, 'Única', 'Rojo Vino', 40.00),
('GOR-06', 'Gorro Beanie Cuero Suave', 1, 'M', 'Gris', 42.00),
('SOM-01', 'Sombrero Ala Ancha Cuero', 3, 'L', 'Negro', 85.00),
('SOM-02', 'Sombrero Texano Cuero', 3, 'M', 'Caramelo', 95.00),
('SOM-03', 'Sombrero Pescador Cuero', 3, 'S', 'Marrón', 55.00),
('ACC-01', 'Guantes de Cuero Térmicos', 4, 'L', 'Negro', 30.00),
('ACC-02', 'Billetera Minimalista Cuero', 4, 'Única', 'Marrón Oscuro', 25.00),
('ACC-03', 'Cinturón de Cuero Clásico', 4, 'M', 'Negro', 35.00);

-- Inicializar el Inventario para todos los productos
INSERT INTO Inventario (idProducto, cantidadActual, ubicacion) VALUES 
(1, 50, 'Estante A1'),
(2, 15, 'Estante A1'),
(3, 120, 'Estante B1'),
(4, 8, 'Estante A2'),
(5, 25, 'Estante A2'),
(6, 40, 'Estante A3'),
(7, 10, 'Vitrina 1'),
(8, 5, 'Vitrina 1'),
(9, 30, 'Estante B2'),
(10, 60, 'Cajón C1'),
(11, 45, 'Cajón C2'),
(12, 80, 'Estante C1');