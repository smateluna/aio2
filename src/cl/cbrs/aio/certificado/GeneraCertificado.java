package cl.cbrs.aio.certificado;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

import cl.cbr.firmaelectronica.ws.WSFirmaElectronica;
import cl.cbr.firmaelectronica.ws.WSFirmaElectronicaServiceLocator;
import cl.cbr.util.ErroresUtil;
import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbr.util.TextosUtil;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.firmaelectronica.delagate.ClienteWsFirmadorDelegate;
import cl.cbrs.firmaelectronica.vo.RegistroFirmaElectronicaVO;
import cl.cbrs.inscripciondigital.util.ConstantesInscripcionDigital;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperRunManager;



public class GeneraCertificado {
	private static Logger logger = Logger.getLogger(GeneraCertificado.class);

	private static String ARCHIVO_PARAMETROS = "ws_inscripciondigital.parametros";
	private String PATH_VISAR =TablaValores.getValor(ARCHIVO_PARAMETROS, "FINAL", "valor");
	private String PATH_FIRMA =TablaValores.getValor(ARCHIVO_PARAMETROS, "PARA_FIRMA", "valor");
	private String TEMPORAL =TablaValores.getValor(ARCHIVO_PARAMETROS, "TEMPORAL", "valor");
	public static String PARAMETROS_COMERCIO= "registro_comercial.parametros";
	public static Integer INS_CON_VIGENCIA= 8;
	public Integer INS_SIN_VIGENCIA= 9;
	public Integer INSCRIPCION= 1;
	public static String INSCRIPCION_CON_VIGENCIA= "11";
	public static String INSCRIPCION_SIN_VIGENCIA= "12";
	public static String REG_PROPIEDADES= "Propiedad";
	public static String REG_HIPOTECAS= "Hipoteca";
	public static String REG_PROHIBICIONES= "Interdicciones y Prohibiciones de Enajenar";
	public static String ERROR_DE_OFICINA= "13";

