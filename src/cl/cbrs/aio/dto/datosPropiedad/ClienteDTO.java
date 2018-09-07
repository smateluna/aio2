package cl.cbrs.aio.dto.datosPropiedad;

public class ClienteDTO {
	
	private String tipoCliente = "";
	private String rut = "";
	private String apPaternoCli = "";
	private String apMaternoCli = "";
	private String nombreCli = "";
	private String nombres = "";
	private boolean vigente;
	
	public String getTipoCliente() {
		return tipoCliente;
	}
	public void setTipoCliente(String tipoCliente) {
		this.tipoCliente = tipoCliente;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(String rut) {
		this.rut = rut;
	}
	public String getApPaternoCli() {
		return apPaternoCli;
	}
	public void setApPaternoCli(String apPaternoCli) {
		this.apPaternoCli = apPaternoCli;
	}
	public String getApMaternoCli() {
		return apMaternoCli;
	}
	public void setApMaternoCli(String apMaternoCli) {
		this.apMaternoCli = apMaternoCli;
	}
	public String getNombreCli() {
		return nombreCli;
	}
	public void setNombreCli(String nombreCli) {
		this.nombreCli = nombreCli;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public boolean isVigente() {
		return vigente;
	}
	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

}
