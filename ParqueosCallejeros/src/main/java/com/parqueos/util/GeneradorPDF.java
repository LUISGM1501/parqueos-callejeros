package com.parqueos.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class GeneradorPDF {
    public static void generarPDF(String nombreArchivo, String contenido) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
        document.open();
        document.add(new Paragraph(contenido));
        document.close();
    }
}