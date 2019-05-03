package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.keycloak.KeycloakSecurityContext;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.RestUtil;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.servicios.response.EstadoTransaccion;
import cl.cbrs.wsusuarios.ServiciosUsuariosDelegate;
import cl.cbrs.wsusuarios.ws.request.CambiarEstadoUsuarioPerfilRequest;
import cl.cbrs.wsusuarios.ws.request.DistribuirCertPropRequest;
import cl.cbrs.wsusuarios.ws.request.ObtenerUsuariosPerfilRequest;
import cl.cbrs.wsusuarios.ws.response.CambiarEstadoUsuarioPerfilResponse;
import cl.cbrs.wsusuarios.ws.response.DistribuirCertPropResponse;
import cl.cbrs.wsusuarios.ws.response.ObtenerUsuariosPerfilResponse;
import cl.cbrs.wsusuarios.ws.vo.UsuarioPerfilVO;

public class MantenedorServiceAction extends CbrsAbstractAction {
	
	private static final String TABLA_PARAMETROS = "ws_usuarios.parametros";

	private static final Logger logger = Logger.getLogger(MantenedorServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerUsuarios(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {		
		
		JSONObject respuesta = new JSONObject();
		JSONArray usuarios = new JSONArray();
		Boolean status = false;
		String msg = "";
		UsuarioPerfilVO[] listaUsuariosVOs = null;
		
		try{
			//cbrlogin - Certificador
			ServiciosUsuariosDelegate delegate = new ServiciosUsuariosDelegate();
			ObtenerUsuariosPerfilRequest requestUsuario = new ObtenerUsuariosPerfilRequest();
			requestUsuario.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			requestUsuario.setNombrePerfil("CERTIFICADOR");

			ObtenerUsuariosPerfilResponse  usuarioResponse = delegate.obtenerUsuariosPerfil(requestUsuario);

			listaUsuariosVOs = usuarioResponse.getUsuarioPerfil();
			if(listaUsuariosVOs!=null){ 
				if(listaUsuariosVOs.length>0){
					for(int i=0;i<listaUsuariosVOs.length;i++){
						UsuarioPerfilVO usuarioPerfilVO = listaUsuariosVOs[i];
						JSONObject fila = new JSONObject();
						fila.put("usuario", usuarioPerfilVO.getUsuario());
						fila.put("estado", usuarioPerfilVO.isActivo());
						fila.put("perfil", usuarioPerfilVO.getPerfil().trim());
						fila.put("nombreUsuario", usuarioPerfilVO.getNombreUsuario());
						fila.put("cantidadcaratulas", usuarioPerfilVO.getCantidadCaratulas());
						fila.put("cantidadasignada", usuarioPerfilVO.getCantidadAsignadas());
						usuarios.add(fila);
					}
				}	
			}	
			
			//cbrlogin - Certificador Hipotecas
			requestUsuario.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			requestUsuario.setNombrePerfil("CERTIFICADOR HIPOTECAS");

			usuarioResponse = delegate.obtenerUsuariosPerfil(requestUsuario);

			listaUsuariosVOs = usuarioResponse.getUsuarioPerfil();
			if(listaUsuariosVOs!=null){ 
				if(listaUsuariosVOs.length>0){
					for(int i=0;i<listaUsuariosVOs.length;i++){
						UsuarioPerfilVO usuarioPerfilVO = listaUsuariosVOs[i];
						JSONObject fila = new JSONObject();
						fila.put("usuario", usuarioPerfilVO.getUsuario());
						fila.put("estado", usuarioPerfilVO.isActivo());
						fila.put("perfil", usuarioPerfilVO.getPerfil());
						fila.put("nombreUsuario", usuarioPerfilVO.getNombreUsuario());
						fila.put("cantidadcaratulas", usuarioPerfilVO.getCantidadCaratulas());
						fila.put("cantidadasignada", usuarioPerfilVO.getCantidadAsignadas());
						usuarios.add(fila);
					}
				}	
			}
			
			//cbrlogin - Certificador Prohibiciones
			requestUsuario.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			requestUsuario.setNombrePerfil("CERTIFICADOR PROHIBICIONES");

			usuarioResponse = delegate.obtenerUsuariosPerfil(requestUsuario);

			listaUsuariosVOs = usuarioResponse.getUsuarioPerfil();
			if(listaUsuariosVOs!=null){ 
				if(listaUsuariosVOs.length>0){
					for(int i=0;i<listaUsuariosVOs.length;i++){
						UsuarioPerfilVO usuarioPerfilVO = listaUsuariosVOs[i];
						JSONObject fila = new JSONObject();
						fila.put("usuario", usuarioPerfilVO.getUsuario());
						fila.put("estado", usuarioPerfilVO.isActivo());
						fila.put("perfil", usuarioPerfilVO.getPerfil());
						fila.put("nombreUsuario", usuarioPerfilVO.getNombreUsuario());
						fila.put("cantidadcaratulas", usuarioPerfilVO.getCantidadCaratulas());
						fila.put("cantidadasignada", usuarioPerfilVO.getCantidadAsignadas());
						usuarios.add(fila);
					}
				}	
			}			
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("usuarios", usuarios);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void cambiaEstadoUsuario(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String nombreUsuario = request.getParameter("nombreUsuario");
		Boolean estado = new Boolean(request.getParameter("estado"));
		String perfil = request.getParameter("perfil");
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		EstadoTransaccion estadoTransaccion = null;
		
		try{
			//cbrlogin
			ServiciosUsuariosDelegate delegate = new ServiciosUsuariosDelegate();
			CambiarEstadoUsuarioPerfilRequest requestUsuario = new CambiarEstadoUsuarioPerfilRequest();
			requestUsuario.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			requestUsuario.setPerfil(perfil);
			requestUsuario.setUser(nombreUsuario);
			requestUsuario.setEstado(estado);
			
			CambiarEstadoUsuarioPerfilResponse usuarioResponse = delegate.cambiarEstadoUsuarioPerfil(requestUsuario);

			estadoTransaccion = usuarioResponse.getEstadoTransaccion();
			
			if(estadoTransaccion.getEstado().equals("OK"))
				status = true;
			else
				status = false;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
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
	
	@SuppressWarnings("unchecked")
	public void redistribuirCaratulas(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String nombreUsuario = request.getParameter("nombreUsuario");
		String perfil = request.getParameter("perfil");
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		List<CaratulaVO> listaCaratulas = new ArrayList<CaratulaVO>();
		
		try{
			nombreUsuario = nombreUsuario.trim();
			perfil = perfil.trim();
			String seccion = "";
			
			if(perfil.equalsIgnoreCase("CERTIFICADOR"))
				seccion="C3";
			else if(perfil.equalsIgnoreCase("CERTIFICADOR HIPOTECAS"))
				seccion="CH";
			else if(perfil.equalsIgnoreCase("CERTIFICADOR PROHIBICIONES"))
				seccion="P1";
			
			//cbrlogin
			ServiciosUsuariosDelegate delegateUsuario = new ServiciosUsuariosDelegate();
			
			WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
			listaCaratulas = delegate.obtenerCaratulasPorSeccionUsuario(seccion,nombreUsuario.trim());
			if(null != listaCaratulas){
				for(CaratulaVO caratulaVO:listaCaratulas){
					String rutUsuarioAsignado = null;
					if(perfil.equalsIgnoreCase("CERTIFICADOR")){
						DistribuirCertPropRequest distribuirCertPropRequest = new DistribuirCertPropRequest ();
						distribuirCertPropRequest.setCaratulaVO(caratulaVO);
						DistribuirCertPropResponse distribuirCertPropResponse = delegateUsuario.distribuirCertProp(distribuirCertPropRequest);
						rutUsuarioAsignado = distribuirCertPropResponse.getRut();
					} else if(perfil.equalsIgnoreCase("CERTIFICADOR HIPOTECAS")){
						rutUsuarioAsignado = distribuirCertHipo();
					} else if(perfil.equalsIgnoreCase("CERTIFICADOR PROHIBICIONES")){
						rutUsuarioAsignado = distribuirCertProh();
					}
					
					
					if(rutUsuarioAsignado!=null){
						EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
						
						FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
						funcionarioEnviaVO.setRutFuncionario("00000105");
						estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);
		
						FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
						funcionarioResponsableVO.setRutFuncionario(rutUsuarioAsignado);
						estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	
						
						SeccionVO seccionVO = new SeccionVO();
						seccionVO.setCodigo(seccion);
						
						estadoCaratulaVO.setSeccion(seccionVO);
						estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
		
						estadoCaratulaVO.setFechaMovimiento(new Date());
						
						delegate.moverCaratulaSeccion(caratulaVO.getNumeroCaratula(), estadoCaratulaVO);
					}
				}
			}
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
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
	
	private String distribuirCertHipo() throws Exception{
		
		Client client = Client.create();
		String ip = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS", "valor");
		String port = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS", "valor");
		
		WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/UsuariosRest/usuario/distribuirCertHipo"));

		ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
		com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

		String rut = null;
		if(statusRespuesta.getStatusCode() == 200){
			rut = RestUtil.getResponse(clientResponse).toString();
		}
		
		return rut;
	}	
	
	private String distribuirCertProh() throws Exception{
		
		Client client = Client.create();
		String ip = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS", "valor");
		String port = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS", "valor");
		
		WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/UsuariosRest/usuario/distribuirCertProh"));

		ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
		com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

		String rut = null;
		if(statusRespuesta.getStatusCode() == 200){
			rut = RestUtil.getResponse(clientResponse).toString();
		}
		
		return rut;
	}			
	
	@SuppressWarnings("unchecked")
	public void getCtasCtes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	

		JSONObject respuesta = new JSONObject();
		Boolean status = true;
		String msg = "";
	
		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			if(StringUtils.isBlank(usuario)){
				msg = "No hay usuario, inicie sesión nuevamente.";
				status = false;
			}else{
				FlujoDAO flujoDao = new FlujoDAO();
				ArrayList<CuentaCorrienteDTO> listaCtasCtes = flujoDao.getCtasCtes();
				
				respuesta.put("listaCtasCtes", listaCtasCtes);
			}

			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			status = false;
			msg = "Se ha detectado un problema en el servidor.";
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
	public void guardarCtaCte(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	

		JSONObject respuesta = new JSONObject();
		Boolean status = true;
		String msg = "";
		String ctaCteReq = request.getParameter("ctaCte");
		
		FlujoDAO flujoDAO = new FlujoDAO();
		Gson gson = new Gson();					
	
		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			if(StringUtils.isBlank(usuario)){
				msg = "No hay usuario, inicie sesión nuevamente.";
				status = false;
			}else{
				if(ctaCteReq!=null && !"".equals(ctaCteReq)){
					CuentaCorrienteDTO dto = gson.fromJson(ctaCteReq, CuentaCorrienteDTO.class);
					
					if(dto.getCodigo()!=null){
						flujoDAO.actualizarCtaCte(dto);
					}else{
						flujoDAO.ingresarCtaCte(dto);
					}
				} else{
					
				}
			}

			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			status = false;
			msg = "Se ha detectado un problema en el servidor.";
		}				

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}		

}