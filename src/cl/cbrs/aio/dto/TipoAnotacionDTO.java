package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;


public class TipoAnotacionDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 2062064934790445567L;
	
	private Long idTipoAnotacion;
	private String descripcion;
	
	public TipoAnotacionDTO(){
		
	}

	public Long getIdTipoAnotacion() {
		return idTipoAnotacion;
	}

	public void setIdTipoAnotacion(Long idTipoAnotacion) {
		this.idTipoAnotacion = idTipoAnotacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String toJSONString(){	
		StringBuffer sb = new StringBuffer();

		sb.append("{");	

		sb.append("\"idTipoAnotacion\"");
		sb.append(":");
		
		sb.append(this.idTipoAnotacion);
		
		sb.append(",");	

		sb.append("\"descripcion\"");
		sb.append(":");

		if(this.descripcion!=null){
			sb.append("\"" + JSONObject.escape(this.descripcion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}
}