package cl.cbrs.aio.dto.estado;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ProductoWebDTO implements JSONAware{
		
	private Long idUsuarioWeb;
	private Long idTransaccion;
	private String descripcionProducto;
	private ArrayList<String> productoGlosa;
	
	private String nameUsuarioWeb;
	
	public ProductoWebDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"idUsuarioWeb\"");
		sb.append(":");

		sb.append(this.idUsuarioWeb);
		
		sb.append(",");
		
		sb.append("\"idTransaccion\"");
		sb.append(":");

		sb.append(this.idTransaccion);
		
		sb.append(",");
		
		sb.append("\"descripcionProducto\"");
		sb.append(":");

		if(this.descripcionProducto!=null){
			sb.append("\"" + JSONObject.escape(this.descripcionProducto.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	

		JSONArray productoGlosaJSONArray = new JSONArray();

		if(this.productoGlosa!=null){
			for(String detalle : this.productoGlosa){				
				productoGlosaJSONArray.add(detalle);
			}			
		}

		sb.append("\"productoGlosa\"");
		sb.append(":");

		sb.append(productoGlosaJSONArray);
		
		sb.append(",");
		
		sb.append("\"nameUsuarioWeb\"");
		sb.append(":");

		if(this.nameUsuarioWeb!=null){
			sb.append("\"" + JSONObject.escape(this.nameUsuarioWeb.trim()) + "\"");			
		}else{
			sb.append("\"\"");	
		}	

		sb.append("}");

		return sb.toString();		
	}

	public Long getIdUsuarioWeb() {
		return idUsuarioWeb;
	}

	public void setIdUsuarioWeb(Long idUsuarioWeb) {
		this.idUsuarioWeb = idUsuarioWeb;
	}

	public Long getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(Long idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public ArrayList<String> getProductoGlosa() {
		return productoGlosa;
	}

	public void setProductoGlosa(ArrayList<String> productoGlosa) {
		this.productoGlosa = productoGlosa;
	}

	public String getNameUsuarioWeb() {
		return nameUsuarioWeb;
	}

	public void setNameUsuarioWeb(String nameUsuarioWeb) {
		this.nameUsuarioWeb = nameUsuarioWeb;
	}
}