package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoActualDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private SeccionDTO seccionDTO;
	private Long fechaL;
	private Date fecha;
	private String descripcionEnFlujo;
		
	public EstadoActualDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		

		sb.append("\"seccionDTO\"");
		sb.append(":");

		if(this.seccionDTO!=null){
			sb.append(this.seccionDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");
		
		sb.append("\"fechaL\"");
		sb.append(":");
		sb.append(this.fechaL);	
		sb.append(",");	
		
		sb.append("\"fecha\"");
		sb.append(":");
		if(this.fecha!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fecha)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"descripcionEnFlujo\"");
		sb.append(":");
		
		if(this.descripcionEnFlujo!=null){
			sb.append("\"" + JSONObject.escape(this.descripcionEnFlujo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public SeccionDTO getSeccionDTO() {
		return seccionDTO;
	}

	public void setSeccionDTO(SeccionDTO seccionDTO) {
		this.seccionDTO = seccionDTO;
	}

	public String getDescripcionEnFlujo() {
		return descripcionEnFlujo;
	}

	public void setDescripcionEnFlujo(String descripcionEnFlujo) {
		this.descripcionEnFlujo = descripcionEnFlujo;
	}

	public Long getFechaL() {
		return fechaL;
	}

	public void setFechaL(Long fechaL) {
		this.fechaL = fechaL;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}