package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.DatosPropiedadDAO;
import cl.cbrs.aio.dto.datosPropiedad.ClienteDTO;
import cl.cbrs.aio.dto.datosPropiedad.Constants;
import cl.cbrs.aio.dto.datosPropiedad.EventoDTO;
import cl.cbrs.aio.dto.datosPropiedad.GP_DTO;
import cl.cbrs.aio.dto.datosPropiedad.HipotecaDTO;
import cl.cbrs.aio.dto.datosPropiedad.NotasDTO;
import cl.cbrs.aio.dto.datosPropiedad.ProhibicionDTO;
import cl.cbrs.aio.dto.datosPropiedad.PropiedadDTO;
import cl.cbrs.aio.dto.datosPropiedad.Util;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.borrador.delegate.WsBorradorDelegate;
import cl.cbrs.borrador.vo.ProrealVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.AnotacionVO;
import cl.cbrs.planos.vo.SimplePlanoVO;
import cl.cbrs.planos.vo.TituloPlanoVO;
import cl.cbrs.planos.ws.ServicioPlanoDelegate;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class AuditoriaServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(AuditoriaServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings("unchecked")
	public void buscarFolios(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");

		try{

			String borradorStr = request.getParameter("borrador")==null?"0":request.getParameter("borrador");
			
			int borrador = Integer.parseInt(borradorStr);
			
			//Buscar folios para un borrador
			ArrayList<Integer> listaFolios = DatosPropiedadDAO.buscaFoliosPorBorrador(borrador);
			if(listaFolios!=null && listaFolios.size()>0){
				for(Integer folio : listaFolios){
					JSONObject fila = new JSONObject();
					fila.put("folio", folio);
					resultado.add(fila);
				}
				
				status = true;
			}else{
				msg = "No se encontró ningun folio para borrador " + borrador;

				status = false;
			}

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarBorrador(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		JSONObject respuesta = new JSONObject();
//		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		final String MAX_ANYOS = TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "BUSCAR", "max_anyos");
		respuesta.put("maxAnyos", MAX_ANYOS);

		try{

			String borradorStr = request.getParameter("borrador")==null?"0":request.getParameter("borrador");
			String folioStr = request.getParameter("folio")==null?"0":request.getParameter("folio");
			String fechaHoyStr = request.getParameter("fechaHoy")==null?"":request.getParameter("fechaHoy");
			String anoStr = request.getParameter("ano")==null?MAX_ANYOS:request.getParameter("ano");
			
			int borrador = Integer.parseInt(borradorStr);
			int folio = Integer.parseInt(folioStr);
			Date fecha = new Date();
			String fechaHoy = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
			if(fechaHoyStr!=""){
				fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaHoyStr);
			}else{
				fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaHoy);
			}
			int anyos = Integer.parseInt(anoStr)>Integer.parseInt(MAX_ANYOS)? anyos=Integer.parseInt(MAX_ANYOS) : Integer.parseInt(anoStr);
			
			//BUSQUEDA LISTA PROPIEDADES
			propiedades(borrador, folio, fecha, anyos, respuesta);
			
			//BUSQUEDA LISTA HIPOTECAS
			hipotecas(borrador, folio, fecha, anyos, respuesta);

			//BUSQUEDA LISTA PROHIBICIONES
			prohibiciones(borrador, folio, fecha, anyos, respuesta);
			
			//BUSQUEDA FECHA ULTIMO GP
			ultimoGP(borrador, folio, respuesta);
			
			//EVENTOS
			eventos(borrador, folio, respuesta);
			
			//BUSQUEDA ROLES
			roles(borrador, folio, respuesta);						
			
			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

//		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarBorradorPorCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		JSONObject respuesta = new JSONObject();
		JSONArray listaBorradoresJSON = new JSONArray();
		Boolean status = false;
		String msg = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();
		WsBorradorDelegate borradorDelegate = new WsBorradorDelegate();

		try {							
			
			String caratulaStr = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
			
			logger.debug("Caratula: " + caratulaStr);
			
			Long caratula = new Long(caratulaStr);
			//Buscar inscripciones por caratula
			
			CaratulaVO caratulaVO = caratulaDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratula);
			if(caratulaVO!=null && caratulaVO.getInscripciones()!=null && caratulaVO.getInscripciones().length>0){
				InscripcionCitadaVO[] inscripcionesCitadaVO = caratulaVO.getInscripciones();
				for(InscripcionCitadaVO inscripcionCitadaVO : inscripcionesCitadaVO ){

					Integer fojas = inscripcionCitadaVO.getFoja();
					Integer numero = inscripcionCitadaVO.getNumero();
					Short anyo = inscripcionCitadaVO.getAno().shortValue();
					Boolean bis = inscripcionCitadaVO.getBis()!=null&&inscripcionCitadaVO.getBis().intValue()!=0?new Boolean(true) : new Boolean(false);
					
					List<ProrealVO> listaBorradores = borradorDelegate.obtenerBorradores(fojas, numero, anyo, bis);
					
					if(listaBorradores!=null && listaBorradores.size()>0){
						for(ProrealVO prorealVO : listaBorradores){
							JSONObject borradorJSON = new JSONObject();
							borradorJSON.put("borrador", prorealVO.getProRealId().getBorrador());
							borradorJSON.put("folio", prorealVO.getProRealId().getFolio());
							listaBorradoresJSON.add(borradorJSON);
						}

						respuesta.put("listaBorradores", listaBorradoresJSON);
						status=true;
					}else{
						msg="No se encontró ningun borrador para caratula " + caratula;
						status=false;
					}
				}
			} else{
				msg="No se encontró ningun borrador para caratula " + caratula;
				status=false;
			}						
					
		} catch (Exception e1) {
			logger.error("Error al buscar folios: ",e1);
			status=false;
			msg="Ocurri&oacute; un problema al realizar la consulta.";			
		}	
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);
		
		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: " + e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarBorradorPorInscripcion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		JSONObject respuesta = new JSONObject();
		JSONArray listaBorradoresJSON = new JSONArray();
		Boolean status = false;
		String msg = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");

		try {
			String fojasStr = request.getParameter("foja")==null?"0":request.getParameter("foja");
			String numeroStr = request.getParameter("numero")==null?"0":request.getParameter("numero");
			String anyoStr = request.getParameter("ano")==null?"0":request.getParameter("ano");
			
			logger.debug("Fojas: " + fojasStr);
			logger.debug("Numero: " + numeroStr);
			logger.debug("Anyo: " + anyoStr);

			Long fojas = new Long(fojasStr);
			String numero = numeroStr;
			Long anyo = new Long(anyoStr);

			//Buscar borradores por foja, numero, anyo
			WsInscripcionDigitalDelegate inscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();
			List<AnotacionVO> listaAnotaciones = inscripcionDigitalDelegate.obtenerListaBorradorFechaFolio(fojas, numero, anyo);

			if(listaAnotaciones!=null && listaAnotaciones.size()>0){
				for(AnotacionVO anotacion : listaAnotaciones){
					JSONObject borradorJSON = new JSONObject();
					borradorJSON.put("borrador", anotacion.getBorrador());
					borradorJSON.put("folio", anotacion.getFolio());
					listaBorradoresJSON.add(borradorJSON);
				}

				respuesta.put("listaBorradores", listaBorradoresJSON);
				status=true;
			}else{
				msg="No se encontró ningun borrador para inscripcion " + fojas +" - " + numero + " - " + anyo;
				status=false;
			}


		} catch (Exception e1) {
			logger.error("Error al buscar borradores por inscripcion: ",e1);
			status=false;
			msg="Ocurri&oacute; un problema al realizar la consulta.";			
		}	
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: " + e.getMessage(),e);
		}
	}
	
	private void propiedades(int borrador, int folio, Date fecha, int anyos, JSONObject respuesta) throws GeneralException {
		JSONObject datosPropiedadJSON = new JSONObject();
		JSONArray listaDueñosJSON = new JSONArray();
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		datosPropiedadJSON.put("fecha", formatoDeFecha.format(fecha));
		datosPropiedadJSON.put("anyos", anyos);

		ArrayList<PropiedadDTO> listaPropiedades = DatosPropiedadDAO.buscarPropiedad(folio, borrador);
		logger.debug("listaPropiedades size: " + listaPropiedades.size());

		if(listaPropiedades.size()==0){				
			respuesta.put("msg", "No se encontraron propiedades vigentes para b&uacute;squeda de borrador <strong>"+borrador+"</strong> folio <strong>"+folio+" </strong> y fecha <strong>"+fecha+" </strong> para los &uacute;ltimos <strong>"+anyos+" </strong> años");
		}

		respuesta.put("estado", true);

		ArrayList<PropiedadDTO> listaDuenyos = obtenerDuenyos(listaPropiedades);
		logger.debug("listaDuenyos size: " + listaDuenyos.size());

		boolean tieneNoVigente = false;
		
		for(PropiedadDTO propiedadDTO: listaDuenyos){
			JSONObject propiedadJSON = new JSONObject();
			propiedadJSON.put("fojas",propiedadDTO.getFoja());
			propiedadJSON.put("numero",propiedadDTO.getNumero());
			propiedadJSON.put("anyo",propiedadDTO.getAnyo());
			propiedadJSON.put("bis",propiedadDTO.getBis());

			JSONArray listaNotasJSON = new JSONArray();
			for(String nota: propiedadDTO.getListaNotas()){
				JSONObject notaJSON = new JSONObject();
				notaJSON.put("nota", nota);
				listaNotasJSON.add(notaJSON);
			}
			propiedadJSON.put("listaNotas", listaNotasJSON);

			propiedadJSON.put("vigente",propiedadDTO.getVigenteT()==1?true:false);
			
			if(propiedadDTO.getVigenteT()!=1){
				tieneNoVigente=true;
			}

			JSONArray listaClientes = new JSONArray();
			for(ClienteDTO clienteDTO:propiedadDTO.getDuenyos()){
				JSONObject clienteJSON = new JSONObject();
				clienteJSON.put("nombres",clienteDTO.getNombres());
				clienteJSON.put("vigente",clienteDTO.isVigente());
				listaClientes.add(clienteJSON);
			}
			propiedadJSON.put("clientes", listaClientes);

			//Datos Cabecera
			datosPropiedadJSON.put("borrador", propiedadDTO.getBorrador());
			datosPropiedadJSON.put("folio", propiedadDTO.getFolio());
			datosPropiedadJSON.put("direccion", propiedadDTO.getDireccion());
			datosPropiedadJSON.put("comuna", propiedadDTO.getComuna());
			datosPropiedadJSON.put("deslinde", propiedadDTO.getDeslindes());

			String url = "";																																																								
			String bisBoolean = propiedadDTO.getBis().intValue()==1?"true":"false";
			url = TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "HOSTS", "HOST_INSCRIPCION_DIGITAL")+"/digital/app/#/inscripcion/"+
			propiedadDTO.getFoja()+ "/" +
			propiedadDTO.getNumero()+ "/" +
			propiedadDTO.getAnyo()+ "/" +
			bisBoolean;

			propiedadJSON.put("urlPdf", url );
			propiedadJSON.put("tienePdf", false);

			listaDueñosJSON.add(propiedadJSON);
		}

		respuesta.put("tieneNoVigenteProp",tieneNoVigente);
		respuesta.put("duenyos",listaDueñosJSON);
		respuesta.put("datosPropiedad",datosPropiedadJSON);
	}
	
	/**
	 * Agrupa el listado de entrada por registro (FOJAS, NUMERO, AÑO) dejando en una sublista los
	 * nombres de clientes y su vigencia para una misma propiedad 
	 * @param listaPropiedades
	 * @return Retorna lista de propiedades agrupadas por registro(FOJAS, NUMERO, AÑO)
	 */
	private ArrayList<PropiedadDTO> obtenerDuenyos(ArrayList<PropiedadDTO> listaPropiedades) {
		ArrayList<PropiedadDTO> listaResultado = new ArrayList<PropiedadDTO>();
		
		for(PropiedadDTO propiedadDTO: listaPropiedades){
			if( !containsAndAdd(listaResultado,propiedadDTO) ){
				StringBuffer nombre = new StringBuffer();
				if( propiedadDTO.getApPaternoCli()!=null && !"".equals(propiedadDTO.getApPaternoCli()) )
					nombre.append(propiedadDTO.getApPaternoCli());
				if( propiedadDTO.getApMaternoCli()!=null && !"".equals(propiedadDTO.getApMaternoCli()) )
					nombre.append(" " + propiedadDTO.getApMaternoCli());
				if( propiedadDTO.getNombreCli()!=null && !"".equals(propiedadDTO.getNombreCli()) )
					nombre.append(", " + propiedadDTO.getNombreCli());
				
				ClienteDTO clienteDTO = new ClienteDTO();
				clienteDTO.setNombres(nombre.toString());
				clienteDTO.setVigente(propiedadDTO.getVigente().intValue()==1?true:false);
				propiedadDTO.getDuenyos().add(clienteDTO);
				
				listaResultado.add(propiedadDTO);
			} 
		}
		
		return listaResultado;
	}
	
	/**
	 * Recorre una lista de propiedades buscando un registro identico al parametro de entrada. 
	 * Si lo encuentra retorna verdadero y lo agrega a la lista de clientes de la propiedad
	 * @param lista
	 * @param propiedadParam
	 * @return
	 */
	private boolean containsAndAdd(ArrayList<PropiedadDTO> lista, PropiedadDTO propiedadParam) {
		
		for(PropiedadDTO propiedadDTO: lista){
			if(propiedadDTO.getFoja().equals(propiedadParam.getFoja()) 
			&& propiedadDTO.getNumero().equals(propiedadParam.getNumero())
			&& propiedadDTO.getAnyo().equals(propiedadParam.getAnyo()) ){
				StringBuffer nombre = new StringBuffer();
				nombre.append(propiedadParam.getApPaternoCli());
				nombre.append(" " + propiedadParam.getApMaternoCli());
				if( !"".equals(nombre.toString().trim()) && !"".equals(propiedadParam.getNombreCli()) )
					nombre.append(", ");
				nombre.append(propiedadParam.getNombreCli());
				
				ClienteDTO clienteDTO = new ClienteDTO();
				clienteDTO.setNombres(nombre.toString());
				clienteDTO.setVigente(propiedadParam.getVigente().intValue()==1?true:false);
				propiedadDTO.getDuenyos().add(clienteDTO);
				
				return true;
			}
		}
		return false;
	}
	
	private void hipotecas(int borrador, int folio, Date fecha, int anyos, JSONObject respuesta) {
		SimpleDateFormat formatoDeFechaGuion = new SimpleDateFormat("dd-MM-yyyy");
		JSONArray listaHipotecasJSON = new JSONArray();
		try{
			ArrayList<HipotecaDTO> listaHipotecas = DatosPropiedadDAO.buscarHipotecas(folio, borrador, anyos, fecha);
			logger.debug("listaHipotecas size: " + listaHipotecas.size());
			listaHipotecas = obtenerAcreedoresHipotecas(listaHipotecas);
			logger.debug("listaHipotecas agrupado size: " + listaHipotecas.size());
			
			boolean tieneNoVigente = false;
			
			for(HipotecaDTO hipotecaDTO: listaHipotecas){					
				
				//Cancelaciones de hipotecas
				int tieneCancelacion = DatosPropiedadDAO.buscarCancelacionesHipotecas( folio,
																borrador,
																hipotecaDTO.getFojas(),
																hipotecaDTO.getNumero(),
																hipotecaDTO.getAnyo(),
																hipotecaDTO.getBis(),
																formatoDeFechaGuion.format(fecha));							
				if(tieneCancelacion==1){
					logger.debug("Hipoteca a Fojas " + hipotecaDTO.getFojas() + " Numero " + hipotecaDTO.getNumero() + " Anyo " + hipotecaDTO.getAnyo() + " CANCELADA");
					hipotecaDTO.setEstado("0");
	//							continue; 
				}
				
				//Saltarse hipotecas del filtro naturaleza
				if( filtroNaturalezaHipotecas(hipotecaDTO.getCodNaturaleza()) )
					continue;
				
				JSONObject hipotecaJSON = new JSONObject();
				hipotecaJSON.put("fojas", hipotecaDTO.getFojas());
				hipotecaJSON.put("numero", hipotecaDTO.getNumero());
				hipotecaJSON.put("anyo", hipotecaDTO.getAnyo());
				hipotecaJSON.put("naturaleza", hipotecaDTO.getNaturaleza());
				hipotecaJSON.put("acreedor", hipotecaDTO.getAcreedor());
				if("0".equals(hipotecaDTO.getEstado()) ){
					hipotecaJSON.put("vigente",false);
					tieneNoVigente=true;
				}else{
					hipotecaJSON.put("vigente",true);
				}
					
				JSONArray listaNotasJSON = new JSONArray();
				for(NotasDTO nota: hipotecaDTO.getListaNotas()){
					JSONObject notaJSON = new JSONObject();
					notaJSON.put("nota", nota.getNota());
					notaJSON.put("caratula", nota.getCaratula());
					listaNotasJSON.add(notaJSON);
				}
				hipotecaJSON.put("listaNotas", listaNotasJSON);
				
				JSONArray listaAcreedoresJSON = new JSONArray();
				for(String acreedor: hipotecaDTO.getAcreedores()){
					JSONObject acreedorJSON = new JSONObject();
					acreedorJSON.put("acreedor", acreedor);
					listaAcreedoresJSON.add(acreedorJSON);
				}
				hipotecaJSON.put("listaAcreedores", listaAcreedoresJSON);						
				hipotecaJSON.put("resumen", Util.rtf2html(hipotecaDTO.getResumen()) );
				
//				try{
//					String nombreArchivo = Util.getPdf(Constants.HIPOTECA,hipotecaDTO.getFojas(),hipotecaDTO.getNumero(),hipotecaDTO.getAnyo());
//					String pathArchivo = Util.getPathPdfJs(Constants.HIPOTECA, hipotecaDTO.getAnyo());
//					if(!"".equals(nombreArchivo)){
//						hipotecaJSON.put("tienePdf", true);
//						hipotecaJSON.put("urlPdf", pathArchivo + nombreArchivo );
//					} else{
//						hipotecaJSON.put("tienePdf", false);
//						hipotecaJSON.put("urlPdf", "#" );
//					}
//				}catch(Exception e){
//					logger.error("Error al buscar archivos: " + e.getMessage(),e);
//					hipotecaJSON.put("tienePdf", false);
//					hipotecaJSON.put("urlPdf", "#" );
//				}
								
				listaHipotecasJSON.add(hipotecaJSON);
			}
			
			//HIPOTECAS CANCELADAS
			ArrayList<HipotecaDTO> listaHipotecasNoVigentes = DatosPropiedadDAO.buscarHipotecasNoVigentes(folio, borrador, anyos, fecha);
			logger.debug("listaHipotecasNoVigentes size: " + listaHipotecasNoVigentes.size());
			listaHipotecasNoVigentes = obtenerAcreedoresHipotecas(listaHipotecasNoVigentes);
			logger.debug("listaHipotecasNoVigentes agrupado size: " + listaHipotecasNoVigentes.size());
			
			for(HipotecaDTO hipotecaDTO: listaHipotecasNoVigentes){				
				
				if(hipotecaDTO.getFechaEstado()!=null && hipotecaDTO.getFechaEstado().compareTo(fecha)<=0)
					hipotecaDTO.setEstado("0");
				else
					hipotecaDTO.setEstado("1");
				
				//Saltarse hipotecas del filtro naturaleza
				if( filtroNaturalezaHipotecas(hipotecaDTO.getCodNaturaleza()) )
					continue;
				
				JSONObject hipotecaJSON = new JSONObject();
				hipotecaJSON.put("fojas", hipotecaDTO.getFojas());
				hipotecaJSON.put("numero", hipotecaDTO.getNumero());
				hipotecaJSON.put("anyo", hipotecaDTO.getAnyo());
				hipotecaJSON.put("naturaleza", hipotecaDTO.getNaturaleza());
				hipotecaJSON.put("acreedor", hipotecaDTO.getAcreedor());
				if("0".equals(hipotecaDTO.getEstado()) ){
					hipotecaJSON.put("vigente",false);
					tieneNoVigente=true;
				}else{
					hipotecaJSON.put("vigente",true);
				}
				
				JSONArray listaNotasJSON = new JSONArray();
				for(NotasDTO nota: hipotecaDTO.getListaNotas()){
					JSONObject notaJSON = new JSONObject();
					notaJSON.put("nota", nota.getNota());
					notaJSON.put("caratula", nota.getCaratula());
					listaNotasJSON.add(notaJSON);
				}
				hipotecaJSON.put("listaNotas", listaNotasJSON);
				
				JSONArray listaAcreedoresJSON = new JSONArray();
				for(String acreedor: hipotecaDTO.getAcreedores()){
					JSONObject acreedorJSON = new JSONObject();
					acreedorJSON.put("acreedor", acreedor);
					listaAcreedoresJSON.add(acreedorJSON);
				}
				hipotecaJSON.put("listaAcreedores", listaAcreedoresJSON);						
				
				hipotecaJSON.put("resumen", Util.rtf2html(hipotecaDTO.getResumen()) );
				
//				try{
//					String nombreArchivo = Util.getPdf(Constants.HIPOTECA,hipotecaDTO.getFojas(),hipotecaDTO.getNumero(),hipotecaDTO.getAnyo());
//					String pathArchivo = Util.getPathPdfJs(Constants.HIPOTECA, hipotecaDTO.getAnyo());
//					if(!"".equals(nombreArchivo)){
//						hipotecaJSON.put("tienePdf", true);
//						hipotecaJSON.put("urlPdf", pathArchivo + nombreArchivo );
//					} else{
//						hipotecaJSON.put("tienePdf", false);
//						hipotecaJSON.put("urlPdf", "#" );
//					}
//				}catch(Exception e){
//					logger.error("Error al buscar archivos: " + e.getMessage(),e);
//					hipotecaJSON.put("tienePdf", false);
//					hipotecaJSON.put("urlPdf", "#" );
//				}
								
				for(int i=0; i<listaHipotecasJSON.size(); i++){
					JSONObject json = (JSONObject)listaHipotecasJSON.get(i);
					int fojas = (Integer)json.get("fojas");
					int numero = (Integer)json.get("numero");
					int anyo = (Integer)json.get("anyo");
					if(fojas==hipotecaDTO.getFojas().intValue() && numero==hipotecaDTO.getNumero().intValue() && anyo==hipotecaDTO.getAnyo().intValue()){
						listaHipotecasJSON.remove(i);
					}
				}
				listaHipotecasJSON.add(hipotecaJSON);
			}					
			
			respuesta.put("tieneNoVigenteHipo",tieneNoVigente);
			respuesta.put("hipotecasVigentes", listaHipotecasJSON);
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}
			
	/**
	 * Agrupa el listado de entrada por registro (FOJAS, NUMERO, AÑO, NATURALEZA) dejando en una sublista los
	 * nombres de clientes y su vigencia para un mismo registro 
	 * @param listaPropiedades
	 * @return Retorna lista de acreedores 
	 */
	private ArrayList<HipotecaDTO> obtenerAcreedoresHipotecas(ArrayList<HipotecaDTO> listaPropiedades) {
		ArrayList<HipotecaDTO> listaResultado = new ArrayList<HipotecaDTO>();

		for(HipotecaDTO hipotecaDTO: listaPropiedades){					
			if( !containsAndAdd(listaResultado,hipotecaDTO) ){
				hipotecaDTO.getAcreedores().add(hipotecaDTO.getAcreedor());
				listaResultado.add(hipotecaDTO);
			} 
		}

		return listaResultado;
	}

	private boolean filtroNaturalezaHipotecas(int codNaturaleza) {

		String codigosStr = TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "FILTRO_NATURALEZA_HIPOTECAS", "codigos");
		String[] codigos = codigosStr.split(",");

		for(int i=0;i<codigos.length;i++){
			if(codNaturaleza==Integer.parseInt(codigos[i]))
				return true;
		}

		return false;
	}

	/**
	 * Recorre una lista de prohibiciones buscando un registro identico al parametro de entrada. 
	 * Si lo encuentra retorna verdadero y lo agrega a la lista de clientes del registro
	 * @param lista
	 * @param propiedadParam
	 * @return
	 */
	private boolean containsAndAdd(ArrayList<HipotecaDTO> lista, HipotecaDTO hipotecaParam) {

		for(HipotecaDTO hipotecaDTO: lista){
			if(hipotecaDTO.getFojas().equals(hipotecaParam.getFojas()) 
					&& hipotecaDTO.getNumero().equals(hipotecaParam.getNumero())
					&& hipotecaDTO.getAnyo().equals(hipotecaParam.getAnyo()) 
					&& hipotecaDTO.getCodNaturaleza().intValue()==hipotecaParam.getCodNaturaleza().intValue()	){
				//						hipotecaDTO.getAcreedores().add(hipotecaParam.getAcreedor());				
				return true;
			}
		}
		return false;
	}	
	
	private void prohibiciones(int borrador, int folio, Date fecha, int anyos, JSONObject respuesta) {
		SimpleDateFormat formatoDeFechaGuion = new SimpleDateFormat("dd-MM-yyyy");
		JSONArray listaProhibicionesJSON = new JSONArray();
		try{
			ArrayList<ProhibicionDTO> listaProhibiciones = DatosPropiedadDAO.buscarProhibiciones(folio, borrador, anyos, fecha);					
			logger.debug("listaProhibiciones size: " + listaProhibiciones.size());
			listaProhibiciones = obtenerAcreedoresProhibicion(listaProhibiciones);
			logger.debug("listaProhibiciones filtrado size: " + listaProhibiciones.size());
			boolean tieneNoVigente = false;
			for(ProhibicionDTO prohibicionDTO: listaProhibiciones){
				
				//Buscar cancelaciones de hipotecas
				int tieneCancelacion = DatosPropiedadDAO.buscarCancelacionesProhibiciones( folio,
																borrador,
																prohibicionDTO.getFojas(),
																prohibicionDTO.getNumero(),
																prohibicionDTO.getAnyo(),
																formatoDeFechaGuion.format(fecha));
				if(tieneCancelacion==1){
					logger.debug("Prohibicion a Fojas " + prohibicionDTO.getFojas() + " Numero " + prohibicionDTO.getNumero() + " Anyo " + prohibicionDTO.getAnyo() + " CANCELADA");
					prohibicionDTO.setEstado("0");
	//							continue;
				}						
				
				JSONObject prohibicionJSON = new JSONObject();
				prohibicionJSON.put("fojas", prohibicionDTO.getFojas());
				prohibicionJSON.put("numero", prohibicionDTO.getNumero());
				prohibicionJSON.put("anyo", prohibicionDTO.getAnyo());
				prohibicionJSON.put("naturaleza", prohibicionDTO.getNaturaleza());
				if("0".equals(prohibicionDTO.getEstado())){ 
					prohibicionJSON.put("vigente",false);
					tieneNoVigente=true;
				}else{
					prohibicionJSON.put("vigente",true);
				}
				
				JSONArray listaNotasJSON = new JSONArray();
				for(NotasDTO nota: prohibicionDTO.getListaNotas()){
					JSONObject notaJSON = new JSONObject();
					notaJSON.put("nota", nota.getNota());
					notaJSON.put("caratula", nota.getCaratula());
					listaNotasJSON.add(notaJSON);
				}
				prohibicionJSON.put("listaNotas", listaNotasJSON);
				
				JSONArray listaAcreedoresJSON = new JSONArray();
				for(String acreedor: prohibicionDTO.getAcreedores()){
					JSONObject acreedorJSON = new JSONObject();							
					acreedorJSON.put("acreedor", acreedor);
					listaAcreedoresJSON.add(acreedorJSON);
				}
				prohibicionJSON.put("listaAcreedores", listaAcreedoresJSON);
				
				prohibicionJSON.put("resumen", Util.rtf2html(prohibicionDTO.getResumen()) );
				
//				try{
//					String nombreArchivo = Util.getPdf(Constants.PROHIBICION,prohibicionDTO.getFojas(),prohibicionDTO.getNumero(),prohibicionDTO.getAnyo());
//					String pathArchivo = Util.getPathPdfJs(Constants.PROHIBICION, prohibicionDTO.getAnyo());
//					if(!"".equals(nombreArchivo)){
//						prohibicionJSON.put("tienePdf", true);
//						prohibicionJSON.put("urlPdf", pathArchivo + nombreArchivo );
//					} else{
//						prohibicionJSON.put("tienePdf", false);
//						prohibicionJSON.put("urlPdf", "#" );
//					}
//				}catch(Exception e){
//					logger.error("Error al buscar archivos: " + e.getMessage(),e);
//					prohibicionJSON.put("tienePdf", false);
//					prohibicionJSON.put("urlPdf", "#" );
//				}
				
				listaProhibicionesJSON.add(prohibicionJSON);
			}
		
		//PROHIBICIONES CANCELADAS
		ArrayList<ProhibicionDTO> listaProhibicionesNoVigentes = DatosPropiedadDAO.buscarProhibicionesNoVigentes(folio, borrador, anyos, fecha);
		listaProhibicionesNoVigentes = obtenerAcreedoresProhibicion(listaProhibicionesNoVigentes);
		for(ProhibicionDTO prohibicionDTO: listaProhibicionesNoVigentes){

			logger.debug("Prohibicion a Fojas " + prohibicionDTO.getFojas() + " Numero " + prohibicionDTO.getNumero() + " Anyo " + prohibicionDTO.getAnyo() + " CANCELADA");
			if(prohibicionDTO.getFechaEstado()!=null && prohibicionDTO.getFechaEstado().compareTo(fecha)<=0)
				prohibicionDTO.setEstado("0");
			else
				prohibicionDTO.setEstado("1");
		
			
			JSONObject prohibicionJSON = new JSONObject();
			prohibicionJSON.put("fojas", prohibicionDTO.getFojas());
			prohibicionJSON.put("numero", prohibicionDTO.getNumero());
			prohibicionJSON.put("anyo", prohibicionDTO.getAnyo());
			prohibicionJSON.put("naturaleza", prohibicionDTO.getNaturaleza());
			if("0".equals(prohibicionDTO.getEstado())){
				prohibicionJSON.put("vigente",false);
				tieneNoVigente=true;
			}else{
				prohibicionJSON.put("vigente",true);
			}
			
			JSONArray listaNotasJSON = new JSONArray();
			for(NotasDTO nota: prohibicionDTO.getListaNotas()){
				JSONObject notaJSON = new JSONObject();
				notaJSON.put("nota", nota.getNota());
				notaJSON.put("caratula", nota.getCaratula());
				listaNotasJSON.add(notaJSON);
			}
			prohibicionJSON.put("listaNotas", listaNotasJSON);
			
			JSONArray listaAcreedoresJSON = new JSONArray();
			for(String acreedor: prohibicionDTO.getAcreedores()){
				JSONObject acreedorJSON = new JSONObject();							
				acreedorJSON.put("acreedor", acreedor);
				listaAcreedoresJSON.add(acreedorJSON);
			}
			prohibicionJSON.put("listaAcreedores", listaAcreedoresJSON);
			
			prohibicionJSON.put("resumen", Util.rtf2html(prohibicionDTO.getResumen()) );
			
//			try{
//				String nombreArchivo = Util.getPdf(Constants.PROHIBICION,prohibicionDTO.getFojas(),prohibicionDTO.getNumero(),prohibicionDTO.getAnyo());
//				String pathArchivo = Util.getPathPdfJs(Constants.PROHIBICION, prohibicionDTO.getAnyo());
//				if(!"".equals(nombreArchivo)){
//					prohibicionJSON.put("tienePdf", true);
//					prohibicionJSON.put("urlPdf", pathArchivo + nombreArchivo );
//				} else{
//					prohibicionJSON.put("tienePdf", false);
//					prohibicionJSON.put("urlPdf", "#" );
//				}
//			}catch(Exception e){
//				logger.error("Error al buscar archivos: " + e.getMessage(),e);
//				prohibicionJSON.put("tienePdf", false);
//				prohibicionJSON.put("urlPdf", "#" );
//			}
			
			for(int i=0; i<listaProhibicionesJSON.size(); i++){
				JSONObject json = (JSONObject)listaProhibicionesJSON.get(i);
				int fojas = (Integer)json.get("fojas");
				int numero = (Integer)json.get("numero");
				int anyo = (Integer)json.get("anyo");
				if(fojas==prohibicionDTO.getFojas().intValue() && numero==prohibicionDTO.getNumero().intValue() && anyo==prohibicionDTO.getAnyo().intValue()){
					listaProhibicionesJSON.remove(i);
				}
			}
			listaProhibicionesJSON.add(prohibicionJSON);
		}					

		respuesta.put("tieneNoVigenteProh",tieneNoVigente);
		respuesta.put("prohibicionesVigentes", listaProhibicionesJSON);
		
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Agrupa el listado de entrada por registro (FOJAS, NUMERO, AÑO, NATURALEZA) dejando en una sublista los
	 * nombres de clientes y su vigencia para un mismo registro 
	 * @param listaPropiedades
	 * @return Retorna lista de acreedores 
	 */
	private ArrayList<ProhibicionDTO> obtenerAcreedoresProhibicion(ArrayList<ProhibicionDTO> listaPropiedades) {
		ArrayList<ProhibicionDTO> listaResultado = new ArrayList<ProhibicionDTO>();
		
		for(ProhibicionDTO prohibicionDTO: listaPropiedades){		
			
			if( !containsAndAdd(listaResultado,prohibicionDTO) ){
				if( filtroTipoAcreedor(prohibicionDTO.getTipoAcreedor()) && //Solo tipo acreedores permitidos en property
						!filtroSiglasProhibiciones(prohibicionDTO.getSigla()) && //Omitir siglas prohibidas en property
						!"".equals(prohibicionDTO.getAcreedor()) && prohibicionDTO.getAcreedor().indexOf(Constants.SIN_ACREEDOR)<0 && prohibicionDTO.getAcreedor().indexOf(Constants.SIN_ACREEDOR.toUpperCase())<0
				){
						prohibicionDTO.getAcreedores().add(prohibicionDTO.getAcreedor());
				}

				listaResultado.add(prohibicionDTO);
			} 
		}
		
		return listaResultado;
	}	

	private boolean filtroTipoAcreedor(String tipoAcreedor) {
		String codigosStr = TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "FILTRO_COMUNERO_PROHIBICION", "codigos");
		String[] codigos = codigosStr.split(",");
		
		for(int i=0;i<codigos.length;i++){
			if( tipoAcreedor.equals(codigos[i]) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * Recorre una lista de propiedades buscando un registro identico al parametro de entrada. 
	 * Si lo encuentra retorna verdadero y lo agrega a la lista de acreedores de la propiedad
	 * @param lista
	 * @param propiedadParam
	 * @return
	 */
	private boolean containsAndAdd(ArrayList<ProhibicionDTO> lista, ProhibicionDTO prohibicionParam) {
		
		for(ProhibicionDTO prohibicionDTO: lista){
			if(prohibicionDTO.getFojas().equals(prohibicionParam.getFojas()) 
			&& prohibicionDTO.getNumero().equals(prohibicionParam.getNumero())
			&& prohibicionDTO.getAnyo().equals(prohibicionParam.getAnyo()) 
			&& prohibicionDTO.getCodNaturaleza().intValue()==prohibicionParam.getCodNaturaleza().intValue()	){
				if( filtroTipoAcreedor(prohibicionDTO.getTipoAcreedor()) &&
						!filtroSiglasProhibiciones(prohibicionDTO.getSigla()) &&
						!"".equals(prohibicionDTO.getAcreedor()) && prohibicionDTO.getAcreedor().indexOf(Constants.SIN_ACREEDOR)<0 && prohibicionDTO.getAcreedor().indexOf(Constants.SIN_ACREEDOR.toUpperCase())<0
				){
						prohibicionDTO.getAcreedores().add(prohibicionDTO.getAcreedor());
				}				
				return true;
			}
		}
		return false;
	}
	
	private boolean filtroSiglasProhibiciones(String sigla) {
		
		String siglasStr = TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "FILTRO_SIGLAS_PROHIBICIONES", "codigos");
		String[] siglas = siglasStr.split(",");		
		
		for(int i=0;i<siglas.length;i++){
			if(sigla.equalsIgnoreCase(siglas[i]))
				return true;
		}
		
		return false;
	}
	
	private void roles(int borrador, int folio, JSONObject respuesta) {
		try{
			ArrayList<String> listaRoles = DatosPropiedadDAO.buscarRoles(borrador, folio);
			logger.debug("Lista roles size: " + listaRoles.size());
			JSONArray listaRolesJSON = new JSONArray();
			for(String rol : listaRoles){
				JSONObject rolJSON = new JSONObject();
				rolJSON.put("rol", rol);
				listaRolesJSON.add(rolJSON);
			}
			
			respuesta.put("listaRoles", listaRolesJSON);
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarPlano(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
		
		Boolean status = false;
		String msg = "";

        JSONObject respuesta = new JSONObject();
       
        response.setContentType("application/json; charset=ISO-8859-1");
        
        String listaInscripcionesReq = request.getParameter("listaInscripcionesPlano");
        logger.debug("listaInscripcionesReq: " + listaInscripcionesReq);
        
        JSONArray listaInscripcionesJSON = new JSONArray();
        
        try { 
        	JSONParser parser = new JSONParser();
        	listaInscripcionesJSON = (JSONArray)parser.parse(listaInscripcionesReq);
        	logger.debug("listaInscripcionesJSON size: " + listaInscripcionesJSON.size() );
        	TituloPlanoVO[] titulosPlanosVO = new TituloPlanoVO[listaInscripcionesJSON.size()];
        	
        	for (int i=0; i<listaInscripcionesJSON.size(); i++) {
			    JSONObject row = (JSONObject) listaInscripcionesJSON.get(i);			    
			    String fojas = (String)row.get("fojasProp");
			    String numero = (String)row.get("numeroProp");
			    String anyo = (String)row.get("anyoProp");
			    String bis = (String)row.get("bisProp");
			    TituloPlanoVO tituloPlanoVO = new TituloPlanoVO();
			    tituloPlanoVO.setFoja(new Integer(fojas));
			    tituloPlanoVO.setNumero(new Integer(numero));
			    tituloPlanoVO.setAno(new Short(anyo));
			    if("0".equals(bis))
			    	tituloPlanoVO.setBis(new Boolean("False"));
			    else
			    	tituloPlanoVO.setBis(new Boolean("True"));
			    titulosPlanosVO[i] = tituloPlanoVO;
			    
        	}
        	
        	//Buscar planos en WSPlanos
        	ServicioPlanoDelegate planosDelegate = new ServicioPlanoDelegate();
        	titulosPlanosVO = planosDelegate.obtenerPlanosPorTitulos(titulosPlanosVO );
        	
        	//Filtrar planos repetidos
        	ArrayList<Integer> categorias = new ArrayList<Integer>();
        	ArrayList<Integer> numeros = new ArrayList<Integer>();        	 
        	ArrayList aux = new ArrayList();
        	for(int i=0; i<titulosPlanosVO.length; i++){
        		SimplePlanoVO[] planosVO = titulosPlanosVO[i].getSimplePlano();
        		for(int j=0; j<planosVO.length; j++){        			
        			if(!aux.contains(planosVO[j].getIdCategoria()+"-"+planosVO[j].getNumeroPlano())){
        				categorias.add(planosVO[j].getIdCategoria().intValue());
        				numeros.add(planosVO[j].getNumeroPlano().intValue());
        				
        				aux.add(planosVO[j].getIdCategoria()+"-"+planosVO[j].getNumeroPlano());
        			}        			
        		}
        	}
        	
        	//Armar URL
        	JSONArray listaUrl = new JSONArray();
        	for(int i=0; i<categorias.size(); i++){
        		JSONObject urlJSON = new JSONObject();
        		urlJSON.put("url", Util.getUrlPlano(numeros.get(i),categorias.get(i)));
        		listaUrl.add(urlJSON);
//        		listaUrl.add(Util.getUrlPlano(numeros.get(i),categorias.get(i)));
        	}
        	
        	respuesta.put("urls", listaUrl); 
        	if(listaUrl.size()>0)
        		respuesta.put("tienePlanos", true);
        	else
        		respuesta.put("tienePlanos", false);

        	status=true;                
			
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage());
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}		
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
    }
	
	private void ultimoGP(int borrador, int folio, JSONObject respuesta) {		
		SimpleDateFormat formatoDeFechaConHora = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		JSONArray listaGPJSON = new JSONArray();
		try{
			ArrayList<GP_DTO> listaGP = DatosPropiedadDAO.buscarUltimosGP(folio, borrador);
			logger.debug("Lista GP size: " + listaGP.size() );

			if(listaGP.size()>0){			
				for(GP_DTO gpDTO:listaGP){
					JSONObject gpJSON = new JSONObject();
					if(gpDTO.getFechaGP()!=null)
						gpJSON.put("fecha", formatoDeFechaConHora.format(gpDTO.getFechaGP().getTime()));
					else
						gpJSON.put("fecha", "");
					gpJSON.put("caratula", gpDTO.getCaratula());
					gpJSON.put("nombreArchivo", gpDTO.getNombreArchivoVersion());
					gpJSON.put("codigoAlpha", gpDTO.getCodArchivoAlpha());
					listaGPJSON.add(gpJSON);	
				}

				respuesta.put("estadoGP", true);

				GP_DTO dto = listaGP.get(0);dto.getFechaGP().getTime();
				Date date = new Date(dto.getFechaGP().getTime());	
				respuesta.put("fechaUltimoGP", formatoDeFechaConHora.format(date));
			}else{
				respuesta.put("fechaUltimoGP", "");
				respuesta.put("msgGP", "No se encontraron datos de GP para borrador <strong>"+borrador+"</strong> folio <strong>"+folio+" </strong>");
			}

			respuesta.put("gp", listaGPJSON);
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	private void eventos(int borrador, int folio, JSONObject respuesta) throws GeneralException {
		SimpleDateFormat formatoDeFechaConHora = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		
		try{
			ArrayList<EventoDTO> listaEventos = DatosPropiedadDAO.buscarEventos(borrador, folio);
			logger.debug("Lista eventos size: " + listaEventos.size());					
				
			JSONArray listaEventosJSON = new JSONArray();
			
			for(EventoDTO eventoDTO:listaEventos){
				JSONObject eventoJSON = new JSONObject();
				
				if(eventoDTO.getFecha()!=null){
					Date date = new Date(eventoDTO.getFecha().getTime());						
					eventoJSON.put("fecha", formatoDeFechaConHora.format(date));
				}else
					eventoJSON.put("fecha", "");
				eventoJSON.put("aplicacion", eventoDTO.getAplicacion());
				eventoJSON.put("caratula", eventoDTO.getCaratula());
				eventoJSON.put("evento", eventoDTO.getEvento());
				eventoJSON.put("funcionario", eventoDTO.getFuncionario());
				
				listaEventosJSON.add(eventoJSON);
			}
	
			respuesta.put("numeroEventos", listaEventos.size());
			respuesta.put("eventos", listaEventosJSON);
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public void verGP(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
		String codigoAlpha= (String) request.getParameter("codigoAlpha");         
                
        ServletOutputStream out = null;
        
        URL urlNet;
        URI uri = null;

        try {    
        	out = response.getOutputStream();        	        	        	      
        	
            response.setContentType("application/pdf");
            byte[] buffer2 = null;
            out.write(buffer2, 0, buffer2.length);

            
            out.flush();
            
            if(out != null)
            	out.close();
        } catch (Exception e) {
        	logger.error("Error al buscar documento: " + e.getMessage(),e);
            request.setAttribute("error", "Archivo no encontrado.");
        } finally{
        	if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
        }
        
    }
	
	@SuppressWarnings("unchecked")
	public void buscarCaratulas(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String borradorStr = request.getParameter("borrador");
		String folioStr = request.getParameter("folio");
		
		JSONObject respuesta = new JSONObject();
		JSONArray listaCaratulasJSON = new JSONArray();		
		JSONArray listaCaratulasPropiedadJSON = new JSONArray();	
		
		Boolean status = false;
		String msg = "";
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();

		try {				
	        String listaPropiedadesReq = request.getParameter("listaPropiedades");
	        logger.debug("listaPropiedadesReq: " + listaPropiedadesReq);
	        
	        JSONArray listaPropiedadesJSON = new JSONArray();
	        
        	JSONParser parser = new JSONParser();
        	listaPropiedadesJSON = (JSONArray)parser.parse(listaPropiedadesReq);
        	logger.debug("listaInscripcionesJSON size: " + listaPropiedadesJSON.size() );
        	TituloPlanoVO[] titulosPlanosVO = new TituloPlanoVO[listaPropiedadesJSON.size()];
        	
        	for (int i=0; i<listaPropiedadesJSON.size(); i++) {
			    JSONObject row = (JSONObject) listaPropiedadesJSON.get(i);			    
			    String fojas = (String)row.get("fojasProp");
			    String numero = (String)row.get("numeroProp");
			    String anyo = (String)row.get("anyoProp");
			    String bis = (String)row.get("bisProp");

			    List<CaratulaVO> listaCaratulas = caratulaDelegate.obtenerCaratulasPorTitulo(new Long(fojas), numero, new Long(anyo), new Integer(bis), null);
			    
			    if(listaCaratulas!=null){
			    	JSONObject caratulaPorPropiedad = new JSONObject();
			    	caratulaPorPropiedad.put("fojas", fojas);
			    	caratulaPorPropiedad.put("numero", numero);
			    	caratulaPorPropiedad.put("anyo", anyo);
			    	caratulaPorPropiedad.put("bis", bis);
			    	
			    	JSONArray listaCaratulasPorPropiedadJSON = new JSONArray();	
				    for(CaratulaVO caratulaVO : listaCaratulas){
				    	
				    	if(!"Despachada".equalsIgnoreCase(caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion())  && !"Rechazada".equalsIgnoreCase(caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion())){
				    		JSONObject caratulaJSON = new JSONObject();

				    		if(caratulaVO.getEstadoActualCaratula()!=null)
				    			caratulaJSON.put("estadoCaratula", caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion());
				    		else
				    			caratulaJSON.put("estadoCaratula", "Sin Estado");
				    		if(caratulaVO.getFechaCreacion()!=null)
				    			caratulaJSON.put("fechaCreacionCaratula", formatoDeFecha.format(caratulaVO.getFechaCreacion()));
				    		else
				    			caratulaJSON.put("fechaCreacionCaratula", "Sin Fecha");
				    		caratulaJSON.put("numeroCaratula", caratulaVO.getNumeroCaratula());

				    		listaCaratulasPorPropiedadJSON.add(caratulaJSON);
				    	}
				    	
				    }
				    caratulaPorPropiedad.put("listaCaratulaPorPropiedad",listaCaratulasPorPropiedadJSON);
				    listaCaratulasPropiedadJSON.add(caratulaPorPropiedad);
			    }
			    
        	}
				
        	if(listaCaratulasPropiedadJSON.size()>0){
				respuesta.put("caratulas", listaCaratulasJSON);
				respuesta.put("caratulasPorPropiedad", listaCaratulasPropiedadJSON);
				status=true;
        	} else{
        		status=false;
				msg="No se encontraron car&aacute;tulas para b&uacute;squeda de borrador <strong>"+borradorStr+"</strong> folio <strong>"+folioStr+" </strong>";
			}
													
		} catch (Exception e1) {
			logger.error("Error al buscar Caratulas en proceso: " + e1.getMessage(),e1);
			msg="Ocurri&oacute; un problema al realizar la consulta.";
			status=false;
		}	
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: " + e.getMessage(),e);
		}
	}
	
	
}