package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TrazaSolicitudDTO implements JSONAware, Serializable{

	private static final long serialVersionUID = -8729567377728589107L;
	
	private Long idTraza;	  
	private SolicitudDTO solicitudDTO;	  
	private Long idEstado;  
	private Date fechaEstado;	

	public TrazaSolicitudDTO(){

	}

	public String toJSONString(){		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

		String fechaEstadoS = "";

		if(this.fechaEstado!=null){
			fechaEstadoS = sdf.format(this.fechaEstado);
		}		

		sb.append("{");	

		sb.append("\"idTraza\"");
		sb.append(":");

		sb.append(this.idTraza);

		sb.append(",");
		
		sb.append("\"solicitudDTO\"");
		sb.append(":");

		if(this.solicitudDTO!=null){
			sb.append(this.solicitudDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");

		sb.append("\"idEstado\"");
		sb.append(":");

		sb.append(this.idEstado);

		sb.append(",");	
		
		sb.append("\"fechaEstado\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(fechaEstadoS) + "\"");	

		sb.append("}");

		return sb.toString();		
	}

	public Long getIdTraza() {
		return idTraza;
	}

	public void setIdTraza(Long idTraza) {
		this.idTraza = idTraza;
	}

	public SolicitudDTO getSolicitudDTO() {
		return solicitudDTO;
	}

	public void setSolicitudDTO(SolicitudDTO solicitudDTO) {
		this.solicitudDTO = solicitudDTO;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}
}