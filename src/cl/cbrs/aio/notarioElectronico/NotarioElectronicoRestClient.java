package cl.cbrs.aio.notarioElectronico;

import java.util.List;

import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import cl.cbr.foliomercantil.vo.NotarioElectronicoVO;
import cl.cbrs.aio.util.ConstantesPortalConservador;
import cl.cbrs.aio.util.RestUtil;

public class NotarioElectronicoRestClient {
	private static final Logger logger = Logger.getLogger(NotarioElectronicoRestClient.class);

	public static List<NotarioElectronicoVO> findAll()throws HTTPException, Exception{
		try{
			
			String endPoint = ConstantesPortalConservador.getParametro("URL_NOTARIO_ELECTRONICO");
		
//			List<NotarioElectronicoVO> list= Client.create().resource(endPoint+"/findAll").get(new GenericType<List<NotarioElectronicoVO>>(){});

			ClientResponse clientResponse = Client.create().resource(endPoint+"/findAll").get(ClientResponse.class);
			JSONArray respuesta = (JSONArray) RestUtil.getResponse(clientResponse);
			List<NotarioElectronicoVO> list = new Gson().fromJson(respuesta.toString(), new TypeToken<List<NotarioElectronicoVO>>(){}.getType());
			 
			return list;
		} catch(Exception e){
			e.printStackTrace();
			logger.error("Error invocando servicio notarioElectronico");
			throw e;
		}
	}

	public static NotarioElectronicoVO findById(Integer idNotario)throws HTTPException, Exception{
		try{
			
			String endPoint = ConstantesPortalConservador.getParametro("URL_NOTARIO_ELECTRONICO");
		
			NotarioElectronicoVO notarioElectronicoVO= Client.create().resource(endPoint+"/byId/"+idNotario)
	                .get(new GenericType<NotarioElectronicoVO>(){});
			 
			return notarioElectronicoVO;
		} catch(Exception e){
			e.printStackTrace();
			logger.error("Error invocando servicio notarioElectronico");
			throw e;
		}
	}

	public static List<NotarioElectronicoVO> findRepositoriosById(Integer idNotario)throws HTTPException, Exception{
		try{			
			String endPoint = ConstantesPortalConservador.getParametro("URL_NOTARIO_ELECTRONICO");
		
			ClientResponse clientResponse = Client.create().resource(endPoint+"/respositoriosNotarioById/"+idNotario).get(ClientResponse.class);
			JSONArray respuesta = (JSONArray) RestUtil.getResponse(clientResponse);
			List<NotarioElectronicoVO> notarios = new Gson().fromJson(respuesta.toString(), new TypeToken<List<NotarioElectronicoVO>>(){}.getType()); 
			 
			return notarios;
		} catch(Exception e){
			e.printStackTrace();
			logger.error("Error invocando servicio notarioElectronico");
			throw e;
		}
	}
}
