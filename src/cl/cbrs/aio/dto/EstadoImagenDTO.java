package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EstadoImagenDTO implements JSONAware, Serializable{
	private static final long serialVersionUID = 8959317955778041130L;
	
	private Long foja;
	private Long numero;
	private Long ano;
	private Integer bis;
	
	//1 SIN IMAGEN
	//2 IMAGEN REFERENCIAL
	//3 DIGITALIZADA
	public static Integer ESTADO_DIGITAL_SIN_IMAGEN_VALUE = 1;
	public static String ESTADO_DIGITAL_SIN_IMAGEN_DESC = "Sin Imagen";

	public static Integer ESTADO_DIGITAL_IMAGEN_REFERENCIAL_VALUE = 2;
	public static String ESTADO_DIGITAL_IMAGEN_REFERENCIAL_DESC = "Imagen Referencial";

	public static Integer ESTADO_DIGITAL_DIGITALIZADA_VALUE = 3;
	public static String ESTADO_DIGITAL_DIGITALIZADA_DESC = "Digitalizado";
		
	private Integer estadoDigitalId;
	private String estadoDigitalDescripcion;

	private Boolean estadoReferencialExiste;
	private String estadoReferencialFecha;
	
	//es la q buscaron?
	private Boolean principal;
	
	//private String firma;
	
	private EstadoInscripcionDTO estadoInscripcionDTO;

	public EstadoImagenDTO(){

	}

	public EstadoImagenDTO(Long foja, Long numero, Long ano, Integer bis) {
		this.foja = foja;
		this.numero = numero;
		this.ano = ano;
		this.bis = bis;
	}


	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");
	
		sb.append("\"" + uuid() + "\"");			

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

		
		if(this.bis==null || this.bis==0){
			sb.append(false);
		}else{
			sb.append(true);
		}
				
		sb.append(",");

		sb.append("\"estadoDigitalId\"");
		sb.append(":");

		sb.append(this.estadoDigitalId);

		sb.append(",");		

		sb.append("\"estadoDigitalDescripcion\"");
		sb.append(":");
	
		sb.append("\"" + JSONObject.escape(this.estadoDigitalDescripcion.trim()) + "\"");			

		sb.append(",");
		
		sb.append("\"estadoReferencialExiste\"");
		sb.append(":");

		sb.append(this.estadoReferencialExiste);

		sb.append(",");

		sb.append("\"estadoReferencialFecha\"");
		sb.append(":");

		if(this.estadoReferencialFecha!=null){
			sb.append("\"" + JSONObject.escape(estadoReferencialFecha) + "\"");				
		}else{
			sb.append("\"\"");				
		}		

		sb.append(",");		
				
		sb.append("\"puedeSolicitar\"");
		sb.append(":");
		if( (this.estadoDigitalId == 1 || this.estadoDigitalId == 2) &&
				(this.estadoInscripcionDTO!=null && this.estadoInscripcionDTO.getIdEstadoInscripcion()==1)){
			sb.append(true);
		}else{
			sb.append(false);
		}
		
		sb.append(",");	

		sb.append("\"principal\"");
		sb.append(":");

		sb.append(this.principal);
		
		sb.append(",");

		sb.append("\"firma\"");
		sb.append(":");

		sb.append("\"" + JSONObject.escape(getFirma()) + "\"");		
		
		sb.append(",");	

		sb.append("\"estadoInscripcionDTO\"");
		sb.append(":");

		sb.append(this.estadoInscripcionDTO.toJSONString());
	

		sb.append("}");

		return sb.toString();		
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

	public Integer getBis() {
		return bis;
	}

	public void setBis(Integer bis) {
		this.bis = bis;
	}

	public Integer getEstadoDigitalId() {
		return estadoDigitalId;
	}

	public void setEstadoDigitalId(Integer estadoDigitalId) {
		this.estadoDigitalId = estadoDigitalId;
	}

	public String getEstadoDigitalDescripcion() {
		return estadoDigitalDescripcion;
	}

	public void setEstadoDigitalDescripcion(String estadoDigitalDescripcion) {
		this.estadoDigitalDescripcion = estadoDigitalDescripcion;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	
	public static final String uuid() {     
		String result = java.util.UUID.randomUUID().toString();     
		result = result.replaceAll("-", "");    
		result = result.substring(0, 32);     
		return result; 
	}

	public String getFirma() {
		
		return 	this.foja+"_"+this.numero+"_"+this.ano+"_"+this.bis;
	}

//	public void setFirma(String firma) {
//		this.firma = firma;
//	}

	public Boolean getEstadoReferencialExiste() {
		return estadoReferencialExiste;
	}

	public void setEstadoReferencialExiste(Boolean estadoReferencialExiste) {
		this.estadoReferencialExiste = estadoReferencialExiste;
	}

	public String getEstadoReferencialFecha() {
		return estadoReferencialFecha;
	}

	public void setEstadoReferencialFecha(String estadoReferencialFecha) {
		this.estadoReferencialFecha = estadoReferencialFecha;
	}

	public EstadoInscripcionDTO getEstadoInscripcionDTO() {
		return estadoInscripcionDTO;
	}

	public void setEstadoInscripcionDTO(EstadoInscripcionDTO estadoInscripcionDTO) {
		this.estadoInscripcionDTO = estadoInscripcionDTO;
	}
}