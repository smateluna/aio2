package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class FormularioDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 7364851986560214369L;
	
	private String tipo;
	private String descripcion;
	
	public FormularioDTO() {
	}

	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"tipo\"");
		sb.append(":");

		if(this.tipo!=null){
			sb.append("\"" + JSONObject.escape(this.tipo.trim()) + "\"");			
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


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}