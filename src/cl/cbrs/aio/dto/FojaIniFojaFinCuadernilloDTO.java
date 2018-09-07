package cl.cbrs.aio.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.estado.RegistroDTO;

public class FojaIniFojaFinCuadernilloDTO implements Serializable,JSONAware{

	private static final long serialVersionUID = -8187142252966662424L;
	
	private Long fojaIni;
	private Long fojaFin;
	
	public FojaIniFojaFinCuadernilloDTO(){
		
	}
	
	public Long getFojaIni() {
		return fojaIni;
	}



	public void setFojaIni(Long fojaIni) {
		this.fojaIni = fojaIni;
	}



	public Long getFojaFin() {
		return fojaFin;
	}



	public void setFojaFin(Long fojaFin) {
		this.fojaFin = fojaFin;
	}

	@SuppressWarnings("unchecked")
	public String toJSONString(){

		StringBuffer sb = new StringBuffer();
		
		sb.append("{");	

		sb.append("\"fojaIni\"");
		sb.append(":");

		sb.append(this.fojaIni);
		
		sb.append(",");
		
		sb.append("\"fojaFin\"");
		sb.append(":");

		sb.append(this.fojaFin);

		sb.append("}");

		return sb.toString();		
	}
}