package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CuentaCorrienteDTO implements JSONAware{
		
	private Integer codigo;
	private String rut = "";
	private String institucion = "";
	private String encargado = "";
	private String telefono = "";
	private String tipo = "";
	private String boleta = "";
	private String email = "";
	private String direccion = "";
	private String direccionEntrega = "";
	private String encargadoEntrega = "";	

	public CuentaCorrienteDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"codigo\"");
		sb.append(":");

		sb.append(this.codigo);

		sb.append(",");	

		sb.append("\"rut\"");
		sb.append(":");
		
		if(this.rut!=null){
			sb.append("\"" + JSONObject.escape(this.rut.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	

		sb.append("\"institucion\"");
		sb.append(":");
		if(this.institucion!=null){
			sb.append("\"" + JSONObject.escape(this.institucion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");
		
		sb.append("\"encargado\"");
		sb.append(":");
		if(this.encargado!=null){
			sb.append("\"" + JSONObject.escape(this.encargado.trim()) + "\"");			
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
		sb.append(",");			
		
		sb.append("\"tipo\"");
		sb.append(":");
		if(this.tipo!=null){
			sb.append("\"" + JSONObject.escape(this.tipo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"boleta\"");
		sb.append(":");
		if(this.boleta!=null){
			sb.append("\"" + JSONObject.escape(this.boleta.trim()) + "\"");			
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
		
		sb.append("\"direccion\"");
		sb.append(":");
		if(this.direccion!=null){
			sb.append("\"" + JSONObject.escape(this.direccion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"direccionEntrega\"");
		sb.append(":");
		if(this.direccionEntrega!=null){
			sb.append("\"" + JSONObject.escape(this.direccionEntrega.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"encargadoEntrega\"");
		sb.append(":");
		if(this.encargadoEntrega!=null){
			sb.append("\"" + JSONObject.escape(this.encargadoEntrega.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}
	
	public String getEncargado() {
		return encargado;
	}

	public void setEncargado(String encargado) {
		this.encargado = encargado;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getBoleta() {
		return boleta;
	}

	public void setBoleta(String boleta) {
		this.boleta = boleta;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getDireccionEntrega() {
		return direccionEntrega;
	}

	public void setDireccionEntrega(String direccionEntrega) {
		this.direccionEntrega = direccionEntrega;
	}

	public String getEncargadoEntrega() {
		return encargadoEntrega;
	}

	public void setEncargadoEntrega(String encargadoEntrega) {
		this.encargadoEntrega = encargadoEntrega;
	}	
}