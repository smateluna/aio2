package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class AnotacionDTO implements JSONAware, Serializable{
	
	private static final long serialVersionUID = 3763033951250900686L;
	
	private String acto;
	private Integer borrador;
	private String direccion;
	private Integer repertorio;
	private Integer folio;
	private Long caratula;
	private String codigoFirma;
	private EstadoAnotacionDTO estadoAnotacionDTO;
	private Date fechaCreacion;
	private Date fechaFirma;
	private Date fechaAprobacion;
	private Long idAnotacion;
	private Long idUsuarioCreador;
	private Long idUsuarioFirmador;
	private InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO;
	private InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO;
	private String texto;
	private TipoAnotacionDTO tipoAnotacionDTO;
	private String caratulaMatriz;
	
	
	private String nombreUsuarioFirmador;
	private String nombreUsuarioCreador;
	private String nombreUsuarioAprobador;
	
	private String nombreUsuarioEliminador;
	private Date fechaEliminacion;

	public AnotacionDTO(){

	}

	public Long getCaratula() {
		return caratula;
	}

	public void setCaratula(Long caratula) {
		this.caratula = caratula;
	}

	public String getCodigoFirma() {
		return codigoFirma;
	}

	public void setCodigoFirma(String codigoFirma) {
		this.codigoFirma = codigoFirma;
	}

	public EstadoAnotacionDTO getEstadoAnotacionDTO() {
		return estadoAnotacionDTO;
	}

	public void setEstadoAnotacionDTO(EstadoAnotacionDTO estadoAnotacionDTO) {
		this.estadoAnotacionDTO = estadoAnotacionDTO;
	}

	public Long getIdAnotacion() {
		return idAnotacion;
	}

	public void setIdAnotacion(Long idAnotacion) {
		this.idAnotacion = idAnotacion;
	}

	public Long getIdUsuarioCreador() {
		return idUsuarioCreador;
	}

	public void setIdUsuarioCreador(Long idUsuarioCreador) {
		this.idUsuarioCreador = idUsuarioCreador;
	}

	public Long getIdUsuarioFirmador() {
		return idUsuarioFirmador;
	}

	public void setIdUsuarioFirmador(Long idUsuarioFirmador) {
		this.idUsuarioFirmador = idUsuarioFirmador;
	}

	public InscripcionDigitalDTO getInscripcionDigitalByIdInscripcionDestinoDTO() {
		return inscripcionDigitalByIdInscripcionDestinoDTO;
	}

	public void setInscripcionDigitalByIdInscripcionDestinoDTO(
			InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO) {
		this.inscripcionDigitalByIdInscripcionDestinoDTO = inscripcionDigitalByIdInscripcionDestinoDTO;
	}

	public InscripcionDigitalDTO getInscripcionDigitalByIdInscripcionOrigenDTO() {
		return inscripcionDigitalByIdInscripcionOrigenDTO;
	}

	public void setInscripcionDigitalByIdInscripcionOrigenDTO(
			InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO) {
		this.inscripcionDigitalByIdInscripcionOrigenDTO = inscripcionDigitalByIdInscripcionOrigenDTO;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public String getCaratulaMatriz() {
		return caratulaMatriz;
	}

	public void setCaratulaMatriz(String caratulaMatriz) {
		this.caratulaMatriz = caratulaMatriz;
	}

	public TipoAnotacionDTO getTipoAnotacionDTO() {
		return tipoAnotacionDTO;
	}

	public void setTipoAnotacionDTO(TipoAnotacionDTO tipoAnotacionDTO) {
		this.tipoAnotacionDTO = tipoAnotacionDTO;
	}

	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}

	public String getNombreUsuarioAprobador() {
		return nombreUsuarioAprobador;
	}

	public void setNombreUsuarioAprobador(String nombreUsuarioAprobador) {
		this.nombreUsuarioAprobador = nombreUsuarioAprobador;
	}

	public String toJSONString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

		String createDate = "";
		String signDate = "";
		String deleteDate = "";
		String aprobacionDate = "";

		
		if(this.fechaCreacion!=null){
			//Date fechaCreacionDate = fechaCreacion.getTime();

			createDate = sdf.format(this.fechaCreacion);
		}

		if(this.fechaFirma!=null){
			//Date fechaFirmaDate = fechaFirma.getTime();

			signDate = sdf.format(this.fechaFirma);
		}
		
		if(this.fechaEliminacion!=null){
			//Date fechaFirmaDate = fechaFirma.getTime();

			deleteDate = sdf.format(this.fechaEliminacion);
		}
		
		if(this.fechaAprobacion!=null){
			//Date fechaFirmaDate = fechaFirma.getTime();

			aprobacionDate = sdf.format(this.fechaAprobacion);
		}

		sb.append("{");	
	
		sb.append("\"idAnotacion\"");
		sb.append(":");

		sb.append(this.idAnotacion);

		sb.append(",");	
		
		sb.append("\"tipoAnotacionDTO\"");
		sb.append(":");
		
		if(this.tipoAnotacionDTO!=null){
			sb.append(this.tipoAnotacionDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"acto\"");
		sb.append(":");

		if(this.acto!=null){
			sb.append("\"" + JSONObject.escape(this.acto.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"direccion\"");
		sb.append(":");

		if(this.direccion!=null){
			sb.append("\"" + JSONObject.escape(this.direccion.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	
		sb.append(",");	
		
		sb.append("\"borrador\"");
		sb.append(":");

		sb.append(this.borrador);		

		sb.append(",");
		
		sb.append("\"folio\"");
		sb.append(":");

		sb.append(this.folio);		

		sb.append(",");
		
		sb.append("\"repertorio\"");
		sb.append(":");

		sb.append(this.repertorio);		

		sb.append(",");

		sb.append("\"caratula\"");
		sb.append(":");

		sb.append(this.caratula);
		
		sb.append(",");

		sb.append("\"caratulaMatriz\"");
		sb.append(":");

		if(this.caratulaMatriz!=null){
			sb.append("\"" + JSONObject.escape(this.caratulaMatriz.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append(",");	

		sb.append("\"estadoAnotacionDTO\"");
		sb.append(":");
		
		if(this.estadoAnotacionDTO!=null){
			sb.append(this.estadoAnotacionDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");	
		
		sb.append("\"fechaCreacion\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(createDate) + "\"");	

		sb.append(",");
		
		sb.append("\"fechaFirma\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(signDate) + "\"");
		
		sb.append(",");
		
		sb.append("\"fechaAprobacion\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(aprobacionDate) + "\"");

		sb.append(",");
		
		sb.append("\"idUsuarioFirmador\"");
		sb.append(":");

		sb.append(this.idUsuarioFirmador);	
		
		sb.append(",");
		
		sb.append("\"codigoFirma\"");
		sb.append(":");

		if(this.codigoFirma!=null){
			sb.append("\"" + JSONObject.escape(this.codigoFirma.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append(",");	

		sb.append("\"idUsuarioCreador\"");
		sb.append(":");

		sb.append(this.idUsuarioCreador);		

		sb.append(",");		

		sb.append("\"texto\"");
		sb.append(":");

		if(this.texto!=null){
			sb.append("\"" + JSONObject.escape(this.texto.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"nombreUsuarioCreador\"");
		sb.append(":");

		if(this.nombreUsuarioCreador!=null){
			sb.append("\"" + JSONObject.escape(this.nombreUsuarioCreador.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"nombreUsuarioFirmador\"");
		sb.append(":");

		if(this.nombreUsuarioFirmador!=null){
			sb.append("\"" + JSONObject.escape(this.nombreUsuarioFirmador.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");
		
		sb.append("\"nombreUsuarioAprobador\"");
		sb.append(":");

		if(this.nombreUsuarioAprobador!=null){
			sb.append("\"" + JSONObject.escape(this.nombreUsuarioAprobador.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"inscripcionDigitalByIdInscripcionDestinoDTO\"");
		sb.append(":");
		
		if(this.inscripcionDigitalByIdInscripcionDestinoDTO!=null){
			sb.append(this.inscripcionDigitalByIdInscripcionDestinoDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");	
		
		sb.append("\"inscripcionDigitalByIdInscripcionOrigenDTO\"");
		sb.append(":");
		
		if(this.inscripcionDigitalByIdInscripcionOrigenDTO!=null){
			sb.append(this.inscripcionDigitalByIdInscripcionOrigenDTO.toJSONString());
		}else{
			sb.append("null");				
		}
		
		sb.append(",");
		
		sb.append("\"fechaEliminacion\"");
		sb.append(":");
		sb.append("\"" + JSONObject.escape(deleteDate) + "\"");	
		
		sb.append(",");
		
		sb.append("\"nombreUsuarioEliminador\"");
		sb.append(":");

		if(this.nombreUsuarioEliminador!=null){
			sb.append("\"" + JSONObject.escape(this.nombreUsuarioEliminador.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaFirma() {
		return fechaFirma;
	}

	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public String getNombreUsuarioFirmador() {
		return nombreUsuarioFirmador;
	}

	public void setNombreUsuarioFirmador(String nombreUsuarioFirmador) {
		this.nombreUsuarioFirmador = nombreUsuarioFirmador;
	}

	public String getNombreUsuarioCreador() {
		return nombreUsuarioCreador;
	}

	public void setNombreUsuarioCreador(String nombreUsuarioCreador) {
		this.nombreUsuarioCreador = nombreUsuarioCreador;
	}

	public String getActo() {
		return acto;
	}

	public void setActo(String acto) {
		this.acto = acto;
	}

	public Integer getBorrador() {
		return borrador;
	}

	public void setBorrador(Integer borrador) {
		this.borrador = borrador;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Integer getRepertorio() {
		return repertorio;
	}

	public void setRepertorio(Integer repertorio) {
		this.repertorio = repertorio;
	}

	public String getNombreUsuarioEliminador() {
		return nombreUsuarioEliminador;
	}

	public void setNombreUsuarioEliminador(String nombreUsuarioEliminador) {
		this.nombreUsuarioEliminador = nombreUsuarioEliminador;
	}

	public Date getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(Date fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}

	public Integer getFolio() {
		return folio;
	}

	public void setFolio(Integer folio) {
		this.folio = folio;
	}
}