package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class SeccionDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 8088781176833361517L;
	
	private String codigo;
	private String descripcion;
	
	public SeccionDTO() {
	}

	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"codigo\"");
		sb.append(":");

		if(this.codigo!=null){
			sb.append("\"" + JSONObject.escape(this.codigo.trim()) + "\"");			
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


	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}