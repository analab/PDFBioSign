package com.analab.pdfbiosign;

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
	
static void PutAcros( String src, String dest, String[] cord) throws IOException, DocumentException{
		
		String[] args;
		PdfReader reader = new PdfReader(src);
        
		for(String tmp_str:cord){
			args=tmp_str.split(":");
			switch(args[2]){
				case "sign" : {
			      PdfStamper stamper =new	PdfStamper(reader,new	FileOutputStream(dest));
			      PdfFormField field = PdfFormField.createSignature(stamper.getWriter());
							
			        field.setWidget(new Rectangle( Float.parseFloat(args[0]), Float.parseFloat(args[1]),Float.parseFloat(args[0])+72, Float.parseFloat(args[1])+48), PdfAnnotation.HIGHLIGHT_INVERT);
			        field.setFieldName(""+args[3]);
			        field.setFlags(PdfAnnotation.FLAGS_PRINT);
			        field.setPage();
			        field.setMKBorderColor(BaseColor.BLACK);
			        field.setMKBackgroundColor(BaseColor.WHITE);
			        PdfAppearance tp = PdfAppearance.createAppearance(stamper.getWriter(), 72, 48);
			        tp.rectangle(0.5f, 0.5f, 71.5f, 47.5f);
			        tp.stroke();
			        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
			        stamper.addAnnotation(field, 1);
					
					stamper.close();
			       break;
				}
				default:{
					System.err.println("Unknown type.");
				}
			}
		}
		
		//document.close();
	}
}