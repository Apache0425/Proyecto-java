package View;

import Controller.ControladorCliente;
import Model.Cliente;
import Model.Registrador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mGestiondeclientes extends JPanel {
    private ControladorCliente controladorCliente;
    private Marcoprincipal mainFrame; // Referencia a Marcoprincipal

    // Componentes del formulario de cliente
    private JTextField idField, typeField, emailField, addressField;
    private JComboBox<String> registradorComboBox;
    private JButton createClientButton, editClientButton, refreshClientButton;
    private JButton deleteClientButton;

    // Componentes para mostrar la lista de clientes
    private JTable clientTable;
    private DefaultTableModel tableModel;

    public mGestiondeclientes(ControladorCliente controladorCliente, Marcoprincipal mainFrame) {
        this.controladorCliente = controladorCliente;
        this.mainFrame = mainFrame;
        // ** CAMBIO CLAVE: Usamos BoxLayout para organizar los subpaneles verticalmente **
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Organiza los componentes de arriba a abajo

        // --- Panel de Formulario para Crear/Editar Cliente ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Clientes"));
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, formPanel.getPreferredSize().height)); // Asegura que el panel no crezca indefinidamente

        idField = new JTextField(15);
        typeField = new JTextField(15);
        emailField = new JTextField(15);
        addressField = new JTextField(15);
        registradorComboBox = new JComboBox<>();

        mActualizarregistrador(); // Poblar el JComboBox de registradores al inicio

        formPanel.add(new JLabel("ID Cliente:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Tipo Identificación:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Dirección:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Registrador:"));
        formPanel.add(registradorComboBox);

        // --- Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createClientButton = new JButton("Crear Cliente");
        editClientButton = new JButton("Editar Cliente");
        refreshClientButton = new JButton("Refrescar Clientes");
        deleteClientButton = new JButton("Eliminar Cliente");

        buttonPanel.add(createClientButton);
        buttonPanel.add(editClientButton);
        buttonPanel.add(deleteClientButton);
        buttonPanel.add(refreshClientButton);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height)); // Asegura que el panel no crezca indefinidamente

        // --- Configuración de la Tabla de Clientes ---
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tipo Identificación", "Email", "Dirección", "ID Registrador"}, 0);
        clientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        // Ajusta el tamaño preferido del JScrollPane para que la tabla sea visible
        scrollPane.setPreferredSize(new Dimension(800, 300)); // Puedes ajustar estos valores
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinea a la izquierda si el BoxLayout tiene más espacio

        // Añadir paneles al panel principal (usando BoxLayout)
        add(formPanel); // El formulario va primero
        add(buttonPanel); // Luego los botones
        add(scrollPane); // Finalmente la tabla

        // Cargar clientes iniciales en la tabla
        mCargarcliente();

        // --- Acciones de los Botones ---
        createClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mCrearcliente();
            }
        });

        editClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEditarcliente();
            }
        });

        refreshClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mCargarcliente();
            }
        });

        deleteClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEliminarcliente();
            }
        });

        // Listener para seleccionar cliente de la tabla y llenar el formulario
        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && clientTable.getSelectedRow() != -1) {
                int selectedRow = clientTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                typeField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                addressField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                String registradorId = tableModel.getValueAt(selectedRow, 4).toString();
                if (!registradorId.equals("N/A")) {
                    registradorComboBox.setSelectedItem(registradorId);
                } else {
                    registradorComboBox.setSelectedIndex(-1); // No selection
                }
            }
        });
    }

    // Método para actualizar el JComboBox de registradores (público para acceso desde Marcoprincipal)
    public void mActualizarregistrador() {
        registradorComboBox.removeAllItems();
        Registrador[] registradores = controladorCliente.mObtenerTodosLosRegistradores();
        if (registradores != null) {
            for (Registrador reg : registradores) {
                registradorComboBox.addItem(reg.mObtenerId());
            }
        }
    }

    private void mCrearcliente() {
        String id = idField.getText();
        String tipo = typeField.getText();
        String email = emailField.getText();
        String direccion = addressField.getText();
        String registradorId = (String) registradorComboBox.getSelectedItem();

        if (id.isEmpty() || tipo.isEmpty() || email.isEmpty() || direccion.isEmpty() || registradorId == null) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error de Creación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Registrador registrador = controladorCliente.mObtenerRegistradorPorId(registradorId);
        if (registrador == null) {
            JOptionPane.showMessageDialog(this, "Registrador seleccionado no válido.", "Error de Creación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controladorCliente.mAgregarCliente(id, tipo, email, direccion, registrador);
        JOptionPane.showMessageDialog(this, "Cliente creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        mDatoscliente();
        mCargarcliente();
        if (mainFrame != null) {
            mainFrame.mActualizarClientesAnalisis();
            mainFrame.mRefrescarListaClientes();
        }
    }

    private void mEditarcliente() {
        String id = idField.getText();
        String nuevoEmail = emailField.getText();
        String nuevaDireccion = addressField.getText();

        if (id.isEmpty() || nuevoEmail.isEmpty() || nuevaDireccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del cliente y los nuevos datos.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente clienteAEditar = controladorCliente.mObtenerClientePorId(id);
        if (clienteAEditar == null) {
            JOptionPane.showMessageDialog(this, "Cliente con ID " + id + " no encontrado.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controladorCliente.mEditarCliente(id, nuevoEmail, nuevaDireccion);
        JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        mDatoscliente();
        mCargarcliente();
        if (mainFrame != null) {
            mainFrame.mActualizarClientesAnalisis();
            mainFrame.mRefrescarListaClientes();
        }
    }

    private void mEliminarcliente() {
        String id = idField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del cliente a eliminar.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el cliente con ID: " + id + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controladorCliente.mEliminarCliente(id)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mDatoscliente();
                mCargarcliente();
                if (mainFrame != null) {
                    mainFrame.mActualizarClientesAnalisis();
                    mainFrame.mRefrescarListaClientes();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Cliente con ID " + id + " no encontrado para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mDatoscliente() {
        idField.setText("");
        typeField.setText("");
        emailField.setText("");
        addressField.setText("");
        if (registradorComboBox.getItemCount() > 0) {
            registradorComboBox.setSelectedIndex(0);
        }
    }

    public void mCargarcliente() {
        tableModel.setRowCount(0); // Limpiar la tabla
        Cliente[] clientes = controladorCliente.mObtenerTodosLosClientes();
        for (Cliente cliente : clientes) {
            String registradorId = (cliente.mObtenerRegistrador() != null) ? cliente.mObtenerRegistrador().mObtenerId() : "N/A";
            tableModel.addRow(new Object[]{
                cliente.mObtenerId(),
                cliente.mObtenerTipoIdentificacion(),
                cliente.mObtenerEmail(),
                cliente.mObtenerDireccion(),
                registradorId
            });
        }
    }
}