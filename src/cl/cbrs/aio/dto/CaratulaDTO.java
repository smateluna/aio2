package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CaratulaDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 4758298456601324860L;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Long numeroCaratula;
	private Long fechaCreacionL;
	private Date fechaCreacion;
	private Long valorPagado;
	private Long valorReal;
	private Long diferencia;
	
	private Integer codigo;
	
    private Long idNotarioElectronico;
	private String codigoDocumentoElectronico;
	private Integer idCanal;
	private String canalTexto;
	private Integer estadoDescargaEscri;
	private Integer empresa;
	private String estadoForm;

	private EstadoActualCaratulaDTO estadoActualCaratulaDTO;
	private TipoFormularioDTO tipoFormularioDTO;
	private InscripcionDigitalDTO inscripcionDigitalDTO;
	private ProductoWebDTO productoWebDTO;
	private ArrayList<TareaDTO> tareaDTOs;
//	private ArrayList<BitacoraDTO> bitacoraDTOs;
	
	private Integer origenCreacion;
    private Long idTransaccion;
	
	public CaratulaDTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"numeroCaratula\"");
		sb.append(":");
		sb.append(this.numeroCaratula);		
		sb.append(",");
		
		sb.append("\"codigo\"");
		sb.append(":");
		sb.append(this.codigo);		
		sb.append(",");		
		
		sb.append("\"origenCreacion\"");
		sb.append(":");
		sb.append(this.origenCreacion);		
		sb.append(",");
		
		sb.append("\"fechaCreacionL\"");
		sb.append(":");
		sb.append(this.fechaCreacionL);		
		sb.append(",");		
		
		sb.append("\"fechaCreacion\"");
		sb.append(":");
		if(this.fechaCreacion!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaCreacion)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");			
		
		sb.append("\"productoWebDTO\"");
		sb.append(":");
		if(this.productoWebDTO!=null)
			sb.append(this.productoWebDTO.toJSONString());
		else 
			sb.append("\"\"");
		sb.append(",");
		
