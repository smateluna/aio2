package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class UafBienDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5607535884631474352L;
	
	private Integer folio;
	private Integer borrador;
	private Date fechaEstado;
	private Integer foja;
	private Integer numero;
	private Integer ano;
	private Boolean bis;
	private Integer codNaturaleza;
	private String descNaturaleza;
	private String regNaturaleza;
	
	public UafBienDTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"folio\"");
		sb.append(":");
		sb.append(this.folio);		
		sb.append(",");		
			
		sb.append("\"borrador\"");
		sb.append(":");
		sb.append(this.borrador);		
		sb.append(",");
		
		sb.append("\"codNaturaleza\"");
		sb.append(":");
		sb.append(this.codNaturaleza);		
		sb.append(",");		
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String fecha = "";
		if(this.fechaEstado!=null){
			fecha = sdf.format(this.fechaEstado);
		}
		sb.append("\"fechaEstado\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(fecha) + "\"");			
		sb.append(",");	
		
		sb.append("\"descNaturaleza\"");
		sb.append(":");
		if(descNaturaleza!=null)
			sb.append("\"" + JSONObject.escape(descNaturaleza.trim()) + "\"");			
		else
			sb.append("\"\"");
		sb.append(",");			
		
		sb.append("\"regNaturaleza\"");
		sb.append(":");
		if(regNaturaleza!=null)
			sb.append("\"" + JSONObject.escape(regNaturaleza.trim()) + "\"");			
		else
			sb.append("\"\"");
		sb.append(",");			
		
		sb.append("\"foja\"");
		sb.append(":");
		sb.append(this.foja);		
		sb.append(",");
		
		sb.append("\"numero\"");
		sb.append(":");
		sb.append(this.numero);		
		sb.append(",");
		
		sb.append("\"ano\"");
		sb.append(":");
		sb.append(this.ano);		
		sb.append(",");
		
		sb.append("\"bis\"");
		sb.append(":");
		sb.append(this.bis);		
		
		sb.append("}");

		return sb.toString();		
	}

	public Integer getFolio() {
		return folio;
	}

	public void setFolio(Integer folio) {
		this.folio = folio;
	}

	public Integer getBorrador() {
		return borrador;
	}

	public void setBorrador(Integer borrador) {
		this.borrador = borrador;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
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

	public Boolean getBis() {
		return bis;
	}

	public void setBis(Boolean bis) {
		this.bis = bis;
	}

	public Integer getCodNaturaleza() {
		return codNaturaleza;
	}

	public void setCodNaturaleza(Integer codNaturaleza) {
		this.codNaturaleza = codNaturaleza;
	}

	public String getDescNaturaleza() {
		return descNaturaleza;
	}

	public void setDescNaturaleza(String descNaturaleza) {
		this.descNaturaleza = descNaturaleza;
	}

	public String getRegNaturaleza() {
		return regNaturaleza;
	}

	public void setRegNaturaleza(String regNaturaleza) {
		this.regNaturaleza = regNaturaleza;
	}
	

}