package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.keycloak.KeycloakSecurityContext;

import cl.cbr.util.GeneralException;
import cl.cbr.util.RUTUtil;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.dto.SolicitudDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.InscripcionDigitalUtil;
import cl.cbrs.aio.util.SolicitudConverter;
import cl.cbrs.aio.util.SolicitudUtil;
import cl.cbrs.documentos.constantes.ConstantesDocumentos;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.inscripciondigitalh.vo.EstadoVO;
import cl.cbrs.inscripciondigitalh.vo.OrigenVO;
import cl.cbrs.inscripciondigitalh.vo.SolicitudVO;

public class SolicitudHipotecasServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(SolicitudHipotecasServiceAction.class);

		private static final Long ESTADO_EN_ESPERA = 1L; 
	//	private static final Integer ESTADO_DIGITALIZADO = 2; 
	//	private static final Integer ESTADO_RECHAZADO = 3; 
		private static final Long ESTADO_EN_ESPERA_DUPLICADO = 4L;
	//	private static final Integer ESTADO_REVISADO = 5; 

	private static final Integer ESTADO_RECHAZO_REVISADO = 6;
	//private static final Integer ESTADO_IMPRESO_ENTREGA_MANUAL = 7;

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void getByUserInSession(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador =context.getIdToken().getPreferredUsername();

		ArrayList<SolicitudDTO> solicitudDTOs = new ArrayList<SolicitudDTO>();

		Calendar cal = Calendar.getInstance(); // locale-specific
//		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, -5); 
		long time = cal.getTimeInMillis();


		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else{

			try {		
				String username = usuarioCreador.replaceAll("CBRS\\\\", "");
				
				WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();
				List<SolicitudVO>  solicitudVOs =  digitalDelegate.obtenerSolicitudesUsuarioDesde(username, new Date(time), null);

				if(solicitudVOs!=null && solicitudVOs.size()>0){

					SolicitudConverter solicitudConverter = new SolicitudConverter();

					for(SolicitudVO solicitudVO: solicitudVOs){

						if(solicitudVO.getEstado()!=null && 
								solicitudVO.getEstado().getIdEstado().intValue() != ESTADO_RECHAZO_REVISADO){
							SolicitudDTO solicitudDTO = solicitudConverter.getSolicitudDTO(solicitudVO);

							solicitudDTOs.add(solicitudDTO);

						}
					}
				}

				status = true;

			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("aaData", solicitudDTOs);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getByRut(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String rut = request.getParameter("rut");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		
		String rut1 = "";
		String dv1 = "";
		Long rutF = null;
		String dvF = "";
		String rutFinal = "";

		ArrayList<SolicitudDTO> solicitudDTOs = new ArrayList<SolicitudDTO>();

		if(StringUtils.isBlank(rut)){
			msg = "Se requiere rut para realizar búsqueda.";
		}else{

			try {		

				if(!StringUtils.isBlank(rut)){
					rut = rut.replaceAll("\\.", "");
					rut = rut.replaceAll("-", "");

					rut1 = rut.substring(0, rut.length()-1);
					dv1 = rut.substring(rut.length()-1, rut.length());

					if(!StringUtils.isBlank(dv1)){
						dv1 = dv1.toUpperCase();
					}else{
						dv1 = "";
					}

					rutF = Long.parseLong(rut1);
					dvF = dv1;

					Calendar cal = Calendar.getInstance(); // locale-specific
					cal.setTime(new Date());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

					cal.add(Calendar.DAY_OF_MONTH, -7);


					long time = cal.getTimeInMillis();

					WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

					List<SolicitudVO>  solicitudVOs =  digitalDelegate.obtenerSolicitudesPorRutDesde(rutF, dvF, new Date(time), null);

					if(solicitudVOs!=null && solicitudVOs.size()>0){

						SolicitudConverter solicitudConverter = new SolicitudConverter();

						for(SolicitudVO solicitudVO: solicitudVOs){
							
//							if(solicitudVO.getEstado()!=null && 
//									solicitudVO.getEstado().getIdEstado().intValue() != ESTADO_RECHAZO_REVISADO &&
//									solicitudVO.getEstado().getIdEstado().intValue() != ESTADO_IMPRESO_ENTREGA_MANUAL){
//								SolicitudDTO solicitudDTO = solicitudConverter.getSolicitudDTO(solicitudVO);
//
//								solicitudDTOs.add(solicitudDTO);
//							}

							if(solicitudVO.getEstado()!=null){
								SolicitudDTO solicitudDTO = solicitudConverter.getSolicitudDTO(solicitudVO);

								solicitudDTOs.add(solicitudDTO);
							}
						}
					}
					
					String resRut = NumberFormat.getInstance(new Locale("ES", "CL")).format(rutF);

					rutFinal = resRut + '-' + dv1;
					
					status = true;						

				}				
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("aaData", solicitudDTOs);
		respuesta.put("rut", rutFinal);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	


	@SuppressWarnings("unchecked")
	public void save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("data");
		String rut = request.getParameter("rut")==null?"":request.getParameter("rut"); 
		String nombre = request.getParameter("nombre")==null?"":request.getParameter("nombre"); 

		JSONObject respuesta = new JSONObject();
		
		JSONObject req = new JSONObject();
		req.put("nombre", nombre);
		req.put("rut", "");

		Boolean status = false;
		String msg = "";

		WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();
		//InscripcionDigitalVO inscripcionDigitalVO;

		HttpSession session = request.getSession(true);
		String origenParam = (String)session.getAttribute("origen");

		Long idOrigen = 0L;
		if(StringUtils.isNotBlank(origenParam)){

			try {
				idOrigen = Long.parseLong(origenParam);
			} catch (NumberFormatException e) {
				logger.error(e.getMessage(),e);
			}
		}

		JSONParser parser = new JSONParser();
		JSONArray arraySol = new JSONArray();
		Object obj;
		try {
			obj = parser.parse(data);

			arraySol = (JSONArray)obj;

		} catch (Exception e1) {
			logger.error(e1);
			msg = "Problemas obteniendo solicitudes.";	
		}

		if(arraySol!=null && arraySol.size()>0){
			try {
				//bloque rut
				Long rutF = null;
				String dvF = "";

				//boolean hayRut = false;
				//boolean rutOK = false;

				if(!StringUtils.isBlank(rut)){
					//hayRut = true;
					rut = rut.replaceAll("\\.", "");
					rut = rut.replaceAll("-", "");
					
					String rut1 = rut.substring(0, rut.length()-1);
					String dv1 = rut.substring(rut.length()-1, rut.length());
					
					if(StringUtils.isBlank(rut1) || StringUtils.isBlank(dv1)){
						msg = "Rut no válido.";														
					}else{

						if(!StringUtils.isBlank(dv1)){
							dv1 = dv1.toUpperCase();
						}else{
							dv1 = "";
						}

						if(StringUtils.isBlank(rut1)){
							msg = "Rut no válido.";	
						}else if(!RUTUtil.validaDigitoVerificador(rut1, dv1)){
							msg = "Rut no válido.";																				
						}else{
							try {
								rutF = Long.parseLong(rut1);
								dvF = dv1;
								
								
								req.put("rut", rutF+""+dvF);
								
								//rutOK = true;
							} catch (Exception e) {
								msg = "Rut no válido.";																				
							}
						}	
					}
				}

				//fin bloque rut

				try {
					//String username = usuarioCreador.replaceAll("CBRS\\\\", "");
					SolicitudVO solicitudVO = new SolicitudVO();

					//TODO tengo tabla origen, pero como lo vinculo con data funcionario?
					OrigenVO origenVO = new OrigenVO();

					//WsFuncionariosDelegate wsFuncionariosDelegate = new WsFuncionariosDelegate();

					//FuncionariosSeccionVO funcionariosSeccionVO =  wsFuncionariosDelegate.obtenerFuncionario(username);

					//SeccionOrigenVO seccionOrigenVO =  funcionariosSeccionVO.getSeccionOrigenVO();

					//SeccionOrigenVO seccionOrigenVO = new SeccionOrigenVO();
					//if(seccionOrigenVO!=null){	


					if(idOrigen!=null && idOrigen.intValue()==2){
						origenVO.setIdOrigen(2L);
						solicitudVO.setOrigen(origenVO);
						solicitudVO.setUsuario("SALA_ABOGADO");
					}else{
						//default exhibicion
						origenVO.setIdOrigen(1L);
						solicitudVO.setOrigen(origenVO);
						solicitudVO.setUsuario("EXHIBICION");						
					}

					solicitudVO.setRut(rutF);
					solicitudVO.setDv(dvF);
					solicitudVO.setNombre(nombre);
					solicitudVO.setImpreso(0);
					for(int i = 0; i<arraySol.size(); i++){
						JSONObject jobject = (JSONObject) arraySol.get(i);

						Long foja = (Long)jobject.get("foja");
						Long numero = (Long)jobject.get("numero");
						Long ano = (Long)jobject.get("ano");
						Boolean bis = (Boolean)jobject.get("bis");

						Integer bisI = bis?1:0;

						solicitudVO.setFoja(foja);
						solicitudVO.setNumero(numero);
						solicitudVO.setAno(ano);
						solicitudVO.setBis(bisI);

						solicitudVO.setFechaSolicitud(new Date());
						solicitudVO.setFechaEstado(new Date());

						digitalDelegate.agregarSolicitud(solicitudVO);
					}

					status = true;
				} catch (Exception e) {
					log.error(e);
					msg = "Se ha detectado un problema en el servidor.";
				}								
			} catch (Exception e1) {
				log.error(e1);
				msg = "Se ha detectado un problema en el servidor.";
			}			
		}

        respuesta.put("req", req);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}	
	
	
	
	@SuppressWarnings("unchecked")
	public void saveSingleConValidacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String fojap = request.getParameter("fojaSol");
		String numerop = request.getParameter("numeroSol");
		String anop = request.getParameter("anoSol");	
		String bisp = request.getParameter("bisSol");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		Boolean tieneRechazo = false;
		String msg = "";

		JSONObject estado = new JSONObject();

		estado.put("tipo", null);
		estado.put("descripcion", "");
		estado.put("esAnoDigital", false);
		estado.put("fna", true);	

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long numero = null;
		try {
			numero = Long.parseLong(numerop);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Integer bis = 0;
		Boolean bisInscripcion = false;
		if("true".equals(bisp)){
			bis = 1;
			bisInscripcion = true;
		}

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador =context.getIdToken().getPreferredUsername();
		usuarioCreador.replaceAll("CBRS\\\\", "");

		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(foja==null || foja.intValue()==0){
			msg = "Foja no válida.";
		}else if(numero==null || numero.intValue()==0){
			msg = "Número no válido.";
		}else if(ano==null || ano.intValue()==0){
			msg = "Año no válido.";
		}else{
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();
			
			try {
                InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
                ConsultaDocumentoDTO consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(foja, numero, ano, bisInscripcion, 2);
	
				if(consultaDocumentoDTO!=null){  //tengo respuesta?				
					
					if(consultaDocumentoDTO.getHayDocumento()){ //tengo papel?
						
						int tipoDocumento = consultaDocumentoDTO.getTipoDocumento();
						
						//que tipo de papel tengo?
						if(tipoDocumento == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO){
							
							estado.put("tipo", ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO);
							estado.put("descripcion", "VERSIONADA");

							status = true;
							
						}else if(tipoDocumento == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL || 
								tipoDocumento == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL){
							
							String descripcion = "";
							
							if(tipoDocumento == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL){
								descripcion = "REFERENCIAL";
							}else{
								descripcion = "ORIGINAL";
							}
							
							estado.put("tipo", tipoDocumento);
							estado.put("descripcion", descripcion);

							Boolean esDigital = digitalDelegate.validaAnosDigitales(foja, numerop, ano);						
							estado.put("esAnoDigital", esDigital);		
							
							status = true;						
						}else{
							msg = "Tipo de documento desconocido. Tipo: "+tipoDocumento;
						}
					}else{ //tengo respuesta, pero no tengo papel de ningún tipo
						Boolean fna = digitalUtil.consultaIndiceHipoteca(foja, numerop, ano);

						estado.put("fna", fna);

						if(fna){
							estado.put("tipo", 7);
							estado.put("descripcion", "No tiene imagen y es solicitable.");

							Boolean esDigital = digitalDelegate.validaAnosDigitales(foja, numerop, ano);						
							estado.put("esAnoDigital", esDigital);	
							
							SolicitudUtil solicitudUtil = new SolicitudUtil();
							
							Boolean resultado = solicitudUtil.solicitarHipotecas(usuarioCreador, foja, numero, ano, bis);
						
							status = resultado;
							
							if(!status){
								msg = "Se ha detectado un problema guardando solicitud.";
							}						
						}else{
							tieneRechazo = digitalDelegate.solicitudTieneRechazo(foja, numerop, ano, bis);

							if(tieneRechazo){
								estado.put("tipo", -1);
								estado.put("descripcion", "Tiene rechazo");
								
								status = true;
							}else{
								estado.put("tipo", 7);
								estado.put("descripcion", "No tiene imagen y es solicitable.");

								Boolean esDigital = digitalDelegate.validaAnosDigitales(foja, numerop, ano);						
								estado.put("esAnoDigital", esDigital);	
								
								SolicitudUtil solicitudUtil = new SolicitudUtil();
								
								Boolean resultado = solicitudUtil.solicitarHipotecas(usuarioCreador, foja, numero, ano, bis);
							
								status = resultado;

								if(!status){
									msg = "Se ha detectado un problema guardando solicitud.";
								}
							}							
						}
					}
				}else{
					msg = "Problemas consultando documento.";
				}
		

			} catch (Exception e1) {
				log.error(e1);
				msg = "Se ha detectado un problema en el servidor.";
			}
		}
		respuesta.put("status", status);
		respuesta.put("estado", estado);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}

	
	
	@SuppressWarnings("unchecked")
	public void saveSingle(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String fojap = request.getParameter("fojaSol");
		String numerop = request.getParameter("numeroSol");
		String anop = request.getParameter("anoSol");	
		String bisp = request.getParameter("bisSol");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";	

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long numero = null;
		try {
			numero = Long.parseLong(numerop);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Integer bis = 0;
		if("true".equals(bisp)){
			bis = 1;
		}

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador =context.getIdToken().getPreferredUsername();
		usuarioCreador.replaceAll("CBRS\\\\", "");
		
		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(foja==null || foja.intValue()==0){
			msg = "Foja no válida.";
		}else if(numero==null || numero.intValue()==0){
			msg = "Número no válido.";
		}else if(ano==null || ano.intValue()==0){
			msg = "Año no válido.";
		}else{
			
			SolicitudUtil solicitudUtil = new SolicitudUtil();
			
			Boolean resultado = solicitudUtil.solicitarHipotecas(usuarioCreador, foja, numero, ano, bis);
		
			status = resultado;

			if(!status){
				msg = "Se ha detectado un problema guardando solicitud.";
			}	
		}
		
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	
	@SuppressWarnings("unchecked")
	public void actualizar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String idSolicitudp = request.getParameter("idSolicitud");
		String accionp = request.getParameter("accion");

		String fechaEstado = "";
		Integer idEstado = 0;
		String descripcionEstado = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		Long idSolicitud = null;
		try {
			idSolicitud = Long.parseLong(idSolicitudp);
		} catch (Exception e1) {

		}

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador =context.getIdToken().getPreferredUsername();
		usuarioCreador.replaceAll("CBRS\\\\", "");
		
		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(idSolicitud==null || idSolicitud.intValue()==0){
			msg = "Solicitud no válida.";
		}else if(StringUtils.isBlank(accionp) && !"remove-rechazo".equals(accionp) && !"remove-ok".equals(accionp)){
			msg = "Acción no válida, intente de nuevo.";
		}else{
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {
				SolicitudVO solicitudVO = digitalDelegate.obtenerSolicitud(idSolicitud);

				Date fecha = new Date();

				fechaEstado = sdf.format(fecha);

				solicitudVO.setFechaEstado(fecha);

				EstadoVO estadoVO = new EstadoVO();

				if("remove-rechazo".equals(accionp)){
					estadoVO.setIdEstado(6L); 
					idEstado = 6;
					descripcionEstado = "Rechazo Revisado";
				}else if( "remove-ok".equals(accionp)){
					estadoVO.setIdEstado(5L); 
					idEstado = 5;
					descripcionEstado = "Revisado";
				}

				try {			
					solicitudVO.setEstado(estadoVO);

					digitalDelegate.actualizarSolicitud(solicitudVO);

					status = true;
				} catch (Exception e1) {
					log.error(e1);
					msg = "Se ha detectado un problema actualizando solicitud.";
				}
			} catch (Exception e) {
				log.error(e);
				msg = "Se ha detectado un problema obteniendo solicitud.";
			}
		}
		respuesta.put("status", status);
		respuesta.put("fechaEstado", fechaEstado);
		respuesta.put("idEstado", idEstado);
		respuesta.put("descripcionEstado", descripcionEstado);


		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void urgenciaSolicitud(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		
		Long foja = Long.parseLong(request.getParameter("foja"));
		String numero = request.getParameter("numero");
		Long ano = Long.parseLong(request.getParameter("ano"));
		String bis = request.getParameter("bis");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		Integer bisInscripcion = 0;
		if("true".equals(bis)){
			bisInscripcion = 1;
		}

		WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {
				digitalDelegate.solicitudUrgencia(foja, numero, ano, bisInscripcion);
				status = true;
			} catch (Exception e) {
				log.error(e);
				msg = "Se ha detectado un problema solicitando urgencia a la solicitud.";
			}
		
		respuesta.put("status", status);

		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void getByFojaNumeroAno(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		Long foja = Long.parseLong(request.getParameter("foja"));
		String numero = request.getParameter("numero");
		Long ano = Long.parseLong(request.getParameter("ano"));
		String bis = request.getParameter("bis");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		
		Integer bisInscripcion = 0;
		if("true".equals(bis)){
			bisInscripcion = 1;
		}

		ArrayList<SolicitudDTO> solicitudDTOs = new ArrayList<SolicitudDTO>();

		if(StringUtils.isBlank(foja.toString()) || StringUtils.isBlank(numero) || StringUtils.isBlank(ano.toString())){
			msg = "Se requiere todos los datos para realizar búsqueda.";
		}else{

			try {		

				WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

					List<SolicitudVO>  solicitudVOEsperas =  digitalDelegate.obtenerSolicitudesPorFojaNumAno(foja, numero, ano, bisInscripcion, ESTADO_EN_ESPERA);
					List<SolicitudVO>  solicitudVOEsperaDuplicados =  digitalDelegate.obtenerSolicitudesPorFojaNumAno(foja, numero, ano, bisInscripcion, ESTADO_EN_ESPERA_DUPLICADO);
					List<SolicitudVO>  solicitudVOs = new ArrayList<SolicitudVO>();
					
					if(null!= solicitudVOEsperas)
						solicitudVOs.addAll(solicitudVOEsperas);
					if(null!= solicitudVOEsperaDuplicados)
						solicitudVOs.addAll(solicitudVOEsperaDuplicados);

					if(solicitudVOs!=null && solicitudVOs.size()>0){

						SolicitudConverter solicitudConverter = new SolicitudConverter();

						for(SolicitudVO solicitudVO: solicitudVOs){

							if(solicitudVO.getEstado()!=null){
								SolicitudDTO solicitudDTO = solicitudConverter.getSolicitudDTO(solicitudVO);

								solicitudDTOs.add(solicitudDTO);
							}
						}
					}
					
					status = true;						

								
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}
		
		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("aaData", solicitudDTOs);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
}