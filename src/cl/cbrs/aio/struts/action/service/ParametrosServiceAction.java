package cl.cbrs.aio.struts.action.service;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dto.estado.NaturalezaDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.ParametrosUtil;

public class ParametrosServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(ParametrosServiceAction.class);
	
	private static ArrayList<NaturalezaDTO> CACHE_NATURALEZAS = null;

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}	 	
	
	@SuppressWarnings({ "unchecked" })
	public void getNaturalezas(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
			
		JSONObject json = new JSONObject();
		json.put("estado", true);	

		ParametrosUtil parametrosUtil = new ParametrosUtil();

		try {					
			if(CACHE_NATURALEZAS==null){
				CACHE_NATURALEZAS = parametrosUtil.getNaturalezasDTO();	
			}          
            
            json.put("listaNaturalezas", CACHE_NATURALEZAS);
			
			
			
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor");
			json.put("estado", false);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}