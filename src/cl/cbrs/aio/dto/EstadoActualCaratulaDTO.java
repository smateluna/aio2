package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoActualCaratulaDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = -349462565989439452L;
	
	private String fechaMov;
	private Long fechaMovL;
	private SeccionDTO seccionDTO;
	private String descripcionEnFlujo;
	
	public EstadoActualCaratulaDTO() {
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"fechaMovL\"");
		sb.append(":");

		sb.append(this.fechaMovL);

		sb.append(",");
		
		sb.append("\"fechaMov\"");
		sb.append(":");

		if(this.fechaMov!=null){
			sb.append("\"" + JSONObject.escape(this.fechaMov.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append(",");
		
		sb.append("\"descripcionEnFlujo\"");
		sb.append(":");
		if(this.descripcionEnFlujo!=null)
			sb.append("\"" + JSONObject.escape(this.descripcionEnFlujo.trim()) + "\"");
		else
			sb.append("\"\"");	
		sb.append(",");		
		
		sb.append("\"seccionDTO\"");
		sb.append(":");
		if(this.seccionDTO!=null)
			sb.append(this.seccionDTO.toJSONString());
		else
			sb.append("\"\"");
		sb.append("}");

		return sb.toString();		
	}

	public SeccionDTO getSeccionDTO() {
		return seccionDTO;
	}

	public void setSeccionDTO(SeccionDTO seccionDTO) {
		this.seccionDTO = seccionDTO;
	}

	public String getFechaMov() {
		return fechaMov;
	}

	public void setFechaMov(String fechaMov) {
		this.fechaMov = fechaMov;
	}

	public Long getFechaMovL() {
		return fechaMovL;
	}

	public void setFechaMovL(Long fechaMovL) {
		this.fechaMovL = fechaMovL;
	}

	public String getDescripcionEnFlujo() {
		return descripcionEnFlujo;
	}

	public void setDescripcionEnFlujo(String descripcionEnFlujo) {
		this.descripcionEnFlujo = descripcionEnFlujo;
	}
}