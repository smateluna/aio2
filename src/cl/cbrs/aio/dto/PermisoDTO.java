package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class PermisoDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 6128342195177333572L;
	
	private String id;
	private String titulo;
	private String path;
	private String icono;
	private String estilo;
	private ArrayList<SubPermisoDTO> subOpciones;
	private ArrayList<String> subPermisos;
		
	public PermisoDTO(){

	}

	@SuppressWarnings("unchecked")
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
		sb.append(",");
		
		sb.append("\"estilo\"");
		sb.append(":");
		if(this.estilo!=null){
			sb.append("\"" + JSONObject.escape(this.estilo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		sb.append(",");		

		JSONArray subOpcionesJSONArray = new JSONArray();		
		if(this.subOpciones!=null){
			for(SubPermisoDTO subpermisoDTO : this.subOpciones){				
				subOpcionesJSONArray.add(subpermisoDTO);
			}			
		}
		sb.append("\"subOpcionesDTO\"");
		sb.append(":");		
		sb.append(subOpcionesJSONArray);
		sb.append(",");
		 
		JSONArray subPermisosJSONArray = new JSONArray();		
		if(this.subPermisos!=null){
			for(String permiso : this.subPermisos){				
				subPermisosJSONArray.add(permiso);
			}			
		}
		sb.append("\"subPermisos\"");
		sb.append(":");		
		sb.append(subPermisosJSONArray);
		
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

	public ArrayList<SubPermisoDTO> getSubOpciones() {
		return subOpciones;
	}

	public void setSubOpciones(ArrayList<SubPermisoDTO> subOpciones) {
		this.subOpciones = subOpciones;
	}

	public ArrayList<String> getSubPermisos() {
		return subPermisos;
	}

	public void setSubPermisos(ArrayList<String> subPermisos) {
		this.subPermisos = subPermisos;
	}

	public String getEstilo() {
		return estilo;
	}

	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}

	
	
}