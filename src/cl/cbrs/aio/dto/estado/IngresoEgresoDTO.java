package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class IngresoEgresoDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
//	String montof = "";
//	if(dto.getMonto()!=null){
//		montof = NumberFormat.getInstance(new Locale("ES", "CL")).format(dto.getMonto());
//	}
//
//	ingresoEgreso.put("idIngresoEgreso", dto.getId());
//	ingresoEgreso.put("caratula", dto.getCaratula());
//	ingresoEgreso.put("fecha", fechaIngresoEgreso);
//	ingresoEgreso.put("idMedio", dto.getIdMedio());
//	ingresoEgreso.put("descMedio", dto.getDescMedio());
//	ingresoEgreso.put("idTipo", dto.getIdTipo());
//	ingresoEgreso.put("descTipo", dto.getDescTipo());
//	ingresoEgreso.put("monto", montof);
//	
//	ingresoEgreso.put("caja", dto.getCaja());
//	
//	ingresoEgreso.put("idTransaccion", dto.getIdTransaccion());
	
	private Long id;
	private Date fecha;
	private Long fechaL;
	
	private Integer idMedio;
	private String descMedio;
	
	private Long idTipo;
	private String descTipo;
	
	private Long monto;
	
	private String caja;
	
	private Long idTransaccion;
	
	public IngresoEgresoDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");

		sb.append(this.id);

		sb.append(",");	
		
		sb.append("\"fechaL\"");
		sb.append(":");
		sb.append(this.fechaL);
		sb.append(",");	
		
		sb.append("\"fecha\"");
		sb.append(":");
		if(this.fecha!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fecha)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"idMedio\"");
		sb.append(":");

		sb.append(this.idMedio);

		sb.append(",");	
		
		sb.append("\"descMedio\"");
		sb.append(":");
		
		if(this.descMedio!=null){
			sb.append("\"" + JSONObject.escape(this.descMedio.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"idTipo\"");
		sb.append(":");

		sb.append(this.idTipo);

		sb.append(",");	
		
		sb.append("\"descTipo\"");
		sb.append(":");
		
		if(this.descTipo!=null){
			sb.append("\"" + JSONObject.escape(this.descTipo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"monto\"");
		sb.append(":");

		sb.append(this.monto);
		
		sb.append(",");	
		
		sb.append("\"caja\"");
		sb.append(":");
		
		if(this.caja!=null){
			sb.append("\"" + JSONObject.escape(this.caja.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"idTransaccion\"");
		sb.append(":");

		sb.append(this.idTransaccion);


		sb.append("}");

		return sb.toString();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIdMedio() {
		return idMedio;
	}

	public void setIdMedio(Integer idMedio) {
		this.idMedio = idMedio;
	}

	public String getDescMedio() {
		return descMedio;
	}

	public void setDescMedio(String descMedio) {
		this.descMedio = descMedio;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public String getDescTipo() {
		return descTipo;
	}

	public void setDescTipo(String descTipo) {
		this.descTipo = descTipo;
	}

	public Long getMonto() {
		return monto;
	}

	public void setMonto(Long monto) {
		this.monto = monto;
	}

	public String getCaja() {
		return caja;
	}

	public void setCaja(String caja) {
		this.caja = caja;
	}

	public Long getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(Long idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Long getFechaL() {
		return fechaL;
	}

	public void setFechaL(Long fechaL) {
		this.fechaL = fechaL;
	}
}