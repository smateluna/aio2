package cl.cbrs.aio.dto.firmaElectronica;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CertificadoFNADTO implements JSONAware{
	
	private Integer registro;
	private Integer derechos;
	
	public CertificadoFNADTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"registro\"");
		sb.append(":");
		sb.append(this.registro);
		sb.append(",");	
		
		sb.append("\"derechos\"");
		sb.append(":");
		sb.append(this.derechos);

		sb.append("}");

		return sb.toString();		
	}

	public Integer getRegistro() {
		return registro;
	}

	public void setRegistro(Integer registro) {
		this.registro = registro;
	}

	public Integer getDerechos() {
		return derechos;
	}

	public void setDerechos(Integer derechos) {
		this.derechos = derechos;
	}

}