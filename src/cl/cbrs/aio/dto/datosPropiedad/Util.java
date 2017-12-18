package cl.cbrs.aio.dto.datosPropiedad;

import cl.cbr.util.TablaValores;

public class Util {
	
	public static String getFilenamePdf(String prefijo, Object fojas, Object numero, Object anyo){
		return prefijo + fojas + "_" + numero + "_" + anyo + Constants.PDF;
	}
	
	public static String getFilenamePdf2(String prefijo, Object fojas, Object numero, Object anyo){
		return prefijo + fojas + "_ " + numero + "_ " + anyo + Constants.PDF;
	}
	
	public static String getPrefijoPorTipo(String tipo){
		if(Constants.PROHIBICION.equals(tipo))
			return Constants.PREFIJO_PROHIBICION;
		
		if(Constants.HIPOTECA.equals(tipo))
			return Constants.PREFIJO_HIPOTECA;
		
		if(Constants.DOMINIO.equals(tipo))
			return Constants.PREFIJO_DOMINIO;
		
		return "";
	}
	
	public static String rtf2html(String in){
		in = in.replace("{\\cf2", "");
		in = in.replace(" }", "");
		
		return in;
	}
	
	public static String getUrlPlano(int numPlano, int categoria) throws Exception{
		
		return TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "HOSTS", "HOST_PLANOS")+"/PlanosWeb/index.jsp?plano="+numPlano+"&categoria="+categoria+"&op=1";

	}
}
