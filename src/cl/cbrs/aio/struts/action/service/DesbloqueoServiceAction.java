package cl.cbrs.aio.struts.action.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.EstadoVO;
import cl.cbrs.inscripciondigital.vo.SolicitudVO;

public class DesbloqueoServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(DesbloqueoServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}


	@SuppressWarnings({ "unchecked" })
	public void buscar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String inscripcionReq = request.getParameter("inscripcion");

		JSONObject json = new JSONObject();
		json.put("estado", true);	

		WsInscripcionDigitalDelegate digitalDelegate = new WsInscripcionDigitalDelegate();
		JSONParser jsonParser = new JSONParser();

		try {		
			JSONObject inscripcionJSON = (JSONObject)jsonParser.parse(inscripcionReq);
			Long foja = new Long(inscripcionJSON.get("foja").toString());
			String numero = inscripcionJSON.get("numero").toString();
			Long ano = new Long(inscripcionJSON.get("ano").toString());
			Integer bis = inscripcionJSON.get("bis")!=null?(Boolean)inscripcionJSON.get("bis")?1:0:0;
			
			List<SolicitudVO> solicitudVOs =  digitalDelegate.obtenerSolicitudesPorFojaNumAno(foja, numero, ano, bis, null);
			
			JSONArray listaSolicitudes = new JSONArray();
			
			if(solicitudVOs!=null){
				for(SolicitudVO solicitudVO : solicitudVOs){
					JSONObject solicitudJSON = new JSONObject();
					solicitudJSON.put("idSolicitud", solicitudVO.getIdSolicitud());
					if(solicitudVO.getEstado()!=null){
						solicitudJSON.put("estado", solicitudVO.getEstado().getDescripcion());
						solicitudJSON.put("idEstado", solicitudVO.getEstado().getIdEstado());
					}
					solicitudJSON.put("foja", solicitudVO.getFoja());
					solicitudJSON.put("numero", solicitudVO.getNumero());
					solicitudJSON.put("ano", solicitudVO.getAno());
					solicitudJSON.put("bis", solicitudVO.getBis());
					solicitudJSON.put("usuario", solicitudVO.getUsuario());
					solicitudJSON.put("fechaEstado", solicitudVO.getFechaEstado().getTime());
					listaSolicitudes.add(solicitudJSON);
				}
			}

			json.put("listaSolicitudes", listaSolicitudes);

			
		} catch(ParseException pe){
			logger.error(pe);
			json.put("msg", "Problemas en servidor al buscar inscripcion");
			json.put("estado", false);
		} catch (Exception e) {
			json.put("msg", "Problemas en servidor al buscar inscripcion");
			json.put("estado", false);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void desbloquear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");
		
		String solicitudReq = request.getParameter("solicitud");
		String listaSolicitudesReq = request.getParameter("listaSolicitudes");

		JSONObject json = new JSONObject();
		json.put("estado", false);	
		String detalleError = "";

		WsInscripcionDigitalDelegate digitalDelegate = new WsInscripcionDigitalDelegate();
		JSONParser jsonParser = new JSONParser();

		try {		
			JSONObject solicitudJSON = (JSONObject)jsonParser.parse(solicitudReq);
			Long idSolicitud = new Long(solicitudJSON.get("idSolicitud").toString());
			Long foja = new Long(solicitudJSON.get("foja").toString());
			String numero = solicitudJSON.get("numero").toString();
			Long ano = new Long(solicitudJSON.get("ano").toString());
			Integer bis = new Integer(solicitudJSON.get("bis").toString());
			String usuario = request.getUserPrincipal().getName();
			usuario = usuario.replaceAll("CBRS\\\\", "");
			
			logger.info("Desbloqueando solicitud id: " + idSolicitud + "\n Foja: " + foja + "\n Numero: " + numero + 
					"\n Ano: " + ano + "\n (Usuario: " + usuario + ")");
			
			//Buscar solicitudes Rechazadas
			//List<SolicitudVO> solicitudVOs =  digitalDelegate.obtenerSolicitudesPorFojaNumAno(foja, numero, ano, bis, 3L);
			JSONArray listaSolicitudesJSON = (JSONArray)jsonParser.parse(listaSolicitudesReq);
			
			if(listaSolicitudesJSON!=null){
				for(int i=0; i<listaSolicitudesJSON.size(); i++){
					JSONObject solicitud = (JSONObject)listaSolicitudesJSON.get(i);
					Long idSolicitudLista = (Long)solicitud.get("idSolicitud");
					SolicitudVO solicitudVO = digitalDelegate.obtenerSolicitud(idSolicitudLista);
					solicitudVO.setFechaEstado(new Date());
					solicitud.put("fechaEstado", solicitudVO.getFechaEstado().getTime());
					
					
					if(solicitudVO!=null && solicitudVO.getEstado()!=null && (solicitudVO.getEstado().getIdEstado().equals(3L) || solicitudVO.getEstado().getIdEstado().equals(6L))){
						
						EstadoVO estado = new EstadoVO();
						
						if(!idSolicitud.equals(idSolicitudLista)){
							logger.info("Actualizar solicitud id " + idSolicitudLista + " a estado 4");
							estado.setIdEstado(4L);
							solicitud.put("idEstado", 4);
							solicitud.put("estado", "En Espera (ya existe una solicitud)");

						} else{
							logger.info("Actualizar solicitud id " + idSolicitudLista + " a estado 1");
							estado.setIdEstado(1L);
							solicitud.put("idEstado", 1);
							solicitud.put("estado", "En Espera");
						}
						
						solicitudVO.setEstado(estado );
						String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
						solicitudVO.setRut(new Long(rutUsuario));
						solicitudVO.setUsuario(usuario);	
						solicitud.put("usuario", usuario);
						digitalDelegate.actualizarSolicitud(solicitudVO );
						
					}
				}
			}
			
			json.put("estado", true);
			json.put("listaSolicitudes", listaSolicitudesJSON);		
						
			
		} /*catch (GeneralException e) {
			json.put("msg", "Problemas en servidor al reingresar caratula: " + e.getInfoAdic());
			logger.error("Error: " + e.getMessage(),e);
		} */catch (Exception e) {
			json.put("msg", "Problemas en servidor al desbloquear solicitud");
			logger.error("Error: " + e.getMessage(),e);
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@SuppressWarnings({ "unchecked" })
	public void getInscripcionSesion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);	

		try {		
			InscripcionDigitalDTO digitalDTO = (InscripcionDigitalDTO)request.getSession().getAttribute("inscripcion");
			if(digitalDTO!=null){
				json.put("foja", digitalDTO.getFoja());
				json.put("numero", digitalDTO.getNumero());
				json.put("ano", digitalDTO.getAno());
				json.put("bis", digitalDTO.getBis());
				
				json.put("status", true);
			} 
			
		} catch (Exception e) {
        	
			logger.error("error en getCaratulaSesion: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar caratula en sesion");
			
		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	} 	
	
	
	private String cambiaEncoding(String campo) throws UnsupportedEncodingException{
		String campoConEncoding=new String("");
		if(null!=campo){
			if(!"".equals(campo)){
				campoConEncoding = new String(campo.getBytes(),"UTF-8");
			}
		}
		
		return campoConEncoding;
		
	}

}