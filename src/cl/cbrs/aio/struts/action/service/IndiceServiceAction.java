package cl.cbrs.aio.struts.action.service;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.keycloak.KeycloakSecurityContext;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.foliomercantil.vo.ActoJuridicoVO;
import cl.cbr.foliomercantil.vo.TipoSociedadVO;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dao.InformacionesDAO;
import cl.cbrs.aio.dto.AtencionInformacionesDTO;
import cl.cbrs.aio.dto.CamposIndiceDTO;
import cl.cbrs.aio.dto.InformacionesDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.SearchTermDTO;
import cl.cbrs.aio.dto.estado.RegistroDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.ConstantesPortalConservador;
import cl.cbrs.aio.util.IndiceServiceUtil;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;
import cl.cbrs.conservadores.indices.ServicioIndiceConservadoresDelegate;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;
import cl.cbrs.wscomercio.ws.ServiciosComercioDelegate;
import cl.cbrs.wscomercio.ws.response.ObtenerActosJuridicosResponse;
import cl.cbrs.wscomercio.ws.response.ObtenerTiposSociedadesResponse;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class IndiceServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(IndiceServiceAction.class);

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings("unchecked")
	public void obtenerActos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray actos = new JSONArray();
		Boolean status = false;
		String msg = "";
		ActoJuridicoVO[] listaActoJuridicoVOs = null;

		try{
			ServiciosComercioDelegate delegate = new ServiciosComercioDelegate();
			ObtenerActosJuridicosResponse actosResponse = delegate.obtenerActosJuridicos();

			listaActoJuridicoVOs = actosResponse.getActosJuridicoVOs();
			if(listaActoJuridicoVOs!=null){ 
				if(listaActoJuridicoVOs.length>0){
					for(int i=0;i<listaActoJuridicoVOs.length;i++){
						ActoJuridicoVO actoJuridicoVO = listaActoJuridicoVOs[i];
						JSONObject fila = new JSONObject();
						fila.put("idActo", actoJuridicoVO.getIdActo());
						fila.put("nombre", stripAccents(actoJuridicoVO.getNombre()));
						actos.add(fila);
					}
				}	
			}	

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("actos", actos);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private static String stripAccents(String s) 
	{
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	@SuppressWarnings("unchecked")
	public void obtenerTipos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray tipos = new JSONArray();
		Boolean status = false;
		String msg = "";
		TipoSociedadVO[] listaTipoSociedadVOs = null;

		try{
			ServiciosComercioDelegate delegate = new ServiciosComercioDelegate();
			ObtenerTiposSociedadesResponse tiposResponse = delegate.obtenerTiposSociedades();

			listaTipoSociedadVOs = tiposResponse.getTiposSociedadesVOs();
			if(listaTipoSociedadVOs!=null){ 
				if(listaTipoSociedadVOs.length>0){
					for(int i=0;i<listaTipoSociedadVOs.length;i++){
						TipoSociedadVO tipoSociedadVO = listaTipoSociedadVOs[i];
						JSONObject fila = new JSONObject();
						fila.put("idTipo", tipoSociedadVO.getIdTipoSociedad());
						fila.put("nombre", tipoSociedadVO.getNombre());
						tipos.add(fila);
					}
				}	
			}	

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("tipos", tipos);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCBRSDisponibles(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		net.sf.json.JSONArray conservadores = new net.sf.json.JSONArray();
		Boolean status = false;
		String msg = "";

		try{
//			ServicioIndiceConservadoresDelegate delegate= new ServicioIndiceConservadoresDelegate();
//			conservadores = delegate.obtenerCBRSDisponibles();

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("conservadores", conservadores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getIndicePropiedades(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String nombreUsuario = (String) request.getSession().getAttribute("nombreUsuario");

		JSONObject respuesta = new JSONObject();

		if(!"null".equals(nombreUsuario)){

			String fojaC = request.getParameter("foja");
			String numeroC = request.getParameter("numero");
			String anoC = request.getParameter("ano");	
			String bis = request.getParameter("bis");	
			String rut = request.getParameter("rut"); 
			String nombre = "";
			try {
				if(request.getParameter("apellidos")!=null){
					nombre = new String(request.getParameter("apellidos").getBytes("ISO-8859-15"),"UTF-8");
					nombre = nombre.toLowerCase();
				}
			} catch (UnsupportedEncodingException e3) {
				nombre= "";
			}
			
			String direccion = "";
			try {
				if(request.getParameter("direccion")!=null){
					direccion = new String(request.getParameter("direccion").getBytes("ISO-8859-15"),"UTF-8");
					direccion = direccion.toLowerCase();
				}
			} catch (UnsupportedEncodingException e3) {
				direccion= "";
			}

			String comunaS = request.getParameter("comuna");  
			String anoS = request.getParameter("anoInscripcion");
			String acto = request.getParameter("acto");
			String tipo = request.getParameter("tipo");
			String exacta = request.getParameter("exacta")==null?"false":request.getParameter("exacta");
			String regPropiedades = request.getParameter("regPropiedades")==null?"false":request.getParameter("regPropiedades");
			String regHipoteca = request.getParameter("regHipoteca")==null?"false":request.getParameter("regHipoteca");
			String regProhibiciones = request.getParameter("regProhibiciones")==null?"false":request.getParameter("regProhibiciones");
			String regComercio = request.getParameter("regComercio")==null?"false":request.getParameter("regComercio");
			String buscarPorInscricionHipo = request.getParameter("buscarPorInscricionHipo")==null?"false":request.getParameter("buscarPorInscricionHipo");
			String buscarPorInscricionProh = request.getParameter("buscarPorInscricionProh")==null?"false":request.getParameter("buscarPorInscricionProh");
			String conservador = request.getParameter("conservador");

			JSONArray aaData = new JSONArray();
			JSONArray aaDataProhibiciones = new JSONArray();
			JSONArray aaDataHipotecas = new JSONArray();
			JSONArray aaDataComercio = new JSONArray();
			//		List<SolrDocument> aaData = null;
			respuesta.put("status", false);
			String msg = "";
			Boolean hayUser = false;

			Integer ano = null;
			try {
				ano = Integer.parseInt(anoS);
			} catch (Exception e2) {
			}

			Integer comuna = null;
			try {
				comuna = Integer.parseInt(comunaS);
			} catch (Exception e2) {
			}	

			Integer foja = null;
			try {
				foja = Integer.parseInt(fojaC);
			} catch (Exception e2) {
			}

			Integer numero = null;
			try {
				numero = Integer.parseInt(numeroC);
			} catch (Exception e2) {
			}

			Integer anoCitado = null;
			try {
				anoCitado = Integer.parseInt(anoC);
			} catch (Exception e2) {
			}

			Boolean isOK = false;


			//		if(StringUtils.isNotBlank(extraFeatures) && "OK".equals(extraFeatures)){
			if(ano==null && comuna==null && StringUtils.isBlank(nombre) 
					&& StringUtils.isBlank(rut) && StringUtils.isBlank(direccion)
					//					&& (foja==null || numero==null || anoCitado==null)){
					&& (foja==null && numero==null && anoCitado==null)){
				respuesta.put("msg", "Se requiere al menos un campo.");	
			}else{
				isOK = true;
			}
			//
			//		}else{
			//			if(ano==null || comuna==null || "".equals(nombre) || nombre.trim().length()==0){
			//				respuesta.put("msg", "Todos los campos son obligatorios.");	
			//			}else{
			//				isOK = true;
			//			}
			//		}

			if(isOK){
				String q = "";

				String SOLR_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRPROP", "endpoint");			
				String SOLR_HIPO_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRHIPO", "endpoint");
				String SOLR_PROH_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRPROH", "endpoint");
				String SOLR_COM_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRCOM", "endpoint");

				Integer SOLR_ROWS = 50;
				try {
					SOLR_ROWS = Integer.parseInt(ConstantesPortalConservador.getParametro("SOLR", "rows"));
				} catch (Exception e1) {
					log.error(e1);
				}

				//WsUsuarioWebDelegate usuarioWebDelegate = new WsUsuarioWebDelegate();

				try {

					//ValidaCantidadConsultaVO cantidadConsultaVO = usuarioWebDelegate.validaCantidadConsulta(idUsuario, ipRequest, 1);

					//if(cantidadConsultaVO!=null && !cantidadConsultaVO.isCumpleLimite()){
					if("false".equals(regPropiedades) && "false".equals(regHipoteca) && "false".equals(regProhibiciones) && "false".equals(regComercio)){
						respuesta.put("msg", "Seleccione a lo menos un Registro");
					} else {

						if("true".equals(regPropiedades)){
							if("false".equals(buscarPorInscricionHipo) && "false".equals(buscarPorInscricionProh)){

								if(null!=conservador){
									aaData = consultaOtroCBRS(nombre, conservador, "Propiedad");
									respuesta.put("status", true);
								}else{

									String url = SOLR_ENDPOINT;
//									System.out.println("url: "+url);
									SolrServer server = new HttpSolrServer( url );
									
									boolean ocupaQueryMejora = false;
									String texto="";
									
									if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
										q = getQueryPropiedades(nombre, comuna, ano, rut, direccion, foja, numero, anoCitado,exacta);
									}else{
										if(!StringUtils.isBlank(nombre))
											texto=texto+nombre;
										if(!StringUtils.isBlank(direccion)){
											if(texto.length()>0)
												texto=texto+" "+direccion;
											else
												texto=texto+direccion;
										}
										
										ArrayList<SearchTermDTO> searchTerms = getSearchTerms(texto,url);
										
										q = getComTexto(texto,searchTerms);
										ocupaQueryMejora=true;
									}

									System.out.println("query solr: "+q);

									SolrQuery query = new SolrQuery();
									
//									query.setQuery(q);
//									query.setRows(SOLR_ROWS);
									
									//mejora
									String highlightFieldSolr = "nombre, direccion";
									
									List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
									CamposIndiceDTO campoNombre = new CamposIndiceDTO();
									campoNombre.setNombreCampo("nombre");
									ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
									campoNombre.setSearchTermDTOs(searchTerms);
									camposIndice.add(campoNombre);
									CamposIndiceDTO campoDireccion = new CamposIndiceDTO();
									campoDireccion.setNombreCampo("direccion");
									searchTerms = getSearchTerms(direccion,url);
									campoDireccion.setSearchTermDTOs(searchTerms);
									camposIndice.add(campoDireccion);
									
									query.set("q", q);
									
									if((!StringUtils.isBlank(nombre) || !StringUtils.isBlank(direccion)) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
										query.set("defType", "edismax");
										if(!StringUtils.isBlank(nombre) && !StringUtils.isBlank(direccion))
											query.set("qf", "nombre direccion");
										else if(!StringUtils.isBlank(nombre))
											query.set("qf", "nombre");
										else if(!StringUtils.isBlank(direccion))
											query.set("qf", "direccion");
										
										query.set("mm", "100%");
										query.setHighlight(true);
										query.setHighlightSimplePre("<span class=\"highlighted\">");
										query.setHighlightSimplePost("</span>");
										query.addHighlightField(highlightFieldSolr);
										
										ocupaQueryMejora=true;
									}
									
									query.setRows(SOLR_ROWS);
									//fin mejora

									try {
										QueryResponse queryResponse = server.query( query );

										NamedList<Object> responseHeader = queryResponse.getResponseHeader();

										Integer status = (Integer) responseHeader.get("status");

										if(status!=null && status==0){	
											respuesta.put("status", true);
											if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
												IndiceServiceUtil iu = new IndiceServiceUtil();

												aaData = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"prop",camposIndice,ocupaQueryMejora);

											}
										}else{
											respuesta.put("msg", "Error invocando servicio");	
										}

									} catch (SolrServerException e) {
										log.error(e);
										respuesta.put("msg", "Problema invocando servicio índice");	
									}
								}
							}
						}

						if("true".equals(regProhibiciones)){
							if("false".equals(buscarPorInscricionHipo)){
								List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
								
								if(null!=conservador){
									aaDataProhibiciones = consultaOtroCBRS(nombre, conservador, "Prohibiciones");
									respuesta.put("status", true);
								}else{
									String url = SOLR_PROH_ENDPOINT;
									SolrServer server = new HttpSolrServer( url );
									
									boolean ocupaQueryMejora = false;
									String texto="";
									
									if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
										q = getQueryProhibiciones(nombre, rut, foja, numero, anoCitado, buscarPorInscricionProh,exacta);
									}else{
										if(!StringUtils.isBlank(nombre))
											texto=texto+nombre;
										
										CamposIndiceDTO campoNombre = new CamposIndiceDTO();
										campoNombre.setNombreCampo("nombre");
										ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
										campoNombre.setSearchTermDTOs(searchTerms);
										camposIndice.add(campoNombre);
										
										q = getComTexto(texto,searchTerms);
										ocupaQueryMejora=true;
									}

//									System.out.println("query solr Proh: "+q);

									SolrQuery query = new SolrQuery();
//									query.setQuery(q);
//									query.setRows(SOLR_ROWS);
									
									//mejora
									String highlightFieldSolr = "nombre";
									
									query.set("q", q);
									
									if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
										query.set("defType", "edismax");
										query.set("qf", "nombre");
										query.set("mm", "100%");
										query.setHighlight(true);
										query.setHighlightSimplePre("<span class=\"highlighted\">");
										query.setHighlightSimplePost("</span>");
										query.addHighlightField(highlightFieldSolr);
										
										ocupaQueryMejora=true;
									}
									
									query.setRows(SOLR_ROWS);
									//fin mejora

									try {
										QueryResponse queryResponse = server.query( query );

										NamedList<Object> responseHeader = queryResponse.getResponseHeader();

										Integer status = (Integer) responseHeader.get("status");

										if(status!=null && status==0){	
											respuesta.put("status", true);
											if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
												IndiceServiceUtil iu = new IndiceServiceUtil();

												aaDataProhibiciones = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"proh",camposIndice,ocupaQueryMejora);

											}
										}else{
											respuesta.put("msg", "Error invocando servicio");	
										}

									} catch (SolrServerException e) {
										log.error(e);
										respuesta.put("msg", "Problema invocando servicio índice");	
									}
								}
							}
						}

						if("true".equals(regHipoteca)){
							if("false".equals(buscarPorInscricionProh)){

								List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
								
								if(null!=conservador){
									aaDataHipotecas = consultaOtroCBRS(nombre, conservador, "Hipoteca");
									respuesta.put("status", true);
								}else{
									String url = SOLR_HIPO_ENDPOINT;
									SolrServer server = new HttpSolrServer( url );
									
									boolean ocupaQueryMejora = false;
									String texto="";
									
									if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
										q = getQueryHipoteca(nombre, rut, foja, numero, anoCitado, buscarPorInscricionHipo,exacta);
									}else{
										if(!StringUtils.isBlank(nombre))
											texto=texto+nombre;
											
										
										CamposIndiceDTO campoNombre = new CamposIndiceDTO();
										campoNombre.setNombreCampo("nombre");
										ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
										campoNombre.setSearchTermDTOs(searchTerms);
										camposIndice.add(campoNombre);
										
										q = getComTexto(texto,searchTerms);
										ocupaQueryMejora=true;
									}

//									System.out.println("query solr Hipo: "+q);

									SolrQuery query = new SolrQuery();
//									query.setQuery(q);
//									query.setRows(SOLR_ROWS);
									
									//mejora
									String highlightFieldSolr = "nombre";

									query.set("q", q);
									
									if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
										query.set("defType", "edismax");
										query.set("qf", "nombre");
										query.set("mm", "100%");
										query.setHighlight(true);
										query.setHighlightSimplePre("<span class=\"highlighted\">");
										query.setHighlightSimplePost("</span>");
										query.addHighlightField(highlightFieldSolr);
										
										ocupaQueryMejora=true;
									}
									
									query.setRows(SOLR_ROWS);
									//fin mejora

									try {
										QueryResponse queryResponse = server.query( query );

										NamedList<Object> responseHeader = queryResponse.getResponseHeader();

										Integer status = (Integer) responseHeader.get("status");

										if(status!=null && status==0){	
											respuesta.put("status", true);
											if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
												IndiceServiceUtil iu = new IndiceServiceUtil();

												aaDataHipotecas = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"hip",camposIndice,ocupaQueryMejora);

											}
										}else{
											respuesta.put("msg", "Error invocando servicio");	
										}

									} catch (SolrServerException e) {
										log.error(e);
										respuesta.put("msg", "Problema invocando servicio índice");	
									}
								}
							}
						}

						if("true".equals(regComercio)){
							if("false".equals(buscarPorInscricionHipo) && "false".equals(buscarPorInscricionProh)){

								List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
								
								if(null!=conservador){
									aaDataComercio = consultaOtroCBRS(nombre, conservador, "Comercio");
									respuesta.put("status", true);
								}else{
									String url = SOLR_COM_ENDPOINT;
//									System.out.println("url: "+url);
									SolrServer server = new HttpSolrServer( url );
									boolean ocupaQueryMejora = false;
									
									if(foja!=null || numero!=null || anoCitado!=null || acto!=null || tipo!=null){
										q = getQueryComercio(nombre, foja, numero, anoCitado, acto, tipo,exacta);
									}else{
										CamposIndiceDTO campoNombre = new CamposIndiceDTO();
										campoNombre.setNombreCampo("nombre");
										ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
										campoNombre.setSearchTermDTOs(searchTerms);
										camposIndice.add(campoNombre);
										
										q = getComTexto(nombre,searchTerms);
										ocupaQueryMejora=true;
									}

//									System.out.println("query solr Com: "+q);

									SolrQuery query = new SolrQuery();
									
//									query.setQuery(q);
//									query.setRows(SOLR_ROWS);
									
									//mejora
									String highlightFieldSolr = "nombreSociedad, personas";
									
									
									
									query.set("q", q);
									
									if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || anoCitado==null || acto==null || tipo==null)){
										query.set("defType", "edismax");
										query.set("qf", "nombreSociedad personas");
										query.set("mm", "100%");
										query.setHighlight(true);
										query.setHighlightSimplePre("<span class=\"highlighted\">");
										query.setHighlightSimplePost("</span>");
										query.addHighlightField(highlightFieldSolr);
										
										ocupaQueryMejora=true;
									}
									
									query.setRows(SOLR_ROWS);
									//fin mejora
									
									try {
										QueryResponse queryResponse = server.query( query );

										NamedList<Object> responseHeader = queryResponse.getResponseHeader();

										Integer status = (Integer) responseHeader.get("status");

										if(status!=null && status==0){	
											respuesta.put("status", true);
											if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
												IndiceServiceUtil iu = new IndiceServiceUtil();

												aaDataComercio = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"com", camposIndice,ocupaQueryMejora);

											}
										}else{
											respuesta.put("msg", "Error invocando servicio");	
										}

									} catch (SolrServerException e) {
										log.error(e);
										respuesta.put("msg", "Problema invocando servicio índice");	
									}
								}
							}
						}
					}
					//					}else{
					//						String msg = cantidadConsultaVO==null?"No hay respuesta de servicio, intente más tarde":cantidadConsultaVO.getDescripcion();
					//						respuesta.put("msg", msg);	
					//					}			
				} catch (Exception e1) {
					log.error(e1);
					respuesta.put("msg", "Problema invocando servicio");
				}
			}

			respuesta.put("aaData", aaData);
			respuesta.put("aaDataProhibiciones", aaDataProhibiciones);
			respuesta.put("aaDataHipotecas", aaDataHipotecas);
			respuesta.put("aaDataComercio", aaDataComercio);

		} else {
			respuesta.put("msg", "Ha finalizado su sesion, favor vuelva a ingresar");
		}

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public String getQueryPropiedades(String nombre, Integer comuna, Integer ano, String rut, String direccion, Integer foja, Integer numero, Integer anoCitado, String exacta) {
		String q = "";

		if(StringUtils.isNotBlank(nombre)){
			//				 if (nombre.startsWith("\"")){
			//                 	q = "nombre:"+nombre;
			//                 }else{
			//					nombre = nombre.replace("\"", "");
			//					nombre = nombre.trim();
			//                  q = "nombre:("+nombre+")";
			//                 }
			if ("true".equals(exacta)){
				q = "nombre:\""+nombre+"\"";
			}else{
				nombre = nombre.replace("\"", "");
				nombre = nombre.trim();
				StringTokenizer tokens=new StringTokenizer(nombre);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}

				q = "nombre:("+nombreParaBusqueda+")";
			}
		}

		if(comuna!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "comuna:"+comuna;
		}

		if(ano!=null){
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "ano:"+ano;
		}

		if(StringUtils.isNotBlank(rut)){

			String rutf = rut.replaceAll("\\.", "");
			rutf = rutf.replaceAll("-", "");

			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "rut:"+rutf;
		}

		if(StringUtils.isNotBlank(direccion)){						
			if(q.length()>0){
				q = q + " AND ";	
			}

			direccion = direccion.replace("\"", "");
			direccion = direccion.trim();
			
			StringTokenizer tokens=new StringTokenizer(direccion);
			String dirParaBusqueda = new String();
			while(tokens.hasMoreTokens()) {
				dirParaBusqueda=dirParaBusqueda+" +"+tokens.nextToken();				
			}

			q = q + "direccion:("+dirParaBusqueda+")";
		}

		if(foja!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "foja:"+foja;
		}

		if(numero!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "numero:"+numero;
		}

		if(anoCitado!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "ano:"+anoCitado;
		}

		//			if(foja!=null && anoCitado!=null && numero!=null){
		//				if(q.length()>0){
		//					q = q + " AND ";	
		//				}
		//
		//				q = q + "foja:\""+foja+"\" AND numero:"+numero+" AND ano:"+anoCitado;
		//			}

		return q;
	}

	public String getQueryHipoteca(String nombre, String rut, Integer foja, Integer numero, Integer anoCitado, String buscarPorInscricionHipo, String exacta) {
		String q = "";

		if(StringUtils.isNotBlank(nombre)){
			if ("true".equals(exacta)){
				q = "nombre:\""+nombre+"\"";
			}else{
				nombre = nombre.replace("\"", "");
				nombre = nombre.trim();
				StringTokenizer tokens=new StringTokenizer(nombre);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}
				q = "nombre:("+nombreParaBusqueda+")";
			}
		}

		if(StringUtils.isNotBlank(rut)){

			String rutf = rut.replaceAll("\\.", "");
			rutf = rutf.replaceAll("-", "");

			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "rut:"+rutf;
		}

		//			if(foja!=null && anoCitado!=null && numero!=null){
		if("false".equals(buscarPorInscricionHipo)){
			if(foja!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "foja_pr:"+foja;
			}

			if(numero!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "numero_pr:"+numero;
			}

			if(anoCitado!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "ano_pr:"+anoCitado;
			}
			//					if(q.length()>0){
			//						q = q + " AND ";	
			//					}
			//	
			//					q = q + "foja_pr:\""+foja+"\" AND numero_pr:"+numero+" AND ano_pr:"+anoCitado;
		} else {
			if(foja!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "foja_hi:"+foja;
			}

			if(numero!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "numero_hi:"+numero;
			}

			if(anoCitado!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "ano_hi:"+anoCitado;
			}
			//					if(q.length()>0){
			//						q = q + " AND ";	
			//					}
			//	
			//					q = q + "foja_hi:\""+foja+"\" AND numero_hi:"+numero+" AND ano_hi:"+anoCitado;
		}
		//			}

		return q;
	}

	public String getQueryProhibiciones(String nombre, String rut, Integer foja, Integer numero, Integer anoCitado, String buscarPorInscricionProh, String exacta) {
		String q = "";

		if(StringUtils.isNotBlank(nombre)){
			if ("true".equals(exacta)){
				q = "nombre:\""+nombre+"\"";
			}else{
				nombre = nombre.replace("\"", "");
				nombre = nombre.trim();
				StringTokenizer tokens=new StringTokenizer(nombre);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}
				q = "nombre:("+nombreParaBusqueda+")";
			}
		}

		if(StringUtils.isNotBlank(rut)){

			String rutf = rut.replaceAll("\\.", "");
			rutf = rutf.replaceAll("-", "");

			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "rut:"+rutf;
		}

		//			if(foja!=null && anoCitado!=null && numero!=null){
		if("false".equals(buscarPorInscricionProh)){
			if(foja!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "foja_pr:"+foja;
			}

			if(numero!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "numero_pr:"+numero;
			}

			if(anoCitado!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "ano_pr:"+anoCitado;
			}
			//					if(q.length()>0){
			//						q = q + " AND ";	
			//					}
			//	
			//					q = q + "foja_pr:\""+foja+"\" AND numero_pr:"+numero+" AND ano_pr:"+anoCitado;
		} else {
			if(foja!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "foja_ph:"+foja;
			}

			if(numero!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "numero_ph:"+numero;
			}

			if(anoCitado!=null){										
				if(q.length()>0){
					q = q + " AND ";	
				}

				q = q + "ano_ph:"+anoCitado;
			}
			//					if(q.length()>0){
			//						q = q + " AND ";	
			//					}
			//	
			//					q = q + "foja_ph:\""+foja+"\" AND numero_ph:"+numero+" AND ano_ph:"+anoCitado;
		}
		//			}

		return q;
	}

	public String getQueryComercio(String nombre, Integer foja, Integer numero, Integer anoCitado, String acto, String tipo, String exacta) {
		String q = "";

		if(StringUtils.isNotBlank(nombre)){
			if ("true".equals(exacta)){
				q = "(nombreSociedad:\""+nombre+"\" OR personas:\""+nombre+"\")";
			}else{
				nombre = nombre.replace("\"", "");
				nombre = nombre.trim();
				StringTokenizer tokens=new StringTokenizer(nombre);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}
				q = "(nombreSociedad:("+nombreParaBusqueda+") OR personas: ("+nombreParaBusqueda+"))";
			}
			nombre = nombre.replace("\"", "");
			nombre = nombre.trim();

		}

		if(StringUtils.isNotBlank(acto)){
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "acto:("+acto+")";
		}

		if(StringUtils.isNotBlank(tipo)){
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "tipo:("+tipo+")";
		}


		if(foja!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "foja:"+foja;
		}

		if(numero!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "numero:"+numero;
		}

		if(anoCitado!=null){										
			if(q.length()>0){
				q = q + " AND ";	
			}

			q = q + "ano:"+anoCitado;
		}

		return q;
	}
	
	public String getComTexto(String nombre, ArrayList<SearchTermDTO> searchTermDTOs) {
		String q = "";
		
		if(StringUtils.isNotBlank(nombre)){
			
			String[] total = nombre.split(" ");
			
			if(total==null){
				q = "+"+nombre;
			}else if(total!=null && total.length>=1){
				
				for(SearchTermDTO dto: searchTermDTOs){

					if(dto.getAbierto() || dto.getTermino().length()==1){
						q = q + " +"+dto.getTermino()+"*";
					}else{
						q = q + " +"+dto.getTermino();
					}
				}
				
			}
		}
		
//		System.out.println(q);

		return q;
	}
	
	public ArrayList<SearchTermDTO> getSearchTerms(String nombre, String regSolr) {
		ArrayList<SearchTermDTO> terminos = new ArrayList<SearchTermDTO>();

		if(StringUtils.isNotBlank(nombre)){

			String[] total = nombre.split(" ");

			if(total!=null && total.length>=1){

				String synonyms = consultaSinonimosSolr(regSolr);

				for(String term: total){

					SearchTermDTO dto = new SearchTermDTO();
					
					if(StringUtils.isNotBlank(term)){

						dto.setTermino(term);
						dto.setAbierto(true);

						try {
							Pattern regex = Pattern.compile("(?i)"+term+"\\b");
							Matcher regexMatcher = regex.matcher(synonyms);
							
							if(regexMatcher.find()){
								dto.setAbierto(false);
							}
							
						} catch (PatternSyntaxException ex) {
							// Syntax error in the regular expression
						}

						terminos.add(dto);					
					}

				}

			}

			//q = nombre.replaceAll(" ", "+");
		}

		return terminos;
	}

	public JSONArray consultaOtroCBRS(String nombre, String conservador, String registro) throws Exception {

		JSONArray resultadoConvert = new JSONArray();
		ServicioIndiceConservadoresDelegate delegate= new ServicioIndiceConservadoresDelegate();
		net.sf.json.JSONArray resultados = new net.sf.json.JSONArray();

		Date date = new Date(); // your date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String year = cal.get(Calendar.YEAR)+"";

		resultados = delegate.obtenerDetalleIndice(nombre, registro, "1980", year, "0", "50", conservador);
		for(int i=0 ; i < resultados.size(); i++) {
//			System.out.println("jarray [" + i + "] --------" + resultados.getString(i));
			JSONObject json = (JSONObject) new JSONParser().parse(resultados.getString(i));
			resultadoConvert.add(json);
		}

		return resultadoConvert;		
	}

	@SuppressWarnings("unchecked")
	public void guardaensesion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		String foja = request.getParameter("foja");
		String numero = request.getParameter("num");
		String ano = request.getParameter("ano");
		Boolean bis = false;

		if(null!=request.getParameter("bis"))
			bis = request.getParameter("bis").equals("0")?false:true;

		String registro = request.getParameter("registro");
		String idregistro = request.getParameter("idregistro");

		InscripcionDigitalDTO inscripcionDigitalDTO = new InscripcionDigitalDTO();
		inscripcionDigitalDTO.setFoja(Long.parseLong(foja));
		inscripcionDigitalDTO.setNumero(numero);
		inscripcionDigitalDTO.setAno(Long.parseLong(ano));
		inscripcionDigitalDTO.setBis(bis);
		RegistroDTO registroDTO = new RegistroDTO();
		registroDTO.setId(Integer.parseInt(idregistro));
		registroDTO.setDescripcion(registro);
		inscripcionDigitalDTO.setRegistroDTO(registroDTO);

		request.getSession().setAttribute("inscripcion", inscripcionDigitalDTO);

		status = true;

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void exportarExcel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String fojaC = request.getParameter("foja");
		String numeroC = request.getParameter("numero");
		String anoC = request.getParameter("ano");	
		String bis = request.getParameter("bis");	
		String rut = request.getParameter("rut"); 
		String nombre = "";
		try {
			if(request.getParameter("apellidos")!=null){
				nombre = new String(request.getParameter("apellidos").getBytes("ISO-8859-15"),"UTF-8");
				nombre = nombre.toLowerCase();
			}
		} catch (UnsupportedEncodingException e3) {
			nombre= "";
		}
		
		String direccion = "";
		try {
			if(request.getParameter("direccion")!=null){
				direccion = new String(request.getParameter("direccion").getBytes("ISO-8859-15"),"UTF-8");
				direccion = direccion.toLowerCase();
			}
		} catch (UnsupportedEncodingException e3) {
			direccion= "";
		}
		String comunaS = request.getParameter("comuna");  
		String anoS = request.getParameter("anoInscripcion");
		String acto = request.getParameter("acto");
		String tipo = request.getParameter("tipo");
		String exacta = request.getParameter("exacta")==null?"false":request.getParameter("exacta");
		String regPropiedades = request.getParameter("regPropiedades")==null?"false":request.getParameter("regPropiedades");
		String regHipoteca = request.getParameter("regHipoteca")==null?"false":request.getParameter("regHipoteca");
		String regProhibiciones = request.getParameter("regProhibiciones")==null?"false":request.getParameter("regProhibiciones");
		String regComercio = request.getParameter("regComercio")==null?"false":request.getParameter("regComercio");
		String buscarPorInscricionHipo = request.getParameter("buscarPorInscricionHipo")==null?"false":request.getParameter("buscarPorInscricionHipo");
		String buscarPorInscricionProh = request.getParameter("buscarPorInscricionProh")==null?"false":request.getParameter("buscarPorInscricionProh");
		String conservador = request.getParameter("conservador");

		nombre = nombre.equalsIgnoreCase("null")?null:nombre;
		rut = rut.equalsIgnoreCase("null")?null:rut;
		direccion = direccion.equalsIgnoreCase("null")?null:direccion;
		fojaC = fojaC.equalsIgnoreCase("null")?null:fojaC;
		numeroC = numeroC.equalsIgnoreCase("null")?null:numeroC;
		anoC = anoC.equalsIgnoreCase("null")?null:anoC;
		tipo = tipo.equalsIgnoreCase("undefined")?null:tipo;
		acto = acto.equalsIgnoreCase("undefined")?null:acto;

		JSONObject respuesta = new JSONObject();
		JSONArray aaData = new JSONArray();
		JSONArray aaDataProhibiciones = new JSONArray();
		JSONArray aaDataHipotecas = new JSONArray();
		JSONArray aaDataComercio = new JSONArray();

		respuesta.put("status", false);
		String msg = "";
		Boolean hayUser = false;

		Integer ano = null;
		try {
			ano = Integer.parseInt(anoS);
		} catch (Exception e2) {
		}

		Integer comuna = null;
		try {
			comuna = Integer.parseInt(comunaS);
		} catch (Exception e2) {
		}	

		Integer foja = null;
		try {
			foja = Integer.parseInt(fojaC);
		} catch (Exception e2) {
		}

		Integer numero = null;
		try {
			numero = Integer.parseInt(numeroC);
		} catch (Exception e2) {
		}

		Integer anoCitado = null;
		try {
			anoCitado = Integer.parseInt(anoC);
		} catch (Exception e2) {
		}

		Boolean isOK = false;



		if(ano==null && comuna==null && StringUtils.isBlank(nombre) 
				&& StringUtils.isBlank(rut) && StringUtils.isBlank(direccion)

				&& (foja==null && numero==null && anoCitado==null)){
			respuesta.put("msg", "Se requiere al menos un campo.");	
		}else{
			isOK = true;
		}

		if(isOK){
			String q = "";

			String SOLR_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRPROP", "endpoint");			
			String SOLR_HIPO_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRHIPO", "endpoint");
			String SOLR_PROH_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRPROH", "endpoint");
			String SOLR_COM_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRCOM", "endpoint");

			Integer SOLR_ROWS = 100000;
			//			try {
			//				SOLR_ROWS = Integer.parseInt(ConstantesPortalConservador.getParametro("SOLR", "rows"));
			//			} catch (Exception e1) {
			//				log.error(e1);
			//			}

			try {

				if("false".equals(regPropiedades) && "false".equals(regHipoteca) && "false".equals(regProhibiciones) && "false".equals(regComercio)){
					respuesta.put("msg", "Seleccione a lo menos un Registro");
				} else {

					if("true".equals(regPropiedades)){
						if("false".equals(buscarPorInscricionHipo) && "false".equals(buscarPorInscricionProh)){

							//							if(null!=conservador){
							//								aaData = consultaOtroCBRS(nombre, conservador, "Propiedad");
							//								respuesta.put("status", true);
							//							}else{

							String url = SOLR_ENDPOINT;
//							System.out.println("url: "+url);
							SolrServer server = new HttpSolrServer( url );

							boolean ocupaQueryMejora = false;
							String texto="";
							
							if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
								q = getQueryPropiedades(nombre, comuna, ano, rut, direccion, foja, numero, anoCitado,exacta);
							}else{
								if(!StringUtils.isBlank(nombre))
									texto=texto+nombre;
								if(!StringUtils.isBlank(direccion)){
									if(texto.length()>0)
										texto=texto+" "+direccion;
									else
										texto=texto+direccion;
								}
								
								ArrayList<SearchTermDTO> searchTerms = getSearchTerms(texto,url);
									
								q = getComTexto(texto,searchTerms);
								ocupaQueryMejora=true;
							}

							System.out.println("query solr: "+q);

							SolrQuery query = new SolrQuery();

//							query.setQuery(q);
//							query.setRows(SOLR_ROWS);
							
							//mejora
							String highlightFieldSolr = "nombre, direccion";
							
							List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
							CamposIndiceDTO campoNombre = new CamposIndiceDTO();
							campoNombre.setNombreCampo("nombre");
							ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
							campoNombre.setSearchTermDTOs(searchTerms);
							camposIndice.add(campoNombre);
							CamposIndiceDTO campoDireccion = new CamposIndiceDTO();
							campoDireccion.setNombreCampo("direccion");
							searchTerms = getSearchTerms(direccion,url);
							campoDireccion.setSearchTermDTOs(searchTerms);
							camposIndice.add(campoDireccion);
							
							query.set("q", q);
							
							if((!StringUtils.isBlank(nombre) || !StringUtils.isBlank(direccion)) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
								query.set("defType", "edismax");
								query.set("qf", "nombre direccion");
								query.set("mm", "100%");
								query.setHighlight(true);
								query.setHighlightSimplePre("<span class=\"highlighted\">");
								query.setHighlightSimplePost("</span>");
								query.addHighlightField(highlightFieldSolr);
								
								ocupaQueryMejora=true;
							}
							
							query.setRows(SOLR_ROWS);
							//fin mejora

							try {
								QueryResponse queryResponse = server.query( query );

								NamedList<Object> responseHeader = queryResponse.getResponseHeader();

								Integer status = (Integer) responseHeader.get("status");

								if(status!=null && status==0){	
									respuesta.put("status", true);
									if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
										IndiceServiceUtil iu = new IndiceServiceUtil();

										aaData = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"prop",camposIndice,ocupaQueryMejora);

									}
								}else{
									respuesta.put("msg", "Error invocando servicio");	
								}

							} catch (SolrServerException e) {
								log.error(e);
								respuesta.put("msg", "Problema invocando servicio índice");	
							}
							//							}
						}
					}

					if("true".equals(regProhibiciones)){
						if("false".equals(buscarPorInscricionHipo)){

							List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
							
							String url = SOLR_PROH_ENDPOINT;
							SolrServer server = new HttpSolrServer( url );

							boolean ocupaQueryMejora = false;
							String texto="";
							
							if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
								q = getQueryProhibiciones(nombre, rut, foja, numero, anoCitado, buscarPorInscricionProh,exacta);
							}else{
								if(!StringUtils.isBlank(nombre))
									texto=texto+nombre;
								
								CamposIndiceDTO campoNombre = new CamposIndiceDTO();
								campoNombre.setNombreCampo("nombre");
								ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
								campoNombre.setSearchTermDTOs(searchTerms);
								camposIndice.add(campoNombre);
									
								q = getComTexto(texto,searchTerms);
								ocupaQueryMejora=true;
							}

//							System.out.println("query solr Proh: "+q);

							SolrQuery query = new SolrQuery();
//							query.setQuery(q);
//							query.setRows(SOLR_ROWS);
							
							//mejora
							String highlightFieldSolr = "nombre";

							query.set("q", q);
							
							if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
								query.set("defType", "edismax");
								query.set("qf", "nombre");
								query.set("mm", "100%");
								query.setHighlight(true);
								query.setHighlightSimplePre("<span class=\"highlighted\">");
								query.setHighlightSimplePost("</span>");
								query.addHighlightField(highlightFieldSolr);
								
								ocupaQueryMejora=true;
							}
							
							query.setRows(SOLR_ROWS);
							//fin mejora

							try {
								QueryResponse queryResponse = server.query( query );

								NamedList<Object> responseHeader = queryResponse.getResponseHeader();

								Integer status = (Integer) responseHeader.get("status");

								if(status!=null && status==0){	
									respuesta.put("status", true);
									if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
										IndiceServiceUtil iu = new IndiceServiceUtil();

										aaDataProhibiciones = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"proh",camposIndice,ocupaQueryMejora);

									}
								}else{
									respuesta.put("msg", "Error invocando servicio");	
								}

							} catch (SolrServerException e) {
								log.error(e);
								respuesta.put("msg", "Problema invocando servicio índice");	
							}
							//							}
						}
					}

					if("true".equals(regHipoteca)){
						if("false".equals(buscarPorInscricionProh)){

							List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
							
							String url = SOLR_HIPO_ENDPOINT;
							SolrServer server = new HttpSolrServer( url );

							boolean ocupaQueryMejora = false;
							String texto="";
							
							if(foja!=null || numero!=null || anoCitado!=null || comuna!=null || ano!=null || rut!=null){
								q = getQueryHipoteca(nombre, rut, foja, numero, anoCitado, buscarPorInscricionHipo,exacta);
							}else{
								if(!StringUtils.isBlank(nombre))
									texto=texto+nombre;

								CamposIndiceDTO campoNombre = new CamposIndiceDTO();
								campoNombre.setNombreCampo("nombre");
								ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
								campoNombre.setSearchTermDTOs(searchTerms);
								camposIndice.add(campoNombre);
								
									
								q = getComTexto(texto,searchTerms);
								ocupaQueryMejora=true;
							}

//							System.out.println("query solr Hipo: "+q);

							SolrQuery query = new SolrQuery();
//							query.setQuery(q);
//							query.setRows(SOLR_ROWS);
							
							//mejora
							String highlightFieldSolr = "nombre";
							
							query.set("q", q);
							
							if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || comuna==null || anoCitado==null || ano==null || rut==null)){
								query.set("defType", "edismax");
								query.set("qf", "nombre");
								query.set("mm", "100%");
								query.setHighlight(true);
								query.setHighlightSimplePre("<span class=\"highlighted\">");
								query.setHighlightSimplePost("</span>");
								query.addHighlightField(highlightFieldSolr);
								
								ocupaQueryMejora=true;
							}
							
							query.setRows(SOLR_ROWS);
							//fin mejora

							try {
								QueryResponse queryResponse = server.query( query );

								NamedList<Object> responseHeader = queryResponse.getResponseHeader();

								Integer status = (Integer) responseHeader.get("status");

								if(status!=null && status==0){	
									respuesta.put("status", true);
									if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
										IndiceServiceUtil iu = new IndiceServiceUtil();

										aaDataHipotecas = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"hip",camposIndice,ocupaQueryMejora);

									}
								}else{
									respuesta.put("msg", "Error invocando servicio");	
								}

							} catch (SolrServerException e) {
								log.error(e);
								respuesta.put("msg", "Problema invocando servicio índice");	
							}
							//							}
						}
					}

					if("true".equals(regComercio)){
						if("false".equals(buscarPorInscricionHipo) && "false".equals(buscarPorInscricionProh)){

							List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
							
							String url = SOLR_COM_ENDPOINT;
//							System.out.println("url: "+url);
							SolrServer server = new HttpSolrServer( url );

							boolean ocupaQueryMejora = false;
							
							if(foja!=null || numero!=null || anoCitado!=null || acto!=null || tipo!=null){
								q = getQueryComercio(nombre, foja, numero, anoCitado, acto, tipo,exacta);
							}else{
								
								CamposIndiceDTO campoNombre = new CamposIndiceDTO();
								campoNombre.setNombreCampo("nombre");
								ArrayList<SearchTermDTO> searchTerms = getSearchTerms(nombre,url);
								campoNombre.setSearchTermDTOs(searchTerms);
								camposIndice.add(campoNombre);
								
								q = getComTexto(nombre,searchTerms);
								ocupaQueryMejora=true;
							}

							String highlightFieldSolr = "nombreSociedad, personas";
							
//							System.out.println("query solr Com: "+q);

							SolrQuery query = new SolrQuery();
							
//							query.setQuery(q);
//							query.setRows(SOLR_ROWS);
							
							//mejora
							
							query.set("q", q);
							
							if(!StringUtils.isBlank(nombre) && (foja==null || numero==null || anoCitado==null || acto==null || tipo==null)){
								query.set("defType", "edismax");
								query.set("qf", "nombreSociedad personas");
								query.set("mm", "100%");
								query.setHighlight(true);
								query.setHighlightSimplePre("<span class=\"highlighted\">");
								query.setHighlightSimplePost("</span>");
								query.addHighlightField(highlightFieldSolr);
								
								ocupaQueryMejora=true;
							}
							
							query.setRows(SOLR_ROWS);
							//fin mejora

							try {
								QueryResponse queryResponse = server.query( query );

								NamedList<Object> responseHeader = queryResponse.getResponseHeader();

								Integer status = (Integer) responseHeader.get("status");

								if(status!=null && status==0){	
									respuesta.put("status", true);
									if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
										IndiceServiceUtil iu = new IndiceServiceUtil();

										aaDataComercio = iu.queryResponseToJsonObjectDesc(queryResponse, hayUser,"com",camposIndice,ocupaQueryMejora);

									}
								}else{
									respuesta.put("msg", "Error invocando servicio");	
								}

							} catch (SolrServerException e) {
								log.error(e);
								respuesta.put("msg", "Problema invocando servicio índice");	
							}
							//							}
						}
					}
				}
			} catch (Exception e1) {
				log.error(e1);
				respuesta.put("msg", "Problema invocando servicio");
			}
		}

		ByteArrayOutputStream archivo = IndiceServiceAction.exportDevolucionesExcel(aaData,aaDataHipotecas,aaDataProhibiciones,aaDataComercio);

		ServletOutputStream out;

		String contentType = "application/ms-excel";
		String filename = "indice_"+new Date().getTime()+".xls";

		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename="+filename);

		try {
			out = response.getOutputStream();

			out.write(archivo.toByteArray());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

	}


	private static ByteArrayOutputStream exportDevolucionesExcel(JSONArray listaaData, JSONArray listaaaDataHipotecas, JSONArray listaaaDataProhibiciones, JSONArray listaaaDataComercio){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int MAX_FILAS = 65000;

		try {


			WritableWorkbook w = Workbook.createWorkbook(out);

			//COMERCIO
			if(listaaaDataComercio!=null && listaaaDataComercio.size()>0){
				double resto = listaaaDataComercio.size()%MAX_FILAS;
				int cantidadHojas = listaaaDataComercio.size()/MAX_FILAS;

				if (resto!=0)
					cantidadHojas = cantidadHojas+1;

				for(int z=0;z<cantidadHojas;z++){

					WritableSheet s = w.createSheet("COMERCIO"+z, 0);

					//TamAño de celdas
					s.setColumnView(0, 10);
					s.setColumnView(1, 10);
					s.setColumnView(2, 10);
					s.setColumnView(3, 30);
					s.setColumnView(4, 30);
					s.setColumnView(5, 15);
					s.setColumnView(6, 30);
					s.setColumnView(7, 10);

					jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat("dd/mm/yy hh:mm:ss");	 
					WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat); 
					dateFormat.setWrap(true);
					//				dateFormat.setBackground(Colour.GRAY_25);

					WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
					WritableCellFormat arial12format = new WritableCellFormat (arial12font); 
					arial12format.setBackground(Colour.LIGHT_GREEN);

					WritableFont subtitles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
					WritableCellFormat subtitlesformat = new WritableCellFormat (subtitles); 
					//				subtitlesformat.setBackground(Colour.GRAY_25);
					subtitlesformat.setWrap(true);

					s.addCell(new jxl.write.Label(0, 0, "Foja",arial12format));  //Foja
					s.addCell(new jxl.write.Label(1, 0, "Numero",arial12format));  //Numero
					s.addCell(new jxl.write.Label(2, 0, "Año",arial12format));  //Agno
					s.addCell(new jxl.write.Label(3, 0, "Nombre Sociedad",arial12format));  //Nombres
					s.addCell(new jxl.write.Label(4, 0, "Socio",arial12format));  //Tipo
					s.addCell(new jxl.write.Label(5, 0, "Acto",arial12format));  //Caratula
					s.addCell(new jxl.write.Label(6, 0, "Tipo",arial12format));  //Caratula
					s.addCell(new jxl.write.Label(7, 0, "Folio",arial12format));  //Caratula


					int cuentaRegistros=0;
					int fila=1;

					for(int i=z*MAX_FILAS;i<listaaaDataComercio.size();i++){
						if(cuentaRegistros==MAX_FILAS){
							break;
						}
						JSONObject rec = (JSONObject) listaaaDataComercio.get(i);

						Integer foja = (Integer) rec.get("foja");
						Integer num = (Integer) rec.get("num");
						Integer ano = (Integer) rec.get("ano");
						String nombreSociedad = (String) rec.get("nombreSociedad");
						String personas = (String) rec.get("personas");
						String tipo = (String) rec.get("tipo");
						String acto = (String) rec.get("acto");
						String folio = (String) rec.get("folio");
						s.addCell(new jxl.write.Label(0, fila, foja.toString(), subtitlesformat));  //Foja
						s.addCell(new jxl.write.Label(1, fila, num.toString(), subtitlesformat));  //numero
						s.addCell(new jxl.write.Label(2, fila, ano.toString(), subtitlesformat));  //ano
						s.addCell(new jxl.write.Label(3, fila, nombreSociedad, subtitlesformat));  //nombreSociedad
						s.addCell(new jxl.write.Label(4, fila, personas, subtitlesformat));  //personas
						s.addCell(new jxl.write.Label(5, fila, acto, subtitlesformat));  //acto
						s.addCell(new jxl.write.Label(6, fila, tipo, subtitlesformat));  //tipo
						s.addCell(new jxl.write.Label(7, fila, folio, subtitlesformat));  //folio

						cuentaRegistros++;
						fila++;
					}
				}
			}

			//PROHIBICIONES
			if(listaaaDataProhibiciones!=null && listaaaDataProhibiciones.size()>0){
				double resto = listaaaDataProhibiciones.size()%MAX_FILAS;
				int cantidadHojas = listaaaDataProhibiciones.size()/MAX_FILAS;

				if (resto!=0)
					cantidadHojas = cantidadHojas+1;

				for(int z=0;z<cantidadHojas;z++){

					WritableSheet s = w.createSheet("PROHIBICIONES"+z, 0);

					//TamAño de celdas
					s.setColumnView(0, 10);
					s.setColumnView(1, 10);
					s.setColumnView(2, 10);
					s.setColumnView(3, 15);
					s.setColumnView(4, 15);
					s.setColumnView(5, 15);
					s.setColumnView(6, 80);
					s.setColumnView(7, 20);
					s.setColumnView(8, 10);
					s.setColumnView(9, 20);


					jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat("dd/mm/yy hh:mm:ss");	 
					WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat); 
					dateFormat.setWrap(true);
					//				dateFormat.setBackground(Colour.GRAY_25);

					WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
					WritableCellFormat arial12format = new WritableCellFormat (arial12font); 
					arial12format.setBackground(Colour.LIGHT_GREEN);

					WritableFont subtitles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
					WritableCellFormat subtitlesformat = new WritableCellFormat (subtitles); 
					//				subtitlesformat.setBackground(Colour.GRAY_25);
					subtitlesformat.setWrap(true);

					s.addCell(new jxl.write.Label(0, 0, "Foja",arial12format));  //Foja
					s.addCell(new jxl.write.Label(1, 0, "Numero",arial12format));  //Numero
					s.addCell(new jxl.write.Label(2, 0, "Año",arial12format));  //Agno
					s.addCell(new jxl.write.Label(3, 0, "Foja Proh",arial12format));  //Foja
					s.addCell(new jxl.write.Label(4, 0, "Numero Proh",arial12format));  //Numero
					s.addCell(new jxl.write.Label(5, 0, "Año Proh",arial12format));  //Agno
					s.addCell(new jxl.write.Label(6, 0, "Nombres",arial12format));  //Nombres
					s.addCell(new jxl.write.Label(7, 0, "Naturaleza",arial12format));  //Naturaleza
					s.addCell(new jxl.write.Label(8, 0, "Tipo",arial12format));  //Tipo
					s.addCell(new jxl.write.Label(9, 0, "Caratula",arial12format));  //Caratula


					int cuentaRegistros=0;
					int fila=1;

					for(int i=z*MAX_FILAS;i<listaaaDataProhibiciones.size();i++){
						if(cuentaRegistros==MAX_FILAS){
							break;
						}
						JSONObject rec = (JSONObject) listaaaDataProhibiciones.get(i);

						Integer foja = (Integer) rec.get("foja");
						Integer num = (Integer) rec.get("num");
						Integer ano = (Integer) rec.get("ano");
						Integer fojah = (Integer) rec.get("fojaph");
						Integer numh = (Integer) rec.get("numph");
						Integer anoh = (Integer) rec.get("anoph");
						String nombres = (String) rec.get("nombre");
						String naturaleza = (String) rec.get("naturaleza");
						String tipo = (String) rec.get("tipo");
						String caratula = (String) rec.get("caratula");
						s.addCell(new jxl.write.Label(0, fila, foja.toString(), subtitlesformat));  //Foja
						s.addCell(new jxl.write.Label(1, fila, num.toString(), subtitlesformat));  //numero
						s.addCell(new jxl.write.Label(2, fila, ano.toString(), subtitlesformat));  //ano
						s.addCell(new jxl.write.Label(3, fila, fojah.toString(), subtitlesformat));  //Foja h
						s.addCell(new jxl.write.Label(4, fila, numh.toString(), subtitlesformat));  //numero h
						s.addCell(new jxl.write.Label(5, fila, anoh.toString(), subtitlesformat));  //ano h
						s.addCell(new jxl.write.Label(6, fila, nombres, subtitlesformat));  //nombres
						s.addCell(new jxl.write.Label(7, fila, naturaleza, subtitlesformat));  //naturaleza
						s.addCell(new jxl.write.Label(8, fila, tipo, subtitlesformat));  //tipo
						s.addCell(new jxl.write.Label(9, fila, caratula, subtitlesformat));  //caratula

						cuentaRegistros++;
						fila++;
					}
				}
			}

			//HIPOTECA
			if(listaaaDataHipotecas!=null && listaaaDataHipotecas.size()>0){

				double resto = listaaaDataHipotecas.size()%MAX_FILAS;
				int cantidadHojas = listaaaDataHipotecas.size()/MAX_FILAS;

				if (resto!=0)
					cantidadHojas = cantidadHojas+1;

				for(int z=0;z<cantidadHojas;z++){

					WritableSheet s = w.createSheet("HIPOTECA"+z, 0);

					//TamAño de celdas
					s.setColumnView(0, 10);
					s.setColumnView(1, 10);
					s.setColumnView(2, 10);
					s.setColumnView(3, 10);
					s.setColumnView(4, 10);
					s.setColumnView(5, 10);
					s.setColumnView(6, 80);
					s.setColumnView(7, 20);
					s.setColumnView(8, 10);
					s.setColumnView(9, 20);


					jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat("dd/mm/yy hh:mm:ss");	 
					WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat); 
					dateFormat.setWrap(true);
					//				dateFormat.setBackground(Colour.GRAY_25);

					WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
					WritableCellFormat arial12format = new WritableCellFormat (arial12font); 
					arial12format.setBackground(Colour.LIGHT_GREEN);

					WritableFont subtitles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
					WritableCellFormat subtitlesformat = new WritableCellFormat (subtitles); 
					//				subtitlesformat.setBackground(Colour.GRAY_25);
					subtitlesformat.setWrap(true);

					s.addCell(new jxl.write.Label(0, 0, "Foja",arial12format));  //Foja
					s.addCell(new jxl.write.Label(1, 0, "Numero",arial12format));  //Numero
					s.addCell(new jxl.write.Label(2, 0, "Año",arial12format));  //Agno
					s.addCell(new jxl.write.Label(3, 0, "Foja H",arial12format));  //Foja
					s.addCell(new jxl.write.Label(4, 0, "Numero H",arial12format));  //Numero
					s.addCell(new jxl.write.Label(5, 0, "Año H",arial12format));  //Agno
					s.addCell(new jxl.write.Label(6, 0, "Nombres",arial12format));  //Nombres
					s.addCell(new jxl.write.Label(7, 0, "Naturaleza",arial12format));  //Naturaleza
					s.addCell(new jxl.write.Label(8, 0, "Tipo",arial12format));  //Tipo
					s.addCell(new jxl.write.Label(9, 0, "Caratula",arial12format));  //Caratula


					int cuentaRegistros=0;
					int fila=1;

					for(int i=z*MAX_FILAS;i<listaaaDataHipotecas.size();i++){
						if(cuentaRegistros==MAX_FILAS){
							break;
						}
						JSONObject rec = (JSONObject) listaaaDataHipotecas.get(i);

						Integer foja = (Integer) rec.get("foja");
						Integer num = (Integer) rec.get("num");
						Integer ano = (Integer) rec.get("ano");
						Integer fojah = (Integer) rec.get("fojah");
						Integer numh = (Integer) rec.get("numh");
						Integer anoh = (Integer) rec.get("anoh");
						String nombres = (String) rec.get("nombre");
						String naturaleza = (String) rec.get("naturaleza");
						String tipo = (String) rec.get("tipo");
						String caratula = (String) rec.get("caratula");
						s.addCell(new jxl.write.Label(0, fila, foja.toString(), subtitlesformat));  //Foja
						s.addCell(new jxl.write.Label(1, fila, num.toString(), subtitlesformat));  //numero
						s.addCell(new jxl.write.Label(2, fila, ano.toString(), subtitlesformat));  //ano
						s.addCell(new jxl.write.Label(3, fila, fojah.toString(), subtitlesformat));  //Foja h
						s.addCell(new jxl.write.Label(4, fila, numh.toString(), subtitlesformat));  //numero h
						s.addCell(new jxl.write.Label(5, fila, anoh.toString(), subtitlesformat));  //ano h
						s.addCell(new jxl.write.Label(6, fila, nombres, subtitlesformat));  //nombres
						s.addCell(new jxl.write.Label(7, fila, naturaleza, subtitlesformat));  //naturaleza
						s.addCell(new jxl.write.Label(8, fila, tipo, subtitlesformat));  //tipo
						s.addCell(new jxl.write.Label(9, fila, caratula, subtitlesformat));  //caratula

						cuentaRegistros++;
						fila++;
					}
				}
			}

			//PROPIEDAD
			if(listaaData!=null && listaaData.size()>0){

				double resto = listaaData.size()%MAX_FILAS;
				int cantidadHojas = listaaData.size()/MAX_FILAS;

				if (resto!=0)
					cantidadHojas = cantidadHojas+1;


				for(int z=0;z<cantidadHojas;z++){

					WritableSheet s = w.createSheet("PROPIEDAD"+z, 0);

					//TamAño de celdas
					s.setColumnView(0, 10);
					s.setColumnView(1, 10);
					s.setColumnView(2, 10);
					s.setColumnView(3, 80);
					s.setColumnView(4, 80);
					s.setColumnView(5, 20);
					s.setColumnView(6, 20);
					s.setColumnView(7, 15);
					s.setColumnView(8, 15);

					jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat("dd/mm/yy hh:mm:ss");	 
					WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat); 
					dateFormat.setWrap(true);
					//				dateFormat.setBackground(Colour.GRAY_25);

					WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
					WritableCellFormat arial12format = new WritableCellFormat (arial12font); 
					arial12format.setBackground(Colour.LIGHT_GREEN);

					WritableFont subtitles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
					WritableCellFormat subtitlesformat = new WritableCellFormat (subtitles); 
					//				subtitlesformat.setBackground(Colour.GRAY_25);
					subtitlesformat.setWrap(true);

					s.addCell(new jxl.write.Label(0, 0, "Foja",arial12format));  //Foja
					s.addCell(new jxl.write.Label(1, 0, "Numero",arial12format));  //Numero
					s.addCell(new jxl.write.Label(2, 0, "Año",arial12format));  //Agno
					s.addCell(new jxl.write.Label(3, 0, "Nombres",arial12format));  //Nombres
					s.addCell(new jxl.write.Label(4, 0, "Direccion",arial12format));  //Direccion
					s.addCell(new jxl.write.Label(5, 0, "Comuna",arial12format));  //Comuna
					s.addCell(new jxl.write.Label(6, 0, "Naturaleza",arial12format));  //Naturaleza
					s.addCell(new jxl.write.Label(7, 0, "Tipo",arial12format));  //Tipo
					s.addCell(new jxl.write.Label(8, 0, "Caratula",arial12format));  //Caratula

					int cuentaRegistros=0;
					int fila=1;

					for(int i=z*MAX_FILAS;i<listaaData.size();i++){
						if(cuentaRegistros==MAX_FILAS){
							break;
						}

						JSONObject rec = (JSONObject) listaaData.get(i);

						Integer foja = (Integer) rec.get("foja");
						Integer num = (Integer) rec.get("num");
						Integer ano = (Integer) rec.get("ano");
						String nombres = (String) rec.get("nombre");
						String direccion = (String) rec.get("dir");
						String comuna = (String) rec.get("comuna");
						String naturaleza = (String) rec.get("naturaleza");
						String tipo = (String) rec.get("tipo");
						String caratula = (String) rec.get("caratula");
						s.addCell(new jxl.write.Label(0, fila, foja.toString(), subtitlesformat));  //Foja
						s.addCell(new jxl.write.Label(1, fila, num.toString(), subtitlesformat));  //numero
						s.addCell(new jxl.write.Label(2, fila, ano.toString(), subtitlesformat));  //ano
						s.addCell(new jxl.write.Label(3, fila, nombres, subtitlesformat));  //nombres
						s.addCell(new jxl.write.Label(4, fila, direccion, subtitlesformat));  //direccion
						s.addCell(new jxl.write.Label(5, fila, comuna, subtitlesformat));  //comuna
						s.addCell(new jxl.write.Label(6, fila, naturaleza, subtitlesformat));  //naturaleza
						s.addCell(new jxl.write.Label(7, fila, tipo, subtitlesformat));  //tipo
						s.addCell(new jxl.write.Label(8, fila, caratula, subtitlesformat));  //caratula

						cuentaRegistros++;
						fila++;
					}

				}
			} 	

			w.write();
			w.close();

		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (RowsExceededException e) {
			logger.error(e.getMessage(),e);
		} catch (WriteException e) {
			logger.error(e.getMessage(),e);
		}
		return out;	
	}

	@SuppressWarnings({ "unchecked" })
	public void getRutSesion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		JSONObject json = new JSONObject();
		json.put("status", false);	

		try {		

			if(request.getSession().getAttribute("rut") != null){
				json.put("rut", request.getSession().getAttribute("rut").toString());
				json.put("requirente", request.getSession().getAttribute("requirente").toString());
				json.put("status", true);
			} 

		} catch (Exception e) {

			logger.error("error en getRutSesion: " + e.getMessage(), e);
			json.put("msg", "Problemas en servidor al buscar rut en sesion");

		}

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@SuppressWarnings({ "unchecked" })
	public void agregarNuevaAtencion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json");

		String rutp = request.getParameter("rut");		

		JSONObject json = new JSONObject();
		json.put("status", false);
		json.put("msg", "");

		JSONObject req = new JSONObject();
		req.put("rut", rutp);

		json.put("req", req);	


		if(StringUtils.isNotBlank(rutp)){

			json.put("status", true);	
			
			WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();
			String dv = rutp.substring(rutp.length()-1, rutp.length()); 
			String rut = rutp.substring(0, rutp.length()-1);
			
			try {
				RequirenteVO requirenteVO = caratulaDelegate.obtenerRequirentePorRut(new UsuarioWebVO(), rut, dv.charAt(0));
				if(requirenteVO != null){
					request.getSession().setAttribute("requirente", requirenteVO.getNombres()+" "+requirenteVO.getApellidoPaterno()+" "+requirenteVO.getApellidoMaterno());
				}else{
					request.getSession().setAttribute("requirente", "no existe en nuestros registros");
				}
			} catch (GeneralException e1) {
				logger.error(e1.getMessage());
			}
			
			request.getSession().setAttribute("rut", rutp);

			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");

			InformacionesDAO daoinfo = new InformacionesDAO();

			InformacionesDTO informacionesDTO = new InformacionesDTO();
			informacionesDTO.setRut(rutp);
			informacionesDTO.setUsuario(usuario);

			try {
				daoinfo.create(informacionesDTO);
			} catch (SQLException e) {
				logger.error(e.getMessage());
			} catch (ServiceLocatorException e) {
				logger.error(e.getMessage());
			} catch (GeneralException e) {
				logger.error(e.getMessage());
			}

		}else{
			json.put("msg", "rut no válido.");
		}


		JSONObject res = new JSONObject();

		//		res.put("entregaEnLineaDTO", entregaEnLineaDTO);

		json.put("res", res);

		try {
			json.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerAtencionPorDia(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONObject contadores = new JSONObject();
		Boolean status = false;
		String msg = "";
		AtencionInformacionesDTO atencionInformacionesDTO = null;

		try{
			InformacionesDAO daoinfo = new InformacionesDAO();
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			String usuario =context.getIdToken().getPreferredUsername();			
			usuario = usuario.replaceAll("CBRS\\\\", "");
			
			atencionInformacionesDTO=daoinfo.get(usuario);

			contadores.put("total", atencionInformacionesDTO.getTotal());
			contadores.put("usuario", atencionInformacionesDTO.getUsuario());

			status = true;

		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("contadores", contadores);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public String consultaSinonimosSolr(String ruta) {
		String synonyms = "";
		URL url;
		try {

			url = new URL(ruta+"/admin/file?file=synonyms.txt&contentType=text/plain;charset=utf-8");

			HttpURLConnection http = (HttpURLConnection)url.openConnection();

			http.setRequestMethod("GET");

			int statusCode = http.getResponseCode();

			if(statusCode==200){				

				BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line+"\n");
				}
				br.close();
				
				return sb.toString();
			}

		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		
		return synonyms;
	}
	
	

}