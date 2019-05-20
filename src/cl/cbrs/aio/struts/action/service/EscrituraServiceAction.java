package cl.cbrs.aio.struts.action.service;


import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;

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
	public void existeEscritura(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		String caratulaReq = request.getParameter("caratula");

		try {           
			if(caratulaReq!=null && !"".equals(caratulaReq)){
				Long caratula = Long.parseLong(caratulaReq);
				DocumentosCliente documentosCliente = new DocumentosCliente();
				JSONObject json = documentosCliente.existeEscritura(caratula);
				
				respuesta.put("hayDocumento", (Boolean)json.get("hayDocumento"));

				if((Boolean)json.get("hayDocumento")){
						respuesta.put("tieneImagen", 1);
						respuesta.put("status", true);
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
			logger.error("Error buscar Escritura: " + e.getMessage());
			respuesta.put("status", false);
			respuesta.put("msg", "Se ha producido un error en el servicio, intente nuevamente");
			try{respuesta.writeJSONString(response.getWriter());}catch(Exception e1){}
		}

	}

	public void verDocumento(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("application/pdf");
		
		String caratulaReq = request.getParameter("caratula");
		ServletOutputStream out = null;
		
		if(caratulaReq!=""){
			try {           			
					DocumentosCliente documentosCliente = new DocumentosCliente();
					byte[] doc = documentosCliente.downloadEscritura(true, Long.parseLong(caratulaReq));
					out = response.getOutputStream();                           
					out.write(doc, 0, doc.length);     					
					out.flush();	
					if(out != null)
						out.close();  
			} catch (Exception e) {
				logger.error("Error buscarUrlEscritura: " + e.getMessage());
			}
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
			} else{
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
			}
        } catch(IOException e){
        	logger.warn("Se cancelo descarga documento caratula: " + caratulaReq);
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

	@SuppressWarnings("unchecked")
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