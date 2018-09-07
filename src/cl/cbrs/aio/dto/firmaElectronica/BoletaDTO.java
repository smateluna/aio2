package cl.cbrs.aio.dto.firmaElectronica;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class BoletaDTO implements JSONAware{
	
	private Integer numero;
	private String url;
	
	public BoletaDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"numero\"");
		sb.append(":");

		sb.append(this.numero);

		sb.append(",");	
		
		sb.append("\"url\"");
		sb.append(":");
		
		if(this.url!=null){
			sb.append("\"" + JSONObject.escape(this.url.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}