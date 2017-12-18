package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.RecepcionPlanoDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.planos.vo.SimplePlanoVO;
import cl.cbrs.planos.vo.TituloPlanoVO;
import cl.cbrs.planos.ws.ServicioPlanoDelegate;

public class PlanosServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(PlanosServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerPlanosPorTitulos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		JSONArray planos = new JSONArray();
		Boolean status = false;
		String msg = "";
		int totalPlanos = 0;
		
		try{
			
			Integer foja = Integer.parseInt(request.getParameter("foja"));
			Integer numero = Integer.parseInt(request.getParameter("numero"));
			Short ano = Short.parseShort(request.getParameter("ano"));
					
			ServicioPlanoDelegate servicioPlanoDelegate = new ServicioPlanoDelegate();
			
			TituloPlanoVO[] listaTitulos = new TituloPlanoVO[1];
			TituloPlanoVO tituloPlanoVO = new TituloPlanoVO();
			tituloPlanoVO.setFoja(foja);
			tituloPlanoVO.setNumero(numero);
			tituloPlanoVO.setAno(ano);
			tituloPlanoVO.setBis(false);
			listaTitulos[0]=tituloPlanoVO;
			
			listaTitulos = servicioPlanoDelegate.obtenerPlanosPorTitulos(listaTitulos);
			
			if(listaTitulos!=null){ 
				if(listaTitulos.length>0){
					for (TituloPlanoVO tituloPlanoVOa : listaTitulos) {
						SimplePlanoVO[] simplePlanoVOs = tituloPlanoVOa.getSimplePlano();
						if(simplePlanoVOs!=null){
							totalPlanos = simplePlanoVOs.length;
							//for (int j = 0; j < simplePlanoVOs.length; j++) {
							for (SimplePlanoVO simplePlanoVO : simplePlanoVOs) {
								JSONObject fila = new JSONObject();
								fila.put("idCategoria", simplePlanoVO.getIdCategoria());
								fila.put("idPlano", simplePlanoVO.getIdPlano());
								fila.put("nombreArchivo", simplePlanoVO.getNombreArchivo());
								fila.put("numeroPlano", simplePlanoVO.getNumeroPlano());
								planos.add(fila);
							}
						}
					}
				}	
			}	
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}
		
		respuesta.put("listaPlanos", planos);
		respuesta.put("totalPlanos", totalPlanos);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerPestamosPendientes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		ArrayList<RecepcionPlanoDTO> listaPlanosPendientes = null;
		
		try{					
			FlujoDAO flujoDAO = new FlujoDAO();
			
			listaPlanosPendientes = flujoDAO.getRecepcionPlanosPendientes();
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}
		
		respuesta.put("listaPlanosPendientes", listaPlanosPendientes);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	
	
}