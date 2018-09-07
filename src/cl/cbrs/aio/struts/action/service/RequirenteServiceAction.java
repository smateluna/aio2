package cl.cbrs.aio.struts.action.service;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;

import cl.cbr.util.RUTUtil;
import cl.cbr.util.StringUtil;
import cl.cbrs.aio.dao.RequirenteDAO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;

public class RequirenteServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(RequirenteServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void getNombre(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();

		json.put("nombre", "");
		json.put("hayNombre", false);
		json.put("status", true);

		String rut = request.getParameter("rut");

		//bloque rut

		if(!StringUtils.isBlank(rut) && rut.length()>=2){
			rut = rut.replaceAll("\\.", "");
			rut = rut.replaceAll("-", "");

			String rut1 = rut.substring(0, rut.length()-1);
			String dv1 = rut.substring(rut.length()-1, rut.length());

			if(rut1 != null && dv1 != null ){

				if(!StringUtils.isBlank(dv1)){
					dv1 = dv1.toUpperCase();
				}else{
					dv1 = "";
				}

				if(StringUtils.isNumeric(rut1) && StringUtils.isNotBlank(rut1) && RUTUtil.validaDigitoVerificador(rut1, dv1)){

					try {
						String rutFinal = rut1+dv1;

						rutFinal = StringUtil.completaPorLaIzquierda(rutFinal, 9, '0');

						RequirenteDAO requirenteDAO = new RequirenteDAO();

						String nombre = requirenteDAO.getNombre(rutFinal);

						if(StringUtils.isNotBlank(nombre)){
							json.put("nombre", nombre.trim());
							json.put("hayNombre", true);
						}
					} catch (Exception e) {

					}
				}	
			}
		}
		
		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}	
	}

	@SuppressWarnings({ "unchecked" })
	public void getRequirenteFull(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();

		json.put("nombre", "");
		json.put("apep", "");
		json.put("apem", "");
		json.put("direccion", "");
		json.put("telefono", "");
		json.put("correo", "");
		json.put("hayNombre", false);
		json.put("status", true);

		String rut = request.getParameter("rut");

		//bloque rut

		if(!StringUtils.isBlank(rut) && rut.length()>=2){
			rut = rut.replaceAll("\\.", "");
			rut = rut.replaceAll("-", "");

			String rut1 = rut.substring(0, rut.length()-1);
			String dv1 = rut.substring(rut.length()-1, rut.length());

			if(rut1 != null && dv1 != null ){

				if(!StringUtils.isBlank(dv1)){
					dv1 = dv1.toUpperCase();
				}else{
					dv1 = "";
				}

				if(StringUtils.isNumeric(rut1) && StringUtils.isNotBlank(rut1) && RUTUtil.validaDigitoVerificador(rut1, dv1)){

					try {
						String rutFinal = rut1+dv1;

						rutFinal = StringUtil.completaPorLaIzquierda(rutFinal, 9, '0');

						RequirenteDAO requirenteDAO = new RequirenteDAO();

						RequirenteVO requirenteVO = requirenteDAO.getRequirenteFull(rutFinal);

						if(null!=requirenteVO){
							json.put("nombre", requirenteVO.getNombres().trim());
							json.put("apep", requirenteVO.getApellidoPaterno().trim());
							json.put("apem", requirenteVO.getApellidoMaterno().trim());
							json.put("direccion", requirenteVO.getDireccion().trim());
							json.put("telefono", requirenteVO.getTelefono());
							json.put("correo", requirenteVO.getEmail());							
							json.put("hayNombre", true);
						}
					} catch (Exception e) {

					}
				}	
			}
		}	

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}		
	}	
}