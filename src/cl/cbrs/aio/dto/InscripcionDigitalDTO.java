package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.estado.RegistroDTO;

public class InscripcionDigitalDTO implements Serializable,JSONAware{

	private static final long serialVersionUID = -8187142252966662424L;
	
	private Long idInscripcion;
	private Date fechaCreacion;
	private Date fechaActualizacion;
	private Long fechaActualizacionL;
	private Date fechaFolio;
	private Long foja;
	private String numero;
	private Long ano;
	private Boolean bis;
	private Integer registro;
	private Boolean impTicket;
	private RegistroDTO registroDTO;
	private ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino;
	private ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen;
	//private ArrayList<BitacoraDTO> bitacoras;
	
	public InscripcionDigitalDTO(){
		
	}

	public Long getIdInscripcion() {
		return idInscripcion;
	}

	public void setIdInscripcion(Long idInscripcion) {
		this.idInscripcion = idInscripcion;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public Long getFoja() {
		return foja;
	}

	public void setFoja(Long foja) {
		this.foja = foja;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
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

	public ArrayList<AnotacionDTO> getAnotacionsForIdInscripcionDestino() {
		return anotacionsForIdInscripcionDestino;
	}

	public void setAnotacionsForIdInscripcionDestino(
			ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino) {
		this.anotacionsForIdInscripcionDestino = anotacionsForIdInscripcionDestino;
	}

	public ArrayList<AnotacionDTO> getAnotacionsForIdInscripcionOrigen() {
		return anotacionsForIdInscripcionOrigen;
	}

	public void setAnotacionsForIdInscripcionOrigen(
			ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen) {
		this.anotacionsForIdInscripcionOrigen = anotacionsForIdInscripcionOrigen;
	}

//	public ArrayList<BitacoraDTO> getBitacoras() {
//		return bitacoras;
//	}
//
//	public void setBitacoras(ArrayList<BitacoraDTO> bitacoras) {
//		this.bitacoras = bitacoras;
//	}
	
	@SuppressWarnings("unchecked")
	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

		String createDate = "";

		if(this.fechaCreacion!=null){
			//Date fechaCreacionDate = fechaCreacion.getTime();

			createDate = sdf.format(this.fechaCreacion);
		}
		
		String updateDate = "";
		Long updateDateLong = null;
		
		if(this.fechaActualizacion!=null){

			updateDate = sdf.format(this.fechaActualizacion);
			updateDateLong = this.fechaActualizacion.getTime();
		}
		
		String folioDate = "";

		if(this.fechaFolio!=null){
			//Date fechaCreacionDate = fechaCreacion.getTime();

			folioDate = sdf.format(this.fechaFolio);
		}
		
		sb.append("{");	

		sb.append("\"idInscripcion\"");
		sb.append(":");

		sb.append(this.idInscripcion);
		
		sb.append(",");
		
		sb.append("\"foja\"");
		sb.append(":");

		sb.append(this.foja);
		
		sb.append(",");

		sb.append("\"numero\"");
		sb.append(":");

		if(this.numero!=null){
			sb.append("\"" + JSONObject.escape(this.numero.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append(",");
		
		sb.append("\"ano\"");
		sb.append(":");
		sb.append(this.ano);		
		sb.append(",");
		
		sb.append("\"registro\"");
		sb.append(":");
		sb.append(this.registro);		
		sb.append(",");		
		
		sb.append("\"registroDTO\"");
		sb.append(":");
		if(this.registroDTO!=null)
			sb.append(this.registroDTO.toJSONString());		
		else
			sb.append("\"\"");
		sb.append(",");			

		sb.append("\"bis\"");
		sb.append(":");

		if(this.bis!=null && this.bis){
			sb.append(this.bis);			
		}else{
			sb.append(false);				
		}		

		sb.append(",");	
		
		sb.append("\"fechaCreacion\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(createDate) + "\"");	

		sb.append(",");
		
		sb.append("\"fechaFolio\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(folioDate) + "\"");	

		sb.append(",");
		
		sb.append("\"fechaActualizacion\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(updateDate) + "\"");	

		sb.append(",");
		
		sb.append("\"fechaActualizacionL\"");
		sb.append(":");
		sb.append("\"" + updateDateLong + "\"");	
		sb.append(",");
		
		
		JSONArray anotacionsForIdInscripcionDestinoJSONArray = new JSONArray();
		
		if(this.anotacionsForIdInscripcionDestino!=null){
			for(AnotacionDTO at : this.anotacionsForIdInscripcionDestino){				
				anotacionsForIdInscripcionDestinoJSONArray.add(at);
			}			
		}

		sb.append("\"anotacionsForIdInscripcionDestino\"");
		sb.append(":");
		
		sb.append(anotacionsForIdInscripcionDestinoJSONArray);

		sb.append(",");

		JSONArray anotacionsForIdInscripcionOrigenJSONArray = new JSONArray();
		
		if(this.anotacionsForIdInscripcionOrigen!=null){
			for(AnotacionDTO at : this.anotacionsForIdInscripcionOrigen){				
				anotacionsForIdInscripcionOrigenJSONArray.add(at);
			}			
		}

		sb.append("\"anotacionsForIdInscripcionOrigen\"");
		sb.append(":");
		
		sb.append(anotacionsForIdInscripcionOrigenJSONArray);

//		sb.append(",");
//
//		JSONArray bitacorasJSONArray = new JSONArray();
//		
//		if(this.bitacoras!=null){
//			for(BitacoraDTO at : this.bitacoras){				
//				bitacorasJSONArray.add(at);
//			}			
//		}
//
//		sb.append("\"bitacoras\"");
//		sb.append(":");
//		
//		sb.append(bitacorasJSONArray);

		sb.append("}");

		return sb.toString();		
	}

	public Date getFechaFolio() {
		return fechaFolio;
	}

	public void setFechaFolio(Date fechaFolio) {
		this.fechaFolio = fechaFolio;
	}

	public void setRegistro(Integer registro) {
		this.registro = registro;
	}

	public Integer getRegistro() {
		return registro;
	}

	public void setRegistroDTO(RegistroDTO registroDTO) {
		this.registroDTO = registroDTO;
	}

	public RegistroDTO getRegistroDTO() {
		return registroDTO;
	}

	public Boolean getImpTicket() {
		return impTicket;
	}

	public void setImpTicket(Boolean impTicket) {
		this.impTicket = impTicket;
	}

	public Long getFechaActualizacionL() {
		return fechaActualizacionL;
	}

	public void setFechaActualizacionL(Long fechaActualizacionL) {
		this.fechaActualizacionL = fechaActualizacionL;
	}
}