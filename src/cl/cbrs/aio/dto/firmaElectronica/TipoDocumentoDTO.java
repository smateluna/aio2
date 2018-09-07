package cl.cbrs.aio.dto.firmaElectronica;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TipoDocumentoDTO implements JSONAware{
	
	private Long codigo;
	private String descripcion;
	
	public TipoDocumentoDTO(){
		
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

		sb.append("}");

		return sb.toString();		
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}