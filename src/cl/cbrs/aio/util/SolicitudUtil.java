package cl.cbrs.aio.util;

import java.util.Date;

import org.jboss.logging.Logger;

import cl.cbr.util.GeneralException;
import cl.cbrs.funcionarios.delegate.WsFuncionariosDelegate;
import cl.cbrs.funcionarios.vo.FuncionariosSeccionVO;
import cl.cbrs.funcionarios.vo.SeccionOrigenVO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalPHDelegate;
import cl.cbrs.inscripciondigital.vo.OrigenVO;
import cl.cbrs.inscripciondigital.vo.SolicitudVO;

public class SolicitudUtil {
	private static final Logger logger = Logger.getLogger(SolicitudUtil.class);
	
	public SolicitudUtil(){
		
	}
	
	public Boolean solicitar(String usuarioCreador, Long foja, Long numero, Long ano, Integer bis){
		
			WsInscripcionDigitalDelegate digitalDelegate = new WsInscripcionDigitalDelegate();
			
			String username = usuarioCreador.replaceAll("CBRS\\\\", "");

			SolicitudVO solicitudVO = new SolicitudVO();

			//TODO tengo tabla origen, pero como lo vinculo con data funcionario?
			OrigenVO origenVO = new OrigenVO();


			WsFuncionariosDelegate wsFuncionariosDelegate = new WsFuncionariosDelegate();

			FuncionariosSeccionVO funcionariosSeccionVO;
			SeccionOrigenVO seccionOrigenVO = new SeccionOrigenVO();
			try {
				funcionariosSeccionVO = wsFuncionariosDelegate.obtenerFuncionario(username);
				seccionOrigenVO =  funcionariosSeccionVO.getSeccionOrigenVO();
			} catch (GeneralException e) {
				seccionOrigenVO.setIdOrigen(5L);	
			}

			if(seccionOrigenVO!=null){							
				origenVO.setIdOrigen(seccionOrigenVO.getIdOrigen());
			}					

			solicitudVO.setOrigen(origenVO);

			//user set
			solicitudVO.setUsuario(username);
			solicitudVO.setNombre("");

			//solicitud data 
			//TODO   1. TOMO? 2. ID_BODEGA?
			solicitudVO.setFoja(foja);
			solicitudVO.setNumero(numero);
			solicitudVO.setAno(ano);
			solicitudVO.setBis(bis);

			solicitudVO.setImpreso(0);
			solicitudVO.setFechaSolicitud(new Date());
			solicitudVO.setFechaEstado(new Date());

			try {
				digitalDelegate.agregarSolicitud(solicitudVO);
				
				return  true;
			} catch (GeneralException e) {
				logger.error(e);
			}
		
		return false;
	}
	
	public Boolean solicitarHipotecas(String usuarioCreador, Long foja, Long numero, Long ano, Integer bis){
		
			WsInscripcionDigitalHDelegate digitalDelegate = new WsInscripcionDigitalHDelegate();
			
			String username = usuarioCreador.replaceAll("CBRS\\\\", "");

			cl.cbrs.inscripciondigitalh.vo.SolicitudVO solicitudVO = new cl.cbrs.inscripciondigitalh.vo.SolicitudVO();

			//TODO tengo tabla origen, pero como lo vinculo con data funcionario?
			cl.cbrs.inscripciondigitalh.vo.OrigenVO origenVO = new cl.cbrs.inscripciondigitalh.vo.OrigenVO();


			WsFuncionariosDelegate wsFuncionariosDelegate = new WsFuncionariosDelegate();

			FuncionariosSeccionVO funcionariosSeccionVO;
			SeccionOrigenVO seccionOrigenVO = new SeccionOrigenVO();
			try {
				funcionariosSeccionVO = wsFuncionariosDelegate.obtenerFuncionario(username);
				seccionOrigenVO =  funcionariosSeccionVO.getSeccionOrigenVO();
			} catch (GeneralException e) {
				seccionOrigenVO.setIdOrigen(5L);	
			}

			if(seccionOrigenVO!=null){							
				origenVO.setIdOrigen(seccionOrigenVO.getIdOrigen());
			}					

			solicitudVO.setOrigen(origenVO);

			//user set
			solicitudVO.setUsuario(username);
			solicitudVO.setNombre("");

			//solicitud data 
			//TODO   1. TOMO? 2. ID_BODEGA?
			solicitudVO.setFoja(foja);
			solicitudVO.setNumero(numero);
			solicitudVO.setAno(ano);
			solicitudVO.setBis(bis);

			solicitudVO.setImpreso(0);
			solicitudVO.setFechaSolicitud(new Date());
			solicitudVO.setFechaEstado(new Date());

			try {
				digitalDelegate.agregarSolicitud(solicitudVO);
				
				return  true;
			} catch (GeneralException e) {
				logger.error(e);
			}
	
		return false;
	}
	
	public Boolean solicitarProhibiciones(String usuarioCreador, Long foja, Long numero, Long ano, Integer bis){
		
			WsInscripcionDigitalPHDelegate digitalDelegate = new WsInscripcionDigitalPHDelegate();
			
			String username = usuarioCreador.replaceAll("CBRS\\\\", "");

			cl.cbrs.inscripciondigitalph.vo.SolicitudVO solicitudVO = new cl.cbrs.inscripciondigitalph.vo.SolicitudVO();

			//TODO tengo tabla origen, pero como lo vinculo con data funcionario?
			cl.cbrs.inscripciondigitalph.vo.OrigenVO origenVO = new cl.cbrs.inscripciondigitalph.vo.OrigenVO();


			WsFuncionariosDelegate wsFuncionariosDelegate = new WsFuncionariosDelegate();

			FuncionariosSeccionVO funcionariosSeccionVO;
			SeccionOrigenVO seccionOrigenVO = new SeccionOrigenVO();
			try {
				funcionariosSeccionVO = wsFuncionariosDelegate.obtenerFuncionario(username);
				seccionOrigenVO =  funcionariosSeccionVO.getSeccionOrigenVO();
			} catch (GeneralException e) {
				seccionOrigenVO.setIdOrigen(5L);	
			}

			if(seccionOrigenVO!=null){							
				origenVO.setIdOrigen(seccionOrigenVO.getIdOrigen());
			}					

			solicitudVO.setOrigen(origenVO);

			//user set
			solicitudVO.setUsuario(username);
			solicitudVO.setNombre("");

			//solicitud data 
			//TODO   1. TOMO? 2. ID_BODEGA?
			solicitudVO.setFoja(foja);
			solicitudVO.setNumero(numero);
			solicitudVO.setAno(ano);
			solicitudVO.setBis(bis);

			solicitudVO.setImpreso(0);
			solicitudVO.setFechaSolicitud(new Date());
			solicitudVO.setFechaEstado(new Date());

			try {
				digitalDelegate.agregarSolicitud(solicitudVO);
				
				return  true;
			} catch (GeneralException e) {
				logger.error(e);
			}
		
		return false;
	}	
}