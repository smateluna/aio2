package cl.cbrs.aio.dto.estado;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

public class CaratulaEstadoDTO implements JSONAware{
	
	private DatosFormularioDTO datosFormularioDTO;
	private ProductoWebDTO productoWebDTO;
	private ArrayList<CitadoDTO> citadoDTOs;
	private EstadoActualDTO estadoActualDTO;
	private ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs;
	private ArrayList<MovimientoDTO> movimientoDTOs;
	private RequirenteDTO requirenteDTO;
	private CuentaCorrienteDTO cuentaCorrienteDTO;
	private ArrayList<BitacoraDTO> bitacoraDTOs;
	private ArrayList<TareaDTO> tareaDTOs;
	private ArrayList<RepertorioDTO> repertorioDTOs;
	
	public CaratulaEstadoDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	

		sb.append("\"datosFormularioDTO\"");
		sb.append(":");

		if(this.datosFormularioDTO!=null){
			sb.append(this.datosFormularioDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"productoWebDTO\"");
		sb.append(":");

		if(this.productoWebDTO!=null){
			sb.append(this.productoWebDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		JSONArray citadoDTOsJSONArray = new JSONArray();
		if(this.citadoDTOs!=null){
			for(CitadoDTO citadoDTO : this.citadoDTOs){				
				citadoDTOsJSONArray.add(citadoDTO);
			}
		}
		sb.append("\"citadoDTOs\"");
		sb.append(":");
		sb.append(citadoDTOsJSONArray);		
		sb.append(",");
		
		sb.append("\"estadoActualDTO\"");
		sb.append(":");

		if(this.estadoActualDTO!=null){
			sb.append(this.estadoActualDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		JSONArray ingresoEgresoDTOsJSONArray = new JSONArray();

		if(this.ingresoEgresoDTOs!=null){
			for(IngresoEgresoDTO ie : this.ingresoEgresoDTOs){				
				ingresoEgresoDTOsJSONArray.add(ie);
			}			
		}

		sb.append("\"ingresoEgresoDTOs\"");
		sb.append(":");

		sb.append(ingresoEgresoDTOsJSONArray);
		
		sb.append(",");
		
		JSONArray movimientoDTOsJSONArray = new JSONArray();

		if(this.movimientoDTOs!=null){
			for(MovimientoDTO mov : this.movimientoDTOs){				
				movimientoDTOsJSONArray.add(mov);
			}			
		}

		sb.append("\"movimientoDTOs\"");
		sb.append(":");

		sb.append(movimientoDTOsJSONArray);
		
		sb.append(",");
		
		JSONArray repertorioDTOsJSONArray = new JSONArray();
		if(this.repertorioDTOs!=null){
			for(RepertorioDTO rep : this.repertorioDTOs)				
				repertorioDTOsJSONArray.add(rep);		
		}
		sb.append("\"repertorioDTOs\"");
		sb.append(":");
		sb.append(repertorioDTOsJSONArray);		
		sb.append(",");		
		
		sb.append("\"requirenteDTO\"");
		sb.append(":");

		if(this.requirenteDTO != null){
			sb.append(this.requirenteDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");	
		
		sb.append("\"cuentaCorrienteDTO\"");
		sb.append(":");

		if(this.cuentaCorrienteDTO != null){
			sb.append(this.cuentaCorrienteDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		JSONArray bitacoraDTOsJSONArray = new JSONArray();

		if(this.bitacoraDTOs!=null){
			for(BitacoraDTO bit : this.bitacoraDTOs){				
				bitacoraDTOsJSONArray.add(bit);
			}			
		}

		sb.append("\"bitacoraDTOs\"");
		sb.append(":");
		sb.append(bitacoraDTOsJSONArray);		
		sb.append(",");
		
		JSONArray tareaDTOsJSONArray = new JSONArray();

		if(this.tareaDTOs!=null){
			for(TareaDTO task : this.tareaDTOs){				
				tareaDTOsJSONArray.add(task);
			}			
		}

		sb.append("\"tareaDTOs\"");
		sb.append(":");

		sb.append(tareaDTOsJSONArray);

		sb.append("}");

		return sb.toString();		
	}

	public DatosFormularioDTO getDatosFormularioDTO() {
		return datosFormularioDTO;
	}

	public void setDatosFormularioDTO(DatosFormularioDTO datosFormularioDTO) {
		this.datosFormularioDTO = datosFormularioDTO;
	}

	public EstadoActualDTO getEstadoActualDTO() {
		return estadoActualDTO;
	}

	public void setEstadoActualDTO(EstadoActualDTO estadoActualDTO) {
		this.estadoActualDTO = estadoActualDTO;
	}

	public ArrayList<IngresoEgresoDTO> getIngresoEgresoDTOs() {
		return ingresoEgresoDTOs;
	}

	public void setIngresoEgresoDTOs(ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs) {
		this.ingresoEgresoDTOs = ingresoEgresoDTOs;
	}

	public ArrayList<MovimientoDTO> getMovimientoDTOs() {
		return movimientoDTOs;
	}

	public void setMovimientoDTOs(ArrayList<MovimientoDTO> movimientoDTOs) {
		this.movimientoDTOs = movimientoDTOs;
	}

	public RequirenteDTO getRequirenteDTO() {
		return requirenteDTO;
	}

	public void setRequirenteDTO(RequirenteDTO requirenteDTO) {
		this.requirenteDTO = requirenteDTO;
	}

	public CuentaCorrienteDTO getCuentaCorrienteDTO() {
		return cuentaCorrienteDTO;
	}

	public void setCuentaCorrienteDTO(CuentaCorrienteDTO cuentaCorrienteDTO) {
		this.cuentaCorrienteDTO = cuentaCorrienteDTO;
	}

	public ArrayList<BitacoraDTO> getBitacoraDTOs() {
		return bitacoraDTOs;
	}

	public void setBitacoraDTOs(ArrayList<BitacoraDTO> bitacoraDTOs) {
		this.bitacoraDTOs = bitacoraDTOs;
	}

	public ArrayList<TareaDTO> getTareaDTOs() {
		return tareaDTOs;
	}

	public void setTareaDTOs(ArrayList<TareaDTO> tareaDTOs) {
		this.tareaDTOs = tareaDTOs;
	}

	public ProductoWebDTO getProductoWebDTO() {
		return productoWebDTO;
	}

	public void setProductoWebDTO(ProductoWebDTO productoWebDTO) {
		this.productoWebDTO = productoWebDTO;
	}

	public void setRepertorioDTOs(ArrayList<RepertorioDTO> repertorioDTOs) {
		this.repertorioDTOs = repertorioDTOs;
	}

	public ArrayList<RepertorioDTO> getRepertorioDTOs() {
		return repertorioDTOs;
	}

	public ArrayList<CitadoDTO> getCitadoDTOs() {
		return citadoDTOs;
	}

	public void setCitadoDTOs(ArrayList<CitadoDTO> citadoDTOs) {
		this.citadoDTOs = citadoDTOs;
	}
}