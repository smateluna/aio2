package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class RepertorioDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private Long fechaIngresoL;
	private Date fechaIngreso;
	private Integer numeroRepertorio;
	private Integer ano;
	private Integer vigente;
	private String observacion;
		
	public RepertorioDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		

		
		sb.append("\"fechaIngresoL\"");
		sb.append(":");
		sb.append(this.fechaIngresoL);		
		sb.append(",");
		
		sb.append("\"fechaIngreso\"");
		sb.append(":");
		if(this.fechaIngreso!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaIngreso)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");	
		
		sb.append("\"numeroRepertorio\"");
		sb.append(":");
		sb.append(this.numeroRepertorio);		
		sb.append(",");
		
		sb.append("\"ano\"");
		sb.append(":");
		sb.append(this.ano);		
		sb.append(",");
		
		sb.append("\"vigente\"");
		sb.append(":");
		sb.append(this.vigente);
		sb.append(",");
		
		sb.append("\"observacion\"");
		sb.append(":");		
		if(this.observacion!=null){
			sb.append("\"" + JSONObject.escape(this.observacion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Integer getNumeroRepertorio() {
		return numeroRepertorio;
	}

	public void setNumeroRepertorio(Integer numeroRepertorio) {
		this.numeroRepertorio = numeroRepertorio;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getVigente() {
		return vigente;
	}

	public void setVigente(Integer vigente) {
		this.vigente = vigente;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getFechaIngresoL() {
		return fechaIngresoL;
	}

	public void setFechaIngresoL(Long fechaIngresoL) {
		this.fechaIngresoL = fechaIngresoL;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}


}