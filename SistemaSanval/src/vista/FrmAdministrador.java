package vista;

import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Producto;

public class FrmAdministrador extends JFrame {

    // Paleta de colores corporativa Sanval S.A.C.
    private static final Color COLOR_FONDO = new Color(244, 246, 249);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_HEADER = new Color(17, 17, 17);
    private static final Color COLOR_PRIMARIO = new Color(204, 0, 0);
    private static final Color COLOR_PRIMARIO_HOVER = new Color(165, 0, 0);
    private static final Color COLOR_SECUNDARIO = new Color(37, 99, 235);
    private static final Color COLOR_SECUNDARIO_HOVER = new Color(29, 78, 216);
    private static final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private static final Color COLOR_PELIGRO_HOVER = new Color(185, 28, 28);
    private static final Color COLOR_NEUTRO = new Color(100, 116, 139);
    private static final Color COLOR_NEUTRO_HOVER = new Color(71, 85, 105);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_SUAVE = new Color(100, 116, 139);
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_FILA_ALT = new Color(248, 250, 252);
    private static final Color COLOR_SELECCION = new Color(254, 226, 226);

    // Fuentes
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_TABLA_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    // Componentes del formulario
    private JTextField txtSku;
    private JTextField txtDescripcion;
    private JTextField txtTalla;
    private JTextField txtStock;

    // Botones de acción
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Tabla e indicadores
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumenInventario;

    // Acceso a datos
    private final ProductoDAO productoDAO;

    public FrmAdministrador() {
        this.productoDAO = new ProductoDAOImpl();
        configurarVentana();
        inicializarComponentes();
        cargarTablaProductos();
    }

    private void configurarVentana() {
        setTitle("Sanval S.A.C. | Panel Administrativo de Inventario");
        setSize(1020, 700);
        setMinimumSize(new Dimension(880, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));
    }

