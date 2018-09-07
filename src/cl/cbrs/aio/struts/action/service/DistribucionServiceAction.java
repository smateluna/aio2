package cl.cbrs.aio.struts.action.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cl.cbrs.aio.dto.estado.CaratulaEstadoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulaEstadoUtil;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.parametros.vo.FuncionarioVO;
import cl.cbrs.parametros.vo.SeccionVO;
import cl.cbrs.parametros.ws.ServicioParametrosDelegate;
import cl.cbrs.parametros.ws.request.ObtenerFuncionariosSeccionRequest;
import cl.cbrs.parametros.ws.request.ObtenerSeccionesRequest;
import cl.cbrs.parametros.ws.response.ObtenerFuncionariosSeccionResponse;
import cl.cbrs.parametros.ws.response.ObtenerSeccionesResponse;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class DistribucionServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(DistribucionServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void getSecciones(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONArray secciones = new JSONArray();

		ServicioParametrosDelegate parametrosDelegate = new ServicioParametrosDelegate();

		try {					
			ObtenerSeccionesRequest seccionesRequest = new ObtenerSeccionesRequest();

			ObtenerSeccionesResponse seccionesResponse = parametrosDelegate.obtenerSecciones(seccionesRequest);

			SeccionVO[] seccionVOs = seccionesResponse.getSecciones();				

			if(seccionVOs!=null){		
				for(SeccionVO seccionVO : seccionVOs){
					JSONObject seccion = new JSONObject();

					seccion.put("codigo", seccionVO.getCodigo());
					seccion.put("descripcion", seccionVO.getDescripcion());
					seccion.put("visible", seccionVO.getVisible());

					secciones.add(seccion);
				}			
			}

			json.put("status", true);

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor obteniendo carátula.");
			logger.error("Error: "+e.getMessage(),e);
		}

		json.put("secciones", secciones);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}		
	}

	@SuppressWarnings({ "unchecked" })
	public void getResponsables(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String codSeccion = request.getParameter("codSeccion");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONArray funcionarios = new JSONArray();

		ServicioParametrosDelegate parametrosDelegate = new ServicioParametrosDelegate();

		try {		
			ObtenerFuncionariosSeccionRequest funcionariosSeccionRequest = new ObtenerFuncionariosSeccionRequest();

			funcionariosSeccionRequest.setCodSeccion(codSeccion);
			funcionariosSeccionRequest.setSoloResponsables(true);

			ObtenerFuncionariosSeccionResponse funcionariosSeccionResponse = parametrosDelegate.obtenerFuncionariosSeccion(funcionariosSeccionRequest);

			FuncionarioVO[] funcionarioVOs = funcionariosSeccionResponse.getFuncionarios();

			if(funcionarioVOs!=null){

				for(FuncionarioVO vo: funcionarioVOs){
					JSONObject obj = new JSONObject();

					obj.put("apePaterno", vo.getApePaterno());
					obj.put("apeMaterno", vo.getApeMaterno());
					obj.put("nombre", vo.getNombre());
					obj.put("rut", vo.getRut());

					funcionarios.add(obj);
				}

			}

			json.put("status", true);

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor obteniendo carátula.");
			logger.error("Error: "+e.getMessage(),e);
		}

		json.put("funcionarios", funcionarios);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}		
	}

	@SuppressWarnings({ "unchecked" })
	public void getCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String numeroCaratula = request.getParameter("numeroCaratula");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("numeroCaratula", numeroCaratula);

		json.put("req", req);	

		JSONObject res = new JSONObject();

		CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();

		if(StringUtils.isNotBlank(numeroCaratula) && StringUtils.isNumeric(numeroCaratula)){

			Long numero = Long.parseLong(numeroCaratula);

			WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();

			try {		
				CaratulaVO caratula = delegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), numero);

				if(caratula!=null){

					CaratulaEstadoUtil cu = new CaratulaEstadoUtil();

					caratulaEstadoDTO = cu.getCaratulaEstadoDTO(caratula);

					res.put("caratulaDTO", caratulaEstadoDTO);

					json.put("status", true);
					
					request.getSession().setAttribute("numeroCaratula", numero);
				}else{
					json.put("msg", "Carátula no encontrada.");					
				}

			} catch (Exception e) {
				json.put("msg", "Problemas en servidor obteniendo carátula.");
				logger.error("Error: "+e.getMessage(),e);
			}
		}else{
			json.put("msg", "Número carátula no válido.");
		}


		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}		
	}	



	@SuppressWarnings({ "unchecked" })
	public void distribuir(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		StringBuilder buffer = new StringBuilder();
		BufferedReader reader;
		try {
			reader = request.getReader();

			String line;
			while((line = reader.readLine()) != null){
				buffer.append(line);
			}

		} catch (IOException e1) {
			logger.error(e1.getMessage(),e1);
		}



		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();

		try {	
			String body = buffer.toString();

			System.out.println(body);

			Object bodyObj = JSONValue.parse(body);

			JSONObject bodyJSON = (JSONObject)bodyObj;

			JSONObject responsableJSON = (JSONObject) bodyJSON.get("responsable");
			JSONArray caratulasJSON = (JSONArray) bodyJSON.get("caratulas");
			
			String codSeccion = (String)bodyJSON.get("codSeccion");
			String rutEnviaPor = (String)request.getSession().getAttribute("rutUsuario");
			String rutFuncionarioResponsable = (String)responsableJSON.get("rut");
			
			EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

			cl.cbrs.caratula.flujo.vo.SeccionVO seccion = new cl.cbrs.caratula.flujo.vo.SeccionVO();
			seccion.setCodigo(codSeccion);
			
			estadoCaratulaVO.setSeccion(seccion);
			estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			cl.cbrs.caratula.flujo.vo.FuncionarioVO responsable = new cl.cbrs.caratula.flujo.vo.FuncionarioVO();
			responsable.setRutFuncionario(rutFuncionarioResponsable);
			
			estadoCaratulaVO.setResponsable(responsable);

			
			cl.cbrs.caratula.flujo.vo.FuncionarioVO enviadoPor = new cl.cbrs.caratula.flujo.vo.FuncionarioVO();
			enviadoPor.setRutFuncionario(rutEnviaPor);
			
			estadoCaratulaVO.setEnviadoPor(enviadoPor);

			for(int i = 0; i<caratulasJSON.size(); i++){
				JSONObject jobj = (JSONObject)caratulasJSON.get(i);

				Long numero = (Long)jobj.get("numero");
				
				estadoCaratulaVO.setFechaMovimiento(new Date());
				
				caratulaClienteDelegate.moverCaratulaSeccion(numero, estadoCaratulaVO);
			}

			json.put("status", true);

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor obteniendo carátula.");
			logger.error("Error: "+e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
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


}