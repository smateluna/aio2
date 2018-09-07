package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.ErroresUtil;
import cl.cbr.util.TablaValores;

public abstract class AbstractJdbcDao {
	
	Logger logger = Logger.getLogger(AbstractJdbcDao.class);
	
	//Flag que indica si conexiones ya fueron configuradas
	private static Boolean configOK = false;
	
	//Archivo de parametros de conexion
	public static final String ARCHIVO_CONEXION = "conexiones.properties";
	
    //Valores para acceder a base de datos de FLUJO (ALPHA)
    private static String IP_FLUJO;
    private static String USERNAME_FLUJO;
    private static String PASSWORD_FLUJO;
    private static String INSTANCIA;
    

    /**
     * Retorna conexion a base de datos 'Flujo'
     * @return
     * @throws Exception
     */
    protected Connection conexionFlujo() throws GeneralException{
    	
    	Connection conexion = null;
    	try {
    		
    	configurarConexiones();
        
    	String str = "jdbc:jtds:sqlserver://" + IP_FLUJO + ":1433;databaseName=flujo"+INSTANCIA;
        //System.out.println(str);
        //Registrar el driver JDBC usando el cargador de clases Class.forName
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
        
        //Abrir conexion
        conexion = DriverManager.getConnection(str, USERNAME_FLUJO, PASSWORD_FLUJO);
        
    	} catch (Exception e) {
			logger.error(ErroresUtil.extraeStackTrace(e));
			throw new GeneralException("CARATULA_200", "Imposible obtener conexion a Flujo");
		}
		return conexion;
        
    }
    
    /**
     * Configura valores de conexion base de datos segun corresponda
     */
    private void configurarConexiones(){
    	if(configOK){
    		return;
    	}
    	
    	//Cargar valores base datos
    	IP_FLUJO = TablaValores.getValor(ARCHIVO_CONEXION, "ALPHA", "valor");
    	USERNAME_FLUJO = TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FLUJO", "username");
        PASSWORD_FLUJO = TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FLUJO", "password");
        INSTANCIA=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FLUJO", "instancia");
        if (INSTANCIA!=null&&!INSTANCIA.equals("")){
        	INSTANCIA=";instance="+INSTANCIA;
        }
        //Configuracion ok
        configOK = true;
    }
}
