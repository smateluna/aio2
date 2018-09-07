package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoCertificadoDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 5683987476672533197L;
	
	private Integer idEstado;
	private String descripcion;
		
	public EstadoCertificadoDTO(){

	}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"idEstado\"");
		sb.append(":");

		sb.append(this.idEstado);
		
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

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}