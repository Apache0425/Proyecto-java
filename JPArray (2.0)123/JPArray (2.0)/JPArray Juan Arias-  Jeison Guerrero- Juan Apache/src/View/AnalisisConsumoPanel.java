package View;

import Controller.ControladorCliente;
import Controller.ControladorConsumo;
import Model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

public class AnalisisConsumoPanel extends JPanel {
    private ControladorConsumo controladorConsumo;
    private ControladorCliente controladorCliente;

    private JComboBox<String> clientComboBox;
    private JComboBox<Integer> monthComboBox;
    private JButton analizarButton;
    private JTextField consumoMinimoField;
    private JTextField consumoMaximoField;
    private JButton cargarConsumosButton;

    public AnalisisConsumoPanel(ControladorConsumo controladorConsumo, ControladorCliente controladorCliente) {
        this.controladorConsumo = controladorConsumo;
        this.controladorCliente = controladorCliente;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Análisis de Consumo (Mínimo/Máximo)"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los componentes se expandirán horizontalmente

        // Componentes para seleccionar cliente y mes
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Seleccionar Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        clientComboBox = new JComboBox<>();
        mActualizarListaClientes();
        add(clientComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Seleccionar Mes (1-12):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        monthComboBox = new JComboBox<>(IntStream.rangeClosed(1, 12).boxed().toArray(Integer[]::new));
        add(monthComboBox, gbc);

        // Botón para cargar consumos (opcional, pero útil para testing)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        cargarConsumosButton = new JButton("Cargar Consumos para el Mes Seleccionado");
        add(cargarConsumosButton, gbc);

        // Botón para analizar
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; // Ocupa 2 columnas
        analizarButton = new JButton("Analizar Consumo");
        add(analizarButton, gbc);

        // Campos para mostrar resultados
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; add(new JLabel("Consumo Mínimo (KW):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        consumoMinimoField = new JTextField(25); // Aumentar tamaño para mostrar más info
        consumoMinimoField.setEditable(false); // No editable por el usuario
        add(consumoMinimoField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(new JLabel("Consumo Máximo (KW):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        consumoMaximoField = new JTextField(25); // Aumentar tamaño para mostrar más info
        consumoMaximoField.setEditable(false); // No editable por el usuario
        add(consumoMaximoField, gbc);

        // --- Listeners de los botones ---
        cargarConsumosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mCargarConsumosParaClienteYMesSeleccionado();
            }
        });

        analizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mAnalizarConsumo();
            }
        });
    }

    private void mActualizarListaClientes() {
        clientComboBox.removeAllItems();
        Cliente[] clientes = controladorCliente.mObtenerTodosLosClientes();
        if (clientes != null) {
            for (Cliente cliente : clientes) {
                clientComboBox.addItem(cliente.mObtenerId());
            }
        }
    }

    // Método público para ser llamado desde Marcoprincipal si se añade/elimina un cliente
    public void mRefrescarClientes() {
        mActualizarListaClientes();
    }

    private void mCargarConsumosParaClienteYMesSeleccionado() {
        String selectedClientId = (String) clientComboBox.getSelectedItem();
        Integer selectedMonth = (Integer) monthComboBox.getSelectedItem();

        if (selectedClientId == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un mes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = controladorCliente.mObtenerClientePorId(selectedClientId);
        if (cliente != null) {
            controladorConsumo.mCargarConsumoCliente(cliente, selectedMonth);
            JOptionPane.showMessageDialog(this, "Consumos cargados para el cliente " + selectedClientId + " en el mes " + selectedMonth + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mAnalizarConsumo() {
        String selectedClientId = (String) clientComboBox.getSelectedItem();
        Integer selectedMonth = (Integer) monthComboBox.getSelectedItem(); // Esto es un Integer

        if (selectedClientId == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente y un mes para analizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = controladorCliente.mObtenerClientePorId(selectedClientId);
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Aquí el 'selectedMonth' (Integer) se auto-desempaqueta a 'int'
        // para coincidir con la firma de los métodos en ControladorConsumo.
        int[] minData = controladorConsumo.mObtenerConsumoMinimoGUI(cliente, selectedMonth);
        int[] maxData = controladorConsumo.mObtenerConsumoMaximoGUI(cliente, selectedMonth);

        if (minData == null || maxData == null) {
            JOptionPane.showMessageDialog(this, "No se encontraron consumos para el cliente " + selectedClientId + " en el mes " + selectedMonth + ". Por favor, cargue los consumos primero.", "Información", JOptionPane.INFORMATION_MESSAGE);
            consumoMinimoField.setText("N/A");
            consumoMaximoField.setText("N/A");
        } else {
            // Formatear la salida para incluir día y hora
            consumoMinimoField.setText(String.format("%d KW (Día: %d, Hora: %d)", minData[0], minData[1], minData[2]));
            consumoMaximoField.setText(String.format("%d KW (Día: %d, Hora: %d)", maxData[0], maxData[1], maxData[2]));
        }
    }
}