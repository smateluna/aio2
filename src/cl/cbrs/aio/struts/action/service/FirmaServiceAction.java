package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.RestUtil;

public class FirmaServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(FirmaServiceAction.class);
	private static final String ARCHIVO = "ws_firmaelectronica.parametros";
	
	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings("unchecked")
	public void obtenerTiposCertificadosPorPerfil(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray tiposCertificados = new JSONArray();
		Boolean status = false;
		String msg = "";
		String idPerfilReq = (String)request.getParameter("idPerfil");
		
		try{

			Client client = Client.create();
			String ip = TablaValores.getValor(ARCHIVO, "IP", "valor");
			String port = TablaValores.getValor(ARCHIVO, "PORT", "valor");				

			WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/FirmaRest/firma/plantilla/"+idPerfilReq));

			ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
			com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

			if(statusRespuesta.getStatusCode() == 200){
				tiposCertificados = (JSONArray) RestUtil.getResponse(clientResponse);
			}				

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listatiposCertificados", tiposCertificados);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerTiposCertificadosPorIdPlantilla(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		String idPlanillaReq = (String)request.getParameter("idPlanilla");
		JSONObject tipoCertificado = new JSONObject();
		
		try{

			Client client = Client.create();
			String ip = TablaValores.getValor(ARCHIVO, "IP", "valor");
			String port = TablaValores.getValor(ARCHIVO, "PORT", "valor");				

			WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/FirmaRest/firma/plantillaPorId/"+idPlanillaReq));
//			WebResource wr = client.resource(new URI("http://192.168.102.210:8080/FirmaRest/firma/plantillaPorId/"+idPlanillaReq));

			ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
			com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

			if(statusRespuesta.getStatusCode() == 200){
				tipoCertificado = (JSONObject) RestUtil.getResponse(clientResponse);
			}				

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("tipoCertificado", tipoCertificado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
}