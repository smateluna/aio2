package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class NaturalezaDTO implements JSONAware{
	
    private Integer codNaturaleza;
    private String descNaturaleza;
    private String registro;
    private String tipo;
	
	public NaturalezaDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"codNaturaleza\"");
		sb.append(":");
		sb.append(this.codNaturaleza);
		sb.append(",");	
		
		sb.append("\"descNaturaleza\"");
		sb.append(":");		
		if(this.descNaturaleza!=null){
			sb.append("\"" + JSONObject.escape(this.descNaturaleza.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");		
		
		sb.append("\"registro\"");
		sb.append(":");		
		if(this.registro!=null){
			sb.append("\"" + JSONObject.escape(this.registro.trim()) + "\"");			
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


		sb.append("}");

		return sb.toString();		
	}

	public Integer getCodNaturaleza() {
		return codNaturaleza;
	}

	public void setCodNaturaleza(Integer codNaturaleza) {
		this.codNaturaleza = codNaturaleza;
	}

	public String getDescNaturaleza() {
		return descNaturaleza;
	}

	public void setDescNaturaleza(String descNaturaleza) {
		this.descNaturaleza = descNaturaleza;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}