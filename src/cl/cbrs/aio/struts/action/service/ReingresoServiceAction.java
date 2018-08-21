package cl.cbrs.aio.struts.action.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.foliomercantil.vo.EstadoVO;
import cl.cbr.foliomercantil.vo.FojaCaratulaVO;
import cl.cbr.foliomercantil.vo.NotarioElectronicoVO;
import cl.cbr.foliomercantil.vo.WorkflowEstadoVO;
import cl.cbr.foliomercantil.vo.WorkflowVO;
import cl.cbr.util.SystemException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.CaratulaDTO;
import cl.cbrs.aio.dto.CierreCtasCtesFinalDTO;
import cl.cbrs.aio.dto.FormularioDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.ReglaReingresoDTO;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.ComercioUtil;
import cl.cbrs.aio.util.ParametrosUtil;
import cl.cbrs.aio.util.RegistrosUtil;
import cl.cbrs.caratula.flujo.vo.CtaCteVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.cuentacorriente.delegate.WsCuentaCorrienteClienteDelegate;
import cl.cbrs.cuentacorriente.vo.ListaNominaCtaCteAioVO;
import cl.cbrs.cuentacorriente.vo.NominaCtaCteAioVO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.notificaciones.Notificador;
import cl.cbrs.ws.notarios.delegate.WsNotarioElectronicoDelegate;
import cl.ee.mensaje.GeneraMensajeEE;

