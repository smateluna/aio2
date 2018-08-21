package cl.cbrs.aio.struts.action.service;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbrs.aio.dto.CartelDTO;
import cl.cbrs.aio.dto.CertificadoCartelDTO;
import cl.cbrs.aio.dto.TipoArchivoDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CartelConverter;
import cl.cbrs.aio.util.CartelesUtil;
import cl.cbrs.carteles.vo.CertificadoCartelVO;
import cl.cbrs.carteles.vo.EstadoCertificadoVO;
import cl.cbrs.carteles.vo.TipoArchivoVO;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CartelesServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(CartelesServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings("unchecked")
	public void buscarCartel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String numerop = request.getParameter("numero");
		String mesp = request.getParameter("mes");
		String anop = request.getParameter("ano");
		String registrop = request.getParameter("registro");
		String bisp = request.getParameter("bis");
		String tipop = request.getParameter("tipo");
		
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		CartelDTO cartelDTO = new CartelDTO();
		
		try {
			Integer numero = Integer.parseInt(numerop);
			Integer mes = Integer.parseInt(mesp);
			Integer ano = Integer.parseInt(anop);
			Integer registro = Integer.parseInt(registrop);
			Boolean bis = Boolean.parseBoolean(bisp);		
			Integer tipo = tipop!=null?Integer.parseInt(tipop):null;
			
			if( (numero==null || numero==0) || (mes==null || mes==0) || (ano==null || ano==0)){
				msg = "Se requiere  Número, Mes y Año";
			}else{
				CartelesUtil cartelesUtil = new CartelesUtil();
				
				String jsonStr = cartelesUtil.obtenerCartel(numero, mes, ano, bis, tipo, registro);
				JSONParser jsonParser = new JSONParser();
				JSONObject obtenerCartelJSON = (JSONObject)jsonParser.parse(jsonStr); 
				Long cantidadPaginas = (Long)obtenerCartelJSON.get("cantidadPaginas");
				Boolean hayArchivo = (Boolean)obtenerCartelJSON.get("hayArchivo");
				Long tipoArchivo = (Long)obtenerCartelJSON.get("tipoArchivo");
				TipoArchivoVO tipoArchivoVO = new TipoArchivoVO(tipoArchivo.intValue());
				TipoArchivoDTO tipoArchivoDTO = new CartelConverter().obtenerTipoArchivoDTO(tipoArchivoVO);
				cartelDTO.setTipoArchivoDTO(tipoArchivoDTO);
				cartelDTO.setCantidadPaginas(cantidadPaginas.intValue());
				cartelDTO.setHayArchivo(hayArchivo);
				cartelDTO.setAno(ano);
				cartelDTO.setNumero(numero);
				cartelDTO.setMes(mes);
				cartelDTO.setBis(bis);
				cartelDTO.setRegistro(registro);

				respuesta.put("cartelDTO", cartelDTO);
				status = true;
			}
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
			msg = "Se ha detectado un problema en el servicio de carteles.";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			msg = "Se ha detectado un problema en el servidor.";
		}	
		

		String sesion = request.getUserPrincipal().getName();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);	
		

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void getJPG(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String numerop = request.getParameter("numero");
		String mesp = request.getParameter("mes");
		String anop = request.getParameter("ano");
		String registrop = request.getParameter("registro");
		String bisp = request.getParameter("bis");
		String tipoArchivop = request.getParameter("tipoArchivo");
		String pagina = request.getParameter("pagina");
	
		try {
			Integer numero = Integer.parseInt(numerop);
			Integer mes = Integer.parseInt(mesp);
			Integer ano = Integer.parseInt(anop);
			Integer registro = Integer.parseInt(registrop);
			Boolean bis = Boolean.parseBoolean(bisp);		
			Integer tipoArchivo = Integer.parseInt(tipoArchivop);
			Integer page = Integer.parseInt(pagina);
			
			CartelesUtil cartelesUtil = new CartelesUtil();
			TipoArchivoVO tipoArchivoVO = new TipoArchivoVO();
			tipoArchivoVO.setTipo(tipoArchivo);

			byte[] imagen = cartelesUtil.obtenerImagenCartel(numero, mes, ano, page, tipoArchivoVO.getTipo(), bis, registro);

			OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
			response.setContentType("image/jpeg");
			out.write(imagen);
			out.close();      
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
		}
		           
	}
	
	@SuppressWarnings("unchecked")
	public void certificar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String numerop = request.getParameter("numero");
		String mesp = request.getParameter("mes");
		String anop = request.getParameter("ano");
		String registrop = request.getParameter("registro");		
		String bisp = request.getParameter("bis");
		String caratulap = request.getParameter("caratula");
		String pagdesdep = request.getParameter("pdesde");
		String paghastap = request.getParameter("phasta");
		String tipoArchivop = request.getParameter("tipo");
		
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		Long idCertificado = null;
		
		try {	
			Long numero = Long.parseLong(numerop);
			Integer mes = Integer.parseInt(mesp);
			Integer ano = Integer.parseInt(anop);
			Integer registro = Integer.parseInt(registrop);
			Long caratula = Long.parseLong(caratulap);
			Boolean bis = Boolean.parseBoolean(bisp);
			Integer pagDesde = Integer.parseInt(pagdesdep);
			Integer pagHasta = Integer.parseInt(paghastap);
			Integer tipoArchivo = Integer.parseInt(tipoArchivop);
		
			if( (mes==null || mes==0) || (ano==null || ano==0)){
				msg = "Se requiere al menos Mes y Año";
			}else{
				CartelesUtil cartelesUtil = new CartelesUtil();
							
				CertificadoCartelVO certificadoCartelVO = new CertificadoCartelVO();
				String nombreUsuario = request.getUserPrincipal().getName();
		        String soloNombre = nombreUsuario.substring(5, nombreUsuario.length());
				certificadoCartelVO.setUsuario(soloNombre);
				
				TipoArchivoVO tipoArchivoVO = new TipoArchivoVO();
				tipoArchivoVO.setTipo(tipoArchivo);
				certificadoCartelVO.setTipoArchivoVO(tipoArchivoVO);
				
				certificadoCartelVO.setCaratula(caratula);
				certificadoCartelVO.setNumero(numero);
				certificadoCartelVO.setMes(mes);
				certificadoCartelVO.setAno(ano);
				certificadoCartelVO.setBis(bis);
				certificadoCartelVO.setIdReg(registro);
				
				if(tipoArchivo==0){
					certificadoCartelVO.setPagDesde(pagDesde);
					certificadoCartelVO.setPagHasta(pagHasta);
				}
				
				Gson gson = new Gson();
				String certificadoCartelJSON = cartelesUtil.certificarImagenCartel(gson.toJson(certificadoCartelVO));
				certificadoCartelVO = gson.fromJson(certificadoCartelJSON, CertificadoCartelVO.class);
			  				  	
			  	idCertificado = certificadoCartelVO.getIdCertificado();

				status = true;
			}
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
			msg = "Se ha detectado un problema en el servicio de carteles.";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			msg = "Se ha detectado un problema en el servidor.";
		}		

		String sesion = request.getUserPrincipal().getName();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);	
		respuesta.put("idCertificado", idCertificado);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void listadoCertificadosByUserInSession(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");
		ArrayList<CertificadoCartelDTO> dtos = new ArrayList<CertificadoCartelDTO>();
		
		if(StringUtils.isBlank(usuario)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else{

			try {		
				CartelesUtil cartelesUtil = new CartelesUtil();				
				CartelConverter converter = new CartelConverter();
				Gson gson = new Gson();
				
				String certificadosJSON = cartelesUtil.obtenerListaCertificadosPorUsuario(usuario, EstadoCertificadoVO.ESPERA_DE_FIRMA, null);
				CertificadoCartelVO[] vos = gson.fromJson(certificadosJSON, CertificadoCartelVO[].class);
				dtos = converter.obtenerArrayCertificadoCartelDTO(vos);

				status = true;

			} catch (IOException e) {
				logger.error(e.getMessage() + " " + e.getCause(), e);
				msg = "Se ha detectado un problema en el servicio de carteles.";
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("aaData", dtos);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void getPDF(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String idCertificadop = request.getParameter("idCertificado");
		String registrop = request.getParameter("registro");
		
		try {
			Long idCertificado = Long.parseLong(idCertificadop);
			Integer registro = Integer.parseInt(registrop);
			CartelesUtil cartelesUtil = new CartelesUtil();	

			byte[] pdf = cartelesUtil.obtenerImagenCertificado(idCertificado, registro);				
			OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
			response.setContentType("application/pdf");

			out.write(pdf);
			out.flush();
			out.close();      

		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
		}
		         
	}
	
	@SuppressWarnings("unchecked")
	public void rehacer(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String numerop = request.getParameter("numero");
		String mesp = request.getParameter("mes");
		String anop = request.getParameter("ano");
		String registrop = request.getParameter("registro");		
		String bisp = request.getParameter("bis");
		String pagdesdep = request.getParameter("pdesde");
		String paghastap = request.getParameter("phasta");
		
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		Long idCertificado = null;
		
		try {	
			Integer numero = Integer.parseInt(numerop);
			Integer mes = Integer.parseInt(mesp);
			Integer ano = Integer.parseInt(anop);
			Integer registro = Integer.parseInt(registrop);
			Boolean bis = Boolean.parseBoolean(bisp);
			Integer pagDesde = Integer.parseInt(pagdesdep);
			Integer pagHasta = Integer.parseInt(paghastap);
			
			if( (mes==null || mes==0) || (ano==null || ano==0)){
				msg = "Se requiere al menos Mes y Año";
			}else{			
				CartelesUtil cartelesUtil = new CartelesUtil();				
				cartelesUtil.generarCartel(numero, mes, ano, bis, registro, pagDesde, pagHasta);
			  				  	
				status = true;
			}
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
			msg = "Se ha detectado un problema en el servicio de carteles.";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			msg = "Se ha detectado un problema en el servidor.";
		}	

		String sesion = request.getUserPrincipal().getName();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);	
		respuesta.put("idCertificado", idCertificado);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void eliminar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String idCertificadop = request.getParameter("idCertificado");
		String registrop = request.getParameter("registro");
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		try {	
			Long idCertificado = Long.parseLong(idCertificadop);
			Integer registro = Integer.parseInt(registrop);
			CartelesUtil cartelesUtil = new CartelesUtil();			
			cartelesUtil.eliminarCertificado(idCertificado, registro);		
		  				  	
			respuesta.put("idCertificado", idCertificado);
			status = true;
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
			msg = "Se ha detectado un problema en el servicio de carteles.";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			msg = "Se ha detectado un problema en el servidor.";
		}	
		

		String sesion = request.getUserPrincipal().getName();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);			

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void firmar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String idCertificadop = request.getParameter("idCertificado");
		String registrop = request.getParameter("registro");		
		
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		try {	
			Long idCertificado = Long.parseLong(idCertificadop);
			Integer registro = Integer.parseInt(registrop);
			CartelesUtil cartelesUtil = new CartelesUtil();	
			cartelesUtil.firmarCartel(idCertificado, registro);
		  	
			respuesta.put("idCertificado", idCertificado);
			status = true;			
		} catch (HTTPException e) {
			logger.error("Error HTTP_"+e.getStatusCode(), e);
			msg = "Se ha detectado un problema en el servicio de carteles.";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			msg = "Se ha detectado un problema en el servidor.";
		}		

		String sesion = request.getUserPrincipal().getName();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);			
		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
}