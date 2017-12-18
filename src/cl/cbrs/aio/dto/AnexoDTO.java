package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;

public class AnexoDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5607535884631474352L;
	
	private java.lang.Long idAnexo;
	private java.lang.String nombre;
	private java.lang.String apPaterno;
	private java.lang.String apMaterno;
	private java.lang.String registro;
	private java.lang.String anexo;
	private java.lang.String codSeccion;
	private java.lang.String rut;
	private java.lang.String piso;
	private java.lang.String correo;
	
	public AnexoDTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"idAnexo\"");
		sb.append(":");
		sb.append(this.idAnexo);		
		sb.append(",");
		
		sb.append("\"nombre\"");
		sb.append(":");
		if(this.nombre!=null)
			sb.append("\""+this.nombre.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");

		sb.append("\"apPaterno\"");
		sb.append(":");
		if(this.apPaterno!=null)
			sb.append("\""+this.apPaterno.trim()+"\"");	
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"apMaterno\"");
		sb.append(":");
		if(this.apMaterno!=null)
			sb.append("\""+this.apMaterno.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"registro\"");
		sb.append(":");
		if(this.registro!=null)
			sb.append("\""+this.registro.trim()+"\"");	
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"anexo\"");
		sb.append(":");
		if(this.anexo!=null)
			sb.append("\""+this.anexo.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"codSeccion\"");
		sb.append(":");
		if(this.codSeccion!=null)
			sb.append("\""+this.codSeccion.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"rut\"");
		sb.append(":");
		if(this.rut!=null)
			sb.append("\""+this.rut.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"piso\"");
		sb.append(":");
		if(this.piso!=null)
			sb.append("\""+this.piso.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"correo\"");
		sb.append(":");
		if(this.correo!=null)
			sb.append("\""+this.correo.trim()+"\"");
		else
			sb.append("\"\"");
		
		sb.append("}");

		return sb.toString();		
	}

	public java.lang.Long getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(java.lang.Long idAnexo) {
		this.idAnexo = idAnexo;
	}

	public java.lang.String getNombre() {
		return nombre;
	}

	public void setNombre(java.lang.String nombre) {
		this.nombre = nombre;
	}

	public java.lang.String getApPaterno() {
		return apPaterno;
	}

	public void setApPaterno(java.lang.String apPaterno) {
		this.apPaterno = apPaterno;
	}

	public java.lang.String getApMaterno() {
		return apMaterno;
	}

	public void setApMaterno(java.lang.String apMaterno) {
		this.apMaterno = apMaterno;
	}

	public java.lang.String getRegistro() {
		return registro;
	}

	public void setRegistro(java.lang.String registro) {
		this.registro = registro;
	}

	public java.lang.String getAnexo() {
		return anexo;
	}

	public void setAnexo(java.lang.String anexo) {
		this.anexo = anexo;
	}

	public java.lang.String getCodSeccion() {
		return codSeccion;
	}

	public void setCodSeccion(java.lang.String codSeccion) {
		this.codSeccion = codSeccion;
	}

	public java.lang.String getRut() {
		return rut;
	}

	public void setRut(java.lang.String rut) {
		this.rut = rut;
	}

	public java.lang.String getPiso() {
		return piso;
	}

	public void setPiso(java.lang.String piso) {
		this.piso = piso;
	}

	public java.lang.String getCorreo() {
		return correo;
	}

	public void setCorreo(java.lang.String correo) {
		this.correo = correo;
	}

}