public class ReingresoServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(ReingresoServiceAction.class);
	public static final String ARCHIVO_PROPERTIES = "reingreso.parametros";
	
	private static ArrayList<FormularioDTO> CACHE_LISTA_FORMULARIOS = null;
	private static WorkflowVO[] CACHE_LISTA_WORKFLOWS = null;
	private static NotarioElectronicoVO[] CACHE_LISTA_NOTARIOS;
	private final String REINGRESO_COMERCIO="X0";
	private static int OBSERVASION_INTERNA = 0;

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void buscarCaratula(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaReq = request.getParameter("caratula");

		JSONObject json = new JSONObject();
		json.put("estado", true);	

		CaratulasUtil caratulasUtil = new CaratulasUtil();

		try {		
			Long caratula = new Long(caratulaReq);
			CaratulaDTO caratulaDTO = caratulasUtil.getCaratulaDTO(caratula);
			if(caratulaDTO!=null){
				json.put("caratulaDTO", caratulaDTO);
				request.getSession().setAttribute("numeroCaratula", caratula);
				
				InscripcionDigitalDTO digitalDTO = (InscripcionDigitalDTO)request.getSession().getAttribute("inscripcion");
				if(digitalDTO!=null){
					json.put("inscripcionDigitalDTO", digitalDTO);
					request.getSession().removeAttribute("inscripcion");
				}
			} else{
				json.put("msg", "No se encontró la carátula " + caratulaReq);
				json.put("estado", false);
			}

			
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al buscar caratula");
			json.put("estado", false);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void reingresarCaratula(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaDTOReq = request.getParameter("caratulaDTO");
		String caratulaOriginalDTOReq = request.getParameter("caratulaOriginalDTO");
		String observacionReq = request.getParameter("observacion")!=null?request.getParameter("observacion").trim():"";
		String codigoExtractoReq = request.getParameter("codigoExtracto")!=null&&!"".equals(request.getParameter("codigoExtracto").trim())?request.getParameter("codigoExtracto").trim():null;
		String notarioReq = request.getParameter("notario");		
		String workflowReq = request.getParameter("workflow");

		JSONObject json = new JSONObject();
		json.put("estado", false);	
		
		String detalleError = "";
		String observacionBitacora = !"".equals(observacionReq)?observacionReq : "Reingreso desde AIO.";

		CaratulasUtil caratulasUtil = new CaratulasUtil();
		ComercioUtil comercioUtil = new ComercioUtil();
		WsInscripcionDigitalDelegate digitalDelegate = new WsInscripcionDigitalDelegate();	

		try {		
			String usuario = request.getUserPrincipal().getName().replaceAll("CBRS\\\\", "");
			JSONParser jsonParser = new JSONParser();						
			
			//Data
			JSONObject caratulaDTOJSON = (JSONObject)jsonParser.parse(cambiaEncoding(caratulaDTOReq));
			CaratulaDTO caratulaDTO = caratulasUtil.getCaratulaDTO(caratulaDTOJSON);
			
			//Data original
			JSONObject caratulaOriginalDTOJSON = (JSONObject)jsonParser.parse(caratulaOriginalDTOReq);
			CaratulaDTO caratulaOriginalDTO = caratulasUtil.getCaratulaDTO(caratulaOriginalDTOJSON);
			
			if(caratulaDTO.getInscripcionDigitalDTO()==null){
				detalleError += "Inscripcion Digital nula";
				throw new Exception("Inscripcion Digital nula");
			}
			
			logger.info("Reingresando caratula " + caratulaOriginalDTO.getNumeroCaratula() + "\n Usuario: " + usuario + 
					" CaratulaDTO: " + caratulaDTOReq + " Observacion: " + observacionReq);
			
			//Si es cta corriente...
			if(caratulaDTO.getCodigo()!=null){
				//Buscar si la caratula esta en cierre ctas corrientes
				FlujoDAO flujoDAO = new FlujoDAO();
				CierreCtasCtesFinalDTO ctasCtesFinalDTO = flujoDAO.getCierreFinalCaratula(caratulaDTO.getNumeroCaratula());
				if(ctasCtesFinalDTO!=null){					
					if(ctasCtesFinalDTO.getCierreActual()==0 && ctasCtesFinalDTO.getMitadDeMes()==0)
						throw new SystemException("Carátula no se puede reingresar, está en cierre de cta corriente y la nómina ya fué enviada.");
					else
						flujoDAO.eliminarCierreFinalCaratula(caratulaDTO.getNumeroCaratula());
				}	
			}
			
			//Comparar Inscripciones
			boolean actualizarInscripcion = false;
//			if( /*(caratulaDTO.getInscripcionDigitalDTO()!=null && caratulaOriginalDTO.getInscripcionDigitalDTO()==null) ||*/
//					( caratulaDTO.getInscripcionDigitalDTO().getFoja()!=null && (caratulaOriginalDTO.getInscripcionDigitalDTO()==null || !caratulaDTO.getInscripcionDigitalDTO().getFoja().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getFoja())) ) ||
//					( caratulaDTO.getInscripcionDigitalDTO().getNumero()!=null && !caratulaDTO.getInscripcionDigitalDTO().getNumero().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getNumero()) ) ||
//					( caratulaDTO.getInscripcionDigitalDTO().getAno()!=null && !caratulaDTO.getInscripcionDigitalDTO().getAno().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getAno()) ) ||
//					( caratulaDTO.getInscripcionDigitalDTO().getBis()!=null && !caratulaDTO.getInscripcionDigitalDTO().getBis().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getBis()) ) /*||
//					( caratulaDTO.getInscripcionDigitalDTO().getRegistro()!=null && (caratulaOriginalDTO.getInscripcionDigitalDTO()==null || !caratulaDTO.getInscripcionDigitalDTO().getRegistro().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getRegistro())) )*/
//			){
			if( caratulaDTO.getInscripcionDigitalDTO().getFoja()!=null && caratulaDTO.getInscripcionDigitalDTO().getNumero()!=null && caratulaDTO.getInscripcionDigitalDTO().getAno()!=null &&					
					( caratulaOriginalDTO.getInscripcionDigitalDTO()==null ||
					( !caratulaDTO.getInscripcionDigitalDTO().getFoja().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getFoja()) ) ||
					( !caratulaDTO.getInscripcionDigitalDTO().getNumero().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getNumero()) ) ||
					( !caratulaDTO.getInscripcionDigitalDTO().getAno().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getAno()) ) ||
					( !caratulaDTO.getInscripcionDigitalDTO().getBis().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getBis()) ) ||
					( caratulaDTO.getInscripcionDigitalDTO().getRegistro()!=null && (caratulaOriginalDTO.getInscripcionDigitalDTO()==null || !caratulaDTO.getInscripcionDigitalDTO().getRegistro().equals(caratulaOriginalDTO.getInscripcionDigitalDTO().getRegistro())) ))
			){			
				actualizarInscripcion = true;
			}
			
			//Comparar Tipo de Formulario
			boolean actualizarTipoFormulario = false;
			if( !caratulaDTO.getTipoFormularioDTO().getId().equals(caratulaOriginalDTO.getTipoFormularioDTO().getId()) ){
				actualizarTipoFormulario = true;
			}
			
			//Actualizar flujo si hay cambio de inscripcion, tipo formulario o se reingresa un GP
			if(actualizarInscripcion || actualizarTipoFormulario || caratulaDTO.getTipoFormularioDTO().getId().intValue()==5){				
				//Para reingreso GP se debe dejar impTicket en 0
				if(caratulaDTO.getTipoFormularioDTO().getId().intValue()==5)
					caratulaDTO.getInscripcionDigitalDTO().setImpTicket(false);
				
				//dejando caratula en proceso
				caratulaDTO.setEstadoForm("p");
				
				caratulaDTO = caratulasUtil.updateCaratulaDTO(caratulaDTO);
				
				if(actualizarInscripcion){
					
					observacionBitacora += " Se actualiza inscripción de Fojas: " + caratulaOriginalDTO.getInscripcionDigitalDTO().getFoja() + " Número: " +caratulaOriginalDTO.getInscripcionDigitalDTO().getNumero() +
							" Año: " + caratulaOriginalDTO.getInscripcionDigitalDTO().getAno();
					if(caratulaOriginalDTO.getInscripcionDigitalDTO().getBis()!=null && caratulaOriginalDTO.getInscripcionDigitalDTO().getBis())
						observacionBitacora += " bis";
					if(caratulaOriginalDTO.getInscripcionDigitalDTO().getRegistroDTO()!=null)
						observacionBitacora += " Registro de " + caratulaOriginalDTO.getInscripcionDigitalDTO().getRegistroDTO().getDescripcion() + ", ";
					
					observacionBitacora += "a Fojas: " + caratulaDTO.getInscripcionDigitalDTO().getFoja() + " Número: " +caratulaDTO.getInscripcionDigitalDTO().getNumero() +
							" Año: " + caratulaDTO.getInscripcionDigitalDTO().getAno();
					if(caratulaDTO.getInscripcionDigitalDTO().getBis()!=null && caratulaDTO.getInscripcionDigitalDTO().getBis())
						observacionBitacora += " bis";
					observacionBitacora += " Registro de " + caratulaDTO.getInscripcionDigitalDTO().getRegistroDTO().getDescripcion() + ".";
				}
				
				if(actualizarTipoFormulario){
					observacionBitacora += " Se actualiza tipo de formulario a: " + caratulaDTO.getTipoFormularioDTO().getDescripcion() + ".";
				}
			}
			
			json.put("caratulaDTO", caratulaDTO);				
					
			ReglaReingresoDTO reglaReingresoDTO = null;
			
			if(CacheAIO.CACHE_REGLAS_REINGRESO.containsKey(caratulaDTO.getTipoFormularioDTO().getId())){
				logger.info("Regla "+ caratulaDTO.getTipoFormularioDTO().getId() +" en CACHE_AIO");
				reglaReingresoDTO = CacheAIO.CACHE_REGLAS_REINGRESO.get(caratulaDTO.getTipoFormularioDTO().getId());
			} else{
				logger.info("Agregando regla "+ caratulaDTO.getTipoFormularioDTO().getId() +" a CACHE_AIO");
				String seccion = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+caratulaDTO.getTipoFormularioDTO().getId(),  "seccion" );
				String idEstado = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+caratulaDTO.getTipoFormularioDTO().getId(),  "estado" );
				String idUsuario = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+caratulaDTO.getTipoFormularioDTO().getId(),  "usuario" );
				String rutFuncionario = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+caratulaDTO.getTipoFormularioDTO().getId(),  "rut_funcionario" );
				String registro = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+caratulaDTO.getTipoFormularioDTO().getId(),  "registro" );
				reglaReingresoDTO = new ReglaReingresoDTO();
				reglaReingresoDTO.setCodSeccion(seccion);
				reglaReingresoDTO.setIdEstado(!"".equals(idEstado)?new Integer(idEstado):null);
				reglaReingresoDTO.setIdTipoFormulario(caratulaDTO.getTipoFormularioDTO().getId());
				reglaReingresoDTO.setIdUsuarioComercio(!"".equals(idUsuario)?new Integer(idUsuario):null);
				reglaReingresoDTO.setRegistro(registro);
				reglaReingresoDTO.setRutFuncionario(rutFuncionario);
				CacheAIO.CACHE_REGLAS_REINGRESO.put(caratulaDTO.getTipoFormularioDTO().getId(), reglaReingresoDTO);
			}
			
			if("Comercio".equalsIgnoreCase(reglaReingresoDTO.getRegistro())){
				if(workflowReq!=null && !"".equals(workflowReq)){
					//INGRESAR EN FLUJO DE COMERCIO		
					Long foja = caratulaDTO.getInscripcionDigitalDTO().getFoja()!=null?caratulaDTO.getInscripcionDigitalDTO().getFoja() : 0L;
					String numero = caratulaDTO.getInscripcionDigitalDTO().getNumero()!=null&&!"".equals(caratulaDTO.getInscripcionDigitalDTO().getNumero())?caratulaDTO.getInscripcionDigitalDTO().getNumero() : "0" ;
					Long ano = caratulaDTO.getInscripcionDigitalDTO().getAno()!=null?caratulaDTO.getInscripcionDigitalDTO().getAno() : 0L;
					FojaCaratulaVO fojaCaratulaVO = new FojaCaratulaVO();
					fojaCaratulaVO.setFoja( foja );
					fojaCaratulaVO.setNumero( numero );
					fojaCaratulaVO.setAno( ano );
					
					JSONObject workflowJSON = (JSONObject)jsonParser.parse(workflowReq);
					Long idWorkflow = new Long(workflowJSON.get("id").toString());
					WorkflowVO workflowVO = null;
					
					//Lista Workflows
					if(CACHE_LISTA_WORKFLOWS==null){
						CACHE_LISTA_WORKFLOWS = comercioUtil.getWorkflows();				
					}
					
					for(int i=0; i<CACHE_LISTA_WORKFLOWS.length; i++){
						if(CACHE_LISTA_WORKFLOWS[i].getIdWorkflow().equals(idWorkflow))
							workflowVO = CACHE_LISTA_WORKFLOWS[i];
					}

					Long idNotario = null;
					if(notarioReq!=null){
						JSONObject notarioJSON = (JSONObject)jsonParser.parse(notarioReq);
						idNotario = notarioJSON!=null && notarioJSON instanceof JSONObject? new Long(notarioJSON.get("id").toString()) : null;
					}
												
					comercioUtil.ingresaCaratulaFlujo(caratulaDTO.getNumeroCaratula(), workflowVO, fojaCaratulaVO , observacionReq, codigoExtractoReq, idNotario);
					
					//Actualizar inscripcion en flujo si para comercio se inserto 0, 0, 0
					if(fojaCaratulaVO.getFoja().equals(0L) && fojaCaratulaVO.getNumero().equals("0") && fojaCaratulaVO.getAno().equals(0L)){
							caratulaDTO.getInscripcionDigitalDTO().setFoja(0L);
							caratulaDTO.getInscripcionDigitalDTO().setNumero("0");
							caratulaDTO.getInscripcionDigitalDTO().setAno(0L);
							caratulaDTO.getInscripcionDigitalDTO().setRegistro(caratulaDTO.getInscripcionDigitalDTO().getRegistro());
						
							caratulaDTO = caratulasUtil.updateCaratulaDTO(caratulaDTO);
					}
									
					WorkflowEstadoVO[] estadosWorflow = workflowVO.getEstadosVO();
					WorkflowEstadoVO estadoWorkflow = estadosWorflow[1];
					EstadoVO estado = estadoWorkflow.getEstadoVO();
					cl.cbr.foliomercantil.vo.EstadoCaratulaVO estadoCaratulaVO = new cl.cbr.foliomercantil.vo.EstadoCaratulaVO();
					//estadoCaratulaVO.setCaratulaVO(caratulaVO);
					estadoCaratulaVO.setIdUsuario(1L);
					estadoCaratulaVO.setEstadoVO(estado);
					estadoCaratulaVO.setFecha(new Date());
					estadoCaratulaVO.setObservacionUsuario(observacionReq);
					comercioUtil.moverCaratula(estadoCaratulaVO, caratulaDTO.getNumeroCaratula());
					
					//Moviendo caratula comercio en flujo 
					FuncionarioVO funcionarioVO = new FuncionarioVO((String)request.getSession().getAttribute("rutUsuario"));
					EstadoCaratulaVO estadoCaratulaFlujoVO = new EstadoCaratulaVO();
					estadoCaratulaFlujoVO.setEnviadoPor(funcionarioVO);
					estadoCaratulaFlujoVO.setFechaMovimiento(new Date());
					estadoCaratulaFlujoVO.setMaquina(request.getRemoteHost());
					estadoCaratulaFlujoVO.setResponsable(new FuncionarioVO(reglaReingresoDTO.getRutFuncionario()));
					SeccionVO seccionVO = new SeccionVO();
					seccionVO.setCodigo(REINGRESO_COMERCIO);
					estadoCaratulaFlujoVO.setSeccion(seccionVO);

					caratulasUtil.moverCaratulaSeccion(caratulaDTO.getNumeroCaratula(), estadoCaratulaFlujoVO );
					
					json.put("msg", "Carátula reingresada.");
										
				} else{
					//ACTUALIZAR COMERCIO			
					if(caratulaDTO.getInscripcionDigitalDTO() != null)
						comercioUtil.actualizarFojaCaratula(caratulaDTO.getInscripcionDigitalDTO(), caratulaDTO.getNumeroCaratula());					
					
					//Mover caratula a seccion parametrizada en Excel			
					if(reglaReingresoDTO.getCodSeccion()!=null && !"".equals(reglaReingresoDTO.getCodSeccion())){					
						//Mover caratula en folio_mercantil a seccion parametrizada en Excel
						cl.cbr.foliomercantil.vo.EstadoCaratulaVO estadoCaratulaVO = new cl.cbr.foliomercantil.vo.EstadoCaratulaVO();
						cl.cbr.foliomercantil.vo.EstadoVO estadoVO = new cl.cbr.foliomercantil.vo.EstadoVO();
						estadoVO.setIdEstado(new Long(reglaReingresoDTO.getIdEstado()));
						estadoCaratulaVO.setEstadoVO(estadoVO );
						estadoCaratulaVO.setFecha(new Date());
						estadoCaratulaVO.setIdUsuario(new Long(reglaReingresoDTO.getIdUsuarioComercio()));
						estadoCaratulaVO.setNombreUsuario(usuario);
						estadoCaratulaVO.setObservacionUsuario(observacionReq);
						comercioUtil.moverCaratula(estadoCaratulaVO, caratulaDTO.getNumeroCaratula());
						
						//Moviendo caratula comercio en flujo 
						FuncionarioVO funcionarioVO = new FuncionarioVO((String)request.getSession().getAttribute("rutUsuario"));
						EstadoCaratulaVO estadoCaratulaFlujoVO = new EstadoCaratulaVO();
						estadoCaratulaFlujoVO.setEnviadoPor(funcionarioVO);
						estadoCaratulaFlujoVO.setFechaMovimiento(new Date());
						estadoCaratulaFlujoVO.setMaquina(request.getRemoteHost());
						estadoCaratulaFlujoVO.setResponsable(new FuncionarioVO(reglaReingresoDTO.getRutFuncionario()));
						SeccionVO seccionVO = new SeccionVO();
						seccionVO.setCodigo(REINGRESO_COMERCIO);
						estadoCaratulaFlujoVO.setSeccion(seccionVO);

						caratulasUtil.moverCaratulaSeccion(caratulaDTO.getNumeroCaratula(), estadoCaratulaFlujoVO );
						
						
						json.put("msg", "Carátula reingresada.");
					} else{
						//Si no hay seccion parametrizada, solicitar reingreso manual
						json.put("msg", "Carátula reingresada. Debe ser distribuida manualmente");
					}					
				}
				

			} else{ 
				//PROPIEDADES, PROHIBICIONES, AGUAS, HIPOTECAS
				
				boolean moverCaratula = reglaReingresoDTO.getCodSeccion()!=null && !"".equals(reglaReingresoDTO.getCodSeccion());
				
				//Ya no se valida si es digital para reingreso (al igual que basic). 24-11-2017
//				Boolean esDigital = false;
//				if(caratulaDTO.getTipoFormularioDTO().getId().equals(1L)){
//					esDigital = digitalDelegate.validaAnosDigitales(caratulaDTO.getInscripcionDigitalDTO().getFoja(), caratulaDTO.getInscripcionDigitalDTO().getNumero(), caratulaDTO.getInscripcionDigitalDTO().getAno());
//				}
				
				//Excepciones Excel
//				if( caratulaDTO.getTipoFormularioDTO().getId().equals(1) && !esDigital){
//					logger.info("Excepcion Excel: distribucion manual");
//					moverCaratula = false; //Si es Inscripcion de Propiedad y no esta digitalizada distribuir manualmente
//				}
				
				if( moverCaratula ){
					FuncionarioVO funcionarioVO = new FuncionarioVO((String)request.getSession().getAttribute("rutUsuario"));
					EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
					estadoCaratulaVO.setEnviadoPor(funcionarioVO);
					estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
					estadoCaratulaVO.setResponsable(new FuncionarioVO(reglaReingresoDTO.getRutFuncionario()));
												
					//09-11-2017 Mover caratula a seccion Reingreso antes de mover en flujo a seccion parametrizada. 
					estadoCaratulaVO.setSeccion(new SeccionVO("63")); // Seccion reingreso
					if(caratulaDTO.getTipoFormularioDTO().getId().equals(5)){
						estadoCaratulaVO.setSeccion(new SeccionVO("RG")); //Para GP enviar a seccion Reingreso de GP
					}
					estadoCaratulaVO.setFechaMovimiento(new Date());
					caratulasUtil.moverCaratulaSeccion(caratulaDTO.getNumeroCaratula(), estadoCaratulaVO );
					
					//Mover caratula en flujo a seccion parametrizada en Excel												
					estadoCaratulaVO.setFechaMovimiento(new Date());									
					estadoCaratulaVO.setSeccion(new SeccionVO(reglaReingresoDTO.getCodSeccion()));
					
					//Excepciones Excel
					if( (caratulaDTO.getTipoFormularioDTO().getId().equals(8) || caratulaDTO.getTipoFormularioDTO().getId().equals(9)) && !caratulaDTO.getInscripcionDigitalDTO().getRegistro().equals(1) ){
						estadoCaratulaVO.setSeccion(new SeccionVO("37")); //Para 'copias de inscripciones' de Hipotecas, Prohibiciones y Aguas enviar a "37 - Copias Propiedades"
					}
					
					caratulasUtil.moverCaratulaSeccion(caratulaDTO.getNumeroCaratula(), estadoCaratulaVO );
					json.put("msg", "Carátula reingresada.");
				} else{
					//Si no hay seccion parametrizada, solicitar reingreso manual
					json.put("msg", "Caratula lista para distribuir a la seccion correspondiente");
				}
			}
			
			//Agregar bitacora
			try{
				String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
				BitacoraDTO bitacoraDTO = caratulasUtil.agregarBitacoraCaratula(caratulaDTO.getNumeroCaratula(), rutUsuario, observacionBitacora,OBSERVASION_INTERNA);
				json.put("bitacoraDTO", bitacoraDTO);
			} catch (Exception e) {
				logger.error("Error: " + e.getMessage(), e);
			}
			
			String habilitarEE= TablaValores.getValor(ARCHIVO_PROPERTIES, "HABILITAR_EE", "valor");
            if("SI".equals(habilitarEE) && caratulaDTO.getOrigenCreacion()!=null && caratulaDTO.getOrigenCreacion().intValue()==3){
            	
                Notificador notificador = new Notificador();
                notificador.notificar(GeneraMensajeEE.getMensajeTramiteReingresado(caratulaDTO.getIdTransaccion(), ""+caratulaDTO.getNumeroCaratula().longValue(), new Date()));
            }			
			
			json.put("estado", true);		
						
			
		} catch (SystemException e) {
			json.put("msg", e.getMessage());
			logger.warn(e.getMessage());
		} catch (GeneralException e) {
			json.put("msg", "Problemas en servidor al reingresar caratula: " + e.getInfoAdic());
			logger.error("Error: " + e.getMessage(),e);
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al reingresar caratula: " + detalleError);
			logger.error("Error: " + e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
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
	
	@SuppressWarnings({ "unchecked" })
	public void getListas(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String caratulaDTOReq = request.getParameter("caratulaDTO");
		
		JSONObject json = new JSONObject();
		json.put("estado", true);	

		RegistrosUtil registrosUtil = new RegistrosUtil();
		ComercioUtil comercioUtil = new ComercioUtil();

		try {		
			JSONParser jsonParser = new JSONParser();
			caratulaDTOReq = cambiaEncoding(caratulaDTOReq);
			JSONObject caratulaDTOJSON = (JSONObject)jsonParser.parse(caratulaDTOReq);
			
			//Lista Workflows
			if(CACHE_LISTA_WORKFLOWS==null){
				CACHE_LISTA_WORKFLOWS = comercioUtil.getWorkflows();				
			}
			
			JSONArray listaWorkflowsJSONIns = new JSONArray();
			JSONArray listaWorkflowsJSONSolCv = new JSONArray();
			JSONArray listaWorkflowsJSONSolSv = new JSONArray();
			JSONArray listaWorkflowsJSONSolVig = new JSONArray();
			
			String idWorkFlowSoportadosStr = TablaValores.getValor(ARCHIVO_PROPERTIES, "FILTRO_WORFLOW",  "idWorkFlowSoportados" );
    		String[] idWorkFlowSoportados = idWorkFlowSoportadosStr.split(",");

            for (WorkflowVO workflowVO : CACHE_LISTA_WORKFLOWS) {
            	JSONObject workflowJSON = new JSONObject();

            	//Filtro Lista WorkFlow
            	for(String idWorkFlow:idWorkFlowSoportados){

            		if(idWorkFlow.equals(workflowVO.getIdWorkflow().toString())){
            			
            			workflowJSON.put("id", workflowVO.getIdWorkflow());
                    	workflowJSON.put("nombre", workflowVO.getNombre());
                    	
                    	if(workflowVO.getIdWorkflow()==4){
                    		listaWorkflowsJSONSolCv.add(workflowJSON);
                    	}else if(workflowVO.getIdWorkflow()==12){
                    		listaWorkflowsJSONSolSv.add(workflowJSON);
                    	}else if(workflowVO.getIdWorkflow()==10 || workflowVO.getIdWorkflow()==16){
                    		listaWorkflowsJSONSolVig.add(workflowJSON);
                    	}else{
                    		listaWorkflowsJSONIns.add(workflowJSON);
                    	}
                    	
                    	break;
            		}
            	}
            	
            }                
            
            json.put("listaWorkflowsJSONIns", listaWorkflowsJSONIns);
            json.put("listaWorkflowsJSONSolCv", listaWorkflowsJSONSolCv);
            json.put("listaWorkflowsJSONSolSv", listaWorkflowsJSONSolSv);
            json.put("listaWorkflowsJSONSolVig", listaWorkflowsJSONSolVig);
			
			ParametrosUtil parametrosUtil = new ParametrosUtil();
			
			if( caratulaDTOJSON.get("inscripcionDigitalDTO")!=null && caratulaDTOJSON.get("inscripcionDigitalDTO") instanceof JSONObject ){
				JSONObject inscripcionDigitalJSON = (JSONObject)caratulaDTOJSON.get("inscripcionDigitalDTO");
				if( inscripcionDigitalJSON.get("registroDTO")!=null && inscripcionDigitalJSON.get("registroDTO") instanceof JSONObject ){
					JSONObject registroDTOJSON = (JSONObject)inscripcionDigitalJSON.get("registroDTO");
					Long idRegistro = (Long)registroDTOJSON.get("id");
					
					//Tipos Formularios					
					if(CACHE_LISTA_FORMULARIOS==null)
						CACHE_LISTA_FORMULARIOS = parametrosUtil.getFormulariosDTO();			
					
					json.put("listaTiposFormulario", getListaTiposFormulario(idRegistro));
				}else{
					JSONArray listaTiposFormulario = new JSONArray();
					JSONObject tipoFormularioJSON = new JSONObject();
					JSONObject tipoFormularioDTOJSON = (JSONObject)caratulaDTOJSON.get("tipoFormularioDTO");
					Long tipoFormulario = (Long)tipoFormularioDTOJSON.get("id");
					tipoFormularioJSON.put("id", tipoFormulario);
					tipoFormularioJSON.put("descripcion", (String)tipoFormularioDTOJSON.get("descripcion"));
					listaTiposFormulario.add(tipoFormularioJSON);
					json.put("listaTiposFormulario", listaTiposFormulario);	
				}
				
				//Lista Registros				
					json.put("listaRegistros", registrosUtil.getRegistros());	
			} else{
				JSONArray listaTiposFormulario = new JSONArray();
				JSONObject tipoFormularioJSON = new JSONObject();
				JSONObject tipoFormularioDTOJSON = (JSONObject)caratulaDTOJSON.get("tipoFormularioDTO");
				Long tipoFormulario = (Long)tipoFormularioDTOJSON.get("id");
				tipoFormularioJSON.put("id", tipoFormulario);
				tipoFormularioJSON.put("descripcion", (String)tipoFormularioDTOJSON.get("descripcion"));
				listaTiposFormulario.add(tipoFormularioJSON);
				json.put("listaTiposFormulario", listaTiposFormulario);				
				json.put("listaRegistros", registrosUtil.getRegistros());	
			}

			
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar caratula");
			json.put("estado", false);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}


	private JSONArray getListaTiposFormulario(Long idRegistro) {
		JSONArray listaTiposFormulario = new JSONArray();
		
		String tipoFormulariosStr = TablaValores.getValor(ARCHIVO_PROPERTIES, "ID_REGISTRO_"+idRegistro,  "tipo_formulario" );
		String[] tipoFormularios = tipoFormulariosStr.split(",");
		for(FormularioDTO formularioDTO : CACHE_LISTA_FORMULARIOS){
			for(int i=0; i<tipoFormularios.length; i++){
				if(formularioDTO.getTipo().equalsIgnoreCase(tipoFormularios[i])){
					JSONObject tipoFormularioJSON = new JSONObject();
					tipoFormularioJSON.put("id", new Integer(formularioDTO.getTipo()));
					tipoFormularioJSON.put("descripcion", (formularioDTO.getDescripcion()));
					listaTiposFormulario.add(tipoFormularioJSON);
				}
			}
		}
		return listaTiposFormulario;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getListaNotarios(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String query = request.getParameter("query");
		
		JSONArray notarios = new JSONArray();
		
		WsNotarioElectronicoDelegate notarioElectronicoDelegate = new WsNotarioElectronicoDelegate();
		
		boolean foundMatch = false;
		try{
			if(CACHE_LISTA_NOTARIOS==null)
				CACHE_LISTA_NOTARIOS = notarioElectronicoDelegate.obtenerTodosLosNotarios();
			
			Pattern regex = Pattern.compile(query.toLowerCase(), Pattern.MULTILINE);
			
			for(int i = 0; i<CACHE_LISTA_NOTARIOS.length; i++){
				NotarioElectronicoVO notario = CACHE_LISTA_NOTARIOS[i];
				Matcher regexMatcher = regex.matcher(notario.getNombre().toLowerCase());
	
				foundMatch = regexMatcher.find();
	
				if(foundMatch){
					JSONObject not = new JSONObject();
					
					String nombre = notario.getNombre();
					
					nombre = nombre.replaceAll("\\(", " - ");
					nombre = nombre.replaceAll("\\)", "");
	
					not.put("nombre", nombre);
					not.put("id", notario.getId());
					not.put("idNotario", notario.getIdNotario());
					not.put("empresa", notario.getEmpresa());
	
					notarios.add(not);
				}
			}
		} catch(Exception e){
			
		}
			
		try {
			notarios.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}
			

	}	
	
	@SuppressWarnings({ "unchecked" })
	public void getListaNotariosFull(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		JSONArray notarios = new JSONArray();
		
		WsNotarioElectronicoDelegate notarioElectronicoDelegate = new WsNotarioElectronicoDelegate();
		

		try{

			NotarioElectronicoVO[] notariosFull = notarioElectronicoDelegate.obtenerTodosLosNotarios();
			
			for(int i = 0; i<notariosFull.length; i++){
				NotarioElectronicoVO notario = notariosFull[i];
				JSONObject not = new JSONObject();
					
				String nombre = notario.getNombre();
					
				nombre = nombre.replaceAll("\\(", " - ");
				nombre = nombre.replaceAll("\\)", "");
	
				not.put("nombre", nombre);
				not.put("id", notario.getId());
				not.put("idNotario", notario.getIdNotario());
	
				notarios.add(not);

			}
		} catch(Exception e){
			
		}
			
		try {
			notarios.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
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