package cl.cbrs.aio.util;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.foliomercantil.vo.EstadoCaratulaVO;
import cl.cbr.foliomercantil.vo.FojaCaratulaVO;
import cl.cbr.foliomercantil.vo.WorkflowVO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.wscomercio.ws.ServiciosComercioDelegate;
import cl.cbrs.wscomercio.ws.request.ActualizarFojaCaratulaRequest;
import cl.cbrs.wscomercio.ws.request.IngresaCaratulaFlujoRequest;
import cl.cbrs.wscomercio.ws.request.MoverCaratulaRequest;
import cl.cbrs.wscomercio.ws.response.ActualizarFojaCaratulaResponse;
import cl.cbrs.wscomercio.ws.response.IngresaCaratulaFlujoResponse;
import cl.cbrs.wscomercio.ws.response.MoverCaratulaResponse;
import cl.cbrs.wscomercio.ws.response.ObtenerWorkflowsResponse;

public class ComercioUtil {
	private static final Logger logger = Logger.getLogger(ComercioUtil.class);
	
	public ComercioUtil(){	
	}
	
	public ActualizarFojaCaratulaResponse actualizarFojaCaratula(InscripcionDigitalDTO inscripcionDigitalDTO, Long numeroCaratula) throws GeneralException, Exception{
		ServiciosComercioDelegate comercioDelegate = new ServiciosComercioDelegate();
		ActualizarFojaCaratulaResponse response = null;
		try {
			ActualizarFojaCaratulaRequest actualizarRequest = new ActualizarFojaCaratulaRequest();
			if(inscripcionDigitalDTO.getAno()!=null)
				actualizarRequest.setAno(inscripcionDigitalDTO.getAno().intValue());
			if(inscripcionDigitalDTO.getFoja()!=null)
				actualizarRequest.setFoja(inscripcionDigitalDTO.getFoja());
			actualizarRequest.setIdRequest("1");
			if(inscripcionDigitalDTO.getNumero()!=null)
				actualizarRequest.setNumero(inscripcionDigitalDTO.getNumero());
			actualizarRequest.setNumeroCaratula(numeroCaratula);
		
			response = comercioDelegate.actualizarFojaCaratula(actualizarRequest );
			
			if(response.getEstadoTransaccion()==null || 
					response.getEstadoTransaccion().getEstado()==null || 
					response.getEstadoTransaccion().getEstado().equalsIgnoreCase("BAD")){
						throw new GeneralException(response.getEstadoTransaccion().getCodigo());
			}
		} catch (RemoteException e) {			
			logger.error(e);
			throw new Exception(e);
		} 
		
		return response;
	}
	
	public MoverCaratulaResponse moverCaratula(EstadoCaratulaVO estadoCaratulaVO, Long numeroCaratula) throws GeneralException, Exception{
		ServiciosComercioDelegate comercioDelegate = new ServiciosComercioDelegate();
		MoverCaratulaResponse response = null;
		
		try {
			MoverCaratulaRequest moverCaratulaRequest = new MoverCaratulaRequest();				
			moverCaratulaRequest.setEstadoCaratulaVO(estadoCaratulaVO );
			moverCaratulaRequest.setIdRequest("2");
			moverCaratulaRequest.setNumeroCaratula(numeroCaratula);

			response = comercioDelegate.moverCaratula(moverCaratulaRequest);
			
			if(response.getEstadoTransaccion()==null || 
					response.getEstadoTransaccion().getEstado()==null || 
					response.getEstadoTransaccion().getEstado().equalsIgnoreCase("BAD")){
						throw new GeneralException(response.getEstadoTransaccion().getCodigo());
			}
		} catch (RemoteException e) {
			logger.error(e);
			throw new Exception(e);
		}
		
		return response;
	}
	
	public IngresaCaratulaFlujoResponse ingresaCaratulaFlujo(Long numeroCaratula, WorkflowVO workflowVO, FojaCaratulaVO fojaCaratulaVO, String observacion, String codigoExtracto, Long idNotario) throws GeneralException, Exception{
		ServiciosComercioDelegate comercioDelegate = new ServiciosComercioDelegate();
		IngresaCaratulaFlujoResponse response = null;
		
		try {
			IngresaCaratulaFlujoRequest request = new IngresaCaratulaFlujoRequest();				
			request.setFojaCaratulaVO(fojaCaratulaVO);
			
			request.setIdRequest("2");
			request.setNumeroCaratula(numeroCaratula);
			request.setObservacion(observacion);
			
			if(workflowVO!=null)
				request.setWorkflowVO(workflowVO);
			if(codigoExtracto!=null)
				request.setCodigoExtracto(codigoExtracto);
			if(idNotario!=null)
				request.setIdNotario(idNotario);

			response = comercioDelegate.ingresaCaratulaFlujo(request);
			
			if(response.getEstadoTransaccion()==null || 
					response.getEstadoTransaccion().getEstado()==null || 
					response.getEstadoTransaccion().getEstado().equalsIgnoreCase("BAD")){
						throw new GeneralException(response.getEstadoTransaccion().getCodigo());
			}
		} catch (RemoteException e) {
			logger.error(e);
			throw new Exception(e);
		}
		
		return response;
	}
	
	public WorkflowVO[] getWorkflows() throws GeneralException, Exception{
		ServiciosComercioDelegate comercioDelegate = new ServiciosComercioDelegate();
		ObtenerWorkflowsResponse response = null;
		WorkflowVO[] workflowVOs = null;
		
		try {
			response = comercioDelegate.obtenerWorkFlows();
			workflowVOs = response.getWorkflowVOs();
			
			if(response.getEstadoTransaccion()==null || 
					response.getEstadoTransaccion().getEstado()==null || 
					response.getEstadoTransaccion().getEstado().equalsIgnoreCase("BAD")){
						throw new GeneralException(response.getEstadoTransaccion().getCodigo());
			}
		} catch (RemoteException e) {
			logger.error(e);
			throw new Exception(e);
		}
		
		return workflowVOs;
	}	
}