package cl.cbrs.aio.dto;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ProductoWebDTO implements JSONAware{
		
	private Long idUsuarioWeb;
	private Long idTransaccion;
	private String descripcionProducto;
	private ArrayList<String> productoGlosa;
	private ArrayList<LiquidacionCaratulaDTO> liquidacionCaratulaDTOs;
	private Long valorUnitario;
	private Integer cantidad;
	private Long valorReal;
	private String apellidoPaternoBoleta;
	private String apellidoMaternoBoleta;
	private String nombreBoleta;
	private String rutBoleta;
	private String dvBoleta;
	private ArrayList<ProductoReceptorEmailDTO> productoReceptorEmailDTOs;
	
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
		
		sb.append("\"valorUnitario\"");
		sb.append(":");
		sb.append(this.valorUnitario);		
		sb.append(",");	
		
		sb.append("\"cantidad\"");
		sb.append(":");
		sb.append(this.cantidad);		
		sb.append(",");
		
		sb.append("\"valorReal\"");
		sb.append(":");
		sb.append(this.valorReal);		
		sb.append(",");
		
		sb.append("\"apellidoPaternoBoleta\"");
		sb.append(":");
		if(this.apellidoPaternoBoleta!=null){
			sb.append("\"" + JSONObject.escape(this.apellidoPaternoBoleta.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"apellidoMaternoBoleta\"");
		sb.append(":");
		if(this.apellidoMaternoBoleta!=null){
			sb.append("\"" + JSONObject.escape(this.apellidoMaternoBoleta.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"nombreBoleta\"");
		sb.append(":");
		if(this.nombreBoleta!=null){
			sb.append("\"" + JSONObject.escape(this.nombreBoleta.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");		
		
		sb.append("\"rutBoleta\"");
		sb.append(":");
		if(this.rutBoleta!=null){
			sb.append("\"" + JSONObject.escape(this.rutBoleta.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"dvBoleta\"");
		sb.append(":");
		if(this.dvBoleta!=null){
			sb.append("\"" + JSONObject.escape(this.dvBoleta.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
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
		
		JSONArray liquidacionCaratulasJSONArray = new JSONArray();
		if(this.liquidacionCaratulaDTOs!=null){
			for(LiquidacionCaratulaDTO liquidacionCaratulaDTO : this.liquidacionCaratulaDTOs){				
				liquidacionCaratulasJSONArray.add(liquidacionCaratulaDTO);
			}			
		}
		sb.append("\"liquidacionCaratulaDTOs\"");
		sb.append(":");
		sb.append(liquidacionCaratulasJSONArray);		
		sb.append(",");		
		
		JSONArray emailsJSONArray = new JSONArray();
		if(this.productoReceptorEmailDTOs!=null){
			for(ProductoReceptorEmailDTO emailDTO : this.productoReceptorEmailDTOs){				
				emailsJSONArray.add(emailDTO);
			}			
		}
		sb.append("\"productoReceptorEmailDTOs\"");
		sb.append(":");
		sb.append(emailsJSONArray);		
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

	public ArrayList<LiquidacionCaratulaDTO> getLiquidacionCaratulaDTOs() {
		return liquidacionCaratulaDTOs;
	}

	public void setLiquidacionCaratulaDTOs(
			ArrayList<LiquidacionCaratulaDTO> liquidacionCaratulaDTOs) {
		this.liquidacionCaratulaDTOs = liquidacionCaratulaDTOs;
	}

	public Long getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Long valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Long getValorReal() {
		return valorReal;
	}

	public void setValorReal(Long valorReal) {
		this.valorReal = valorReal;
	}

	public String getApellidoPaternoBoleta() {
		return apellidoPaternoBoleta;
	}

	public void setApellidoPaternoBoleta(String apellidoPaternoBoleta) {
		this.apellidoPaternoBoleta = apellidoPaternoBoleta;
	}

	public String getApellidoMaternoBoleta() {
		return apellidoMaternoBoleta;
	}

	public void setApellidoMaternoBoleta(String apellidoMaternoBoleta) {
		this.apellidoMaternoBoleta = apellidoMaternoBoleta;
	}

	public String getNombreBoleta() {
		return nombreBoleta;
	}

	public void setNombreBoleta(String nombreBoleta) {
		this.nombreBoleta = nombreBoleta;
	}

	public String getRutBoleta() {
		return rutBoleta;
	}

	public void setRutBoleta(String rutBoleta) {
		this.rutBoleta = rutBoleta;
	}

	public String getDvBoleta() {
		return dvBoleta;
	}

	public void setDvBoleta(String dvBoleta) {
		this.dvBoleta = dvBoleta;
	}

	public ArrayList<ProductoReceptorEmailDTO> getProductoReceptorEmailDTOs() {
		return productoReceptorEmailDTOs;
	}

	public void setProductoReceptorEmailDTOs(
			ArrayList<ProductoReceptorEmailDTO> productoReceptorEmailDTOs) {
		this.productoReceptorEmailDTOs = productoReceptorEmailDTOs;
	}
}