package Controller;

import Model.Consumo;
import Model.Cliente;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ControladorConsumo {
    private Consumo[][][] consumos = new Consumo[12][31][24];

    public int mObtenerDiasDelMes(int mes) {
        switch (mes) {
            case 2: return 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }

    public void mCargarConsumoCliente(Cliente cliente, int mes) {
        if (cliente.mObtenerRegistrador() == null) {
            System.out.println("Error: El cliente no tiene un registrador asignado. No se pueden cargar consumos.");
            return;
        }
        int diasDelMes = mObtenerDiasDelMes(mes);
        mCargarConsumosAutomaticos(mes, diasDelMes);
    }

    public void mCargarConsumosAutomaticos(int mes, int dias) {
        for (int d = 0; d < dias; d++) {
            for (int h = 0; h < 24; h++) {
                consumos[mes - 1][d][h] = new Consumo(h, (int) (Math.random() * 900) + 100);
            }
        }
        System.out.println("Consumos generados automáticamente para el mes " + mes);
    }

    public void mMostrarConsumos(int mes) {
        int dias = mObtenerDiasDelMes(mes);
        System.out.println("===== Consumos registrados para el mes " + mes + " =====");
        for (int d = 0; d < dias; d++) {
            System.out.print("Día " + (d + 1) + ": ");
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null) {
                    System.out.print(c.mObtenerConsumoKW() + "KW ");
                }
            }
            System.out.println();
        }
    }

    public void mCambiarConsumo(int mes, int dia, int hora, int nuevoConsumo) {
        if (dia >= 1 && dia <= mObtenerDiasDelMes(mes) && hora >= 0 && hora < 24) {
            consumos[mes - 1][dia - 1][hora] = new Consumo(hora, nuevoConsumo);
            System.out.println("Consumo actualizado para el mes " + mes + ", día " + dia + ", hora " + hora + ": " + nuevoConsumo + "KW");
        } else {
            System.out.println("Día u hora fuera de rango.");
        }
    }

    public void mGenerarFactura(Cliente cliente, int mes) {
        int total = mCalcularFacturaPorMes(mes);
        System.out.println("Factura generada para el cliente " + cliente.mObtenerId() + " del mes " + mes + " con un total de " + total + " COP.");
    }

    public int mHallarConsumoMinimo(int mes) {
        int minimo = Integer.MAX_VALUE;
        for (int d = 0; d < consumos[mes - 1].length; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null && c.mObtenerConsumoKW() < minimo) {
                    minimo = c.mObtenerConsumoKW();
                }
            }
        }
        return minimo;
    }

    public int mHallarConsumoMaximo(int mes) {
        int maximo = 0;
        for (int d = 0; d < consumos[mes - 1].length; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null && c.mObtenerConsumoKW() > maximo) {
                    maximo = c.mObtenerConsumoKW();
                }
            }
        }
        return maximo;
    }

    public int mCalcularFacturaPorMes(int mes) {
        int total = 0;
        for (int d = 0; d < consumos[mes - 1].length; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null) {
                    total += c.mObtenerPrecio();
                }
            }
        }
        System.out.println("Total de la factura del mes " + mes + ": " + total + " COP.");
        return total;
    }

    public void mHallarConsumoPorFranjas(Cliente cliente, int mes) {
        int madrugada = 0, mañana = 0, noche = 0;
        int diasDelMes = mObtenerDiasDelMes(mes);

        for (int d = 0; d < diasDelMes; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null) {
                    if (h >= 0 && h < 6) madrugada += c.mObtenerConsumoKW();
                    else if (h >= 7 && h < 17) mañana += c.mObtenerConsumoKW();
                    else if (h >= 18 && h < 23) noche += c.mObtenerConsumoKW();
                }
            }
        }

        System.out.println("Consumo por franjas horarias en el mes " + mes + ":");
        System.out.println(" Madrugada (0-6): " + madrugada + " KW");
        System.out.println(" Mañana (7-17): " + mañana + " KW");
        System.out.println(" Tarde (18-23): " + noche + " KW");
    }

    public void mHallarConsumoPorDias(Cliente cliente, int mes) {
        int diasDelMes = mObtenerDiasDelMes(mes);
        System.out.println("Consumo por días en el mes " + mes + ":");

        for (int d = 0; d < diasDelMes; d++) {
            int totalDia = 0;
            for (int h = 0; h < 24; h++) {
                Consumo c = consumos[mes - 1][d][h];
                if (c != null) {
                    totalDia += c.mObtenerConsumoKW();
                }
            }
            System.out.println(" Día " + (d + 1) + ": " + totalDia + " KW");
        }
    }

    //  MÉTODO ACTUALIZADO PARA GENERAR PDF 
    public void mGenerarFacturaPDF(Cliente cliente, int mes) {
        Document documento = new Document();
        try {
            String nombreArchivo = "Factura_" + cliente.mObtenerId() + "_Mes" + mes + ".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            documento.add(new Paragraph("===== FACTURA DE ENERGÍA ====="));
            documento.add(new Paragraph("ID Cliente: " + cliente.mObtenerId()));
            documento.add(new Paragraph("Mes de consumo: " + mes));
            documento.add(new Paragraph(" "));

            int total = mCalcularFacturaPorMes(mes);
            documento.add(new Paragraph("💵 Total a pagar: " + total + " COP"));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("📊 Consumo por día:"));
            int diasDelMes = mObtenerDiasDelMes(mes);
            for (int d = 0; d < diasDelMes; d++) {
                int totalDia = 0;
                for (int h = 0; h < 24; h++) {
                    Consumo c = consumos[mes - 1][d][h];
                    if (c != null) {
                        totalDia += c.mObtenerConsumoKW();
                    }
                }
                documento.add(new Paragraph("Día " + (d + 1) + ": " + totalDia + " KW"));
            }

            System.out.println(" Factura PDF generada correctamente: " + nombreArchivo);
        } catch (DocumentException | FileNotFoundException e) {
            System.out.println(" Error al generar el PDF: " + e.getMessage());
        } finally {
            documento.close();
        }
    }
}
