package View;

import Controller.ControladorCliente;
import Controller.ControladorConsumo;
import javax.swing.*;
import java.awt.*;

public class Marcoprincipal extends JFrame {
    private ControladorCliente controladorCliente;
    private ControladorConsumo controladorConsumo;

    private mGestiondeclientes clientPanel;
    private Gestionregistrador registradorPanel;
    private Facturacion invoicePanel; // Panel para la generación de facturas
    private AnalisisConsumoPanel analisisConsumoPanel; // Nuevo panel para el análisis de consumo

    public Marcoprincipal() {
        // Inicializar controladores
        controladorCliente = new ControladorCliente();
        controladorConsumo = new ControladorConsumo();

        // ** Datos de prueba para que la GUI no esté vacía al inicio **
        // Creamos algunos registradores y clientes para que puedas probar
        controladorCliente.mAgregarRegistrador("REG001", "Calle 10 # 20-30", "Bogota");
        controladorCliente.mAgregarRegistrador("REG002", "Carrera 5 # 15-25", "Medellin");
        controladorCliente.mAgregarCliente("CLI001", "CC", "cli1@example.com", "Av 30 # 40-50", controladorCliente.mObtenerRegistradorPorId("REG001"));
        controladorCliente.mAgregarCliente("CLI002", "NIT", "cli2@example.com", "Cl 7 # 1-2", controladorCliente.mObtenerRegistradorPorId("REG002"));


        // Configuración básica de la ventana
        setTitle("Aplicación de Gestión de Clientes y Consumos");
        setSize(1000, 700); // Tamaño inicial
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        // Crear un JTabbedPane para organizar las diferentes secciones
        JTabbedPane tabbedPane = new JTabbedPane();

        // Inicializar los paneles
        // CORRECTO: Pasa 'this' al constructor de mGestiondeclientes
        clientPanel = new mGestiondeclientes(controladorCliente, this);
        registradorPanel = new Gestionregistrador(controladorCliente, this);
        invoicePanel = new Facturacion(controladorConsumo, controladorCliente);
        analisisConsumoPanel = new AnalisisConsumoPanel(controladorConsumo, controladorCliente); // Inicializa el nuevo panel de análisis

        // Añadir los paneles al JTabbedPane
        tabbedPane.addTab("Gestión de Clientes", clientPanel);
        tabbedPane.addTab("Gestión de Registradores", registradorPanel);
        tabbedPane.addTab("Generar Facturas", invoicePanel);
        tabbedPane.addTab("Análisis de Consumos", analisisConsumoPanel); // Añade el nuevo panel

        // Añadir el JTabbedPane a la ventana principal
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Método para refrescar el JComboBox de registradores en el panel de clientes
    public void mActualizarRegistradorCliente() { // Renombrado para mayor claridad
        if (clientPanel != null) {
            clientPanel.mActualizarregistrador();
        }
    }

    // Método para refrescar la lista de clientes en el panel de análisis de consumo
    public void mActualizarClientesAnalisis() { // Renombrado
        if (analisisConsumoPanel != null) {
            analisisConsumoPanel.mRefrescarClientes(); // Llama al método del panel de análisis
        }
    }

    // Método para hacer visible la ventana
    public void mIniciardor() { // Mantenido el nombre original
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }

    // Nuevo método para refrescar la lista de clientes en mGestiondeclientes
    public void mRefrescarListaClientes() { // Renombrado para mayor claridad
        if (clientPanel != null) {
            clientPanel.mCargarcliente();
        }
    }
}