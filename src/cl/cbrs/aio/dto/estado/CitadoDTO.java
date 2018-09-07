package cl.cbrs.aio.dto.estado;

import org.json.simple.JSONAware;

public class CitadoDTO implements JSONAware{
	private RegistroDTO registroDTO;
		
	private Integer foja;
	private Integer numero;
	private Integer ano;
	private Integer bis;
		
	public CitadoDTO(){
		
	}
	
	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");		

		sb.append("\"registroDTO\"");
		sb.append(":");

		if(this.registroDTO!=null){
			sb.append(this.registroDTO.toJSONString());
		}else{
			sb.append("null");				
		}

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

		sb.append("}");

		return sb.toString();		
	}

	public RegistroDTO getRegistroDTO() {
		return registroDTO;
	}

	public void setRegistroDTO(RegistroDTO registroDTO) {
		this.registroDTO = registroDTO;
	}

	public Integer getFoja() {
		return foja;
	}

	public void setFoja(Integer foja) {
		this.foja = foja;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getBis() {
		return bis;
	}

	public void setBis(Integer bis) {
		this.bis = bis;
	}

}