package com.example.itexttest;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class AcroMaker {
	static private double x_cord=0,y_cord=0;
	static private String dest_path = "/storage/sdcard0/external_SD/AcroMaker.pdf";
	
	static public String PutAcros(String[] cord, String path) throws IOException, DocumentException{
		
		String[] args;
		PdfReader reader = new PdfReader(path);
        FileOutputStream os = new FileOutputStream(dest_path);
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        
        PdfPTable table = new PdfPTable(2);
        int n = reader.getNumberOfPages();
        PdfImportedPage page;
        for (int i = 1; i <= n; i++) {
        page = writer.getImportedPage(reader, i);
        	table.addCell(Image.getInstance(page));
        }
        document.add(table);
		for(String tmp_str:cord){
			args=tmp_str.split(":");
			switch(args[2]){
				case "sign" : {
			      
			        PdfFormField field = PdfFormField.createSignature(writer);
			        field.setWidget(new Rectangle( Float.parseFloat(args[0]), Float.parseFloat(args[1]),Float.parseFloat(args[0])+72, Float.parseFloat(args[1])+48), PdfAnnotation.HIGHLIGHT_INVERT);
			        field.setFieldName(""+args[3]);
			        field.setFlags(PdfAnnotation.FLAGS_PRINT);
			        field.setPage();
			        field.setMKBorderColor(BaseColor.BLACK);
			        field.setMKBackgroundColor(BaseColor.WHITE);
			        PdfAppearance tp = PdfAppearance.createAppearance(writer, 72, 48);
			        tp.rectangle(0.5f, 0.5f, 71.5f, 47.5f);
			        tp.stroke();
			        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
			        writer.addAnnotation(field);
			        
			        
			        
				}
				default:{
					System.err.println("Unknown type.");
				}
			}
		}
		
		document.close();
		return dest_path;
	}
}