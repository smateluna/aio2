package cl.cbrs.aio.util;

import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class RegistrosUtil {
	private static final Logger logger = Logger.getLogger(RegistrosUtil.class);


	public RegistrosUtil(){	
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONArray getRegistros(){				
		JSONArray listaRegistros = new JSONArray();

		try {

			JSONObject registroPropiedadesJSON = new JSONObject();
			registroPropiedadesJSON.put("id", 1);
			registroPropiedadesJSON.put("descripcion", "Propiedades");
			listaRegistros.add(registroPropiedadesJSON);
			JSONObject registroHipotecasJSON = new JSONObject();
			registroHipotecasJSON.put("id", 2);
			registroHipotecasJSON.put("descripcion", "Hipotecas");
			listaRegistros.add(registroHipotecasJSON);
			JSONObject registroProhibicionesJSON = new JSONObject();
			registroProhibicionesJSON.put("id", 3);
			registroProhibicionesJSON.put("descripcion", "Prohibiciones");
			listaRegistros.add(registroProhibicionesJSON);
			JSONObject registroComercioJSON = new JSONObject();
			registroComercioJSON.put("id", 4);
			registroComercioJSON.put("descripcion", "Comercio");
			listaRegistros.add(registroComercioJSON);	
			JSONObject registroAguasJSON = new JSONObject();
			registroAguasJSON.put("id", 5);
			registroAguasJSON.put("descripcion", "Aguas");
			listaRegistros.add(registroAguasJSON);

		} catch (Exception e) {
			logger.error(e);
		}
		
		return listaRegistros;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getRegistro(int idRegistro){				
		JSONObject registroJSON = new JSONObject();

			switch(idRegistro){
				case 1: 
					registroJSON.put("id", 1);
					registroJSON.put("descripcion", "Propiedades");
					break;
				case 2: 
					registroJSON.put("id", 2);
					registroJSON.put("descripcion", "Hipotecas");					
					break;
				case 3: 
					registroJSON.put("id", 3);
					registroJSON.put("descripcion", "Prohibiciones");					
					break;
				case 4: 
					registroJSON.put("id", 4);
					registroJSON.put("descripcion", "Comercio");					
					break;
				case 5: 
					registroJSON.put("id", 5);
					registroJSON.put("descripcion", "Aguas");					
					break;
			}

		
		return registroJSON;
	}	
	
}