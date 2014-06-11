package com.analab.pdfbiosign;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.Cipher;

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
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.xml.xmp.XmpWriter;

public class AcroMaker {
	static private double x_cord=0,y_cord=0;
	static private String dest_path = "/storage/sdcard0/external_SD/AcroMaker.pdf";
	
	static void PutAcros( String src, String dest, String[] cord) throws IOException, DocumentException{
		
		String[] args;
		PdfReader reader = new PdfReader(src);
        
		/*FileOutputStream os = new FileOutputStream(dest);
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
        document.add(table);*/
		for(String tmp_str:cord){
			args=tmp_str.split(":");
			switch(args[2]){
				case "sign" : {
			      System.out.println("Dostane");
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
	
	static void SignDocument(String src, String FieldName, String dest, Image sign, byte[] bio) 
			throws FileNotFoundException, IOException, DocumentException, GeneralSecurityException{
		
		//encrypt biometric data  
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(4096,new SecureRandom());
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		Cipher cipher = Cipher.getInstance("RSA"); 
        cipher.init(Cipher.ENCRYPT_MODE, pub); 

        byte[] encryptedByteData = cipher.doFinal(bio);
		
		
		//sign document
	    Properties properties = new Properties();
		properties.load(new FileInputStream("/storage/sdcard0/external_SD/key.properties"));
		
		String path = properties.getProperty("PRIVATE");
        String keystore_password = properties.getProperty("PASSWORD");
        String key_password = properties.getProperty("PASSWORD");
        KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        String alias = (String)ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey)ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
     
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = PdfStamper.createSignature(reader, new FileOutputStream(dest), '\0');

        
        
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(FieldName);
        appearance.setLocation("GPS suradnice");
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
        appearance.setSignatureGraphic(sign);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        
        ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();
        MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, CryptoStandard.CMS);
	}
}
