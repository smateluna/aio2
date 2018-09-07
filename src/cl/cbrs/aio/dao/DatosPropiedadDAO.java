package cl.cbrs.aio.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.BussinesException;
import cl.cbr.util.ErroresUtil;
import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.datosPropiedad.CaratulaDTO;
import cl.cbrs.aio.dto.datosPropiedad.Constants;
import cl.cbrs.aio.dto.datosPropiedad.EventoDTO;
import cl.cbrs.aio.dto.datosPropiedad.GP_DTO;
import cl.cbrs.aio.dto.datosPropiedad.HipotecaDTO;
import cl.cbrs.aio.dto.datosPropiedad.NotasDTO;
import cl.cbrs.aio.dto.datosPropiedad.ProhibicionDTO;
import cl.cbrs.aio.dto.datosPropiedad.PropiedadDTO;
import cl.cbrs.aio.dto.datosPropiedad.QuiebraDTO;
import net.sourceforge.jtds.jdbc.Driver;

public class DatosPropiedadDAO {
	
	
	private static Logger logger = Logger.getLogger(DatosPropiedadDAO.class); 
	static{
	System.setProperty("jboss.server.home.dir", "C:\\jboss-5.1.0.GA\\server\\default");}
	static final String DB_FOLIO_REAL= "Folio_Real";
	static final String DB_FIRMA_ELECTRONICA= "Firma_E";
	static final String DB_INDICES= "indices";
	static final String ARCHIVO_CONEXION = "conexiones.properties";
	static String FOLIO_URL="jdbc:jtds:sqlserver://"+TablaValores.getValor(ARCHIVO_CONEXION, "FOLIO_REAL", "valor")+":1433;databaseName=";	
	static String FIRMA_URL="jdbc:jtds:sqlserver://"+TablaValores.getValor(ARCHIVO_CONEXION, "FIRMA_E", "valor")+":1433;databaseName=";
	static String INDICES_URL="jdbc:jtds:sqlserver://"+TablaValores.getValor(ARCHIVO_CONEXION, "INDICES", "valor")+":1433;databaseName=";
	static String FOLIO_USERNAME=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FOLIO_REAL", "username");
	static String FOLIO_PASSWORD=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FOLIO_REAL", "password");
	static String FIRMA_USERNAME=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FIRMA_E", "username");
	static String FIRMA_PASSWORD=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_FIRMA_E", "password");
	static String INDICES_USERNAME=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_INDICES", "username");
	static String INDICES_PASSWORD=TablaValores.getValor(ARCHIVO_CONEXION, "USUARIO_INDICES", "password");
	
