package cl.cbrs.aio.struts.action.service;


import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.keycloak.KeycloakSecurityContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.AnteriorDAO;
import cl.cbrs.aio.dao.FolioRealDAO;
import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.BorradorDTO;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.AnotacionUtil;
import cl.cbrs.aio.util.BorradoresUtil;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.ConstantesDigital;
import cl.cbrs.aio.util.ConverterVoToDtoMachine;
import cl.cbrs.aio.util.InscripcionDigitalUtil;
import cl.cbrs.aio.util.RestUtil;
import cl.cbrs.documentos.constantes.ConstantesDocumentos;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.inscripciondigital.util.ConstantesInscripcionDigital;

public class InscripcionDigitalHipotecasServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(InscripcionDigitalHipotecasServiceAction.class);

	private static int CARATULAS_EN_PROCESO = 0;
	private static int CARATULAS_FINALIZADAS = 1;
	private static long ANOTACION_FOLIADA = 6;
	private static String WS_ALERTAS = "ws_alertas.parametros";
	private static String WS_CARATULA = "ws_caratula.parametros";

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void getInscripcion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");	
		String bisp = request.getParameter("bis");	

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long numeroS = null;
		try {
			numeroS = Long.parseLong(numero);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Short anoShort = null;

		try {
			anoShort = Short.parseShort(anop);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		Boolean bis = false;
		Integer bisi = 0;
		if("true".equals(bisp)){
			bis = true;	
			bisi = 1;
		}

		//generales
		Boolean status = false;
		String msg = "";
		String msgCaratulas = "";
		String msgBorradores = "";
		Boolean seDigitalizoEnElDia = false;

		//estado
		Boolean estadoFna = false;
		Boolean estadoEsAnoDigital = false;
		Boolean estadoTieneRechazo = false;
		Boolean rehacerImagen = false;


		//contadores
		Integer contadorBorrador = 0;
		Integer contadorProceso = 0;
		Integer contadorTerminada = 0;

		InscripcionDigitalDTO inscripcionDigitalDTO = new InscripcionDigitalDTO();

		inscripcionDigitalDTO.setAno(ano);
		inscripcionDigitalDTO.setFoja(foja);
		inscripcionDigitalDTO.setNumero(numero);
		inscripcionDigitalDTO.setBis(bis);

		InscripcionDigitalDTO inscripcionAnteriorDTO = new InscripcionDigitalDTO();
		InscripcionDigitalDTO inscripcionSiguienteDTO = new InscripcionDigitalDTO();
		
		JSONArray alertas = null;

		ConsultaDocumentoDTO consultaDocumentoDTO = new ConsultaDocumentoDTO();

		if( (foja==null || foja==0) || (StringUtils.isBlank(numero) || !StringUtils.isNumeric(numero)) || (ano==null || ano==0)){
			msg = "Se requiere Foja, Número y Año";
		}else{

			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {
				//Buscar alertas
				Client client = Client.create();
				String ip = TablaValores.getValor(WS_ALERTAS, "IP_WS", "valor");
				String port = TablaValores.getValor(WS_ALERTAS, "PORT_WS", "valor");
				WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/alertas/byInscripcion/"+foja+"/"+numero+"/"+ano+"/2/"+bisi));
				ClientResponse clientResponse = wr.type("application/json").get(ClientResponse.class);
				com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

				if(statusRespuesta.getStatusCode() == 200){
					alertas = (JSONArray) RestUtil.getResponse(clientResponse);
				} else{
					msg = "No se pudo verificar si existen alertas para esta inscripcion";
					throw new Exception("No se pudo verificar si existen alertas para esta inscripcion");
				}
				
				estadoTieneRechazo = digitalDelegate.solicitudTieneRechazo(foja, numero, ano, bisi);

				if(estadoTieneRechazo){
					status = true;
				}else{						
					InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
					consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(foja, numeroS, ano, bis, 2);

					if(consultaDocumentoDTO!=null){

						if(consultaDocumentoDTO.getHayDocumento()){//hay papel
							estadoFna = true;
							if(consultaDocumentoDTO.getTipoDocumento()== ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO){
								//tenemos imagen al dia
								ConverterVoToDtoMachine converter = new ConverterVoToDtoMachine();

								cl.cbrs.inscripciondigitalh.vo.InscripcionAnteriorSiguienteVO as = digitalDelegate.obtenerInscripcionAnteriorSiguiente(foja, numero, ano);

								if(as!=null){										
									if(as.getInscripcionDigitalAnterior()!=null)
										inscripcionAnteriorDTO = converter.getInscripcionDigitalDTO(false, as.getInscripcionDigitalAnterior());

									if(as.getInscripcionDigitalSiguiente()!=null)
										inscripcionSiguienteDTO = converter.getInscripcionDigitalDTO(false, as.getInscripcionDigitalSiguiente());
								}

								cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO inscripcionDigitalVO = digitalDelegate.obtenerInscripcionDigital(foja, numero, ano, bis);
								inscripcionDigitalDTO = converter.getInscripcionDigitalDTO(true, inscripcionDigitalVO);		
								
								if(inscripcionDigitalDTO.getFechaActualizacion()!=null){
									Date fechaActual = new Date();
									SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy"); 
								    String fechaActualizacionIns = formateador.format(inscripcionDigitalDTO.getFechaActualizacion());
								    String fechaSistema = formateador.format(fechaActual);
								    if(fechaSistema.compareTo(fechaActualizacionIns)==0){
								    	rehacerImagen=true;
								    }
								}
							    
								estadoEsAnoDigital = true;
								status = true;															
							}else if(consultaDocumentoDTO.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL ||
									consultaDocumentoDTO.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL){
								//es referencial

								estadoEsAnoDigital = digitalDelegate.validaAnosDigitales(foja, numero, ano);
								
								DateFormat formateador = new SimpleDateFormat("yyyy-MM-dd"); 
								String fechaArchivo = formateador.format(consultaDocumentoDTO.getFechaArchivo());
								String fechaActual = formateador.format(new Date());
								
								if(fechaArchivo.compareTo(fechaActual)==0)
									seDigitalizoEnElDia=true;
								
								status = true;
							}else{
								//tipo de documento desconocido
								msg = "Tipo de documento desconocido. Tipo:"+consultaDocumentoDTO.getTipoDocumento();
							}
							
							//Contar borradores
							try{
							FolioRealDAO folioRealDAO = new FolioRealDAO();
							contadorBorrador = folioRealDAO.getCantidadBorradoresDesdeH(foja.intValue(), numeroS.intValue(), anoShort, bis);
							} catch(Exception e){
								msgBorradores = "No se pudo obtener borradores relacionados a esta inscripcion";
								contadorBorrador=-1;
							}
							
							//Contar caratulas en proceso y terminadas
							try{
								String ipCaratula = TablaValores.getValor(WS_CARATULA, "IP_WS", "valor");
								String portCaratula = TablaValores.getValor(WS_CARATULA, "PORT_WS", "valor");
								wr = client.resource(new URI("http://"+ipCaratula+":"+portCaratula+"/CaratulaRest/caratula/cantidadCaratulasPorTitulo/2/"+foja+"/"+numero+"/"+ano+"/"+bisi+"/"+CARATULAS_EN_PROCESO));
								clientResponse = wr.type("application/json").get(ClientResponse.class);
								statusRespuesta = clientResponse.getClientResponseStatus();
								if(statusRespuesta.getStatusCode() == 200){
									contadorProceso = Integer.parseInt((String)RestUtil.getResponse(clientResponse));
								} else{
									contadorProceso=-1;
									msgCaratulas = "No se pudo obtener caratulas en proceso relacionadas a esta inscripción";
								}
								
								wr = client.resource(new URI("http://"+ipCaratula+":"+portCaratula+"/CaratulaRest/caratula/cantidadCaratulasPorTitulo/2/"+foja+"/"+numero+"/"+ano+"/"+bisi+"/"+CARATULAS_FINALIZADAS));
								clientResponse = wr.type("application/json").get(ClientResponse.class);
								statusRespuesta = clientResponse.getClientResponseStatus();
								if(statusRespuesta.getStatusCode() == 200){
									contadorTerminada = Integer.parseInt((String)RestUtil.getResponse(clientResponse));
								} else{
									contadorTerminada=-1;
									msgCaratulas = "No se pudo obtener caratulas terminadas relacionadas a esta inscripción";
								}	
							} catch(Exception e){
								msgCaratulas = "No se pudo obtener caratulas relacionadas a esta inscripcion";
								contadorProceso=-1;
								contadorTerminada=-1;
							}
						}else{										
							estadoFna = digitalUtil.consultaIndiceHipoteca(foja, numero, ano);
							estadoEsAnoDigital = digitalDelegate.validaAnosDigitales(foja, numero, ano);							
							status = true;
						}
					}else{
						//viene null
						msg = "Problemas consultando documento.";
					}
				}				
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			} catch (cl.cbr.common.exception.GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			} catch (URISyntaxException e) {
				log.error(e.getMessage(),e);
				msg = "No se pudo verificar si existen alertas para esta inscripcion";
			} catch (HTTPException e) {
				log.error(e.getMessage(),e);
				msg = "No se pudo verificar si existen alertas para esta inscripcion";
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				if("".equals(msg))
					msg = "Se ha detectado un problema en el servidor.";
			}	
		}
		
		String urlGpOnline = ConstantesDigital.getParametro("URL_GP_ONLINE");
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String sesion =context.getIdToken().getPreferredUsername();

		JSONObject respuesta = new JSONObject();

		JSONObject contadores = new JSONObject();		
		contadores.put("borradores", contadorBorrador);
		contadores.put("terminadas", contadorTerminada);
		contadores.put("proceso", contadorProceso);

		JSONObject estado = new JSONObject();
		estado.put("esAnoDigital", estadoEsAnoDigital);	
		estado.put("fna", estadoFna);		
		estado.put("tieneRechazo", estadoTieneRechazo);
		estado.put("rehacerImagen", rehacerImagen);

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);
		respuesta.put("msgCaratulas", msgCaratulas);
		respuesta.put("msgBorradores", msgBorradores);
		respuesta.put("alertas", alertas);
		respuesta.put("estado", estado);
		respuesta.put("urlGpOnline", urlGpOnline);
		respuesta.put("inscripcionDigitalDTO", inscripcionDigitalDTO);
		respuesta.put("inscripcionAnteriorDTO", inscripcionAnteriorDTO);
		respuesta.put("inscripcionSiguienteDTO", inscripcionSiguienteDTO);
		respuesta.put("consultaDocumentoDTO", consultaDocumentoDTO);
		respuesta.put("contadores", contadores);
		respuesta.put("seDigitalizoEnElDia", seDigitalizoEnElDia);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void getInscripcionParaDigital(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");	
		String bisp = request.getParameter("bis");	

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long numeroS = null;
		try {
			numeroS = Long.parseLong(numero);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Boolean bis = false;
		Integer bisi = 0;
		if("true".equals(bisp)){
			bis = true;	
			bisi = 1;
		}

		//generales
		Boolean status = false;
		String msg = "";

		//estado
		Boolean estadoFna = false;
		Boolean estadoEsAnoDigital = false;
		Boolean estadoTieneRechazo = false;

		InscripcionDigitalDTO inscripcionDigitalDTO = new InscripcionDigitalDTO();

		inscripcionDigitalDTO.setAno(ano);
		inscripcionDigitalDTO.setFoja(foja);
		inscripcionDigitalDTO.setNumero(numero);
		inscripcionDigitalDTO.setBis(bis);

		ConsultaDocumentoDTO consultaDocumentoDTO = new ConsultaDocumentoDTO();

		if( (foja==null || foja==0) || (StringUtils.isBlank(numero) || !StringUtils.isNumeric(numero)) || (ano==null || ano==0)){
			msg = "Se requiere Foja, Número y Año";
		}else{

			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {

				estadoTieneRechazo = digitalDelegate.solicitudTieneRechazo(foja, numero, ano, bisi);

				if(estadoTieneRechazo){
					status = true;
				}else{						
					InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
					consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(foja, numeroS, ano, bis, 2);

					if(consultaDocumentoDTO!=null){

						if(consultaDocumentoDTO.getHayDocumento()){//hay papel
							estadoFna = true;
							if(consultaDocumentoDTO.getTipoDocumento()== ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO){
								//tenemos imagen al dia

								cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO inscripcionDigitalVO = digitalDelegate.obtenerInscripcionDigital(foja, numero, ano, bis);

								ConverterVoToDtoMachine converter = new ConverterVoToDtoMachine();

								inscripcionDigitalDTO = converter.getInscripcionDigitalDTO(true, inscripcionDigitalVO);

								estadoEsAnoDigital = true;
								status = true;															
							}else if(consultaDocumentoDTO.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL ||
									consultaDocumentoDTO.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL){
								//es referencial

								estadoEsAnoDigital = digitalDelegate.validaAnosDigitales(foja, numero, ano);
								status = true;
							}else{
								//tipo de documento desconocido
								msg = "Tipo de documento desconocido. Tipo:"+consultaDocumentoDTO.getTipoDocumento();
							}
						}else{										
							estadoFna = digitalUtil.consultaIndice(foja, numero, ano);							
							estadoEsAnoDigital = digitalDelegate.validaAnosDigitales(foja, numero, ano);							
							status = true;
						}
					}else{
						//viene null
						msg = "Problemas consultando documento.";
					}
				}				
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}


		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String sesion =context.getIdToken().getPreferredUsername();

		JSONObject respuesta = new JSONObject();

		JSONObject estado = new JSONObject();
		estado.put("esAnoDigital", estadoEsAnoDigital);	
		estado.put("fna", estadoFna);
		estado.put("tieneRechazo", estadoTieneRechazo);

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);	
		respuesta.put("estado", estado);
		respuesta.put("inscripcionDigitalDTO", inscripcionDigitalDTO);
		respuesta.put("consultaDocumentoDTO", consultaDocumentoDTO);


		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	
	
	
	
	@SuppressWarnings("unchecked")
	public void getInscripcionSimple(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");	
		String bisp = request.getParameter("bis");	

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}


		Boolean bis = false;
		if("true".equals(bisp)){
			bis = true;	
		}

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		InscripcionDigitalDTO inscripcionDigitalDTO = null;

		if( (foja==null || foja==0) || (StringUtils.isBlank(numero) || !StringUtils.isNumeric(numero)) || (ano==null || ano==0)){
			msg = "Se requiere Foja, Número y Año";
		}else{

			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {
				cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO inscripcionDigitalVO = digitalDelegate.obtenerInscripcionDigital(foja, numero, ano, bis);

				if(inscripcionDigitalVO!=null){						
					ConverterVoToDtoMachine converter = new ConverterVoToDtoMachine();

					inscripcionDigitalDTO = converter.getInscripcionDigitalDTO(true, inscripcionDigitalVO);

					status = true;
				}				
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}	
		}

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String sesion =context.getIdToken().getPreferredUsername();

		respuesta.put("status", status);
		respuesta.put("sesion", sesion);
		respuesta.put("msg", msg);	
		respuesta.put("inscripcionDigitalDTO", inscripcionDigitalDTO);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getBusquedaTitulo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");	
		String bisp = request.getParameter("bis");	
		String anterioresp = request.getParameter("anteriores");

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long numeroL = null;
		try {
			numeroL = Long.parseLong(numero);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Boolean bis = false;
		Integer bisi = 0;
		if("true".equals(bisp)){
			bis = true;	
			bisi = 1;
		}

		Boolean anteriores = false;
		if("true".equals(anterioresp)){
			anteriores = true;	
		}

		ArrayList<ConsultaDocumentoDTO> estados = new ArrayList<ConsultaDocumentoDTO>();

		JSONObject respuesta = new JSONObject();

		Boolean tieneRechazo = false;
		Boolean fna = false;
		Boolean status = false;
		String msg = "";

		if( (foja==null || foja==0) || (StringUtils.isBlank(numero) || !StringUtils.isNumeric(numero)) || (ano==null || ano==0)){
			msg = "Se requiere Foja, Número y Año";
		}else {	
			status = true;
			try {					
				WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

				tieneRechazo = digitalDelegate.solicitudTieneRechazo(foja, numero, ano, bisi);

				if(!tieneRechazo){
					InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();

					ConsultaDocumentoDTO consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(foja, numeroL, ano, bis, 2);

					if(consultaDocumentoDTO!=null && consultaDocumentoDTO.getHayDocumento()){
						estados.add(consultaDocumentoDTO);
						fna = true;
					}else{

						Boolean existe = digitalUtil.consultaIndice(foja, numero, ano);

						if(existe==null || !existe){
							msg = "Foja, Número y Año no encontradas.";					
						}else{
							fna = true;
							estados.add(consultaDocumentoDTO);
						}
					}					

					if(anteriores){
						AnteriorDAO anteriorDAO = new AnteriorDAO();

						ArrayList<BorradorDTO> borradorDTOs = anteriorDAO.buscaBorrador(foja, numeroL, ano, bisi);

						if(borradorDTOs!=null){					
							for(BorradorDTO dto: borradorDTOs){

								ArrayList<ConsultaDocumentoDTO> imagenDTOs = anteriorDAO.consultaBorradorFolio(dto.getFolio(), dto.getBorrador());

								if(imagenDTOs!=null){

									for(ConsultaDocumentoDTO imagenDTO: imagenDTOs){

										if(!digitalUtil.existeResultado(estados, imagenDTO.getFirma())){

											ConsultaDocumentoDTO documentoDTO = 
													digitalUtil.getConsultaDocumentoDTO(imagenDTO.getFoja(), imagenDTO.getNumero(), imagenDTO.getAno(), imagenDTO.getBis(), 2);										

											if(documentoDTO!=null){		
												estados.add(documentoDTO);											
											}
										}
									}							
								}
							}					
						}
					}				
				}
			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema en el servidor.";	
			}
		}			

		//se podra solicitar alguna? 
		Boolean solicita = false;

		if(estados!=null && estados.size()>0){

			for(ConsultaDocumentoDTO consultaDocumentoDTO : estados){

				if(consultaDocumentoDTO.getPuedeSolicitar()){
					solicita = true;
					break;
				}			
			}
		}

		respuesta.put("solicita", solicita);
		respuesta.put("tieneRechazo", tieneRechazo);
		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("aaData", estados);
		respuesta.put("fna", fna);

		JSONObject req = new JSONObject();
		req.put("foja", foja);
		req.put("numero", numero);
		req.put("ano", ano);
		req.put("bis", bis);
		req.put("anteriores", anteriores);

		respuesta.put("req", req);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	public void getJPG(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		DocumentosCliente documentosCliente = new DocumentosCliente();

		String page = request.getParameter("p");
		String fojasReq = request.getParameter("fojas");
		String numeroReq = request.getParameter("numero");
		String anoReq = request.getParameter("ano");
		String bisReq = request.getParameter("bis");
		String tipoDocReq = request.getParameter("tipoDoc");

		if(StringUtils.isNumeric(page)){

			try {
				Integer foja = Integer.parseInt(fojasReq);
				Integer numero = Integer.parseInt(numeroReq);
				Integer anol = Integer.parseInt(anoReq);
				Boolean bis = "true".equalsIgnoreCase(bisReq)? true : false;
				Integer tipoDocumento = Integer.parseInt(tipoDocReq);
				Integer pagina = Integer.parseInt(page);


				boolean conSello = true;

				if(tipoDocumento == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO){
					conSello = false;
				}

				byte[] imagen = documentosCliente.downloadPageInscripcion(conSello, foja, numero, anol, bis, 2, tipoDocumento, pagina);
				OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
				response.setContentType("image/jpeg");
				out.write(imagen);
				out.close();      														


			} catch(IOException e){
	        	logger.warn("Se cancelo descarga documento");
	        } catch(HTTPException e){
				logger.error("Error HTTP codigo : "+e.getStatusCode(),e);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}           
	}


	@SuppressWarnings("unchecked")
	public void obtenerBorradores(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		JSONArray borradores = new JSONArray();

		String fojaReq = request.getParameter("foja");
		String numeroReq = request.getParameter("numero");
		String anoReq = request.getParameter("ano");
		String bisReq = request.getParameter("bis");

		try{
			Integer foja = Integer.parseInt(fojaReq);
			Integer numero = Integer.parseInt(numeroReq);
			Short ano = Short.parseShort(anoReq);
			Boolean bis = "true".equalsIgnoreCase(bisReq)? true : false;

			BorradoresUtil borradoresUtil = new BorradoresUtil();					
			borradores = borradoresUtil.getBorradoresDesdeH(foja.intValue(), numero, ano.shortValue(), bis);

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("borradores", borradores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void obtenerRepertorios(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		JSONArray borradores = new JSONArray();

		String fojaReq = request.getParameter("foja");
		String numeroReq = request.getParameter("numero");
		String anoReq = request.getParameter("ano");
		String bisReq = request.getParameter("bis");

		try{
			Integer foja = Integer.parseInt(fojaReq);
			Integer numero = Integer.parseInt(numeroReq);
			Short ano = Short.parseShort(anoReq);
			Boolean bis = "true".equalsIgnoreCase(bisReq)? true : false;

			BorradoresUtil borradoresUtil = new BorradoresUtil();					
			borradores = borradoresUtil.getBorradores(foja.intValue(), numero, ano.shortValue(), bis);
			
			
			//WsRepertorioClienteDelegate wsRepertorioClienteDelegate = new WsRepertorioClienteDelegate();
			
			
		
			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("borradores", borradores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCaratulasPorEstado(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		JSONArray caratulas = new JSONArray();

		String fojaReq = request.getParameter("foja");
		String numeroReq = request.getParameter("numero");
		String anoReq = request.getParameter("ano");
		String bisReq = request.getParameter("bis");
		String estado = request.getParameter("estado");

		try{
			Long foja = Long.parseLong(fojaReq);
			//Integer numero = Integer.parseInt(numeroReq);
			Long ano = Long.parseLong(anoReq);

			//Boolean bis = false;
			Integer bisi = 0;
			if("true".equals(bisReq)){
				//bis = true;	
				bisi = 1;
			}

			Integer estadoInt = Integer.parseInt(estado);

			CaratulasUtil caratulasUtil = new CaratulasUtil();
			caratulas = caratulasUtil.getCaratulas(2,foja, numeroReq, ano, bisi, estadoInt);

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("caratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	 @SuppressWarnings("unchecked")
	    public void getInscripcionJPG(ActionMapping mapping, ActionForm form,
	                HttpServletRequest request, HttpServletResponse response) {
	          response.setContentType("text/json");

	          JSONObject json = new JSONObject();
	          json.put("status", false);
	          json.put("msg", "");    
	          
	          JSONObject res = new JSONObject();

	          ConsultaDocumentoDTO dto = new ConsultaDocumentoDTO();           
	          String fojaReq = request.getParameter("foja");
	          String numeroReq = request.getParameter("numero");
	          String anoReq = request.getParameter("ano");
	          String bisReq = request.getParameter("bis");
	          Boolean tienerechazo = false;
	          Boolean anodigital = false;
	          
	          try{
	                Long foja = Long.parseLong(fojaReq);
	                Long numero = Long.parseLong(numeroReq);
	                Long ano = Long.parseLong(anoReq);
	                Boolean bis = "true".equalsIgnoreCase(bisReq)? true : false;
	                int bisInt = "true".equalsIgnoreCase(bisReq)? 1 : 0;
	                
	                WsInscripcionDigitalHDelegate delegate = new WsInscripcionDigitalHDelegate();
	                InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
	                dto = digitalUtil.getConsultaDocumentoDTO(foja, numero, ano, bis, 2);
	                
	                anodigital = delegate.validaAnosDigitales(foja, numeroReq, ano);
	                tienerechazo = delegate.solicitudTieneRechazo(foja, numeroReq, ano,bisInt);
	                 
	                if(dto!=null)               
	                      json.put("status", true);
	                                            
	                
	          }catch (GeneralException e) {

	          }catch (Exception e){
	                
	          }
	          
	          res.put("consultaDocumentoDTO", dto);
	          res.put("tienerechazo", tienerechazo);
	          res.put("anodigital", anodigital);
	          json.put("res", res);

	          try {
	                json.writeJSONString(response.getWriter());
	          } catch (IOException e) {
	        	  logger.error(e.getMessage(),e);
	          }           
	    }   	  
	 
	 public void verInscripcionCertificar(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	            HttpServletResponse response) {

		 	String caratulap = request.getParameter("caratula");
	        String pathArchivo= "";   
	        byte[] buffer = null;
	        
	        Integer caratula = null;
			try {
				caratula = Integer.parseInt(caratulap);
			} catch (Exception e1) {

			}
	        
	   	 	try {
   	 			pathArchivo = ConstantesInscripcionDigital.getParametro("PATH")+"/"+caratula+".pdf";
				
			} catch (Exception e1) {
				logger.error("Error al buscar documento: " + e1.getMessage(),e1);
	            request.setAttribute("error", "Problema al encontrar nombre archivo.");
			}
	   	 	
	        
	        ServletOutputStream out = null;

	        try {    
	            out = response.getOutputStream();                                            
	            
	            response.setContentType("application/pdf");
	            
                FileInputStream file = new FileInputStream(pathArchivo);
                    
                buffer = new byte[1024];
                int bytesRead = 0;
              
                do{
                    bytesRead = file.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, bytesRead);
                }while (bytesRead == buffer.length);
	            

	            
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
		public void getAnotacionParaRevision(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response) {

			JSONObject respuesta = new JSONObject();
			Boolean status = false;
			String msg = "";

			JSONArray anotaciones = new JSONArray();

			String fojainiReq = request.getParameter("fojaini");
			String fojafinReq = request.getParameter("fojafin");
			String anoReq = request.getParameter("ano");

			try{
				Long fojaini = Long.parseLong(fojainiReq);
				Long fojafin = Long.parseLong(fojafinReq);
				Long ano = Long.parseLong(anoReq);

				AnotacionUtil anotacionUtil = new AnotacionUtil();					
				anotaciones = anotacionUtil.getAnotaciones(fojaini, fojafin, ano);
			
				status = true;

			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}

			respuesta.put("anotaciones", anotaciones);
			respuesta.put("status", status);
			respuesta.put("msg", msg);

			try {
				respuesta.writeJSONString(response.getWriter());
			} catch (IOException e) {
				logger.error(e);
			}
		}
	 
	 @SuppressWarnings("unchecked")
		public void actualizaEstadoAnotacion(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response) {

			JSONObject respuesta = new JSONObject();
			Boolean status = false;
			String msg = "";

			JSONArray anotaciones = new JSONArray();

			String anotacionIdReq = request.getParameter("anotacion");

			try{
				Long anotacionId = Long.parseLong(anotacionIdReq);

				WsInscripcionDigitalHDelegate delegate = new WsInscripcionDigitalHDelegate();
				
				cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacion = delegate.obtenerAnotacion(anotacionId);
				
				cl.cbrs.inscripciondigitalh.vo.EstadoAnotacionVO estadoAnotacionVo = anotacion.getEstadoAnotacionVo();
				estadoAnotacionVo.setIdEstado(ANOTACION_FOLIADA);
				
				anotacion.setEstadoAnotacionVo(estadoAnotacionVo);
				
				delegate.actualizarAnotacion(anotacion);
				
				status = true;

			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}

			respuesta.put("anotaciones", anotaciones);
			respuesta.put("status", status);
			respuesta.put("msg", msg);

			try {
				respuesta.writeJSONString(response.getWriter());
			} catch (IOException e) {
				logger.error(e);
			}
		}
	 
		public void getNotas(ActionMapping mapping, ActionForm form, HttpServletRequest request,
				HttpServletResponse response) {

			String fojap = request.getParameter("foja");
			String numerop = request.getParameter("numero");
			String anop = request.getParameter("ano");	
			String bisp = request.getParameter("bis");

			JSONObject respuesta = new JSONObject();
			JSONArray anotaciones = new JSONArray();

			Boolean status = false;
			String msg = "";

			Long foja = null;
			try {
				foja = Long.parseLong(fojap);
			} catch (Exception e1) {

			}

			Long ano = null;
			try {
				ano = Long.parseLong(anop);
			} catch (Exception e1) {

			}

			Boolean bis = false;
			if("true".equals(bisp)){
				bis = true;	
			}

			try {

				AnotacionUtil anotacionUtil = new AnotacionUtil();					
				anotaciones = anotacionUtil.getAnotacionesInscripcionHipotecas(foja, numerop, ano, bis);

				status = true;

			} catch (Exception e1) {
				logger.error("Error al obtener notas: " + e1.getMessage(),e1);
				request.setAttribute("error", "Error al obtener notas.");

				status = false;
				msg = "Error al obtener notas.";
			}

			respuesta.put("anotaciones", anotaciones);
			respuesta.put("status", status);
			respuesta.put("msg", msg);


			try {
				respuesta.writeJSONString(response.getWriter());
			} catch (IOException e) {
				logger.error(e);
			}

		}	 
	 
	 
}