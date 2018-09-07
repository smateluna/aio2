package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class SolicitudDTO implements JSONAware, Serializable{

	private static final long serialVersionUID = 5611002389174682354L;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Long idSolicitud;
	private Date fechaSolicitud;
	private OrigenDTO origenDTO;
	private String usuario;
	private Long rut;
	private String dv;
	private EstadoDTO estadoDTO;
	private Date fechaEstado;
	private BodegaDTO bodegaDTO;
	private Integer impreso;
	private Long foja;
	private Long numero;
	private Long ano;
	private Integer bis;
	private String nombre;
	private Long tomo;
	private ArrayList<TrazaSolicitudDTO> listaTrazaSolicitudDTO;
	private ObservacionSolicitudDTO observacionSolicitudDTO;

	public SolicitudDTO(){

	}

	@SuppressWarnings("unchecked")
	public String toJSONString(){
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		StringBuffer sb = new StringBuffer();

//		String estadoDate = "";
//
//		if(this.fechaEstado!=null){
//			estadoDate = sdf.format(this.fechaEstado);
//		}
//
//		String solicitudDate = "";
//
//		if(this.fechaSolicitud!=null){
//			solicitudDate = sdf.format(this.fechaSolicitud);
//		}

		sb.append("{");	

		sb.append("\"idSolicitud\"");
		sb.append(":");

		sb.append(this.idSolicitud);

		sb.append(",");
		
		sb.append("\"fechaSolicitud\"");
		sb.append(":");
		if(this.fechaSolicitud!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaSolicitud)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");		


		sb.append("\"origenDTO\"");
		sb.append(":");

		if(this.origenDTO!=null){
			sb.append(this.origenDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");

		sb.append("\"usuario\"");
		sb.append(":");

		if(this.usuario!=null){
			sb.append("\"" + JSONObject.escape(this.usuario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}		

		sb.append(",");	

		sb.append("\"rut\"");
		sb.append(":");

		sb.append(this.rut);

		sb.append(",");

		sb.append("\"dv\"");
		sb.append(":");

		if(this.dv!=null){
			sb.append("\"" + JSONObject.escape(this.dv.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}	

		sb.append(",");


		sb.append("\"estadoDTO\"");
		sb.append(":");

		if(this.estadoDTO!=null){
			sb.append(this.estadoDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");
		
		sb.append("\"fechaEstado\"");
		sb.append(":");
		if(this.fechaEstado!=null)
			sb.append("\"" + JSONObject.escape(sdf.format(this.fechaEstado)) + "\"");
		else
			sb.append("\"\"");
		sb.append(",");


		sb.append("\"bodegaDTO\"");
		sb.append(":");

		if(this.bodegaDTO!=null){
			sb.append(this.bodegaDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append(",");	

		sb.append("\"impreso\"");
		sb.append(":");

		sb.append(this.impreso);

		sb.append(",");

		sb.append("\"foja\"");
		sb.append(":");

		sb.append(this.foja);

		sb.append(",");

		sb.append("\"numero\"");
		sb.append(":");

		sb.append(this.numero);

		sb.append(",");

		sb.append("\"ano\"");
		sb.append(":");

		sb.append(this.ano);

		sb.append(",");

		sb.append("\"bis\"");
		sb.append(":");

		sb.append(this.bis);

		sb.append(",");		

		sb.append("\"nombre\"");
		sb.append(":");

		if(this.nombre!=null){
			sb.append("\"" + JSONObject.escape(this.nombre.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append(",");

		sb.append("\"tomo\"");
		sb.append(":");

		sb.append(this.tomo);

		sb.append(",");	

		JSONArray listaTrazaSolicitudJSONArray = new JSONArray();

		if(this.listaTrazaSolicitudDTO!=null){
			for(TrazaSolicitudDTO trazaSolicitudDTO : this.listaTrazaSolicitudDTO){				
				listaTrazaSolicitudJSONArray.add(trazaSolicitudDTO);
			}			
		}

		sb.append("\"listaTrazaSolicitudDTO\"");
		sb.append(":");

		sb.append(listaTrazaSolicitudJSONArray);
		
		sb.append(",");

		sb.append("\"observacionSolicitudDTO\"");
		sb.append(":");

		if(this.observacionSolicitudDTO!=null){
			sb.append(this.observacionSolicitudDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public OrigenDTO getOrigenDTO() {
		return origenDTO;
	}

	public void setOrigenDTO(OrigenDTO origenDTO) {
		this.origenDTO = origenDTO;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Long getRut() {
		return rut;
	}

	public void setRut(Long rut) {
		this.rut = rut;
	}

	public String getDv() {
		return dv;
	}

	public void setDv(String dv) {
		this.dv = dv;
	}

	public EstadoDTO getEstadoDTO() {
		return estadoDTO;
	}

	public void setEstadoDTO(EstadoDTO estadoDTO) {
		this.estadoDTO = estadoDTO;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public BodegaDTO getBodegaDTO() {
		return bodegaDTO;
	}

	public void setBodegaDTO(BodegaDTO bodegaDTO) {
		this.bodegaDTO = bodegaDTO;
	}

	public Integer getImpreso() {
		return impreso;
	}

	public void setImpreso(Integer impreso) {
		this.impreso = impreso;
	}

	public Long getFoja() {
		return foja;
	}

	public void setFoja(Long foja) {
		this.foja = foja;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Long getAno() {
		return ano;
	}

	public void setAno(Long ano) {
		this.ano = ano;
	}

	public Integer getBis() {
		return bis;
	}

	public void setBis(Integer bis) {
		this.bis = bis;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getTomo() {
		return tomo;
	}

	public void setTomo(Long tomo) {
		this.tomo = tomo;
	}

	public ArrayList<TrazaSolicitudDTO> getListaTrazaSolicitudDTO() {
		return listaTrazaSolicitudDTO;
	}

	public void setListaTrazaSolicitudDTO(
			ArrayList<TrazaSolicitudDTO> listaTrazaSolicitudDTO) {
		this.listaTrazaSolicitudDTO = listaTrazaSolicitudDTO;
	}

	public ObservacionSolicitudDTO getObservacionSolicitudDTO() {
		return observacionSolicitudDTO;
	}

	public void setObservacionSolicitudDTO(
			ObservacionSolicitudDTO observacionSolicitudDTO) {
		this.observacionSolicitudDTO = observacionSolicitudDTO;
	}

}