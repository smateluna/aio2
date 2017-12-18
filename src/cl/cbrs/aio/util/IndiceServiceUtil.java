package cl.cbrs.aio.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import cl.cbrs.aio.dto.CamposIndiceDTO;

public class IndiceServiceUtil {
	
	public IndiceServiceUtil(){}
	
	
	@SuppressWarnings("unchecked")
	public JSONArray queryResponseToJsonObjectDesc(QueryResponse queryResponse, Boolean conKey,String registro, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONArray props = new JSONArray();
		Integer cantidad = queryResponse.getResults().size();

		for(int i = 0 ; i <= cantidad-1; i++){
			SolrDocument doc = queryResponse.getResults().get(i);
			JSONObject salida = null;
			if("prop".equals(registro))	
				salida = converterSolrDocumentToJSONObject(doc, conKey,queryResponse,camposIndice,ocupaQueryMejora);
			else if("hip".equals(registro))
				salida = converterSolrDocumentToJSONObjectHipo(doc,conKey,queryResponse,camposIndice,ocupaQueryMejora);
			else if("proh".equals(registro))
				salida = converterSolrDocumentToJSONObjectProh(doc, conKey,queryResponse,camposIndice,ocupaQueryMejora);
			else if("com".equals(registro))
				salida = converterSolrDocumentToJSONObjectCom(doc, conKey,queryResponse,camposIndice,ocupaQueryMejora);
			
			if(salida!=null){
				props.add(salida);

			}
		}
		
		return props;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray queryResponseToJsonObject(QueryResponse queryResponse, Boolean conKey){
		JSONArray props = new JSONArray();
		
		for(int i = 0 ; i < queryResponse.getResults().size(); i++){

			SolrDocument doc = queryResponse.getResults().get(i);

			JSONObject salida = converterSolrDocumentToJSONObject(doc, conKey,null,null,false);

			props.add(salida);
		}
		
		return props;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject converterSolrDocumentToJSONObject(SolrDocument doc, Boolean conKay, QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONObject salida = new JSONObject();
		
		String id = (String) doc.getFieldValue("id");
		
		Integer fojaj = (Integer) doc.getFieldValue("foja");
		Integer numeroj = (Integer) doc.getFieldValue("numero");
		Integer anoj = (Integer) doc.getFieldValue("ano");

		String nombrej = (String)doc.getFieldValue("nombre");
		String direccionj = (String)doc.getFieldValue("direccion");
		String naturalezaj = (String)doc.getFieldValue("naturaleza");
		String tipo = (String)doc.getFieldValue("tipo");
		String descComunaj = (String)doc.getFieldValue("descComuna");
		String caratulaj = (String)doc.getFieldValue("caratula");

		Integer tieneImagenj = (Integer)doc.getFieldValue("tieneImagen");

		salida.put("key", "");
		if(conKay){
			String mensaje = fojaj+"_"+numeroj+"_"+anoj+"_false";

			CifradoUtil cu = new CifradoUtil();

			String key = cu.cifrado(mensaje);

			salida.put("key", key);	
		}
		
		salida.put("id", id);
		salida.put("foja", fojaj);
		salida.put("num", numeroj);
		salida.put("ano", anoj);
		salida.put("tieneImagen", tieneImagenj);
		
		salida.put("caratula", "");
		if(caratulaj!=null){
			int pos = caratulaj.indexOf("-");
			if(pos > -1){
				String[] caratula = caratulaj.split("-");
				caratulaj = caratula[0];
			}
			
			salida.put("caratula", caratulaj.trim());
		}
		
		salida.put("nombre", "");
		if(nombrej!=null){
			salida.put("nombre", nombrej.trim());
		}

		salida.put("naturaleza", "");
		if(naturalezaj!=null){
			salida.put("naturaleza", naturalezaj.trim());
		}

		salida.put("dir", "");
		if(direccionj!=null){
			salida.put("dir", direccionj.trim());
		}
		
		salida.put("comuna", "");
		if(direccionj!=null){
			salida.put("comuna", descComunaj.trim());
		}


		salida.put("tipo", "");
		if(tipo!=null){
			salida.put("tipo", tipo.trim());
		}
		
		
		//mejora
		if(ocupaQueryMejora){
			Map<String, Map<String, List<String>>> destacados = queryResponse.getHighlighting();

			Map<String, List<String>> highlight = destacados.get(id);

			List<String> destacadoNombre = highlight.get("nombre");
			List<String> destacadoDireccion = highlight.get("direccion");

			JSONObject dest = new JSONObject();


			Integer matchesNombre = 0;

			if(destacadoNombre!=null){

				String nombre = destacadoNombre.get(0);

				matchesNombre = StringUtils.countMatches(nombre, "highlighted");

				dest.put("nombre", nombre);

			}

			Integer matchesDireccion = 0;

			if(destacadoDireccion!=null){


				String direccion = destacadoDireccion.get(0);

				matchesDireccion = StringUtils.countMatches(direccion, "highlighted");

				dest.put("dir", direccion);

			}

			int searchTermsNombre=0;
			int searchTermsDir=0;
			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("nombre"))
					searchTermsNombre=campos.getSearchTermDTOs().size();
				if(campos.getNombreCampo().equals("direccion"))
					searchTermsDir=campos.getSearchTermDTOs().size();
			}
				
			if((matchesNombre>=searchTermsNombre && searchTermsNombre!=0) || (matchesDireccion>=searchTermsDir && searchTermsDir!=0)){
				salida.put("highlight", dest);

				salida.put("revisado", 0);

				return salida;			
			}
			return null;
		} else {
			return salida;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject converterSolrDocumentToJSONObjectHipo(SolrDocument doc, Boolean conKay, QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONObject salida = new JSONObject();
		
		String id = (String) doc.getFieldValue("id");
		
		Integer fojaj = (Integer) doc.getFieldValue("foja_pr");
		Integer numeroj = (Integer) doc.getFieldValue("numero_pr");
		Integer anoj = (Integer) doc.getFieldValue("ano_pr");
		
		Integer fojah = (Integer) doc.getFieldValue("foja_hi");
		Integer numeroh = (Integer) doc.getFieldValue("numero_hi");
		Integer anoh = (Integer) doc.getFieldValue("ano_hi");

		String nombrej = (String)doc.getFieldValue("nombre");
		String naturalezaj = (String)doc.getFieldValue("naturaleza");
		String tipo = (String)doc.getFieldValue("tipo");
		String caratulaj = (String)doc.getFieldValue("caratula");

		Integer tieneImagenj = (Integer)doc.getFieldValue("tieneImagen");

		salida.put("key", "");
		if(conKay){
			String mensaje = fojaj+"_"+numeroj+"_"+anoj+"_false";

			CifradoUtil cu = new CifradoUtil();

			String key = cu.cifrado(mensaje);

			salida.put("key", key);	
		}
		
		salida.put("id", id);
		salida.put("foja", fojaj);
		salida.put("num", numeroj);
		salida.put("ano", anoj);
		salida.put("fojah", fojah);
		salida.put("numh", numeroh);
		salida.put("anoh", anoh);
		salida.put("tieneImagen", tieneImagenj);
		
		salida.put("caratula", "");
		if(caratulaj!=null){
			int pos = caratulaj.indexOf("-");
			if(pos > -1){
				String[] caratula = caratulaj.split("-");
				caratulaj = caratula[0];
			}
			
			salida.put("caratula", caratulaj.trim());
		}
		
		salida.put("nombre", "");
		if(nombrej!=null){
			salida.put("nombre", nombrej.trim());
		}

		salida.put("naturaleza", "");
		if(naturalezaj!=null){
			salida.put("naturaleza", naturalezaj.trim());
		}

		salida.put("tipo", "");
		if(tipo!=null){
			salida.put("tipo", tipo.trim());
		}
		
		
		//mejora
		if(ocupaQueryMejora){
			Map<String, Map<String, List<String>>> destacados = queryResponse.getHighlighting();

			Map<String, List<String>> highlight = destacados.get(id);

			List<String> destacadoNombre = highlight.get("nombre");

			JSONObject dest = new JSONObject();


			Integer matchesNombre = 0;

			if(destacadoNombre!=null){

				String nombre = destacadoNombre.get(0);

				matchesNombre = StringUtils.countMatches(nombre, "highlighted");

				dest.put("nombre", nombre);

			}

			int searchTermsNombre=0;

			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("nombre"))
					searchTermsNombre=campos.getSearchTermDTOs().size();
			}
			
			if(matchesNombre>=searchTermsNombre){
				salida.put("highlight", dest);

				salida.put("revisado", 0);

				return salida;			
			}
			
			return null;
		} else {
			return salida;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject converterSolrDocumentToJSONObjectProh(SolrDocument doc, Boolean conKay, QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONObject salida = new JSONObject();
		
		String id = (String) doc.getFieldValue("id");
		
		Integer fojaj = (Integer) doc.getFieldValue("foja_pr");
		Integer numeroj = (Integer) doc.getFieldValue("numero_pr");
		Integer anoj = (Integer) doc.getFieldValue("ano_pr");
		
		Integer fojaph = (Integer) doc.getFieldValue("foja_ph");
		Integer numeroph = (Integer) doc.getFieldValue("numero_ph");
		Integer anoph = (Integer) doc.getFieldValue("ano_ph");

		String nombrej = (String)doc.getFieldValue("nombre");
		String naturalezaj = (String)doc.getFieldValue("naturaleza");
		String tipo = (String)doc.getFieldValue("tipo");
		String caratulaj = (String)doc.getFieldValue("caratula");

		Integer tieneImagenj = (Integer)doc.getFieldValue("tieneImagen");

		salida.put("key", "");
		if(conKay){
			String mensaje = fojaj+"_"+numeroj+"_"+anoj+"_false";

			CifradoUtil cu = new CifradoUtil();

			String key = cu.cifrado(mensaje);

			salida.put("key", key);	
		}
		
		salida.put("id", id);
		salida.put("foja", fojaj);
		salida.put("num", numeroj);
		salida.put("ano", anoj);
		salida.put("fojaph", fojaph);
		salida.put("numph", numeroph);
		salida.put("anoph", anoph);
		salida.put("tieneImagen", tieneImagenj);
		
		salida.put("caratula", "");
		if(caratulaj!=null){
			int pos = caratulaj.indexOf("-");
			if(pos > -1){
				String[] caratula = caratulaj.split("-");
				caratulaj = caratula[0];
			}
			
			salida.put("caratula", caratulaj.trim());
		}
		
		salida.put("nombre", "");
		if(nombrej!=null){
			salida.put("nombre", nombrej.trim());
		}

		salida.put("naturaleza", "");
		if(naturalezaj!=null){
			salida.put("naturaleza", naturalezaj.trim());
		}

		salida.put("tipo", "");
		if(tipo!=null){
			salida.put("tipo", tipo.trim());
		}
		
		
		//mejora
		if(ocupaQueryMejora){
			Map<String, Map<String, List<String>>> destacados = queryResponse.getHighlighting();

			Map<String, List<String>> highlight = destacados.get(id);

			List<String> destacadoNombre = highlight.get("nombre");

			JSONObject dest = new JSONObject();


			Integer matchesNombre = 0;

			if(destacadoNombre!=null){

				String nombre = destacadoNombre.get(0);

				matchesNombre = StringUtils.countMatches(nombre, "highlighted");

				dest.put("nombre", nombre);

			}

			int searchTermsNombre=0;

			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("nombre"))
					searchTermsNombre=campos.getSearchTermDTOs().size();
			}
			
			if(matchesNombre>=searchTermsNombre){
				salida.put("highlight", dest);

				salida.put("revisado", 0);

				return salida;			
			}
			
			return null;
		} else {
			return salida;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject converterSolrDocumentToJSONObjectCom(SolrDocument doc, Boolean conKay, QueryResponse queryResponse, List<CamposIndiceDTO> camposIndice,boolean ocupaQueryMejora){
		JSONObject salida = new JSONObject();
		
		String id = (String) doc.getFieldValue("id");
		
		Integer fojaj = (Integer) doc.getFieldValue("foja");
		Integer numeroj = (Integer) doc.getFieldValue("numero");
		Integer anoj = (Integer) doc.getFieldValue("ano");
		String nombreSociedadj = (String)doc.getFieldValue("nombreSociedad");
		String actoj = (String)doc.getFieldValue("acto");
		String tipoj = (String)doc.getFieldValue("tipo");
		String folioj = (String)doc.getFieldValue("folio");
		ArrayList personasj = (ArrayList)doc.getFieldValue("personas");

		salida.put("key", "");
		if(conKay){
			String mensaje = fojaj+"_"+numeroj+"_"+anoj+"_false";

			CifradoUtil cu = new CifradoUtil();

			String key = cu.cifrado(mensaje);

			salida.put("key", key);	
		}
		
		salida.put("id", id);
		salida.put("foja", fojaj);
		salida.put("num", numeroj);
		salida.put("ano", anoj);
		
		salida.put("nombreSociedad", "");
		if(nombreSociedadj!=null){
			salida.put("nombreSociedad", nombreSociedadj.trim());
		}

		salida.put("personas", "");
		if(personasj!=null){
			salida.put("personas", personasj.toString().replace("[", "").replace("]", ""));
		}

		salida.put("acto", "");
		if(actoj!=null){
			salida.put("acto", actoj.trim());
		}
		
		salida.put("tipo", "");
		if(tipoj!=null){
			salida.put("tipo", tipoj.trim());
		}
		
		salida.put("folio", "");
		if(folioj!=null){
			salida.put("folio", folioj.trim());
		}
		
		//mejora
		if(ocupaQueryMejora){
			Map<String, Map<String, List<String>>> destacados = queryResponse.getHighlighting();

			Map<String, List<String>> highlight = destacados.get(id);

			List<String> destacadoSociedad = highlight.get("nombreSociedad");
			List<String> destacadoPersonas = highlight.get("personas");

			JSONObject dest = new JSONObject();


			Integer matchesNombreSociedad = 0;

			if(destacadoSociedad!=null){


				String nombreSociedad = destacadoSociedad.get(0);

				matchesNombreSociedad = StringUtils.countMatches(nombreSociedad, "highlighted");

				dest.put("nombreSociedad", nombreSociedad);
				//			dest.put("personas", "");
			}

			Integer matchesPersonas = 0; 

			if(destacadoPersonas!=null){

				List<String> destacadoPersonasFinal = new ArrayList<String>();

				for(int i = 0; i<personasj.size(); i++){

					String per = (String)personasj.get(i);

					String perCmp = per.replaceAll(" ", "");

					String perTemp = "";

					for(String destacadoP: destacadoPersonas){

						String dper = Jsoup.parse(destacadoP).text();

						String dperCmp = dper.replaceAll(" ", "");

						if(perCmp.equalsIgnoreCase(dperCmp)){
							perTemp = destacadoP;
							break;
						}
					}


					if(StringUtils.isNotBlank(perTemp)){

						destacadoPersonasFinal.add(perTemp);		
					}else{
						destacadoPersonasFinal.add(per);
					}
				}


				String destPer = destacadoPersonasFinal.toString();

				destPer = destPer.replace("[", "");
				destPer = destPer.replace("]", "");

				matchesPersonas = StringUtils.countMatches(destPer, "highlighted");


				dest.put("personas", destPer);
				//			dest.put("nombreSociedad", "");
			}

			int searchTerms=0;
			for(CamposIndiceDTO campos:camposIndice){
				if(campos.getNombreCampo().equals("nombre"))
					searchTerms=campos.getSearchTermDTOs().size();
			}

			if(matchesNombreSociedad>=searchTerms || matchesPersonas>=searchTerms){
				salida.put("highlight", dest);

				salida.put("revisado", 0);

				return salida;			
			}
			
			return null;
		} else {
			return salida;
		}
		
	}
	
	
	public boolean existeFNA(ArrayList<String> fna, String busca){
		
		if(fna!=null && fna.size()>0){
			
			for(String titulo: fna){
				
				if(titulo.equals(busca)){
					return true;
				}
			}	
		}
		return false;
	}
}

