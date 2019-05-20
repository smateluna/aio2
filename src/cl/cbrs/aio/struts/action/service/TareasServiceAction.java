package cl.cbrs.aio.struts.action.service;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.keycloak.KeycloakSecurityContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.botondepagoweb.vo.TransaccionWebVO;
import cl.cbr.common.ConstantesComercio;
import cl.cbr.foliomercantil.vo.NotarioElectronicoVO;
import cl.cbr.util.ErroresUtil;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.DocumentoDAO;
import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dao.InformacionesDAO;
import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.CaratulaDTO;
import cl.cbrs.aio.dto.CierreCtasCtesFinalDTO;
import cl.cbrs.aio.dto.DocumentoDTO;
import cl.cbrs.aio.dto.LiquidacionCaratulaDTO;
import cl.cbrs.aio.dto.LiquidacionTemporalDTO;
import cl.cbrs.aio.dto.LiquidacionTemporalIdDTO;
import cl.cbrs.aio.dto.PermisoDTO;
import cl.cbrs.aio.dto.ProductoReceptorEmailDTO;
import cl.cbrs.aio.dto.ProductoWebDTO;
import cl.cbrs.aio.dto.TransaccionWebDTO;
import cl.cbrs.aio.dto.estado.CaratulaEstadoDTO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulaEstadoUtil;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.FuncionarioSeccionUtil;
import cl.cbrs.aio.util.NotarioUtil;
import cl.cbrs.aio.util.RestUtil;
import cl.cbrs.aio.util.UsuarioUtil;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CtaCteVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.ProductoGlosaVO;
import cl.cbrs.caratula.flujo.vo.ProductoReceptorEmailVO;
import cl.cbrs.caratula.flujo.vo.ProductoVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.cuentacorriente.delegate.WsCuentaCorrienteClienteDelegate;
import cl.cbrs.delegate.botondepago.WsBotonDePagoClienteDelegate;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.firmaelectronica.delagate.ClienteWsFirmadorDelegate;
import cl.cbrs.funcionarios.vo.FuncionariosSeccionVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class TareasServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(TareasServiceAction.class);
	private final String TABLA_PARAMETROS="ProcesaImpresion.parametros";
	private final String FECHA_REHACER_IMAGEN= TablaValores.getValor(TABLA_PARAMETROS,"FECHA_REHACER_IMAGEN", "valor");
	private static final Long ESCRITURA = 1L;
	private static final String SECCION_REPERTORIO = "16";
	private static String ARCHIVO_PARAMETROS = "aio.parametros";
	private final String UPLOAD_DIRECTORY =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_VERSIONAR_ESCRITURA", "valor");
	private String UPLOAD_DIRECTORY_OCR =TablaValores.getValor(ARCHIVO_PARAMETROS, "PATH_OCR_ESCRITURA", "valor");
	private static String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
	private String SECCIONES_RECHAZOS =TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "TAB_SECCIONES.RECHAZADOS", "valor");
	private String INICIO_BITACORA_LIQUIDAR = "Liquidacion de caratula aprobada.";
	private static final String CANAL_CAJA = "1";
 
	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings({ "unchecked" })
	public void getCaratulasPorUsuario(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject respuesta = new JSONObject();
		//JSONArray resultado = new JSONArray();
		ArrayList<CaratulaDTO> resultado = new ArrayList<CaratulaDTO>();
		Boolean hayInscripcion = false;
		Boolean tieneRepertorio = false;
		Boolean status = false;
		String msg = "";
		List<CaratulaVO> caratulaVOs = null;		

		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
			CaratulasUtil caratulasUtil = new CaratulasUtil();
			
			caratulaVOs = caratulaClienteDelegate.obtenerCaratulasPorSeccionUsuario(null, usuario);

			//Excepcion: El usuario marojas debe visualizar las caratulas en seccion C2 asignadas a rquense
			if("marojas".equalsIgnoreCase(usuario)){
				List<CaratulaVO> caratulasQuense = caratulaClienteDelegate.obtenerCaratulasPorSeccionUsuario("C2", "rquense");
				if(caratulaVOs!=null){										
					if(caratulasQuense!=null){
						caratulaVOs = new ArrayList<CaratulaVO>(caratulaVOs);
						caratulaVOs.addAll(caratulasQuense);
					}
				} else
					caratulaVOs = caratulasQuense;
			}

			//Excepcion: El usuario mbustamante y GSGORBINI debe visualizar las caratulas en seccion 16 asignadas a jzuniga
			if("mbustamante".equalsIgnoreCase(usuario) || "GSGORBINI".equalsIgnoreCase(usuario)){
				List<CaratulaVO> caratulasQuense = caratulaClienteDelegate.obtenerCaratulasPorSeccionUsuario("16", "jzuniga");
				if(caratulaVOs!=null){										
					if(caratulasQuense!=null){
						caratulaVOs = new ArrayList<CaratulaVO>(caratulaVOs);
						caratulaVOs.addAll(caratulasQuense);
					}
				} else
					caratulaVOs = caratulasQuense;
			}

			//Excepcion: La seccion repertorio debe visualizar ademas a tipo formulario 1 y que esten en caja
			if(caratulaVOs!=null && SECCION_REPERTORIO.equalsIgnoreCase(caratulaVOs.get(0).getEstadoActualCaratula().getSeccion().getCodigo())){
				InformacionesDAO informacionesDAO = new InformacionesDAO();

				List<CaratulaVO> caratulasInscripcion = informacionesDAO.getAllPorSeccionCanalTipoFormulario();
				if(caratulaVOs!=null){										
					if(caratulasInscripcion!=null){
						caratulaVOs = new ArrayList<CaratulaVO>(caratulaVOs);
						caratulaVOs.addAll(caratulasInscripcion);
					}
				}
			}
			
			//Caratulas Reingresos GP pendientes - Solo si el usuario tiene recurso REINGRESO_GP
	    	ArrayList<PermisoDTO> listaPermisos = (ArrayList<PermisoDTO>) request.getSession().getAttribute("permisosUsuario");
	    	if(listaPermisos!=null){
		    	UsuarioUtil util = new UsuarioUtil();
		    	ArrayList<String> subPermisos = util.getSubPermisosUsuarioModulo(listaPermisos, "tareas");
				if(subPermisos.contains("REINGRESO_GP")){
					FuncionarioSeccionUtil funcionarioSeccionUtil = new FuncionarioSeccionUtil();
					FuncionariosSeccionVO funcionarioSeccion = funcionarioSeccionUtil.obtenerFuncionarioVO(usuario);
					
					if(funcionarioSeccion!=null && funcionarioSeccion.getTabFuncionariosVO()!=null && funcionarioSeccion.getTabFuncionariosVO().getTabSeccionesVO()!=null && funcionarioSeccion.getTabFuncionariosVO().getTabSeccionesVO().getCodSeccion()!=null){
						Client client = Client.create();
						Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
						String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
						String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");
						WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerCaratulasReingresosGPPendienteSeccion/"+funcionarioSeccion.getTabFuncionariosVO().getTabSeccionesVO().getCodSeccion()));
						ClientResponse clientResponse = wr.type("application/json").get(ClientResponse.class);
						com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();
			
						if(statusRespuesta.getStatusCode() == 200){
							JSONArray jsonArray = (JSONArray) RestUtil.getResponse(clientResponse);
							if(caratulaVOs==null)
								caratulaVOs = new ArrayList<CaratulaVO>();
							for(int i=0; i<jsonArray.size(); i++){					
								CaratulaVO caratulaVO = gson.fromJson(jsonArray.get(i).toString(), CaratulaVO.class);
								caratulaVOs.add(caratulaVO);
							}				
						} else{
							//TODO warning error al cargar tareas de reingresos
						}
					}
				}
	    	}
			
			if(caratulaVOs!=null && caratulaVOs.size()>0){
				for(CaratulaVO caratulaVO: caratulaVOs){
					CaratulaDTO caratulaDTO = caratulasUtil.getCaratulaDTO(caratulaVO);
					resultado.add(caratulaDTO);
					
					if(caratulaDTO.getInscripcionDigitalDTO()!=null && 
						caratulaDTO.getInscripcionDigitalDTO().getFoja()!=null &&
						!caratulaDTO.getInscripcionDigitalDTO().getFoja().equals(0L) 
//						&& caratulaDTO.getInscripcionDigitalDTO().getRegistroDTO()!=null && 
//						caratulaDTO.getInscripcionDigitalDTO().getRegistroDTO().getId()!=null &&
//						!caratulaDTO.getInscripcionDigitalDTO().getRegistroDTO().getId().equals(0)
						)
						hayInscripcion=true;
					if(caratulaDTO.getEstadoActualCaratulaDTO()!=null &&
							caratulaDTO.getEstadoActualCaratulaDTO().getSeccionDTO()!=null &&
							caratulaDTO.getEstadoActualCaratulaDTO().getSeccionDTO().getCodigo()!=null &&
							caratulaDTO.getEstadoActualCaratulaDTO().getSeccionDTO().getCodigo().equals("16"))
							tieneRepertorio=true;
				}
			}

			Integer fechaRehacerImagen = FECHA_REHACER_IMAGEN!=null?new Integer(FECHA_REHACER_IMAGEN):null;
			respuesta.put("FECHA_REHACER_IMAGEN", fechaRehacerImagen);

			status = true;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("caratulas", resultado);
		respuesta.put("hayInscripcion", hayInscripcion);
		respuesta.put("tieneRepertorio", tieneRepertorio);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}		

	@SuppressWarnings({ "unchecked" })
	public void getCaratulasLiquidacionPorUsuario(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject respuesta = new JSONObject();
		//JSONArray resultado = new JSONArray();
		ArrayList<LiquidacionCaratulaDTO> resultado = new ArrayList<LiquidacionCaratulaDTO>();
		Boolean status = false;
		String msg = "";

		List<CaratulaVO> caratulaVOs = null;

		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
			CaratulasUtil caratulasUtil = new CaratulasUtil();

			caratulaVOs = caratulaClienteDelegate.obtenerCaratulasPorSeccionUsuario("RL", usuario);

			if(caratulaVOs!=null && caratulaVOs.size()>0){
				for(CaratulaVO caratulaVO: caratulaVOs){
					LiquidacionCaratulaDTO liquidacionDTO = new LiquidacionCaratulaDTO();
					liquidacionDTO = caratulasUtil.getLiquidacionCaratulaDTO(caratulaVO, true, true);
					resultado.add(liquidacionDTO);
				}
			}

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("caratulas", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	@SuppressWarnings({ "unchecked" })
	public void getCaratulaLiquidacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratulaReq = (String)request.getParameter("numeroCaratula");

		JSONObject respuesta = new JSONObject();
		ArrayList<LiquidacionCaratulaDTO> resultado = new ArrayList<LiquidacionCaratulaDTO>();
		Boolean status = false;
		Boolean estaIngresadaCtaCte = false;
		Boolean puedeLiquidar = true;
		Boolean caratulaLiquidada = false;
		Boolean caratulaPendienteEntregaDoc = false;
		JSONArray documentosLiquidacion = new JSONArray();
		String msg = "";

		CaratulaVO caratulaVO = null;
		LiquidacionCaratulaDTO liquidacionDTO = new LiquidacionCaratulaDTO();

		try {			
			WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
			Long numeroCaratula = new Long(numeroCaratulaReq);
			caratulaVO = caratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);

			if(caratulaVO!=null){		
				CaratulasUtil caratulasUtil = new CaratulasUtil();
				liquidacionDTO = caratulasUtil.getLiquidacionCaratulaDTO(caratulaVO, true, false);
				resultado.add(liquidacionDTO);	

				//Buscar si la caratula esta en cierre ctas corrientes
				FlujoDAO flujoDAO = new FlujoDAO();
				CierreCtasCtesFinalDTO ctasCtesFinalDTO = flujoDAO.getCierreFinalCaratula(numeroCaratula);
				if(ctasCtesFinalDTO!=null && caratulaVO.getCodigo()!=null && caratulaVO.getCodigo()!=0){
					estaIngresadaCtaCte=true;
					CtaCteVO ctaCteVO =  caratulaClienteDelegate.obtenerCtaCte(caratulaVO.getCodigo());
					if(ctaCteVO!=null){				
						CuentaCorrienteDTO cuentaCorrienteDTO = new CuentaCorrienteDTO();
						cuentaCorrienteDTO.setCodigo(ctaCteVO.getCodigo());	
						cuentaCorrienteDTO.setInstitucion(ctaCteVO.getInstitucion());
						cuentaCorrienteDTO.setRut(ctaCteVO.getRut());
						liquidacionDTO.setCuentaCorrienteDTO(cuentaCorrienteDTO);
					}

					liquidacionDTO.setCierreCtasCtesFinalDTO(ctasCtesFinalDTO);

					//Si estan en cero el cierre actual y mitad de mes, significa que ya se envio la boleta y ya no se puede liquidar
					if(ctasCtesFinalDTO.getCierreActual()==0 && ctasCtesFinalDTO.getMitadDeMes()==0)
						puedeLiquidar = false;	

					//Se busca la caratula en las caratulas ya cerradas, si está no se puede liquidar
					//						WsCuentaCorrienteClienteDelegate cuentaCorrienteClienteDelegate = new WsCuentaCorrienteClienteDelegate();
					//						NominaCtaCteAioVO nominaCtaCteAioVO = cuentaCorrienteClienteDelegate.obtenerCierreCtaCte(ctaCteVO.getCodigo().toString(), ctasCtesFinalDTO.getFechaRev(), 0);
					//						ListaNominaCtaCteAioVO[] lista = nominaCtaCteAioVO.getListaNominaCtaCteAioVO();
					//						if(lista!=null){
					//							for(ListaNominaCtaCteAioVO detallenomina: lista){
					//								if(detallenomina.getCaratula().equals(numeroCaratula) && ctasCtesFinalDTO.getCierreActual()==0)
					//									puedeLiquidar = false;						
					//							}
					//						}
				}	

				if(caratulaVO.getCanal()!=null && 1==caratulaVO.getCanal().getId()){
					Client client = Client.create();
					String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
					String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");
					JSONArray caratulas = new JSONArray();

					WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerCaratulasPendientesPorNumCaratula/"+caratulaVO.getNumeroCaratula()));

					ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
					com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

					if(statusRespuesta.getStatusCode() == 200){
						caratulas = (JSONArray) RestUtil.getResponse(clientResponse);
						if(caratulas.size()>0){
							caratulaPendienteEntregaDoc=true;
						}
					}
				}
				
				//Buscar liquidacion anterior en bitacora para aviso
				for(BitacoraCaratulaVO bitacoraCaratulaVO : caratulaVO.getBitacoraCaratulaVO()){
					if(bitacoraCaratulaVO.getObservacion().contains(INICIO_BITACORA_LIQUIDAR)){
						caratulaLiquidada = true;
						break;
					}
				}
				
				//obtener documentos liquidacion
				Client client = Client.create();
				String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
				String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");				

				WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerDocumentosLiquidacionPorNumCaratula/"+caratulaVO.getNumeroCaratula()));

				ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
				com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

				if(statusRespuesta.getStatusCode() == 200){
					documentosLiquidacion = (JSONArray) RestUtil.getResponse(clientResponse);
				}	
				
				BitacoraCaratulaVO[] bitacoraCaratulaVOs = caratulaVO.getBitacoraCaratulaVO();
				JSONArray bitacoras = new JSONArray();
				if(null!=bitacoraCaratulaVOs){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					for(BitacoraCaratulaVO bitacora:bitacoraCaratulaVOs){
						JSONObject fila = new JSONObject();
						fila.put("id", bitacora.getIdBitacora());
						fila.put("observacion", bitacora.getObservacion());
						fila.put("fecha", sdf.format(bitacora.getFecha()));
						fila.put("funcionario", bitacora.getNombreFuncionario()+ " " +  bitacora.getApellidoPaternoFuncionario());
						bitacoras.add(fila);
					}
				}
				respuesta.put("listabitacoras", bitacoras);

				status = true;
			} else{
				msg = "No se pudo obtener mas informacion de la caratula.";
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("liquidacionCaratula", liquidacionDTO);
		respuesta.put("estaIngresadaCtaCte", estaIngresadaCtaCte);
		respuesta.put("puedeLiquidar", puedeLiquidar);
		respuesta.put("caratulaLiquidada", caratulaLiquidada);
		respuesta.put("caratulaPendienteEntregaDoc", caratulaPendienteEntregaDoc);
		respuesta.put("documentosLiquidacion", documentosLiquidacion);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}		
	
	@SuppressWarnings({ "unchecked" })
	public void getGlosaDocumento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String codAlpha = (String)request.getParameter("codAlpha");

		JSONObject respuesta = new JSONObject();
		
		Boolean status = false;		
		String msg = "";
		String glosa = "";

		try {			
				CaratulasUtil caratulasUtil = new CaratulasUtil();
				glosa = caratulasUtil.getGlosaDocumento(codAlpha);				
				status = true;			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("glosaDocumento", glosa);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}		

	@SuppressWarnings({ "unchecked" })
	public void buscarTransaccion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String idTransaccionReq = (String)request.getParameter("idTransaccion");

		JSONObject respuesta = new JSONObject();
		ArrayList<LiquidacionCaratulaDTO> resultado = new ArrayList<LiquidacionCaratulaDTO>();
		Boolean status = false;
		String msg = "";

		try {
			Long idTransaccion = new Long(idTransaccionReq);
			WsBotonDePagoClienteDelegate delegate = new WsBotonDePagoClienteDelegate();
			TransaccionWebVO tx = delegate.obtenerTransaccionPorId(idTransaccion);

			//TransaccionWebDTO
			TransaccionWebDTO transaccionWebDTO = new TransaccionWebDTO();
			transaccionWebDTO.setId(tx.getId());
			if(tx.getFechaTransaccion()!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String pagoFechaTransaccionS = sdf.format(tx.getFechaTransaccion());
				transaccionWebDTO.setFechaTransaccion(pagoFechaTransaccionS);
				transaccionWebDTO.setFechaTransaccionL(tx.getFechaTransaccion().getTime());
			}
			transaccionWebDTO.setMontoTransaccion(tx.getMontoTransaccion());
			transaccionWebDTO.setEstadoActualTransaccion(tx.getEstadoActualTransaccion());
			if(tx.getPagoFechaPago()!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String pagoFechaPagoS = sdf.format(tx.getPagoFechaPago());
				transaccionWebDTO.setPagoFechaPago(pagoFechaPagoS);
				transaccionWebDTO.setPagoFechaPagoL(tx.getPagoFechaPago().getTime());
			}
			transaccionWebDTO.setPagoNombreBanco(tx.getPagoNombreBanco());			

			ProductoVO[] ps = tx.getProductos();
			ArrayList<ProductoWebDTO> listaProductoWebDTOs = new ArrayList<ProductoWebDTO>();
			for(ProductoVO productoVO : ps){
				ProductoWebDTO productoWebDTO = new ProductoWebDTO();
				CaratulaVO[] caratulaVOs = productoVO.getListaCaratulasVO();
				//List<CaratulaVO> caratulaVOsList = Arrays.asList(caratulaVOs);
				if(caratulaVOs!=null && caratulaVOs.length>0){

					ArrayList<LiquidacionCaratulaDTO> listaLiquidacionCaratulaDTO = new ArrayList<LiquidacionCaratulaDTO>();
					for(CaratulaVO caratulaVO : caratulaVOs){
						CaratulasUtil caratulasUtil = new CaratulasUtil();
						LiquidacionCaratulaDTO liquidacionCaratulaDTO = caratulasUtil.getLiquidacionCaratulaDTO(caratulaVO, false, true);
						listaLiquidacionCaratulaDTO.add(liquidacionCaratulaDTO);
						//						CaratulaPortalVO caratulaPortalVO = caratulaPortalMaker(caratulaVO, conPapeles, true);	
						//						caratulaPortalVOs.add(caratulaPortalVO);
					}
					productoWebDTO.setLiquidacionCaratulaDTOs(listaLiquidacionCaratulaDTO);
					productoWebDTO.setDescripcionProducto(productoVO.getDescripcionProducto());
					productoWebDTO.setValorUnitario(productoVO.getValorUnitario());
					productoWebDTO.setCantidad(productoVO.getCantidad());
					productoWebDTO.setValorReal(productoVO.getValorReal());

					//txBoleta
					productoWebDTO.setApellidoPaternoBoleta(productoVO.getApellidoPaternoBoleta());
					productoWebDTO.setApellidoMaternoBoleta(productoVO.getApellidoMaternoBoleta());
					productoWebDTO.setNombreBoleta(productoVO.getNombreBoleta());
					productoWebDTO.setRutBoleta(productoVO.getRutBoleta());
					productoWebDTO.setDvBoleta(productoVO.getDvBoleta());

					//txMails
					ProductoReceptorEmailVO[] receptoresMail = productoVO.getListaProductoReceptorEmailVO();
					if(receptoresMail!=null && receptoresMail.length>0){
						ArrayList<ProductoReceptorEmailDTO> listaEmails = new ArrayList<ProductoReceptorEmailDTO>();
						for(ProductoReceptorEmailVO productoReceptorEmailVO : receptoresMail){
							ProductoReceptorEmailDTO emailDTO = new ProductoReceptorEmailDTO();
							emailDTO.setNombreCorto(productoReceptorEmailVO.getNombreCorto());
							emailDTO.seteMail(productoReceptorEmailVO.geteMail());
							listaEmails.add(emailDTO);
						}
						productoWebDTO.setProductoReceptorEmailDTOs(listaEmails);
					}

					if(productoVO.getListaProductoGlosaVO()!=null){
						ArrayList<String> glosa = new ArrayList<String>();
						ProductoGlosaVO[] glosaProd = productoVO.getListaProductoGlosaVO();

						if(glosaProd!=null && glosaProd.length>0){

							for(ProductoGlosaVO pg : glosaProd){
								glosa.add(pg.getGlosa());
							}			
						}
						productoWebDTO.setProductoGlosa(glosa);
					}
				}

				listaProductoWebDTOs.add(productoWebDTO);
			}
			transaccionWebDTO.setProductoWebDTOs(listaProductoWebDTOs);

			respuesta.put("transaccionDTO", transaccionWebDTO);

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("caratulas", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	@SuppressWarnings("unchecked")
	public void aprobarCaratula(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		respuesta.put("status", false);
		String msg = "";	

		String numeroCaratulaReq = request.getParameter("numeroCaratula");
		String canal = request.getParameter("canal");
		String totalReq = request.getParameter("total");
		String papelesReq = request.getParameter("papeles");
		String actualizarCierreReq = request.getParameter("actualizarCierre");
		String seccionDestino = request.getParameter("seccionDestino");
		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
		String observacionBitacora = INICIO_BITACORA_LIQUIDAR;

		WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();

		try {
			if(papelesReq!=null && !"".equals(papelesReq)){
				JSONParser parser = new JSONParser();
				JSONArray papelesJSONArray = (JSONArray)parser.parse(papelesReq);
				for(int i=0; i<papelesJSONArray.size(); i++){
					JSONObject papelJSON = (JSONObject)papelesJSONArray.get(i);
					Integer valorDocumento = papelJSON.get("valorDocumento")!=null?new Integer(papelJSON.get("valorDocumento").toString()):null;
					JSONObject tipoDocumentoJSON = (JSONObject)papelJSON.get("tipoDocumentoDTO");
					Long codTipoDocumento = tipoDocumentoJSON!=null?new Long(tipoDocumentoJSON.get("codigo").toString()):null;
					String descTipoDocumento = tipoDocumentoJSON.get("descripcion")!=null?tipoDocumentoJSON.get("descripcion").toString():"";
					if(codTipoDocumento!=null && codTipoDocumento.intValue()==-1 && valorDocumento!=null){
						observacionBitacora += " Se agrega item: " + descTipoDocumento + " por: $" + valorDocumento + ".";
					}
				}
			}

			Long ncaratula = Long.parseLong(numeroCaratulaReq);
			Long valorReal = new Long(totalReq);
			Boolean actualizarCierre = Boolean.parseBoolean(actualizarCierreReq);

			CaratulaVO caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), ncaratula);

			if(CANAL_CAJA.equals(canal)){

				Client client = Client.create();
				Gson gson = new Gson();
				String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
				String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");
				JSONArray caratulas = new JSONArray();

//				WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerCaratulasPendientesPorNumCaratula/"+ncaratula));
//				ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
				
				WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/eliminarCaratulaPendiente/"+ncaratula));
				ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).delete(ClientResponse.class);
				com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

				if(statusRespuesta.getStatusCode() == 200){
//					caratulas = (JSONArray) getResponse(clientResponse);
//					if(caratulas.size()>0){
//						msg = "Caratula se encuentra en espera de ser enviada a entrega documentos";
//					}
//					else{

						LiquidacionTemporalDTO liquidacionTemporalDTO = new LiquidacionTemporalDTO();
						LiquidacionTemporalIdDTO liquidacionTemporalIdDTO = new LiquidacionTemporalIdDTO();
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
						Date fechaHoy = new Date();	

						wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/liquidacionTemporal"));
						KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
						String usuario =context.getIdToken().getPreferredUsername();			
						usuario = usuario.replaceAll("CBRS\\\\", "");

						liquidacionTemporalIdDTO.setCaratula(ncaratula.intValue());

						liquidacionTemporalIdDTO.setCodSeccion(seccionDestino);

						liquidacionTemporalIdDTO.setFechaMov(df.format(fechaHoy));
						liquidacionTemporalIdDTO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
						liquidacionTemporalIdDTO.setRutEnvia(rutUsuario);
						liquidacionTemporalIdDTO.setRutFuncionario(rutUsuario);

						if(null!=caratulaVO.getCodigo() && caratulaVO.getCodigo()!=0)
							liquidacionTemporalIdDTO.setEsCtaCte(1);
						else				
							liquidacionTemporalIdDTO.setEsCtaCte(0);

						liquidacionTemporalIdDTO.setUsuarioLiquidador(usuario);

						liquidacionTemporalDTO.setId(liquidacionTemporalIdDTO);

						String jsonInString = gson.toJson(liquidacionTemporalDTO);
						//						System.out.println(jsonInString);
						clientResponse = wr.type("application/json").post(ClientResponse.class, jsonInString);

						JSONObject obj = (JSONObject) RestUtil.getResponse(clientResponse);

						if(valorReal!=null){ 
							if(!valorReal.equals(caratulaVO.getValorReal()) ){
								observacionBitacora += " Se actualiza valor real de $" + caratulaVO.getValorReal() + " a $" + valorReal + ". ";
								caratulaVO.setValorReal(valorReal);
								wsCaratulaClienteDelegate.actualizarCaratula(caratulaVO);												
	
								//Actualizar cierre cta corriente si corresponde
								if(actualizarCierre){
									logger.debug("Actualizar cierre");
									observacionBitacora += " Se actualiza valor real en cierre cta corriente a $" + valorReal + ".";
									WsCuentaCorrienteClienteDelegate ctaCteDelegate = new WsCuentaCorrienteClienteDelegate();
									ctaCteDelegate.modificaValorCtaCte(null, valorReal, ncaratula, null);
								}		
							} else{
								observacionBitacora += " Monto aprobado: $" + valorReal + ". ";
							}
						}

						//Agregar bitacora
						try{
							CaratulasUtil caratulasUtil = new CaratulasUtil();
							caratulasUtil.agregarBitacoraCaratula(ncaratula, rutUsuario, observacionBitacora, BitacoraCaratulaVO.OBSERVACION_INTERNA);
						} catch (Exception e) {
							logger.error("Error: " + e.getMessage(), e);
							respuesta.put("warn", true);
							msg = "Advertencia, usuario no puede agregar bitacora. Avisar a soporte.";
						}		

						respuesta.put("status", true);
//					}
				}else{//ERROR DE SERVICIO
					msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
				}//FIN ERROR DE SERVICIO

			}else{//SI ES CANAL WEB

				EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
				FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
				SeccionVO seccionVO = new SeccionVO();
				FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();

				funcionarioEnviaVO.setRutFuncionario(rutUsuario);
				estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

				if(caratulaVO.getCodigo()!=null && caratulaVO.getCodigo().intValue()!=0)
					funcionarioResponsableVO.setRutFuncionario("99999999");//Escrituras Web (ctas ctes)			Solicitado por RBennett
				else				
					funcionarioResponsableVO.setRutFuncionario("180906703");//Belen Poblete 					Solicitado por RBennett
				estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	

				seccionVO.setCodigo("08"); //entrega de docs
				estadoCaratulaVO.setSeccion(seccionVO);
				estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));

				estadoCaratulaVO.setFechaMovimiento(new Date());

				wsCaratulaClienteDelegate.moverCaratulaSeccion(ncaratula, estadoCaratulaVO);

				if(valorReal!=null && !valorReal.equals(caratulaVO.getValorReal()) ){
					observacionBitacora += " Se actualiza valor real de $" + caratulaVO.getValorReal() + " a $" + valorReal + ". ";
					caratulaVO.setValorReal(valorReal);
					wsCaratulaClienteDelegate.actualizarCaratula(caratulaVO);												

					//Actualizar cierre cta corriente si corresponde
					if(actualizarCierre){
						logger.debug("Actualizar cierre");
						observacionBitacora += " Se actualiza valor real en cierre cta corriente a $" + valorReal + ".";
						WsCuentaCorrienteClienteDelegate ctaCteDelegate = new WsCuentaCorrienteClienteDelegate();
						ctaCteDelegate.modificaValorCtaCte(null, valorReal, ncaratula, null);
					}						
				}

				//Agregar bitacora
				try{
					CaratulasUtil caratulasUtil = new CaratulasUtil();
					caratulasUtil.agregarBitacoraCaratula(ncaratula, rutUsuario, observacionBitacora, BitacoraCaratulaVO.OBSERVACION_INTERNA);					
				} catch (Exception e) {
					logger.error("Error: " + e.getMessage(), e);
					respuesta.put("warn", true);
					msg = "Advertencia, usuario no puede agregar bitacora. Avisar a soporte.";
				}		

				respuesta.put("status", true);
			}//FIN SI ES CANAL WEB


		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";			
		}		

		try {
			respuesta.put("msg", msg);
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}		

	//	@SuppressWarnings({ "unchecked" })
	//	public void buscarDocumento(ActionMapping mapping, ActionForm form,
	//			HttpServletRequest request, HttpServletResponse response) {
	//		response.setContentType("text/json");
	//
	//		String idNotario = (String)request.getParameter("idNotario");
	//		String codDocumento = (String)request.getParameter("codDocumento");
	//		String origen = (String)request.getParameter("origen");
	//		String numeroCaratula = (String)request.getParameter("numeroCaratula");
	//		
	//		JSONObject respuesta = new JSONObject();
	//		Boolean status = false;
	//		String msg = "";
	//
	//		try {
	//
	//			ObtenerExtractoElectronicoRequest obtenerExtractoElectronicoRequest = new ObtenerExtractoElectronicoRequest();
	//			ObtenerExtractoElectronicoResponse obtenerExtractoElectronicoResponse = new ObtenerExtractoElectronicoResponse();
	//
	////			obtenerExtractoElectronicoRequest.setCaratula(10000000L);
	////			obtenerExtractoElectronicoRequest.setIdNotario(102L);
	////			obtenerExtractoElectronicoRequest.setCodigoEmpresa("FOJAS");
	////			obtenerExtractoElectronicoRequest.setCodigoExtracto("123456819053");
	////			obtenerExtractoElectronicoRequest.setIdRequest("1");
	//			Long caratula = Long.parseLong(numeroCaratula);
	//			obtenerExtractoElectronicoRequest.setCaratula(caratula);
	//			obtenerExtractoElectronicoRequest.setIdNotario(Long.parseLong(idNotario));
	//			if("2".equals(origen))
	//				obtenerExtractoElectronicoRequest.setCodigoEmpresa("SIRI");
	//			else
	//				obtenerExtractoElectronicoRequest.setCodigoEmpresa("FOJAS");
	//			
	//			obtenerExtractoElectronicoRequest.setCodigoExtracto(codDocumento.trim());
	//			obtenerExtractoElectronicoRequest.setIdRequest("1");
	//
	//			try{
	//			ServiciosComercioDelegate delegate = new ServiciosComercioDelegate();
	//			System.out.println(caratula+" "+idNotario+" "+obtenerExtractoElectronicoRequest.getCodigoEmpresa()+" "+codDocumento.trim()+" "+obtenerExtractoElectronicoRequest.getIdRequest());
	//			obtenerExtractoElectronicoResponse = delegate.obtenerExtractoElectronico(obtenerExtractoElectronicoRequest);
	//			
	//			cl.cbrs.servicios.response.EstadoTransaccion estadoTransaccion = obtenerExtractoElectronicoResponse.getEstadoTransaccion();
	//			if("OK".equals(estadoTransaccion.getEstado())){
	//				byte[] escritura = obtenerExtractoElectronicoResponse.getExtractoElectronico();
	//				
	//				String path = TablaValores.getValor(TABLA_PARAMETROS,"PATH_ESCRITURA", "valor");
	//				FileOutputStream fos = new FileOutputStream(path+"C_"+numeroCaratula+".pdf");
	//				
	//				fos.write(escritura);
	//				fos.close();
	//				
	//				WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
	//				CaratulaVO caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(),caratula);
	//				caratulaVO.setEstadoDescargaEscri(1);
	//				wsCaratulaClienteDelegate.actualizarCaratula(caratulaVO);
	//				
	//				status = true;
	//			}else{
	//				msg=estadoTransaccion.getMensaje();
	//			}
	//			
	//			}catch(Exception e){
	//
	//			}
	//			
	//			
	//
	//		} catch (Exception e) {
	//			logger.error(e);
	//
	//			status = false;
	//			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
	//		}
	//
	//		respuesta.put("status", status);
	//		respuesta.put("msg", msg);
	//
	//		try {
	//			respuesta.writeJSONString(response.getWriter());
	//		} catch (IOException e) {
	//			logger.error(e);
	//		}
	//	}

	@SuppressWarnings({ "unchecked" })
	public void buscarDocumento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String idNotario = (String)request.getParameter("idNotario");
		String codDocumento = (String)request.getParameter("codDocumento");
		String numeroCaratula = (String)request.getParameter("numeroCaratula");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try {			
			NotarioUtil util = new NotarioUtil();
			InputStream archivo = util.descargaDocumento(idNotario, codDocumento);

			if(archivo!=null){
				File folderOcr = new File(UPLOAD_DIRECTORY_OCR+File.separator +"OCR_"+numeroCaratula);

				if (!folderOcr.exists()) {
					folderOcr.mkdir();
				}

				File fileVersiona = new File(UPLOAD_DIRECTORY+File.separator +"C_"+numeroCaratula+".pdf");
				File fileOcr = new File(folderOcr.toString() + File.separator +"OCR_"+numeroCaratula+".pdf");

				FileOutputStream fop = new FileOutputStream(fileVersiona);
				FileOutputStream fopOcr = new FileOutputStream(fileOcr);

				IOUtils.copy(archivo,fop);
				fop.close();

				IOUtils.copy(archivo,fopOcr);
				fopOcr.close();

				archivo.close();	
				status = true;
			} else{
				status=false;
				msg = "No se encontró documento en repositorio de Notaría";
			}

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void buscarDocumentoFusion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String idNotario = (String)request.getParameter("idNotario");
		String codDocumento = (String)request.getParameter("codDocumento");
		String numeroCaratulaS = (String)request.getParameter("numeroCaratula");
		String version = (String)request.getParameter("version");

		Long numeroCaratula = Long.parseLong(numeroCaratulaS);
		CaratulasUtil caratulasUtil=new CaratulasUtil();

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try {
			NotarioUtil util = new NotarioUtil();
			InputStream inputStream = util.descargaDocumento(idNotario, codDocumento);
			
			if(inputStream!=null){
			
				DocumentosCliente docDelegate = new DocumentosCliente();
				DocumentoDAO dm = new DocumentoDAO();
				DocumentoDTO documentoDTO = new DocumentoDTO();
	
				documentoDTO = dm.getDocumento(numeroCaratula, Integer.parseInt(version), ESCRITURA);
	
				if(documentoDTO==null){
					msg = "Caratula no encontrada en documentos";
					status=false;
				}else{
	
					byte[] archivo = docDelegate.downloadEscritura(false, documentoDTO.getIdDocumento(), 0L, 0L, documentoDTO.getVersion().intValue(), false, false, true);
	
					// write byte array to the output stream
					List<InputStream> lista = new ArrayList<InputStream>();
					List<InputStream> lista2 = new ArrayList<InputStream>();
					//archivo 1
					lista.add(new ByteArrayInputStream(archivo));
					lista2.add(new ByteArrayInputStream(archivo));
	
					//archivo 2							
					lista.add(inputStream);
					lista2.add(inputStream);
	
					File folderOcr = new File(UPLOAD_DIRECTORY_OCR+File.separator +"OCR_"+numeroCaratula);
	
					if (!folderOcr.exists()) {
						folderOcr.mkdir();
					}
	
					File fileVersiona = new File(UPLOAD_DIRECTORY+File.separator +"C_"+numeroCaratula+".pdf");
					File fileOcr = new File(folderOcr.toString() + File.separator +"OCR_"+numeroCaratula+".pdf");
	
					FileOutputStream fop = new FileOutputStream(fileVersiona);
					FileOutputStream fopOcr = new FileOutputStream(fileOcr);
	
					this.merge(lista, fop);
					fop.close();
	
					this.merge(lista2, fopOcr);
					fopOcr.close();
	
					status = true;
				}
	
				//Reingresar Caratula
				CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();
	
				WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
	
				try {		
					CaratulaVO caratulaVO = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numeroCaratula);
	
					if(caratulaVO!=null){	
						//Reviso si la caratula esta rechazada
						if(SECCIONES_RECHAZOS.contains(caratulaVO.getEstadoActualCaratula().getSeccion().getCodigo())){
							CaratulaEstadoUtil cu = new CaratulaEstadoUtil();
							String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
							caratulaEstadoDTO = cu.getCaratulaEstadoDTO(caratulaVO);
							List<MovimientoDTO> movimientos = caratulaEstadoDTO.getMovimientoDTOs();
	
							for (int i=movimientos.size()-1; i >= 0; i--){
								MovimientoDTO movimientoDto = (MovimientoDTO)movimientos.get(i);
								//							System.out.println(movimientoDto.getSeccionDTO().getDescripcion());
								if(SECCIONES_RECHAZOS.contains(movimientoDto.getSeccionDTO().getId())){
									//								System.out.println("esta rechazada");
									movimientoDto = (MovimientoDTO)movimientos.get(i-1);
									//								System.out.println("la siguiente seria :" + movimientoDto.getSeccionDTO().getDescripcion());
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
	
										caratulasUtil.moverCaratulaSeccion(numeroCaratula, estadoCaratulaFlujoVO );
										break;
									}
								}
							}
						} 	
					}
	
				} catch (Exception e) {
					status = false;
					msg = "Se ha detectado un problema en reingreso de caratula";
				}
				//Fin Reingresar Caratula
			}
			

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
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
	public void anularDocumento(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
			HttpServletResponse response) {

		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
		JSONObject respuesta = new JSONObject();
		respuesta.put("status", true);
		String msg = "";	
		ClienteWsFirmadorDelegate clienteWsFirmadorDelegate = new ClienteWsFirmadorDelegate();
		String codArchivoAlpha = request.getParameter("codArchivoAlpha");
		String descripcion = request.getParameter("descripcion");
		String caratula = request.getParameter("caratula");

		try{
			clienteWsFirmadorDelegate.anularDocumento(codArchivoAlpha);

			//Agregar bitacora
			try{
				CaratulasUtil caratulasUtil = new CaratulasUtil();
				String bitacora = "Liquidacion de caratula: Se elimina documento electronico " + descripcion + ", codigo " + codArchivoAlpha + " en modulo liquidacion";
				caratulasUtil.agregarBitacoraCaratula(new Long(caratula), rutUsuario, bitacora, BitacoraCaratulaVO.OBSERVACION_INTERNA);					
			} catch (Exception e) {
				logger.error("Error: " + e.getMessage(), e);
				respuesta.put("warn", true);
				msg = "Advertencia, usuario no puede agregar bitacora. Avisar a soporte.";
			}
		} catch (Exception e1) {
			respuesta.put("status", false);
			logger.error(e1.getMessage(), e1);
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";			
		}		

		try {
			respuesta.put("msg", msg);
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void agregarDocumentoLiquidacion(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONObject respuestaDocumentoLiquidacion = null;
		respuesta.put("status", false);
		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
		String msg = "";	
		String documentoLiquidacion = request.getParameter("documento");

		try{
			String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
			String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");
			
			Client client = Client.create();
			WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/agregarDocumentoLiquidacion"));
			ClientResponse clientResponse = wr.type("application/json").post(ClientResponse.class, documentoLiquidacion);
			if(clientResponse.getClientResponseStatus().getStatusCode()==200){
				respuesta.put("status", true);
				respuestaDocumentoLiquidacion = (JSONObject) RestUtil.getResponse(clientResponse);
				
				//Agregar bitacora
				try{
					JSONObject docJson = (JSONObject)new JSONParser().parse(documentoLiquidacion);
					Long caratula = (Long)docJson.get("caratula");
					String nombre = (String)docJson.get("nombre");
					Long valor = (Long)docJson.get("valor");
					CaratulasUtil caratulasUtil = new CaratulasUtil();
					caratulasUtil.agregarBitacoraCaratula(caratula, rutUsuario, "Liquidacion de caratula: Se agrego documento " + nombre + ", valor " + valor, BitacoraCaratulaVO.OBSERVACION_INTERNA);					
				} catch (Exception e) {
					logger.error("Error: " + e.getMessage(), e);
					respuesta.put("warn", true);
					msg = "Advertencia, usuario no puede agregar bitacora. Avisar a soporte.";
				}
				

			} else{
				msg = "Error al agregar documento.";
				logger.error("Error al agregar documento: (" + clientResponse.getClientResponseStatus().getStatusCode() + ") " + clientResponse.getClientResponseStatus().getReasonPhrase());
			}
		} catch (Exception e1) {
			respuesta.put("status", false);
			logger.error(e1.getMessage(), e1);
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";			
		}		

		try {
			respuesta.put("documentoLiquidacion", respuestaDocumentoLiquidacion);
			respuesta.put("msg", msg);
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}	
	
	@SuppressWarnings("unchecked")
	public void eliminarDocumentoLiquidacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String documentoP = request.getParameter("documento");
		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		if(!StringUtils.isBlank(documentoP)){
			try {
					JSONObject docJson = (JSONObject)new JSONParser().parse(documentoP);
					Long idDocumento = (Long)docJson.get("id");
					Long caratula = (Long)docJson.get("caratula");
					String nombre = (String)docJson.get("nombre");
					Long valor = (Long)docJson.get("valor");
					
					//Eliminar de tabla temporal
					Client client = Client.create();
					String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
					String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");
					
					String url = "http://"+ip+":"+port+"/CaratulaRest/caratula/eliminarDocumentoLiquidacion/"+idDocumento;
					
					WebResource wr = client.resource(new URI(url));
																                    
					ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).delete(ClientResponse.class);
					com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();
					
					if(statusRespuesta.getStatusCode() == 200){
						status=true;
						
						//Agregar bitacora
						try{
							CaratulasUtil caratulasUtil = new CaratulasUtil();
							caratulasUtil.agregarBitacoraCaratula(caratula, rutUsuario, "Liquidacion de caratula: Se elimino documento " + nombre + ", valor " + valor, BitacoraCaratulaVO.OBSERVACION_INTERNA);					
						} catch (Exception e) {
							logger.error("Error: " + e.getMessage(), e);
							respuesta.put("warn", true);
							msg = "Advertencia, usuario no puede agregar bitacora. Avisar a soporte.";
						}
						

					} else{
						msg = "Error al eliminar documento.";
						logger.error("Error al eliminar documento: (" + clientResponse.getClientResponseStatus().getStatusCode() + ") " + clientResponse.getClientResponseStatus().getReasonPhrase());
					}


				

			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
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

	public static void merge(List<InputStream> streamOfPDFFiles, OutputStream outputStream) throws Exception,
	DocumentException {

		Rectangle pageSize1 = new Rectangle(0, 0, 660, 990);

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
			System.out.println(ErroresUtil.extraeStackTrace(e));
			throw e;
		}
		finally {
			if (document.isOpen()) {
				document.close();
				try {
					System.out.println("cerrando los archivos");
					for (Iterator iterator = streamOfPDFFiles.iterator(); iterator.hasNext();) {
						InputStream inputStream = (InputStream) iterator.next();
						try {
							inputStream.close();
						}
						catch (Exception e) {
							System.out.println("no se pudo cerrar");
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