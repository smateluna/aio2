package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class RequirenteDTO implements JSONAware{
		
	private String rut;	
	private String dv;
	
	private String nombres;
	private String apellidoPaterno;
	private String apellidoMaterno;
	
	private String email;
	private String giro;
	
	private String direccion;
	private String telefono;
		
	public RequirenteDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"rut\"");
		sb.append(":");

		if(this.rut!=null){
			sb.append("\"" + JSONObject.escape(this.rut.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	

		sb.append("\"dv\"");
		sb.append(":");
		
		if(this.dv!=null){
			sb.append("\"" + JSONObject.escape(this.dv.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	

		sb.append("\"nombres\"");
		sb.append(":");
		
		if(this.nombres!=null){
			sb.append("\"" + JSONObject.escape(this.nombres.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"apellidoPaterno\"");
		sb.append(":");
		
		if(this.apellidoPaterno!=null){
			sb.append("\"" + JSONObject.escape(this.apellidoPaterno.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"apellidoMaterno\"");
		sb.append(":");
		
		if(this.apellidoMaterno!=null){
			sb.append("\"" + JSONObject.escape(this.apellidoMaterno.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"email\"");
		sb.append(":");
		
		if(this.email!=null){
			sb.append("\"" + JSONObject.escape(this.email.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"giro\"");
		sb.append(":");
		
		if(this.giro!=null){
			sb.append("\"" + JSONObject.escape(this.giro.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"direccion\"");
		sb.append(":");
		
		if(this.direccion!=null){
			sb.append("\"" + JSONObject.escape(this.direccion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	

		sb.append("\"telefono\"");
		sb.append(":");
		
		if(this.telefono!=null){
			sb.append("\"" + JSONObject.escape(this.telefono.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}


	public String getDv() {
		return dv;
	}

	public void setDv(String dv) {
		this.dv = dv;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGiro() {
		return giro;
	}

	public void setGiro(String giro) {
		this.giro = giro;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}
}