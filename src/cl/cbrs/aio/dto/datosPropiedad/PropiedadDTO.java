package cl.cbrs.aio.dto.datosPropiedad;

import java.util.ArrayList;

public class PropiedadDTO {
	
	private Integer folio;
	private Integer borrador;
	private Integer digito;
	private Integer foja;
	private Integer numero;
	private Integer anyo;
	private Integer bis;
	private Integer vigenteT;
	private Integer vigente;
	private String tipoCliente = "";
	private Integer orden;
	private String rut = "";
	private String apPaternoCli = "";
	private String apMaternoCli = "";
	private String nombreCli = "";
	private String nombres = "";
	private String direccion = "";
	private String comuna = "";
	private String deslindes = "";
	private ArrayList<String> listaNotas = new ArrayList<String>();
	private ArrayList<ClienteDTO> duenyos = new ArrayList<ClienteDTO>();
	private String naturaleza = "";
	
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
	public Integer getDigito() {
		return digito;
	}
	public void setDigito(Integer digito) {
		this.digito = digito;
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
	public Integer getAnyo() {
		return anyo;
	}
	public void setAnyo(Integer anyo) {
		this.anyo = anyo;
	}
	public Integer getBis() {
		return bis;
	}
	public void setBis(Integer bis) {
		this.bis = bis;
	}
	public Integer getVigenteT() {
		return vigenteT;
	}
	public void setVigenteT(Integer vigenteT) {
		this.vigenteT = vigenteT;
	}
	public Integer getVigente() {
		return vigente;
	}
	public void setVigente(Integer vigente) {
		this.vigente = vigente;
	}
	public String getTipoCliente() {
		return tipoCliente;
	}
	public void setTipoCliente(String tipoCliente) {
		this.tipoCliente = tipoCliente;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
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
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getComuna() {
		return comuna;
	}
	public void setComuna(String comuna) {
		this.comuna = comuna;
	}
	public String getDeslindes() {
		return deslindes;
	}
	public void setDeslindes(String deslindes) {
		this.deslindes = deslindes;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public ArrayList<ClienteDTO> getDuenyos() {
		return duenyos;
	}
	public void setDuenyos(ArrayList<ClienteDTO> duenyos) {
		this.duenyos = duenyos;
	}
	public void setListaNotas(ArrayList<String> listaNotas) {
		this.listaNotas = listaNotas;
	}
	public ArrayList<String> getListaNotas() {
		return listaNotas;
	}
	public String getNaturaleza() {
		return naturaleza;
	}
	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
	}

}
