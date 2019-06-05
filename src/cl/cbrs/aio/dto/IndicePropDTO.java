package cl.cbrs.aio.dto;

public class IndicePropDTO {
	
	private Integer foja;
	private Integer numero;
	private Integer ano;
	private String nombresCom;
	private String dirAntigua;
	private Boolean procesado;
	private Long idI;
	
	public IndicePropDTO(){
		
	}

	public Integer getFoja() {
		return foja;
	}

	public void setFoja(Integer foja) {
		this.foja = foja;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getNombresCom() {
		return nombresCom;
	}

	public void setNombresCom(String nombresCom) {
		this.nombresCom = nombresCom;
	}

	public String getDirAntigua() {
		return dirAntigua;
	}

	public void setDirAntigua(String dirAntigua) {
		this.dirAntigua = dirAntigua;
	}

	public Boolean getProcesado() {
		return procesado;
	}

	public void setProcesado(Boolean procesado) {
		this.procesado = procesado;
	}

	public Long getIdI() {
		return idI;
	}

	public void setIdI(Long idI) {
		this.idI = idI;
	}

	
}
