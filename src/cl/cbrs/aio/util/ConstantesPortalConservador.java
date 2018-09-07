package cl.cbrs.aio.util;

import cl.cbr.util.TablaValores;

public class ConstantesPortalConservador{

    /**
	 * 
	 */
	public static String PARAMETROS_PORTAL_CONSERVADOR = "portal_conservador.parametros";
    public static String PARAMETROS_WS_ENTREGA_DOCUMENTOS = "ws_EntregaDocumentos.parametros";
    
    //Constantes ID de registros para consultas
	public static final Integer REGISTRO_PROPIEDADES = 1;
	public static final Integer REGISTRO_HIPOTECAS = 2;
	public static final Integer REGISTRO_PROHIBICIONES = 3;
	public static final Integer REGISTRO_COMERCIO = 4;
	public static final Integer REGISTRO_AGUAS = 5;	

    public static String AMBIENTE;

    //URL web service busqueda en indices de propiedad
    public static String URL_WS_BUSQUEDA_PROPIEDAD;
    
//	URI uri = new URI("http",
//			"200.29.23.3",
//			"/descargaArchivo/Descarga",
//			"notario=711&codigo=EXT-130305-1720-34694",
//			null);
   
    static {
        cargarValores();
    }
    
    public static String getParametro(String parametro){
        if (AMBIENTE !=null && AMBIENTE.equals("LINUX")){
            String valor = TablaValores.getValor(PARAMETROS_PORTAL_CONSERVADOR, parametro, "valor").replaceAll("z:", "/mnt/z");
            valor = valor.replaceAll("y:", "/mnt/y");
            valor = valor.replaceAll("x:", "/mnt/x");
            valor = valor.replaceAll("\\\\", "/");
            return valor;
        }else{
            return TablaValores.getValor(PARAMETROS_PORTAL_CONSERVADOR, parametro, "valor");
        }
    }
    
    public static String getParametro(String parametro,String clave){
        if (AMBIENTE !=null && AMBIENTE.equals("LINUX")){
            String valor = TablaValores.getValor(PARAMETROS_PORTAL_CONSERVADOR, parametro, clave).replaceAll("z:", "/mnt/z");
            valor = valor.replaceAll("y:", "/mnt/y");
            valor = valor.replaceAll("x:", "/mnt/x");
            valor = valor.replaceAll("\\\\", "/");
            return valor;
        } else {
            return TablaValores.getValor(PARAMETROS_PORTAL_CONSERVADOR, parametro, clave);
        }
    }
    
    public static void cargarValores(){
        AMBIENTE = getParametro("AMBIENTE");
        URL_WS_BUSQUEDA_PROPIEDAD = getParametro("URL_WS_BUSQUEDA_PROPIEDAD");
    }
    
    public static String getParametroEntregaDocumentos(String parametro){
        if (AMBIENTE !=null && AMBIENTE.equals("LINUX")){
            String valor = TablaValores.getValor(PARAMETROS_WS_ENTREGA_DOCUMENTOS, parametro, "valor").replaceAll("z:", "/mnt/z");
            valor = valor.replaceAll("y:", "/mnt/y");
            valor = valor.replaceAll("x:", "/mnt/x");
            valor = valor.replaceAll("\\\\", "/");
            return valor;
        }else{
            return TablaValores.getValor(PARAMETROS_WS_ENTREGA_DOCUMENTOS, parametro, "valor");
        }
    }    
    
}
