package View;

import java.util.Scanner;
import Controller.ControladorCliente;
import Controller.ControladorConsumo;
import Model.Cliente;
import Model.Registrador;

public class Vista {
    public static void mIniciar() {
        Scanner sc = new Scanner(System.in);
        ControladorCliente controladorCliente = new ControladorCliente();
        ControladorConsumo controladorConsumo = new ControladorConsumo();

        while (true) {
            System.out.println("===== MENÚ PRINCIPAL =====");
            System.out.println("1. Crear Cliente (con registrador)");
            System.out.println("2. Editar Cliente");
            System.out.println("3. Crear Registrador");
            System.out.println("4. Editar Registrador");
            System.out.println("5. Cargar consumos de todos los clientes automáticamente");
            System.out.println("6. Cargar consumos de un cliente automáticamente");
            System.out.println("7. Cambiar consumo de una hora específica");
            System.out.println("8. Generar factura en PDF");
            System.out.println("9. Hallar consumo mínimo de un cliente");
            System.out.println("10. Hallar consumo máximo de un cliente");
            System.out.println("11. Hallar consumo por franjas de un cliente");
            System.out.println("12. Hallar consumo por días de un cliente");
            System.out.println("13. Calcular factura de un cliente");
            System.out.println("0. Salir");

            System.out.print("Seleccione una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese ID del cliente: ");
                    String id = sc.nextLine();
                    System.out.print("Ingrese tipo de identificación: ");
                    String tipo = sc.nextLine();
                    System.out.print("Ingrese email: ");
                    String email = sc.nextLine();
                    System.out.print("Ingrese dirección: ");
                    String direccion = sc.nextLine();

                    System.out.print("Ingrese ID del registrador: ");
                    String idReg = sc.nextLine();
                    System.out.print("Ingrese dirección del registrador: ");
                    String dirReg = sc.nextLine();
                    System.out.print("Ingrese ciudad del registrador: ");
                    String ciudadReg = sc.nextLine();

                    Registrador registrador = new Registrador(idReg, dirReg, ciudadReg);
                    controladorCliente.mAgregarCliente(id, tipo, email, direccion, registrador);
                    break;

                case 2:
                    System.out.print("Ingrese ID del cliente a editar: ");
                    String idEditar = sc.nextLine();
                    System.out.print("Ingrese nuevo email: ");
                    String nuevoEmail = sc.nextLine();
                    System.out.print("Ingrese nueva dirección: ");
                    String nuevaDireccion = sc.nextLine();
                    controladorCliente.mEditarCliente(idEditar, nuevoEmail, nuevaDireccion);
                    break;

                case 3:
                    System.out.print("Ingrese ID del registrador: ");
                    String idRegNuevo = sc.nextLine();
                    System.out.print("Ingrese dirección: ");
                    String dirRegNuevo = sc.nextLine();
                    System.out.print("Ingrese ciudad: ");
                    String ciudadRegNuevo = sc.nextLine();
                    controladorCliente.mAgregarRegistrador(idRegNuevo, dirRegNuevo, ciudadRegNuevo);
                    break;

                case 4:
                    System.out.print("Ingrese ID del registrador a editar: ");
                    String idEditarReg = sc.nextLine();
                    System.out.print("Ingrese nueva dirección: ");
                    String nuevaDirReg = sc.nextLine();
                    System.out.print("Ingrese nueva ciudad: ");
                    String nuevaCiudadReg = sc.nextLine();
                    controladorCliente.mEditarRegistrador(idEditarReg, nuevaDirReg, nuevaCiudadReg);
                    break;

                case 5:
                case 6:
                    System.out.print("Ingrese el mes (1-12): ");
                    int mes = sc.nextInt();
                    sc.nextLine();
                    int diasDelMes = controladorConsumo.mObtenerDiasDelMes(mes);

                    if (opcion == 5) {
                        controladorConsumo.mCargarConsumosAutomaticos(null, mes, diasDelMes);
                    } else {
                        System.out.print("Ingrese ID del cliente: ");
                        String idCliente = sc.nextLine();
                        Cliente clienteConsumo = controladorCliente.mObtenerClientePorId(idCliente);

                        if (clienteConsumo == null || clienteConsumo.mObtenerRegistrador() == null) {
                            System.out.println("Error: Cliente sin registrador, no se pueden cargar consumos.");
                            break;
                        }

                        controladorConsumo.mCargarConsumoCliente(clienteConsumo, mes);
                    }
                    break;

                case 7:
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesCambio = sc.nextInt();
                    System.out.print("Ingrese el día (1-31): ");
                    int diaCambio = sc.nextInt();
                    System.out.print("Ingrese la hora (0-23): ");
                    int horaCambio = sc.nextInt();
                    System.out.print("Ingrese el nuevo consumo en KW: ");
                    int nuevoConsumo = sc.nextInt();
                    controladorConsumo.mCambiarConsumo(null, mesCambio, diaCambio, horaCambio, nuevoConsumo);
                    break;

                case 8:
                    System.out.print("Ingrese ID del cliente: ");
                    String idFactura = sc.nextLine();
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesFactura = sc.nextInt();
                    Cliente clienteFactura = controladorCliente.mObtenerClientePorId(idFactura);
                    controladorConsumo.mGenerarFacturaPDF(clienteFactura, mesFactura);
                    break;

                case 9:
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesMinimo = sc.nextInt();
                    System.out.println("Consumo mínimo del mes " + mesMinimo + ": " + controladorConsumo.mHallarConsumoMinimo(mesMinimo) + " KW");
                    break;
                    
                case 10:
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesMaximo = sc.nextInt();
                    System.out.println("Consumo máximo del mes " + mesMaximo + ": " + controladorConsumo.mHallarConsumoMaximo(mesMaximo) + " KW");
                    break;

                    case 11:
                    System.out.print("Ingrese ID del cliente: ");
                    String idClienteFranjas = sc.nextLine();
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesFranjas = sc.nextInt();
                    Cliente clienteFranjas = controladorCliente.mObtenerClientePorId(idClienteFranjas);

                    if (clienteFranjas == null) {
                        System.out.println("Error: Cliente no encontrado.");
                    } else {
                        controladorConsumo.mHallarConsumoPorFranjas(clienteFranjas, mesFranjas);
                    }
                    break;

                    case 12:
                    System.out.print("Ingrese ID del cliente: ");
                    String idClienteDias = sc.nextLine();
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesDias = sc.nextInt();
                    Cliente clienteDias = controladorCliente.mObtenerClientePorId(idClienteDias);

                    if (clienteDias == null) {
                        System.out.println("Error: Cliente no encontrado.");
                    } else {
                        controladorConsumo.mHallarConsumoPorDias(clienteDias, mesDias);
                    }
                    break;

                case 13:
                    System.out.print("Ingrese el mes (1-12): ");
                    int mesFacturaTotal = sc.nextInt();
                    controladorConsumo.mCalcularFacturaPorMes(mesFacturaTotal);
                    break;

                case 0:
                    System.out.println("Saliendo del programa...");
                    return;

                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        }
    }
}