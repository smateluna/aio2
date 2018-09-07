package cl.cbrs.aio.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;

import cl.cbr.util.TablaValores;

public class CartelesUtil {
	
	private static final Logger logger = Logger.getLogger(CartelesUtil.class);
	
	final String TABLA_PARAMETROS = "ws_carteles.parametros";
	
	public String obtenerCartel(Integer numero, Integer mes, Integer ano, Boolean bis, Integer tipoArchivo, Integer idReg) throws HTTPException, IOException{
		//obtenerCartel/{numero}/{mes}/{ano}/{bis}/{idReg}?tipoArchivo=1
		String[] pathParams = {numero.toString(), mes.toString(), ano.toString(), bis.toString(), idReg.toString()};
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(tipoArchivo!=null)		
			queryParams.put("tipoArchivo", tipoArchivo.toString());
		String urlStr = getUrl("obtenerCartel", pathParams, queryParams);
		
		return getResponse(urlStr).toString();
	}
	
	public byte[] obtenerImagenCartel(Integer numero, Integer mes, Integer ano, Integer page, Integer tipoArchivo, Boolean bis, Integer idReg) throws HTTPException, IOException{
		//obtenerImagenCartel/{numero}/{mes}/{ano}/{bis}/{numeroPagina}/{idReg}?tipoArchivo=1
		String[] pathParams = {numero.toString(), mes.toString(), ano.toString(), bis.toString(), page.toString(), idReg.toString(), tipoArchivo.toString()};
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(tipoArchivo!=null)		
			queryParams.put("tipoArchivo", tipoArchivo.toString());
		String urlStr = getUrl("obtenerImagenCartel", pathParams, queryParams);
		
		return getResponse(urlStr).toByteArray();		
	}
	
	public byte[] obtenerImagenCertificado(Long idCertificado, Integer idReg) throws HTTPException, IOException{ 
		//obtenerImagenCertificado/{idCertificado}/{idReg}
		String[] pathParams = {idCertificado.toString(), idReg.toString()};
		String urlStr = getUrl("obtenerImagenCertificado", pathParams, null);
		return getResponse(urlStr).toByteArray();	
	}
	
	public String certificarImagenCartel(String certificadoCartelJSON) throws HTTPException, IOException{
		//certificarImagenCartel/{certificadoCartel}
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("certificadoCartel", certificadoCartelJSON);
		String urlStr = getUrl("certificarImagenCartel", null, queryParams);
		return getResponse(urlStr).toString();
	}
	
	public String generarCartel(Integer numero, Integer mes, Integer ano, Boolean bis, Integer idReg, Integer paginaDesde, Integer paginaHasta) throws HTTPException, IOException{
		//generarCartel/{numero}/{mes}/{ano}/{bis}/{idReg}/{paginaDesde}/{paginaHasta}
		String[] pathParams = {numero.toString(), mes.toString(), ano.toString(), bis.toString(), idReg.toString(), paginaDesde.toString(), paginaHasta.toString()};
		String urlStr = getUrl("generarCartel", pathParams, null);
		return getResponse(urlStr).toString();
	}

	public String eliminarCertificado(Long idCertificado, Integer idReg) throws HTTPException, IOException{
		//eliminarCertificado/{idCertificado}/{idReg}
		String[] pathParams = {idCertificado.toString(), idReg.toString()};
		String urlStr = getUrl("eliminarCertificado", pathParams, null);
		return getResponse(urlStr).toString();
	}
	
	public String obtenerListaCertificadosPorUsuario(String usuario, Integer idEstado, Integer maxResults) throws HTTPException, IOException{
		//obtenerListaCertificadosPorUsuario/{usuario}/{idEstado}/{idReg}?maxResults=1
		String[] pathParams = {usuario, idEstado.toString()};
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(maxResults!=null)		
			queryParams.put("maxResults", maxResults.toString());
		String urlStr = getUrl("obtenerListaCertificadosPorUsuario", pathParams, queryParams);
		return getResponse(urlStr).toString();
	}
	
	public String firmarCartel(Long idCertificado, Integer idReg) throws HTTPException, IOException{
		//firmarCartel/{idCertificado}/{idReg}
		String[] pathParams = {idCertificado.toString(), idReg.toString()};
		String urlStr = getUrl("firmarCartel", pathParams, null);
		return getResponse(urlStr).toString();
	}
	
	
	//HTTP UTIL
	public ByteArrayOutputStream getResponse(String urlParam) throws HTTPException, IOException{		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		URL url = new URL(urlParam);
		HttpURLConnection conexion = (HttpURLConnection)url.openConnection();
		String timeout = TablaValores.getValor(TABLA_PARAMETROS, "TIMEOUT", "valor");
		if(timeout!=null)
			conexion.setConnectTimeout(new Integer(timeout));

		InputStream input = new BufferedInputStream(url.openStream());
		byte data[] = new byte[1024];
		int count = 0;
		while ((count = input.read(data)) != -1) {				
			buffer.write(data, 0, count);
		}
		input.close();

		return buffer;
	}
	
	public String getUrl(String metodo, String[] pathParams, HashMap<String,String> queryParams){

		String server = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS", "valor");
		String port = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS", "valor");
		
		StringBuffer uriStrBuffer = new StringBuffer("http://"+server+":"+port+"/wsCarteles/rest/cartel/" + metodo);
		
		if(pathParams!=null && pathParams.length>0)
			for(String pathParam : pathParams)
				uriStrBuffer.append("/" + pathParam.replaceAll(" ", "%20"));
		
		if(queryParams!=null && queryParams.size()>0){
			uriStrBuffer.append("?");
			for(String key : queryParams.keySet())
				uriStrBuffer.append(key+"=" + queryParams.get(key).replaceAll(" ", "%20")+"&");
			uriStrBuffer.deleteCharAt(uriStrBuffer.length()-1);
		}		
		
		return uriStrBuffer.toString();
	}
	
}
