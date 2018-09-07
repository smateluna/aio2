package cl.cbrs.aio.dto.firmaElectronica;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class EntregaEnLineaDTO implements JSONAware {
	
	private Boolean estado;
	private ArrayList<RegistroFirmaElectronicaDTO> documentos;
	private String mensaje;
	private String mensajeWeb;
	private BoletaDTO boleta;
	private ArrayList<String> advertencias;
	
	public EntregaEnLineaDTO() {
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"estado\"");
		sb.append(":");

		sb.append(this.estado);
		
		sb.append(",");
		
		sb.append("\"boleta\"");
		sb.append(":");

		if(this.boleta!=null){
			sb.append(this.boleta.toJSONString());
		}else{
			sb.append("null");				
		}
				
		sb.append(",");
		
		JSONArray papelesJSONArray = new JSONArray();

		if(this.documentos!=null){
			for(RegistroFirmaElectronicaDTO papel : this.documentos){				
				papelesJSONArray.add(papel);
			}			
		}

		sb.append("\"documentos\"");
		sb.append(":");

		sb.append(papelesJSONArray);
		
		sb.append(",");		

		sb.append("\"mensaje\"");
		sb.append(":");

		if(this.mensaje!=null){
			sb.append("\"" + JSONObject.escape(this.mensaje.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		
		sb.append(",");		

		sb.append("\"mensajeWeb\"");
		sb.append(":");

		if(this.mensajeWeb!=null){
			sb.append("\"" + JSONObject.escape(this.mensajeWeb.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		
		sb.append(",");
		
		JSONArray advertenciasJSONArray = new JSONArray();

		if(this.advertencias!=null){
			for(String adv : this.advertencias){				
				advertenciasJSONArray.add(adv);
			}			
		}

		sb.append("\"advertencias\"");
		sb.append(":");

		sb.append(advertenciasJSONArray);

		sb.append("}");

		return sb.toString();		
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public ArrayList<RegistroFirmaElectronicaDTO> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(ArrayList<RegistroFirmaElectronicaDTO> documentos) {
		this.documentos = documentos;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensajeWeb() {
		return mensajeWeb;
	}

	public void setMensajeWeb(String mensajeWeb) {
		this.mensajeWeb = mensajeWeb;
	}

	public ArrayList<String> getAdvertencias() {
		return advertencias;
	}

	public void setAdvertencias(ArrayList<String> advertencias) {
		this.advertencias = advertencias;
	}

	public BoletaDTO getBoleta() {
		return boleta;
	}

	public void setBoleta(BoletaDTO boleta) {
		this.boleta = boleta;
	}
}