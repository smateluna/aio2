package cl.cbrs.aio.util;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.PermisoDTO;
import cl.cbrs.aio.dto.SubPermisoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.wsusuarios.ServiciosUsuariosDelegate;
import cl.cbrs.wsusuarios.ws.request.ObtenerModulosPorUsuarioPerfilRequest;
import cl.cbrs.wsusuarios.ws.response.ObtenerModulosPorUsuarioPerfilResponse;
import cl.cbrs.wsusuarios.ws.vo.ModuloVO;
import cl.cbrs.wsusuarios.ws.vo.RecursoVO;
import cl.cbrs.wsusuarios.ws.vo.UsuarioPerfilVO;

public class UsuarioUtil {
	private static final Logger logger = Logger.getLogger(UsuarioUtil.class);
	
	private static final String TABLA_PARAMETROS = "ws_usuarios.parametros";

	public UsuarioUtil(){

	}

//	public ArrayList<String> getPerfilesUsuario(String usuario) throws Exception{
//		ArrayList<String> listaPerfiles = new ArrayList<String>();
//
//		ServiciosUsuariosDelegate usuarioDelegate = new ServiciosUsuariosDelegate();
//		LoginUsuarioRequest request = new LoginUsuarioRequest();
//		request.setIdRequest("01");
//		request.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
//		request.setUser(usuario);
//
//		LoginUsuarioResponse response = usuarioDelegate.loginUsuario(request );
//
//		if(response!=null && response.getUsuarioPerfil()!=null){
//			UsuarioPerfilVO[] usuarioPerfilVOs = response.getUsuarioPerfil();
//			for(UsuarioPerfilVO usuarioPerfilVO : usuarioPerfilVOs)
//				listaPerfiles.add(usuarioPerfilVO.getPerfil().trim());
//		}
//
//		return listaPerfiles;
//	}
	
