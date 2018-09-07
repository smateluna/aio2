package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class BitacoraDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = -7740236202572224989L;
	
	private EstadoBitacoraDTO estadoBitacoraDTO;
	private Date fecha;
	private Long idBitacora;
	private Long idUsuario;
	private InscripcionDigitalDTO inscripcionDigitalDTO;
	
	private String nombreUsuario;
	
	public BitacoraDTO(){
		
	}

	public EstadoBitacoraDTO getEstadoBitacoraDTO() {
		return estadoBitacoraDTO;
	}

	public void setEstadoBitacoraDTO(EstadoBitacoraDTO estadoBitacoraDTO) {
		this.estadoBitacoraDTO = estadoBitacoraDTO;
	}


	public Long getIdBitacora() {
		return idBitacora;
	}

	public void setIdBitacora(Long idBitacora) {
		this.idBitacora = idBitacora;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public InscripcionDigitalDTO getInscripcionDigitalDTO() {
		return inscripcionDigitalDTO;
	}

	public void setInscripcionDigitalDTO(InscripcionDigitalDTO inscripcionDigitalDTO) {
		this.inscripcionDigitalDTO = inscripcionDigitalDTO;
	}
	
	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

		String d = "";

		if(this.fecha!=null){
			//Date fechaDate = this.fecha.getTime();

			d = sdf.format(this.fecha);
		}

		sb.append("{");	
		
		sb.append("\"idBitacora\"");
		sb.append(":");

		sb.append(this.idBitacora);

		sb.append(",");	

		sb.append("\"fecha\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(d) + "\"");	
		
		sb.append(",");	

		sb.append("\"idUsuario\"");
		sb.append(":");

		sb.append(this.idUsuario);

		sb.append(",");
		
		sb.append("\"estadoBitacoraDTO\"");
		sb.append(":");
		
		if(this.estadoBitacoraDTO!=null){
			sb.append(this.estadoBitacoraDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"nombreUsuario\"");
		sb.append(":");

		if(this.nombreUsuario!=null){
			sb.append("\"" + JSONObject.escape(this.nombreUsuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
}