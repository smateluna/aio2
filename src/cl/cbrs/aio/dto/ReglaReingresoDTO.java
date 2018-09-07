package cl.cbrs.aio.dto;

import java.io.Serializable;

public class ReglaReingresoDTO implements Serializable{

	private static final long serialVersionUID = 4879076564802898542L;
	
	private String rutFuncionario;
	private Integer idTipoFormulario;
	private String codSeccion;
	private Integer idEstado;
	private Integer idUsuarioComercio;
	private String registro;
	
	public ReglaReingresoDTO(){
	}
	
	public ReglaReingresoDTO(int idTipoFormulario){
		this.idTipoFormulario = idTipoFormulario;
	}
	
	public String getRutFuncionario() {
		return rutFuncionario;
	}
	public void setRutFuncionario(String rutFuncionario) {
		this.rutFuncionario = rutFuncionario;
	}
	public int getIdTipoFormulario() {
		return idTipoFormulario;
	}
	public void setIdTipoFormulario(Integer idTipoFormulario) {
		this.idTipoFormulario = idTipoFormulario;
	}
	public String getCodSeccion() {
		return codSeccion;
	}
	public void setCodSeccion(String codSeccion) {
		this.codSeccion = codSeccion;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getRegistro() {
		return registro;
	}

	public int getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public int getIdUsuarioComercio() {
		return idUsuarioComercio;
	}

	public void setIdUsuarioComercio(Integer idUsuarioComercio) {
		this.idUsuarioComercio = idUsuarioComercio;
	}
	
	
}
