package com.parqueos.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class GeneradorPDF {
    // Metodo para generar un PDF
    public static void generarPDF(String nombreArchivo, String contenido) throws IOException, DocumentException {
        // Crear un documento
        Document document = new Document();
        // Crear una instancia de PdfWriter
        PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
        // Abrir el documento
        document.open();
        // Agregar contenido al documento
        document.add(new Paragraph(contenido));
        // Cerrar el documento
        document.close();
    }
}