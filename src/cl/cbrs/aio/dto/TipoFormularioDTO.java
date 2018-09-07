package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TipoFormularioDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5400235850784772801L;
	
	private Integer id;
	private String descripcion;
	private String idDescripcion;
	
	public TipoFormularioDTO() {
	}

	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");
		sb.append(this.id);		
		sb.append(",");
		
		sb.append("\"idDescripcion\"");
		sb.append(":");
		if(this.idDescripcion!=null){
			sb.append("\"" + JSONObject.escape(this.idDescripcion.trim()) + "\"");				
		}else{
			sb.append("\"\"");				
		}	
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



	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getIdDescripcion() {
		return idDescripcion;
	}


	public void setIdDescripcion(String idDescripcion) {
		this.idDescripcion = idDescripcion;
	}
}