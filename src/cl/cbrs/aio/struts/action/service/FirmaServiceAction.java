package cl.cbrs.aio.struts.action.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.StringUtil;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.ConstantesPortalConservador;
import cl.cbrs.aio.util.InscripcionDigitalUtil;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.CaratulaTempVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CausalRechazoVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaTempVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TareaIdTempVO;
import cl.cbrs.caratula.flujo.vo.TareaTempVO;
import cl.cbrs.caratula.flujo.vo.TipoFormularioVO;
import cl.cbrs.cuentacorriente.delegate.WsCuentaCorrienteClienteDelegate;
import cl.cbrs.cuentacorriente.vo.ListaNominaCtaCteAioVO;
import cl.cbrs.cuentacorriente.vo.NominaCtaCteAioVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

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
				tiposCertificados = (JSONArray) getResponse(clientResponse);
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
	
	private static Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(response.getType()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(MediaType.APPLICATION_JSON_TYPE.equals(response.getType()))
				respuesta = new JSONParser().parse(response.getEntity(String.class));
			else
				respuesta = response.getEntity(String.class);

		} else if(response!=null)
			throw new HTTPException(response.getStatus());
		else
			throw new Exception("Sin respuesta del servicio");
		return respuesta;
	}      
	
	
	
	
}