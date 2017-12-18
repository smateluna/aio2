package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class BodegaDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 7962658581868829169L;
	
	private String descripcion;
	private Long idBodega;
	private ArrayList<AnoBodegaDTO> listaAnoBodegaDTO;
	private ArrayList<SolicitudDTO> listaSolicitudDTO;
		
	public BodegaDTO(){

	}

	@SuppressWarnings("unchecked")
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"idBodega\"");
		sb.append(":");

		sb.append(this.idBodega);
		
		sb.append(",");
		
		sb.append("\"descripcion\"");
		sb.append(":");

		if(this.descripcion!=null){
			sb.append("\"" + JSONObject.escape(this.descripcion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append(",");
	
		JSONArray listaAnoBodegaJSONArray = new JSONArray();
		
		if(this.listaAnoBodegaDTO!=null){
			for(AnoBodegaDTO anoBodegaDTO : this.listaAnoBodegaDTO){				
				listaAnoBodegaJSONArray.add(anoBodegaDTO);
			}			
		}

		sb.append("\"listaAnoBodegaDTO\"");
		sb.append(":");
		
		sb.append(listaAnoBodegaJSONArray);

		sb.append(",");

		JSONArray listaSolicitudJSONArray = new JSONArray();
		
		if(this.listaSolicitudDTO!=null){
			for(SolicitudDTO solicitudDTO : this.listaSolicitudDTO){				
				listaSolicitudJSONArray.add(solicitudDTO);
			}			
		}

		sb.append("\"listaSolicitudDTO\"");
		sb.append(":");
		
		sb.append(listaSolicitudJSONArray);

		sb.append("}");

		return sb.toString();		
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public ArrayList<AnoBodegaDTO> getListaAnoBodegaDTO() {
		return listaAnoBodegaDTO;
	}

	public void setListaAnoBodegaDTO(ArrayList<AnoBodegaDTO> listaAnoBodegaDTO) {
		this.listaAnoBodegaDTO = listaAnoBodegaDTO;
	}

	public ArrayList<SolicitudDTO> getListaSolicitudDTO() {
		return listaSolicitudDTO;
	}

	public void setListaSolicitudDTO(ArrayList<SolicitudDTO> listaSolicitudDTO) {
		this.listaSolicitudDTO = listaSolicitudDTO;
	}

	public Long getIdBodega() {
		return idBodega;
	}

	public void setIdBodega(Long idBodega) {
		this.idBodega = idBodega;
	}
}