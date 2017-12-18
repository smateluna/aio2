package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = -3984761282604959898L;
	
	private String descripcion;
	private Long idEstado;
	private ArrayList<SolicitudDTO> listaSolicitudDTO;
		
	public EstadoDTO(){

	}

	@SuppressWarnings("unchecked")
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"idEstado\"");
		sb.append(":");

		sb.append(this.idEstado);
		
		sb.append(",");
		
		sb.append("\"descripcion\"");
		sb.append(":");

		if(this.descripcion!=null){
			sb.append("\"" + JSONObject.escape(this.descripcion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		

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


	public ArrayList<SolicitudDTO> getListaSolicitudDTO() {
		return listaSolicitudDTO;
	}

	public void setListaSolicitudDTO(ArrayList<SolicitudDTO> listaSolicitudDTO) {
		this.listaSolicitudDTO = listaSolicitudDTO;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}
}