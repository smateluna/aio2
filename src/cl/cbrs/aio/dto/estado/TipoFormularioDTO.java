package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TipoFormularioDTO implements JSONAware{
	
	private Integer id;
	private String descripcion;
	
	public TipoFormularioDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");

		sb.append(this.id);

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}