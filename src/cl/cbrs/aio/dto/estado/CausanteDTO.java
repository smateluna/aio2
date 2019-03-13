package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CausanteDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private String nombreCausante;
	private String run;
	private String profesionOficio;
	private String estadoCivil;
	private String ultimoDomicilio;
	private String fechaDefuncion;
	private String lugarDefuncion;
	private String inscripcionDefuncion;
		
	public CausanteDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		

		sb.append("\"nombreCausante\"");
		sb.append(":");		
		if(this.nombreCausante!=null){
			sb.append("\"" + JSONObject.escape(this.nombreCausante.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"run\"");
		sb.append(":");		
		if(this.run!=null){
			sb.append("\"" + JSONObject.escape(this.run.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"profesionOficio\"");
		sb.append(":");		
		if(this.profesionOficio!=null){
			sb.append("\"" + JSONObject.escape(this.profesionOficio.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"estadoCivil\"");
		sb.append(":");		
		if(this.estadoCivil!=null){
			sb.append("\"" + JSONObject.escape(this.estadoCivil.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");

		sb.append("\"ultimoDomicilio\"");
		sb.append(":");		
		if(this.ultimoDomicilio!=null){
			sb.append("\"" + JSONObject.escape(this.ultimoDomicilio.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"fechaDefuncion\"");
		sb.append(":");
		if(this.fechaDefuncion!=null)
			sb.append("\"" + JSONObject.escape(this.fechaDefuncion.trim()) + "\"");
		else
			sb.append("\"\"");
		
		sb.append(",");	
		
		sb.append("\"lugarDefuncion\"");
		sb.append(":");		
		if(this.lugarDefuncion!=null){
			sb.append("\"" + JSONObject.escape(this.lugarDefuncion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"inscripcionDefuncion\"");
		sb.append(":");		
		if(this.inscripcionDefuncion!=null){
			sb.append("\"" + JSONObject.escape(this.inscripcionDefuncion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public String getNombreCausante() {
		return nombreCausante;
	}

	public void setNombreCausante(String nombreCausante) {
		this.nombreCausante = nombreCausante;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getProfesionOficio() {
		return profesionOficio;
	}

	public void setProfesionOficio(String profesionOficio) {
		this.profesionOficio = profesionOficio;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getUltimoDomicilio() {
		return ultimoDomicilio;
	}

	public void setUltimoDomicilio(String ultimoDomicilio) {
		this.ultimoDomicilio = ultimoDomicilio;
	}

	public String getFechaDefuncion() {
		return fechaDefuncion;
	}

	public void setFechaDefuncion(String fechaDefuncion) {
		this.fechaDefuncion = fechaDefuncion;
	}

	public String getLugarDefuncion() {
		return lugarDefuncion;
	}

	public void setLugarDefuncion(String lugarDefuncion) {
		this.lugarDefuncion = lugarDefuncion;
	}

	public String getInscripcionDefuncion() {
		return inscripcionDefuncion;
	}

	public void setInscripcionDefuncion(String inscripcionDefuncion) {
		this.inscripcionDefuncion = inscripcionDefuncion;
	}

	
}