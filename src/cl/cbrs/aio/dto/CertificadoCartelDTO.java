package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CertificadoCartelDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 1437624793907347781L;
	
	private Integer ano;
	private Boolean bis;
	private Long caratula;
	private EstadoCertificadoDTO estadoCertificadoDTO;
	private Date fechaCreacion;
	private Date fechaFirma;
	private Long idCertificado;
	private Integer mes;
	private Long numero;
	private Integer pagDesde;
	private Integer pagHasta;
	private TipoArchivoDTO tipoArchivoDTO;
	private String usuario;
	private Integer idReg;
	
	public CertificadoCartelDTO(){
		
	}
	
	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"ano\"");
		sb.append(":");

		sb.append(this.ano);
		
		sb.append(",");	
		
		sb.append("\"bis\"");
		sb.append(":");

		sb.append(this.bis);
		
		sb.append(",");	
		
		sb.append("\"caratula\"");
		sb.append(":");

		sb.append(this.caratula);
		
		sb.append(",");	
		
		sb.append("\"idReg\"");
		sb.append(":");
		sb.append(this.idReg);		
		sb.append(",");			
		
		sb.append("\"estadoCertificadoDTO\"");
		sb.append(":");

		sb.append(this.estadoCertificadoDTO.toJSONString());

		sb.append(",");	
		
		sb.append("\"fechaCreacion\"");
		sb.append(":");
		
		if(this.fechaCreacion!=null){
			String fechaCreacionS = sdf.format(this.fechaCreacion);
			sb.append("\"" + JSONObject.escape(fechaCreacionS.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		
		sb.append(",");	
		
		sb.append("\"fechaFirma\"");
		sb.append(":");
		
		if(this.fechaFirma!=null){
			String fechaFirmaS = sdf.format(this.fechaFirma);
			sb.append("\"" + JSONObject.escape(fechaFirmaS.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		
		sb.append(",");

		sb.append("\"idCertificado\"");
		sb.append(":");

		sb.append(this.idCertificado);

		sb.append(",");
		
		sb.append("\"mes\"");
		sb.append(":");

		sb.append(this.mes);

		sb.append(",");
		
		sb.append("\"numero\"");
		sb.append(":");

		sb.append(this.numero);
		
		sb.append(",");
		
		sb.append("\"pagDesde\"");
		sb.append(":");

		sb.append(this.pagDesde);
		
		sb.append(",");
		
		sb.append("\"pagHasta\"");
		sb.append(":");

		sb.append(this.pagHasta);
		
		sb.append(",");
		
		sb.append("\"tipoArchivoDTO\"");
		sb.append(":");

		sb.append(this.tipoArchivoDTO.toJSONString());

		sb.append(",");	
		
		sb.append("\"usuario\"");
		sb.append(":");
		
		if(this.usuario!=null){
			sb.append("\"" + JSONObject.escape(usuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append("}");

		return sb.toString();		
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

	public Long getCaratula() {
		return caratula;
	}

	public void setCaratula(Long caratula) {
		this.caratula = caratula;
	}

	public EstadoCertificadoDTO getEstadoCertificadoDTO() {
		return estadoCertificadoDTO;
	}

	public void setEstadoCertificadoDTO(EstadoCertificadoDTO estadoCertificadoDTO) {
		this.estadoCertificadoDTO = estadoCertificadoDTO;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaFirma() {
		return fechaFirma;
	}

	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public Long getIdCertificado() {
		return idCertificado;
	}

	public void setIdCertificado(Long idCertificado) {
		this.idCertificado = idCertificado;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getPagDesde() {
		return pagDesde;
	}

	public void setPagDesde(Integer pagDesde) {
		this.pagDesde = pagDesde;
	}

	public Integer getPagHasta() {
		return pagHasta;
	}

	public void setPagHasta(Integer pagHasta) {
		this.pagHasta = pagHasta;
	}

	public TipoArchivoDTO getTipoArchivoDTO() {
		return tipoArchivoDTO;
	}

	public void setTipoArchivoDTO(TipoArchivoDTO tipoArchivoDTO) {
		this.tipoArchivoDTO = tipoArchivoDTO;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Integer getIdReg() {
		return idReg;
	}

	public void setIdReg(Integer idReg) {
		this.idReg = idReg;
	}

}