	static String LINK_DB_FLUJO=TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "HOSTS", "DB_FLUJO") != null?
			TablaValores.getValor(Constants.ARCHIVO_PROPERTIES, "HOSTS", "DB_FLUJO") : "";
	
	
	private static Connection getConnection(String url, String usuario, String clave) throws SQLException{
        Connection con=null;
        try{
        	Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con= DriverManager.getConnection(url, usuario, clave);
        }
        catch(SQLException sqle){
            logger.error(ErroresUtil.extraeStackTrace(sqle));
            throw sqle;
        }
        catch(ClassNotFoundException cnfe){
            logger.error(ErroresUtil.extraeStackTrace(cnfe));
        }
        return con;
    }
	
	private static Connection getConnectionFolioReal() throws SQLException{
        return getConnection(FOLIO_URL+DB_FOLIO_REAL, FOLIO_USERNAME, FOLIO_PASSWORD);
	}
	
	private static Connection getConnectionFirmaE() throws SQLException{
        return getConnection(FIRMA_URL+DB_FIRMA_ELECTRONICA, FIRMA_USERNAME, FIRMA_PASSWORD);
	}
	
	private static Connection getConnectionIndices() throws SQLException{
        return getConnection(INDICES_URL+DB_INDICES, INDICES_USERNAME, INDICES_PASSWORD);
	}
	
	public static ArrayList<CaratulaDTO> buscarCaratulasEnProceso(int borrador, int folio) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<CaratulaDTO> listaCaratulas = new ArrayList<CaratulaDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BUSCA_CARATULA_ENPROCESO_BF(?,?)}");
			call.setLong(1, borrador);
			call.setLong(2, folio);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				CaratulaDTO caratulaDTO = new CaratulaDTO();
				caratulaDTO.setFechaEnvio( rs.getTimestamp("FECHA_ENVIO") );
				caratulaDTO.setCaratula( rs.getInt("CARATULA") );
				
				listaCaratulas.add(caratulaDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER CARATULA");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaCaratulas;
	}
	
	public static ArrayList<String> buscarRoles(int borrador, int folio) throws GeneralException{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<String> listaRoles = new ArrayList<String>();
		final String sql = "select ROL_SII from BORRADOR_ROL where BORRADOR=? and FOLIO=? ";
		
		try{
			conn = getConnectionFolioReal();
			
			pstm = conn.prepareStatement(sql);
			pstm.setLong(1, borrador);
			pstm.setLong(2, folio);					
			rs = pstm.executeQuery();
			
			while(rs.next()){ 
				String rol = valorSinNull(rs.getString("ROL_SII"));				
				listaRoles.add(rol);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER ROL");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(pstm!=null){try{pstm.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaRoles;
	}
	
	public static ArrayList<EventoDTO> buscarEventos(int borrador, int folio) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<EventoDTO> listaEventos = new ArrayList<EventoDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call ULTIMOS_EVENTOS_BORRADOR(?,?)}");
			call.setLong(1, borrador);
			call.setLong(2, folio);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				EventoDTO eventoDTO = new EventoDTO();
				eventoDTO.setFecha( rs.getTimestamp("time_stamp") );
				eventoDTO.setAplicacion( valorSinNull(rs.getString("APLICACION")) );
				eventoDTO.setCaratula( valorSinNull(rs.getString("caratula")) );
				eventoDTO.setEvento( valorSinNull(rs.getString("EVENTO")) );
				eventoDTO.setFuncionario( valorSinNull(rs.getString("Funcionario")) );
				
				listaEventos.add(eventoDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER EVENTO");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaEventos;
	}
	
	public static ArrayList<GP_DTO> buscarUltimosGP(int folio, int borrador) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<GP_DTO> listaGP = new ArrayList<GP_DTO>();
		
		try{
			conn = getConnectionFirmaE();
			
			call = conn.prepareCall("{call BUSCA_ULTIMO_GP_BF(?,?)}");
			call.setLong(1, borrador);
			call.setLong(2, folio);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				GP_DTO gpDTO = new GP_DTO();
				gpDTO.setFechaGP( rs.getTimestamp("FECHA_FIRMA") );
				gpDTO.setCaratula( rs.getInt("CARATULA") );
				gpDTO.setNombreArchivoVersion( valorSinNull(rs.getString("NOMBRE_ARCHIVO_VERSION")) );
				gpDTO.setCodArchivoAlpha( valorSinNull(rs.getString("COD_ARCHIVO_ALPHA")) );
				
				listaGP.add(gpDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROPIEDAD");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaGP;
	}
	
	public static Timestamp buscarUltimoGP(int folio, int borrador) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		Timestamp time = null;
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BUSCA_HIST_GP_BF(?,?)}");
			call.setLong(1, borrador);
			call.setLong(2, folio);			
			rs = call.executeQuery();
			
			if(rs.next()){ 
				time = rs.getTimestamp("FECHA_DIG");
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROPIEDAD");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return time;
	}
	
	public static ArrayList<PropiedadDTO> buscarPropiedad(int folio, int borrador) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList<PropiedadDTO> listaPropiedades = new ArrayList<PropiedadDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call REC_FOLIO_N_GP(?,?)}");
			call.setLong(1, folio);
			call.setLong(2, borrador);			
			rs = call.executeQuery();
			
			while(rs.next()){
				PropiedadDTO propiedadDTO = new PropiedadDTO(); 
				propiedadDTO.setFolio( rs.getInt("FOLIO") );
				propiedadDTO.setBorrador( rs.getInt("BORRADOR") );
				propiedadDTO.setDigito( rs.getInt("DIGITO") );
				propiedadDTO.setFoja( rs.getInt("FOJA_P") );
				propiedadDTO.setNumero( rs.getInt("NUMERO_P") );
				propiedadDTO.setAnyo( rs.getInt("ANO_P") );
				propiedadDTO.setBis( rs.getInt("BIS_P") );
				if(rs.getObject("VIGENTE_T")==null)
					propiedadDTO.setVigenteT( 1 );
				else
					propiedadDTO.setVigenteT( rs.getInt("VIGENTE_T") );
				propiedadDTO.setVigente( rs.getInt("VIGENTE") );
				propiedadDTO.setTipoCliente( valorSinNull(rs.getString("TIPO_CLI")) );
				propiedadDTO.setOrden( rs.getInt("ORDEN") );
				propiedadDTO.setRut( valorSinNull(rs.getString("RUT")) );
				propiedadDTO.setApPaternoCli( valorSinNull(rs.getString("AP_PATERNO_CLI")) );
				propiedadDTO.setApMaternoCli( valorSinNull(rs.getString("AP_MATERNO_CLI")) );
				propiedadDTO.setNombreCli( valorSinNull(rs.getString("NOMBRES_CLI")) );
				propiedadDTO.setDireccion( valorSinNull(rs.getString("DIR")) );
				propiedadDTO.setComuna( valorSinNull(rs.getString("DESC_COM")) );
				propiedadDTO.setDeslindes( valorSinNull(rs.getString("DESLINDE")) ); 
				propiedadDTO.setNaturaleza( valorSinNull(rs.getString("DESC_NAT")));
				
				call = conn.prepareCall("{call BUSCA_NOTAS_HIP_BFFNA(?,?,?,?,?,?)}");
				call.setInt(1, borrador);
				call.setInt(2, folio);
				call.setInt(3, propiedadDTO.getFoja());
				call.setInt(4, propiedadDTO.getNumero());
				call.setInt(5, propiedadDTO.getAnyo());
				call.setInt(6, propiedadDTO.getBis());			
				rs2 = call.executeQuery();
				
				ArrayList<String> listaNotas = new ArrayList<String>();
				while(rs2.next()){
					listaNotas.add(valorSinNull(rs2.getString("DETALLE")));
				}				
				propiedadDTO.setListaNotas(listaNotas);
				
				if(rs2!=null) 
					rs2.close();
				
				listaPropiedades.add(propiedadDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROPIEDAD");
		}finally{
			if(rs2!=null) {try{rs2.close();}catch(Exception e){}}
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaPropiedades;
		
	}
	
	public static ArrayList<HipotecaDTO> buscarHipotecas(int folio, int borrador, int anyos, Date fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList<HipotecaDTO> listaHipotecas = new ArrayList<HipotecaDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call REC_HIPO(?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, anyos);
			call.setDate(4, new java.sql.Date(fecha.getTime()));			
			rs = call.executeQuery();
			
			while(rs.next()){
				HipotecaDTO hipotecaDTO = new HipotecaDTO(); 
				hipotecaDTO.setFojas( rs.getInt("FOJA_H") );
				hipotecaDTO.setNumero( rs.getInt("NUMERO_H") );
				hipotecaDTO.setAnyo( rs.getInt("ANO_H") );
				hipotecaDTO.setNaturaleza( valorSinNull(rs.getString("DESC_NAT")) );
				hipotecaDTO.setCodNaturaleza( rs.getInt("COD_NAT") );
				hipotecaDTO.setEstado( valorSinNull(rs.getString("ESTADO")) );
				hipotecaDTO.setCaratula(rs.getLong("CARATULA") );
				
				StringBuffer nombreCompleto = new StringBuffer();
				nombreCompleto.append(valorSinNull(rs.getString("AP_PATERNO_CLI")));				
				String apMaterno = valorSinNull(rs.getString("AP_MATERNO_CLI"));				
				nombreCompleto.append(" " + apMaterno);
				String nombre = valorSinNull(rs.getString("NOMBRES_CLI"));
				if ( !"".equals(nombreCompleto.toString().trim()) && !"".equals(nombre) )
					nombreCompleto.append(", ");
				nombreCompleto.append(nombre);
				
				if( nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR)>0 || nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR.toUpperCase())>0 )
					hipotecaDTO.setAcreedor("");
				else
					hipotecaDTO.setAcreedor( nombreCompleto.toString() );
				
				
				hipotecaDTO.setResumen( valorSinNull(rs.getString("RESUMEN")) );
				hipotecaDTO.setBis( rs.getInt("BIS") );
				hipotecaDTO.setFechaEstado(rs.getDate("FECHA_EST"));
				
				call = conn.prepareCall("{call BUSCA_NOTAS_HIP_BFFNA_AIO(?,?,?,?,?,?)}");
				call.setInt(1, borrador);
				call.setInt(2, folio);
				call.setInt(3, hipotecaDTO.getFojas());
				call.setInt(4, hipotecaDTO.getNumero());
				call.setInt(5, hipotecaDTO.getAnyo());
				call.setInt(6, hipotecaDTO.getBis());			
				rs2 = call.executeQuery();
				
				ArrayList<NotasDTO> listaNotas = new ArrayList<NotasDTO>();
				while(rs2.next()){
					NotasDTO notasDTO = new NotasDTO();
					notasDTO.setCaratula(valorSinNull(rs2.getString("CARATULA")));
					notasDTO.setNota(valorSinNull(rs2.getString("DETALLE")));
					listaNotas.add(notasDTO);
				}				
				hipotecaDTO.setListaNotas(listaNotas);
				
				if(rs2!=null) 
					rs2.close();
				
				listaHipotecas.add(hipotecaDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER HIPOTECAS");
		}finally{
			if(rs2!=null) {try{rs2.close();}catch(Exception e){}}
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaHipotecas;		
	}
	
	public static ArrayList<HipotecaDTO> buscarHipotecasNoVigentes(int folio, int borrador, int anyos, Date fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList<HipotecaDTO> listaHipotecas = new ArrayList<HipotecaDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call REC_HIPO_NO_V(?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, anyos);
			call.setDate(4, new java.sql.Date(fecha.getTime()));			
			rs = call.executeQuery();
			
			while(rs.next()){
				HipotecaDTO hipotecaDTO = new HipotecaDTO(); 
				hipotecaDTO.setFojas( rs.getInt("FOJA_H") );
				hipotecaDTO.setNumero( rs.getInt("NUMERO_H") );
				hipotecaDTO.setAnyo( rs.getInt("ANO_H") );
				hipotecaDTO.setNaturaleza( valorSinNull(rs.getString("DESC_NAT")) );
				hipotecaDTO.setCodNaturaleza( rs.getInt("COD_NAT") );
				hipotecaDTO.setEstado( valorSinNull(rs.getString("ESTADO")) );
				hipotecaDTO.setCaratula( rs.getLong("CARATULA") );
				
				StringBuffer nombreCompleto = new StringBuffer();
				nombreCompleto.append(valorSinNull(rs.getString("AP_PATERNO_CLI")));				
				String apMaterno = valorSinNull(rs.getString("AP_MATERNO_CLI"));				
				nombreCompleto.append(" " + apMaterno);
				String nombre = valorSinNull(rs.getString("NOMBRES_CLI"));
				if ( !"".equals(nombreCompleto.toString().trim()) && !"".equals(nombre) )
					nombreCompleto.append(", ");
				nombreCompleto.append(nombre);
				
				if( nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR)>0 || nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR.toUpperCase())>0 )
					hipotecaDTO.setAcreedor("");
				else
					hipotecaDTO.setAcreedor( nombreCompleto.toString() );
				
				
				hipotecaDTO.setResumen( valorSinNull(rs.getString("RESUMEN")) );
				hipotecaDTO.setBis( rs.getInt("BIS") );
				hipotecaDTO.setFechaEstado(rs.getDate("FECHA_EST"));
				
				call = conn.prepareCall("{call BUSCA_NOTAS_HIP_BFFNA_AIO(?,?,?,?,?,?)}");
				call.setInt(1, borrador);
				call.setInt(2, folio);
				call.setInt(3, hipotecaDTO.getFojas());
				call.setInt(4, hipotecaDTO.getNumero());
				call.setInt(5, hipotecaDTO.getAnyo());
				call.setInt(6, hipotecaDTO.getBis());			
				rs2 = call.executeQuery();
				
				ArrayList<NotasDTO> listaNotas = new ArrayList<NotasDTO>();
				while(rs2.next()){
					NotasDTO notasDTO = new NotasDTO();
					notasDTO.setCaratula(valorSinNull(rs2.getString("CARATULA")));
					notasDTO.setNota(valorSinNull(rs2.getString("DETALLE")));
					listaNotas.add(notasDTO);
				}				
				hipotecaDTO.setListaNotas(listaNotas);
				
				if(rs2!=null) 
					rs2.close();
				
				listaHipotecas.add(hipotecaDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER HIPOTECAS");
		}finally{
			if(rs2!=null) {try{rs2.close();}catch(Exception e){}}
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaHipotecas;		
	}	
	
	public static int buscarCancelacionesHipotecas(int folio, int borrador, int fojas, int numero, int anyo, int bis, String fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		int res=0;
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BUSCA_CAN_HI_FOLIO_5(?,?,?,?,?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, fojas);
			call.setInt(4, numero);
			call.setInt(5, anyo);
			call.setInt(6, bis);
			call.setString(7, fecha);
			call.registerOutParameter(8, java.sql.Types.INTEGER);
			call.execute();
			
			res = call.getInt(8);
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER CANCELACIONES HIPOTECAS");
		}finally{
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return res;
		
	}	
	
	public static int buscarCancelacionesProhibiciones(int folio, int borrador, int fojas, int numero, int anyo, String fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		int res=0;
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BUSCA_CAN_PH_FOLIO_4(?,?,?,?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, fojas);
			call.setInt(4, numero);
			call.setInt(5, anyo);
			call.setString(6, fecha);
			call.registerOutParameter(7, java.sql.Types.INTEGER);
			call.execute();
			
			res = call.getInt(7);
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER CANCELACIONES HIPOTECAS");
		}finally{
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return res;
		
	}
	
	public static ArrayList<Integer> buscaFoliosPorBorrador(int borrador) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<Integer> listaFolios = new ArrayList<Integer>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BUSCA_X_BORRADOR(?)}");
			call.setInt(1, borrador);
			call.execute();
			
			rs = call.executeQuery();
			
			while(rs.next()){
				listaFolios.add(rs.getInt("FOLIO"));
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER FOLIOS POR BORRADOR");
		}finally{
			if(rs!=null){try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaFolios;
		
	}	
	
	public static ArrayList<ProhibicionDTO> buscarProhibiciones(int folio, int borrador, int anyos, Date fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList<ProhibicionDTO> listaProhibiciones = new ArrayList<ProhibicionDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call REC_PROH(?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, anyos);
			call.setDate(4, new java.sql.Date(fecha.getTime()));	
			rs = call.executeQuery();
			
			while(rs.next()){
				ProhibicionDTO prohibicionDTO = new ProhibicionDTO(); 
				prohibicionDTO.setFojas( rs.getInt("FOJA_PH") );
				prohibicionDTO.setNumero( rs.getInt("NUMERO_PH") );
				prohibicionDTO.setAnyo( rs.getInt("ANO_PH") );
				prohibicionDTO.setEstado( valorSinNull(rs.getString("ESTADO")) );
				prohibicionDTO.setNaturaleza( valorSinNull(rs.getString("DESC_NAT")) );
				prohibicionDTO.setSigla( valorSinNull(rs.getString("SIGLA")) );
				prohibicionDTO.setCodNaturaleza( rs.getInt("COD_NAT") );
				prohibicionDTO.setTipoAcreedor( rs.getString("TIPO1") );
				prohibicionDTO.setCaratula( rs.getLong("CARATULA") );

				StringBuffer nombreCompleto = new StringBuffer();
				nombreCompleto.append(valorSinNull(rs.getString("AP_PATERNO_CLI")));				
				String apMaterno = valorSinNull(rs.getString("AP_MATERNO_CLI"));				
				nombreCompleto.append(" " + apMaterno);
				String nombre = valorSinNull(rs.getString("NOMBRES_CLI"));
				if ( !"".equals(nombreCompleto.toString().trim()) && !"".equals(nombre) )
					nombreCompleto.append(", ");
				nombreCompleto.append(nombre);
				
				if( nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR)>0 || nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR.toUpperCase())>0 )
					prohibicionDTO.setAcreedor("");
				else
					prohibicionDTO.setAcreedor( nombreCompleto.toString() );
				
				prohibicionDTO.setResumen( valorSinNull(rs.getString("RESUMEN")) );
				prohibicionDTO.setBis( rs.getInt("BIS_PH") );
				prohibicionDTO.setFechaEstado(rs.getDate("FECHA_EST"));
				
				call = conn.prepareCall("{call BUSCA_NOTAS_PRH_BFFNA_AIO(?,?,?,?,?,?)}");
				call.setInt(1, borrador);
				call.setInt(2, folio);
				call.setInt(3, prohibicionDTO.getFojas());
				call.setInt(4, prohibicionDTO.getNumero());
				call.setInt(5, prohibicionDTO.getAnyo());
				call.setInt(6, prohibicionDTO.getBis());			
				rs2 = call.executeQuery();
				
				ArrayList<NotasDTO> listaNotas = new ArrayList<NotasDTO>();
				while(rs2.next()){
					NotasDTO notasDTO = new NotasDTO();
					notasDTO.setCaratula(valorSinNull(rs2.getString("CARATULA")));
					notasDTO.setNota(valorSinNull(rs2.getString("DETALLE")));
					listaNotas.add(notasDTO);
				}
				prohibicionDTO.setListaNotas(listaNotas);
				
				
				if(rs2!=null) 
					rs2.close();
				
				listaProhibiciones.add(prohibicionDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROHIBICION");
		}finally{
			if(rs2!=null) {try{rs2.close();}catch(Exception e){}}
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaProhibiciones;
		
	}
	
	public static ArrayList<ProhibicionDTO> buscarProhibicionesNoVigentes(int folio, int borrador, int anyos, Date fecha) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList<ProhibicionDTO> listaProhibiciones = new ArrayList<ProhibicionDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call REC_PROH_NO_V(?,?,?,?)}");
			call.setInt(1, folio);
			call.setInt(2, borrador);
			call.setInt(3, anyos);
			call.setDate(4, new java.sql.Date(fecha.getTime()));
			rs = call.executeQuery();
			
			while(rs.next()){
				ProhibicionDTO prohibicionDTO = new ProhibicionDTO(); 
				prohibicionDTO.setFojas( rs.getInt("FOJA_PH") );
				prohibicionDTO.setNumero( rs.getInt("NUMERO_PH") );
				prohibicionDTO.setAnyo( rs.getInt("ANO_PH") );
				prohibicionDTO.setEstado( valorSinNull(rs.getString("ESTADO")) );
				prohibicionDTO.setNaturaleza( valorSinNull(rs.getString("DESC_NAT")) );
				prohibicionDTO.setSigla( valorSinNull(rs.getString("SIGLA")) );
				prohibicionDTO.setCodNaturaleza( rs.getInt("COD_NAT") );
				prohibicionDTO.setTipoAcreedor( rs.getString("TIPO1") );
				prohibicionDTO.setCaratula( rs.getLong("CARATULA") );

				StringBuffer nombreCompleto = new StringBuffer();
				nombreCompleto.append(valorSinNull(rs.getString("AP_PATERNO_CLI")));				
				String apMaterno = valorSinNull(rs.getString("AP_MATERNO_CLI"));				
				nombreCompleto.append(" " + apMaterno);
				String nombre = valorSinNull(rs.getString("NOMBRES_CLI"));
				if ( !"".equals(nombreCompleto.toString().trim()) && !"".equals(nombre) )
					nombreCompleto.append(", ");
				nombreCompleto.append(nombre);
				
				if( nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR)>0 || nombreCompleto.toString().indexOf(Constants.SIN_ACREEDOR.toUpperCase())>0 )
					prohibicionDTO.setAcreedor("");
				else
					prohibicionDTO.setAcreedor( nombreCompleto.toString() );
				
				prohibicionDTO.setResumen( valorSinNull(rs.getString("RESUMEN")) );
				prohibicionDTO.setBis( rs.getInt("BIS_PH") );
				prohibicionDTO.setFechaEstado(rs.getDate("FECHA_EST"));
				
				call = conn.prepareCall("{call BUSCA_NOTAS_PRH_BFFNA_AIO(?,?,?,?,?,?)}");
				call.setInt(1, borrador);
				call.setInt(2, folio);
				call.setInt(3, prohibicionDTO.getFojas());
				call.setInt(4, prohibicionDTO.getNumero());
				call.setInt(5, prohibicionDTO.getAnyo());
				call.setInt(6, prohibicionDTO.getBis());			
				rs2 = call.executeQuery();
				
				ArrayList<NotasDTO> listaNotas = new ArrayList<NotasDTO>();
				while(rs2.next()){
					NotasDTO notasDTO = new NotasDTO();
					notasDTO.setCaratula(valorSinNull(rs2.getString("CARATULA")));
					notasDTO.setNota(valorSinNull(rs2.getString("DETALLE")));
					listaNotas.add(notasDTO);
				}
				prohibicionDTO.setListaNotas(listaNotas);
				
				if(rs2!=null) 
					rs2.close();
				
				listaProhibiciones.add(prohibicionDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROHIBICION");
		}finally{
			if(rs2!=null) {try{rs2.close();}catch(Exception e){}}
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaProhibiciones;
		
	}	
	
	public static ArrayList<QuiebraDTO> buscarQuiebraInterdiccionPorNombre(String nombre) throws GeneralException{
		
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<QuiebraDTO> res = new ArrayList<QuiebraDTO>();
		
		try{
			conn = getConnectionIndices();
			
			call = conn.prepareCall("{call BUSCA_IND_PROH_NOM_ESTUDIANTES(?,?)}");
			call.setString(1, nombre);
			call.setInt(2, 0);
			rs = call.executeQuery();
			
			while(rs.next()){
				QuiebraDTO quiebraDTO = new QuiebraDTO();
				quiebraDTO.setTipo(valorSinNull(rs.getString("DESC_NATURALEZA")));
				quiebraDTO.setFoja(rs.getInt("FOJA_PH"));
				quiebraDTO.setNumero(rs.getInt("NUMERO_PH"));
				quiebraDTO.setAno(rs.getInt("AÑO_PH"));
				quiebraDTO.setNombres(valorSinNull(rs.getString("NOMBRES")));
				
				res.add(quiebraDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER QUIEBRES");
		}finally{
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return res;
		
	}	
	
	public static ArrayList<PropiedadDTO> buscarBorradoresPorTitulo(Integer fojas, Integer numero, Integer anyo, Boolean bis) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<PropiedadDTO> listaBorradores = new ArrayList<PropiedadDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BORRADOR_X_TITULO_PROP(?,?,?,?)}");
			call.setInt(1, fojas);
			call.setInt(2, numero);
			call.setInt(3, anyo);
			call.setBoolean(4, bis);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				PropiedadDTO propiedadDTO = new PropiedadDTO();
				propiedadDTO.setFolio( rs.getInt("FOLIO") );
				propiedadDTO.setBorrador( rs.getInt("BORRADOR") );
				propiedadDTO.setFoja( rs.getInt("FOJA_P") );
				propiedadDTO.setNumero( rs.getInt("NUMERO_P") );
				propiedadDTO.setAnyo( rs.getInt("ANO_P") );
				propiedadDTO.setBis( rs.getInt("BIS_P"));
				
				listaBorradores.add(propiedadDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER BORRADORES POR PROPIEDAD");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaBorradores;
	}	
	
	
	public static ArrayList<PropiedadDTO> buscarPropiedadesPorCaratula(Long caratula) throws GeneralException{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<PropiedadDTO> listaProp = new ArrayList<PropiedadDTO>();
		final String sql = "SELECT DISTINCT m.CARATULA, m.BORRADOR, m.FOLIO  " + 
							"FROM Folio_Real.dbo.MAE_OT AS m  " + 
							"LEFT JOIN "+LINK_DB_FLUJO+"flujo.dbo.MAE_FORMULARIO AS f ON m.CARATULA = f.CARATULA " +  
							"INNER JOIN "+LINK_DB_FLUJO+"flujo.dbo.ESTADO_ACTUAL_CARATULA AS e ON e.CARATULA = f.CARATULA " +  
							"INNER JOIN "+LINK_DB_FLUJO+"Folio_Real.dbo.PROREAL pro ON m.FOLIO = pro.FOLIO AND m.BORRADOR = pro.BORRADOR " +  
							"LEFT JOIN "+LINK_DB_FLUJO+"flujo.dbo.ENCABEZADO_CARATULA ec ON ec.CARATULA = f.CARATULA " +  
							"LEFT JOIN "+LINK_DB_FLUJO+"flujo.dbo.TAB_FORMULARIOS tf ON tf.TIPO_FORM = f.TIPO_FORM " +  
							"LEFT JOIN "+LINK_DB_FLUJO+"flujo.dbo.TAB_SECCIONES TS ON TS.COD_SECCION = e.COD_SECCION " +  
							"WHERE m.CARATULA=? " + 
							"order by m.BORRADOR, m.folio desc";
		
		try{
			conn = getConnectionFolioReal();
			
			pstm = conn.prepareStatement(sql);
			pstm.setLong(1, caratula);				
			rs = pstm.executeQuery();
			
			while(rs.next()){
				PropiedadDTO dto = new PropiedadDTO();
				Integer borrador = rs.getInt("BORRADOR");				
				Integer folio = rs.getInt("FOLIO");
				dto.setBorrador(borrador);
				dto.setFolio(folio);
				listaProp.add(dto);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER PROPS POR CARATULA");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(pstm!=null){try{pstm.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		
		return listaProp;
	}	
	private static String valorSinNull(String param){
		
		if( param == null )
			return "";
		
		return param.trim();
	}
	
	public boolean anularDocumento(String codigoDocumento) throws GeneralException{
		Connection conn = null;
		int rs = 0;
		PreparedStatement pstm = null;
		boolean respuesta = false;
		
		final String sql =" Update Firma_E.dbo.REGISTRO_FIRMA_ELECT set VIGENTE=0 " +
		"where COD_ARCHIVO_ALPHA='" + codigoDocumento + "'";
		
		try{
			
			conn = getConnectionFirmaE();
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeUpdate();
			if(rs > 0)
				respuesta = true;
			
		}catch(Exception e){
			throw new GeneralException(null,"ERROR AL ANULAR DOCUMENTO");
		}finally{
//			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(pstm!=null){try{pstm.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return respuesta;
	}
	
	public static ArrayList<PropiedadDTO> buscarBorradoresPorTituloHipoteca(Integer fojas, Integer numero, Integer anyo, Boolean bis) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<PropiedadDTO> listaBorradores = new ArrayList<PropiedadDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BORRADOR_X_TITULO_HIPO(?,?,?,?)}");
			call.setInt(1, fojas);
			call.setInt(2, numero);
			call.setInt(3, anyo);
			call.setBoolean(4, bis);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				PropiedadDTO propiedadDTO = new PropiedadDTO();
				propiedadDTO.setFolio( rs.getInt("FOLIO") );
				propiedadDTO.setBorrador( rs.getInt("BORRADOR") );
				propiedadDTO.setFoja( rs.getInt("FOJA_H") );
				propiedadDTO.setNumero( rs.getInt("NUMERO_H") );
				propiedadDTO.setAnyo( rs.getInt("ANO_H") );
				propiedadDTO.setBis( rs.getInt("BIS_H"));
				
				listaBorradores.add(propiedadDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER BORRADORES POR HIPOTECA");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaBorradores;
	}
	
	public static ArrayList<PropiedadDTO> buscarBorradoresPorTituloProhibiciones(Integer fojas, Integer numero, Integer anyo, Boolean bis) throws GeneralException{
		Connection conn = null;
		CallableStatement call = null;
		ResultSet rs = null;
		ArrayList<PropiedadDTO> listaBorradores = new ArrayList<PropiedadDTO>();
		
		try{
			conn = getConnectionFolioReal();
			
			call = conn.prepareCall("{call BORRADOR_X_TITULO_PROH(?,?,?,?)}");
			call.setInt(1, fojas);
			call.setInt(2, numero);
			call.setInt(3, anyo);
			call.setBoolean(4, bis);			
			rs = call.executeQuery();
			
			while(rs.next()){ 
				PropiedadDTO propiedadDTO = new PropiedadDTO();
				propiedadDTO.setFolio( rs.getInt("FOLIO") );
				propiedadDTO.setBorrador( rs.getInt("BORRADOR") );
				propiedadDTO.setFoja( rs.getInt("FOJA_PH") );
				propiedadDTO.setNumero( rs.getInt("NUMERO_PH") );
				propiedadDTO.setAnyo( rs.getInt("ANO_PH") );
				propiedadDTO.setBis( rs.getInt("BIS_PH"));
				
				listaBorradores.add(propiedadDTO);
			}
			
		}catch(Exception e){
			logger.error("Error al acceder a la base de datos: " + e.getMessage(),e);
			throw new GeneralException(null,"ERROR AL OBTENER BORRADORES POR PROHIBICIONES");
		}finally{
			if(rs!=null) {try{rs.close();}catch(Exception e){}}
			if(call!=null){try{call.close();}catch(Exception e){}}
			if(conn!=null){try{conn.close();}catch(Exception e){}}
		}
		return listaBorradores;
	}
	
	 public static List<Timestamp> getUsuarioFirmadorSuplentes() throws BussinesException
			  {
			    Connection con = null;
			    try
			    {
//			      DriverManager.registerDriver(new Driver());
			      con = getConnection("jdbc:jtds:sqlserver://maipo:1433;databaseName=","sa","sa");
			      String sql = "SELECT * FROM Firma_E.dbo.USUARIO_FIRMADOR UF INNER JOIN Firma_E.dbo.REGISTRO R ON UF.ID_REGISTRO=R.REGISTRO";
			      sql = sql + " INNER JOIN Firma_E.dbo.fe_usuarios on uf.rut_usuario=fe_usuarios.rut";
			      sql = sql + " WHERE UF.Id_TIPO_USUARIO=2 and uf.activo=1";
			      sql = sql + " order by nombres";

			      List lista = new ArrayList();
			      Statement stm = con.createStatement();
			      ResultSet rs = stm.executeQuery(sql);
			      while (rs.next()) {

			    	  System.out.println(rs.getString("NOMBREs"));
			        Timestamp t = (rs.getTimestamp("FECHA_FIN_FIRMA"));
			        lista.add(t);
			      }

			      return lista;
			    }
			    catch (Exception e) {
			      logger.error(e);
			      throw new BussinesException("GEN-001");
			    }
			    finally {
			      if (con != null)
			        try {
			          con.close();
			        }
			        catch (SQLException e) {
			          logger.error(e);
			        }
			    }
			  }	

public static void main(String[] args) {
	try{
		System.setProperty("jboss.server.home.dir", "C:\\jboss-5.1.0.GA\\server\\default");
	List suplentes = getUsuarioFirmadorSuplentes();
    for (Iterator iterator = suplentes.iterator(); iterator.hasNext(); ) {
    	Timestamp uf = (Timestamp)iterator.next();
    	System.out.println("Fecha usuario: "+uf+" - "+uf.getTime());
    	
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date hoy = sdf.parse("2017-09-15 05:00:00");
        Calendar cal = new GregorianCalendar();
        cal.setTime(uf);
        cal.set(10, 23);
        cal.set(12, 59);
        System.out.println("Fecha usuario mod.: "+cal.getTime()+" - "+cal.getTimeInMillis());
//        uf.setTime(cal.getTimeInMillis());
        System.out.println("Fecha sistema: "+hoy+" - "+hoy.getTime());
//        if(uf.after(hoy))
//        if (hoy.after(cal.getTime()))
        if(hoy.getTime() > cal.getTimeInMillis())
            System.out.println("expirado");
        else
        	System.out.println("vigente");
      }
	} catch(Exception e){
	}
	

}
}

