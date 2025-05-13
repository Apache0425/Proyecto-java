package Controller;

import Model.Cliente;
import Model.Registrador;

public class ControladorCliente {
    private Cliente[] clientes = new Cliente[100];
    private Registrador[] registradores = new Registrador[100];
    private int contadorClientes = 0;
    private int contadorRegistradores = 0;

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
            registradores[contadorRegistradores] = new Registrador(id, direccion, ciudad);
            contadorRegistradores++;
            System.out.println("Registrador agregado correctamente.");
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

    public void mMostrarClientes() {
        System.out.println("Lista de clientes:");
        for (int i = 0; i < contadorClientes; i++) {
            System.out.println(clientes[i].toString());
        }
    }

    public void mMostrarRegistradores() {
        System.out.println("Lista de registradores:");
        for (int i = 0; i < contadorRegistradores; i++) {
            System.out.println(registradores[i].toString());
        }
    }
}