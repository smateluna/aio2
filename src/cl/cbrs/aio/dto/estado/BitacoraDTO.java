package cl.cbrs.aio.dto.estado;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class BitacoraDTO implements JSONAware{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Integer id;
	private Long fechaL;
	private Date fecha;
	private FuncionarioDTO funcionario;
	
	private Integer idOrigen;
	private String descOrigen;
	
	private Integer idTipo;
	private String descTipo;
	
	private Integer idCausal;
	private String descCausal;
		
	private String comentario;
	
	private Integer categoria;
	
	private UsuarioWebVO usuarioWebVO;
		
	public BitacoraDTO(){
		
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
		
		sb.append("\"funcionario\"");
		sb.append(":");
		
		if(this.funcionario!=null){
			sb.append(this.funcionario.toJSONString());			
		}else{
			sb.append("null");				
		}
		
		sb.append(",");	
		
		sb.append("\"usuarioWebVO\"");
		sb.append(":");
		
		if(this.usuarioWebVO!=null){
			sb.append("{");		
			
			sb.append("\"nombre\"");
			sb.append(":");
			
			if(this.usuarioWebVO.getNombres()!=null){
				sb.append("\"" + JSONObject.escape(this.usuarioWebVO.getNombres().trim()) + "\"");			
			}else{
				sb.append("\"\"");				
			}
			
			sb.append(",");	
			
			sb.append("\"apellidoPaterno\"");
			sb.append(":");
			
			if(this.usuarioWebVO.getApellidoPaterno()!=null){
				sb.append("\"" + JSONObject.escape(this.usuarioWebVO.getApellidoPaterno().trim()) + "\"");			
			}else{
				sb.append("\"\"");				
			}
			
			sb.append(",");	
			
			sb.append("\"apellidoMaterno\"");
			sb.append(":");
			
			if(this.usuarioWebVO.getApellidoMaterno()!=null){
				sb.append("\"" + JSONObject.escape(this.usuarioWebVO.getApellidoMaterno().trim()) + "\"");			
			}else{
				sb.append("\"\"");				
			}

			sb.append("}");
		}else{
			sb.append("null");				
		}
		
		sb.append(",");	
		
		sb.append("\"idOrigen\"");
		sb.append(":");

		sb.append(this.idOrigen);

		sb.append(",");	
		
		sb.append("\"descOrigen\"");
		sb.append(":");
		
		if(this.descOrigen!=null){
			sb.append("\"" + JSONObject.escape(this.descOrigen.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"categoria\"");
		sb.append(":");
		
		if(this.categoria!=null){
			sb.append("\"" + this.categoria + "\"");			
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
		
		sb.append("\"idCausal\"");
		sb.append(":");

		sb.append(this.idCausal);

		sb.append(",");	
		
		sb.append("\"descCausal\"");
		sb.append(":");
		
		if(this.descCausal!=null){
			sb.append("\"" + JSONObject.escape(this.descCausal.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}
		
		sb.append(",");	
		
		sb.append("\"comentario\"");
		sb.append(":");
		
		if(this.comentario!=null){
			sb.append("\"" + JSONObject.escape(this.comentario.trim()) + "\"");			
		}else{
			sb.append("\"\"");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public String getDescOrigen() {
		return descOrigen;
	}

	public void setDescOrigen(String descOrigen) {
		this.descOrigen = descOrigen;
	}



	public String getDescTipo() {
		return descTipo;
	}

	public void setDescTipo(String descTipo) {
		this.descTipo = descTipo;
	}


	public String getDescCausal() {
		return descCausal;
	}

	public void setDescCausal(String descCausal) {
		this.descCausal = descCausal;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public FuncionarioDTO getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(FuncionarioDTO funcionario) {
		this.funcionario = funcionario;
	}

	public Integer getIdOrigen() {
		return idOrigen;
	}

	public void setIdOrigen(Integer idOrigen) {
		this.idOrigen = idOrigen;
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public Integer getIdCausal() {
		return idCausal;
	}

	public void setIdCausal(Integer idCausal) {
		this.idCausal = idCausal;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public UsuarioWebVO getUsuarioWebVO() {
		return usuarioWebVO;
	}

	public void setUsuarioWebVO(UsuarioWebVO usuarioWebVO) {
		this.usuarioWebVO = usuarioWebVO;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	public Long getFechaL() {
		return fechaL;
	}

	public void setFechaL(Long fechaL) {
		this.fechaL = fechaL;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
}