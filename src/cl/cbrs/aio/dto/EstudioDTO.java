package cl.cbrs.aio.dto;

import java.sql.Timestamp;

public class EstudioDTO {
	
	private Long idEstudio;
	private Long idDocumento;
	private String usuario;
	private Timestamp fecha;
	private int idReg;
	private String xfdf;
	private Boolean valid;
	private byte[] fdf;
	
	public EstudioDTO(){
		
	}
	
	public Long getIdEstudio() {
		return idEstudio;
	}
	public void setIdEstudio(Long idEstudio) {
		this.idEstudio = idEstudio;
	}
	public Long getIdDocumento() {
		return idDocumento;
	}
	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Timestamp getFecha() {
		return fecha;
	}
	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}
	public int getIdReg() {
		return idReg;
	}
	public void setIdReg(int idReg) {
		this.idReg = idReg;
	}

	public String getXfdf() {
		return xfdf;
	}

	public void setXfdf(String xfdf) {
		this.xfdf = xfdf;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public byte[] getFdf() {
		return fdf;
	}

	public void setFdf(byte[] fdf) {
		this.fdf = fdf;
	}
	
	
	

}
