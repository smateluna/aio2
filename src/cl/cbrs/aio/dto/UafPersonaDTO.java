package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class UafPersonaDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5607535884631474352L;
	
	private String nombre;
	private String rut;
	private String caratula;
	
	public UafPersonaDTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		

		sb.append("\"caratula\"");
		sb.append(":");
		if(this.caratula!=null)
			sb.append("\"" + JSONObject.escape(this.caratula.trim()) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");
	
		
		sb.append("\"nombre\"");
		sb.append(":");
		if(this.nombre!=null)
			sb.append("\"" + JSONObject.escape(this.nombre.trim()) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");

		sb.append("\"rut\"");
		sb.append(":");
		if(this.rut!=null)
			sb.append("\"" + JSONObject.escape(this.rut.trim()) + "\"");
		else
			sb.append("\"\"");	
		
		sb.append("}");

		return sb.toString();		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getCaratula() {
		return caratula;
	}

	public void setCaratula(String caratula) {
		this.caratula = caratula;
	}


	

}