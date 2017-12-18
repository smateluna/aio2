package cl.cbrs.aio.struts.action.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.CamposIndiceDTO;
import cl.cbrs.aio.dto.SearchTermDTO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.ConstantesPortalConservador;

public class OficioServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(OficioServiceAction.class);
	private static ArrayList<String> listaCiudades = null;

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings("unchecked")
	public void getOficios(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String nombreUsuario = (String) request.getSession().getAttribute("nombreUsuario");

		JSONObject respuesta = new JSONObject();

		if(!"null".equals(nombreUsuario)){

			String institucionP = request.getParameter("institucion");
			String caratulaP = request.getParameter("caratula");					
			String estadoP = request.getParameter("estado");
			String materiaP = request.getParameter("materia");
			String rolP = request.getParameter("rol");
			String oficioP = request.getParameter("oficio");
			String identificadorP = request.getParameter("identificador");
			String fechaEntregaP = request.getParameter("fechaEntrega");
			String fechaCreacionDesdeP = request.getParameter("fechaCreacionDesde");
			String fechaCreacionHastaP = request.getParameter("fechaCreacionHasta");
			String ciudadP = request.getParameter("ciudad");
			
			String requirente = "";
			try {
				if(request.getParameter("requirente")!=null){
					requirente = new String(request.getParameter("requirente").getBytes(),"UTF-8");
					requirente = requirente.toLowerCase();
				}
			} catch (UnsupportedEncodingException e3) {
				requirente= "";
			}
			
			Long caratula = null;
			String institucion = null;
			String materia = null;
			String rol = null;
			String oficio = null;
			String identificador = null;
			Date fechaCreacionDesde = null;
			Date fechaCreacionHasta = null;
			Date fechaEntrega = null;

			JSONArray aaData = new JSONArray();
			respuesta.put("status", false);

			if(StringUtils.isBlank(institucionP) && StringUtils.isBlank(caratulaP) && StringUtils.isBlank(requirente) && StringUtils.isBlank(estadoP)
					&& StringUtils.isBlank(materiaP) && StringUtils.isBlank(rolP) && StringUtils.isBlank(oficioP) && StringUtils.isBlank(identificadorP) 
					&& StringUtils.isBlank(fechaCreacionDesdeP) && StringUtils.isBlank(fechaCreacionHastaP) && StringUtils.isBlank(fechaEntregaP) 
					&& StringUtils.isBlank(ciudadP)){
				respuesta.put("msg", "Se requiere al menos un campo.");	
			}else{
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					
					if(caratulaP!=null & !"".equals(caratulaP))
						caratula = Long.parseLong(caratulaP);
					if(institucionP!=null & !"".equals(institucionP))
						institucion = new String(institucionP.getBytes(),"UTF-8");
					if(materiaP!=null & !"".equals(materiaP))
						materia = new String(materiaP.getBytes(),"UTF-8");
					if(rolP!=null & !"".equals(rolP))
						rol = new String(rolP.getBytes(),"UTF-8");
					if(oficioP!=null & !"".equals(oficioP))
						oficio = new String(oficioP.getBytes(),"UTF-8");
					if(identificadorP!=null & !"".equals(identificadorP))
						identificador = new String(identificadorP.getBytes(),"UTF-8");					
					if(fechaCreacionDesdeP!=null & !"".equals(fechaCreacionDesdeP))
						fechaCreacionDesde = sdf.parse(fechaCreacionDesdeP);
					if(fechaCreacionHastaP!=null & !"".equals(fechaCreacionHastaP))
						fechaCreacionHasta = sdf.parse(fechaCreacionHastaP);
					if(fechaEntregaP!=null & !"".equals(fechaEntregaP))
						fechaEntrega = sdf.parse(fechaEntregaP);
					
					String q = "";

					String SOLR_ENDPOINT = ConstantesPortalConservador.getParametro("SOLROFICIO", "endpoint");			

					Integer SOLR_ROWS = 50;
					try {
						SOLR_ROWS = Integer.parseInt(ConstantesPortalConservador.getParametro("SOLR", "rows"));
					} catch (Exception e1) {
						log.error(e1);
					}

					try {
						String url = SOLR_ENDPOINT;
						SolrServer server = new HttpSolrServer( url );
						
						boolean ocupaQueryMejora = false;
						String texto="";
						
						if(caratula!=null || institucion!=null || estadoP!=null || materia!=null || rol!=null || oficio!=null || identificador!=null || fechaCreacionDesde!=null || fechaCreacionHasta!=null || fechaEntrega!=null || ciudadP!=null){
							q = getQuery(requirente, caratula, institucion, estadoP, materia, rol, oficio, identificador, fechaCreacionDesde, fechaCreacionHasta, fechaEntrega, ciudadP, false);
						}else{
							if(!StringUtils.isBlank(requirente))
								texto=texto+requirente;
							if(!StringUtils.isBlank(institucion)){
								if(texto.length()>0)
									texto=texto+" "+institucion;
								else
									texto=texto+institucion;
							}							
							
							ArrayList<SearchTermDTO> searchTerms = getSearchTerms(texto,url);
							
							q = getComTexto(texto,searchTerms);
							ocupaQueryMejora=true;
						}
	
						SolrQuery query = new SolrQuery();
						
						//mejora
						String highlightFieldSolr = "requirente, institucion";
						
						List<CamposIndiceDTO> camposIndice= new ArrayList<CamposIndiceDTO>();
						
						CamposIndiceDTO campoNombre = new CamposIndiceDTO();
						campoNombre.setNombreCampo("requirente");
						ArrayList<SearchTermDTO> searchTerms = getSearchTerms(requirente,url);
						campoNombre.setSearchTermDTOs(searchTerms);
						camposIndice.add(campoNombre);		
						
						CamposIndiceDTO campoInstitucion = new CamposIndiceDTO();
						campoInstitucion.setNombreCampo("institucion");
						searchTerms = getSearchTerms(institucion,url);
						campoInstitucion.setSearchTermDTOs(searchTerms);
						camposIndice.add(campoInstitucion);
						
						query.set("q", q);
						
						if(!StringUtils.isBlank(requirente) || !StringUtils.isBlank(institucion)){
							query.set("defType", "edismax");
							query.set("qf", "requirente institucion");
							query.set("mm", "100%");
							query.setHighlight(true);
							query.setHighlightSimplePre("<span class=\"highlighted\">");
							query.setHighlightSimplePost("</span>");
							query.addHighlightField(highlightFieldSolr);
							
							ocupaQueryMejora=true;
						}
						
						query.setRows(SOLR_ROWS);
						query.setSort("fechaCreacion", SolrQuery.ORDER.desc);
						//fin mejora
	
						try {
							QueryResponse queryResponse = server.query( query );
	
							NamedList<Object> responseHeader = queryResponse.getResponseHeader();
	
							Integer status = (Integer) responseHeader.get("status");
	
							if(status!=null && status==0){	
								respuesta.put("status", true);
								if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
									aaData = queryResponseToJsonObjectDesc(queryResponse,camposIndice,ocupaQueryMejora);
	
								}
							}else{
								respuesta.put("msg", "Error invocando servicio");	
							}
	
						} catch (SolrServerException e) {
							log.error(e.getMessage(),e);
							respuesta.put("msg", "Problema invocando servicio índice");	
						}

					} catch (Exception e1) {
						log.error(e1.getMessage(),e1);
						respuesta.put("msg", "Problema invocando servicio");
					}
				} catch(Exception e){
					log.error(e.getMessage(), e);
					respuesta.put("msg", "Error al consultar oficios");
				}
			}



			respuesta.put("aaData", aaData);

		} else {
			respuesta.put("msg", "Ha finalizado su sesion, favor vuelva a ingresar");
		}

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public String getQuery(String requirente, Long caratula, String institucion, String estado, String materia, String rol, String oficio, String identificador, Date fechaCreacionDesde, Date fechaCreacionHasta, Date fechaEntrega, String ciudad, Boolean exacta) {
		String q = "";

		if(StringUtils.isNotBlank(requirente)){
			if ("true".equals(exacta)){
				q = "requirente:\""+requirente+"\"";
			}else{
				requirente = requirente.replace("\"", "");
				requirente = requirente.trim();
				StringTokenizer tokens=new StringTokenizer(requirente);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}

				q = "requirente:("+nombreParaBusqueda+")";
			}
		}

		if(caratula!=null){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "caratula:"+caratula;
		}
		
		if(institucion!=null && StringUtils.isNotBlank(institucion)){										
			if(q.length()>0)
				q += " AND ";	
			if ("true".equals(exacta)){
				q += "institucion:\""+institucion+"\"";
			}else{
				institucion = institucion.replace("\"", "");
				institucion = institucion.trim();
				StringTokenizer tokens=new StringTokenizer(institucion);
				String nombreParaBusqueda = new String();
				while(tokens.hasMoreTokens()) {
					nombreParaBusqueda=nombreParaBusqueda+" +"+tokens.nextToken();				
				}

				q += "institucion:("+nombreParaBusqueda+")";
			}
		}
		
		if(estado!=null && StringUtils.isNotBlank(estado)){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "estado:"+estado;
		}
		
		if(ciudad!=null && StringUtils.isNotBlank(ciudad)){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "ciudad:"+ciudad;
		}		
		
		if(materia!=null && StringUtils.isNotBlank(materia)){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "materia:"+materia;
		}
		
		if(rol!=null && StringUtils.isNotBlank(rol)){		
			if(rol.indexOf("-")>0)
				rol = "\""+rol+"\"";
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "rol:"+rol;
		}
		
		if(oficio!=null && StringUtils.isNotBlank(oficio)){		
			if(oficio.indexOf("-")>0)
				oficio = "\""+oficio+"\"";			
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "oficio:"+oficio;
		}
		
		if(identificador!=null && StringUtils.isNotBlank(identificador)){
			if(identificador.indexOf("-")>0)
				identificador = "\""+identificador+"\"";				
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "identificador:"+identificador;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat sdfHasta = new SimpleDateFormat("yyyy-MM-dd'T23:59:59.999Z'");
		
		if(fechaCreacionDesde!=null && fechaCreacionHasta==null){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "fechaCreacion:["+sdf.format(fechaCreacionDesde)+" TO *]";
		}
		
		if(fechaCreacionHasta!=null && fechaCreacionDesde==null){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "fechaCreacion:[* TO "+sdf.format(fechaCreacionHasta)+"]";
		}
		
		if(fechaCreacionDesde!=null && fechaCreacionHasta!=null){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "fechaCreacion:["+sdf.format(fechaCreacionDesde)+" TO "+sdfHasta.format(fechaCreacionHasta)+"]";
		}		
		
		if(fechaEntrega!=null){										
			if(q.length()>0)
				q = q + " AND ";	
			q = q + "fechaEntrega:\""+sdf.format(fechaEntrega)+"\"";
		}		

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
							log.error(ex.getMessage(),ex);
						}

						terminos.add(dto);					
					}

				}

			}

		}

		return terminos;
	}
	
	private String getComTexto(String nombre, ArrayList<SearchTermDTO> searchTermDTOs) {
		String q = "";
		
		if(StringUtils.isNotBlank(nombre)){
			
			String[] total = nombre.split(" ");
			
			if(total==null){
				q = "+"+nombre;
			}else if(total!=null && total.length>=1){
				
				for(SearchTermDTO dto: searchTermDTOs){

					if(dto.getAbierto()){
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

	@SuppressWarnings("unchecked")
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try{
			String solrSuggesterUrl = ConstantesPortalConservador.getParametro("SOLROFICIO_SUGGEST", "endpoint");
			respuesta.put("solrSuggesterUrl", solrSuggesterUrl);
			
			FlujoDAO flujoDao = new FlujoDAO();
			if(listaCiudades==null)
				listaCiudades = flujoDao.getCiudades();
			respuesta.put("ciudades", listaCiudades);
	
			status = true;
		} catch(Exception e){
			logger.error(e.getMessage(),e );
			status = false;
			msg = "Error al inicializar oficio";
		}
		
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
	
	@SuppressWarnings("unchecked")
	public JSONArray queryResponseToJsonObjectDesc(QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONArray props = new JSONArray();
		Integer cantidad = queryResponse.getResults().size();

		for(int i = 0 ; i<cantidad; i++){
			SolrDocument doc = queryResponse.getResults().get(i);
			JSONObject salida = null;
			salida = converterSolrDocumentToJSONObject(doc, queryResponse, camposIndice, ocupaQueryMejora);
			
			if(salida!=null){
				props.add(salida);

			}
		}
		
		return props;
	}	
	
	@SuppressWarnings("unchecked")
	public JSONObject converterSolrDocumentToJSONObject(SolrDocument doc, QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice, boolean ocupaQueryMejora){
		JSONObject salida = new JSONObject();
		
		String id = (String) doc.getFieldValue("id");
		String caratulaj = (String) doc.getFieldValue("caratula");
		String estado = (String) doc.getFieldValue("estado");
		String institucion = (String) doc.getFieldValue("institucion");
		String requirente = (String)doc.getFieldValue("requirente");
		String materia = (String)doc.getFieldValue("materia");
		String rol = (String)doc.getFieldValue("rol");
		String oficio = (String)doc.getFieldValue("oficio");
		String identificador = (String)doc.getFieldValue("identificador");
		Date fechaEntrega = (Date)doc.getFieldValue("fechaEntrega");
		Date fechaCreacion = (Date)doc.getFieldValue("fechaCreacion");
		
		salida.put("id", id);
		salida.put("caratula", "");
		if(caratulaj!=null){
			caratulaj = caratulaj.split("-")[0];			
			salida.put("caratula", new Long(caratulaj.trim()));
		}
		
		salida.put("requirente", "");
		if(requirente!=null){
			salida.put("requirente", requirente.trim());
		}

		salida.put("estado", "");
		if(estado!=null){
			salida.put("estado", estado.trim());
		}

		salida.put("institucion", "");
		if(institucion!=null){
			salida.put("institucion", institucion.trim());
		}
		
		salida.put("materia", "");
		if(materia!=null){
			salida.put("materia", materia.trim());
		}

		salida.put("rol", "");
		if(rol!=null){
			salida.put("rol", rol.trim());
		}
		
		salida.put("oficio", "");
		if(oficio!=null){
			salida.put("oficio", oficio.trim());
		}
		
		salida.put("identificador", "");
		if(identificador!=null){
			salida.put("identificador", identificador.trim());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		salida.put("fechaEntrega", "");
		if(fechaEntrega!=null){
//			salida.put("fechaEntrega", fechaEntrega.getTime());
			salida.put("fechaEntrega", sdf.format(fechaEntrega));
		}
		
		salida.put("fechaCreacion", "");
		if(fechaCreacion!=null){
//			salida.put("fechaCreacion", fechaCreacion.getTime());
			salida.put("fechaCreacion", sdf.format(fechaCreacion));
		}
		
		
		//mejora
		if(ocupaQueryMejora){
			Map<String, Map<String, List<String>>> destacados = queryResponse.getHighlighting();

			Map<String, List<String>> highlight = destacados.get(id);

			List<String> destacadoRequirente = highlight.get("requirente");
			List<String> destacadoInstitucion = highlight.get("institucion");

			JSONObject dest = new JSONObject();

			Integer matchesRequirente = 0;
			if(destacadoRequirente!=null){
				String requirenteH = destacadoRequirente.get(0);
				matchesRequirente = StringUtils.countMatches(requirenteH, "highlighted");
				dest.put("requirente", requirenteH);
			}
			
			Integer matchesInstitucion = 0;
			if(destacadoInstitucion!=null){
				String institucionH = destacadoInstitucion.get(0);
				matchesInstitucion = StringUtils.countMatches(institucionH, "highlighted");
				dest.put("institucion", institucionH);
			}


			int searchTermsRequirente=0;
			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("requirente"))
					searchTermsRequirente=campos.getSearchTermDTOs().size();
			}
			
			int searchTermsInstitucion=0;
			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("institucion"))
					searchTermsInstitucion=campos.getSearchTermDTOs().size();
			}			
			
			if((matchesRequirente>=searchTermsRequirente && searchTermsRequirente!=0) || (matchesInstitucion>=searchTermsInstitucion && searchTermsInstitucion!=0)){
				salida.put("highlight", dest);
				salida.put("revisado", 0);
				return salida;			
			}
			
			return null;
		} else {
			return salida;
		}
	}

}