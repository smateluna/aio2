package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.keycloak.KeycloakSecurityContext;

import cl.cbr.util.StringUtil;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.certificado.GeneraCertificado;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.EstadoInscripcionVO;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.inscripciondigital.vo.OrigenVO;
import cl.cbrs.inscripciondigital.vo.SolicitudVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class CertificacionServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(CertificacionServiceAction.class);
	final String TABLA_PARAMETROS="ProcesaImpresion.parametros";
	private final String SECCION_EN_PARTE = "C2";
	Integer FECHA_REHACER_IMAGEN= Integer.parseInt(TablaValores.getValor(TABLA_PARAMETROS,"FECHA_REHACER_IMAGEN", "valor"));
	private final String TIPO_EN_PARTE = "10";
	private static final Long ORIGEN_COPIA_ELECTRONICA = 5L;
	Integer INACTIVA = 0;
	Integer ACTIVA = 1;
	
	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerCaratulasParaCertificar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";
		
		List<CaratulaVO> caratulaVOs = null;
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuario =context.getIdToken().getPreferredUsername();			
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
//		C3 Copias Electronicas por default
		String seccion = "C3";
		Long caratulaAux = null;
		
		try{
			WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
//			WsInscripcionDigitalDelegate insdelegate = new WsInscripcionDigitalDelegate();
			caratulaVOs = delegate.obtenerCaratulasPorSeccionUsuario(seccion,usuario);
			
			if(caratulaVOs!=null){ 
				if(caratulaVOs.size()>0){
					for(CaratulaVO caratula: caratulaVOs){
						caratulaAux = caratula.getNumeroCaratula();
						InscripcionCitadaVO[] inscripciones = caratula.getInscripciones();
						if(null != inscripciones){
							for(InscripcionCitadaVO ins : inscripciones){
								try{
									JSONObject fila = new JSONObject();
									fila.put("caratula", caratula.getNumeroCaratula());
									
									fila.put("fechaCreacion", sdf.format(caratula.getEstadoActualCaratula().getFechaMov()));
									
									if(null != caratula.getTipoFormulario()){
										if(seccion==SECCION_EN_PARTE){
											fila.put("tipoFormulario", "Copia en Parte");
											fila.put("idtipoFormulario", 10);
										}else{
											fila.put("tipoFormulario", caratula.getTipoFormulario().getDescripcion());
											fila.put("idtipoFormulario", caratula.getTipoFormulario().getTipo());
										}
									}else{
										fila.put("tipoFormulario", "");
										fila.put("idtipoFormulario", "");
									}	
	
									fila.put("foja", ins.getFoja());
									fila.put("numero", ins.getNumero());
									fila.put("ano", ins.getAno());
									fila.put("bis", ins.getBis());
									
									if(FECHA_REHACER_IMAGEN <= ins.getAno())
										fila.put("rehaceImagen", 0);
									else	
										fila.put("rehaceImagen", 1);
									
	//								if(seccion==SECCION_EN_PARTE){
	//					                InscripcionDigitalVO inscripcionDigitalVO = insdelegate.obtenerInscripcionDigital(new Long(ins.getFoja()),ins.getNumero().toString(),new Long(ins.getAno()), false);
	//					                
	//					                if(null!=inscripcionDigitalVO){
	//					                	System.out.println("caratula"+caratula.getNumeroCaratula() + " " + sdf.format(inscripcionDigitalVO.getFechaActualizacion()));
	//					                	fila.put("fechaDoc", sdf.format(inscripcionDigitalVO.getFechaActualizacion()));
	//					                	fila.put("fechaDocLong", inscripcionDigitalVO.getFechaActualizacion().getTime());
	//					                }	
	//								}else{
									if(ins.getFecha()!=null){
										fila.put("fechaDoc", sdf.format(ins.getFecha()));
										fila.put("fechaDocLong", ins.getFecha().getTime());
									}
									else{
										fila.put("fechaDoc", "");
										fila.put("fechaDocLong", 1);
									}
										
	//								}
	
									resultado.add(fila);
								} catch(Exception e){
									logger.error(e.getMessage()+" Caratula: " +caratulaAux,e);
								}
							}
						}
					}
				}	
			}	
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void certificar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuario =context.getIdToken().getPreferredUsername();			
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		try{
		
			String caratula = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
	    	String tipo = request.getParameter("tipoCertificacion")==null?"0":request.getParameter("tipoCertificacion");
	    	
	    	String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
	    	rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
	    	
	    	boolean enParte = false;
	//		C2 Seccion Certificacion de Copia en Parte - TIPO 10
			if(tipo.equals(TIPO_EN_PARTE.toString()))
				enParte=true;
	    	
	    	GeneraCertificado genera = new GeneraCertificado();
	    	WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();
	        
	        CaratulaVO caratulaVO;
			
			caratulaVO = delegateCaratula.obtenerCaratulaPorNumero(new UsuarioWebVO(), new Long(caratula));
	
            genera.crearCertificado(caratulaVO,true,enParte,tipo,usuario);
	            	                
            delegateCaratula.actualizarCaratula(caratulaVO);
	
            //*habilitar antes del paso
            moverCaratula(delegateCaratula,caratulaVO,rutFuncionario);
				
			status = true;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);

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
	
	private void moverCaratula(WsCaratulaClienteDelegate delegate,CaratulaVO caratula, String rutFuncionario) throws cl.cbr.common.exception.GeneralException{
	        EstadoCaratulaVO estadoCaratula = new EstadoCaratulaVO();
	        estadoCaratula.setFechaMovimiento(new Date());
	        
	        SeccionVO seccion = new SeccionVO("A8"); 
	        estadoCaratula.setSeccion(seccion);
	        
	        FuncionarioVO funcionarioEnvia = null;

            funcionarioEnvia = new FuncionarioVO(rutFuncionario);
            estadoCaratula.setMaquina("COPIAS PROP");
	        
	        estadoCaratula.setEnviadoPor(funcionarioEnvia);
	        FuncionarioVO funcionarioResponsable = new FuncionarioVO("00046");
	        
	        estadoCaratula.setResponsable(funcionarioResponsable);
	        
	        delegate.moverCaratulaSeccion(caratula.getNumeroCaratula(), estadoCaratula);
	        
	 }
	
	@SuppressWarnings("unchecked")
	public void rehacerImagen(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		
		try{

			String caratula = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
			String ano = request.getParameter("ano")==null?"":request.getParameter("ano");
	    	Long foja = request.getParameter("foja")==null?0:new Long(request.getParameter("foja"));
	    	String numero = request.getParameter("numero")==null?"":request.getParameter("numero");
	    	Integer bis = request.getParameter("bis")==null?0:new Integer(request.getParameter("bis"));
	    	String motivo = request.getParameter("motivo")==null?"":request.getParameter("motivo");
	    	
	    	String motivoConEncoding = new String(motivo.getBytes(),"UTF-8");
	    	
	    	String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
	    	rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
	    	
        	WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();
        	WsInscripcionDigitalDelegate delegateInscripcion = new WsInscripcionDigitalDelegate();

        	//Solicitando nueva imagen
			SolicitudVO solicitudVO = new SolicitudVO();
			
			solicitudVO.setFechaSolicitud(new Date());
			OrigenVO origenVO = new OrigenVO();
			origenVO.setIdOrigen(ORIGEN_COPIA_ELECTRONICA);
			solicitudVO.setOrigen(origenVO);
			solicitudVO.setUsuario("procesoCopia");
			solicitudVO.setRut(10L);
			solicitudVO.setDv("5");
			solicitudVO.setNombre("REHACER-"+motivoConEncoding+" C:"+caratula+" - Digital Bodega");
			solicitudVO.setFechaEstado(new Date());
			solicitudVO.setImpreso(0);
			solicitudVO.setFoja(foja);
			solicitudVO.setNumero(new Long(numero));
			solicitudVO.setAno(new Long(ano));
			solicitudVO.setBis(0);
			
			logger.debug("TITULO A SOLICITAR PARA REHACER: "+foja+" "+numero+" "+ano);
        	
        	delegateInscripcion.agregarSolicitud(solicitudVO);
        	
        	//Eliminando Inscripcion Digital
        	InscripcionDigitalVO inscripcionDigitalVO = delegateInscripcion.obtenerInscripcionDigital(foja, numero,new Long(ano), false);
        	if(null!=inscripcionDigitalVO){
        			//        	delegateInscripcion.eliminarInscripcionDigital(inscripcionDigitalVO);
		        	//Dejando Inactiva la inscripcion
		        	EstadoInscripcionVO estadoInscripcionVO = new EstadoInscripcionVO();
		        	estadoInscripcionVO.setIdEstadoInscripcion(INACTIVA);
		        	inscripcionDigitalVO.setEstadoInscripcionVO(estadoInscripcionVO);
		        	delegateInscripcion.actualizarInscripcion(inscripcionDigitalVO);
			}   	

        	//Moviendo caratula a En proceso de Copia Electronica para que se rehaga la digitalizacion  
        	
        	EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

			FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
			funcionarioEnviaVO.setRutFuncionario(rutFuncionario);
			estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

			FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
			funcionarioResponsableVO.setRutFuncionario("00000105");
			estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	
			
			SeccionVO seccionVO = new SeccionVO();
			seccionVO.setCodigo("C4"); 
			estadoCaratulaVO.setSeccion(seccionVO);
			estadoCaratulaVO.setMaquina("Sistema Certificacion");

			estadoCaratulaVO.setFechaMovimiento(new Date());
        	
        	delegateCaratula.moverCaratulaSeccion(Long.parseLong(caratula), estadoCaratulaVO);
				
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
	
	@SuppressWarnings("unchecked")
	public void distribuir(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuario =context.getIdToken().getPreferredUsername();			
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		try{
		
			String caratula = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
	    	String tipo = request.getParameter("tipoCertificacion")==null?"0":request.getParameter("tipoCertificacion");
	    	
	    	String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
	    	rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
	    	
	    	String rutEnvia = null;	        	
        	String rutResponsable = null;
        	String seccionDestino = null;
        	String maquina = null;
        	
        	WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();

        	if(TIPO_EN_PARTE.equals(tipo)){
	        	rutEnvia = rutFuncionario;	        	
	        	rutResponsable = "06929085k";
	        	seccionDestino = SECCION_EN_PARTE;
	        	maquina = "Proceso Copia Electronica";
        	} else {
	        	rutEnvia = rutFuncionario;	        	
	        	rutResponsable = "00019";
	        	seccionDestino = "C1";
	        	maquina = "Proceso Copia Electronica";
        	}
        	
        	EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

			FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
			funcionarioEnviaVO.setRutFuncionario(rutEnvia);
			estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

			FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
			funcionarioResponsableVO.setRutFuncionario(rutResponsable);
			estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	
			
			SeccionVO seccionVO = new SeccionVO();
			seccionVO.setCodigo(seccionDestino); 
			estadoCaratulaVO.setSeccion(seccionVO);
			estadoCaratulaVO.setMaquina(maquina);

			estadoCaratulaVO.setFechaMovimiento(new Date());
        	
        	delegateCaratula.moverCaratulaSeccion(Long.parseLong(caratula), estadoCaratulaVO);
				
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
	
	@SuppressWarnings("unchecked")
	public void obtenerCaratulasParaCertificarEnParte(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";
		
		List<CaratulaVO> caratulaVOs = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
//		C2 Copias Electronicas En Parte por default
		String seccion = "C2";
		Long caratulaAux = null;
		
		try{
			WsCaratulaClienteDelegate delegate = new WsCaratulaClienteDelegate();
//			WsInscripcionDigitalDelegate insdelegate = new WsInscripcionDigitalDelegate();
			caratulaVOs = delegate.obtenerCaratulasPorSeccionUsuario(seccion,"RQUENSE");
								
			if(caratulaVOs!=null){ 
				if(caratulaVOs.size()>0){
					
					for(CaratulaVO caratula: caratulaVOs){
						caratulaAux = caratula.getNumeroCaratula();
						InscripcionCitadaVO[] inscripciones = caratula.getInscripciones();
						if(null != inscripciones){
							for(InscripcionCitadaVO ins : inscripciones){
								try{
									JSONObject fila = new JSONObject();
									fila.put("caratula", caratula.getNumeroCaratula());
									
									fila.put("fechaCreacion", sdf.format(caratula.getEstadoActualCaratula().getFechaMov()));
									
									if(null != caratula.getTipoFormulario()){
										if(seccion==SECCION_EN_PARTE){
											fila.put("tipoFormulario", "Copia en Parte");
											fila.put("idtipoFormulario", 10);
										}else{
											fila.put("tipoFormulario", caratula.getTipoFormulario().getDescripcion());
											fila.put("idtipoFormulario", caratula.getTipoFormulario().getTipo());
										}
									}else{
										fila.put("tipoFormulario", "");
										fila.put("idtipoFormulario", "");
									}	
	
									fila.put("foja", ins.getFoja());
									fila.put("numero", ins.getNumero());
									fila.put("ano", ins.getAno());
									fila.put("bis", ins.getBis());
									
									if(FECHA_REHACER_IMAGEN <= ins.getAno())
										fila.put("rehaceImagen", 0);
									else	
										fila.put("rehaceImagen", 1);
									
	//								if(seccion==SECCION_EN_PARTE){
	//					                InscripcionDigitalVO inscripcionDigitalVO = insdelegate.obtenerInscripcionDigital(new Long(ins.getFoja()),ins.getNumero().toString(),new Long(ins.getAno()), false);
	//					                
	//					                if(null!=inscripcionDigitalVO){
	////					                	System.out.println("caratula"+caratula.getNumeroCaratula() + " " + sdf.format(inscripcionDigitalVO.getFechaActualizacion()));
	//					                	fila.put("fechaDoc", sdf.format(inscripcionDigitalVO.getFechaActualizacion()));
	//					                	fila.put("fechaDocLong", inscripcionDigitalVO.getFechaActualizacion().getTime());
	//					                }	
	//								}else{
									if(ins.getFecha()!=null){
										fila.put("fechaDoc", sdf.format(ins.getFecha()));
										fila.put("fechaDocLong", ins.getFecha().getTime());
									}
									else{
										fila.put("fechaDoc", "");
										fila.put("fechaDocLong", 1);
									}
								//								}
	
									resultado.add(fila);
								} catch(Exception e){
									logger.error(e.getMessage()+" Caratula: " +caratulaAux,e);
								}
							}
						}
					}
				}	
			}	
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void vistaPrevia(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		
		KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
		String usuario =context.getIdToken().getPreferredUsername();			
		usuario = usuario.replaceAll("CBRS\\\\", "");
		
		try{
		
			String caratula = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
	    	String titulo = request.getParameter("titulo")==null?"":request.getParameter("titulo");
	    	String cuerpoplantilla = request.getParameter("cuerpocertificado")==null?"":request.getParameter("cuerpocertificado");
	    	String prefijo = request.getParameter("prefijo")==null?"":request.getParameter("prefijo");
	    	String valor = request.getParameter("valor")==null?"":request.getParameter("valor");
	
	    	GeneraCertificado genera = new GeneraCertificado();
	    	WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();
	        
	        CaratulaVO caratulaVO;
			
			caratulaVO = delegateCaratula.obtenerCaratulaPorNumero(new UsuarioWebVO(), new Long(caratula));
	
            genera.crearCertificadoPlantilla(caratulaVO,false,usuario,titulo,cuerpoplantilla,prefijo,Long.parseLong(valor));
				
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
		
	@SuppressWarnings("unchecked")
	public void certificarvistapreviaplantilla(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		
		try{
		
			String caratula = request.getParameter("caratula")==null?"0":request.getParameter("caratula");
	    	String prefijo = request.getParameter("prefijo")==null?"0":request.getParameter("prefijo");
	    	
	    	GeneraCertificado genera = new GeneraCertificado();
	    	
            genera.firmarCertificadoPlantilla(caratula,prefijo);
	      
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
	
}