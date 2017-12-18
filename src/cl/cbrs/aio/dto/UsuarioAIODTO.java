package cl.cbrs.aio.dto;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONAware;

public class UsuarioAIODTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 5607535884631474352L;
	
	private String nombre;
	private String path;
	private HttpSession httpSession;
	private String userAgent;
	private Long fechaUltimoAccesoL;
	private Long fechaCreacionL;
	private String ip;
	private String perfil;
	
	public UsuarioAIODTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"nombre\"");
		sb.append(":");
		if(this.nombre!=null)
			sb.append("\""+this.nombre.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");

		sb.append("\"path\"");
		sb.append(":");
		if(this.path!=null)
			sb.append("\""+this.path.trim()+"\"");	
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"ip\"");
		sb.append(":");
		if(this.nombre!=null)
			sb.append("\""+this.ip.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"perfil\"");
		sb.append(":");
		if(this.perfil!=null)
			sb.append("\""+this.perfil.trim()+"\"");
		else
			sb.append("\"\"");
		sb.append(",");		

		sb.append("\"userAgent\"");
		sb.append(":");
		if(this.userAgent!=null)
			sb.append("\""+this.userAgent.trim()+"\"");	
		else
			sb.append("\"\"");	
		sb.append(",");

		sb.append("\"fechaUltimoAccesoL\"");
		sb.append(":");
		sb.append(this.fechaUltimoAccesoL);	
		sb.append(",");

		sb.append("\"fechaCreacionL\"");
		sb.append(":");
		sb.append(this.fechaCreacionL);	
		
		sb.append("}");

		return sb.toString();		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Long getFechaUltimoAccesoL() {
		return fechaUltimoAccesoL;
	}

	public void setFechaUltimoAccesoL(Long fechaUltimoAccesoL) {
		this.fechaUltimoAccesoL = fechaUltimoAccesoL;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public Long getFechaCreacionL() {
		return fechaCreacionL;
	}

	public void setFechaCreacionL(Long fechaCreacionL) {
		this.fechaCreacionL = fechaCreacionL;
	}


}