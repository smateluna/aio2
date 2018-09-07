package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class SeccionDTO implements JSONAware{
	
	private String id;
	private String descripcion;
	
	public SeccionDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");

		if(this.id!=null){
			sb.append("\"" + JSONObject.escape(this.id.trim()) + "\"");			
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}