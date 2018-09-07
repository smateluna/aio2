package cl.cbrs.aio.struts.action.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.ErroresUtil;
import cl.cbr.util.RUTUtil;
import cl.cbr.util.SendMail;
import cl.cbr.util.StringUtil;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.DatosPropiedadDAO;
import cl.cbrs.aio.dao.FirmaEDAO;
import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.CaratulaEstadoDTO;
import cl.cbrs.aio.dto.estado.CausalRechazoDTO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.dto.estado.IngresoEgresoDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.dto.estado.ProductoWebDTO;
import cl.cbrs.aio.dto.estado.RepertorioDTO;
import cl.cbrs.aio.dto.estado.TareaDTO;
import cl.cbrs.aio.dto.firmaElectronica.EntregaEnLineaDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulaEstadoUtil;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.EntregaEnLineaUtil;
import cl.cbrs.aio.util.ParametrosUtil;
import cl.cbrs.aio.util.ReporteUtil;
import cl.cbrs.aio.util.TemplateMaker;
import cl.cbrs.caratula.flujo.vo.AnulaCaratulaVO;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CausalRechazoVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.ProductoGlosaVO;
import cl.cbrs.caratula.flujo.vo.ProductoVO;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TareaVO;
import cl.cbrs.caratula.flujo.vo.TipoTareaVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.delegate.repertorio.WsRepertorioClienteDelegate;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;
import net.sf.jasperreports.engine.JRException;

public class UtilServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(UtilServiceAction.class);
	
	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	
	@SuppressWarnings("unchecked")
	public void getEstadoReporteFirstPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		SimpleDateFormat sdfReporte = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date fecha = new Date();
		String fechaReporte = sdfReporte.format(fecha);

		String ncaratula = request.getParameter("nc");
		String tipo = "pdf";
		String ingresoEgresop = "false";
		
		boolean hayIngresoEgreso = false;

		if(ingresoEgresop!=null && "true".equals(ingresoEgresop)){
			hayIngresoEgreso = true;
		}	
		
		CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();		
		
		try {
			ServletOutputStream out = response.getOutputStream();
			String[] caratulas = ncaratula.split(",");
			List<InputStream> listaCarautlas = new ArrayList<InputStream>();
			for(int i=0; i<caratulas.length; i++){
				Long numeroCaratula = new Long(caratulas[i]);
				
				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
				CaratulaEstadoDTO caratulaEstadoDTO = caratulaEstadoUtil.getCaratulaEstadoDTO(caratulaVO);
				JSONObject respuesta = caratulaEstadoUtil.estadoReporteJSON(caratulaEstadoDTO, numeroCaratula);
				JSONObject data = (JSONObject)respuesta.get("data");								
	
				HashMap<String, Object> map = new HashMap<String, Object>();
	
				map.put("fechaReporte", fechaReporte);
				map.put("ncaratula", ""+data.get("ncaratula"));
				map.put("fechaIngreso", ""+data.get("fechaIngreso"));
				map.put("nform", ""+data.get("nform"));
				map.put("tform", ""+data.get("tform"));
				map.put("vtasado", ""+data.get("vtasado"));
				map.put("vpagado", ""+data.get("vpagado"));
				map.put("vreal", ""+data.get("vreal"));
				map.put("diferencia", ""+data.get("diferencia"));
				map.put("repertorio", ""+data.get("repertorio"));
	
				map.put("rut", ""+data.get("rut2"));
				map.put("nombres", ""+data.get("nombres"));
				map.put("apellidop", ""+data.get("apaterno"));
				map.put("apellidom", ""+data.get("amaterno"));			
				map.put("email", ""+data.get("email"));
				map.put("giro", ""+data.get("giro"));			
				map.put("direccion", ""+data.get("direccion"));
	
				map.put("telefono", ""+data.get("telefono"));
				map.put("ccc", ""+data.get("ccc"));
	
				map.put("codigocc", ""+data.get("codigocc"));
				map.put("rutcc", ""+data.get("rutcc"));
				map.put("institucion", ""+data.get("institucion"));
	
				map.put("seccionActual", ""+data.get("seccionActual"));
				map.put("fechaActual", ""+data.get("fechaActual"));
	
				map.put("obs", ""+data.get("obs"));
				
				map.put("citadoFoja", ""+data.get("citadoFoja"));
				map.put("citadoNum", ""+data.get("citadoNum"));
				map.put("citadoAno", ""+data.get("citadoAno"));
				map.put("citadoRegistroNombre", ""+data.get("citadoRegistroNombre"));
	
	
				if("1".equals(""+data.get("canal"))){
					map.put("canal", "Caja");				
				}else if("2".equals(""+data.get("canal"))){
					map.put("canal", "Web");				
				}else{
					map.put("canal", "-");				
				}
				
				String detalle = "";
				
				if(caratulaVO!=null){
					ProductoVO productoVO = caratulaVO.getProducto();
					if(productoVO != null){
						ProductoGlosaVO[] listaProductoGlosa= caratulaVO.getProducto().getListaProductoGlosaVO();
						if(listaProductoGlosa!=null){
							for(ProductoGlosaVO prodGlosaVO : listaProductoGlosa){
								detalle +=prodGlosaVO.getGlosa()+"\n";
							}
						}
					}
				}
	
				map.put("detalle", detalle);
	
				//TAREAS
				ArrayList<TareaDTO> tareas = (ArrayList<TareaDTO>)data.get("tareas");
	
				HashMap<String, String> map2 = new HashMap<String, String>();
				map2.put("fechaReporte", fechaReporte);
				map2.put("ncaratula", ""+data.get("ncaratula"));	
	
				//REPERTORIO
				WsRepertorioClienteDelegate repertorioClienteDelegate = new WsRepertorioClienteDelegate();
				List<RepertorioVO> repertorioVOs = repertorioClienteDelegate.existeCaratulaConRepertorio(numeroCaratula);			
	
				if("pdf".equals(tipo))
					response.setContentType("application/pdf");
				
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				bout = ReporteUtil.export(map, tareas, null, map2, null, tipo, null, hayIngresoEgreso, null);
				InputStream is = new ByteArrayInputStream(bout.toByteArray());
				listaCarautlas.add(is);							
			}
			merge(listaCarautlas, out);
			
			if(out != null)
	           	out.close();

		} catch (IOException e) {
			logger.error("Error IO al generar PDF: " + e.getMessage(), e);
		} catch (JRException e) {
			logger.error("Error JRE al generar PDF: " + e.getMessage(), e);
		} catch (GeneralException e) {
			logger.error("Error General al generar PDF: " + e.getMessage(), e);
		} catch (DocumentException e) {
			logger.error("Error Documento al fusionar PDFs: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Error General al generar PDF: " + e.getMessage(), e);
		}
	}
	
	private void merge(List<InputStream> streamOfPDFFiles, OutputStream outputStream) throws Exception,
	DocumentException {

//		Rectangle pageSize1 = new Rectangle(0, 0, 660, 990);

		Document document = new Document();


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

					pageOfCurrentReaderPDF++;
					currentPageNumber++;
					page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);

					cb.addTemplate(page, 0, 0);

				}
				pageOfCurrentReaderPDF = 0;
			}

			outputStream.flush();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
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
							logger.error(e.getMessage(),e);
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
	
	
	
}