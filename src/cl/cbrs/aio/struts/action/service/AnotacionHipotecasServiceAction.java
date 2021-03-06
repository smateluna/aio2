package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.keycloak.KeycloakSecurityContext;

import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.delegate.repertorio.WsRepertorioClienteDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;
import cl.cbrs.wsusuarios.ServiciosUsuariosDelegate;
import cl.cbrs.wsusuarios.ws.request.LoginUsuarioRequest;
import cl.cbrs.wsusuarios.ws.response.LoginUsuarioResponse;
import cl.cbrs.wsusuarios.ws.vo.UsuarioPerfilVO;

public class AnotacionHipotecasServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(AnotacionHipotecasServiceAction.class);
	
	private static String PARAMETROS_LIBROS = "digital_libros.parametros";
	
	private ArrayList<String> listaNotasCache = new ArrayList<String>();

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void addAnotacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String texto = request.getParameter("texto");
		String idInscripcionp = request.getParameter("idInscripcion");
		String repertoriop = request.getParameter("repertorio");
		String caratulap = request.getParameter("caratula");
		String acto = request.getParameter("acto");
		String versionp = request.getParameter("version");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";


		Long idInscripcion = null;
		try {
			idInscripcion = Long.parseLong(idInscripcionp);
		} catch (Exception e1) {

		}
		
		Integer repertorio = null;
		try {
			repertorio = Integer.parseInt(repertoriop);
		} catch (Exception e1) {

		}
		
		Long caratula = null;
		try {
			caratula = Long.parseLong(caratulap);
		} catch (Exception e1) {

		}
		
		Long version = null;
		try {
			version = Long.parseLong(versionp);
		} catch (Exception e1) {

		}
			
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador = context.getIdToken().getPreferredUsername();

		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(idInscripcion==null || idInscripcion.intValue()==0){
			msg = "Busque la inscripción e ingrese la nota nuevamente.";
		}else if(StringUtils.isBlank(texto)){
			msg = "Se requiere texto nota.";
		}else{
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO = new cl.cbrs.inscripciondigitalh.vo.AnotacionVO();

			
			anotacionVO.setNombreUsuarioCreador(usuarioCreador);
			anotacionVO.setTexto(texto);
			anotacionVO.setActo(acto);
			
			if(caratula!=null && repertorio!=0){
				anotacionVO.setRepertorio(repertorio);
				anotacionVO.setCaratula(caratula);
				
				if(version!=null){
					if(version==0)				
						anotacionVO.setCaratulaMatriz(caratula.toString());
					else
						anotacionVO.setCaratulaMatriz(caratula+"-"+version);
				}else{
					anotacionVO.setCaratulaMatriz(caratula.toString());
				}
				
			}
			
			cl.cbrs.inscripciondigitalh.vo.TipoAnotacionVO tipoAnotacionVo =  new cl.cbrs.inscripciondigitalh.vo.TipoAnotacionVO();
			cl.cbrs.inscripciondigitalh.vo.EstadoAnotacionVO estadoAnotacionVO = new cl.cbrs.inscripciondigitalh.vo.EstadoAnotacionVO();
			
			if(listaNotasCache.isEmpty()){
				//Obtener tipos anotacion de property
				String strTipoAnotacion = "TIPOS_ANOTACION.";
				int i=0;
				while(TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "valor")!=null){
					String tipo = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "tipo").trim();
					String nombre = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "nombre").trim();
					
					//Agregar las de tipo nota a listaNotasCache
					if("nota".equalsIgnoreCase(tipo) && !listaNotasCache.contains(nombre.toUpperCase()))
						listaNotasCache.add(nombre.toUpperCase());
				
					i++;
				}
			}			
			
			if(listaNotasCache.contains(acto.toUpperCase())){
				//NOTAS
				tipoAnotacionVo.setIdTipoAnotacion(1L); 
				estadoAnotacionVO.setIdEstado(1L);			//ESPERA_FOLIO
				
				if("REPERTORIO".equals(acto.toUpperCase()))
					anotacionVO.setIdUsuarioCreador(1L);
				else
					anotacionVO.setIdUsuarioCreador(2L);
			} else{
				//ANOTACIONES
				tipoAnotacionVo.setIdTipoAnotacion(3L);
				estadoAnotacionVO.setIdEstado(5L);			//PARA_IMPRESION
				anotacionVO.setIdUsuarioCreador(1L);
			}

			cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO digitalVO = new cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO();

			digitalVO.setIdInscripcion(idInscripcion);

			anotacionVO.setInscripcionDigitalByIdInscripcionDestinoVo(digitalVO);
			anotacionVO.setTipoAnotacionVo(tipoAnotacionVo);		
			anotacionVO.setEstadoAnotacionVo(estadoAnotacionVO);	

			anotacionVO.setFechaCreacion(new Date());

			try {
				digitalDelegate.agregarAnotacion(anotacionVO);

				status = true;
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
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
	public void removeAnotacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String idAnotacionp = request.getParameter("idAnotacion");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";
		
		Date fechaE = new Date();


		Long idAnotacion = null;
		try {
			idAnotacion = Long.parseLong(idAnotacionp);
		} catch (Exception e1) {

		}

		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioEliminador =context.getIdToken().getPreferredUsername();
		usuarioEliminador.replaceAll("CBRS\\\\", "");

		if(StringUtils.isBlank(usuarioEliminador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(idAnotacion==null || idAnotacion.intValue()==0){
			msg = "Se requiere id de anotacion.";
		}else{
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();

			try {				
				cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO = digitalDelegate.obtenerAnotacion(idAnotacion);
				
				if( (anotacionVO.getTipoAnotacionVo().getIdTipoAnotacion() == 1 || 
						anotacionVO.getTipoAnotacionVo().getIdTipoAnotacion() == 2 ) &&
						anotacionVO.getEstadoAnotacionVo().getIdEstado()!=1){
					msg = "Ya no está en ESPERA DE FOLIO, no puede eliminar.";
				}else{					
					anotacionVO.setFechaEliminacion(new Date());
					anotacionVO.setNombreUsuarioEliminador(usuarioEliminador);
								
					digitalDelegate.eliminarAnotacionLogica(anotacionVO);
					status = true;
				}

			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
			}			
		}

		respuesta.put("status", status);
		respuesta.put("fechaEliminacion", sdf.format(fechaE));
		respuesta.put("usuarioEliminador", usuarioEliminador);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void getTiposAnotacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	

		JSONObject respuesta = new JSONObject();
		Boolean status = true;
		String msg = "";
		JSONArray listaTiposAnotacionJSON = new JSONArray();
	
		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			if(StringUtils.isBlank(usuario)){
				msg = "No hay usuario, inicie sesión nuevamente.";
			}else{
				//Obtener perfiles del usuario
				LoginUsuarioRequest usuarioRequest = new LoginUsuarioRequest();
				usuarioRequest.setIdRequest("8888");
				usuarioRequest.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
				usuarioRequest.setUser(usuario);
				usuarioRequest.setPassword("");
				ServiciosUsuariosDelegate wsUsuario = new ServiciosUsuariosDelegate();
				LoginUsuarioResponse usuarioResponse = wsUsuario.loginUsuario(usuarioRequest);
				UsuarioPerfilVO[] perfiles = usuarioResponse.getUsuarioPerfil();
				
				ArrayList<String> perfilesUsuario = new ArrayList<String>();
				for(int i=0; i<perfiles.length; i++){
					UsuarioPerfilVO usuarioPerfil = perfiles[i];
					perfilesUsuario.add(usuarioPerfil.getPerfil().toUpperCase().trim());												
				}				
				
				if(request.getSession().getAttribute("tiposAnotaciones")==null){
					//Obtener tipos anotacion de property
					String strTipoAnotacion = "TIPOS_ANOTACION.";
					int i=0;
					while(TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "valor")!=null){
						String tipo = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "tipo").trim();
						String perfil = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "perfil").trim();
						String nombre = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "nombre").trim();
						String valor = TablaValores.getValor(PARAMETROS_LIBROS, strTipoAnotacion+i, "valor").trim();
						
						//Agregar las de tipo nota a listaNotasCache
						if("nota".equalsIgnoreCase(tipo) && !listaNotasCache.contains(nombre.toUpperCase()))
							listaNotasCache.add(nombre.toUpperCase());
						
						//Tipos por perfil
						if(perfilesUsuario.contains(perfil.toUpperCase())){
							JSONObject tipoAnotacionJSON = new JSONObject();
							tipoAnotacionJSON.put("tipoAnotacion", tipo);
							tipoAnotacionJSON.put("nombreAnotacion", nombre);
							tipoAnotacionJSON.put("valorAnotacion", valor);
							
							listaTiposAnotacionJSON.add(tipoAnotacionJSON);							
						}
						
						//Se agregan tipos "libres", que no necesitan de un perfil
						if("libre".equalsIgnoreCase(perfil)){
							JSONObject tipoAnotacionJSON = new JSONObject();
							tipoAnotacionJSON.put("tipoAnotacion", tipo);
							tipoAnotacionJSON.put("nombreAnotacion", nombre);
							tipoAnotacionJSON.put("valorAnotacion", valor);
							
							listaTiposAnotacionJSON.add(tipoAnotacionJSON);
						}

						i++;
					}
					
					request.getSession().setAttribute("tiposAnotaciones", listaTiposAnotacionJSON);
				}else{
					//sacar tipos anotacion de session
					listaTiposAnotacionJSON = (JSONArray) request.getSession().getAttribute("tiposAnotaciones");
				}

			}

			
		} catch (Exception e) {
			log.error(e);
			status = false;
			msg = "Se ha detectado un problema en el servidor.";
		}				

		respuesta.put("listaTiposAnotaciones", listaTiposAnotacionJSON);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void modificaAnotacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	

		String idAnotacionp = request.getParameter("idAnotacion");
		String caratulap = request.getParameter("caratula");
		String versionp = request.getParameter("version");
		String texto = request.getParameter("texto");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";


		Long idAnotacion = null;
		try {
			idAnotacion = Long.parseLong(idAnotacionp);
		} catch (Exception e1) {

		}
		
		Long caratula = null;
		try {
			caratula = Long.parseLong(caratulap);
		} catch (Exception e1) {

		}
		
		Long version = null;
		try {
			version = Long.parseLong(versionp);
		} catch (Exception e1) {

		}
			
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuarioCreador =context.getIdToken().getPreferredUsername();

		if(StringUtils.isBlank(usuarioCreador)){
			msg = "No hay usuario, inicie sesión nuevamente.";
		}else if(idAnotacion==null || idAnotacion.intValue()==0){
			msg = "No hay nota, no se podra realizar cambio.";
		}else if(StringUtils.isBlank(texto)){
			msg = "Se requiere texto nota.";
		}else{
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();
			
			try {
				cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO = digitalDelegate.obtenerAnotacion(idAnotacion);

				anotacionVO.setNombreUsuarioCreador(usuarioCreador);
				anotacionVO.setTexto(texto);

				if(caratula!=null){
					anotacionVO.setCaratula(caratula);

					if(version!=null){
						if(version==0)				
							anotacionVO.setCaratulaMatriz(caratula.toString());
						else
							anotacionVO.setCaratulaMatriz(caratula+"-"+version);
					}else{
						anotacionVO.setCaratulaMatriz(caratula.toString());
					}

				}

				digitalDelegate.actualizarAnotacion(anotacionVO);

				status = true;
			} catch (GeneralException e) {
				log.error(e);
				msg = "Se ha detectado un problema en el servidor.";
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
	public void getRepertorioCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	

		JSONObject respuesta = new JSONObject();
		Boolean status = true;
		String msg = "";
		Integer repertorio = new Integer(0);
		Date fechaIngresoRepertorio = new Date();
		String caratulap = request.getParameter("caratula");
		
		Long caratula = null;
		try {
			if(!StringUtils.isBlank(caratulap))
				caratula = Long.parseLong(caratulap);

		} catch (Exception e1) {

		}
		
		try {
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			if(StringUtils.isBlank(usuario)){
				msg = "No hay usuario, inicie sesión nuevamente.";
			}else{
				if(null!=caratula){
					WsRepertorioClienteDelegate wsRepertorioClienteDelegate = new WsRepertorioClienteDelegate();
					List<RepertorioVO> repertorios = wsRepertorioClienteDelegate.existeCaratulaConRepertorio(caratula);
					if(null!=repertorios){
						if(repertorios.size()>0){
							RepertorioVO repertorioVO = repertorios.get(0);
							repertorio = repertorioVO.getRepertorioIdVO().getNumRepertorioProp();
							fechaIngresoRepertorio = repertorioVO.getFechaIngreso();
						}
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			status = false;
			msg = "Se ha detectado un problema en el servidor.";
		}				

		respuesta.put("repertorio", repertorio);
		respuesta.put("fechaIngresoRepertorio", fechaIngresoRepertorio.getTime());
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}		
	}
	
	

}