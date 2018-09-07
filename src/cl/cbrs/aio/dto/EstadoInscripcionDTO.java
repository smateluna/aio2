package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoInscripcionDTO implements JSONAware, Serializable{
	private static final long serialVersionUID = -4962484754751231886L;
	
	private Integer idEstadoInscripcion;
	private String descripcion;

	public EstadoInscripcionDTO(){}
	
	public EstadoInscripcionDTO(Integer idEstadoInscripcion, String descripcion) {
		this.idEstadoInscripcion = idEstadoInscripcion;
		this.descripcion = descripcion;
	}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	

		sb.append("\"idEstadoInscripcion\"");
		sb.append(":");

		sb.append(this.idEstadoInscripcion);

		sb.append(",");

		sb.append("\"descripcion\"");
		sb.append(":");

		if(this.descripcion!=null){
			sb.append("\"" + JSONObject.escape(descripcion) + "\"");				
		}else{
			sb.append("\"\"");				
		}		

		sb.append("}");

		return sb.toString();		
	}


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getIdEstadoInscripcion() {
		return idEstadoInscripcion;
	}

	public void setIdEstadoInscripcion(Integer idEstadoInscripcion) {
		this.idEstadoInscripcion = idEstadoInscripcion;
	}
}