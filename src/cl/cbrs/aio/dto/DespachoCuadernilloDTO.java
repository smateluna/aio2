package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class DespachoCuadernilloDTO implements JSONAware, Serializable{

	private static final long serialVersionUID = 5611002389174682354L;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Long id;
	private Date fecha;
	private String usuario;
	private Integer cuadernillo;
	private Integer ano;

	public DespachoCuadernilloDTO(){

	}

	@SuppressWarnings("unchecked")
	public String toJSONString(){

		StringBuffer sb = new StringBuffer();

		sb.append("{");	

		sb.append("\"id\"");
		sb.append(":");
		sb.append(this.id);
		sb.append(",");
		
		sb.append("\"fecha\"");
		sb.append(":");
		if(this.fecha!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fecha)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");		

		sb.append("\"usuario\"");
		sb.append(":");

		if(this.usuario!=null){
			sb.append("\"" + JSONObject.escape(this.usuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		sb.append(",");	

		sb.append("\"cuadernillo\"");
		sb.append(":");
		sb.append(this.cuadernillo);
		sb.append(",");	
		
		sb.append("\"ano\"");
		sb.append(":");
		sb.append(this.ano);

		

		sb.append("}");

		return sb.toString();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getCuadernillo() {
		return cuadernillo;
	}

	public void setCuadernillo(Integer cuadernillo) {
		this.cuadernillo = cuadernillo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	

}