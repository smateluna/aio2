package cl.cbrs.aio.util;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.AnexoDTO;
import cl.cbrs.anexos.vo.AnexoVO;
import cl.cbrs.anexos.ws.ServicioAnexosDelegate;
import cl.cbrs.anexos.ws.request.EditarAnexoRequest;
import cl.cbrs.anexos.ws.request.ObtenerAnexosRequest;
import cl.cbrs.anexos.ws.response.EditarAnexoResponse;
import cl.cbrs.anexos.ws.response.ObtenerAnexosResponse;

public class AnexosUtil {

	public AnexosUtil(){
		
	}

	public ArrayList<AnexoVO> getAnexosVO(){
		ArrayList<AnexoVO> anexos = new ArrayList<AnexoVO>();
		ServicioAnexosDelegate delegate = new ServicioAnexosDelegate();
		ObtenerAnexosResponse obtenerAnexosResponse = delegate.obtenerAnexos(new ObtenerAnexosRequest("1") );
		AnexoVO[] anexoVOs = obtenerAnexosResponse.getAnexos();

		for(AnexoVO anexoVO: anexoVOs){
				anexos.add(anexoVO);			
		}	
		return anexos;
	}
	
	public ArrayList<AnexoDTO> getAnexosDTO(){
		ArrayList<AnexoDTO> anexos = new ArrayList<AnexoDTO>();
		ServicioAnexosDelegate delegate = new ServicioAnexosDelegate();
		ObtenerAnexosResponse obtenerAnexosResponse = delegate.obtenerAnexos(new ObtenerAnexosRequest("1") );
		AnexoVO[] anexoVOs = obtenerAnexosResponse.getAnexos();

		for(AnexoVO anexoVO: anexoVOs){
				AnexoDTO anexoDTO = getAnexoDTOfromVO(anexoVO);
				anexos.add(anexoDTO);			
		}	
		return anexos;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getAnexosJSONArray(){
		JSONArray anexos = new JSONArray();
		ArrayList<AnexoDTO> anexoDTOList = getAnexosDTO();
		
		if(anexoDTOList!=null && anexoDTOList.size()>0){
			
			for(AnexoDTO anexoDTO: anexoDTOList){
				
				anexos.add(anexoDTO);
				
				if(anexos.size()>20)break;
			}
		}		
		
		return anexos;
	}

	private AnexoDTO getAnexoDTOfromVO(AnexoVO anexoVO) {
		AnexoDTO anexoDTO = new AnexoDTO();
		
		anexoDTO.setAnexo(anexoVO.getAnexo());
		anexoDTO.setApMaterno(anexoVO.getApMaterno());
		anexoDTO.setApPaterno(anexoVO.getApPaterno());
		anexoDTO.setCodSeccion(anexoVO.getCodSeccion());
		anexoDTO.setCorreo(anexoVO.getCorreo());
		anexoDTO.setIdAnexo(anexoVO.getIdAnexo());
		anexoDTO.setNombre(anexoVO.getNombre());
		anexoDTO.setPiso(anexoVO.getPiso());
		anexoDTO.setRegistro(anexoVO.getRegistro());
		anexoDTO.setRut(anexoVO.getRut());
		
		return anexoDTO;
	}
	
	private AnexoVO getAnexoVOfromJSON(JSONObject anexoJSON) {
		AnexoVO anexoVO = new AnexoVO();
		
		anexoVO.setAnexo((String)anexoJSON.get("anexo"));
		anexoVO.setApMaterno((String)anexoJSON.get("apMaterno"));
		anexoVO.setApPaterno((String)anexoJSON.get("apPaterno"));
		anexoVO.setCodSeccion((String)anexoJSON.get("codSeccion"));
		anexoVO.setCorreo((String)anexoJSON.get("correo"));
		anexoVO.setIdAnexo((Long)anexoJSON.get("idAnexo"));
		anexoVO.setNombre((String)anexoJSON.get("nombre"));
		anexoVO.setPiso((String)anexoJSON.get("piso"));
		anexoVO.setRegistro((String)anexoJSON.get("registro"));
		anexoVO.setRut((String)anexoJSON.get("rut"));
		
		return anexoVO;
	}	
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayAnexoDTO(ArrayList<AnexoDTO> anexoDTOList){
		JSONArray anexos = new JSONArray();
		
		if(anexoDTOList!=null && anexoDTOList.size()>0){
			
			for(AnexoDTO anexoDTO: anexoDTOList){
				
				anexos.add(anexoDTO);
			}
		}		
		
		return anexos;
	}
	
	public String editarAnexo(Object anexoJSON) throws Exception {	
		String response = null;
		AnexoVO anexoVO = getAnexoVOfromJSON((JSONObject)anexoJSON);
		ServicioAnexosDelegate delegate = new ServicioAnexosDelegate();
		EditarAnexoRequest requestInicial = new EditarAnexoRequest("1", anexoVO);
		EditarAnexoResponse editarAnexoResponse = delegate.editarAnexo(requestInicial );
		if(editarAnexoResponse.getEstadoTransaccion()!=null)
			response = editarAnexoResponse.getEstadoTransaccion().getEstado();
		
		return response;

		
	}	
}