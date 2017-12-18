package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CausalRechazoDTO implements JSONAware{

	private Integer codigo;
	private String descripcion;		
	private String template;

	public CausalRechazoDTO(){

	}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	

		sb.append("\"codigo\"");
		sb.append(":");

		sb.append(this.codigo);

		sb.append(",");	

		sb.append("\"descripcion\"");
		sb.append(":");

		if(this.descripcion!=null){
			sb.append("\"" + JSONObject.escape(this.descripcion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append(",");

		sb.append("\"template\"");
		sb.append(":");

		if(this.template!=null){
			sb.append("\"" + JSONObject.escape(this.template.trim()) + "\"");			
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}