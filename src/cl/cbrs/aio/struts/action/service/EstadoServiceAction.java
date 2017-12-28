package cl.cbrs.aio.struts.action.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

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
import org.json.simple.parser.JSONParser;

import cl.cbr.common.exception.GeneralException;
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

public class EstadoServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(EstadoServiceAction.class);
	
	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void getCausalesRechazo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");	

		ArrayList<CausalRechazoDTO> rechazos = new ArrayList<CausalRechazoDTO>();

		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();

		try {		
			List<CausalRechazoVO> causalRechazoVOs = delegate.obtenerCausalesRechazo();

			if(causalRechazoVOs!=null){
				
				for(CausalRechazoVO rechazo : causalRechazoVOs){
					CausalRechazoDTO dto = new CausalRechazoDTO();					
					dto.setCodigo(rechazo.getCodigoCausaRechazo());
					dto.setDescripcion(rechazo.getDescripcionCausaRechazo());
					dto.setTemplate(rechazo.getTemplateCausaRechazo());	
					
					rechazos.add(dto);
				}
				
				json.put("status", true);
			} else{
				json.put("msg", "No se encontraron causas de rechazo");
			}
			
			

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor obteniendo causales rechazo");
		}

		json.put("rechazos", rechazos);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}


	@SuppressWarnings({ "unchecked" })
	public void getEstado(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);

		json.put("req", req);	

		CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();
//		EntregaEnLineaDTO entregaEnLineaDTO = new EntregaEnLineaDTO();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){

			Long numero = Long.parseLong(numeroCaratula);

			WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
			CaratulaEstadoUtil cu = new CaratulaEstadoUtil();

			try {		
				CaratulaVO caratula = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numero);

				if(caratula!=null){		
//					EntregaEnLineaUtil entregaEnLineaUtil = new EntregaEnLineaUtil();

					caratulaEstadoDTO = cu.getCaratulaEstadoDTO(caratula);
					//entregaEnLineaDTO = entregaEnLineaUtil.getDocumentosFirma(caratula.getNumeroCaratula());

					json.put("status", true);					
					request.getSession().setAttribute("numeroCaratula", numeroCaratula);
				} else{
					//Si no existe, buscar si fue anulada
					AnulaCaratulaVO[] anulaCaratulaVOs = delegate.obtenerCaratulasAnuladas(new Long(numeroCaratula));
					if(anulaCaratulaVOs!=null && anulaCaratulaVOs.length>0){
						AnulaCaratulaVO anulaVO = anulaCaratulaVOs[0]; //Ultima Anulacion
						caratulaEstadoDTO = cu.getCaratulaEstadoDTO(anulaVO);	
						json.put("status", true);
						request.getSession().setAttribute("numeroCaratula", numeroCaratula);

					} else{
						json.put("msg", "Carátula no encontrada.");		
					}
								
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo carátula.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}


		JSONObject res = new JSONObject();

		res.put("caratulaDTO", caratulaEstadoDTO);