	public JSONArray getPerfilesUsuario(String usuario) throws Exception{
		
		Client client = Client.create();
		String ip = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS", "valor");
		String port = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS", "valor");
		JSONArray listaPerfiles = new JSONArray();
         System.out.println("http://"+ip+":"+port+"/UsuariosRest/usuario/loginUsuario/"+CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA")+"/"+usuario);
//		System.out.println("http://"+ip+":"+port+"/UsuariosRest/usuario/loginUsuario/"+CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA")+"/"+usuario);
		
		WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/UsuariosRest/usuario/loginUsuario/"+CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA")+"/"+usuario));

		ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
		com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

		if(statusRespuesta.getStatusCode() == 200){
			listaPerfiles = (JSONArray) getResponse(clientResponse);
		}
		
		return listaPerfiles;
	}

	public ArrayList<PermisoDTO> getPermisosUsuario(String usuario, String perfil, UsuarioPerfilVO usuarioPerfilVO) throws Exception{

		ServiciosUsuariosDelegate usuarioDelegate = new ServiciosUsuariosDelegate();
		ObtenerModulosPorUsuarioPerfilRequest modulosPorUsuarioPerfilRequest = new ObtenerModulosPorUsuarioPerfilRequest();
		modulosPorUsuarioPerfilRequest.setIdRequest("1");
		modulosPorUsuarioPerfilRequest.setNombrePerfil(perfil);
		modulosPorUsuarioPerfilRequest.setSistema(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
		modulosPorUsuarioPerfilRequest.setUsuario("BASICO".equals(perfil)?"aiobasico":usuario);

		ObtenerModulosPorUsuarioPerfilResponse modulosPorUsuarioPerfilResponse = usuarioDelegate.obtenerModulosPorUsuarioPerfil(modulosPorUsuarioPerfilRequest );
		usuarioPerfilVO.setNombreUsuario(modulosPorUsuarioPerfilResponse.getUsuarioPerfil().getNombreUsuario());
		usuarioPerfilVO.setRut(modulosPorUsuarioPerfilResponse.getUsuarioPerfil().getRut());

		ModuloVO[] moduloVOs = modulosPorUsuarioPerfilResponse.getUsuarioPerfil().getModulos();		

		ArrayList<PermisoDTO> permisos = new ArrayList<PermisoDTO>();	

		permisos.add(getPermisoHome()); //Se agrega permiso Home por defecto

		for(ModuloVO moduloVO : moduloVOs){
			String modulo = moduloVO.getNombreModulo().trim();
			if("ANEXOS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoAnexos(moduloVO.getRecursos()));
			else if("BODEGA".equalsIgnoreCase(modulo))
				permisos.add(getPermisoBodega(moduloVO.getRecursos()));
			else if("CARTELES".equalsIgnoreCase(modulo))
				permisos.add(getPermisoCarteles(moduloVO.getRecursos()));
			else if("DESBLOQUEO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDesbloqueo(moduloVO.getRecursos()));
			else if("DIGITAL".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDigital(moduloVO.getRecursos()));
			else if("DIGITAL HIPOTECAS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDigitalHipotecas(moduloVO.getRecursos()));	
			else if("DIGITAL PROHIBICIONES".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDigitalProhibiciones(moduloVO.getRecursos()));			
			else if("DISTRIBUCION".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDistribucion(moduloVO.getRecursos()));
			else if("DOCUMENTOS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoDocumentos(moduloVO.getRecursos()));
			else if("ESTADO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoEstado(moduloVO.getRecursos()));
			else if("INDICE".equalsIgnoreCase(modulo))
				permisos.add(getPermisoIndice(moduloVO.getRecursos()));
			else if("indiceComercio".equalsIgnoreCase(modulo))
				permisos.add(getPermisoIndiceComercio());
			else if("indicePropiedad".equalsIgnoreCase(modulo))
				permisos.add(getPermisoIndicePropiedad());
			else if("mantDistribucion".equalsIgnoreCase(modulo))
				permisos.add(getPermisoMantDistribucion(moduloVO.getRecursos()));
			else if("REINGRESO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoReingreso(moduloVO.getRecursos()));
			else if("SOLICITUDES".equalsIgnoreCase(modulo))
				permisos.add(getPermisoSolicitudes(moduloVO.getRecursos()));
			else if("URGENCIA".equalsIgnoreCase(modulo))
				permisos.add(getPermisoUrgencia(moduloVO.getRecursos()));
			else if("CERTIFICACION".equalsIgnoreCase(modulo))
				permisos.add(getPermisoCertificacion(moduloVO.getRecursos()));
			else if("CERTIFICACION HIPOTECAS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoCertificacionHipotecas(moduloVO.getRecursos()));			
			else if("REPERTORIO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoRepertorio(moduloVO.getRecursos()));
			else if("GPONLINE".equalsIgnoreCase(modulo))
				permisos.add(getPermisoGponline(moduloVO.getRecursos()));
			else if("AUDITORIA".equalsIgnoreCase(modulo))
				permisos.add(getPermisoAuditoria(moduloVO.getRecursos()));
			else if("CERTIFICACION EN PARTE".equalsIgnoreCase(modulo))
				permisos.add(getPermisoCertificacionEnParte(moduloVO.getRecursos()));
			else if("TAREAS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoTareas(moduloVO.getRecursos()));		
			else if("UAF".equalsIgnoreCase(modulo))
				permisos.add(getPermisoUAF(moduloVO.getRecursos()));				
			else if("LISTADO REPERTORIO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoListadoRepertorio(moduloVO.getRecursos()));
			else if("LIQUIDACION".equalsIgnoreCase(modulo))
				permisos.add(getPermisoLiquidacion(moduloVO.getRecursos()));			
			else if("REVISION DE NOTAS".equalsIgnoreCase(modulo))
				permisos.add(getPermisoRevisionNotas(moduloVO.getRecursos()));
			else if("LISTADO CTA CTE".equalsIgnoreCase(modulo))
				permisos.add(getPermisoListadoCtaCte(moduloVO.getRecursos()));
			else if("NOMINA CTA CTE".equalsIgnoreCase(modulo))
				permisos.add(getPermisoNominaCtaCte(moduloVO.getRecursos()));
			else if("INFO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoInfo(moduloVO.getRecursos()));
			else if("REINGRESO ESCRITURA".equalsIgnoreCase(modulo))
				permisos.add(getPermisoReingresoEscritura(moduloVO.getRecursos()));
			else if("OFICIO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoOficio());
			else if("Mantenedor Ctas Ctes".equalsIgnoreCase(modulo))
				permisos.add(getPermisoMantCtasCtes(moduloVO.getRecursos()));			
			else if("Recepcion Planos".equalsIgnoreCase(modulo))
				permisos.add(getPermisoRecepcionPlanos(moduloVO.getRecursos()));									
			else if("PLANTILLERO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoPlantillero(moduloVO.getRecursos()));
			else if("LISTADO CUADERNILLO".equalsIgnoreCase(modulo))
				permisos.add(getPermisoListadoCuadernillo(moduloVO.getRecursos()));

		}

		return permisos;
	}

	private PermisoDTO getPermisoPlantillero(RecursoVO[] recursos) {
		PermisoDTO dto = new PermisoDTO();
		dto.setId("plantillero");
		dto.setTitulo("Certificación Manual");
		dto.setPath("/plantillero");
		dto.setIcono("fa-edit");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	}

	private PermisoDTO getPermisoHome(){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("home");
		dto.setTitulo("Home");
		dto.setPath("/home");
		dto.setIcono("fa-home");

		return dto;
	}

	private PermisoDTO getPermisoEstado(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("estado");
		dto.setTitulo("Estado");
		dto.setPath("/estado");
		dto.setIcono("fa-info");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos);  
		return dto;
	};

	private PermisoDTO getPermisoIndiceComercio(){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("indice_com");
		dto.setTitulo("Índice Comercio");
		dto.setPath("/indice_com");
		dto.setIcono("fa-list");

		return dto;
	};

	private PermisoDTO getPermisoIndicePropiedad(){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("indice_prop");
		dto.setTitulo("Índice Propiedad");
		dto.setPath("/indice_prop");
		dto.setIcono("fa-list");

		return dto;
	};

	private PermisoDTO getPermisoIndice(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("indice");
		dto.setTitulo("Índice");
		dto.setPath("/indice");
		dto.setIcono("fa-list");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 


		//subOpciones: [permisoIndiceComercio, permisoIndicePropiedad]

		return dto;
	};

	private PermisoDTO getPermisoReingreso(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("reingreso");
		dto.setTitulo("Reingreso");
		dto.setPath("/reingreso");
		dto.setIcono("fa-repeat");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoBodega(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("bodega");
		dto.setTitulo("Bodega");
		dto.setPath("/bodega");
		dto.setIcono("fa-archive");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoDesbloqueo(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("desbloqueo");
		dto.setTitulo("Desbloqueo mal citado");
		dto.setPath("/desbloqueo");
		dto.setIcono("fa-unlock");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoUrgencia(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("urgencia");
		dto.setTitulo("Urgencia solicitud");
		dto.setPath("/urgencia");
		dto.setIcono("fa-ambulance");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoDocumentos(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("documentos");
		dto.setTitulo("Documentos por carátula");
		dto.setPath("/documentos");
		dto.setIcono("fa-paperclip");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoAnexos(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("anexos");
		dto.setTitulo("Anexos");
		dto.setPath("/anexos");
		dto.setIcono("fa-phone-square");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoDistribucion(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("distribucion");
		dto.setTitulo("Distribución");
		dto.setPath("/distribucion");
		dto.setIcono("fa-code-fork");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoSolicitudes(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("solicitudes");
		dto.setTitulo("Solicitudes digitalización");
		dto.setPath("/solicitudes");
		dto.setIcono("fa-list-alt");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoDigital(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("digital");
		dto.setTitulo("Digital Propiedades");
		dto.setPath("/digital");
		dto.setIcono("fa-desktop");
		dto.setEstilo("color: Black");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};
	
	private PermisoDTO getPermisoDigitalHipotecas(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("digitalHipotecas");
		dto.setTitulo("Digital Hipotecas");
		dto.setPath("/digitalHipotecas");
		dto.setIcono("fa-desktop");
		dto.setEstilo("color: DarkRed");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	
	
	private PermisoDTO getPermisoDigitalProhibiciones(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("digitalProhibiciones");
		dto.setTitulo("Digital Prohibiciones");
		dto.setPath("/digitalProhibiciones");
		dto.setIcono("fa-desktop");
		dto.setEstilo("color: DarkGreen ");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	

	private PermisoDTO getPermisoMantDistribucion(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("mantdistribucion");
		dto.setTitulo("Mantenedor Distri.");
		dto.setPath("/mantenedorDistribucion");
		dto.setIcono("fa-users");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoCarteles(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("carteles");
		dto.setTitulo("Carteles");
		dto.setPath("/carteles");
		dto.setIcono("fa-folder-open");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoCertificacion(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("certificado");
		dto.setTitulo("Certificación");
		dto.setPath("/certificacion");
		dto.setIcono("fa-check-circle");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};
	
	private PermisoDTO getPermisoCertificacionHipotecas(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("certificadoHipotecas");
		dto.setTitulo("Certificación Hipotecas");
		dto.setPath("/certificacionHipotecas");
		dto.setIcono("fa-check-circle");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	

	private PermisoDTO getPermisoRepertorio(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("repertorio");
		dto.setTitulo("Repertorio");
		dto.setPath("/repertorio");
		dto.setIcono("fa-info");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoGponline(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("gponline");
		dto.setTitulo("GpOnline");
		dto.setPath("/gponline");
		dto.setIcono("fa-building");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoAuditoria(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("auditoria");
		dto.setTitulo("Auditoría");
		dto.setPath("/auditoria");
		dto.setIcono("fa-eye");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoLiquidacion(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("liquidacion");
		dto.setTitulo("Liquidación");
		dto.setPath("/liquidacion");
		dto.setIcono("fa-usd");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoCertificacionEnParte(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("certificadoenparte");
		dto.setTitulo("Certificación En Parte");
		dto.setPath("/certificacionenparte");
		dto.setIcono("fa-check-circle");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoTareas(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("tareas");
		dto.setTitulo("Tareas");
		dto.setPath("/tareas");
		dto.setIcono("fa-file-text-o");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};  

	private PermisoDTO getPermisoUAF(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("uaf");
		dto.setTitulo("Buscador UAF");
		dto.setPath("/uaf");
		dto.setIcono("fa-clipboard");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		//    ArrayList<SubPermisoDTO> subOpciones = new ArrayList<SubPermisoDTO>();
		//    subOpciones.add(getPermisoUAF_Op1());
		//    subOpciones.add(getPermisoUAF_Op2());	
		//	dto.setSubOpciones(subOpciones);


		return dto;
	};  

	private PermisoDTO getPermisoListadoRepertorio(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("listadoRepertorio");
		dto.setTitulo("Listado Repertorio");
		dto.setPath("/listadorepertorio");
		dto.setIcono("fa-list");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};

	private PermisoDTO getPermisoListadoCtaCte(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("listadoCtaCte");
		dto.setTitulo("Listado Cta Cte");
		dto.setPath("/listadoctacte");
		dto.setIcono("fa-list");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};
	
	private PermisoDTO getPermisoNominaCtaCte(RecursoVO[] recursos) {
		PermisoDTO dto = new PermisoDTO();
		dto.setId("nominaCtaCte");
		dto.setTitulo("Nomina Cta Cte");
		dto.setPath("/nominactacte");
		dto.setIcono("fa-list");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	}
	
	private PermisoDTO getPermisoInfo(RecursoVO[] recursos) {
		PermisoDTO dto = new PermisoDTO();
		dto.setId("info");
		dto.setTitulo("Info AIO");
		dto.setPath("/info");
		dto.setIcono("fa-users");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	}	

	private PermisoDTO getPermisoRevisionNotas(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("revisionNotas");
		dto.setTitulo("Revisión de Notas");
		dto.setPath("/revisionNotas");
		dto.setIcono("fa-eye");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	

	private SubPermisoDTO getPermisoUAF_Op1(){
		SubPermisoDTO dto = new SubPermisoDTO();
		dto.setId("uaf1");
		dto.setTitulo("Búsqueda de personas");
		dto.setPath("/uaf1");
		dto.setIcono("fa-list"); 	

		return dto;
	};

	private SubPermisoDTO getPermisoUAF_Op2(){
		SubPermisoDTO dto = new SubPermisoDTO();
		dto.setId("uaf2");
		dto.setTitulo("Búsqueda de bienes");
		dto.setPath("/uaf2");
		dto.setIcono("fa-briefcase"); 	

		return dto;
	};
	
	private PermisoDTO getPermisoReingresoEscritura(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("reingresoEscritura");
		dto.setTitulo("Reingreso Escritura");
		dto.setPath("/reingresoEscritura");
		dto.setIcono("fa-repeat");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};
	private PermisoDTO getPermisoOficio(){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("oficio");
		dto.setTitulo("Búsqueda Oficios");
		dto.setPath("/oficio");
		dto.setIcono("fa-search");

		return dto;
	};
	
	private PermisoDTO getPermisoMantCtasCtes(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("mantctasctes");
		dto.setTitulo("Mantenedor Ctas Ctes");
		dto.setPath("/mantenedorCtasCtes");
		dto.setIcono("fa-cogs");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	
	
	private PermisoDTO getPermisoRecepcionPlanos(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("recepcionPlanos");
		dto.setTitulo("Recepción Planos");
		dto.setPath("/recepcionPlanos");
		dto.setIcono("fa-map-marker");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};
	
	private PermisoDTO getPermisoListadoCuadernillo(RecursoVO[] recursos){
		PermisoDTO dto = new PermisoDTO();
		dto.setId("listadoCuadernillo");
		dto.setTitulo("Listado Cuadernillos");
		dto.setPath("/listadoCuadernillo");
		dto.setIcono("fa-list");

		ArrayList<String> subPermisos = new ArrayList<String>();
		for(RecursoVO recurso : recursos){
			subPermisos.add(recurso.getNombreRecurso().trim());
		}	   
		dto.setSubPermisos(subPermisos); 

		return dto;
	};	
	
	private static Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(response.getType()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(MediaType.APPLICATION_JSON_TYPE.equals(response.getType()))
				respuesta = new JSONParser().parse(response.getEntity(String.class));
			else
				respuesta = response.getEntity(String.class);

		} else if(response!=null)
			throw new HTTPException(response.getStatus());
		else
			throw new Exception("Sin respuesta del servicio");
		return respuesta;
	}	

}