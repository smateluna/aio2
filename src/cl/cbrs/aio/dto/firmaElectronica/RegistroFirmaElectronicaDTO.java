package cl.cbrs.aio.dto.firmaElectronica;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class RegistroFirmaElectronicaDTO implements JSONAware {
	
	private String codArchivoAlpha;
	private Integer enviado;
	private Long fechaFirma;
	private Long fechaPdf;
	private String nombreArchivo;
	private String nombreArchivoVersion;
	private TipoDocumentoDTO tipoDocumentoDTO;
	private String usuario;
	private Integer valorDocumento;
	private Integer vigente;
	private String url;
	private ArrayList<CertificadoFNADTO> certificadoFNADTOs;
	private String glosaCobroCertificado;
	
	public RegistroFirmaElectronicaDTO() {
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	
		
		sb.append("\"codArchivoAlpha\"");
		sb.append(":");

		if(this.codArchivoAlpha!=null){
			sb.append("\"" + JSONObject.escape(this.codArchivoAlpha.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append(",");

		sb.append("\"enviado\"");
		sb.append(":");

		sb.append(this.enviado);
		
		sb.append(",");

		sb.append("\"fechaFirma\"");
		sb.append(":");

		sb.append(this.fechaFirma);
				
		sb.append(",");

		sb.append("\"fechaPdf\"");
		sb.append(":");
		sb.append(this.fechaPdf);
		sb.append(",");
		
		sb.append("\"glosaCobroCertificado\"");
		sb.append(":");		
		if(this.glosaCobroCertificado!=null){
			sb.append("\"" + JSONObject.escape(this.glosaCobroCertificado.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		sb.append(",");			
		
		sb.append("\"nombreArchivo\"");
		sb.append(":");

		if(this.nombreArchivo!=null){
			sb.append("\"" + JSONObject.escape(this.nombreArchivo.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		
		
		sb.append(",");
		
		sb.append("\"nombreArchivoVersion\"");
		sb.append(":");

		if(this.nombreArchivoVersion!=null){
			sb.append("\"" + JSONObject.escape(this.nombreArchivoVersion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		
		sb.append(",");
		
		sb.append("\"tipoDocumentoDTO\"");
		sb.append(":");

		sb.append(this.tipoDocumentoDTO.toJSONString());
		
		sb.append(",");
		
		sb.append("\"usuario\"");
		sb.append(":");

		if(this.usuario!=null){
			sb.append("\"" + JSONObject.escape(this.usuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");

		sb.append("\"valorDocumento\"");
		sb.append(":");
		sb.append(this.valorDocumento);
		sb.append(",");
		
		JSONArray certificadosJSONArray = new JSONArray();		
		if(this.certificadoFNADTOs!=null){
			for(CertificadoFNADTO certificado : this.certificadoFNADTOs){				
				certificadosJSONArray.add(certificado);
			}			
		}
		sb.append("\"certificadoFNADTOs\"");
		sb.append(":");		
		sb.append(certificadosJSONArray);
		sb.append(",");		

		sb.append("\"vigente\"");
		sb.append(":");

		sb.append(this.vigente);
		
		sb.append(",");
		
		sb.append("\"url\"");
		sb.append(":");

		if(this.url!=null){
			sb.append("\"" + JSONObject.escape(this.url.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public String getCodArchivoAlpha() {
		return codArchivoAlpha;
	}

	public void setCodArchivoAlpha(String codArchivoAlpha) {
		this.codArchivoAlpha = codArchivoAlpha;
	}

	public Integer getEnviado() {
		return enviado;
	}

	public void setEnviado(Integer enviado) {
		this.enviado = enviado;
	}

	public Long getFechaFirma() {
		return fechaFirma;
	}

	public void setFechaFirma(Long fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getNombreArchivoVersion() {
		return nombreArchivoVersion;
	}

	public void setNombreArchivoVersion(String nombreArchivoVersion) {
		this.nombreArchivoVersion = nombreArchivoVersion;
	}

	public TipoDocumentoDTO getTipoDocumentoDTO() {
		return tipoDocumentoDTO;
	}

	public void setTipoDocumentoDTO(TipoDocumentoDTO tipoDocumentoDTO) {
		this.tipoDocumentoDTO = tipoDocumentoDTO;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Integer getValorDocumento() {
		return valorDocumento;
	}

	public void setValorDocumento(Integer valorDocumento) {
		this.valorDocumento = valorDocumento;
	}

	public Integer getVigente() {
		return vigente;
	}

	public void setVigente(Integer vigente) {
		this.vigente = vigente;
	}

	public Long getFechaPdf() {
		return fechaPdf;
	}

	public void setFechaPdf(Long fechaPdf) {
		this.fechaPdf = fechaPdf;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<CertificadoFNADTO> getCertificadoFNADTOs() {
		return certificadoFNADTOs;
	}

	public void setCertificadoFNADTOs(ArrayList<CertificadoFNADTO> certificadoFNADTOs) {
		this.certificadoFNADTOs = certificadoFNADTOs;
	}

	public String getGlosaCobroCertificado() {
		return glosaCobroCertificado;
	}

	public void setGlosaCobroCertificado(String glosaCobroCertificado) {
		this.glosaCobroCertificado = glosaCobroCertificado;
	}

}