//		JSONArray bitacoraDTOsJSONArray = new JSONArray();
//		if(this.bitacoraDTOs!=null){
//			for(BitacoraDTO bit : this.bitacoraDTOs){				
//				bitacoraDTOsJSONArray.add(bit);
//			}			
//		}
//		sb.append("\"bitacoraDTOs\"");
//		sb.append(":");
//		sb.append(bitacoraDTOsJSONArray);		
//		sb.append(",");		
		
		sb.append("\"valorPagado\"");
		sb.append(":");
		sb.append(this.valorPagado);		
		sb.append(",");
		
		sb.append("\"valorReal\"");
		sb.append(":");
		sb.append(this.valorReal);
		sb.append(",");
		
		sb.append("\"idNotarioElectronico\"");
		sb.append(":");
		sb.append(this.idNotarioElectronico);
		sb.append(",");
		
		sb.append("\"codigoDocumentoElectronico\"");
		sb.append(":");
		if(this.codigoDocumentoElectronico!=null)
			sb.append("\"" + JSONObject.escape(this.codigoDocumentoElectronico.trim()) + "\"");
		else
			sb.append("\"0\"");
		
		sb.append(",");
		
		sb.append("\"idCanal\"");
		sb.append(":");
		sb.append(this.idCanal);
		sb.append(",");
		
		sb.append("\"canalTexto\"");
		sb.append(":");
		sb.append("\"" + this.canalTexto + "\"");
		sb.append(",");
		
		sb.append("\"estadoDescargaEscri\"");
		sb.append(":");
		sb.append(this.estadoDescargaEscri);
		sb.append(",");
		
		sb.append("\"empresa\"");
		sb.append(":");
		sb.append(this.empresa);
		sb.append(",");
				
		sb.append("\"diferencia\"");
		sb.append(":");

		sb.append(this.diferencia);
		
		sb.append(",");	
		
		sb.append("\"estadoForm\"");
		sb.append(":");
		sb.append(this.estadoForm);
		sb.append(",");
		
		JSONArray tareasJSONArray = new JSONArray();		
		if(this.tareaDTOs!=null){
			for(TareaDTO tarea : this.tareaDTOs){				
				tareasJSONArray.add(tarea);
			}			
		}
		sb.append("\"tareaDTOs\"");
		sb.append(":");		
		sb.append(tareasJSONArray);
		sb.append(",");			
		
		sb.append("\"tipoFormularioDTO\"");
		sb.append(":");

		if(this.tipoFormularioDTO!=null)
			sb.append(this.tipoFormularioDTO.toJSONString());
		else 
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"inscripcionDigitalDTO\"");
		sb.append(":");
		if(this.inscripcionDigitalDTO!=null)
			sb.append(this.inscripcionDigitalDTO.toJSONString());
		else
			sb.append("\"\"");
		
		sb.append(",");	
		
		sb.append("\"estadoActualCaratulaDTO\"");
		sb.append(":");

		if(this.estadoActualCaratulaDTO!=null)
			sb.append(this.estadoActualCaratulaDTO.toJSONString());
		else
			sb.append("\"\"");

		sb.append("}");

		return sb.toString();		
	}

	public Long getNumeroCaratula() {
		return numeroCaratula;
	}

	public void setNumeroCaratula(Long numeroCaratula) {
		this.numeroCaratula = numeroCaratula;
	}

	public EstadoActualCaratulaDTO getEstadoActualCaratulaDTO() {
		return estadoActualCaratulaDTO;
	}

	public void setEstadoActualCaratulaDTO(
			EstadoActualCaratulaDTO estadoActualCaratulaDTO) {
		this.estadoActualCaratulaDTO = estadoActualCaratulaDTO;
	}

	public TipoFormularioDTO getTipoFormularioDTO() {
		return tipoFormularioDTO;
	}

	public void setTipoFormularioDTO(TipoFormularioDTO tipoFormularioDTO) {
		this.tipoFormularioDTO = tipoFormularioDTO;
	}

	public void setInscripcionDigitalDTO(InscripcionDigitalDTO inscripcionDigitalDTO) {
		this.inscripcionDigitalDTO = inscripcionDigitalDTO;
	}

	public InscripcionDigitalDTO getInscripcionDigitalDTO() {
		return inscripcionDigitalDTO;
	}

	public ProductoWebDTO getProductoWebDTO() {
		return productoWebDTO;
	}

	public void setProductoWebDTO(ProductoWebDTO productoDTO) {
		this.productoWebDTO = productoDTO;
	}

	public ArrayList<TareaDTO> getTareaDTOs() {
		return tareaDTOs;
	}

	public void setTareaDTOs(ArrayList<TareaDTO> tareaDTOs) {
		this.tareaDTOs = tareaDTOs;
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

	public Long getIdNotarioElectronico() {
		return idNotarioElectronico;
	}

	public void setIdNotarioElectronico(Long idNotarioElectronico) {
		this.idNotarioElectronico = idNotarioElectronico;
	}

	public String getCodigoDocumentoElectronico() {
		return codigoDocumentoElectronico;
	}

	public void setCodigoDocumentoElectronico(String codigoDocumentoElectronico) {
		this.codigoDocumentoElectronico = codigoDocumentoElectronico;
	}

	public Integer getIdCanal() {
		return idCanal;
	}

	public void setIdCanal(Integer idCanal) {
		this.idCanal = idCanal;
	}

	public Integer getEstadoDescargaEscri() {
		return estadoDescargaEscri;
	}

	public void setEstadoDescargaEscri(Integer estadoDescargaEscri) {
		this.estadoDescargaEscri = estadoDescargaEscri;
	}

	public Integer getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Integer empresa) {
		this.empresa = empresa;
	}

	public String getEstadoForm() {
		return estadoForm;
	}

	public void setEstadoForm(String estadoForm) {
		this.estadoForm = estadoForm;
	}

	public String getCanalTexto() {
		return canalTexto;
	}

	public void setCanalTexto(String canalTexto) {
		this.canalTexto = canalTexto;
	}

	public Integer getOrigenCreacion() {
		return origenCreacion;
	}

	public void setOrigenCreacion(Integer origenCreacion) {
		this.origenCreacion = origenCreacion;
	}

	public Long getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(Long idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Long getFechaCreacionL() {
		return fechaCreacionL;
	}

	public void setFechaCreacionL(Long fechaCreacionL) {
		this.fechaCreacionL = fechaCreacionL;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}


//	public ArrayList<BitacoraDTO> getBitacoraDTOs() {
//		return bitacoraDTOs;
//	}
//
//	public void setBitacoraDTOs(ArrayList<BitacoraDTO> bitacoraDTOs) {
//		this.bitacoraDTOs = bitacoraDTOs;
//	}
	
	

}