package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class SubPermisoDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 2187799358804268813L;
	
	private String id;
	private String titulo;
	private String path;
	private String icono;
		
	public SubPermisoDTO(){

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
	
		sb.append("\"titulo\"");
		sb.append(":");
		if(this.titulo!=null){
			sb.append("\"" + JSONObject.escape(this.titulo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		sb.append(",");
		
		sb.append("\"path\"");
		sb.append(":");
		if(this.path!=null){
			sb.append("\"" + JSONObject.escape(this.path.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		sb.append(",");
		
		sb.append("\"icono\"");
		sb.append(":");
		if(this.icono!=null){
			sb.append("\"" + JSONObject.escape(this.icono.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append("}");

		return sb.toString();		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}	
	
}