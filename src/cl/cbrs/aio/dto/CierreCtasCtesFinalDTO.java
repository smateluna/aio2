package cl.cbrs.aio.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

public class CierreCtasCtesFinalDTO {
	
	private Integer caratula;
	private Integer codigo;
	private Integer valorReal;
	private String clienteCtaCte;
	private Date fechaRev;
	private Integer mitadDeMes;
	private Integer cierreActual;
	
	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");			
		
		sb.append("\"caratula\"");
		sb.append(":");
		sb.append(this.caratula);		
		sb.append(",");	
		
		sb.append("\"codigo\"");
		sb.append(":");
		sb.append(this.codigo);		
		sb.append(",");
		
		sb.append("\"valorReal\"");
		sb.append(":");
		sb.append(this.valorReal);		
		sb.append(",");	
		
		sb.append("\"clienteCtaCte\"");
		sb.append(":");		
		if(this.clienteCtaCte!=null){
			sb.append("\"" + JSONObject.escape(this.clienteCtaCte.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");	
		
		sb.append("\"fechaRev\"");
		sb.append(":");	
		if(this.fechaRev!=null){
			String fecha = sdf.format(this.fechaRev);
			sb.append("\"" + JSONObject.escape(fecha.trim()) + "\"");			
		}else{
			sb.append("\"\"");
		}
		sb.append(",");		
		
		sb.append("\"mitadDeMes\"");
		sb.append(":");
		sb.append(this.mitadDeMes);		
		sb.append(",");	
		
		sb.append("\"cierreActual\"");
		sb.append(":");
		sb.append(this.cierreActual);		

		sb.append("}");

		return sb.toString();		
	}	
	
	public CierreCtasCtesFinalDTO(){
		
	}

	public Integer getCaratula() {
		return caratula;
	}

	public void setCaratula(Integer caratula) {
		this.caratula = caratula;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getValorReal() {
		return valorReal;
	}

	public void setValorReal(Integer valorReal) {
		this.valorReal = valorReal;
	}

	public String getClienteCtaCte() {
		return clienteCtaCte;
	}

	public void setClienteCtaCte(String clienteCtaCte) {
		this.clienteCtaCte = clienteCtaCte;
	}

	public Date getFechaRev() {
		return fechaRev;
	}

	public void setFechaRev(Date fechaRev) {
		this.fechaRev = fechaRev;
	}

	public Integer getMitadDeMes() {
		return mitadDeMes;
	}

	public void setMitadDeMes(Integer mitadDeMes) {
		this.mitadDeMes = mitadDeMes;
	}

	public Integer getCierreActual() {
		return cierreActual;
	}

	public void setCierreActual(Integer cierreActual) {
		this.cierreActual = cierreActual;
	}
	
	

}
