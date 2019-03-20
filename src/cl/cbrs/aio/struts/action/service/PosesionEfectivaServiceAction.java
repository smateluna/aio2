package cl.cbrs.aio.struts.action.service;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;

public class PosesionEfectivaServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(PosesionEfectivaServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	public void verPosesionEfectiva(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("application/pdf");

		String documentoReq = request.getParameter("documento");
		
		ServletOutputStream out = null;

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject documentoJSON = (JSONObject)jsonParser.parse(documentoReq);
			Integer idTipoDocumentop = Integer.parseInt(documentoJSON.get("idTipoDocumento").toString()); 
			String nombreArchivop = documentoJSON.get("nombreArchivo").toString(); 
			Integer idRegp = documentoJSON.get("idReg")==null?0:Integer.parseInt(documentoJSON.get("idReg").toString()); 
			Date fechap = null;
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

			if(documentoJSON.get("fechaDocumento")!=null)
				fechap = format.parse(documentoJSON.get("fechaDocumento").toString());

			
			DocumentosCliente documentosCliente = new DocumentosCliente();
			byte[] archivo = documentosCliente.downloadDocumento(idTipoDocumentop, idRegp, nombreArchivop, fechap);

			response.setHeader("Content-Disposition", "filename=" + nombreArchivop);
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
	
	
}