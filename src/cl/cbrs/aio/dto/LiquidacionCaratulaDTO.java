package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.dto.firmaElectronica.RegistroFirmaElectronicaDTO;

public class LiquidacionCaratulaDTO implements JSONAware, Serializable {
	
	private static final long serialVersionUID = 4758298456601324860L;
	
	private CaratulaDTO caratula;
	
	//diferencia
	private Long diferencia;
	private Boolean hayDiferencia;
	private Boolean hayDiferenciaFavor;
	private Boolean hayDiferenciaContra;
		
	//tx diferencia
	private Boolean hayTxDiferencia;
	private TransaccionWebDTO txDiferenciaDTO;
	
	//papeles
	private ArrayList<String> advertencias;
	private ArrayList<RegistroFirmaElectronicaDTO> papeles;
	
	private Boolean hayBoleta;
	private Integer numeroBoleta;
	
	private CuentaCorrienteDTO cuentaCorrienteDTO;
	private CierreCtasCtesFinalDTO cierreCtasCtesFinalDTO;		
	
	public LiquidacionCaratulaDTO() {

	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"caratulaDTO\"");
		sb.append(":");
		if(this.caratula!=null)
			sb.append(this.caratula.toJSONString());	
		else
			sb.append("\"\"");
		sb.append(",");
		
		sb.append("\"cuentaCorrienteDTO\"");
		sb.append(":");
		if(this.cuentaCorrienteDTO!=null)
			sb.append(this.cuentaCorrienteDTO.toJSONString());	
		else
			sb.append("\"\"");
		sb.append(",");		
		
		sb.append("\"cierreCtasCtesFinalDTO\"");
		sb.append(":");
		if(this.cierreCtasCtesFinalDTO!=null)
			sb.append(this.cierreCtasCtesFinalDTO.toJSONString());	
		else
			sb.append("\"\"");
		sb.append(",");				
		
		sb.append("\"diferencia\"");
		sb.append(":");
		sb.append(this.diferencia);		
		sb.append(",");	
		
		sb.append("\"hayDiferencia\"");
		sb.append(":");
		sb.append(this.hayDiferencia);		
		sb.append(",");
		
		sb.append("\"hayDiferenciaFavor\"");
		sb.append(":");
		sb.append(this.hayDiferenciaFavor);		
		sb.append(",");	
		
		sb.append("\"hayDiferenciaContra\"");
		sb.append(":");
		sb.append(this.hayDiferenciaContra);		
		sb.append(",");	
		
		sb.append("\"hayTxDiferencia\"");
		sb.append(":");
		sb.append(this.hayTxDiferencia);		
		sb.append(",");
		
		sb.append("\"txDiferenciaDTO\"");
		sb.append(":");
		if(this.txDiferenciaDTO!=null)
			sb.append(this.txDiferenciaDTO.toJSONString());	
		else
			sb.append("\"\"");
		sb.append(",");		
		
		JSONArray advertenciasJSONArray = new JSONArray();		
		if(this.advertencias!=null){
			for(String advertencia : this.advertencias){				
				advertenciasJSONArray.add(advertencia);
			}			
		}
		sb.append("\"advertencias\"");
		sb.append(":");		
		sb.append(advertenciasJSONArray);
		sb.append(",");		
		
		JSONArray papelesJSONArray = new JSONArray();		
		if(this.papeles!=null){
			for(RegistroFirmaElectronicaDTO papel : this.papeles){				
				papelesJSONArray.add(papel);
			}			
		}
		sb.append("\"papeles\"");
		sb.append(":");		
		sb.append(papelesJSONArray);
		sb.append(",");			
		
		sb.append("\"hayBoleta\"");
		sb.append(":");
		sb.append(this.hayBoleta);		
		sb.append(",");	
		
		sb.append("\"numeroBoleta\"");
		sb.append(":");
		sb.append(this.numeroBoleta);		

		sb.append("}");

		return sb.toString();		
	}

	public CaratulaDTO getCaratula() {
		return caratula;
	}

	public void setCaratula(CaratulaDTO caratula) {
		this.caratula = caratula;
	}

	public Long getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(Long diferencia) {
		this.diferencia = diferencia;
	}

	public Boolean getHayDiferencia() {
		return hayDiferencia;
	}

	public void setHayDiferencia(Boolean hayDiferencia) {
		this.hayDiferencia = hayDiferencia;
	}

	public Boolean getHayDiferenciaFavor() {
		return hayDiferenciaFavor;
	}

	public void setHayDiferenciaFavor(Boolean hayDiferenciaFavor) {
		this.hayDiferenciaFavor = hayDiferenciaFavor;
	}

	public Boolean getHayDiferenciaContra() {
		return hayDiferenciaContra;
	}

	public void setHayDiferenciaContra(Boolean hayDiferenciaContra) {
		this.hayDiferenciaContra = hayDiferenciaContra;
	}

	public Boolean getHayTxDiferencia() {
		return hayTxDiferencia;
	}

	public void setHayTxDiferencia(Boolean hayTxDiferencia) {
		this.hayTxDiferencia = hayTxDiferencia;
	}

	public ArrayList<String> getAdvertencias() {
		return advertencias;
	}

	public void setAdvertencias(ArrayList<String> advertencias) {
		this.advertencias = advertencias;
	}

	public Boolean getHayBoleta() {
		return hayBoleta;
	}

	public void setHayBoleta(Boolean hayBoleta) {
		this.hayBoleta = hayBoleta;
	}

	public Integer getNumeroBoleta() {
		return numeroBoleta;
	}

	public void setNumeroBoleta(Integer numeroBoleta) {
		this.numeroBoleta = numeroBoleta;
	}

	public TransaccionWebDTO getTxDiferenciaDTO() {
		return txDiferenciaDTO;
	}

	public void setTxDiferenciaDTO(TransaccionWebDTO txDiferenciaDTO) {
		this.txDiferenciaDTO = txDiferenciaDTO;
	}

	public ArrayList<RegistroFirmaElectronicaDTO> getPapeles() {
		return papeles;
	}

	public void setPapeles(ArrayList<RegistroFirmaElectronicaDTO> papeles) {
		this.papeles = papeles;
	}

	public CuentaCorrienteDTO getCuentaCorrienteDTO() {
		return cuentaCorrienteDTO;
	}

	public void setCuentaCorrienteDTO(CuentaCorrienteDTO cuentaCorrienteDTO) {
		this.cuentaCorrienteDTO = cuentaCorrienteDTO;
	}

	public CierreCtasCtesFinalDTO getCierreCtasCtesFinalDTO() {
		return cierreCtasCtesFinalDTO;
	}

	public void setCierreCtasCtesFinalDTO(CierreCtasCtesFinalDTO cierreCtasCtesFinalDTO) {
		this.cierreCtasCtesFinalDTO = cierreCtasCtesFinalDTO;
	}

	
}