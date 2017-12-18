package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ObservacionSolicitudDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 3727006651881058558L;
	
	private Integer id;
	private String titulo;
	private String texto;
	private TipoObservacionDTO tipoObservacionDTO;

	public ObservacionSolicitudDTO(){}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"id\"");
		sb.append(":");

		sb.append(this.id);
		
		sb.append(",");
		
		sb.append("\"titulo\"");
		sb.append(":");

		if(this.titulo!=null){
			sb.append("\"" + JSONObject.escape(this.titulo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append(",");
		
		sb.append("\"texto\"");
		sb.append(":");

		if(this.texto!=null){
			sb.append("\"" + JSONObject.escape(this.texto.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	

		sb.append(",");

		sb.append("\"tipoObservacionDTO\"");
		sb.append(":");

		if(this.tipoObservacionDTO!=null){
			sb.append(this.tipoObservacionDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append("}");

		return sb.toString();		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public TipoObservacionDTO getTipoObservacionDTO() {
		return tipoObservacionDTO;
	}

	public void setTipoObservacionDTO(TipoObservacionDTO tipoObservacionDTO) {
		this.tipoObservacionDTO = tipoObservacionDTO;
	}
}