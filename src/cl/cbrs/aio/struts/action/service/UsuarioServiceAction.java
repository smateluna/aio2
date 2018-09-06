package cl.cbrs.aio.struts.action.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.keycloak.KeycloakSecurityContext;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.PermisoDTO;
import cl.cbrs.aio.dto.UsuarioAIODTO;
import cl.cbrs.aio.listener.SessionCounter;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.UsuarioUtil;
import cl.cbrs.wsusuarios.ws.vo.UsuarioPerfilVO;

public class UsuarioServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(UsuarioServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getUsuariosLogueados(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("estado", false);	

		try {		
			JSONArray listaUsuarios = new JSONArray();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY,-2);
			Date vencido = cal.getTime();
			
			if(SessionCounter.USUARIOS!=null && SessionCounter.USUARIOS.size()>0){
		        for( String key : SessionCounter.USUARIOS.keySet()){
		        	UsuarioAIODTO usuarioAIO = SessionCounter.USUARIOS.get(key);
		        	try{
		        		if(usuarioAIO.getHttpSession()!=null){
		        			Date fechaUltimoAcceso = new Date(usuarioAIO.getHttpSession().getLastAccessedTime());

		        			if(fechaUltimoAcceso.before(vencido)){
//		        				SessionCounter.USUARIOS.remove(key);
		        				continue;
		        			}
		        		}
		        		if(usuarioAIO.getFechaCreacionL()!=null){
			        		Date fechaUltimoAcceso = new Date(usuarioAIO.getFechaUltimoAccesoL());
		        			if(fechaUltimoAcceso.before(vencido)){
//		        				SessionCounter.USUARIOS.remove(key);
		        				continue;
		        			}
		        		}
		        		if(usuarioAIO.getPath()!=null && !"".equals(usuarioAIO.getPath()))
		        			listaUsuarios.add(usuarioAIO);
		        	} catch(IllegalStateException e){
		        		SessionCounter.USUARIOS.remove(key);
		        	}
		        }
		        
		        json.put("numeroSesiones", SessionCounter.USUARIOS.size());
			}
	        
	        
	        
			json.put("listaUsuarios", listaUsuarios);
			

			json.put("estado", true);
			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getUsuario: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar usuario");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void setSessionActiva(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("estado", false);	

		try {		
			String usuario = request.getRemoteUser()!=null?request.getRemoteUser():"";
			usuario = usuario.replaceAll("CBRS\\\\", "");
			UsuarioAIODTO usuarioAIO = new UsuarioAIODTO();
			usuarioAIO.setNombre(usuario);
			usuarioAIO.setPath(request.getParameter("path"));
			usuarioAIO.setUserAgent(request.getHeader("User-Agent"));
			usuarioAIO.setFechaUltimoAccesoL(request.getSession().getLastAccessedTime());
			usuarioAIO.setFechaCreacionL(request.getSession().getCreationTime());
			usuarioAIO.setIp(request.getRemoteAddr());
			usuarioAIO.setPerfil(request.getParameter("perfil"));

			SessionCounter.USUARIOS.put(usuario, usuarioAIO);		

			json.put("estado", true);
			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getUsuario: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar usuario");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	

	@SuppressWarnings({ "unchecked" })
	public void getUsuario(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		JSONObject json = new JSONObject();
		json.put("estado", false);	


		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuario = request.getRemoteUser()!=null?request.getRemoteUser():"";
		usuario =context.getIdToken().getPreferredUsername();
			
		System.out.println("Obteniendo datos de usuario..."+usuario);
			try {		
			
			json.put("nombreUsuario", usuario);
			 logger.debug("Obteniendo datos de usuario..."+usuario);
			//Propiedades AIO
			json.put("sistema", CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			json.put("anoArchivoNacional", new Integer(CacheAIO.CACHE_CONFIG_AIO.get("ANO_ARCHIVO_NACIONAL")));

			json.put("modulo", (String) request.getSession().getAttribute("modulo"));
			json.put("grupo", (String) request.getSession().getAttribute("grupo"));
			json.put("activoAtencion", (Boolean) request.getSession().getAttribute("activoAtencion"));
			json.put("userIpAddress", (String) request.getSession().getAttribute("userIpAddress"));
			json.put("ipSocket", (String) request.getSession().getAttribute("ipSocket"));
			json.put("puertoSocket", (String) request.getSession().getAttribute("puertoSocket"));
						
			json.put("estado", true);
			
			
			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getUsuario: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar usuario");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getPermisosUsuario(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		UsuarioUtil util = new UsuarioUtil();
		
		String perfil = request.getParameter("perfil");	
		
		json.put("status", false);	

		try {		
			
			String usuario = request.getRemoteUser()!=null?request.getRemoteUser():"";
			 logger.debug("Obteniendo datos de usuario..."+usuario);
			 System.out.println("Obteniendo datos de usuario..."+usuario);
			
//			if(usuario!=null && !"".equals(usuario)){
				usuario = usuario.replaceAll("CBRS\\\\", "");
				ArrayList<PermisoDTO> listaPermisos = (ArrayList<PermisoDTO>) request.getSession().getAttribute("permisosUsuario");
				UsuarioPerfilVO usuarioPerfilVO = new UsuarioPerfilVO();
				String rut = (String) request.getSession().getAttribute("rutUsuario");
				String nombreUsuario = (String) request.getSession().getAttribute("nombreUsuario");
				String modulo = (String) request.getSession().getAttribute("modulo");
				String grupo = (String) request.getSession().getAttribute("grupo");
				String userIpAddress = (String) request.getSession().getAttribute("userIpAddress");
				Boolean activoAtencion = (Boolean) request.getSession().getAttribute("activoAtencion");
				String ipSocket = (String) request.getSession().getAttribute("ipSocket");
				String puertoSocket = (String) request.getSession().getAttribute("puertoSocket");
				String idPerfil = (String) request.getSession().getAttribute("idPerfil");
								
				if(listaPermisos==null){
					listaPermisos = util.getPermisosUsuario(usuario, perfil, usuarioPerfilVO);	
					nombreUsuario = usuarioPerfilVO.getNombreUsuario();
					rut = usuarioPerfilVO.getRut();
					request.getSession().setAttribute("permisosUsuario",listaPermisos);
					request.getSession().setAttribute("rutUsuario", rut);
					request.getSession().setAttribute("nombreUsuario", nombreUsuario);	
					request.getSession().setAttribute("usuario", usuario);
					request.getSession().setAttribute("idPerfil",idPerfil);
					
					//Verifico si perfil aplica para atencion de modulos - RG
					String valor = TablaValores.getValor("aio.parametros", "perfiles_atencion_publico", perfil.toLowerCase());
					if(null!=valor && valor.equals("1")){
						userIpAddress = request.getRemoteAddr();
						//modulo;activo(boolean);grupo;
						activoAtencion = Boolean.parseBoolean(TablaValores.getValor("aio.parametros", userIpAddress, "activo"));
						if(null!=activoAtencion){
							request.getSession().setAttribute("activoAtencion", activoAtencion);
							if(activoAtencion){
								modulo = TablaValores.getValor("aio.parametros", userIpAddress, "modulo");
								grupo = TablaValores.getValor("aio.parametros", userIpAddress, "grupo");
								ipSocket = TablaValores.getValor("atencion_publico.parametros", "ip", "valor");
								puertoSocket = TablaValores.getValor("atencion_publico.parametros", "puerto", "valor");
								request.getSession().setAttribute("userIpAddress",userIpAddress);
								request.getSession().setAttribute("modulo",modulo);
								request.getSession().setAttribute("grupo",grupo);
								request.getSession().setAttribute("ipSocket",ipSocket);
								request.getSession().setAttribute("puertoSocket",puertoSocket);
							}
						}
					}
					//Fin - Verifico si perfil aplica para atencion de modulos - RG
				}

				json.put("ipSocket", ipSocket);
				json.put("puertoSocket", puertoSocket);
				json.put("activoAtencion", activoAtencion);
				json.put("modulo", modulo);
				json.put("grupo", grupo);
				json.put("userIpAddress", userIpAddress);
				json.put("idPerfil", idPerfil);
								
				json.put("permisosUsuario", listaPermisos);
				json.put("status", true);

				
//			} 			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getPermisosUsuario: " + e.getMessage(), e);
			json.put("msg", "Este usuario no tiene modulos configurados para perfil " + perfil);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void getPerfilesUsuario(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		UsuarioUtil util = new UsuarioUtil();
		
		json.put("status", false);	

		try {		
			String usuario = request.getRemoteUser()!=null?request.getRemoteUser():"";
			 logger.debug("Obteniendo datos de usuario..."+usuario);
			 System.out.println("Obteniendo datos de usuario..."+usuario);
			usuario = usuario.replaceAll("CBRS\\\\", "");
//			ArrayList<String> listaPerfiles = util.getPerfilesUsuario(usuario);			
			JSONArray listaPerfiles = util.getPerfilesUsuario(usuario);
			
			if(listaPerfiles!=null && listaPerfiles.size()>0){
				json.put("perfilesUsuario", listaPerfiles);
				json.put("status", true);
			} else{
				//Usuario defecto
				listaPerfiles = util.getPerfilesUsuario("aiobasico");	
				
				if(listaPerfiles!=null && listaPerfiles.size()>0){
					json.put("perfilesUsuario", listaPerfiles);
					json.put("status", true);
				} else{	
					json.put("msg", "Este usuario no puede utilizar esta aplicacion");
					json.put("status", false);
					
		        	if(request.getSession()!=null){
		        		request.getSession().invalidate();
		        	}
				}
			}
						
		} catch (Exception e) {
			e.printStackTrace();
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getPermisosUsuario: " + e.getMessage(), e);
			json.put("msg", "Este usuario no puede utilizar esta aplicacion");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void getSessionAttribute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String attributeReq = request.getParameter("attribute");

		JSONObject json = new JSONObject();
		json.put("estado", false);	

		try {		
			String attribute = (String)request.getSession().getAttribute(attributeReq);
			
			if(attribute!=null && !"".endsWith(attribute)){
				json.put("value", attribute);
				json.put("estado", true);
			} 
			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getUsuario: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al obtener variable en sesion");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void setSessionAttribute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String attributeReq = request.getParameter("attribute");
		String valueReq = request.getParameter("value");

		JSONObject json = new JSONObject();
		json.put("estado", false);	

		try {		
			request.getSession().setAttribute(attributeReq, valueReq);			

			json.put("estado", true);
			
		} catch (Exception e) {
        	if(request.getSession()!=null){
        		request.getSession().invalidate();
        	}
        	
			logger.error("error en getUsuario: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al setear variable en sesion");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	

}