	public void crearCertificado(CaratulaVO caratulaVO, boolean firmar, boolean enParte,String tipo, String usuario) throws Exception {
		File file1=null;
		File file2=null;
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;

		try {

			String nombreDocumentoVersion = "";
			String codigoFirma = "";
			String nombreDocumento = "";
			String prefijo = "";

			ClienteWsFirmadorDelegate delegate = new ClienteWsFirmadorDelegate();

			if(tipo.equals(INS_CON_VIGENCIA.toString())){
				nombreDocumento = "CPV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CPV_";
			}else if(tipo.equals(INS_SIN_VIGENCIA.toString())){
				nombreDocumento = "CPS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CPS_";
			}else if(tipo.equals(ERROR_DE_OFICINA)){
				nombreDocumento = "CEO_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CEO_";
			}else if(caratulaVO.getTipoFormulario().getTipo().equals(INSCRIPCION) || tipo.equals(INSCRIPCION_CON_VIGENCIA) || tipo.equals(INSCRIPCION_SIN_VIGENCIA)){
				nombreDocumento = "CMP_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CMP_";
			}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_CON_VIGENCIA)){
				nombreDocumento = "CPV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CPV_";
			}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_SIN_VIGENCIA)){
				nombreDocumento = "CPS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
				prefijo = "CPS_";
			}
			
			if(caratulaVO.getInscripciones()!=null && caratulaVO.getInscripciones()[0]!=null 
					&& caratulaVO.getInscripciones()[0].getRegistro()!=null 
					&& caratulaVO.getInscripciones()[0].getRegistro().intValue()==2)
			{
				if(tipo.equals(INS_CON_VIGENCIA.toString())){
					nombreDocumento = "CHV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CHV_";
				}else if(tipo.equals(INS_SIN_VIGENCIA.toString())){
					nombreDocumento = "CHS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CHS_";
				}else if(tipo.equals(ERROR_DE_OFICINA)){
					nombreDocumento = "CEH_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CEH_";
				}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_CON_VIGENCIA)){
					nombreDocumento = "CHV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CHV_";
				}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_SIN_VIGENCIA)){
					nombreDocumento = "CHS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CHS_";
				}				
			}
			
			if(caratulaVO.getInscripciones()!=null && caratulaVO.getInscripciones()[0]!=null 
					&& caratulaVO.getInscripciones()[0].getRegistro()!=null 
					&& caratulaVO.getInscripciones()[0].getRegistro().intValue()==3)
			{
				if(tipo.equals(INS_CON_VIGENCIA.toString())){
					nombreDocumento = "CXV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CXV_";
				}else if(tipo.equals(INS_SIN_VIGENCIA.toString())){
					nombreDocumento = "CXS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CXS_";
				}else if(tipo.equals(ERROR_DE_OFICINA)){
					nombreDocumento = "CEX_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CEX_";
				}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_CON_VIGENCIA)){
					nombreDocumento = "CXV_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CXV_";
				}else if(caratulaVO.getTipoFormulario().getTipo().equals(INS_SIN_VIGENCIA)){
					nombreDocumento = "CXS_" + caratulaVO.getNumeroCaratula()+ ".pdf";
					prefijo = "CXS_";
				}				
			}
				

			logger.debug("nombreDocumento:"+nombreDocumento);
			//TODO cambiar a llamada a webservice
			//Integer version = 10;
			Integer version = delegate.obtenerVersion(nombreDocumento); 
			logger.debug("version:"+version);

			if(null == version)
				version=0;

			nombreDocumentoVersion = prefijo + caratulaVO.getNumeroCaratula() + "-" + version.intValue() + ".pdf";
			logger.debug("nombreDocumentoVersion:"+nombreDocumentoVersion);

			codigoFirma = prefijo.substring(0,prefijo.length()-1) + Long.toHexString(new Long(caratulaVO.getNumeroCaratula()).longValue())+"-"+ Integer.toHexString(version);

			codigoFirma = codigoFirma.toLowerCase();

			RegistroFirmaElectronicaVO registroFirmaElectronicaVO= new RegistroFirmaElectronicaVO();
			registroFirmaElectronicaVO.setCaratula(caratulaVO.getNumeroCaratula().toString());
			registroFirmaElectronicaVO.setCodArchivoAlpha(codigoFirma);
			registroFirmaElectronicaVO.setFechaPdf(new Date());
			registroFirmaElectronicaVO.setNombreArchivo(nombreDocumento);
			registroFirmaElectronicaVO.setNombreArchivoVersion(nombreDocumentoVersion);
			registroFirmaElectronicaVO.setEnviado(0);

			InputStream certificado = getCertificado(caratulaVO, codigoFirma, nombreDocumento, enParte, tipo,null,usuario);

			crearCertificados(certificado, nombreDocumentoVersion, codigoFirma);

			List<InputStream> lista = new ArrayList<InputStream>();
			file1=new File(TEMPORAL+"TMP_"+nombreDocumentoVersion);
			fis1 = new FileInputStream(file1);
			lista.add(fis1);
			file2=new File(PATH_VISAR+caratulaVO.getNumeroCaratula()+".pdf");
			fis2 = new FileInputStream(file2);
			lista.add(fis2);

			OutputStream o = new FileOutputStream(new File(PATH_FIRMA+nombreDocumentoVersion));
			merge(lista, o,codigoFirma);
			o.close();

			if(firmar){
				registroFirmaElectronicaVO.setValorDocumento( caratulaVO.getValorReal().intValue()  );
				delegate.registrarDocumento(registroFirmaElectronicaVO);
			}

		} catch (DocumentException e) {
        	logger.error(e);
        	throw e;
        } catch (FileNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (ClassNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (SQLException e) {
        	logger.error(e);
        	throw e;
        } finally{
        	try{if(fis1!=null)fis1.close();}catch(IOException e){}
        	try{if(fis2!=null)fis2.close();}catch(IOException e){}
        	try{
        		if(file1!=null) {
        			if(file1.delete())
        				logger.debug("file1 borrado exitoso");
        			else
        				logger.debug("file1 borrado error");
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage(),e);
        	}
        	try{
        		if(file2!=null){ 
        			if(file2.delete())
        				logger.debug("file2 borrado exitoso");
        			else
        				logger.debug("file2 borrado error");
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage(),e);
        	}

        }
	}

	public void crearCertificadoErrorOficina(CaratulaVO caratulaVO, boolean firmar, boolean enParte,Long foja, String numero, Long anno, String usuario) throws Exception {
		File file1=null;
		File file2=null;
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;

		try {

			String nombreDocumentoVersion = "";
			String codigoFirma = "";
			String nombreDocumento = "";
			String prefijo = "";

			String nombreArchivo = caratulaVO.getNumeroCaratula() +"_"+numero+"_"+anno;

			ClienteWsFirmadorDelegate delegate = new ClienteWsFirmadorDelegate();

			nombreDocumento = "CEO_" + nombreArchivo+".pdf";
			prefijo = "CEO_";


			logger.debug("nombreDocumento:"+nombreDocumento);
			Integer version = delegate.obtenerVersion(nombreDocumento); 
			logger.debug("version:"+version);

			if(null == version)
				version=0;

			nombreDocumentoVersion = prefijo + nombreArchivo+ "-" + version.intValue() + ".pdf";
			logger.debug("nombreDocumentoVersion:"+nombreDocumentoVersion);

			codigoFirma = prefijo.substring(0,prefijo.length()-1) + Long.toHexString(caratulaVO.getNumeroCaratula())+"-"+Long.toHexString(foja)+"-"+Long.toHexString(Long.parseLong(numero))+"-"+Long.toHexString(anno)+"-"+ Integer.toHexString(version);

			codigoFirma = codigoFirma.toLowerCase();

			RegistroFirmaElectronicaVO registroFirmaElectronicaVO= new RegistroFirmaElectronicaVO();
			registroFirmaElectronicaVO.setCaratula(caratulaVO.getNumeroCaratula().toString());
			registroFirmaElectronicaVO.setCodArchivoAlpha(codigoFirma);
			registroFirmaElectronicaVO.setFechaPdf(new Date());
			registroFirmaElectronicaVO.setNombreArchivo(nombreDocumento);
			registroFirmaElectronicaVO.setNombreArchivoVersion(nombreDocumentoVersion);
			registroFirmaElectronicaVO.setEnviado(0);

			InputStream certificado = getCertificado(caratulaVO, codigoFirma, nombreDocumento, enParte, ERROR_DE_OFICINA, nombreArchivo, usuario);

			crearCertificados(certificado, nombreDocumentoVersion, codigoFirma);

			List<InputStream> lista = new ArrayList<InputStream>();
			file1=new File(TEMPORAL+"TMP_"+nombreDocumentoVersion);
			fis1 = new FileInputStream(file1);
			lista.add(fis1);
			file2=new File(PATH_VISAR+nombreArchivo+".pdf");
			fis2 = new FileInputStream(file2);
			lista.add(fis2);

			OutputStream o = new FileOutputStream(new File(TablaValores.getValor(ARCHIVO_PARAMETROS, "PARA_FIRMA_ERROR_OFICINA", "valor")+nombreDocumentoVersion));
			merge(lista, o,codigoFirma);
			o.close();

			if(firmar){
				registroFirmaElectronicaVO.setValorDocumento( caratulaVO.getValorReal().intValue()  );
				delegate.registrarDocumento(registroFirmaElectronicaVO);
			}

			WSFirmaElectronicaServiceLocator locator=new WSFirmaElectronicaServiceLocator();
			//            WSFirmaElectronica  servicio = locator.getFirmaElectronicaWS(new URL("http://192.168.100.224:8080/WsFirmaElectronica/services/FirmaElectronicaWS?wsdl"));
			WSFirmaElectronica  servicio = locator.getFirmaElectronicaWS(new URL(TablaValores.getValor(PARAMETROS_COMERCIO, "ENDPOINT_FIRMA", "valor")));
			String respuesta =servicio.firmarArchivo(TablaValores.getValor(ARCHIVO_PARAMETROS, "PARA_FIRMA_ERROR_OFICINA", "valor"), nombreDocumentoVersion);
			if (!respuesta.equals("OK"))
				throw new Exception();

		} catch (DocumentException e) {
        	logger.error(e);
        	throw e;
        } catch (FileNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (ClassNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (SQLException e) {
        	logger.error(e);
        	throw e;
        } finally{
        	try{if(fis1!=null)fis1.close();}catch(IOException e){}
        	try{if(fis2!=null)fis2.close();}catch(IOException e){}
        	try{
        		if(file1!=null) {
        			if(file1.delete())
        				logger.debug("file1 borrado exitoso");
        			else
        				logger.debug("file1 borrado error");
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage(),e);
        		throw e;
        	}
        	try{
        		if(file2!=null){ 
        			if(file2.delete())
        				logger.debug("file2 borrado exitoso");
        			else
        				logger.debug("file2 borrado error");
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage(),e);
        		throw e;
        	}
        }
	}


	private static InputStream getCertificado(CaratulaVO caratulaVO, String codigoBarra, String nombreDocumento,boolean enParte, String tipo,String nomArchivoConTitulo, String usuario) throws ClassNotFoundException, SQLException, FileNotFoundException, GeneralException   {

		String jasperPath = TablaValores.getValor("jasper.parametros", "path" , "valor");
		String jasperFile = jasperPath + TablaValores.getValor("jasper.parametros", "certificado_copia_inscripcion" , "valor");

		ByteArrayInputStream certificadoDigital = null;
		InputStream input = null;
		try {
			InscripcionCitadaVO[] ins = caratulaVO.getInscripciones();
			String bis="";

			if(null != ins[0].getBis()){
				if(ins[0].getBis()==1)
					bis="BIS ";
			}	

			Map parametros1 = new HashMap();
			parametros1.put("TEXTO1", "El texto del documento");
			parametros1.put("CODIGO_BARRA", codigoBarra.toLowerCase());
			parametros1.put("CARATULA", "Carátula "+caratulaVO.getNumeroCaratula());
			parametros1.put("TEXTO_BARRA", codigoBarra.toLowerCase());
			parametros1.put("FOJAS", ins[0].getFoja().toString());
			parametros1.put("NUMERO", ins[0].getNumero().toString());
			parametros1.put("ANO", ins[0].getAno().toString());
			parametros1.put("BIS", bis);
			if(caratulaVO.getInscripciones()[0].getRegistro() == 3)
				parametros1.put("REGISTRO", REG_PROHIBICIONES);
			else if(caratulaVO.getInscripciones()[0].getRegistro() == 2)
				parametros1.put("REGISTRO", REG_HIPOTECAS);
			else
				parametros1.put("REGISTRO", REG_PROPIEDADES);
			parametros1.put("USUARIO", usuario);
			parametros1.put("PATH", jasperPath);

			String sufijoVigencia="";
			String tipoDocumento = TablaValores.getValor(ARCHIVO_PARAMETROS, "SIN_VIGENCIA", "valor");
			Date date = null;
			String tipoActo = "COPIA_SIN_VIGENCIA";
			//TODO cambiar las fechas por las que correspondan, averiguar fecha de vigencia y fecha de certificacion.
			if(tipo.equals(INSCRIPCION_CON_VIGENCIA) || tipo.equals(INS_CON_VIGENCIA.toString()) || enParte){
				if(enParte){
					sufijoVigencia= sufijoVigencia + " y se encuentra vigente en parte al día ";
				} else {
					sufijoVigencia= sufijoVigencia + " y se encuentra vigente al día ";   
				}

				date = new Date();
				int diasVigencia = -1;
				diasVigencia=new Integer(ConstantesInscripcionDigital.getParametro("DIAS_VIGENCIA")).intValue();
				Calendar cal =Fechas.obtenerHabilSiguiente(diasVigencia, Calendar.getInstance());
				sufijoVigencia= sufijoVigencia + cal.get(Calendar.DATE)+" de "+ TextosUtil.nombreDeMes(cal.get(Calendar.MONTH)+1) +" de "+(cal.get(Calendar.YEAR));
				tipoDocumento = TablaValores.getValor(ARCHIVO_PARAMETROS, "CON_VIGENCIA", "valor");
				tipoActo="COPIA_VIGENCIA";
			}

			if(enParte){
				tipoDocumento = TablaValores.getValor(ARCHIVO_PARAMETROS, "EN_PARTE", "valor");
			}

			date = new Date();
			parametros1.put("SUFIJO_VIGENCIA",sufijoVigencia); 
			parametros1.put("FECHA_P", date.getDate()+" de "+ TextosUtil.nombreDeMes(date.getMonth()) +" de "+date.getYear());
			parametros1.put("TIPO_DOC",tipoDocumento );

			String pathArchivo = "";
			if(null != nomArchivoConTitulo)
				pathArchivo = ConstantesInscripcionDigital.getParametro("FINAL")+nomArchivoConTitulo+".pdf";
			else
				pathArchivo = ConstantesInscripcionDigital.getParametro("FINAL")+caratulaVO.getNumeroCaratula()+".pdf";
			input = new FileInputStream(new File(pathArchivo));
			//TODO se debe implementar metodo calculaValor
			double valor=0;
			try{
				if (!tipo.equals(ERROR_DE_OFICINA)){
					valor=calcularDerechos(getNumeroCarillas(input),0,0,tipoActo).doubleValue();
				}
				logger.debug("valorDerechos:"+valor);
			}catch(Exception e){
				logger.error("Error calculando derechos:",e);
			}
			caratulaVO.setValorReal((long)valor);
			parametros1.put("VALOR", "$ "+(int)valor);

			InputStream is1 = new BufferedInputStream(new FileInputStream(jasperFile));

			byte[] rep1 = JasperRunManager.runReportToPdf(is1, parametros1);
			certificadoDigital = new ByteArrayInputStream(rep1);

		}catch (JRException e) {
			logger.error(e.getMessage(),e);
			throw new GeneralException("JRE",e.getMessage()); 
		}catch(JRRuntimeException jre){
			logger.error(jre.getMessage(),jre);
		}catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new GeneralException("JRE",e.getMessage()); 
		}finally{
			try{if(input!=null)input.close();}catch(Exception e){}

		}
		return certificadoDigital;
	}

	private static void crearCertificados(InputStream certificado, String nombreCertificado, String codigoVerificacion) throws DocumentException {
		try {

			Rectangle pageSize = new Rectangle(0, 0, 740, 940);
			Document document = new Document(pageSize);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(TablaValores.getValor(
					ARCHIVO_PARAMETROS, "TEMPORAL", "valor")
					+ "TMP_"+nombreCertificado));
			
			PdfReader pdfReader = new PdfReader(certificado);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfContentByte cbu = writer.getDirectContentUnder();
			Image img = Image.getInstance(TablaValores.getValor(ARCHIVO_PARAMETROS, "LOGO_CHILE", "valor"));
			img.setAbsolutePosition(200, 300);
			String fuente = TablaValores.getValor(ARCHIVO_PARAMETROS, "FONT_VERDANA", "valor");
			BaseFont bf = BaseFont.createFont(fuente,BaseFont.IDENTITY_H, true);
			cbu.setFontAndSize(bf, 11);

			int pagina =0;
			for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
				pagina++;

				if(i!=0){
					document.newPage();
				}

				PdfImportedPage page = writer.getImportedPage(pdfReader, (i+1));
				cbu.setLineWidth(1);
				cbu.setRGBColorFillF(0xff, 0xff, 0xff);
				cbu.setColorStroke(new Color(133, 7, 28));
				//cbu.rectangle(100, 100, width - 205, height - 150);
				cbu.fillStroke();
				cbu.addImage(img);
				cbu.addTemplate(page, 70, 0);

				//agregaInformacionPieDePagina(pagina, pdfReader.getNumberOfPages(), 600, cbu);

				if (pagina == pdfReader.getNumberOfPages()) {
					cb.beginText();
					cb.setFontAndSize(bf, 11);
					cb.showTextAligned(Element.ALIGN_LEFT, "Santiago, "
							+ TextosUtil.fechaEnPalabras(new Date(), false, false, false) + ".", 123, 254, 0);

					cb.endText();
					PdfFormField sig = PdfFormField.createSignature(writer);

					sig.setWidget(new Rectangle(340, 250, 620, 128), null);
					sig.setFlags(PdfAnnotation.FLAGS_PRINT);
					sig.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g"));
					sig.setFieldName("Signature1");
					sig.setPage(pdfReader.getNumberOfPages());
					sig.setMKBackgroundColor(new Color(Color.TRANSLUCENT)); 
					writer.addAnnotation(sig);
				}
			}

			document.close();
			certificado.close();
		}catch(JRRuntimeException jre){
			jre.printStackTrace();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new DocumentException();
		}finally{
			try{if(certificado!=null)certificado.close();}catch(IOException e){}
		}

	}

	public static Long getNumeroCarillas(InputStream pdf){
		int numPaginas = 0;
		try{
			PdfReader pdfReader = new PdfReader(pdf);
			numPaginas = pdfReader.getNumberOfPages();
			System.out.println("numPaginas:"+numPaginas);

		}catch(IOException ioe){
			logger.error(ioe.getMessage(),ioe);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return new Long(numPaginas);
	}

	public static void merge(List<InputStream> streamOfPDFFiles, OutputStream outputStream,String codigoFirma) throws Exception,
	DocumentException {
		Rectangle pageSize1 = new Rectangle(0, 0, 740, 940);

		Document document = new Document(pageSize1);

		InputStream pdf = null;
		try {
			List<InputStream> pdfs = streamOfPDFFiles;
			List<PdfReader> readers = new ArrayList<PdfReader>();
			int totalPages = 0;
			Iterator<InputStream> iteratorPDFs = pdfs.iterator();

			while (iteratorPDFs.hasNext()) {
				pdf = iteratorPDFs.next();
				PdfReader pdfReader = new PdfReader(pdf);
				readers.add(pdfReader);
				totalPages += pdfReader.getNumberOfPages();
			}
			int cantidadDePaginas = totalPages;
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			document.open();
			PdfContentByte cb = writer.getDirectContent(); // Holds the PDF

			PdfImportedPage page;
			int currentPageNumber = 0;
			int pageOfCurrentReaderPDF = 0;
			Iterator<PdfReader> iteratorPDFReader = readers.iterator();

			while (iteratorPDFReader.hasNext()) {
				PdfReader pdfReader = iteratorPDFReader.next();

				while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
					document.newPage();

					if(currentPageNumber != 1){
						Rectangle pageSize = new Rectangle(0, 0, 740, 990);
						document.setPageSize(pageSize);
					}

					pageOfCurrentReaderPDF++;
					currentPageNumber++;
					page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
					if (currentPageNumber == 1) {
						PdfFormField sig = PdfFormField.createSignature(writer);

						sig.setWidget(new Rectangle(340, 250, 620, 128), null);
						sig.setFlags(PdfAnnotation.FLAGS_PRINT);
						sig.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g"));
						sig.setFieldName("Signature1");
						sig.setPage(currentPageNumber);
						writer.addAnnotation(sig);
					}
					cb.addTemplate(page, 0, 0);
					/**
					 * agregar numero de pagina
					 */
					agregaInformacionPieDePagina(currentPageNumber, cantidadDePaginas, 800, cb,codigoFirma);

				}
				pageOfCurrentReaderPDF = 0;
			}

			outputStream.flush();
		}
		catch (Exception e) {
			System.out.println(ErroresUtil.extraeStackTrace(e));
			throw e;
		}
		finally {
			if (document.isOpen()) {
				document.close();
				try {
					for (Iterator iterator = streamOfPDFFiles.iterator(); iterator.hasNext();) {
						InputStream inputStream = (InputStream) iterator.next();
						try {
							inputStream.close();
						}
						catch (Exception e) {
							logger.error("no se pudo cerrar: " + e.getMessage(),e);
						}
					}
					outputStream.close();
				}
				catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			if(pdf!=null)
				pdf.close();
		}
	}

	private static Double calcularDerechos(long carillas, long capital, long capitalDerechos, String tipoACTO)
			throws Exception {
		Double carilla = null;
		Double base = null;
		Double factor_capital = null;
		Double factor_derecho = null;
		carilla = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "carilla"));//300
		base = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "base"));//4300
		factor_capital = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "factor_capital"));//0
		factor_derecho = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "derecho"));//0
		Double minimo = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "minimo"));//4600
		Double maximo = new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "maximo"));//150000
		Double diferenciaMinina=new Double(TablaValores.getValor(PARAMETROS_COMERCIO,tipoACTO, "diferencia_minima"));//2400
		double valor = 0.0D;
		valor = carilla.doubleValue() * (double) carillas + base.doubleValue() + factor_capital.doubleValue()
		* (double) capital + factor_derecho.doubleValue() * (double) capitalDerechos;
		if (valor < minimo.doubleValue()) {
			valor = minimo.doubleValue();
		}
		if (valor > maximo.doubleValue()) {
			valor = maximo.doubleValue();
		}

		if ((valor - minimo.doubleValue()) < diferenciaMinina.doubleValue()) {
			return minimo;
		}
		else {
			return Double.valueOf(valor);
		}
	}

	private static void agregaInformacionPieDePagina(int pagina, int totalPaginas, float anchoDocumento,
			PdfContentByte cb, String codigoFirma) throws Exception {
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		cb.beginText();
		cb.setColorFill(Color.black);
		cb.setFontAndSize(bf, 8);
		cb.showTextAligned(Element.ALIGN_LEFT, "página " + pagina + " de " + totalPaginas, anchoDocumento - 155, 30, 0);
		if (pagina>1){
			cb.setFontAndSize(bf, 20);
			cb.setColorFill(Color.black);
			cb.setFontAndSize(bf, 8);
			cb.showTextAligned(Element.ALIGN_LEFT, "Documento incorpora Firma", 50, 40, 0);
			cb.showTextAligned(Element.ALIGN_LEFT, "Electronica Avanzada", 50, 32, 0);
			cb.showTextAligned(Element.ALIGN_LEFT, "Código de Verificación: " + codigoFirma, 50, 24, 0);
			cb.setFontAndSize(bf, 8);
		}
		cb.endText();
		cb.stroke();

	}

	public void crearCertificadoPlantilla(CaratulaVO caratulaVO, boolean firmar, String usuario, String titulo, String cuerpoCertificado, String prefijo, Long valor) throws Exception {
		File file1=null;
		FileInputStream fis1 = null;
		OutputStream o = null;
		
		try {

			String nombreDocumentoVersion = "";
			String codigoFirma = "";
			String nombreDocumento = "";

			String nombreArchivo = caratulaVO.getNumeroCaratula().toString();

			ClienteWsFirmadorDelegate delegate = new ClienteWsFirmadorDelegate();

			nombreDocumento = prefijo+"_" + nombreArchivo+".pdf";
			prefijo = prefijo+"_";


			logger.debug("nombreDocumento:"+nombreDocumento);
			Integer version = delegate.obtenerVersion(nombreDocumento); 
			logger.debug("version:"+version);

			if(null == version)
				version=0;

			nombreDocumentoVersion = prefijo + nombreArchivo+ "-" + version.intValue() + ".pdf";
			logger.debug("nombreDocumentoVersion:"+nombreDocumentoVersion);

			codigoFirma = prefijo.substring(0,prefijo.length()-1) + Long.toHexString(caratulaVO.getNumeroCaratula())+"-"+ Integer.toHexString(version);

			codigoFirma = codigoFirma.toLowerCase();

			RegistroFirmaElectronicaVO registroFirmaElectronicaVO= new RegistroFirmaElectronicaVO();
			registroFirmaElectronicaVO.setCaratula(caratulaVO.getNumeroCaratula().toString());
			registroFirmaElectronicaVO.setCodArchivoAlpha(codigoFirma);
			registroFirmaElectronicaVO.setFechaPdf(new Date());
			registroFirmaElectronicaVO.setNombreArchivo(nombreDocumento);
			registroFirmaElectronicaVO.setNombreArchivoVersion(nombreDocumentoVersion);
			registroFirmaElectronicaVO.setEnviado(0);
			registroFirmaElectronicaVO.setValorDocumento( valor.intValue() );
			delegate.registrarDocumento(registroFirmaElectronicaVO);

			InputStream certificado = getCertificadoPlantilla(caratulaVO, codigoFirma, usuario, titulo, cuerpoCertificado);
															
			crearCertificados(certificado, nombreDocumentoVersion, codigoFirma);

			//TODO pegar portada a documento original
			List<InputStream> lista = new ArrayList<InputStream>();
			file1=new File(TEMPORAL+"TMP_"+nombreDocumentoVersion);
			fis1 = new FileInputStream(file1);
			lista.add(fis1);

			o = new FileOutputStream(new File(TablaValores.getValor(ARCHIVO_PARAMETROS, "PARA_FIRMA_ERROR_OFICINA", "valor")+nombreDocumentoVersion));
			merge(lista, o,codigoFirma);

		} catch (DocumentException e) {
        	logger.error(e);
        	throw e;
        } catch (FileNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (ClassNotFoundException e) {
        	logger.error(e);
        	throw e;
        } catch (SQLException e) {
        	logger.error(e);
        	throw e;
        } finally{
        	try{if(fis1!=null)fis1.close();}catch(IOException e){}
        	try{if(o!=null)o.close();}catch(IOException e){}
        	try{
        		if(file1!=null) {
        			if(file1.delete())
        				logger.debug("file1 borrado exitoso");
        			else
        				logger.debug("file1 borrado error");
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage(),e);
        		throw e;
        	}
        }
	}
		
	private static InputStream getCertificadoPlantilla(CaratulaVO caratulaVO, String codigoBarra, String usuario, String titulo, String cuerpoCertificado) throws ClassNotFoundException, SQLException, FileNotFoundException, GeneralException   {

		String jasperPath = TablaValores.getValor("jasper.parametros", "path" , "valor");
		String jasperFile = jasperPath + TablaValores.getValor("jasper.parametros", "plantillero" , "valor");

		ByteArrayInputStream certificadoDigital = null;
		InputStream input = null;
		try {
			
			Map parametros1 = new HashMap();
			parametros1.put("TEXTO1", "El texto del documento");
			parametros1.put("CODIGO_BARRA", codigoBarra.toLowerCase());
			parametros1.put("CARATULA", "Carátula "+caratulaVO.getNumeroCaratula());
			parametros1.put("TEXTO_BARRA", codigoBarra.toLowerCase());
			parametros1.put("TITULO", titulo);
			parametros1.put("CUERPO_CERTIFICADO", cuerpoCertificado);
			parametros1.put("USUARIO", usuario);
			parametros1.put("PATH", jasperPath);

			Date date = new Date();
			
			parametros1.put("FECHA_P", date.getDate()+" de "+ TextosUtil.nombreDeMes(date.getMonth()) +" de "+date.getYear());
	
			InputStream is1 = new BufferedInputStream(new FileInputStream(jasperFile));

			byte[] rep1 = JasperRunManager.runReportToPdf(is1, parametros1);
			certificadoDigital = new ByteArrayInputStream(rep1);

		}catch (JRException e) {
			logger.error(e.getMessage(),e);
			throw new GeneralException("JRE",e.getMessage()); 
		}catch(JRRuntimeException jre){
			logger.error(jre.getMessage(),jre);
		}catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new GeneralException("JRE",e.getMessage()); 
		}finally{
			try{if(input!=null)input.close();}catch(Exception e){}

		}
		return certificadoDigital;
	}
	
	public void firmarCertificadoPlantilla(String caratula,String prefijo) throws Exception {
			String nombreDocumentoVersion = "";
			String nombreDocumento = "";

			String nombreArchivo = caratula;

			ClienteWsFirmadorDelegate delegate = new ClienteWsFirmadorDelegate();

			nombreDocumento = prefijo+"_" + nombreArchivo+".pdf";
			prefijo = prefijo+"_";

			logger.debug("nombreDocumento:"+nombreDocumento);
			Integer version = delegate.obtenerVersion(nombreDocumento); 
			logger.debug("version:"+version);

			if(null == version)
				version=0;

			version=version-1;
						
			nombreDocumentoVersion = prefijo + nombreArchivo+ "-" + version + ".pdf";
			logger.debug("nombreDocumentoVersion:"+nombreDocumentoVersion);
			
			File inFile = new File(TEMPORAL+nombreDocumentoVersion);
	        File outFile = new File(PATH_FIRMA+nombreDocumentoVersion);
	        
	        try{
	        	FileUtils.moveFile(inFile,outFile);
		     }catch (Exception e) {
		    	 logger.error(e.getMessage(),e);
		     }
			
	}
	
}
