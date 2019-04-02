package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dao.FolioRealDAO;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.InscripcionDigitalUtil;
import cl.cbrs.borrador.delegate.WsBorradorDelegate;
import cl.cbrs.borrador.vo.ProrealVO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;

public class BorradoresServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(BorradoresServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerCantidadBorradores(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		int totalBorradores = 0;
		
		try{
			
			Integer foja = Integer.parseInt(request.getParameter("foja"));
			Integer numero = Integer.parseInt(request.getParameter("numero"));
			Short ano = Short.parseShort(request.getParameter("ano"));
					
			WsBorradorDelegate wsBorradorDelegate = new WsBorradorDelegate();
			totalBorradores = wsBorradorDelegate.cantidadBorradores(foja, numero, ano, false);
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}
		
		respuesta.put("totalBorradores", totalBorradores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerBorradores(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		JSONArray borradores = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<ProrealVO> listaBorradores = null;
		
		try{
			
			Integer foja = Integer.parseInt(request.getParameter("foja"));
			Integer numero = Integer.parseInt(request.getParameter("numero"));
			Short ano = Short.parseShort(request.getParameter("ano"));
					
			WsBorradorDelegate wsBorradorDelegate = new WsBorradorDelegate();
			listaBorradores = wsBorradorDelegate.obtenerBorradores(foja, numero, ano, false);
			if(listaBorradores!=null){ 
				if(listaBorradores.size()>0){
					for(ProrealVO prorealVO: listaBorradores){
						JSONObject fila = new JSONObject();
						fila.put("borrador", prorealVO.getProRealId().getBorrador());
						fila.put("folio", prorealVO.getProRealId().getFolio());
						borradores.add(fila);
					}
				}	
			}	
			
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}
		
		respuesta.put("listaBorradores", borradores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerTitulosAnteriores(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		JSONArray titulosanteriores = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<ProrealVO> listaBorradores = null;
		int totalTitulosAnteriores = 0;
		try{
			WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();
			
			Integer foja = Integer.parseInt(request.getParameter("foja"));
			Integer numero = Integer.parseInt(request.getParameter("numero"));
			Short ano = Short.parseShort(request.getParameter("ano"));
			Boolean bis = Boolean.parseBoolean(request.getParameter("bis"));
			
			FolioRealDAO folioRealDAO = new FolioRealDAO();
			List<ProrealVO> listaTitulosAnteriores = folioRealDAO.consultaTitulosAnteriores(foja, numero, ano, bis);
			for(ProrealVO dto : listaTitulosAnteriores){
				JSONObject fila = new JSONObject();
				fila.put("foja", dto.getProRealId().getFojaP());
				fila.put("numero", dto.getProRealId().getNumeroP());
				fila.put("ano", dto.getProRealId().getAnoP());
				fila.put("bis", dto.getProRealId().getBisP());
				fila.put("vigente", dto.getVigenteT());
				
				InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
				ConsultaDocumentoDTO consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(foja.longValue(), numero.longValue(), ano.longValue(), bis, 1);
				fila.put("consultaDocumentoDTO", consultaDocumentoDTO);		
				
				InscripcionDigitalVO  inscripcionDigitalVO = wsInscripcionDigitalDelegate.obtenerInscripcionDigital(foja.longValue(), numero.toString(), ano.longValue(), bis);
//				if(inscripcionDigitalVO!=null && inscripcionDigitalVO.getEstadoInscripcionVO().getIdEstadoInscripcion()==1)
//					fila.put("estaDigitalizada", true);
//				else
//					fila.put("estaDigitalizada", false);
				if(inscripcionDigitalVO!=null)
					fila.put("idInscripcion", inscripcionDigitalVO.getIdInscripcion());
				else
					fila.put("idInscripcion", "");

				
//				Boolean estadoTieneRechazo = wsInscripcionDigitalDelegate.solicitudTieneRechazo(foja.longValue(), numero.toString(), ano.longValue(), bis?1:0);				
//				JSONObject estado = new JSONObject();
//				estado.put("tieneRechazo", estadoTieneRechazo);				
//				fila.put("estado", estado);
				
				titulosanteriores.add(fila);
			}
			respuesta.put("totalTitulosAnteriores", listaTitulosAnteriores.size());
					
//			WsBorradorDelegate wsBorradorDelegate = new WsBorradorDelegate();
//			WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();
//			listaBorradores = wsBorradorDelegate.obtenerBorradores(foja, numero, ano, false);
//			int i=0;
//			if(listaBorradores!=null){ 
//				if(listaBorradores.size()>0){
//					for(ProrealVO prorealVO: listaBorradores){
//						List<ProrealVO> prorealVOs = wsBorradorDelegate.consultaTitulosAnteriores(prorealVO.getProRealId().getBorrador(),prorealVO.getProRealId().getFolio());
//						if(prorealVOs!=null){ 
//							if(prorealVOs.size()>0){
//								for(ProrealVO tituloAnterior: prorealVOs){
//									JSONObject fila = new JSONObject();
//									
//									Integer fojaP = tituloAnterior.getProRealId().getFojaP();
//									Integer numeroP = tituloAnterior.getProRealId().getNumeroP();
//									Short anoP = tituloAnterior.getProRealId().getAnoP();
//									Boolean bisP = tituloAnterior.getProRealId().getBisP();
//									
//									fila.put("foja", fojaP);
//									fila.put("numero", numeroP);
//									fila.put("ano", anoP);
//									int bis=0;
//									if(bisP)
//										bis=1;
//									
//									fila.put("bis", bis);
//									fila.put("vigente", tituloAnterior.getVigenteT());
//									
//									InscripcionDigitalVO  inscripcionDigitalVO = wsInscripcionDigitalDelegate.obtenerInscripcionDigital(fojaP.longValue(), numeroP.toString(), anoP.longValue(), bisP);
//									if(null!=inscripcionDigitalVO){
//										fila.put("idInscripcion", inscripcionDigitalVO.getIdInscripcion());
//									}else{
//										fila.put("idInscripcion", "");
//									}
//									
//									InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
//									ConsultaDocumentoDTO consultaDocumentoDTO = digitalUtil.getConsultaDocumentoDTO(fojaP.longValue(), numeroP.longValue(), anoP.longValue(), bisP, 1);
//									fila.put("consultaDocumentoDTO", consultaDocumentoDTO);
//									
//									Boolean estadoTieneRechazo = wsInscripcionDigitalDelegate.solicitudTieneRechazo(foja.longValue(), numero.toString(), ano.longValue(), bis);
//									
//									JSONObject estado = new JSONObject();
//									estado.put("tieneRechazo", estadoTieneRechazo);
//									
//									fila.put("estado", estado);
//									
//									titulosanteriores.add(fila);
//									if(i>=30){
//										i++;
//										break;
//									}
//									i++;
//								}
//							}
//						}
//					}
//				}	
//			}	
//			totalTitulosAnteriores=i;
			status = true;
			
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}
		
		respuesta.put("listaTitulosAnteriores", titulosanteriores);
//		respuesta.put("totalTitulosAnteriores", totalTitulosAnteriores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
}