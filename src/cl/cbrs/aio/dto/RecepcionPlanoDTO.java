package cl.cbrs.aio.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.estado.CanalDTO;
import cl.cbrs.aio.dto.estado.RequirenteDTO;
import cl.cbrs.aio.dto.estado.TipoFormularioDTO;

public class RecepcionPlanoDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private Long id;	
	private Integer numero;
	private String letra;
	private String categoria;
	private String requirente;
	private RequirenteDTO requirenteDTO;
	private Date fechaRetiro;
	private Date fechaDevolucion;
	private String observacion;
	
	public RecepcionPlanoDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"id\"");
		sb.append(":");
		sb.append(this.id);
		sb.append(",");	

		sb.append("\"numero\"");
		sb.append(":");
		sb.append(this.numero);
		sb.append(",");	
		
		sb.append("\"letra\"");
		sb.append(":");
		if(this.letra!=null){
			sb.append("\"" + JSONObject.escape(this.letra.trim()) + "\"");
		}else{
			sb.append("null");				
		}
		sb.append(",");
		
		sb.append("\"categoria\"");
		sb.append(":");
		if(this.categoria!=null){
			sb.append("\"" + JSONObject.escape(this.categoria.trim()) + "\"");
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");		
		
		sb.append("\"fechaRetiro\"");
		sb.append(":");
		if(this.fechaRetiro!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaRetiro)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"fechaRetiroL\"");
		sb.append(":");
		if(this.fechaRetiro!=null)
			sb.append(this.fechaRetiro.getTime());
		else
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"fechaDevolucion\"");
		sb.append(":");
		if(this.fechaDevolucion!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaDevolucion)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");		

		sb.append("\"requirenteDTO\"");
		sb.append(":");
		if(this.requirenteDTO!=null){
			sb.append(this.requirenteDTO.toJSONString());
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");		
		
		sb.append("\"requirente\"");
		sb.append(":");
		if(this.requirente!=null){
			sb.append("\"" + JSONObject.escape(this.requirente.trim()) + "\"");
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");			
		
		sb.append("\"observacion\"");
		sb.append(":");
		if(this.observacion!=null){
			sb.append("\"" + JSONObject.escape(this.observacion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getLetra() {
		return letra;
	}

	public void setLetra(String letra) {
		this.letra = letra;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public RequirenteDTO getRequirenteDTO() {
		return requirenteDTO;
	}

	public void setRequirenteDTO(RequirenteDTO requirenteDTO) {
		this.requirenteDTO = requirenteDTO;
	}

	public Date getFechaRetiro() {
		return fechaRetiro;
	}

	public void setFechaRetiro(Date fechaRetiro) {
		this.fechaRetiro = fechaRetiro;
	}

	public Date getFechaDevolucion() {
		return fechaDevolucion;
	}

	public void setFechaDevolucion(Date fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getRequirente() {
		return requirente;
	}

	public void setRequirente(String requirente) {
		this.requirente = requirente;
	}

	
	
}