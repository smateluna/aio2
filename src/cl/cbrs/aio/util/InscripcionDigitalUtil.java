package cl.cbrs.aio.util;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.json.simple.JSONObject;

import cl.cbrs.aio.documentos.DocumentosCliente;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.service.IndiceServiceAction;
import cl.cbrs.documentos.constantes.ConstantesDocumentos;
import cl.cbrs.indice.propiedad.delegate.IndicePropiedadDelegate;
import cl.cbrs.indice.propiedad.ws.vo.ExisteInscripcionVO;

public class InscripcionDigitalUtil {

	private static final Logger logger = Logger.getLogger(InscripcionDigitalUtil.class);

	public static final String PROHIBICION = "Prohibicion";
	public static final String PREFIJO_PROHIBICION = "PROH ";

	public static final String DOMINIO = "Dominio";
	public static final String PREFIJO_DOMINIO = "PROP ";

	public static final String HIPOTECA = "Hipoteca";
	public static final String PREFIJO_HIPOTECA = "HIPO ";

	public InscripcionDigitalUtil(){

	}

	public boolean existeResultado(ArrayList<ConsultaDocumentoDTO> estados, String firma){

		if(estados!=null && estados.size()>0){

			for(ConsultaDocumentoDTO estado: estados){

				if(estado.getFirma().equals(firma)){
					return true;
				}
			}
		}		
		return false;
	}

	public Boolean consultaIndice(Long foja, String numero, Long ano){
		Boolean fna = false;
		IndicePropiedadDelegate delegate= new IndicePropiedadDelegate();

		ExisteInscripcionVO consulta = new ExisteInscripcionVO();
		consulta.setFoja(foja.intValue());
		consulta.setNumero(numero);
		consulta.setAno(ano.intValue());
		consulta.setRegistro(1);

		try {
			if("AIO".equals(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA")))
				fna = delegate.existeRegistro(consulta);
			else 
				fna = true;
		} catch (cl.cbr.common.exception.GeneralException e) {
			logger.error(e.getMessage(),e);
		}			
		return fna;
	}

	public Boolean consultaIndiceProhibiciones(Long foja, String numero, Long ano){
		Boolean fna = false;

		if("AIO".equals(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"))){

				IndiceServiceAction indiceServiceAction = new IndiceServiceAction();
				String SOLR_PROH_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRPROH", "endpoint");
				String url = SOLR_PROH_ENDPOINT;
				SolrServer server = new HttpSolrServer( url );
				String q = "";

				q = indiceServiceAction.getQueryProhibiciones(null, null, foja.intValue(), Integer.parseInt(numero), ano.intValue(),"true","false");

//				System.out.println("query solr Proh: "+q);

				SolrQuery query = new SolrQuery();

				query.set("q", q);

				Integer SOLR_ROWS = 50;
				try {
					SOLR_ROWS = Integer.parseInt(ConstantesPortalConservador.getParametro("SOLR", "rows"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}					
				
				query.setRows(SOLR_ROWS);
				//fin mejora

				try {
					QueryResponse queryResponse = server.query( query );

					NamedList<Object> responseHeader = queryResponse.getResponseHeader();

					Integer status = (Integer) responseHeader.get("status");

					if(status!=null && status==0){	
						if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
							fna=true;
						}
					}

				} catch (SolrServerException e) {
					e.printStackTrace();
				}

		}else{ 
			fna = true;
		}			
		return fna;
	}
	
	public Boolean consultaIndiceHipoteca(Long foja, String numero, Long ano){
		Boolean fna = false;

		if("AIO".equals(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"))){

				IndiceServiceAction indiceServiceAction = new IndiceServiceAction();
				String SOLR_HIPO_ENDPOINT = ConstantesPortalConservador.getParametro("SOLRHIPO", "endpoint");
				String url = SOLR_HIPO_ENDPOINT;
				SolrServer server = new HttpSolrServer( url );
				String q = "";

				q = indiceServiceAction.getQueryHipoteca(null, null, foja.intValue(), Integer.parseInt(numero), ano.intValue(),"true","false");

//				System.out.println("query solr Hipo: "+q);

				SolrQuery query = new SolrQuery();

				query.set("q", q);

				Integer SOLR_ROWS = 50;
				try {
					SOLR_ROWS = Integer.parseInt(ConstantesPortalConservador.getParametro("SOLR", "rows"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}					
				
				query.setRows(SOLR_ROWS);
				//fin mejora

				try {
					QueryResponse queryResponse = server.query( query );

					NamedList<Object> responseHeader = queryResponse.getResponseHeader();

					Integer status = (Integer) responseHeader.get("status");

					if(status!=null && status==0){	
						if(queryResponse.getResults()!=null && queryResponse.getResults().getNumFound()>0){
							fna=true;
						}
					}

				} catch (SolrServerException e) {
					e.printStackTrace();
				}

		}else{ 
			fna = true;
		}			
		return fna;
	}

	public ConsultaDocumentoDTO getConsultaDocumentoDTO(Long foja, Long numero, Long ano, Boolean bis, Integer idRegistro){
		ConsultaDocumentoDTO dto = null;

		try{
			DocumentosCliente documentosCliente = new DocumentosCliente();
			JSONObject json = documentosCliente.existeInscripcion(foja.intValue(), numero.intValue(), ano.intValue(), bis, idRegistro);

			if(json!=null){      
				dto = new ConsultaDocumentoDTO();
				if(json.get("cantidadPaginas")!=null)
					dto.setCantidadPaginas(((Long)json.get("cantidadPaginas")).intValue());
				if(json.get("hayDocumento")!=null)
					dto.setHayDocumento((Boolean)json.get("hayDocumento"));
				if(json.get("tieneNotas")!=null)
					dto.setTieneNotas((Boolean)json.get("tieneNotas"));
				if(json.get("idTipoDocumento")!=null)
					dto.setTipoDocumento(((Long)json.get("idTipoDocumento")).intValue()); 
				if(json.get("fechaArchivo")!=null){
					Date date=new Date((Long)json.get("fechaArchivo"));
					dto.setFechaArchivo(date);
				}

				//					dto.setTipoDocumento(((Long)json.get("fechaArchivo")).intValue());

				if(dto.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_VERSIONADO){
					dto.setPuedeSolicitar(false);
				}else if(dto.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_REFERENCIAL || 
						dto.getTipoDocumento() == ConstantesDocumentos.ID_TIPO_DOCUMENTO_INSCRIPCION_ORIGINAL){
					dto.setPuedeSolicitar(true);
				}else{
					dto.setPuedeSolicitar(false);
				}

				dto.setFoja(foja);
				dto.setNumero(numero);
				dto.setAno(ano);
				dto.setBis(bis);
			}                            

		}catch (HTTPException e) {
			logger.error("Error HTTP codigo " + e.getStatusCode(), e);
		}catch (Exception e){
			logger.error("Error al consultar documento: " + e.getMessage(),e);
		}
		return dto;
	}	

}