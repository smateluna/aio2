package cl.cbrs.aio.util;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.ClientResponse;

public class RestUtil {
	public static Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(response.getType().toString().contains(MediaType.APPLICATION_OCTET_STREAM_TYPE.toString()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(response.getType().toString().contains(MediaType.APPLICATION_JSON_TYPE.toString()))
				respuesta = new JSONParser().parse(response.getEntity(String.class));
			else
				respuesta = response.getEntity(String.class);

		} else if(response!=null)
			throw new HTTPException(response.getStatus());
		else
			throw new Exception("Sin respuesta del servicio");
		return respuesta;
	}	
}
