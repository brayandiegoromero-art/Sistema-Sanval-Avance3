package modelo;

public class Producto {
    
    private int idProducto;
    private String codigoSKU;
    private String descripcion;
    private int idCategoria;
    private String talla;
    private String color;
    private double precioUnitario;
    private String estado;
    private int stock;

    public Producto() {
    }

    public Producto(String codigoSKU, String descripcion, String talla, int stock) {
        this.codigoSKU = codigoSKU;
        this.descripcion = descripcion;
        this.talla = talla;
        this.stock = stock;
        this.idCategoria = 1;
        this.color = "Negro";
        this.precioUnitario = 45.00;
        this.estado = "Activo";
    }

    public Producto(int idProducto, String codigoSKU, String descripcion, int idCategoria, String talla, String color, double precioUnitario, String estado) {
        this.idProducto = idProducto;
        this.codigoSKU = codigoSKU;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.talla = talla;
        this.color = color;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getCodigoSKU() { return codigoSKU; }
    public void setCodigoSKU(String codigoSKU) { this.codigoSKU = codigoSKU; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
