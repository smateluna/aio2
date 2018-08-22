package cl.cbrs.aio.struts.action.service;


import java.io.BufferedOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.documentos.constantes.ConstantesDocumentos;

public class PdfServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(PdfServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	
	public void getInscripcion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {	
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");
		String bisp = request.getParameter("bis");
		String estado = request.getParameter("estado");
		String registro = request.getParameter("registro");


		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Boolean bis = false;
		
		//Integer bisi = 0;
		if("true".equals(bisp)){
			bis = true;	
			//bisi = 1;
		}
		
		Integer tipoDocumento = null;
		try {
			tipoDocumento = Integer.parseInt(estado);
		} catch (Exception e1) {

		}
		
		try{
			DocumentosCliente documentosCliente = new DocumentosCliente();
		
			if("prop".equals(registro)){
				byte[] pdfByte = null;
						
				pdfByte = documentosCliente.downloadInscripcion(ConstantesDocumentos.FORMATO_PDF, foja.intValue(), Integer.valueOf(numero), ano.intValue(), bis, 1, tipoDocumento);
				OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
				response.setContentType("application/pdf");
				out.write(pdfByte);
				out.close();

			} else if("proh".equals(registro)){
				byte[] pdfByte = null;
							
				pdfByte = documentosCliente.downloadInscripcion(ConstantesDocumentos.FORMATO_PDF, foja.intValue(), Integer.valueOf(numero), ano.intValue(), bis, 3, tipoDocumento);
				OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
				response.setContentType("application/pdf");
				out.write(pdfByte);
				out.close();

			} else if("hip".equals(registro)){
				byte[] pdfByte = null;
							
				pdfByte = documentosCliente.downloadInscripcion(ConstantesDocumentos.FORMATO_PDF, foja.intValue(), Integer.valueOf(numero), ano.intValue(), bis, 2, tipoDocumento);
				OutputStream out = new BufferedOutputStream(response.getOutputStream()); 
				response.setContentType("application/pdf");
				out.write(pdfByte);
				out.close();
					
			}
		} catch(HTTPException e){
			logger.error("Error HTTP codigo " + e.getStatusCode(), e);
		} catch (Exception e) {
			logger.error("Problemas obteniendo documento "+registro+":"+foja+"-"+numero+"-"+ano+" bis:"+bis+" tipoDocumento:"+tipoDocumento);
		}
	}
}