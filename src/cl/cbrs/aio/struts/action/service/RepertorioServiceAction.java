package cl.cbrs.aio.struts.action.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.delegate.repertorio.WsRepertorioClienteDelegate;
import cl.cbrs.repertorio.flujo.vo.ListadoRepertorioVO;
import cl.cbrs.repertorio.flujo.vo.RepertorioPorRegistroVO;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;

public class RepertorioServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(RepertorioServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings({ "unchecked" })
	public void getRepertorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";

		List<RepertorioVO> repertorioVOs = null;
		Long numCaratula = (request.getParameter("numCaratula")==null || request.getParameter("numCaratula").equals("null") || request.getParameter("numCaratula").trim().equals(""))?null:Long.parseLong(request.getParameter("numCaratula"));
		Long numRepertorio = (request.getParameter("numRepertorio")==null || request.getParameter("numRepertorio").equals("null") || request.getParameter("numRepertorio").trim().equals(""))?null:Long.parseLong(request.getParameter("numRepertorio"));
		Integer anio = request.getParameter("anio")==null?null:Integer.parseInt(request.getParameter("anio"));

		try {

			WsRepertorioClienteDelegate cliente = new WsRepertorioClienteDelegate();

			repertorioVOs = cliente.consultaRepertorio(numCaratula,numRepertorio,anio);

			if(repertorioVOs!=null){ 
				if(repertorioVOs.size()>0){
					for(RepertorioVO repertorio: repertorioVOs){
						JSONObject fila = new JSONObject();
						fila.put("caratula", repertorio.getRepertorioIdVO().getCaratula());
						fila.put("repertorio", repertorio.getRepertorioIdVO().getNumRepertorioProp());
						fila.put("anorepertorio", repertorio.getRepertorioIdVO().getAnoRepertorioProp());
						fila.put("tipo", repertorio.getActosCont()!=null?repertorio.getActosCont().trim():"");
						fila.put("requirente", repertorio.getRequirente()!=null?repertorio.getRequirente().trim():"");
						fila.put("notario", repertorio.getNotario()!=null?repertorio.getNotario().trim():"");
						fila.put("notaria", repertorio.getNotaria()!=null?repertorio.getNotaria().trim():"");
						fila.put("vendedor", repertorio.getNombreApellVende()!=null?repertorio.getNombreApellVende().trim():"");
						fila.put("comprador", repertorio.getNombreApellCompra()!=null?repertorio.getNombreApellCompra().trim():"");
						fila.put("fechaIngreso", repertorio.getFechaIngreso()!=null?repertorio.getFechaIngreso().getTime():"");
						fila.put("obs", repertorio.getObservacion()!=null?repertorio.getObservacion():"");

						resultado.add(fila);
					}
				}
			}

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	@SuppressWarnings({ "unchecked" })
	public void consultaDetalleRepertorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";

		List<RepertorioPorRegistroVO> repertorioVOs = null;
		Long numCaratula = (request.getParameter("numCaratula")==null || request.getParameter("numCaratula").equals("null"))?null:Long.parseLong(request.getParameter("numCaratula"));
		Long numRepertorio = (request.getParameter("numRepertorio")==null || request.getParameter("numRepertorio").equals("null"))?null:Long.parseLong(request.getParameter("numRepertorio"));
		Integer anio = request.getParameter("anio")==null?null:Integer.parseInt(request.getParameter("anio"));

		try {

			WsRepertorioClienteDelegate cliente = new WsRepertorioClienteDelegate();

			repertorioVOs = cliente.consultaDetalleRepertorio(numCaratula,numRepertorio,anio);

			if(repertorioVOs!=null){ 
				if(repertorioVOs.size()>0){
					for(RepertorioPorRegistroVO repertorio: repertorioVOs){
						JSONObject fila = new JSONObject();
						fila.put("registro", repertorio.getRegistro());
						fila.put("repertorio", repertorio.getRepertorio());
						fila.put("caratula", repertorio.getCaratula());
						fila.put("inscripciones", repertorio.getInscripciones());
						fila.put("nombreArchivo", repertorio.getNombreArchivo());

						resultado.add(fila);
					}
				}
			}

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	@SuppressWarnings({ "unchecked" })
	public void getListadoRepertorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject respuesta = new JSONObject();
		JSONArray resultado = new JSONArray();
		Boolean status = false;
		String msg = "";

		List<ListadoRepertorioVO> repertorioVOs = null;
		String fechaDesdeStr = request.getParameter("fechaDesde")==null?"":request.getParameter("fechaDesde");
		String fechaHastaStr = request.getParameter("fechaHasta")==null?"":request.getParameter("fechaHasta");

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			WsRepertorioClienteDelegate cliente = new WsRepertorioClienteDelegate();

			repertorioVOs = cliente.obtenerListadoRepertorio(new SimpleDateFormat("yyyy-MM-dd").parse(fechaDesdeStr),new SimpleDateFormat("yyyy-MM-dd").parse(fechaHastaStr));

			if(repertorioVOs!=null){ 
				if(repertorioVOs.size()>0){
					for(ListadoRepertorioVO repertorio: repertorioVOs){
						JSONObject fila = new JSONObject();
						fila.put("repertorio", repertorio.getRepertorio()!=null?repertorio.getRepertorio():"");
						fila.put("caratula", repertorio.getCaratula());
						fila.put("comprador", repertorio.getComprador()!=null?repertorio.getComprador().trim():"");
						fila.put("vendedor", repertorio.getVendedor()!=null?repertorio.getVendedor().trim():"");
						fila.put("requirente", repertorio.getRequirente()!=null?repertorio.getRequirente().trim():"");
						fila.put("acto", repertorio.getActo()!=null?repertorio.getActo().trim():"");
						fila.put("registro", repertorio.getRegistro()!=null?repertorio.getRegistro().trim():"");
						fila.put("fechacreacion", repertorio.getFechaCreacion()!=null?sdf.format(repertorio.getFechaCreacion()):"");
						fila.put("fechaSeccion", repertorio.getFechaSeccion()!=null?sdf.format(repertorio.getFechaSeccion()):"");
//						System.out.println("fila:" + fila.toString());	
						resultado.add(fila);
					}
				}
			}

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("resultado", resultado);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	


}