    private void inicializarComponentes() {
        // 1. ENCABEZADO SUPERIOR
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(COLOR_HEADER);
        panelHeader.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel panelTitulos = new JPanel(new GridBagLayout());
        panelTitulos.setOpaque(false);
        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 0;
        gbcHeader.anchor = GridBagConstraints.WEST;

        JLabel lblMarca = new JLabel("<html><span style='color:#FFFFFF;'>SANVAL</span> <span style='color:#CC0000;'>S.A.C.</span> &mdash; Panel Administrativo</html>");
        lblMarca.setFont(FUENTE_TITULO);
        panelTitulos.add(lblMarca, gbcHeader);

        gbcHeader.gridy = 1;
        gbcHeader.insets = new Insets(4, 0, 0, 0);
        JLabel lblSubtitulo = new JLabel("Gestión de Inventario y Control de Stock en Tiempo Real (MySQL)");
        lblSubtitulo.setFont(FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(new Color(180, 185, 195));
        panelTitulos.add(lblSubtitulo, gbcHeader);

        lblResumenInventario = new JLabel("Cargando inventario...");
        lblResumenInventario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResumenInventario.setForeground(Color.WHITE);
        lblResumenInventario.setBackground(new Color(38, 38, 38));
        lblResumenInventario.setOpaque(true);
        lblResumenInventario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(65, 65, 65), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        panelHeader.add(panelTitulos, BorderLayout.WEST);
        panelHeader.add(lblResumenInventario, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. CONTENEDOR CENTRAL (Formulario + Tabla)
        JPanel panelContenido = new JPanel(new BorderLayout(0, 18));
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(new EmptyBorder(20, 24, 24, 24));

        // 2.1 Tarjeta de Formulario y Botones
        JPanel cardFormulario = new JPanel(new BorderLayout(0, 16));
        cardFormulario.setBackground(COLOR_PANEL);
        cardFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTituloForm = new JLabel("Datos del Producto");
        lblTituloForm.setFont(FUENTE_SECCION);
        lblTituloForm.setForeground(COLOR_TEXTO);
        cardFormulario.add(lblTituloForm, BorderLayout.NORTH);

        // Campos de texto en grilla
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        txtSku = crearCampoTexto("Ej. GOR-08");
        txtDescripcion = crearCampoTexto("Ej. Gorro de Cuero Artesanal");
        txtTalla = crearCampoTexto("Ej. S, M, L o Única");
        txtStock = crearCampoTexto("Ej. 25");

        // Fila 1: Código SKU y Descripción
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panelCampos.add(crearEtiqueta("Código SKU:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        panelCampos.add(txtSku, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(6, 20, 6, 8);
        panelCampos.add(crearEtiqueta("Descripción:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(6, 8, 6, 8);
        panelCampos.add(txtDescripcion, gbc);

        // Fila 2: Talla y Stock
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelCampos.add(crearEtiqueta("Talla:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        panelCampos.add(txtTalla, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(6, 20, 6, 8);
        panelCampos.add(crearEtiqueta("Stock:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(6, 8, 6, 8);
        panelCampos.add(txtStock, gbc);

        cardFormulario.add(panelCampos, BorderLayout.CENTER);

        // Barra de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(6, 0, 0, 0));

        btnRegistrar = crearBoton("Registrar Producto", COLOR_PRIMARIO, COLOR_PRIMARIO_HOVER);
        btnActualizar = crearBoton("Actualizar Stock", COLOR_SECUNDARIO, COLOR_SECUNDARIO_HOVER);
        btnEliminar = crearBoton("Eliminar Producto", COLOR_PELIGRO, COLOR_PELIGRO_HOVER);
        btnLimpiar = crearBoton("Limpiar Campos", COLOR_NEUTRO, COLOR_NEUTRO_HOVER);

        btnRegistrar.addActionListener(e -> accionRegistrarProducto());
        btnActualizar.addActionListener(e -> accionActualizarStock());
        btnEliminar.addActionListener(e -> accionEliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        cardFormulario.add(panelBotones, BorderLayout.SOUTH);
        panelContenido.add(cardFormulario, BorderLayout.NORTH);

        // 2.2 Tarjeta Inferior con JTable
        JPanel cardTabla = new JPanel(new BorderLayout(0, 12));
        cardTabla.setBackground(COLOR_PANEL);
        cardTabla.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(18, 24, 20, 24)
        ));

        JPanel panelEncabezadoTabla = new JPanel(new BorderLayout());
        panelEncabezadoTabla.setOpaque(false);

        JLabel lblTituloTabla = new JLabel("Listado de Productos Registrados");
        lblTituloTabla.setFont(FUENTE_SECCION);
        lblTituloTabla.setForeground(COLOR_TEXTO);

        JLabel lblAyudaTabla = new JLabel("Seleccione una fila para cargar sus datos automáticamente en el formulario");
        lblAyudaTabla.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblAyudaTabla.setForeground(COLOR_TEXTO_SUAVE);

        panelEncabezadoTabla.add(lblTituloTabla, BorderLayout.WEST);
        panelEncabezadoTabla.add(lblAyudaTabla, BorderLayout.EAST);
        cardTabla.add(panelEncabezadoTabla, BorderLayout.NORTH);

        String[] columnas = {"ID", "Código SKU", "Descripción", "Talla", "Stock", "Precio Unit.", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        estilizarTabla();

        // Al seleccionar una fila en la tabla, poblar los campos del formulario
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1) {
                int fila = tablaProductos.getSelectedRow();
                txtSku.setText( String.valueOf(modeloTabla.getValueAt(fila, 1)));
                txtDescripcion.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
                txtTalla.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                txtStock.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(new LineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        cardTabla.add(scrollTabla, BorderLayout.CENTER);
        panelContenido.add(cardTabla, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }

    private JTextField crearCampoTexto(String tooltip) {
        JTextField txt = new JTextField();
        txt.setFont(FUENTE_INPUT);
        txt.setForeground(COLOR_TEXTO);
        txt.setBackground(new Color(250, 251, 252));
        txt.setToolTipText(tooltip);
        txt.setPreferredSize(new Dimension(180, 38));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return txt;
    }

    private JButton crearBoton(String texto, Color colorBase, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorBase);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 40));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorBase);
            }
        });
        return btn;
    }

    private void estilizarTabla() {
        tablaProductos.setFont(FUENTE_TABLA);
        tablaProductos.setForeground(COLOR_TEXTO);
        tablaProductos.setRowHeight(34);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setSelectionBackground(COLOR_SELECCION);
        tablaProductos.setSelectionForeground(COLOR_HEADER);
        tablaProductos.setShowHorizontalLines(true);
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setGridColor(COLOR_BORDE);
        tablaProductos.setFillsViewportHeight(true);

        // Estilo del encabezado de la tabla
        JTableHeader header = tablaProductos.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COLOR_HEADER);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(FUENTE_TABLA_HEADER);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(column == 0 || column == 3 || column == 4 || column == 5 || column == 6
                        ? SwingConstants.CENTER : SwingConstants.LEFT);
                return lbl;
            }
        });

        // Renderizador de celdas con colores alternados y resaltado de bajo stock
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBorder(new EmptyBorder(0, 12, 0, 12));

                if (!isSelected) {
                    cell.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_FILA_ALT);
                    cell.setForeground(COLOR_TEXTO);
                }

                // Alineación por columna
                if (column == 0 || column == 3 || column == 4 || column == 5 || column == 6) {
                    cell.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    cell.setHorizontalAlignment(SwingConstants.LEFT);
                }

                // Destacar columna de Stock
                if (column == 4 && value != null) {
                    try {
                        int cant = Integer.parseInt(value.toString());
                        cell.setFont(FUENTE_TABLA.deriveFont(Font.BOLD));
                        if (!isSelected) {
                            if (cant <= 10) {
                                cell.setForeground(COLOR_PELIGRO);
                            } else {
                                cell.setForeground(new Color(22, 163, 74));
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                } else {
                    cell.setFont(FUENTE_TABLA);
                }
                return cell;
            }
        };

        for (int i = 0; i < tablaProductos.getColumnCount(); i++) {
            tablaProductos.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Anchos proporcionales de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(300);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(100);
    }

    // ==================== LÓGICA CRUD Y EVENTOS ====================

    private void cargarTablaProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoDAO.listar();
        int stockTotal = 0;

        for (Producto p : productos) {
            stockTotal += p.getStock();
            Object[] fila = {
                p.getIdProducto(),
                p.getCodigoSKU(),
                p.getDescripcion(),
                p.getTalla(),
                p.getStock(),
                String.format("S/ %.2f", p.getPrecioUnitario()),
                p.getEstado()
            };
            modeloTabla.addRow(fila);
        }

        lblResumenInventario.setText(String.format("Productos: %d   |   Stock Total: %d uds.", productos.size(), stockTotal));
    }

    private void accionRegistrarProducto() {
        String sku = txtSku.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String talla = txtTalla.getText().trim();
        String stockTexto = txtStock.getText().trim();

        if (sku.isEmpty() || descripcion.isEmpty() || talla.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos (Código SKU, Descripción, Talla y Stock).",
                    "Campos Incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockTexto);
            if (stock < 0) {
                JOptionPane.showMessageDialog(this,
                        "El stock no puede ser un número negativo.",
                        "Stock Inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El campo Stock debe contener un número entero válido.",
                    "Formato Inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto nuevoProducto = new Producto(sku.toUpperCase(), descripcion, talla, stock);
        boolean exito = productoDAO.registrar(nuevoProducto);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Producto registrado exitosamente con el SKU: " + sku.toUpperCase(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTablaProductos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el producto. Verifique que el Código SKU no esté duplicado y que MySQL esté activo.",
                    "Error al Registrar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionActualizarStock() {
        String sku = txtSku.getText().trim();
        String stockTexto = txtStock.getText().trim();

        if (sku.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto de la tabla o ingrese el Código SKU y el nuevo Stock.",
                    "Datos Requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nuevoStock;
        try {
            nuevoStock = Integer.parseInt(stockTexto);
            if (nuevoStock < 0) {
                JOptionPane.showMessageDialog(this,
                        "El stock no puede ser negativo.",
                        "Stock Inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese una cantidad numérica entera válida para el Stock.",
                    "Formato Inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito = productoDAO.actualizarStock(sku, nuevoStock);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Stock actualizado correctamente para el producto " + sku.toUpperCase() + " (" + nuevoStock + " unidades).",
                    "Stock Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTablaProductos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ningún producto con el Código SKU: " + sku,
                    "Error al Actualizar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarProducto() {
        String sku = txtSku.getText().trim();

        if (sku.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto de la tabla o ingrese su Código SKU para eliminarlo.",
                    "SKU Requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el producto con SKU \"" + sku.toUpperCase() + "\" del inventario?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        boolean exito = productoDAO.eliminar(sku);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "El producto " + sku.toUpperCase() + " fue eliminado correctamente.",
                    "Producto Eliminado",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTablaProductos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el producto. Verifique que el Código SKU exista en la base de datos.",
                    "Error al Eliminar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtSku.setText("");
        txtDescripcion.setText("");
        txtTalla.setText("");
        txtStock.setText("");
        tablaProductos.clearSelection();
        txtSku.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            FrmAdministrador frame = new FrmAdministrador();
            frame.setVisible(true);
        });
    }
}
