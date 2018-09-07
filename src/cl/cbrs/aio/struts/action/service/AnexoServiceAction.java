package cl.cbrs.aio.struts.action.service;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbrs.aio.dto.AnexoDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.AnexosUtil;
import cl.cbrs.aio.util.ParametrosUtil;

public class AnexoServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(AnexoServiceAction.class);
	
	JSONArray listaAnexosJSONCache = null;
	JSONArray listaSeccionesJSONCache = null;

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void getAnexos(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("estado", true);	

		AnexosUtil anexosUtil = new AnexosUtil();
		ParametrosUtil parametrosUtil = new ParametrosUtil();

		try {		
			if(listaAnexosJSONCache == null)				
				listaAnexosJSONCache = anexosUtil.getAnexosJSONArray();				
			
			if(listaSeccionesJSONCache==null)
				listaSeccionesJSONCache= parametrosUtil.getSeccionesJSONArray();
			
			json.put("listaAnexos", listaAnexosJSONCache);
			json.put("listaSecciones", listaSeccionesJSONCache);	

		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al obtener anexos");
			json.put("estado", false);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void guardarAnexo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String anexoReq = request.getParameter("anexoReq");
		System.out.println(anexoReq);

		JSONObject json = new JSONObject();
		json.put("estado", false);	

		AnexosUtil anexosUtil = new AnexosUtil();

		try {		
			JSONParser jsonParser = new JSONParser();
			JSONObject anexoEditadoJSON = (JSONObject)jsonParser.parse(anexoReq);
			String responseEditar = anexosUtil.editarAnexo(anexoEditadoJSON);
			Long idAnexoEditar = (Long)anexoEditadoJSON.get("idAnexo");
			
			if("OK".equalsIgnoreCase(responseEditar)){
				json.put("estado", true);
				int indice = 0;
				for(Object obj: listaAnexosJSONCache){
					AnexoDTO anexoDTO = (AnexoDTO)obj;
					if(anexoDTO.getIdAnexo().compareTo(idAnexoEditar)==0){
						listaAnexosJSONCache.remove(obj);
						break;
					}
					indice++;
				}
				listaAnexosJSONCache.add(indice,anexoEditadoJSON);
			} 			
			
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al guardar anexo");
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	


}