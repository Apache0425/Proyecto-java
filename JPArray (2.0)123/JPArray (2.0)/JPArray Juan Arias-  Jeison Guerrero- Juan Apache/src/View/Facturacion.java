package View;

import Controller.ControladorCliente;
import Controller.ControladorConsumo;
import Model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Facturacion extends JPanel { // Nombre del archivo: Facturacion.java
    private ControladorConsumo controladorConsumo;
    private ControladorCliente controladorCliente;

    private JComboBox<String> clientComboBox;
    private JComboBox<Integer> monthComboBox;
    private JButton generatePdfButton;
    private JButton loadConsumosButton; // Botón para cargar consumos aleatorios

    public Facturacion(ControladorConsumo controladorConsumo, ControladorCliente controladorCliente) {
        this.controladorConsumo = controladorConsumo;
        this.controladorCliente = controladorCliente;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Generación de Facturas PDF"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Seleccionar Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; clientComboBox = new JComboBox<>(); mActualizarcliente(); add(clientComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Seleccionar Mes:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        monthComboBox.setSelectedItem(1); // Default to January
        add(monthComboBox, gbc);

        // Botón para cargar consumos
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; // Ocupa dos columnas
        loadConsumosButton = new JButton("Cargar Consumos Aleatorios (para el mes)");
        add(loadConsumosButton, gbc);

        // Botón para generar PDF
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; // Ocupa dos columnas
        generatePdfButton = new JButton("Generar Factura PDF");
        add(generatePdfButton, gbc);

        // --- Acciones de los Botones ---
        loadConsumosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mCargarConsumosParaClienteSeleccionado();
            }
        });

        generatePdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mGenerarFactura();
            }
        });
    }

    // Método para actualizar el JComboBox de clientes (anteriormente mActualizarcliente)
    // Cambié el nombre del método para ser más descriptivo y evitar confusiones con mGestiondeclientes.mActualizarcliente()
    public void updateClientComboBox() {
        clientComboBox.removeAllItems();
        Cliente[] clientes = controladorCliente.mObtenerTodosLosClientes();
        if (clientes != null) {
            for (Cliente cli : clientes) {
                clientComboBox.addItem(cli.mObtenerId());
            }
        }
    }

    // Mantengo el nombre mActualizarcliente para compatibilidad con Marcoprincipal
    // pero internamente Facturacion usará updateClientComboBox.
    public void mActualizarcliente() {
        updateClientComboBox();
    }


    private void mCargarConsumosParaClienteSeleccionado() {
        String selectedClientId = (String) clientComboBox.getSelectedItem();
        Integer selectedMonth = (Integer) monthComboBox.getSelectedItem();

        if (selectedClientId == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un mes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = controladorCliente.mObtenerClientePorId(selectedClientId);
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controladorConsumo.mCargarConsumoCliente(cliente, selectedMonth);
        JOptionPane.showMessageDialog(this, "Consumos aleatorios cargados para " + cliente.mObtenerId() + " en el mes " + selectedMonth + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mGenerarFactura() {
        try {
            String selectedClientId = (String) clientComboBox.getSelectedItem();
            Integer selectedMonth = (Integer) monthComboBox.getSelectedItem();

            if (selectedClientId == null || selectedMonth == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un mes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente cliente = controladorCliente.mObtenerClientePorId(selectedClientId);
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Llamamos a mGenerarFacturaPDF con el cliente y el mes
            String rutaPDF = controladorConsumo.mGenerarFacturaPDF(cliente, selectedMonth);

            if (rutaPDF != null) {
                JOptionPane.showMessageDialog(this, "Factura PDF generada correctamente en: " + rutaPDF, "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Intenta abrir la carpeta donde se guardó el PDF
                try {
                    File pdfFile = new File(rutaPDF);
                    File parentDir = pdfFile.getParentFile();
                    if (parentDir != null && parentDir.exists() && parentDir.isDirectory()) {
                         Desktop.getDesktop().open(parentDir);
                    } else {
                         // Si no se puede abrir la carpeta, intentar abrir el archivo directamente
                         Desktop.getDesktop().open(pdfFile);
                    }
                } catch (Exception ex) {
                    System.err.println("No se pudo abrir el directorio o el archivo automáticamente: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo generar la factura PDF. Verifique los datos y la consola para errores.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al generar la factura PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}