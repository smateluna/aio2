package cl.cbrs.aio.documentos;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cl.cbr.util.TablaValores;
import cl.cbrs.documentos.constantes.ConstantesDocumentos;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DocumentosCliente {
	private static final Logger LOGGER = Logger.getLogger(DocumentosCliente.class);
	private static final String TABLA_PARAMETROS = "ws_documentos.parametros";
	private static String SERVER = "";
	private static String PORT = "";
	private static String SERVER_ESCRITURAS = "";
	private static String PORT_ESCRITURAS = "";	
	private static String SERVER_TOMOS = "";
	private static String PORT_TOMOS = "";	
	private static String SERVER_INSCRIPCION = "";
	private static String PORT_INSCRIPCION = "";	
	private static String SERVER_FIRMA = "";
	private static String PORT_FIRMA = "";
	
	public DocumentosCliente(){
		SERVER = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS", "valor");
		PORT = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS", "valor");
		
		SERVER_FIRMA = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS_FIRMA", "valor");
		PORT_FIRMA = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS_FIRMA", "valor");		
		
		SERVER_ESCRITURAS = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS_ESCRITURAS", "valor");
		PORT_ESCRITURAS = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS_ESCRITURAS", "valor");
		
		SERVER_TOMOS = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS_TOMOS", "valor");
		PORT_TOMOS = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS_TOMOS", "valor");
		
		SERVER_INSCRIPCION = TablaValores.getValor(TABLA_PARAMETROS, "IP_WS_INSCRIPCION", "valor");
		PORT_INSCRIPCION = TablaValores.getValor(TABLA_PARAMETROS, "PORT_WS_INSCRIPCION", "valor");		
	}	

	public JSONObject existeDocumento(Integer idTipoDocumento, Integer idReg, String nombreDocumento, Date fechaDocumento) throws HTTPException, Exception{
		//{idTipoDocumento}/{idReg}/{nombreDocumento}?fechaDocumento=fechaDocumento
		String[] pathParams = {idTipoDocumento.toString(), idReg.toString(), nombreDocumento}; 
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(fechaDocumento!=null)
			queryParams.put("fechaDocumento", String.valueOf(fechaDocumento.getTime()));
		
		return (JSONObject)executeREST(SERVER, PORT, "file", "existeDocumento", pathParams, queryParams);
	}
	
	public byte[] downloadDocumento(Integer idTipoDocumento, Integer idReg, String nombreDocumento, Date fechaDocumento) throws HTTPException, Exception{
		//{idTipoDocumento}/{idReg}/{nombreDocumento}?fechaDocumento=fechaDocumento
		String[] pathParams = {idTipoDocumento.toString(), idReg.toString(), nombreDocumento}; 
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(fechaDocumento!=null)
			queryParams.put("fechaDocumento", String.valueOf(fechaDocumento.getTime()));
		
		return (byte[])executeREST(SERVER, PORT, "file", "downloadDocumento", pathParams, queryParams);
	}	
	
	public JSONObject existeFirma(String nombreDocumento, String firmador, Date fechaDocumento) throws HTTPException, Exception{
		//{nombreDocumento}/{firmador}?fechaDocumento=fechaDocumento
		String[] pathParams = {nombreDocumento, firmador}; 
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(fechaDocumento!=null)
			queryParams.put("fechaDocumento", String.valueOf(fechaDocumento.getTime()));
		
		return (JSONObject)executeREST(SERVER_FIRMA, PORT_FIRMA, "file", "existeFirma", pathParams, queryParams);
	}
	
	public byte[] downloadFirma(String nombreDocumento, String firmador, Date fechaDocumento) throws HTTPException, Exception{
		//{nombreDocumento}/{firmador}?fechaDocumento=fechaDocumento
		String[] pathParams = {nombreDocumento, firmador}; 
		HashMap<String, String> queryParams = new HashMap<String, String>();
		if(fechaDocumento!=null)
			queryParams.put("fechaDocumento", String.valueOf(fechaDocumento.getTime()));
		LOGGER.debug(SERVER_FIRMA +"---"+PORT_FIRMA+" -->"+pathParams+" "+queryParams);
		return (byte[])executeREST(SERVER_FIRMA, PORT_FIRMA, "file", "downloadFirma", pathParams, queryParams);
	}	
	
	public JSONObject existeInscripcion(Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg) throws HTTPException, Exception{
		//{foja}/{numero}/{ano}/{bis}/{idReg}
		String[] pathParams = {foja.toString(), numero.toString(), ano.toString(), String.valueOf(bis), idReg.toString()}; 
		
		return (JSONObject)executeREST(SERVER_INSCRIPCION, PORT_INSCRIPCION, "file", "existeInscripcion", pathParams, null);
	}
	
	public byte[] downloadPageInscripcion(boolean conSello, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg, Integer idTipoDocumento, Integer pagina) throws HTTPException, Exception{
		//{conSello}/{foja}/{numero}/{ano}/{bis}/{idReg}/{idTipoDocumento}/{pagina}
		String[] pathParams = {String.valueOf(conSello), foja.toString(), numero.toString(), ano.toString(), String.valueOf(bis), idReg.toString(), idTipoDocumento.toString(), pagina.toString()}; 
		
		return (byte[])executeREST(SERVER_INSCRIPCION, PORT_INSCRIPCION, "file", "downloadPageInscripcion", pathParams, null);
	}	
	
	public byte[] downloadPageInscripcionVersionada(boolean conSello, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg, Integer pagina) throws HTTPException, Exception{		
		return this.downloadPageInscripcion(conSello, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO, pagina); 
	}
	
	public byte[] downloadPageInscripcionReferencial(boolean conSello, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg, Integer pagina) throws HTTPException, Exception{		
		return this.downloadPageInscripcion(conSello, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL, pagina); 
	}
	
	public byte[] downloadPageInscripcionOriginal(boolean conSello, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg, Integer pagina) throws HTTPException, Exception{		
		return this.downloadPageInscripcion(conSello, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL, pagina); 
	}
	
	public byte[] downloadInscripcion(String formato, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg, Integer idTipoDocumento) throws HTTPException, Exception{
		//{formato}/{foja}/{numero}/{ano}/{bis}/{idReg}/{idTipoDocumento}
		String[] pathParams = {formato, foja.toString(), numero.toString(), ano.toString(), String.valueOf(bis), idReg.toString(), idTipoDocumento.toString()}; 
		
		return (byte[])executeREST(SERVER_INSCRIPCION, PORT_INSCRIPCION, "file", "downloadInscripcion", pathParams, null);
	}	
	
	public byte[] downloadInscripcionVersionada(String formato, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg) throws HTTPException, Exception{
		return this.downloadInscripcion(formato, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO);
	}	
	
	public byte[] downloadInscripcionReferencial(String formato, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg) throws HTTPException, Exception{
		return this.downloadInscripcion(formato, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL);
	}	
	
	public byte[] downloadInscripcionOriginal(String formato, Integer foja, Integer numero, Integer ano, boolean bis, Integer idReg) throws HTTPException, Exception{
		return this.downloadInscripcion(formato, foja, numero, ano, bis, idReg, ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL);
	}
	
	public JSONObject existeTomo(Long idDocumento, Integer idReg) throws HTTPException, Exception{
		//{idDocumento}/{idReg}
		String[] pathParams = {idDocumento.toString(), idReg.toString()};
		
		return (JSONObject)executeREST(SERVER_TOMOS, PORT_TOMOS, "file", "existeTomo", pathParams, null);
	}
	
	public byte[] downloadTomo(Long idDocumento, Integer idReg) throws HTTPException, Exception{
		//{idDocumento}/{idReg}
		String[] pathParams = {idDocumento.toString(), idReg.toString()};
		
		return (byte[])executeREST(SERVER_TOMOS, PORT_TOMOS, "file", "downloadTomo", pathParams, null);
	}	
	
	public JSONObject existeEscritura(Long idDocumentop, Long idDetalleDocumentop, Long caratulap, Integer versionp, 
			boolean esVersion, boolean esDetalle, boolean esPrincipal) throws HTTPException, Exception{

		//{idDocumentop}/{idDetalleDocumentop}/{caratulap}/{versionp}/{esVersionp}/{esDetallep}/{esPrincipalp}
		String[] pathParams = {idDocumentop.toString(), idDetalleDocumentop.toString(), caratulap.toString(), versionp.toString(), 
				String.valueOf(esVersion), String.valueOf(esDetalle), String.valueOf(esPrincipal)};
		
		return (JSONObject)executeREST(SERVER_ESCRITURAS, PORT_ESCRITURAS, "file", "existeEscritura", pathParams, null);			
	}
	
	public byte[] downloadEscritura(boolean conRayas, Long idDocumentop, Long idDetalleDocumentop, Long caratulap, Integer versionp,  
			boolean esVersion, boolean esDetalle, boolean esPrincipal) throws HTTPException, Exception{

		//{conRayas}/{idDocumentop}/{idDetalleDocumentop}/{caratulap}/{versionp}/{esVersionp}/{esDetallep}/{esPrincipalp}
		String[] pathParams = {String.valueOf(conRayas), idDocumentop.toString(), idDetalleDocumentop.toString(), caratulap.toString(), 
				versionp.toString(), String.valueOf(esVersion), String.valueOf(esDetalle), String.valueOf(esPrincipal)};
		
		return (byte[])executeREST(SERVER_ESCRITURAS, PORT_ESCRITURAS, "file", "downloadEscritura", pathParams, null);
	}
	
	public JSONObject eliminarEscritura(Long idDocumentop, boolean esDetalle, boolean esPrincipal) throws HTTPException, Exception{

		//{idDocumentop}/{esDetallep}/{esPrincipalp}
		String[] pathParams = {idDocumentop.toString(), String.valueOf(esDetalle), String.valueOf(esPrincipal)};
		
		return (JSONObject)executeREST(SERVER_ESCRITURAS, PORT_ESCRITURAS, "file", "eliminarEscritura", pathParams, null);			
	}	
	
	public JSONObject documentosPorCaratula(Long caratula, String tipoBusqueda) throws HTTPException, Exception {
		//{caratula}/{tipoBusqueda}
		String[] pathParams = {caratula.toString(), tipoBusqueda};
		
		return (JSONObject)executeREST(SERVER, PORT, "caratula", "documentosPorCaratula", pathParams, null);			
	} 	 
    

	
    private Object executeREST(String server, String port, String servicio, String metodo, String[] pathParams, HashMap<String,String> queryParams) throws HTTPException, Exception
    {    	
		StringBuffer uriStrBuffer = new StringBuffer("http://"+server+":"+port+"/wsDocumentos/rest/"+servicio+"/"+metodo); 
		
		if(pathParams!=null && pathParams.length>0)
			for(String pathParam : pathParams)
				uriStrBuffer.append("/" + pathParam.trim().replaceAll(" ", "%20"));
		
		if(queryParams!=null && queryParams.size()>0){
			uriStrBuffer.append("?");
			for(String key : queryParams.keySet())
				uriStrBuffer.append(key+"=" + queryParams.get(key).trim().replaceAll(" ", "%20")+"&");
			uriStrBuffer.deleteCharAt(uriStrBuffer.length()-1);
		}
		
		Client client = Client.create();
		System.out.println("URL--->"+uriStrBuffer.toString());
		LOGGER.debug("U R L   __>"+uriStrBuffer.toString());
		
		WebResource wr = client.resource(new URI(uriStrBuffer.toString()));
		ClientResponse response = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
		
		return getResponse(response);      
    }   
    
	private Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(response.getType()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(MediaType.APPLICATION_JSON_TYPE.equals(response.getType()))
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
