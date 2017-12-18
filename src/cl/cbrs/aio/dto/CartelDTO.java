package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;

public class CartelDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = -8864957288319516213L;
	
	private Integer cantidadPaginas;
	private TipoArchivoDTO tipoArchivoDTO;
	private Boolean hayArchivo;
	private Integer numero;
	private Integer mes;
	private Integer ano;
	private Integer registro;
	private Boolean bis;
	
	public CartelDTO(){
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"cantidadPaginas\"");
		sb.append(":");

		sb.append(this.cantidadPaginas);

		sb.append(",");	
		
		sb.append("\"tipoArchivoDTO\"");
		sb.append(":");

		sb.append(this.tipoArchivoDTO.toJSONString());

		sb.append(",");	
		
		sb.append("\"hayArchivo\"");
		sb.append(":");

		sb.append(this.hayArchivo);

		sb.append(",");

		sb.append("\"numero\"");
		sb.append(":");

		sb.append(this.numero);
		
		sb.append(",");	
		
		sb.append("\"mes\"");
		sb.append(":");

		sb.append(this.mes);
		
		sb.append(",");	
		
		sb.append("\"ano\"");
		sb.append(":");
		sb.append(this.ano);		
		sb.append(",");	
		
		sb.append("\"registro\"");
		sb.append(":");
		sb.append(this.registro);		
		sb.append(",");			
		
		sb.append("\"bis\"");
		sb.append(":");

		sb.append(this.bis);
		
		sb.append("}");

		return sb.toString();		
	}

	public Integer getCantidadPaginas() {
		return cantidadPaginas;
	}

	public void setCantidadPaginas(Integer cantidadPaginas) {
		this.cantidadPaginas = cantidadPaginas;
	}

	public TipoArchivoDTO getTipoArchivoDTO() {
		return tipoArchivoDTO;
	}

	public void setTipoArchivoDTO(TipoArchivoDTO tipoArchivoDTO) {
		this.tipoArchivoDTO = tipoArchivoDTO;
	}

	public Boolean getHayArchivo() {
		return hayArchivo;
	}

	public void setHayArchivo(Boolean hayArchivo) {
		this.hayArchivo = hayArchivo;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Boolean getBis() {
		return bis;
	}

	public void setBis(Boolean bis) {
		this.bis = bis;
	}

	public Integer getRegistro() {
		return registro;
	}

	public void setRegistro(Integer registro) {
		this.registro = registro;
	}

}