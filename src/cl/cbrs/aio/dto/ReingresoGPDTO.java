package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ReingresoGPDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = -7740236202572224989L;
	
	private Integer caratulaNueva;	
	private Integer caratulaOriginal;
	private Date fecha;
	private String usuario;
	
	public ReingresoGPDTO(){
		
	}

	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

		String d = "";

		if(this.fecha!=null){
			//Date fechaDate = this.fecha.getTime();

			d = sdf.format(this.fecha);
		}

		sb.append("{");	
		
		sb.append("\"caratulaNueva\"");
		sb.append(":");
		sb.append(this.caratulaNueva);
		sb.append(",");	

		sb.append("\"caratulaOriginal\"");
		sb.append(":");
		sb.append(this.caratulaOriginal);
		sb.append(",");
		
		sb.append("\"fecha\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(d) + "\"");	
		sb.append(",");
		
		sb.append("\"usuario\"");
		sb.append(":");
		if(this.usuario!=null){
			sb.append("\"" + JSONObject.escape(this.usuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}



	public Integer getCaratulaNueva() {
		return caratulaNueva;
	}



	public void setCaratulaNueva(Integer caratulaNueva) {
		this.caratulaNueva = caratulaNueva;
	}



	public Integer getCaratulaOriginal() {
		return caratulaOriginal;
	}



	public void setCaratulaOriginal(Integer caratulaOriginal) {
		this.caratulaOriginal = caratulaOriginal;
	}



	public Date getFecha() {
		return fecha;
	}



	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}