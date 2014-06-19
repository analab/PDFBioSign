package com.analab.pdfbiosign;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Cipher;
import android.annotation.SuppressLint;
import android.os.Environment;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.text.pdf.PdfSignatureAppearance.SignatureEvent;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.ProviderDigest;

@SuppressLint("TrulyRandom")
public class AcroMaker {

	public static void PutAcros( String src, String dest, String[] cord) throws IOException, DocumentException{
		
		String[] args;
		PdfReader reader = new PdfReader(src);
		FileOutputStream os = new	FileOutputStream(dest);
		Rectangle rect;
		PdfFormField field;
		Font font = new Font(FontFamily.HELVETICA, 18);
		 
		for(String tmp_str:cord){
			args=tmp_str.split(":");
			switch(args[2]){
				case "sign" : {
			      PdfStamper stamper =new	PdfStamper(reader,os);
			      field = PdfFormField.createSignature(stamper.getWriter());
							
			        field.setWidget(new Rectangle( Float.parseFloat(args[0]), Float.parseFloat(args[1]),Float.parseFloat(args[0])+(72*2), Float.parseFloat(args[1])+(48*2)), PdfAnnotation.HIGHLIGHT_INVERT);
			        field.setFieldName(""+args[3]);
			        field.setFlags(PdfAnnotation.FLAGS_PRINT);
			        field.setPage();
			        field.setMKBorderColor(BaseColor.BLACK);
			        field.setMKBackgroundColor(BaseColor.WHITE);
			        PdfAppearance tp = PdfAppearance.createAppearance(stamper.getWriter(), 72*2, 48*2);
			        tp.rectangle(0.5f, 0.5f, 71.5f*2, 47.5f*2);
			        tp.stroke();
			        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
			        stamper.addAnnotation(field, Integer.parseInt(args[4]));
					
					stamper.close();
			       break;
				}
				case "checkbox" : {
				      PdfStamper stamper =new	PdfStamper(reader,os);
				      PdfWriter writer = stamper.getWriter();
				      PdfContentByte canvas = writer.getDirectContent();
				      PdfAppearance[] onOff = new PdfAppearance[2];
				        onOff[0] = canvas.createAppearance(20, 20);
				        onOff[0].rectangle(1, 1, 18, 18);
				        onOff[0].stroke();
				        onOff[1] = canvas.createAppearance(20, 20);
				        onOff[1].setRGBColorFill(255, 128, 128);
				        onOff[1].rectangle(1, 1, 18, 18);
				        onOff[1].fillStroke();
				        onOff[1].moveTo(1, 1);
				        onOff[1].lineTo(19, 19);
				        onOff[1].moveTo(1, 19);
				        onOff[1].lineTo(19, 1);
				        onOff[1].stroke();
				        RadioCheckField checkbox;
				        
				            rect = new Rectangle(Float.parseFloat(args[0]), Float.parseFloat(args[1]),Float.parseFloat(args[0])+72, Float.parseFloat(args[1])+48);
				            checkbox = new RadioCheckField(writer, rect, args[3], "Yes");
				            field = checkbox.getCheckField();
				            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "No", onOff[0]);
				            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Yes", onOff[1]);
				            writer.addAnnotation(field);
				            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
				                new Phrase(args[3], font), Float.parseFloat(args[0])+30, Float.parseFloat(args[1])-16, 0);
				        
						stamper.close();
				       break;
					}
				default:{
					System.err.println("Unknown type.");
				}
			}
		}
		
	}
	// return byte strem
	static void SignDocument(String src, String FieldName, String dest, Image sign, final byte[] bio, PrivateKey privkey) 
			throws FileNotFoundException, IOException, DocumentException, GeneralSecurityException{
		
//		Cipher cipher = Cipher.getInstance("RSA"); 
//        cipher.init(Cipher.ENCRYPT_MODE, privkey);
//        final byte[] encryptedByteData = cipher.doFinal(bio);
   
		//sign document
        
	    Properties properties = new Properties();
	    properties.load(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/" + "Analab/key.properties"));
		//properties.load(new FileInputStream("/storage/sdcard0/external_SD/key.properties"));
		String path =  Environment.getExternalStorageDirectory().getPath() + "/" + "Analab/hackveda.pfx";//properties.getProperty("PRIVATE");//
        String keystore_password = properties.getProperty("PASSWORD");
        String key_password = properties.getProperty("PASSWORD");
        
        KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        String alias = (String)ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey)ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        
        PdfReader reader = new PdfReader(src);
       // ByteArrayOutputStream boas = null;
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(FieldName);
        appearance.setLocation("GPS suradnice");
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
        appearance.setSignatureGraphic(sign);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
        appearance.setSignatureEvent(
        		new SignatureEvent(){
        			public void getSignatureDictionary(PdfDictionary sig) {sig.put(new PdfName("BioSignEncripted"),new PdfString(new String(bio)));}
        		}
        );
        
        ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        ExternalDigest digest = new ProviderDigest("BC");
       
		MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, CryptoStandard.CMS);
        
       // byte[] out = null;
       // os.write(out);
      //  return boas.toByteArray();
	}
	
	
	static public Set<String> GetTextForSearch(String src) throws IOException {
        Set<String> set = new HashSet<String>();
        int k=0,j=0;
		PdfReader reader = new PdfReader(src);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        TextExtractionStrategy strategy;
        String tmp;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            tmp=strategy.getResultantText();
            k=0;j=0;
            do{
            	k=tmp.indexOf("{PDFBioSign:", k);
            	if(k>=0)j=tmp.indexOf('}', k);
            		else j = -1;
            	if(j>0 && k>=0){
            		set.add(tmp.substring(k, j+1));
            	}
            	k=j;
            }while(k>0);
         
            	
        }
        reader.close();
        return set;
    }
}
