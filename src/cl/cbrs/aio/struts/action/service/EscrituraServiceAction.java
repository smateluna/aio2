package cl.cbrs.aio.struts.action.service;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.DocumentoDAO;
import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.DocumentoDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;

public class EscrituraServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(EscrituraServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings("unchecked")
	public void obtenerEscrituraPorInscripcion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		response.setContentType("application/json; charset=ISO-8859-1");

		String caratulaReq = request.getParameter("caratula");

		try {           
			if(caratulaReq!=""){
				int caratula = Integer.parseInt(caratulaReq);
				String server = TablaValores.getValor("repertorioWeb.properties", "HOSTS", "HOST_ESCRITURAS");
				logger.debug("SERVER    "+server);
				String httpResponse = excuteHttp("http://"+server+"/documentos/do/caratulaService", "metodo=getCaratulaVersion&_dc=1339697228141&caratulap="+caratula+"&tipoDocumentop=1&tipoRegistrop=0&node=root");
				//	            System.out.println("httpResponse "+httpResponse);
				JSONParser parser = new JSONParser();
				JSONObject caratulaServiceJSON = (JSONObject)parser.parse(httpResponse);

				if((Boolean)caratulaServiceJSON.get("success")){
					JSONObject dataJSON = (JSONObject)caratulaServiceJSON.get("data");
					//	                  System.out.println("dataJSON " +dataJSON);
					if((Boolean)dataJSON.get("existe")){
						Long version = (Long)dataJSON.get("version");                          
						String url = "http://"+server+"/documentos/do/fileService?metodo=download&idDocumentop=0&idDetalleDocumentop=0&caratulap="+caratula+"&versionp="+version+"&idTipoDocumentop=1&idRegp=0&esVersionp=true&esPrincipalp=true&esDetallep=false";

						respuesta.put("urlPdf", url);
						respuesta.put("tieneImagen", 1);
						respuesta.put("status", true);
					}else{
						respuesta.put("tieneImagen", 0);
						respuesta.put("status", true);
					}

				} else{
					respuesta.put("tieneImagen", 0);
					respuesta.put("status", true);
				}           
			}else{
				respuesta.put("tieneImagen", 0);
				respuesta.put("status", true);
			}

			respuesta.writeJSONString(response.getWriter());
		} catch (Exception e) {
			logger.error("Error buscarUrlEscritura: " + e.getMessage());
			respuesta.put("status", false);
			respuesta.put("msg", "Se ha producido un error en el servicio, intente nuevamente");
			try{respuesta.writeJSONString(response.getWriter());}catch(Exception e1){}
		}

	}

	private String excuteHttp(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response  
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	public void verDocumento(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String caratulaReq = request.getParameter("caratula");
		String url= "";        
		String type= request.getParameter("type");

		try {           
			if(caratulaReq!=""){
				int caratula = Integer.parseInt(caratulaReq);
				String server = TablaValores.getValor("repertorioWeb.properties", "HOSTS", "HOST_ESCRITURAS");
				String httpResponse = excuteHttp("http://"+server+"/documentos/do/caratulaService", "metodo=getCaratulaVersion&_dc=1339697228141&caratulap="+caratula+"&tipoDocumentop=1&tipoRegistrop=0&node=root");
				//		            System.out.println("httpResponse "+httpResponse);
				JSONParser parser = new JSONParser();
				JSONObject caratulaServiceJSON = (JSONObject)parser.parse(httpResponse);

				if((Boolean)caratulaServiceJSON.get("success")){
					JSONObject dataJSON = (JSONObject)caratulaServiceJSON.get("data");
					//		                  System.out.println("dataJSON " +dataJSON);
					if((Boolean)dataJSON.get("existe")){
						Long version = (Long)dataJSON.get("version");                          
						url = "http://"+server+"/documentos/do/fileService?metodo=download&idDocumentop=0&idDetalleDocumentop=0&caratulap="+caratula+"&versionp="+version+"&idTipoDocumentop=1&idRegp=0&esVersionp=true&esPrincipalp=true&esDetallep=false";
					}

				}           
			}	            

		} catch (Exception e) {
			logger.error("Error buscarUrlEscritura: " + e.getMessage());
		}

		ServletOutputStream out = null;

		URL urlNet;
		URI uri = null;

		try {    
			out = response.getOutputStream();                                            

			response.setContentType("application/pdf");
			logger.info("Abriendo fichero: " + url + " type: " + type);

			if(type!=null && "uri".equalsIgnoreCase(type)){
				String codigo= (String) request.getParameter("codigo");

				if(codigo!=null && !"".equals(codigo))
					url=url+"&codigo="+codigo;

				uri = new URI(url);

				urlNet = uri.toURL();

				URLConnection conn = urlNet.openConnection();
				InputStream inputStream = conn.getInputStream();
				byte[] buffer2 = IOUtils.toByteArray(inputStream);
				out.write(buffer2, 0, buffer2.length);

			} else{
				FileInputStream file = new FileInputStream(url);

				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				do{
					bytesRead = file.read(buffer, 0, buffer.length);
					out.write(buffer, 0, bytesRead);
				}while (bytesRead == buffer.length);

				if(file!=null)
					file.close();

			}

			out.flush();

			if(out != null)
				out.close();
		} catch (Exception e) {
			logger.error("Error al buscar documento: " + e.getMessage(),e);
			request.setAttribute("error", "Archivo no encontrado.");
		} finally{
			if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
		}

	}     

	public void verDocumentoEstudio(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("application/pdf");

		String caratulaReq = request.getParameter("caratula");
		String versionReq = request.getParameter("version");
		String idTipoDocumentoReq = request.getParameter("idTipoDocumento");
		DocumentoDAO dm = new DocumentoDAO();
		DocumentoDTO documentoDTO = new DocumentoDTO();

		ServletOutputStream out = null;

		try {
			documentoDTO = dm.getDocumento(Long.parseLong(caratulaReq), Integer.parseInt(versionReq), Long.parseLong(idTipoDocumentoReq));

			if(documentoDTO==null){
				logger.error("Error: Carátula no encontrada en documentos");
				request.setAttribute("error", "Error: Carátula no encontrada en documentos");
			}

			DocumentosCliente documentosCliente = new DocumentosCliente();
			byte[] archivo = null;
			if(documentoDTO.getIdTipoDocumento().intValue() == 1){
				logger.debug("CAratula:"+caratulaReq+" Tipo documento 1");
				archivo = documentosCliente.downloadEscritura(true, documentoDTO.getIdDocumento(), 0L, 0L, documentoDTO.getVersion().intValue(), false, false, true);
			}
			else if(documentoDTO.getIdTipoDocumento().intValue() == 3){
				logger.debug("CAratula:"+caratulaReq+" Tipo documento 3");
				archivo = documentosCliente.downloadDocumento(3, 0, documentoDTO.getNombreArchivoVersion(), documentoDTO.getFechaProcesa());
			}
			out = response.getOutputStream();			    
			out.write(archivo, 0, archivo.length);
			out.flush();

			if(out != null)
				out.close();
		} catch(HTTPException e){
			logger.error("Error HTTP codigo " + e.getStatusCode() + " al buscar documento: " + e.getMessage(),e);
			request.setAttribute("error", "Archivo no encontrado.");	        	
		} catch (Exception e) {
			logger.error("Error al buscar documento: " + e.getMessage(),e);
			request.setAttribute("error", "Archivo no encontrado.");
		} finally{
			if(out!=null){try{out.close();}catch(Exception e){logger.error("Error: " + e.getMessage(),e);}}
		}


	}  

	public void eliminarDocumento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String idDocumentoS = (String)request.getParameter("idDocumento");
		String caratulaS = (String)request.getParameter("caratula");
		String version = (String)request.getParameter("version");

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		Long idDocumento = null;
		Long caratula = null;

		try {
			if(StringUtils.isNotBlank(idDocumentoS)){
				CaratulasUtil caratulasUtil=new CaratulasUtil();
				
				idDocumento = Long.parseLong(idDocumentoS);
				caratula = Long.parseLong(caratulaS);
			
				DocumentosCliente delegate = new DocumentosCliente();
				respuesta = delegate.eliminarEscritura(idDocumento, false, true);
				
				if(respuesta.containsKey("estadoUpdate") && respuesta.get("estadoUpdate").equals(true)){
					String observacion = "Eliminacion Escritura Version "+ version;
					String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
					caratulasUtil.agregarBitacoraCaratula(caratula, rutUsuario, observacion,BitacoraCaratulaVO.OBSERVACION_INTERNA);
					
					status = true;
				} else{
					msg = "No se pudo eliminar la escritura. " + respuesta.get("errormsg");
					status = false;
				}
			}


		} catch (Exception e) {
			logger.error(e);

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

}