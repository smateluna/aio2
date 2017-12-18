package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoBitacoraDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5132396717203205879L;
	
	private Long idEstado;
	private String descripcion;
	private Integer tipo;
	
	public EstadoBitacoraDTO(){
		
	}
	
	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
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
		
		sb.append(",");	
		
		sb.append("\"tipo\"");
		sb.append(":");
		
		sb.append(this.tipo);

		sb.append("}");

		return sb.toString();		
	}
}