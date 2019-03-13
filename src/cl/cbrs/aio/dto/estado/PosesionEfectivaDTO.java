package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class PosesionEfectivaDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private String folio;
	private CausanteDTO causante;
	private String titulo;
	private Date fechaProceso;
	private Long caratula;
		
	public PosesionEfectivaDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		

		sb.append("\"folio\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(this.folio.trim()) + "\"");
		sb.append(",");
		
		sb.append("\"caratula\"");
		sb.append(":");
		sb.append(this.caratula);		
		sb.append(",");
		
		sb.append("\"causante\"");
		sb.append(":");
		if(this.causante!=null){
			sb.append(this.causante.toJSONString());			
		}else{
			sb.append("null");				
		}
		
		sb.append(",");	
		
		sb.append("\"fechaProceso\"");
		sb.append(":");
		if(this.fechaProceso!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaProceso)) + "\"");
		else
			sb.append("\"\"");
		
		sb.append(",");	
		
		sb.append("\"titulo\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(this.titulo.trim()) + "\"");
	
		sb.append("}");

		return sb.toString();		
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public CausanteDTO getCausante() {
		return causante;
	}

	public void setCausante(CausanteDTO causante) {
		this.causante = causante;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public Long getCaratula() {
		return caratula;
	}

	public void setCaratula(Long caratula) {
		this.caratula = caratula;
	}

	

}