package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ConsultaDocumentoDTO implements JSONAware, Serializable{
	private static final long serialVersionUID = -5226379392423139512L;
	
	private Integer cantidadPaginas;
	private Integer tipoDocumento;
	private Boolean hayDocumento;
	private Boolean tieneNotas;
	private Date fechaArchivo;

	private Long foja;
	private Long numero;
	private Long ano;
	private Boolean bis;
	
	//customs
	private String keyImg;
	private Boolean puedeSolicitar;
	
	public ConsultaDocumentoDTO(){
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String fechaArchivoS = "";

		if(this.fechaArchivo!=null){
			//Date fechaCreacionDate = fechaCreacion.getTime();

			fechaArchivoS = sdf.format(this.fechaArchivo);
		}

		sb.append("{");	
		
		sb.append("\"cantidadPaginas\"");
		sb.append(":");

		sb.append(this.cantidadPaginas);

		sb.append(",");	
		
		sb.append("\"tipoDocumento\"");
		sb.append(":");

		sb.append(this.tipoDocumento);

		sb.append(",");	
		
		sb.append("\"hayDocumento\"");
		sb.append(":");

		sb.append(this.hayDocumento);

		sb.append(",");
		
		sb.append("\"tieneNotas\"");
		sb.append(":");

		sb.append(this.tieneNotas);
		
		sb.append(",");	
		
		sb.append("\"keyImg\"");
		sb.append(":");
		
		if(this.keyImg!=null){
			sb.append("\"" + JSONObject.escape(this.keyImg.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
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
		
		sb.append(",");	
		
		sb.append("\"puedeSolicitar\"");
		sb.append(":");

		sb.append(this.puedeSolicitar);
		
		sb.append(",");
		
		sb.append("\"fechaArchivo\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(fechaArchivoS) + "\"");
		
		sb.append(",");	
		
		sb.append("\"firma\"");
		sb.append(":");
		
		if(this.getFirma()!=null){
			sb.append("\"" + JSONObject.escape(this.getFirma()) + "\"");			
		}else{
			sb.append("\"\"");				
		}


		sb.append("}");

		return sb.toString();		
	}

	public Integer getCantidadPaginas() {
		return cantidadPaginas;
	}

	public void setCantidadPaginas(Integer cantidadPaginas) {
		this.cantidadPaginas = cantidadPaginas;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Boolean getHayDocumento() {
		return hayDocumento;
	}

	public void setHayDocumento(Boolean hayDocumento) {
		this.hayDocumento = hayDocumento;
	}

	public Boolean getTieneNotas() {
		return tieneNotas;
	}

	public void setTieneNotas(Boolean tieneNotas) {
		this.tieneNotas = tieneNotas;
	}

	public String getKeyImg() {
		return keyImg;
	}

	public void setKeyImg(String keyImg) {
		this.keyImg = keyImg;
	}

	public Long getFoja() {
		return foja;
	}

	public void setFoja(Long foja) {
		this.foja = foja;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Long getAno() {
		return ano;
	}

	public void setAno(Long ano) {
		this.ano = ano;
	}

	public Boolean getBis() {
		return bis;
	}

	public void setBis(Boolean bis) {
		this.bis = bis;
	}
	
	public String getFirma() {
		
		return 	this.foja+"_"+this.numero+"_"+this.ano+"_"+this.bis;
	}

	public Boolean getPuedeSolicitar() {
		return puedeSolicitar;
	}

	public void setPuedeSolicitar(Boolean puedeSolicitar) {
		this.puedeSolicitar = puedeSolicitar;
	}

	public Date getFechaArchivo() {
		return fechaArchivo;
	}

	public void setFechaArchivo(Date fechaArchivo) {
		this.fechaArchivo = fechaArchivo;
	}

}	