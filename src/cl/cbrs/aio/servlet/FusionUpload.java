package cl.cbrs.aio.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.Rectangle;
//import com.lowagie.text.pdf.PdfContentByte;
//import com.lowagie.text.pdf.PdfImportedPage;
//import com.lowagie.text.pdf.PdfReader;
//import com.lowagie.text.pdf.PdfWriter;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.DocumentoDAO;
import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.DocumentoDTO;
import cl.cbrs.aio.dto.estado.CaratulaEstadoDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.util.CaratulaEstadoUtil;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

/**
 * Servlet to handle File upload request from Client
 * @author Javin Paul
 */
public class FusionUpload extends HttpServlet {
	Logger logger = Logger.getLogger(FusionUpload.class);
	private  String ARCHIVO_PARAMETROS = "aio.parametros";
	private String UPLOAD_DIRECTORY =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_VERSIONAR_ESCRITURA", "valor");
	private String UPLOAD_DIRECTORY_OCR =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_OCR_ESCRITURA", "valor");
	private  String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
	private String SECCIONES_RECHAZOS =TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "TAB_SECCIONES.RECHAZADOS", "valor");

	private  final Long ESCRITURA = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		JSONObject respuesta = new JSONObject();

		//process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request)){
			try {

				String observacionUsuario = null;
				Long caratula = null;
				Integer version = null;

				CaratulasUtil caratulasUtil=new CaratulasUtil();

				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				DocumentosCliente docDelegate = new DocumentosCliente();
				DocumentoDAO dm = new DocumentoDAO();
				DocumentoDTO documentoDTO = new DocumentoDTO();

				for(FileItem item : multiparts){
					if(!item.isFormField()){

						//TODO juntar ultima version escritura con documentos anexos 

						documentoDTO = dm.getDocumento(caratula, version, ESCRITURA);

						if(documentoDTO==null){
							respuesta.put("message", "Error: Caratula no encontrada en documentos");
							respuesta.put("status", false);
						}else{

							byte[] archivo = docDelegate.downloadEscritura(false, documentoDTO.getIdDocumento(), 0L, 0L, documentoDTO.getVersion().intValue(), false, false, true);

							// write byte array to the output stream
							List<InputStream> lista = new ArrayList<InputStream>();
							List<InputStream> lista2 = new ArrayList<InputStream>();
							//archivo 1
							lista.add(new ByteArrayInputStream(archivo));
							lista2.add(new ByteArrayInputStream(archivo));

							//archivo 2							
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							liberate(item.getInputStream(), os);
							
							lista.add(new ByteArrayInputStream(os.toByteArray()));
							lista2.add(new ByteArrayInputStream(os.toByteArray()));
							
							File folderOcr = new File(UPLOAD_DIRECTORY_OCR+File.separator +"OCR_"+caratula);
							
							if (!folderOcr.exists()) {
					  			folderOcr.mkdir();
					  		}

							File fileVersiona = new File(UPLOAD_DIRECTORY+File.separator +"C_"+caratula+".pdf");
							File fileOcr = new File(folderOcr.toString() + File.separator +"OCR_"+caratula+".pdf");
							
							FileOutputStream fop = new FileOutputStream(fileVersiona);
							FileOutputStream fopOcr = new FileOutputStream(fileOcr);
					  		
							this.merge(lista, fop);
					  		fop.close();
					  		
							this.merge(lista2, fopOcr);
					  		fopOcr.close();			
						}
					}else{
						String name = item.getFieldName();
						if(name.equalsIgnoreCase("observacion"))
							observacionUsuario = item.getString();
						else if(name.equalsIgnoreCase("caratula"))
							caratula=Long.parseLong(item.getString());
						else if(name.equalsIgnoreCase("version"))
							version=Integer.parseInt(item.getString());
					}
				}

				if(documentoDTO!=null){
					String observacion = "Reingreso Escritura Fusion " + observacionUsuario;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					caratulasUtil.agregarBitacoraCaratula(caratula, rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);

					//Reingresar Caratula
					CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();

					WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();


					try {		
						CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratula);

						if(caratulaVO!=null){	
							//Reviso si la caratula esta rechazada
							if(SECCIONES_RECHAZOS.contains(caratulaVO.getEstadoActualCaratula().getSeccion().getCodigo())){
								CaratulaEstadoUtil cu = new CaratulaEstadoUtil();
								caratulaEstadoDTO = cu.getCaratulaEstadoDTO(caratulaVO);
								List<MovimientoDTO> movimientos = caratulaEstadoDTO.getMovimientoDTOs();

								for (int i=movimientos.size()-1; i >= 0; i--){
									MovimientoDTO movimientoDto = (MovimientoDTO)movimientos.get(i);
									//								System.out.println(movimientoDto.getSeccionDTO().getDescripcion());
									if(SECCIONES_RECHAZOS.contains(movimientoDto.getSeccionDTO().getId())){
										//									System.out.println("esta rechazada");
										movimientoDto = (MovimientoDTO)movimientos.get(i-1);
										//									System.out.println("la siguiente seria :" + movimientoDto.getSeccionDTO().getDescripcion());
										//verifico si la seccion que viene es distinta a rechazo, para asi reingresarla a dicha seccion
										if(!SECCIONES_RECHAZOS.contains(movimientoDto.getSeccionDTO().getId())){
											//Moviendo caratula comercio en flujo 
											FuncionarioVO funcionarioVO = new FuncionarioVO(rutUsuario);
											EstadoCaratulaVO estadoCaratulaFlujoVO = new EstadoCaratulaVO();
											estadoCaratulaFlujoVO.setEnviadoPor(funcionarioVO);
											estadoCaratulaFlujoVO.setFechaMovimiento(new Date());
											estadoCaratulaFlujoVO.setMaquina("AIO REINGRESO ESCRI");
											estadoCaratulaFlujoVO.setResponsable(new FuncionarioVO(movimientoDto.getResponsable().getRut()));
											SeccionVO seccionVO = new SeccionVO();
											seccionVO.setCodigo(movimientoDto.getSeccionDTO().getId());
											estadoCaratulaFlujoVO.setSeccion(seccionVO);

											caratulasUtil.moverCaratulaSeccion(caratula, estadoCaratulaFlujoVO );
											break;
										}
									}
								}
							}
						}

					} catch (Exception e) {

					}
					//Fin Reingresar Caratula

					//File uploaded successfully
					respuesta.put("message", "Fusionar OK");
					respuesta.put("status", true);
				}

			} catch (InvalidPdfException e) {
				logger.error(e.getMessage(), e);
				respuesta.put("message", "ERROR: Archivo pdf invalido");
				respuesta.put("status", false);
			} catch (BadPasswordException e) {
				logger.error(e.getMessage());
				respuesta.put("message", "ERROR: Archivo protegido con clave");
				respuesta.put("status", false);
			} catch (Exception ex) {
				logger.error("ERROR Fusionando",ex);
				ex.printStackTrace();
				//            	respuesta.put("message", "Problema Versionando" + ex);
				respuesta.put("message", "Problema Fusionando");
				respuesta.put("status", false);
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

	private void merge(List<InputStream> streamOfPDFFiles, OutputStream outputStream) throws Exception,
	DocumentException {
		//Rectangle pageSize = new Rectangle(0, 0, 740, 990);
		Rectangle pageSize1 = new Rectangle(0, 0, 660, 990);

		Document document = new Document(pageSize1);


		InputStream pdf = null;
		try {
			//int cantidadDePaginas = streamOfPDFFiles.size()+1;
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

					//                    if(currentPageNumber != 1){
					//                    	Rectangle pageSize = new Rectangle(0, 0, 740, 990);
					//                    	document.setPageSize(pageSize);
					//                    }

					pageOfCurrentReaderPDF++;
					currentPageNumber++;
					page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
					//                    if (currentPageNumber == 1) {
					////                        String fuente = TablaValores.getValor(ARCHIVO_PARAMETROS, "FONT_VERDANA", "valor");
					//                        PdfFormField sig = PdfFormField.createSignature(writer);
					//
					//                        sig.setWidget(new Rectangle(340, 250, 620, 128), null);
					//                        //sig.setWidget(new Rectangle(300, 250, 620, 128), null);
					//                        sig.setFlags(PdfAnnotation.FLAGS_PRINT);
					//                        sig.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g"));
					//                        sig.setFieldName("Signature1");
					//                        sig.setPage(currentPageNumber);
					//                        writer.addAnnotation(sig);
					//                    }
					cb.addTemplate(page, 0, 0);

				}
				pageOfCurrentReaderPDF = 0;
			}
			
			outputStream.flush();
		}
//		catch (Exception e) {
//			System.out.println(ErroresUtil.extraeStackTrace(e));
//			throw e;
//		}
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
							logger.error("no se pudo cerrar archivo: " + e.getMessage(),e);
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
	
	  private void liberate(InputStream  input, OutputStream output) throws IOException, DocumentException {	
		  com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(input);
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

