package cl.cbrs.aio.dto;

import java.sql.Timestamp;

public class DocumentoDTO {
	
	private Long idDocumento;
	private Long idTipoDocumento;
	private Long idReg;
	private String nombreArchivo;
	private String nombreArchivoVersion;
	private Long version;
	private Long indice;
	private Boolean vigente;
	private Timestamp fechaProcesa;
	private Long caratula;
	private String codFirmaE;
	private Timestamp fechaDigitaliza;
	
	public DocumentoDTO(){
		
	}

	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public Long getIdReg() {
		return idReg;
	}

	public void setIdReg(Long idReg) {
		this.idReg = idReg;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getNombreArchivoVersion() {
		return nombreArchivoVersion;
	}

	public void setNombreArchivoVersion(String nombreArchivoVersion) {
		this.nombreArchivoVersion = nombreArchivoVersion;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getIndice() {
		return indice;
	}

	public void setIndice(Long indice) {
		this.indice = indice;
	}

	public Boolean getVigente() {
		return vigente;
	}

	public void setVigente(Boolean vigente) {
		this.vigente = vigente;
	}

	public Timestamp getFechaProcesa() {
		return fechaProcesa;
	}

	public void setFechaProcesa(Timestamp fechaProcesa) {
		this.fechaProcesa = fechaProcesa;
	}

	public Long getCaratula() {
		return caratula;
	}

	public void setCaratula(Long caratula) {
		this.caratula = caratula;
	}

	public String getCodFirmaE() {
		return codFirmaE;
	}

	public void setCodFirmaE(String codFirmaE) {
		this.codFirmaE = codFirmaE;
	}

	public Timestamp getFechaDigitaliza() {
		return fechaDigitaliza;
	}

	public void setFechaDigitaliza(Timestamp fechaDigitaliza) {
		this.fechaDigitaliza = fechaDigitaliza;
	}

}
