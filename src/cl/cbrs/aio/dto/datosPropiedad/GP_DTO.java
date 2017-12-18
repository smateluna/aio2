package cl.cbrs.aio.dto.datosPropiedad;

import java.sql.Timestamp;

public class GP_DTO {
	private Timestamp fechaGP;
	private Integer caratula;
	private String nombreArchivoVersion;
	private String codArchivoAlpha;
	
	public Timestamp getFechaGP() {
		return fechaGP;
	}
	public void setFechaGP(Timestamp fechaGP) {
		this.fechaGP = fechaGP;
	}
	public Integer getCaratula() {
		return caratula;
	}
	public void setCaratula(Integer caratula) {
		this.caratula = caratula;
	}
	public String getCodArchivoAlpha() {
		return codArchivoAlpha;
	}
	public void setCodArchivoAlpha(String codArchivoAlpha) {
		this.codArchivoAlpha = codArchivoAlpha;
	}
	public String getNombreArchivoVersion() {
		return nombreArchivoVersion;
	}
	public void setNombreArchivoVersion(String nombreArchivoVersion) {
		this.nombreArchivoVersion = nombreArchivoVersion;
	}

}
