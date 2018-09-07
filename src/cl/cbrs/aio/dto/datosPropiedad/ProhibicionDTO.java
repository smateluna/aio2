package cl.cbrs.aio.dto.datosPropiedad;

import java.util.ArrayList;
import java.util.Date;

public class ProhibicionDTO {
	
	private Integer fojas;
	private Integer numero;
	private Integer anyo;
	private String naturaleza = "";
	private Integer codNaturaleza;
	private String acreedor = "";
	private String tipoAcreedor = "";
	private String estado;
	private String sigla;
	private ArrayList<NotasDTO> listaNotas = new ArrayList<NotasDTO>();
	private ArrayList<String> acreedores = new ArrayList<String>();
	private String resumen = "";
	private Integer bis;
	private Date fechaEstado;
	private Long caratula;
	
	public Integer getFojas() {
		return fojas;
	}
	public void setFojas(Integer fojas) {
		this.fojas = fojas;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Integer getAnyo() {
		return anyo;
	}
	public void setAnyo(Integer anyo) {
		this.anyo = anyo;
	}
	public String getNaturaleza() {
		return naturaleza;
	}
	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
	}
	public String getAcreedor() {
		return acreedor;
	}
	public void setAcreedor(String acreedor) {
		this.acreedor = acreedor;
	}
	public String getResumen() {
		return resumen;
	}
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}
	public Integer getBis() {
		return bis;
	}
	public void setBis(Integer bis) {
		this.bis = bis;
	}
	public void setListaNotas(ArrayList<NotasDTO> listaNotas) {
		this.listaNotas = listaNotas;
	}
	public ArrayList<NotasDTO> getListaNotas() {
		return listaNotas;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getEstado() {
		return estado;
	}
	public void setAcreedores(ArrayList<String> acreedores) {
		this.acreedores = acreedores;
	}
	public ArrayList<String> getAcreedores() {
		return acreedores;
	}
	public void setCodNaturaleza(Integer codNaturaleza) {
		this.codNaturaleza = codNaturaleza;
	}
	public Integer getCodNaturaleza() {
		return codNaturaleza;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getSigla() {
		return sigla;
	}
	public void setTipoAcreedor(String tipoAcreedor) {
		this.tipoAcreedor = tipoAcreedor;
	}
	public String getTipoAcreedor() {
		return tipoAcreedor;
	}
	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}
	public Date getFechaEstado() {
		return fechaEstado;
	}
	public Long getCaratula() {
		return caratula;
	}
	public void setCaratula(Long caratula) {
		this.caratula = caratula;
	}
	
	
}
