package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbrs.aio.dao.UafDAO;
import cl.cbrs.aio.dto.UafBienDTO;
import cl.cbrs.aio.dto.UafPersonaDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.ParametrosUtil;

public class UafServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(UafServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings({ "unchecked" })
	public void buscarPersonas(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String nInscripciones = request.getParameter("nInscripciones");
		String ano = request.getParameter("ano");		
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try {			
			UafDAO uafDAO = new UafDAO();
			ArrayList<UafPersonaDTO> listaPersonas = uafDAO.buscarPersonas(nInscripciones, ano) ;			
			respuesta.put("listaPersonas", listaPersonas);
			
			HashMap<String, ArrayList<UafPersonaDTO>> hashMap = new HashMap<String, ArrayList<UafPersonaDTO>>();
			for(UafPersonaDTO dto : listaPersonas){				
				ArrayList<UafPersonaDTO> lista = hashMap.get(dto.getRut());
				if(hashMap.isEmpty() || !personaEnLista(lista, dto))
					if(!hashMap.containsKey(dto.getRut()))
						hashMap.put(dto.getRut(), new ArrayList<UafPersonaDTO>());
				
					hashMap.get(dto.getRut()).add(dto);// listaPersonasAgrupadas.add(dto);
			}		
			
			respuesta.put("listaPersonasAgrupadas", hashMap);

			status = true;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public void buscarBienes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String nInscripciones = request.getParameter("nInscripciones");
		String ano = request.getParameter("ano");
		String naturalezasReq = request.getParameter("naturalezas");
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try {			
			ArrayList<String> listaNaturalezas = new ArrayList<String>();
			JSONParser jsonParser = new JSONParser();
			JSONArray listaNaturalezasJSON = (JSONArray)jsonParser.parse(naturalezasReq);
			for(int i=0; i<listaNaturalezasJSON.size(); i++){
				JSONObject naturalezaJSON = (JSONObject)listaNaturalezasJSON.get(i);
				listaNaturalezas.add(naturalezaJSON.get("id").toString());
			}
			UafDAO uafDAO = new UafDAO();
			ArrayList<UafBienDTO> listaBienes = uafDAO.buscarBienes(nInscripciones, ano, listaNaturalezas) ;			
			
			HashMap<String, ArrayList<UafBienDTO>> hashMap = new HashMap<String, ArrayList<UafBienDTO>>();
			for(UafBienDTO dto : listaBienes){
				ArrayList<UafBienDTO> lista = hashMap.get(dto.getBorrador()+"-"+dto.getFolio());
				if(hashMap.isEmpty() || !bienEnLista(lista, dto))
					if(!hashMap.containsKey(dto.getBorrador()+"-"+dto.getFolio()))
						hashMap.put(dto.getBorrador()+"-"+dto.getFolio(), new ArrayList<UafBienDTO>());
				
					hashMap.get(dto.getBorrador()+"-"+dto.getFolio()).add(dto);// listaPersonasAgrupadas.add(dto);
			}		
			respuesta.put("listaBienesAgrupados", hashMap);

			status = true;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, intente nuevamente. Si el problema persiste comunicarse con soporte";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	
	
	private Boolean personaEnLista(ArrayList<UafPersonaDTO> lista, UafPersonaDTO persona){
		
		if(lista != null)
			for(UafPersonaDTO dto : lista){
				if(dto.getRut().equals(persona.getRut()))
					return true;
			}
		
		return false;
	}
	
	private Boolean bienEnLista(ArrayList<UafBienDTO> lista, UafBienDTO bien){
		
		if(lista != null)
			for(UafBienDTO dto : lista){
				if( (dto.getBorrador()+"+"+dto.getFolio()).equals(bien.getBorrador()+"+"+bien.getFolio() ))
					return true;
			}
		
		return false;
	}	

}
