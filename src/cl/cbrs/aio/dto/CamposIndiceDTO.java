package cl.cbrs.aio.dto;

import java.util.ArrayList;


public class CamposIndiceDTO {
	
	private String nombreCampo;
	private ArrayList<SearchTermDTO> searchTermDTOs;
	
	public String getNombreCampo() {
		return nombreCampo;
	}
	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}
	public ArrayList<SearchTermDTO> getSearchTermDTOs() {
		return searchTermDTOs;
	}
	public void setSearchTermDTOs(ArrayList<SearchTermDTO> searchTermDTOs) {
		this.searchTermDTOs = searchTermDTOs;
	}

}
