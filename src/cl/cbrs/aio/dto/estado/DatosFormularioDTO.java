package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class DatosFormularioDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	private Long numeroCaratula;	
	private Date fechaIngreso;
	private Long fechaIngresoL;
	
	private TipoFormularioDTO tipoFormularioDTO;
	private CanalDTO canalDTO;
	
	private Long valorTasado;
	private Long valorPagado;
	private Long valorReal;
	private Long diferencia;
	
	private String estado;
	
	private Integer codigo;
	private String clienteCuentaCorriente;
	private String usuarioCreador;
	
	public DatosFormularioDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
		
		sb.append("\"numeroCaratula\"");
		sb.append(":");

		sb.append(this.numeroCaratula);

		sb.append(",");	

		sb.append("\"fechaIngresoL\"");
		sb.append(":");
		sb.append(this.fechaIngresoL);			
		sb.append(",");	
		
		sb.append("\"fechaIngreso\"");
		sb.append(":");
		if(this.fechaIngreso!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaIngreso)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");

		sb.append("\"tipoFormularioDTO\"");
		sb.append(":");

		if(this.tipoFormularioDTO!=null){
			sb.append(this.tipoFormularioDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");
		
		sb.append("\"canalDTO\"");
		sb.append(":");
		
		if(this.canalDTO!=null){
			sb.append(this.canalDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"valorTasado\"");
		sb.append(":");

		sb.append(this.valorTasado);
		
		sb.append(",");
		
		sb.append("\"valorPagado\"");
		sb.append(":");

		sb.append(this.valorPagado);
		
		sb.append(",");
		
		sb.append("\"valorReal\"");
		sb.append(":");

		sb.append(this.valorReal);

		sb.append(",");
		
		sb.append("\"diferencia\"");
		sb.append(":");

		sb.append(this.diferencia);
		
		sb.append(",");
		
		sb.append("\"usuarioCreador\"");
		sb.append(":");
		
		if(this.usuarioCreador!=null){
			sb.append("\"" + JSONObject.escape(this.usuarioCreador.trim()) + "\"");
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"estado\"");
		sb.append(":");

		if(this.estado!=null){
			sb.append("\"" + JSONObject.escape(this.estado.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"codigo\"");
		sb.append(":");

		sb.append(this.codigo);
		
		sb.append(",");	
		
		sb.append("\"clienteCuentaCorriente\"");
		sb.append(":");
		
		if(this.clienteCuentaCorriente!=null){
			sb.append("\"" + JSONObject.escape(this.clienteCuentaCorriente.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Long getNumeroCaratula() {
		return numeroCaratula;
	}

	public void setNumeroCaratula(Long numeroCaratula) {
		this.numeroCaratula = numeroCaratula;
	}

	public TipoFormularioDTO getTipoFormularioDTO() {
		return tipoFormularioDTO;
	}

	public void setTipoFormularioDTO(TipoFormularioDTO tipoFormularioDTO) {
		this.tipoFormularioDTO = tipoFormularioDTO;
	}

	public CanalDTO getCanalDTO() {
		return canalDTO;
	}

	public void setCanalDTO(CanalDTO canalDTO) {
		this.canalDTO = canalDTO;
	}

	public Long getValorTasado() {
		return valorTasado;
	}

	public void setValorTasado(Long valorTasado) {
		this.valorTasado = valorTasado;
	}

	public Long getValorPagado() {
		return valorPagado;
	}

	public void setValorPagado(Long valorPagado) {
		this.valorPagado = valorPagado;
	}

	public Long getValorReal() {
		return valorReal;
	}

	public void setValorReal(Long valorReal) {
		this.valorReal = valorReal;
	}

	public Long getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(Long diferencia) {
		this.diferencia = diferencia;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getClienteCuentaCorriente() {
		return clienteCuentaCorriente;
	}

	public void setClienteCuentaCorriente(String clienteCuentaCorriente) {
		this.clienteCuentaCorriente = clienteCuentaCorriente;
	}

	public String getUsuarioCreador() {
		return usuarioCreador;
	}

	public void setUsuarioCreador(String usuarioCreador) {
		this.usuarioCreador = usuarioCreador;
	}

	public Long getFechaIngresoL() {
		return fechaIngresoL;
	}

	public void setFechaIngresoL(Long fechaIngresoL) {
		this.fechaIngresoL = fechaIngresoL;
	}
	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
}