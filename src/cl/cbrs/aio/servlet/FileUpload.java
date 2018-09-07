package cl.cbrs.aio.servlet;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.struts.action.service.ReingresoServiceAction;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;

/**
 * Servlet to handle File upload request from Client
 * @author Javin Paul
 */
public class FileUpload extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(FileUpload.class);
	
	private static String ARCHIVO_PARAMETROS = "aio.parametros";
	private String UPLOAD_DIRECTORY =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_VERSIONAR_ESCRITURA", "valor");
	private String UPLOAD_DIRECTORY_OCR =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_OCR_ESCRITURA", "valor");

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		JSONObject respuesta = new JSONObject();

		//process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request)){
			try {

				String observacionUsuario = null;
				Long caratula = null;

				CaratulasUtil caratulasUtil=new CaratulasUtil();

				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for(FileItem item : multiparts){
					if(!item.isFormField()){
						
						PdfReader pdfReader = new PdfReader(item.getInputStream());
						ByteArrayOutputStream bufferUnlocked = new ByteArrayOutputStream();
						liberate(pdfReader, bufferUnlocked);
						
						
						File folderOcr = new File(UPLOAD_DIRECTORY_OCR+File.separator +"OCR_"+caratula);
						
						if (!folderOcr.exists()) {
				  			folderOcr.mkdir();
				  		}

						File fileVersiona = new File(UPLOAD_DIRECTORY+File.separator +"C_"+caratula+".pdf");
						File fileOcr = new File(folderOcr.toString() + File.separator +"OCR_"+caratula+".pdf");
						
						FileOutputStream fop = new FileOutputStream(fileVersiona);
						FileOutputStream fopOcr = new FileOutputStream(fileOcr);
						
//				  		InputStream archivo = item.getInputStream();
//				  		InputStream archivoCopia = item.getInputStream();
				  		
//				  		IOUtils.copy(archivo,fop);
				  		bufferUnlocked.writeTo(fop);
				  		fop.close();
				  		
//				  		IOUtils.copy(archivoCopia,fopOcr);
				  		bufferUnlocked.writeTo(fopOcr);
				  		fopOcr.close();
				  		
//				  		archivo.close();	
//				  		archivoCopia.close();
					}else{
						String name = item.getFieldName();
						if(name.equalsIgnoreCase("observacion"))
							observacionUsuario = item.getString();
						else if(name.equalsIgnoreCase("caratula"))
							caratula=Long.parseLong(item.getString());

					}
				}

				String observacion = "Reingreso Escritura " + observacionUsuario;
				String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
				caratulasUtil.agregarBitacoraCaratula(caratula, rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);

				//File uploaded successfully
				respuesta.put("message", "Versionar OK");
				respuesta.put("status", true);

			} catch (Exception ex) {
				//            	respuesta.put("message", "Problema Versionando" + ex);
				respuesta.put("message", "Problema Versionando");
				respuesta.put("status", false);
				logger.error(ex.getMessage(),ex);
			}          

		}else{
			respuesta.put("message","Problema con servidor");
			respuesta.put("status", false);
		}

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
		}


	}
	
	  private void liberate(PdfReader reader, OutputStream output) throws IOException, DocumentException {
		    try {
		      setBooleanField(reader, "ownerPasswordUsed", false);
		      setBooleanField(reader, "encrypted", false);
		    } catch (NoSuchFieldException nsfe) {
		      // We expect these fields to be part of iText.  If they are not found, then we are probably using a different version.
		      AssertionError ae = new AssertionError("could not find a field");
		      ae.initCause(nsfe);
		      throw ae;
		    }

		    reader.removeUsageRights();
		    PdfCopyFields copy = new PdfCopyFields(output);
		    copy.addDocument(reader);
		    copy.close();
		  }	
	  
		private void setBooleanField(Object o, String fieldName, boolean value) throws NoSuchFieldException {
		    Field field = o.getClass().getDeclaredField(fieldName);
		    field.setAccessible(true);
		    try {
		      field.setBoolean(o, value);
		    } catch (IllegalAccessException iae) {
		      // The call to setAccessible() should have stopped this from happening.  If it didn't then we are probably running in
		      // some more strict container or virtual machine.
		      throw new RuntimeException(iae);
		    }
		  }	 	

}