//		res.put("entregaEnLineaDTO", entregaEnLineaDTO);

		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void cambiarEstado(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		
		String estadoFormulario = request.getParameter("idEstadoFormulario");
		String descEstadoFormulario = request.getParameter("descEstadoFormulario");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);
		req.put("idEstadoFormulario", estadoFormulario);
		req.put("descEstadoFormulario", descEstadoFormulario);
		json.put("req", req);	
		
		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
		CaratulasUtil caratulasUtil = new CaratulasUtil();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){
			try {
				Long numero = Long.parseLong(numeroCaratula);
				descEstadoFormulario = cambiaEncoding(descEstadoFormulario);

				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numero);

				if(caratulaVO!=null){
					caratulaVO.setEstadoFormulario(estadoFormulario);
					delegate.actualizarCaratula(caratulaVO);
					
					//Estado Aprobada (d): mover a seccion Aprobada 
					if("Aprobada".equalsIgnoreCase(descEstadoFormulario)){
						String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
						rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
						
						EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
						estadoCaratulaVO.setEnviadoPor(new FuncionarioVO(rutFuncionario));
						estadoCaratulaVO.setFechaMovimiento(new Date());
						estadoCaratulaVO.setMaquina("CS10");
						estadoCaratulaVO.setResponsable(new FuncionarioVO(rutFuncionario));
						estadoCaratulaVO.setSeccion(new SeccionVO("AP"));
						caratulasUtil.moverCaratulaSeccion(new Long(numeroCaratula), estadoCaratulaVO );
					}

					//Agregar bitacora
					String observacion = "Se cambia estado caratula a " + descEstadoFormulario;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					json.put("bitacoraDTO", bitacoraDTO);

					json.put("status", true);
				}else{
					json.put("msg", "Carátula no encontrada.");					
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor actualizando estado carátula.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void updateRequirente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("caratula");		
		String correoRequirente = request.getParameter("correo");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numero", numeroCaratula);
		req.put("correo", correoRequirente);
		json.put("req", req);	

		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
		
		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){
			try {	
				Long numero = Long.parseLong(numeroCaratula);
				correoRequirente = cambiaEncoding(correoRequirente);
	
				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numero);
				CaratulasUtil caratulasUtil = new CaratulasUtil();

				if(caratulaVO!=null && caratulaVO.getRequirente()!=null){
					RequirenteVO requirenteVO = caratulaVO.getRequirente();
					requirenteVO.setEmail(correoRequirente);
					delegate.actualizarRequirente(new UsuarioWebVO(), requirenteVO);
					
					//Agregar bitacora
					String observacion = "Se cambia email requirente a " + correoRequirente;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					json.put("bitacoraDTO", bitacoraDTO);

					json.put("status", true);
				}else{
					json.put("msg", "Carátula o requirente no encontrado.");					
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo carátula.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void editarDatosCitado(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String citadoDTOReq = request.getParameter("citadoDTO");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("citadoDTO", citadoDTOReq);
		json.put("req", req);	

		if(StringUtils.isNotBlank(caratulaReq) && StringUtils.isNumeric(caratulaReq)){
			
			JSONParser jsonParser = new JSONParser();
			
			try {

				Long numeroCaratula = Long.parseLong(caratulaReq);
				JSONObject caratulaJSON = (JSONObject)jsonParser.parse(citadoDTOReq);
				Integer fojas = Integer.parseInt(caratulaJSON.get("foja").toString());
				Integer numero = Integer.parseInt(caratulaJSON.get("numero").toString());
				Integer ano = Integer.parseInt(caratulaJSON.get("ano").toString());
				String bisStr = caratulaJSON.get("bis")!=null&&caratulaJSON.get("bis").toString().equals("true")?"SI":"NO";
				Integer bis = caratulaJSON.get("bis")!=null&&caratulaJSON.get("bis").toString().equals("true")?1:0;
				JSONObject registroDTOJSON = ((JSONObject)caratulaJSON.get("registroDTO"));
				Integer idRegistro = Integer.parseInt(registroDTOJSON.get("id").toString());
				String descRegistro = cambiaEncoding((String)registroDTOJSON.get("descripcion"));
	
				WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
				CaratulasUtil caratulasUtil = new CaratulasUtil();
						
				//delegate.modificarFojas(numeroCaratula, rutFuncionario, fojas, numero, ano);
				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
				
				if(caratulaVO.getInscripciones()!=null && caratulaVO.getInscripciones().length>0){
					caratulaVO.getInscripciones()[0].setFoja(fojas);
					caratulaVO.getInscripciones()[0].setNumero(numero);
					caratulaVO.getInscripciones()[0].setAno(ano);
					caratulaVO.getInscripciones()[0].setBis(bis);
					caratulaVO.getInscripciones()[0].setRegistro(idRegistro);

				} else{
					InscripcionCitadaVO[] inscripcion = new InscripcionCitadaVO[1];
					inscripcion[0] = new InscripcionCitadaVO();
					inscripcion[0].setFoja(fojas);
					inscripcion[0].setNumero(numero);
					inscripcion[0].setAno(ano);
					inscripcion[0].setBis(bis);
					inscripcion[0].setRegistro(idRegistro);
					//Tipo formulario 7,8 Vigencia es 1
					if(caratulaVO.getTipoFormulario()!=null && (
							caratulaVO.getTipoFormulario().getTipo().equals(7) ||
							caratulaVO.getTipoFormulario().getTipo().equals(8)
						))
						inscripcion[0].setTipoVigencia(1);
					else
						inscripcion[0].setTipoVigencia(0);
					caratulaVO.setInscripciones(inscripcion);
				}
					delegate.actualizarCaratula(caratulaVO);
					
					//Agregar bitacora
					String observacion = "Datos citado modificado. Registro: " + descRegistro + " Foja: " + fojas + " Número: "+ numero + " Año: " + ano + " Bis: " + bisStr;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					json.put("bitacoraDTO", bitacoraDTO);
				
				json.put("status", true);
			} catch (Exception e) {
				json.put("msg", "Problemas en servidor al editar datos citado.");
				logger.error("Error: "+e.getMessage(), e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void agregarTarea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String codtareaReq = request.getParameter("codTarea");
		String descTareaReq = request.getParameter("descTarea");


		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("codtarea", codtareaReq);
		req.put("descTarea", descTareaReq);
		json.put("req", req);
		
		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
		CaratulasUtil caratulasUtil = new CaratulasUtil();	

		if(StringUtils.isNotBlank(caratulaReq) && StringUtils.isNumeric(caratulaReq)){
			try {	
				Long numeroCaratula = Long.parseLong(caratulaReq);
				Integer codTarea = Integer.parseInt(codtareaReq);
				descTareaReq = cambiaEncoding(descTareaReq);
	
				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);

				if(caratulaVO!=null){
					TareaVO[] tareaVOs = caratulaVO.getTareas();
					
					TareaVO tareaVO = new TareaVO();
					tareaVO.setCapital(0L);
					tareaVO.setCodigoMoneda(null);
					tareaVO.setTotal(0L);
					TipoTareaVO tipo = new TipoTareaVO(codTarea);
					tareaVO.setTipo(tipo );				
					
					List<TareaVO> listTareaVOs = new ArrayList<TareaVO>(Arrays.asList(tareaVOs));
					listTareaVOs.add(tareaVO);
					caratulaVO.setTareas(listTareaVOs.toArray(new TareaVO[listTareaVOs.size()]));
					
					delegate.actualizarCaratula(caratulaVO);
					
					//Agregar bitacora
					String observacion = "Se agrega tarea " + descTareaReq;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					json.put("bitacoraDTO", bitacoraDTO);

					json.put("status", true);
				}else{
					json.put("msg", "Carátula no encontrada.");					
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor al agregar tarea.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void eliminarTarea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String codtareaReq = request.getParameter("codTarea");
		String descTareaReq = request.getParameter("descTarea");


		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("codtarea", codtareaReq);
		req.put("descTarea", descTareaReq);
		json.put("req", req);
	
		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();

		if(StringUtils.isNotBlank(caratulaReq) && StringUtils.isNumeric(caratulaReq)){
			try {	
				Long numeroCaratula = Long.parseLong(caratulaReq);
				Integer codTarea = Integer.parseInt(codtareaReq);
				descTareaReq = cambiaEncoding(descTareaReq);

				CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
				CaratulasUtil caratulasUtil = new CaratulasUtil();

				if(caratulaVO!=null){
					TareaVO[] tareaVOs = caratulaVO.getTareas();					
					List<TareaVO> listTareaVOs = new ArrayList<TareaVO>(Arrays.asList(tareaVOs));
					
					TareaVO tareaAEliminar = null;
					for(TareaVO tarea : listTareaVOs){
						if(tarea.getTipo().getCodigo().intValue() == codTarea)
							tareaAEliminar = tarea;
					}
					
					if(tareaAEliminar!=null){
						listTareaVOs.remove(tareaAEliminar);
						
						caratulaVO.setTareas(listTareaVOs.toArray(new TareaVO[listTareaVOs.size()]));
						
						delegate.actualizarCaratula(caratulaVO);
					}
					
					//Agregar bitacora
					String observacion = "Se elimina tarea " + descTareaReq;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					json.put("bitacoraDTO", bitacoraDTO);

					json.put("status", true);
				}else{
					json.put("msg", "Carátula no encontrada.");					
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor al eliminar tarea.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void agregarCaja(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String fechaCajaReq = request.getParameter("fechaCaja");
		String horaCajaReq = request.getParameter("horaCaja");


		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("fechaCaja", fechaCajaReq);
		req.put("horaCaja", horaCajaReq);
		json.put("req", req);
	

		if(StringUtils.isNotBlank(caratulaReq) && StringUtils.isNumeric(caratulaReq)){

			CaratulasUtil caratulasUtil = new CaratulasUtil();

			try {					
				Long numeroCaratula = Long.parseLong(caratulaReq);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date fechaMovimiento = sdf.parse(fechaCajaReq+" "+horaCajaReq+":00");
				String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
				rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
				
				EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
				estadoCaratulaVO.setEnviadoPor(new FuncionarioVO(rutFuncionario));
				estadoCaratulaVO.setFechaMovimiento(fechaMovimiento);
				estadoCaratulaVO.setMaquina("CS10");
				estadoCaratulaVO.setResponsable(new FuncionarioVO(rutFuncionario));
				estadoCaratulaVO.setSeccion(new SeccionVO("01"));
				caratulasUtil.moverCaratulaSeccion(numeroCaratula, estadoCaratulaVO );
				
				//Agregar bitacora
				String observacion = "Se agrega caja en fecha " + sdf.format(fechaMovimiento);
				String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
				BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(new Long(numeroCaratula), rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
				json.put("bitacoraDTO", bitacoraDTO);
				
				json.put("status", true);
			} catch (Exception e) {
				json.put("msg", "Problemas en servidor al agregar caja.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void obtenerListaTareas(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		ParametrosUtil parametrosUtil = new ParametrosUtil();

		try {					
			json.put("listaTareas", parametrosUtil.getTareasDTO());
			
			json.put("status", true);
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al agregar caja.");
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}

	@SuppressWarnings({ "unchecked" })
	public void rechazarCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String codigoReq = request.getParameter("codigo");
		String motivoReq = request.getParameter("motivo");


		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("codigo", codigoReq);
		req.put("motivo", motivoReq);
		json.put("req", req);

		CaratulasUtil caratulasUtil = new CaratulasUtil();

		try {					
			Long numeroCaratula = Long.parseLong(caratulaReq);
			Integer codigoCausaRechazo = Integer.parseInt(codigoReq);
			String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
			rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
			motivoReq = cambiaEncoding(motivoReq);
			
			caratulasUtil.rechazarCaratula(numeroCaratula, rutFuncionario, codigoCausaRechazo, motivoReq);
			json.put("status", true);
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al rechazar caratula.");
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getCaratulaSesion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);	

		try {		

			if(request.getSession().getAttribute("numeroCaratula") != null){
				json.put("numeroCaratula", request.getSession().getAttribute("numeroCaratula").toString());
				json.put("status", true);
			} 
			
		} catch (Exception e) {
        	
			logger.error("error en getCaratulaSesion: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar caratula en sesion");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@SuppressWarnings("unchecked")
	public void getEstadoReporte(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		SimpleDateFormat sdfReporte = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date fecha = new Date();
		String fechaReporte = sdfReporte.format(fecha);

		String ncaratula = request.getParameter("nc");
		String tipo = request.getParameter("tipo");
		String ingresoEgresop = request.getParameter("ingresoEgreso");
		
		boolean hayIngresoEgreso = false;

		if(ingresoEgresop!=null && "true".equals(ingresoEgresop)){
			hayIngresoEgreso = true;
		}	
		
		CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
		WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();

		ByteArrayOutputStream bout = null;
		try {
			Long numeroCaratula = new Long(ncaratula);
			
			CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
			CaratulaEstadoDTO caratulaEstadoDTO = caratulaEstadoUtil.getCaratulaEstadoDTO(caratulaVO);
			JSONObject respuesta = caratulaEstadoUtil.estadoReporteJSON(caratulaEstadoDTO, numeroCaratula);
			JSONObject data = (JSONObject)respuesta.get("data");
			ServletOutputStream out = response.getOutputStream();

			bout = new ByteArrayOutputStream();

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

			//BITACORA
			ArrayList<BitacoraDTO> bitacoraCaratulaVOs = (ArrayList<BitacoraDTO>)data.get("bitacora"); //caratulaEstadoDTO.getBitacoraDTOs();
			
			String detalle = "";
			//			if(caratulaVO.getGlosaCtaCte1()!=null)
			//			detalle += caratulaVO.getGlosaCtaCte1()+"\n";
			//		if(caratulaVO.getGlosaCtaCte2()!=null)
			//			detalle += caratulaVO.getGlosaCtaCte2()+"\n";
			//		if(caratulaVO.getGlosaCtaCte3()!=null)
			//			detalle += caratulaVO.getGlosaCtaCte3()+"\n";
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
				
				if(bitacoraCaratulaVOs!=null){
					ListIterator<BitacoraDTO> li = bitacoraCaratulaVOs.listIterator(bitacoraCaratulaVOs.size());
					while(li.hasPrevious()){
						BitacoraDTO bitacoraVO = (BitacoraDTO) li.previous();

						Date fechaBitacora = bitacoraVO.getFecha();
						String fechaBitacoraText = sdfReporte.format(fechaBitacora);
						
						detalle +=bitacoraVO.getDescOrigen()+" ("+fechaBitacoraText+"): "+bitacoraVO.getComentario()+"\n";
					}
				}
			}

			map.put("detalle", detalle);

			//TAREAS
			ArrayList<TareaDTO> tareas = (ArrayList<TareaDTO>)data.get("tareas");
			
			//HISTORIAL					
			ArrayList<MovimientoDTO> historial = (ArrayList<MovimientoDTO>)data.get("historial"); //) caratulaEstadoDTO.getMovimientoDTOs();//DataManager.getMovimientos(Integer.valueOf(ncaratula));

			HashMap<String, String> map2 = new HashMap<String, String>();
			map2.put("fechaReporte", fechaReporte);
			map2.put("ncaratula", ""+data.get("ncaratula"));	

			//REPERTORIO
			WsRepertorioClienteDelegate repertorioClienteDelegate = new WsRepertorioClienteDelegate();
			List<RepertorioVO> repertorioVOs = repertorioClienteDelegate.existeCaratulaConRepertorio(numeroCaratula);			
			
			//INGRESOS - EGRESOS
			ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs = (ArrayList<IngresoEgresoDTO>)data.get("ingresoEgreso"); //caratulaEstadoDTO.getIngresoEgresoDTOs();//DataManager.getIngresoEgreso(Integer.valueOf(ncaratula));

			if("pdf".equals(tipo))
				response.setContentType("application/pdf");	

			bout = ReporteUtil.export(map, tareas, repertorioVOs, map2, historial, tipo, bitacoraCaratulaVOs, hayIngresoEgreso, ingresoEgresoDTOs);

			out.write(bout.toByteArray());	

			out.flush();
			
			if(out != null)
	           	out.close();
			 
		} catch (IOException e) {
			logger.error("Error IO al generar PDF: " + e.getMessage(), e);
		} catch (JRException e) {
			logger.error("Error JRE al generar PDF: " + e.getMessage(), e);
		} catch (GeneralException e) {
			logger.error("Error General al generar PDF: " + e.getMessage(), e);
		}
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

		ByteArrayOutputStream bout = null;
		try {
			Long numeroCaratula = new Long(ncaratula);
			
			CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
			CaratulaEstadoDTO caratulaEstadoDTO = caratulaEstadoUtil.getCaratulaEstadoDTO(caratulaVO);
			JSONObject respuesta = caratulaEstadoUtil.estadoReporteJSON(caratulaEstadoDTO, numeroCaratula);
			JSONObject data = (JSONObject)respuesta.get("data");
			ServletOutputStream out = response.getOutputStream();

			bout = new ByteArrayOutputStream();

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

			bout = ReporteUtil.export(map, tareas, repertorioVOs, map2, null, tipo, null, hayIngresoEgreso, null);

			out.write(bout.toByteArray());	

			out.flush();
			
			if(out != null)
	           	out.close();
			 
		} catch (IOException e) {
			logger.error("Error IO al generar PDF: " + e.getMessage(), e);
		} catch (JRException e) {
			logger.error("Error JRE al generar PDF: " + e.getMessage(), e);
		} catch (GeneralException e) {
			logger.error("Error General al generar PDF: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getDocumentosEntrega(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);
		json.put("req", req);	

		EntregaEnLineaDTO entregaEnLineaDTO = new EntregaEnLineaDTO();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){
			Long numero = Long.parseLong(numeroCaratula);

			try {		
				EntregaEnLineaUtil entregaEnLineaUtil = new EntregaEnLineaUtil();
				entregaEnLineaDTO = entregaEnLineaUtil.getDocumentosFirma(numero);				

				json.put("status", true);					

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo documentos entrega.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else
			json.put("msg", "Número carátula no válido.");

		JSONObject res = new JSONObject();
		res.put("entregaEnLineaDTO", entregaEnLineaDTO);
		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}		
	
	@SuppressWarnings({ "unchecked" })
	public void dejarDocumentoNoVigente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String codigoDocumento = request.getParameter("codigoDocumento");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		if(StringUtils.isNotBlank(codigoDocumento)){

			try {		
				//anulando documento
				
//				WsFirmaElectronica w = new WsFirmaElectronica();
//				AnularDocumentoRequest r = new AnularDocumentoRequest();
//				r.setCodigoVerificacion(codigoDocumento);
//				AnularDocumentoResponse res =w.anularDocumento(r);
				
				DatosPropiedadDAO dao = new DatosPropiedadDAO();
				boolean respuesta = dao.anularDocumento(codigoDocumento);
				if(respuesta)	
					json.put("status", true);
				else
					json.put("status", false);

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo documentos entrega.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else
			json.put("msg", "Codigo no encontrado.");

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void getRepertorios(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);
		json.put("req", req);	

		ArrayList<RepertorioDTO> repertorioDTOs = new ArrayList<RepertorioDTO>();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){
			Long numero = Long.parseLong(numeroCaratula);

			try {		
				CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
				repertorioDTOs = caratulaEstadoUtil.getRepertorioDTO(numero);				

				json.put("status", true);					

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo repertorios.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else
			json.put("msg", "Número carátula no válido.");

		JSONObject res = new JSONObject();
		res.put("repertorioDTOs", repertorioDTOs);
		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getIngresosEgresos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);
		json.put("req", req);	

		ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs = new ArrayList<IngresoEgresoDTO>();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){
			Long numero = Long.parseLong(numeroCaratula);

			try {		
				CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
				ingresoEgresoDTOs = caratulaEstadoUtil.getIngresoEgresoDTO(numero);				

				json.put("status", true);					

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo ingresos egresos.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else
			json.put("msg", "Número carátula no válido.");

		JSONObject res = new JSONObject();
		res.put("ingresoEgresoDTOs", ingresoEgresoDTOs);
		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void getCuentaCorriente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String codigo = request.getParameter("codigo");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("codigo", codigo);
		json.put("req", req);	

		CuentaCorrienteDTO cuentaCorrienteDTO = new CuentaCorrienteDTO();

		if(StringUtils.isNotBlank(codigo) && StringUtils.isNumeric(codigo)){
			Integer numero = Integer.parseInt(codigo);

			try {		
				CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
				cuentaCorrienteDTO = caratulaEstadoUtil.getCuentaCorrienteDTO(numero);				

				json.put("status", true);					

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo cuenta corriente.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else
			json.put("msg", "Código no válido.");

		JSONObject res = new JSONObject();
		res.put("cuentaCorrienteDTO", cuentaCorrienteDTO);
		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getProductoWeb(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String productoWebDTOReq = request.getParameter("productoWebDTO");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("productoWebDTO", productoWebDTOReq);
		json.put("req", req);	

		ProductoWebDTO productoWebDTO = new ProductoWebDTO();
		
		JSONParser jsonParser = new JSONParser();
		

		try {
			productoWebDTOReq = cambiaEncoding(productoWebDTOReq);
			JSONObject productoWebDTOJSON = (JSONObject)jsonParser.parse(productoWebDTOReq);
			
			CaratulaEstadoUtil caratulaEstadoUtil = new CaratulaEstadoUtil();
			productoWebDTO = caratulaEstadoUtil.getProductoWebDTO(productoWebDTOJSON);
			caratulaEstadoUtil.getProductoWebDTOFull(productoWebDTO);			

			json.put("status", true);					

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor obteniendo producto web.");
			logger.error("Error: "+e.getMessage(),e);
		}


		JSONObject res = new JSONObject();
		res.put("productoWebDTO", productoWebDTO);
		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getCaratulasPorRut(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String rut = request.getParameter("rut");
		String resultadop = request.getParameter("resultado");
		
		JSONObject json = new JSONObject();
		JSONArray listaCaratulasJSON = new JSONArray();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("rut", rut);
		
		json.put("req", req);	

		Integer resultado = null;
		if(!StringUtils.isBlank(resultadop)){
			resultado = Integer.parseInt(resultadop);
		}

		if(!StringUtils.isBlank(rut) && rut.length()>=2){
			rut = rut.replaceAll("\\.", "");
			rut = rut.replaceAll("-", "");

			String rut1 = rut.substring(0, rut.length()-1);
			String dv1 = rut.substring(rut.length()-1, rut.length());
						

			if(rut1 != null && dv1 != null ){

				if(!StringUtils.isBlank(dv1)){
					dv1 = dv1.toUpperCase();
				}else{
					dv1 = "";
				}

				if(StringUtils.isNumeric(rut1) && StringUtils.isNotBlank(rut1) && RUTUtil.validaDigitoVerificador(rut1, dv1)){

					try {
						String rutFinal = rut1+dv1;

						rutFinal = StringUtil.completaPorLaIzquierda(rutFinal, 9, '0');

						WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
						CaratulaEstadoUtil cu = new CaratulaEstadoUtil();
			
				
							List<CaratulaVO> caratulaVOs = delegate.obtenerCaratulasPorRutConMaximo(rut1, dv1, resultado);
			
							if(caratulaVOs!=null){		
			
								
								for(CaratulaVO caratulaVO : caratulaVOs){
									CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();
									caratulaEstadoDTO = cu.getCaratulaEstadoDTO(caratulaVO);
									
									listaCaratulasJSON.add(caratulaEstadoDTO);
								}
			

								json.put("status", true);					

							} else{
								json.put("msg", "No se encontraron carátulas.");	
							}

					} catch (Exception e) {
						json.put("msg", "Problemas en servidor obteniendo carátula.");
						logger.error("Error: "+e.getMessage(),e);
					}
				} else
					json.put("msg", "Rut no valido");
			} else
				json.put("msg", "Rut no valido");
		} else
			json.put("msg", "Rut no valido");


		JSONObject res = new JSONObject();

		res.put("listaCaratulas", listaCaratulasJSON);

		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}		
	
	public void getDocumentos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratulaReq = request.getParameter("numeroCaratula");
		String tipoDocumentoReq = request.getParameter("tipoDocumento");


		
		JSONObject json = new JSONObject();

		try {		
			Long numeroCaratula = new Long(numeroCaratulaReq);
			DocumentosCliente documentosCliente = new DocumentosCliente();
			json = documentosCliente.documentosPorCaratula(numeroCaratula, tipoDocumentoReq);
			

		} catch(HTTPException e){ 
			logger.error("Error HTTP codigo : "+e.getStatusCode(),e);
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	public void existeDocumento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String documentoReq = request.getParameter("documento");
		System.out.println("documento:"+documentoReq);
		logger.debug("documento:"+documentoReq);
		JSONObject json = new JSONObject();

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq);
			Integer idTipoDocumentop = Integer.parseInt(documentoJSON.get("idTipoDocumento").toString()); 
			String nombreArchivop = documentoJSON.get("nombreArchivo").toString(); 
			Integer idRegp = documentoJSON.get("idReg")==null?0:Integer.parseInt(documentoJSON.get("idReg").toString()); 
			Date fechap = null;
			if(documentoJSON.get("fechaDocumento")!=null){
				try{
				String fechaDocumento = (String)documentoJSON.get("fechaDocumento");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				fechap = sdf.parse(fechaDocumento);
				} catch(Exception e){
					try{
						fechap = new Date((Long)documentoJSON.get("fechaDocumento"));
					} catch(Exception e1){
						logger.error("No se pudo determinar fecha de documento: " + documentoJSON.toJSONString());
					}
				}
			}
			
			DocumentosCliente documentosCliente = new DocumentosCliente();
			json = documentosCliente.existeDocumento(idTipoDocumentop, idRegp, nombreArchivop, fechap);

		} catch(HTTPException e){
			logger.error("Error HTTP codigo : "+e.getStatusCode(),e);
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	 public void downloadDocumento(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	            HttpServletResponse response) {
//		 	response.setContentType("application/pdf");
		 	
		 	
			String documentoReq = request.getParameter("documento");
			
			ServletOutputStream out = null;

			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq);
				Integer idTipoDocumentop = Integer.parseInt(documentoJSON.get("idTipoDocumento").toString()); 
				String nombreArchivop = documentoJSON.get("nombreArchivo").toString(); 
				Integer idRegp = documentoJSON.get("idReg")==null?0:Integer.parseInt(documentoJSON.get("idReg").toString()); 
				Date fechap = null;
				if(documentoJSON.get("fechaDocumento")!=null)
					fechap = new Date((Long)documentoJSON.get("fechaDocumento"));
				
				DocumentosCliente documentosCliente = new DocumentosCliente();
				byte[] archivo = documentosCliente.downloadDocumento(idTipoDocumentop, idRegp, nombreArchivop, fechap);

				response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivop);
				out = response.getOutputStream();			    
	         	out.write(archivo, 0, archivo.length);
	         	out.flush();
	     
	            if(out != null)
	                  out.close();
	        } catch(HTTPException e){
	            logger.error("Error HTTP codigo " + e.getStatusCode() + " al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");	        	
	        } catch (Exception e) {
	            logger.error("Error al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");
	        } finally{
	            if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
	        }
	        
	        
	    }
	 public void downloadDocumento2(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	            HttpServletResponse response) {
//		 	response.setContentType("application/pdf");
		 	
		 	
			String documentoReq = request.getParameter("documento");
			
			ServletOutputStream out = null;

			try {
				JSONParser jsonParser = new JSONParser();
				//JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq);
				Integer idTipoDocumentop = Integer.parseInt(request.getParameter("idTipoDocumento").toString()); 
				String nombreArchivop = request.getParameter("nombreArchivo").toString(); 
				Integer idRegp = request.getParameter("idReg")==null?0:Integer.parseInt(request.getParameter("idReg").toString()); 
				Date fechap = null;
				if(request.getParameter("fechaDocumento")!=null)
					fechap = new Date(new Long(request.getParameter("fechaDocumento")));
				
				DocumentosCliente documentosCliente = new DocumentosCliente();
				byte[] archivo = documentosCliente.downloadDocumento(idTipoDocumentop, idRegp, nombreArchivop, fechap);

				response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivop);
				out = response.getOutputStream();			    
	         	out.write(archivo, 0, archivo.length);
	         	out.flush();
	     
	            if(out != null)
	                  out.close();
	        } catch(HTTPException e){
	            logger.error("Error HTTP codigo " + e.getStatusCode() + " al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");	        	
	        } catch (Exception e) {
	            logger.error("Error al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");
	        } finally{
	            if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
	        }
	        
	        
	    } 		
	
	public void existeFirma(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String documentoReq = request.getParameter("documento");
		
		JSONObject json = new JSONObject();

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq); 
			String nombreArchivop = documentoJSON.get("nombreArchivo").toString();  
			String rutFirmadorp = documentoJSON.get("rutFirmador").toString();
			Date fechap = null;
			if(documentoJSON.get("fechaDocumento")!=null)
				fechap = new Date((Long)documentoJSON.get("fechaDocumento"));
			
			String firmador = TablaValores.getValor("impresion.parametros", "RUT_" + rutFirmadorp.split("-")[0], "CARPETA");
			
			DocumentosCliente documentosCliente = new DocumentosCliente();
			json = documentosCliente.existeFirma(nombreArchivop, firmador, fechap);

		} catch(HTTPException e){
			logger.error("Error HTTP codigo : "+e.getStatusCode(),e);
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	@Deprecated 
	 public void downloadFirma(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
	 	response.setContentType("application/pdf");
	 	logger.debug("Iniciando");
	 	Boolean download = true;
	 	
		String documentoReq = request.getParameter("documento");
		logger.debug(documentoReq);
		if(request.getParameter("download")!=null)
			download = new Boolean(request.getParameter("download"));
		
		ServletOutputStream out = null;

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq); 
			String nombreArchivop = documentoJSON.get("nombreArchivo").toString();  
			String rutFirmadorp = documentoJSON.get("rutFirmador").toString();
			Date fechap = null;
			if(documentoJSON.get("fechaDocumento")!=null)
				fechap = new Date((Long)documentoJSON.get("fechaDocumento"));
			
			String firmador = TablaValores.getValor("impresion.parametros", "RUT_" + rutFirmadorp.split("-")[0], "CARPETA");
			
			DocumentosCliente documentosCliente = new DocumentosCliente();
			byte[] archivo = documentosCliente.downloadFirma(nombreArchivop, firmador, fechap);

			if(download)
				response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivop);
			
			out = response.getOutputStream();			    
         	out.write(archivo, 0, archivo.length);
         	out.flush();
     
            if(out != null)
                  out.close();
        } catch(HTTPException e){
            logger.error("Error HTTP codigo " + e.getStatusCode() + " al buscar documento: " + e.getMessage(),e);
            request.setAttribute("error", "Archivo no encontrado.");	        	
        } catch (Exception e) {
            logger.error("Error al buscar documento: " + e.getMessage(),e);
            request.setAttribute("error", "Archivo no encontrado.");
        } finally{
            if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
        }
        
        
    } 	
	 public void downloadFirma2(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	            HttpServletResponse response) {
		 	response.setContentType("application/pdf");
		 	logger.debug("Iniciando");
		 	Boolean download = true;
		 	
			String documentoReq = request.getParameter("documento");
			logger.debug(documentoReq);
			if(request.getParameter("download")!=null)
				download = new Boolean(request.getParameter("download"));
			
			ServletOutputStream out = null;

			try {
				//JSONParser jsonParser = new JSONParser();
				//JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq); 
				String nombreArchivop = request.getParameter("nombreArchivo"); //documentoJSON.get("nombreArchivo").toString();  
				String rutFirmadorp = request.getParameter("rutFirmador");//documentoJSON.get("rutFirmador").toString();
				String fecha=request.getParameter("fechaDocumento");
				Date fechap = null;
				if(fecha!=null)
					fechap = new Date(new Long(fecha));
				
				String firmador = TablaValores.getValor("impresion.parametros", "RUT_" + rutFirmadorp.split("-")[0], "CARPETA");
				
				DocumentosCliente documentosCliente = new DocumentosCliente();
				byte[] archivo = documentosCliente.downloadFirma(nombreArchivop, firmador, fechap);

				if(download)
					response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivop);
				
				out = response.getOutputStream();			    
	         	out.write(archivo, 0, archivo.length);
	         	out.flush();
	     
	            if(out != null)
	                  out.close();
	        } catch(HTTPException e){
	            logger.error("Error HTTP codigo " + e.getStatusCode() + " al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");	        	
	        } catch (Exception e) {
	            logger.error("Error al buscar documento: " + e.getMessage(),e);
	            request.setAttribute("error", "Archivo no encontrado.");
	        } finally{
	            if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
	        }
	        
	        
	    } 	
	
	public void existeEscritura(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String escrituraReq = request.getParameter("escritura");
		
		JSONObject json = new JSONObject();

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject escrituraJSON = (JSONObject)jsonParser.parse(escrituraReq);
			Long idDocumentop = escrituraJSON.get("idDocumento")==null?0L:Long.parseLong(escrituraJSON.get("idDocumento").toString()); 
			Long idDetalleDocumentop = escrituraJSON.get("idDetalleDocumento")==null?0L:Long.parseLong(escrituraJSON.get("idDetalleDocumento").toString()); 
			Long caratulap = escrituraJSON.get("caratula")==null?0L:Long.parseLong(escrituraJSON.get("caratula").toString()); 
			Integer versionp = escrituraJSON.get("version")==null?0:Integer.parseInt(escrituraJSON.get("version").toString()); 
			boolean esVersion = escrituraJSON.get("esVersion")==null?false:Boolean.parseBoolean(escrituraJSON.get("esVersion").toString());
			boolean esDetalle = escrituraJSON.get("esDetalle")==null?false:Boolean.parseBoolean(escrituraJSON.get("esDetalle").toString());
			boolean esPrincipal = escrituraJSON.get("esPrincipal")==null?false:Boolean.parseBoolean(escrituraJSON.get("esPrincipal").toString());
			
			DocumentosCliente documentosCliente = new DocumentosCliente();
			json = documentosCliente.existeEscritura(idDocumentop, idDetalleDocumentop, caratulap, versionp, esVersion, esDetalle, esPrincipal);			

		} catch(HTTPException e){
			logger.error("Error HTTP codigo : "+e.getStatusCode(),e);
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void agregarBitacora(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");
		String comentarioReq = request.getParameter("comentario");
		String categoriaReq = request.getParameter("categoria");
		String correoReq = request.getParameter("correo");

		Integer categoria = null;
		if(StringUtils.isNotBlank(categoriaReq)){
			categoria=Integer.parseInt(categoriaReq);
		}
		


		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratula", caratulaReq);
		req.put("comentario", comentarioReq);
		req.put("categoria", categoriaReq);
		json.put("req", req);

		CaratulasUtil caratulasUtil = new CaratulasUtil();

		try {					
			Long numeroCaratula = Long.parseLong(caratulaReq);
			String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
			rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
//			motivoReq = cambiaEncoding(motivoReq);
			
			if(comentarioReq!=null && !"".equals(comentarioReq.trim())){
				BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(numeroCaratula, rutFuncionario, comentarioReq, categoria);
				json.put("bitacoraDTO", bitacoraDTO);
				json.put("status", true);
				
				if(categoria==1 && correoReq!=null && !correoReq.equalsIgnoreCase("null")){
			    	String template = TemplateMaker.getNotificacion(bitacoraDTO, numeroCaratula);
			    	template = template.replaceAll("'", "&#39;");
			    	StringBuffer html = new StringBuffer(template);				    					    	
			        String from = "portal.conservador.noreply@conservador.cl";
			        String subject = "Actualización carátula";
		        	new SendMail().sendMensaje(correoReq, from, subject, html, "", false, true);
				}
			} else
				json.put("status", false);
					
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al agregar bitacora");
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void vincularCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaObjReq = request.getParameter("caratulaObj");
		String caratulaSrcReq = request.getParameter("caratulaSrc");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("caratulaObj", caratulaObjReq);
		req.put("caratulaSrc", caratulaSrcReq);
		json.put("req", req);

		CaratulasUtil caratulasUtil = new CaratulasUtil();

		try {					
			Long caratulaObj = Long.parseLong(caratulaObjReq);
			Long caratulaSrc = Long.parseLong(caratulaSrcReq);
			String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
			rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');			
			
			logger.debug("vinculando los documentos entrega de caratula " + caratulaSrc + " en caratula " + caratulaObj);
			
			FirmaEDAO dao = new FirmaEDAO();
			int rows = dao.updateCaratulaFirma(caratulaSrc, caratulaObj);
			
			if(rows>0){
				String comentarioReq = "Documentos entrega vinculados a caratula " + caratulaObj;
				BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(caratulaSrc, rutFuncionario, comentarioReq, 0);
				json.put("bitacoraDTO", bitacoraDTO);
				
				comentarioReq =  "Documentos entrega vinculados desde caratula " + caratulaSrc;
				caratulasUtil.agregarBitacoraCaratula(caratulaObj, rutFuncionario, comentarioReq, 0);
				
				json.put("status", true);
			} else{
				json.put("status", false);
				json.put("msg", "No se vincularon documentos");
			}
					
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al vincular caratula");
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error("Error: "+e.getMessage(), e);
		}		
	}	
	
	private String cambiaEncoding(String campo) throws UnsupportedEncodingException{
		String campoConEncoding=new String("");
		if(null!=campo){
			if(!"".equals(campo)){
				campoConEncoding = new String(campo.getBytes(),"UTF-8");
			}
		}
		
		return campoConEncoding;
		
	}
}