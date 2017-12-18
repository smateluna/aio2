package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ProductoReceptorEmailDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 4758298456601324860L;
	
	private Long id;
	private String nombreCorto;
	private String eMail;
	
	public ProductoReceptorEmailDTO() {
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");
		sb.append(this.id);		
		sb.append(",");		
		
		sb.append("\"nombreCorto\"");
		sb.append(":");
		if(this.nombreCorto!=null){
			sb.append("\"" + JSONObject.escape(this.nombreCorto.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}				
		sb.append(",");		
		
		sb.append("\"eMail\"");
		sb.append(":");
		if(this.eMail!=null)
			sb.append("\"" + JSONObject.escape(this.eMail.trim()) + "\"");
		else
			sb.append("\"\"");
		

		sb.append("}");

		return sb.toString();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreCorto() {
		return nombreCorto;
	}

	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	
}