package cl.cbrs.aio.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;

import cl.cbrs.aio.dto.FormularioDTO;
import cl.cbrs.aio.dto.SeccionDTO;
import cl.cbrs.aio.dto.estado.NaturalezaDTO;
import cl.cbrs.aio.dto.estado.TareaDTO;
import cl.cbrs.parametros.vo.FormularioVO;
import cl.cbrs.parametros.vo.NaturalezaVO;
import cl.cbrs.parametros.vo.SeccionVO;
import cl.cbrs.parametros.vo.TareaVO;
import cl.cbrs.parametros.ws.ServicioParametrosDelegate;
import cl.cbrs.parametros.ws.request.ObtenerFormulariosRequest;
import cl.cbrs.parametros.ws.request.ObtenerNaturalezasRequest;
import cl.cbrs.parametros.ws.request.ObtenerSeccionesRequest;
import cl.cbrs.parametros.ws.request.ObtenerTareasRequest;
import cl.cbrs.parametros.ws.response.ObtenerFormulariosResponse;
import cl.cbrs.parametros.ws.response.ObtenerNaturalezasResponse;
import cl.cbrs.parametros.ws.response.ObtenerSeccionesResponse;
import cl.cbrs.parametros.ws.response.ObtenerTareasResponse;

public class ParametrosUtil {

	public ParametrosUtil(){
		
	}

	public ArrayList<SeccionVO> getSeccionesVO() throws Exception{
		ArrayList<SeccionVO> secciones = new ArrayList<SeccionVO>();
		ServicioParametrosDelegate delegate = new ServicioParametrosDelegate();
		ObtenerSeccionesResponse obtenerSeccionesResponse = delegate.obtenerSecciones(new ObtenerSeccionesRequest("1"));
		SeccionVO[] seccionVOs = obtenerSeccionesResponse.getSecciones();

		for(SeccionVO seccionVO: seccionVOs){
			secciones.add(seccionVO);			
		}	
		return secciones;
	}
	
	public ArrayList<SeccionDTO> getSeccionesDTO() throws Exception{
		ArrayList<SeccionDTO> secciones = new ArrayList<SeccionDTO>();
		ServicioParametrosDelegate delegate = new ServicioParametrosDelegate();
		ObtenerSeccionesResponse obtenerSeccionesResponse = delegate.obtenerSecciones(new ObtenerSeccionesRequest("1"));
		SeccionVO[] seccionVOs = obtenerSeccionesResponse.getSecciones();

		for(SeccionVO seccionVO: seccionVOs){
			SeccionDTO seccionDTO = getSeccionDTOfromVO(seccionVO);
			secciones.add(seccionDTO);			
		}	
		return secciones;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getSeccionesJSONArray() throws Exception{
		JSONArray secciones = new JSONArray();
		ArrayList<SeccionDTO> seccionDTOList = getSeccionesDTO();
		
		if(seccionDTOList!=null && seccionDTOList.size()>0){
			
			for(SeccionDTO seccionDTO: seccionDTOList){
				
				secciones.add(seccionDTO);
			}
		}		
		
		return secciones;
	}

	private SeccionDTO getSeccionDTOfromVO(SeccionVO seccionVO) {
		SeccionDTO seccionDTO = new SeccionDTO();
		
		seccionDTO.setCodigo(seccionVO.getCodigo());
		seccionDTO.setDescripcion(seccionVO.getDescripcion());
		
		return seccionDTO;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArraySeccionDTO(ArrayList<SeccionDTO> seccionDTOList){
		JSONArray secciones = new JSONArray();
		
		if(seccionDTOList!=null && seccionDTOList.size()>0){
			
			for(SeccionDTO seccionDTO: seccionDTOList){
				
				secciones.add(seccionDTO);
			}
		}		
		
		return secciones;
	}	
	
	public ArrayList<FormularioDTO> getFormulariosDTO() throws Exception{
		ArrayList<FormularioDTO> formularios = new ArrayList<FormularioDTO>();
		ServicioParametrosDelegate delegate = new ServicioParametrosDelegate();
		ObtenerFormulariosResponse obtenerFormulariosResponse = delegate.obtenerFormularios(new ObtenerFormulariosRequest("1"));
		FormularioVO[] formularioVOs = obtenerFormulariosResponse.getFormularios();

		for(FormularioVO formularioVO: formularioVOs){
			FormularioDTO formularioDTO = getFormularioDTOfromVO(formularioVO);
			formularios.add(formularioDTO);			
		}	
		return formularios;
	}
	
	private FormularioDTO getFormularioDTOfromVO(FormularioVO formularioVO) {
		FormularioDTO formularioDTO = new FormularioDTO();
		
		formularioDTO.setTipo(formularioVO.getTipo());
		formularioDTO.setDescripcion(formularioVO.getDescripcion());
		
		return formularioDTO;
	}
	
	public ArrayList<TareaDTO> getTareasDTO() throws Exception{
		ArrayList<TareaDTO> tareaDTOs = new ArrayList<TareaDTO>();
		ServicioParametrosDelegate delegate = new ServicioParametrosDelegate();
		ObtenerTareasResponse obtenerFormulariosResponse = delegate.obtenerTareas(new ObtenerTareasRequest("1"));
		TareaVO[] tareaVOs = obtenerFormulariosResponse.getTareaVOs();

		for(TareaVO tareaVO: tareaVOs){
			TareaDTO tareaDTO = getTareaDTOfromVO(tareaVO);
			tareaDTOs.add(tareaDTO);			
		}	
		return tareaDTOs;
	}
	
	private TareaDTO getTareaDTOfromVO(TareaVO tareaVO) {
		TareaDTO tareaDTO = new TareaDTO();
		
		tareaDTO.setId(tareaVO.getCodigo().intValue());
		tareaDTO.setDescripcion(tareaVO.getDescripcion());
		
		return tareaDTO;
	}
	
	public ArrayList<NaturalezaDTO> getNaturalezasDTO() throws Exception{
		ArrayList<NaturalezaDTO> naturalezas = new ArrayList<NaturalezaDTO>();
		ServicioParametrosDelegate delegate = new ServicioParametrosDelegate();
		ObtenerNaturalezasResponse naturalezasResponse = delegate.obtenerNaturalezas(new ObtenerNaturalezasRequest("1"));
		NaturalezaVO[] naturalezaVOs = naturalezasResponse.getNaturalezas();

		for(NaturalezaVO naturalezaVO: naturalezaVOs){
			naturalezas.add(getNaturalezaDTOfromVO(naturalezaVO));			
		}
		
		return naturalezas;
	}

	private NaturalezaDTO getNaturalezaDTOfromVO(NaturalezaVO naturalezaVO) {
		NaturalezaDTO naturalezaDTO = new NaturalezaDTO();
		naturalezaDTO.setCodNaturaleza(naturalezaVO.getCodNaturaleza());
		naturalezaDTO.setDescNaturaleza(naturalezaVO.getDescNaturaleza());
		naturalezaDTO.setRegistro(naturalezaVO.getRegistro());
		naturalezaDTO.setTipo(naturalezaVO.getTipo());
		return naturalezaDTO;
	}
}