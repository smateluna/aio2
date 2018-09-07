package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class BorradorDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = -673471670382125962L;
	
	private Integer borrador;
	private Integer folio;
	private String direccion;
	
	public BorradorDTO() {
	}

	public Integer getBorrador() {
		return borrador;
	}

	public void setBorrador(Integer borrador) {
		this.borrador = borrador;
	}

	public Integer getFolio() {
		return folio;
	}

	public void setFolio(Integer folio) {
		this.folio = folio;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"borrador\"");
		sb.append(":");

		sb.append(this.borrador);
		
		sb.append(",");
		
		sb.append("\"folio\"");
		sb.append(":");

		sb.append(this.folio);
		
		sb.append(",");
		
		sb.append("\"direccion\"");
		sb.append(":");

		if(this.direccion!=null){
			sb.append("\"" + JSONObject.escape(this.direccion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		

		sb.append("}");

		return sb.toString();		
	}
}