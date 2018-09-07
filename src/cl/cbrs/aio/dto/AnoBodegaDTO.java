package cl.cbrs.aio.dto;

import java.io.Serializable;

import org.json.simple.JSONAware;

public class AnoBodegaDTO implements JSONAware, Serializable{

	private static final long serialVersionUID = -5104951763623758324L;
	
	private Long id;
	private Integer ano;
	private BodegaDTO bodegaDTO;
	
	public AnoBodegaDTO(){

	}

	public String toJSONString(){
		StringBuffer sb = new StringBuffer();

		sb.append("{");	
	
		sb.append("\"id\"");
		sb.append(":");

		sb.append(this.id);

		sb.append(",");	
		
		sb.append("\"ano\"");
		sb.append(":");

		sb.append(this.ano);

		sb.append(",");			
		
		sb.append("\"bodegaDTO\"");
		sb.append(":");
		
		if(this.bodegaDTO!=null){
			sb.append(this.bodegaDTO.toJSONString());
		}else{
			sb.append("null");				
		}

		sb.append("}");

		return sb.toString();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public BodegaDTO getBodegaDTO() {
		return bodegaDTO;
	}

	public void setBodegaDTO(BodegaDTO bodegaDTO) {
		this.bodegaDTO = bodegaDTO;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}
}