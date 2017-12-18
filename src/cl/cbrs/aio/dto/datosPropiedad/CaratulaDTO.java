package cl.cbrs.aio.dto.datosPropiedad;

import java.sql.Timestamp;

public class CaratulaDTO {
	private Integer caratula;
	private Timestamp fechaEnvio;
	
	public Integer getCaratula() {
		return caratula;
	}
	public void setCaratula(Integer caratula) {
		this.caratula = caratula;
	}
	public Timestamp getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(Timestamp fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	
	
}
