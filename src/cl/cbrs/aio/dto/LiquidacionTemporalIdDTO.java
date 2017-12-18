package cl.cbrs.aio.dto;

public class LiquidacionTemporalIdDTO {
	
	private String fechaMov;
	private Integer caratula;
	private String codSeccion;
	private String rutFuncionario;
	private String maquina;
	private String rutEnvia;
	private Integer esCtaCte;
	private String usuarioLiquidador;
	
	public String getFechaMov() {
		return fechaMov;
	}
	public void setFechaMov(String fechaMov) {
		this.fechaMov = fechaMov;
	}
	public Integer getCaratula() {
		return caratula;
	}
	public void setCaratula(Integer caratula) {
		this.caratula = caratula;
	}
	public String getCodSeccion() {
		return codSeccion;
	}
	public void setCodSeccion(String codSeccion) {
		this.codSeccion = codSeccion;
	}
	public String getRutFuncionario() {
		return rutFuncionario;
	}
	public void setRutFuncionario(String rutFuncionario) {
		this.rutFuncionario = rutFuncionario;
	}
	public String getMaquina() {
		return maquina;
	}
	public void setMaquina(String maquina) {
		this.maquina = maquina;
	}
	public String getRutEnvia() {
		return rutEnvia;
	}
	public void setRutEnvia(String rutEnvia) {
		this.rutEnvia = rutEnvia;
	}
	public Integer getEsCtaCte() {
		return esCtaCte;
	}
	public void setEsCtaCte(Integer esCtaCte) {
		this.esCtaCte = esCtaCte;
	}
	public String getUsuarioLiquidador() {
		return usuarioLiquidador;
	}
	public void setUsuarioLiquidador(String usuarioLiquidador) {
		this.usuarioLiquidador = usuarioLiquidador;
	}
	
	
	
}