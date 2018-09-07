package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TipoArchivoDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = -4524577334346175489L;
	
	private Integer tipoArchivo;
	private String descripcion;
		
	public TipoArchivoDTO(){

	}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"tipoArchivo\"");
		sb.append(":");

		sb.append(this.tipoArchivo);
		
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

	public Integer getTipoArchivo() {
		return tipoArchivo;
	}

	public void setTipoArchivo(Integer tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}