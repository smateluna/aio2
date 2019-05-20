package cl.cbrs.aio.util;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jboss.logging.Logger;

import com.lowagie.text.pdf.PdfReader;

import cl.cbr.foliomercantil.vo.NotarioElectronicoVO;
import cl.cbrs.aio.notarioElectronico.NotarioElectronicoRestClient;

public class NotarioUtil{
	private static final Logger logger = Logger.getLogger(NotarioUtil.class);


	public NotarioUtil(){
	
	}	
	
	public InputStream descargaDocumento(String idNotario, String codigoDoc) throws Exception{
		
			List<NotarioElectronicoVO> notarios = NotarioElectronicoRestClient.findRepositoriosById(new Integer(idNotario));
			
			if(notarios == null || notarios.size() == 0) {									
			}else{
				URL url;				
				String protocolo = "";
				String server = "";
				String path = "";
				String args = "";
				String ws = "";
				for(NotarioElectronicoVO notarioElectronicoVO : notarios) {

					if(notarioElectronicoVO.getEmpresa().intValue() == NotarioElectronicoVO.FOJAS.intValue()){
						protocolo = ConstantesPortalConservador.getParametro("WS_FOJAS", "protocolo");
						server = ConstantesPortalConservador.getParametro("WS_FOJAS", "server");
						path = ConstantesPortalConservador.getParametro("WS_FOJAS", "path");
						args = ConstantesPortalConservador.getParametro("WS_FOJAS", "args");
						ws = ConstantesPortalConservador.getParametro("WS_FOJAS", "full");						
					} else {
						protocolo = ConstantesPortalConservador.getParametro("WS_SIRI", "protocolo");
						server = ConstantesPortalConservador.getParametro("WS_SIRI", "server");
						path = ConstantesPortalConservador.getParametro("WS_SIRI", "path");
						args = ConstantesPortalConservador.getParametro("WS_SIRI", "args");
						ws = ConstantesPortalConservador.getParametro("WS_SIRI", "full");						
					}
					
					args = args.replaceAll("_id_", ""+notarioElectronicoVO.getIdNotario());
					args = args.replaceAll("_cod_", codigoDoc);
					
					ws = ws.replaceAll("_id_", ""+notarioElectronicoVO.getIdNotario());
					ws = ws.replaceAll("_cod_", codigoDoc);			
					
					URI uri = new URI(protocolo,
							server,
							path,
							args,
							null);
					

					url = uri.toURL();

					URLConnection conn = url.openConnection();
						
					if( (conn.getContentType()==null || conn.getContentType().length()==0) || !conn.getContentType().startsWith("application/pdf") || 
							(conn.getContentType().startsWith("application/pdf") && conn.getContentLength()==0)){
						continue;
					}
					return conn.getInputStream();
				}
	
			}
		
		return null;
	}
	
	
	public int numeroDePaginas(InputStream archivo){
		PdfReader reader = null;
		
		try {
			reader = new PdfReader(archivo);
			return reader.getNumberOfPages();
		}
		catch (Exception e) {
			logger.error(e);

		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return 1;
	}
	
	public long numeroDePaginas(byte[] pdfIn){
		PdfReader reader = null;
		try {
			reader = new PdfReader(pdfIn);
			return reader.getNumberOfPages();
		}
		catch (Exception e) {
			logger.error(e);

		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return 1;
	}	
}