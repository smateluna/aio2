package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class FuncionarioDTO implements JSONAware{
	
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String rut;

	public FuncionarioDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		
		
		sb.append("\"nombre\"");
		sb.append(":");
		
		if(this.nombre!=null){
			sb.append("\"" + JSONObject.escape(this.nombre.trim()) + "\"");			
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
		
		sb.append("\"rut\"");
		sb.append(":");
		
		if(this.rut!=null){
			sb.append("\"" + JSONObject.escape(this.rut.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}
	
}