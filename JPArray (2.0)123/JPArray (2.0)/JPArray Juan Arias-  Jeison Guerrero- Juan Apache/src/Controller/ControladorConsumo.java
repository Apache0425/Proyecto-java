package Controller;

import Model.Consumo;
import Model.Cliente;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Element; // Importar Element para alineación

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class ControladorConsumo {
    // ELIMINAR O COMENTAR ESTA LÍNEA: private Consumo[][][] consumos = new Consumo[12][31][24]; // Ya no es necesaria aquí

    public int mObtenerDiasDelMes(int mes) {
        switch (mes) {
            case 2:
                return 28; // Simplificado, no considera años bisiestos
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    // Método modificado para cargar consumos al Cliente ESPECÍFICO
    public void mCargarConsumoCliente(Cliente cliente, int mes) {
        if (cliente == null || cliente.mObtenerRegistrador() == null) {
            System.out.println("Error: El cliente no existe o no tiene un registrador asignado. No se pueden cargar consumos.");
            return;
        }
        int diasDelMes = mObtenerDiasDelMes(mes);
        Random random = new Random();

        // Limpiar consumos existentes para el mes antes de cargar nuevos
        for (int dia = 0; dia < diasDelMes; dia++) {
            for (int hora = 0; hora < 24; hora++) {
                cliente.mEstablecerConsumo(mes, dia + 1, hora, null); // Establecer a null para limpiar
            }
        }

        for (int dia = 0; dia < diasDelMes; dia++) {
            for (int hora = 0; hora < 24; hora++) {
                int consumoKW = 5 + random.nextInt(20); // Consumo entre 5 y 24 KW por hora
                Consumo nuevoConsumo = new Consumo(hora, consumoKW);
                cliente.mEstablecerConsumo(mes, dia + 1, hora, nuevoConsumo);
            }
        }
        System.out.println("Consumos cargados para cliente " + cliente.mObtenerId() + " en el mes " + mes + ".");
    }

    public String mGenerarFacturaPDF(Cliente cliente, int mes) {
        if (cliente == null) {
            System.err.println("Error: Cliente no proporcionado para generar factura.");
            return null;
        }

        String fileName = "factura_" + cliente.mObtenerId() + "_mes_" + mes + ".pdf";
        String filePath = "facturas_clientes" + File.separator + fileName;

        File directory = new File("facturas_clientes");
        if (!directory.exists()) {
            directory.mkdirs(); // Crea el directorio si no existe
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Fuentes personalizadas
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLUE);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font detailHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY);


            // Título de la Factura (como en 1_Mes6.pdf)
            Paragraph mainTitle = new Paragraph("===== FACTURA DE ENERGÍA =====", titleFont);
            mainTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(mainTitle);
            document.add(new Paragraph("\n")); // Salto de línea

            // Información del Cliente
            document.add(new Paragraph("ID Cliente: " + cliente.mObtenerId(), textFont));
            document.add(new Paragraph("Tipo de Identificación: " + cliente.mObtenerTipoIdentificacion(), textFont));
            document.add(new Paragraph("Email: " + cliente.mObtenerEmail(), textFont));
            document.add(new Paragraph("Dirección: " + cliente.mObtenerDireccion(), textFont));
            document.add(new Paragraph("Registrador Asignado: " + (cliente.mObtenerRegistrador() != null ? cliente.mObtenerRegistrador().mObtenerId() : "N/A"), textFont));
            document.add(new Paragraph("\n"));

            // Mes de Facturación
            document.add(new Paragraph("Mes de consumo: " + mes, subTitleFont));
            document.add(new Paragraph("\n"));

            // Resumen de Consumo y Precio Total
            double totalConsumoKW = 0;
            double totalPrecioCOP = 0;
            int diasDelMes = mObtenerDiasDelMes(mes);

            // Calcular totales
            for (int d = 0; d < diasDelMes; d++) {
                for (int h = 0; h < 24; h++) {
                    Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h); // Usar dia + 1
                    if (consumo != null) {
                        totalConsumoKW += consumo.mObtenerConsumoKW();
                        totalPrecioCOP += consumo.mObtenerPrecio();
                    }
                }
            }
            // total a pagar, alineado a la derecha como en 1_Mes6.pdf
            Paragraph totalPagar = new Paragraph("Total a pagar: " + totalPrecioCOP + " COP", subTitleFont);
            totalPagar.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(totalPagar);
            document.add(new Paragraph("\n"));

            // ** SECCIÓN PARA EL CONSUMO Y PRECIO POR DÍA **
            document.add(new Paragraph("Consumo y Precio por día:", detailHeaderFont)); // Título ligeramente modificado
            document.add(new Paragraph("\n"));

            for (int d = 0; d < diasDelMes; d++) {
                double totalConsumoDia = 0;
                double totalPrecioDia = 0; // Agregamos una variable para el precio total del día
                for (int h = 0; h < 24; h++) {
                    Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h); // Usar dia + 1
                    if (consumo != null) {
                        totalConsumoDia += consumo.mObtenerConsumoKW();
                        totalPrecioDia += consumo.mObtenerPrecio(); // Sumamos el precio de cada hora
                    }
                }
                // Añadimos el precio al párrafo
                document.add(new Paragraph("Día " + (d + 1) + ": " + totalConsumoDia + " KW - " + totalPrecioDia + " COP", textFont));
            }


        } catch (DocumentException | FileNotFoundException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return filePath;
    }

    // Los métodos mHallarConsumoMinimo, mHallarConsumoMaximo, mHallarConsumoPorFranjas,
    // mHallarConsumoPorDias, y mCalcularFacturaPorMes permanecen igual
    // ya que no están directamente involucrados en la generación del PDF,
    // sino que son para la lógica interna o para la Vista de consola.

    public void mHallarConsumoMinimo(Cliente cliente, int mes) {
        if (cliente == null) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        int minConsumo = Integer.MAX_VALUE;
        int diaMin = -1;
        int horaMin = -1;
        int diasDelMes = mObtenerDiasDelMes(mes);

        for (int d = 0; d < diasDelMes; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h);
                if (consumo != null && consumo.mObtenerConsumoKW() < minConsumo) {
                    minConsumo = consumo.mObtenerConsumoKW();
                    diaMin = d + 1;
                    horaMin = h;
                }
            }
        }
        if (diaMin != -1) {
            System.out.println("Consumo mínimo para cliente " + cliente.mObtenerId() + " en mes " + mes + ": " + minConsumo + " KW (Día: " + diaMin + ", Hora: " + horaMin + ")");
        } else {
            System.out.println("No se encontraron consumos para el cliente " + cliente.mObtenerId() + " en el mes " + mes + ".");
        }
    }

    public void mHallarConsumoMaximo(Cliente cliente, int mes) {
        if (cliente == null) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        int maxConsumo = Integer.MIN_VALUE;
        int diaMax = -1;
        int horaMax = -1;
        int diasDelMes = mObtenerDiasDelMes(mes);

        for (int d = 0; d < diasDelMes; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h);
                if (consumo != null && consumo.mObtenerConsumoKW() > maxConsumo) {
                    maxConsumo = consumo.mObtenerConsumoKW();
                    diaMax = d + 1;
                    horaMax = h;
                }
            }
        }
        if (diaMax != -1) {
            System.out.println("Consumo máximo para cliente " + cliente.mObtenerId() + " en mes " + mes + ": " + maxConsumo + " KW (Día: " + diaMax + ", Hora: " + horaMax + ")");
        } else {
            System.out.println("No se encontraron consumos para el cliente " + cliente.mObtenerId() + " en el mes " + mes + ".");
        }
    }

    public void mHallarConsumoPorFranjas(Cliente cliente, int mes) {
        if (cliente == null) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        double franja1 = 0; // 00:00 - 06:59
        double franja2 = 0; // 07:00 - 17:59
        double franja3 = 0; // 18:00 - 23:59

        int diasDelMes = mObtenerDiasDelMes(mes);
        for (int d = 0; d < diasDelMes; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo c = cliente.mObtenerConsumoEspecifico(mes, d + 1, h);
                if (c != null) {
                    if (h >= 0 && h < 7) { // 0-6
                        franja1 += c.mObtenerConsumoKW();
                    } else if (h >= 7 && h < 18) { // 7-17
                        franja2 += c.mObtenerConsumoKW();
                    } else { // 18-23
                        franja3 += c.mObtenerConsumoKW();
                    }
                }
            }
        }
        System.out.println("Consumo por franjas para cliente " + cliente.mObtenerId() + " en mes " + mes + ":");
        System.out.println("Franja 0-6h: " + franja1 + " KW");
        System.out.println("Franja 7-17h: " + franja2 + " KW");
        System.out.println("Franja 18-23h: " + franja3 + " KW");
    }

    public void mHallarConsumoPorDias(Cliente cliente, int mes) {
        if (cliente == null) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        System.out.println("Consumo por días para cliente " + cliente.mObtenerId() + " en mes " + mes + ":");
        int diasDelMes = mObtenerDiasDelMes(mes);
        for (int d = 0; d < diasDelMes; d++) {
            int totalDia = 0;
            for (int h = 0; h < 24; h++) {
                Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h);
                if (consumo != null) {
                    totalDia += consumo.mObtenerConsumoKW();
                }
            }
            System.out.println("Día " + (d + 1) + ": " + totalDia + " KW");
        }
    }

    public void mCalcularFacturaPorMes(Cliente cliente, int mes) {
        if (cliente == null) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        double totalConsumoKW = 0;
        double totalPrecioCOP = 0;
        int diasDelMes = mObtenerDiasDelMes(mes);

        for (int d = 0; d < diasDelMes; d++) {
            for (int h = 0; h < 24; h++) {
                Consumo consumo = cliente.mObtenerConsumoEspecifico(mes, d + 1, h);
                if (consumo != null) {
                    totalConsumoKW += consumo.mObtenerConsumoKW();
                    totalPrecioCOP += consumo.mObtenerPrecio();
                }
            }
        }
        System.out.println("Factura para cliente " + cliente.mObtenerId() + " en mes " + mes + ":");
        System.out.println("Consumo Total: " + totalConsumoKW + " KW");
        System.out.println("Precio Total: " + totalPrecioCOP + " COP");
    }
}