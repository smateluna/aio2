package cl.cbrs.aio.dto.datosPropiedad;

public class QuiebraDTO {
	
	private String tipo = "";
	private Integer foja;
	private Integer numero;
	private Integer ano;
	private String nombres = "";
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	
	

}
