package Controller;

import Model.Cliente;
import Model.Registrador;

public class ControladorCliente {
    private Cliente[] clientes = new Cliente[100];
    private Registrador[] registradores = new Registrador[100];
    private int contadorClientes = 0;
    private int contadorRegistradores = 0;

    // ... (tus métodos existentes mAgregarCliente, mEditarCliente, etc.) ...

    public void mAgregarCliente(String id, String tipo, String email, String direccion, Registrador registrador) {
        if (contadorClientes < clientes.length) {
            Cliente nuevoCliente = new Cliente(id, tipo, email, direccion, registrador);
            clientes[contadorClientes] = nuevoCliente;
            contadorClientes++;
            System.out.println("Cliente agregado con registrador obligatorio.");
        } else {
            System.out.println("No se pueden agregar más clientes.");
        }
    }

    public void mEditarCliente(String id, String nuevoEmail, String nuevaDireccion) {
        for (int i = 0; i < contadorClientes; i++) {
            if (clientes[i].mObtenerId().equals(id)) {
                clientes[i].mActualizarEmail(nuevoEmail);
                clientes[i].mActualizarDireccion(nuevaDireccion);
                System.out.println("Cliente actualizado correctamente.");
                return;
            }
        }
        System.out.println("Cliente no encontrado.");
    }

    public void mAgregarRegistrador(String id, String direccion, String ciudad) {
        if (contadorRegistradores < registradores.length) {
            Registrador nuevoRegistrador = new Registrador(id, direccion, ciudad);
            registradores[contadorRegistradores] = nuevoRegistrador;
            contadorRegistradores++;
            System.out.println("Registrador agregado.");
        } else {
            System.out.println("No se pueden agregar más registradores.");
        }
    }

    public void mEditarRegistrador(String id, String nuevaDireccion, String nuevaCiudad) {
        for (int i = 0; i < contadorRegistradores; i++) {
            if (registradores[i].mObtenerId().equals(id)) {
                registradores[i].mActualizarDireccion(nuevaDireccion);
                registradores[i].mActualizarCiudad(nuevaCiudad);
                System.out.println("Registrador actualizado correctamente.");
                return;
            }
        }
        System.out.println("Registrador no encontrado.");
    }

    public Cliente mObtenerClientePorId(String id) {
        for (int i = 0; i < contadorClientes; i++) {
            if (clientes[i].mObtenerId().equals(id)) {
                return clientes[i];
            }
        }
        return null;
    }

    // *** NUEVOS MÉTODOS PARA LA GUI ***

    public Registrador mObtenerRegistradorPorId(String id) {
        for (int i = 0; i < contadorRegistradores; i++) {
            if (registradores[i].mObtenerId().equals(id)) {
                return registradores[i];
            }
        }
        return null;
    }

    public Registrador[] mObtenerTodosLosRegistradores() {
        // Devuelve una copia del arreglo para evitar modificaciones externas directas
        Registrador[] regs = new Registrador[contadorRegistradores];
        System.arraycopy(registradores, 0, regs, 0, contadorRegistradores);
        return regs;
    }

    public Cliente[] mObtenerTodosLosClientes() {
        // Devuelve una copia del arreglo
        Cliente[] clis = new Cliente[contadorClientes];
        System.arraycopy(clientes, 0, clis, 0, contadorClientes);
        return clis;
    }



    /**
     * Elimina un cliente por su ID.
     * @param idCliente El ID del cliente a eliminar.
     * @return true si el cliente fue eliminado, false si no se encontró.
     */
    public boolean mEliminarCliente(String idCliente) {
        for (int i = 0; i < contadorClientes; i++) {
            if (clientes[i].mObtenerId().equals(idCliente)) {
                // Mover los elementos restantes para llenar el espacio
                for (int j = i; j < contadorClientes - 1; j++) {
                    clientes[j] = clientes[j + 1];
                }
                clientes[contadorClientes - 1] = null; // Limpiar la última posición
                contadorClientes--;
                System.out.println("Cliente " + idCliente + " eliminado correctamente.");
                return true;
            }
        }
        System.out.println("Cliente con ID " + idCliente + " no encontrado para eliminar.");
        return false;
    }

    /**
     * Elimina un registrador por su ID.
     *
     * @param idRegistrador El ID del registrador a eliminar.
     * @return true si el registrador fue eliminado, false si no se encontró.
     */
    public boolean mEliminarRegistrador(String idRegistrador) {
        for (int i = 0; i < contadorRegistradores; i++) {
            if (registradores[i].mObtenerId().equals(idRegistrador)) {
                // Mover los elementos restantes para llenar el espacio
                for (int j = i; j < contadorRegistradores - 1; j++) {
                    registradores[j] = registradores[j + 1];
                }
                registradores[contadorRegistradores - 1] = null; // Limpiar la última posición
                contadorRegistradores--;
                System.out.println("Registrador " + idRegistrador + " eliminado correctamente.");

                
                for (int k = 0; k < contadorClientes; k++) {
                    if (clientes[k].mObtenerRegistrador() != null && clientes[k].mObtenerRegistrador().mObtenerId().equals(idRegistrador)) {
                        clientes[k].mAsignarRegistrador(null); // O podrías asignar un "Registrador por defecto"
                        System.out.println("Registrador desasignado del cliente: " + clientes[k].mObtenerId());
                    }
                }
                return true;
            }
        }
        System.out.println("Registrador con ID " + idRegistrador + " no encontrado para eliminar.");
        return false;
    }
}