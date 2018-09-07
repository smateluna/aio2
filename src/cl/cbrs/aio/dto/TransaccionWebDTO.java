package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class TransaccionWebDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 4758298456601324860L;
	
	private Long id;
	private String fechaTransaccion;
	private Long fechaTransaccionL;
	private Long montoTransaccion;
	private Integer estadoActualTransaccion;
	private String pagoNombreBanco;
	private String pagoFechaPago;
	private Long pagoFechaPagoL;
	private ArrayList<ProductoWebDTO> productoWebDTOs;
	
	public TransaccionWebDTO() {
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");
		sb.append(this.id);		
		sb.append(",");		
		
		sb.append("\"fechaTransaccion\"");
		sb.append(":");
		if(this.fechaTransaccion!=null){
			sb.append("\"" + JSONObject.escape(this.fechaTransaccion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}				
		sb.append(",");		
		
		sb.append("\"fechaTransaccionL\"");
		sb.append(":");
		sb.append(this.fechaTransaccionL);		
		sb.append(",");	
		
		sb.append("\"montoTransaccion\"");
		sb.append(":");
		sb.append(this.montoTransaccion);		
		sb.append(",");	
		
		sb.append("\"estadoActualTransaccion\"");
		sb.append(":");
		sb.append(this.estadoActualTransaccion);		
		sb.append(",");	
		
		sb.append("\"pagoNombreBanco\"");
		sb.append(":");
		if(this.pagoNombreBanco!=null)
			sb.append("\"" + JSONObject.escape(this.pagoNombreBanco.trim()) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");			
			
		sb.append("\"pagoFechaPago\"");
		sb.append(":");
		if(this.pagoFechaPago!=null){
			sb.append("\"" + JSONObject.escape(this.pagoFechaPago.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}				
		sb.append(",");	
		
		sb.append("\"pagoFechaPagoL\"");
		sb.append(":");
		sb.append(this.pagoFechaPagoL);		
		sb.append(",");
				
		JSONArray productosJSONArray = new JSONArray();		
		if(this.productoWebDTOs!=null){
			for(ProductoWebDTO productoWebDTO : this.productoWebDTOs){				
				productosJSONArray.add(productoWebDTO);
			}			
		}
		sb.append("\"productoWebDTOs\"");
		sb.append(":");		
		sb.append(productosJSONArray);		

		sb.append("}");

		return sb.toString();		
	}

	public Integer getEstadoActualTransaccion() {
		return estadoActualTransaccion;
	}

	public void setEstadoActualTransaccion(Integer estadoActualTransaccion) {
		this.estadoActualTransaccion = estadoActualTransaccion;
	}

	public String getPagoNombreBanco() {
		return pagoNombreBanco;
	}

	public void setPagoNombreBanco(String pagoNombreBanco) {
		this.pagoNombreBanco = pagoNombreBanco;
	}

	public String getPagoFechaPago() {
		return pagoFechaPago;
	}

	public void setPagoFechaPago(String pagoFechaPago) {
		this.pagoFechaPago = pagoFechaPago;
	}

	public Long getPagoFechaPagoL() {
		return pagoFechaPagoL;
	}

	public void setPagoFechaPagoL(Long pagoFechaPagoL) {
		this.pagoFechaPagoL = pagoFechaPagoL;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public Long getFechaTransaccionL() {
		return fechaTransaccionL;
	}

	public void setFechaTransaccionL(Long fechaTransaccionL) {
		this.fechaTransaccionL = fechaTransaccionL;
	}

	public Long getMontoTransaccion() {
		return montoTransaccion;
	}

	public void setMontoTransaccion(Long montoTransaccion) {
		this.montoTransaccion = montoTransaccion;
	}

	public ArrayList<ProductoWebDTO> getProductoWebDTOs() {
		return productoWebDTOs;
	}

	public void setProductoWebDTOs(ArrayList<ProductoWebDTO> productoWebDTOs) {
		this.productoWebDTOs = productoWebDTOs;
	}


	
}