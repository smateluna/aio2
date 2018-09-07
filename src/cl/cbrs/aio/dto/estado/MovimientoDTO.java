package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class MovimientoDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private Long fechaL;	
	private Date fecha;
	private SeccionDTO seccionDTO;
	private FuncionarioDTO responsable;
	private FuncionarioDTO envia;
	
	public MovimientoDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	

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

		sb.append("\"seccionDTO\"");
		sb.append(":");

		if(this.seccionDTO!=null){
			sb.append(this.seccionDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");
		
		sb.append("\"responsable\"");
		sb.append(":");
		
		if(this.responsable!=null){
			sb.append(this.responsable.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"envia\"");
		sb.append(":");
		
		if(this.envia!=null){
			sb.append(this.envia.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Long getFechaL() {
		return fechaL;
	}

	public void setFechaL(Long fecha) {
		this.fechaL = fecha;
	}

	public SeccionDTO getSeccionDTO() {
		return seccionDTO;
	}

	public void setSeccionDTO(SeccionDTO seccionDTO) {
		this.seccionDTO = seccionDTO;
	}

	public FuncionarioDTO getResponsable() {
		return responsable;
	}

	public void setResponsable(FuncionarioDTO responsable) {
		this.responsable = responsable;
	}

	public FuncionarioDTO getEnvia() {
		return envia;
	}

	public void setEnvia(FuncionarioDTO envia) {
		this.envia = envia;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}