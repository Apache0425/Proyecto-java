package View;

import Controller.ControladorCliente;
import Model.Registrador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gestionregistrador extends JPanel {
    private ControladorCliente controladorCliente;
    private Marcoprincipal mainFrame; // Referencia a Marcoprincipal para notificar actualizaciones

    // Componentes del formulario de registrador
    private JTextField idField, addressField, cityField;
    private JButton createRegistradorButton, editRegistradorButton, refreshButton;
    private JButton deleteRegistradorButton;

    // Componentes para mostrar la lista de registradores
    private JTable registradorTable;
    private DefaultTableModel tableModel;

    public Gestionregistrador(ControladorCliente controladorCliente, Marcoprincipal mainFrame) { // Constructor modificado
        this.controladorCliente = controladorCliente;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // --- Panel de Formulario para Crear/Editar Registrador ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Registradores"));

        idField = new JTextField(15);
        addressField = new JTextField(15);
        cityField = new JTextField(15);

        formPanel.add(new JLabel("ID Registrador:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Dirección:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Ciudad:"));
        formPanel.add(cityField);

        // --- Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createRegistradorButton = new JButton("Crear Registrador");
        editRegistradorButton = new JButton("Editar Registrador");
        refreshButton = new JButton("Refrescar Registradores");
        deleteRegistradorButton = new JButton("Eliminar Registrador"); // Inicializa el botón de eliminar

        buttonPanel.add(createRegistradorButton);
        buttonPanel.add(editRegistradorButton);
        buttonPanel.add(deleteRegistradorButton); // Añade el botón de eliminar
        buttonPanel.add(refreshButton);

        // --- Configuración de la Tabla de Registradores ---
        tableModel = new DefaultTableModel(new Object[]{"ID", "Dirección", "Ciudad"}, 0);
        registradorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(registradorTable);

        // Añadir paneles al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Cargar registradores iniciales en la tabla
        loadRegistradoresToTable();

        // --- Acciones de los Botones ---
        createRegistradorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mCrearRegistrador(); // Nombre original si existía o uno lógico
            }
        });

        editRegistradorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEditarRegistrador(); // Nombre original si existía o uno lógico
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRegistradoresToTable();
            }
        });

        deleteRegistradorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEliminarRegistrador(); // Nombre original si existía o uno lógico
            }
        });

        // Listener para seleccionar registrador de la tabla y llenar el formulario
        registradorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && registradorTable.getSelectedRow() != -1) {
                int selectedRow = registradorTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                addressField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                cityField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
    }

    private void mCrearRegistrador() {
        String id = idField.getText();
        String address = addressField.getText();
        String city = cityField.getText();

        if (id.isEmpty() || address.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error de Creación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controladorCliente.mObtenerRegistradorPorId(id) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un registrador con este ID.", "Error de Creación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controladorCliente.mAgregarRegistrador(id, address, city);
        JOptionPane.showMessageDialog(this, "Registrador creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        mLimpiarCampos(); // Nombre original
        loadRegistradoresToTable();
        if (mainFrame != null) {
            mainFrame.mActualizarRegistradorCliente(); // Notifica a mGestiondeclientes
            mainFrame.mActualizarClientesAnalisis(); // Notifica al panel de análisis
            mainFrame.mRefrescarListaClientes(); // Notifica a mGestiondeclientes para que actualice la tabla de clientes
        }
    }

    private void mEditarRegistrador() {
        String id = idField.getText();
        String nuevaDireccion = addressField.getText();
        String nuevaCiudad = cityField.getText();

        if (id.isEmpty() || nuevaDireccion.isEmpty() || nuevaCiudad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del registrador y los nuevos datos.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Registrador registradorAEditar = controladorCliente.mObtenerRegistradorPorId(id);
        if (registradorAEditar == null) {
            JOptionPane.showMessageDialog(this, "Registrador con ID " + id + " no encontrado.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controladorCliente.mEditarRegistrador(id, nuevaDireccion, nuevaCiudad);
        JOptionPane.showMessageDialog(this, "Registrador actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        mLimpiarCampos(); // Nombre original
        loadRegistradoresToTable(); // Actualizar la tabla de registradores
        if (mainFrame != null) {
            mainFrame.mActualizarRegistradorCliente(); // Notifica al panel de clientes
            mainFrame.mActualizarClientesAnalisis(); // Notifica al panel de análisis
            mainFrame.mRefrescarListaClientes();
        }
    }

    private void mEliminarRegistrador() {
        String id = idField.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registrador de la tabla o ingrese su ID para eliminar.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el registrador con ID: " + id + "? Esto también desvinculará este registrador de cualquier cliente asociado.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controladorCliente.mEliminarRegistrador(id)) {
                JOptionPane.showMessageDialog(this, "Registrador eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mLimpiarCampos(); // Nombre original
                loadRegistradoresToTable();
                if (mainFrame != null) {
                    mainFrame.mActualizarRegistradorCliente(); // Notifica al panel de clientes
                    mainFrame.mActualizarClientesAnalisis(); // Notifica al panel de análisis
                    mainFrame.mRefrescarListaClientes();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Registrador con ID " + id + " no encontrado para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mLimpiarCampos() { // Nombre original
        idField.setText("");
        addressField.setText("");
        cityField.setText("");
    }

    private void loadRegistradoresToTable() {
        tableModel.setRowCount(0); // Limpiar la tabla
        Registrador[] registradores = controladorCliente.mObtenerTodosLosRegistradores();
        if (registradores == null) return;

        for (Registrador reg : registradores) {
            tableModel.addRow(new Object[]{
                reg.mObtenerId(),
                reg.mObtenerDireccion(),
                reg.mObtenerCiudad()
            });
        }
    }
}