package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;

public class RequirenteDAO extends AbstractJdbcDao {
	private static final Logger logger = Logger.getLogger(RequirenteDAO.class);
	
	public RequirenteDAO(){
		
	}
	
	
	public String getNombre(String rut) throws SQLException, ServiceLocatorException,GeneralException{
		Connection conexion = this.conexionFlujo();
		ResultSet rs = null;
	    Statement stm = null;

		try {
			
//			DataSource registro = ServiceLocator.getInstance().getDataSource("java:dsFlujo");
			
//			con = registro.getConnection();

			String sql = "select NOMBRES, APE_PATERNO "+
						 "from TAB_REQUIRENTES tr "+
						 "where tr.RUT_REQUIRENTE = '"+rut+"'";

			Statement ps = conexion.createStatement();
			rs = ps.executeQuery(sql);

			if(rs!=null){
				if(rs.next()){ 
					
					String nombres = rs.getString("NOMBRES");
					String apePaterno = rs.getString("APE_PATERNO");
					
					String fullName = "";
					if(StringUtils.isNotBlank(nombres)){
						fullName = nombres.trim();
					}
					
					if(StringUtils.isNotBlank(apePaterno)){
						fullName = fullName + " "+apePaterno.trim();
					}

					return fullName;
				}   				
			}

		}finally {
			if (rs != null)
		        try {
		            rs.close();
		        } catch (SQLException e1) {
		        	logger.error(e1.getMessage(),e1);
		        }
		    if (stm != null)
		        try {
		            stm.close();
		        } catch (SQLException e1) {
		        	logger.error(e1.getMessage(),e1);
		        }
		    if (conexion != null)
		        try {
		        	conexion.close();
		        } catch (SQLException e) {
		        	logger.error(e.getMessage(),e);
		        }
		}        
		return null;
	} 
	
	public RequirenteVO getRequirenteFull(String rut) throws SQLException, ServiceLocatorException,GeneralException{
		Connection con = null;
		ResultSet rs = null;
		RequirenteVO requirenteVO = null;
		
		try {
			//DataSource registro = ServiceLocator.getInstance().getDataSource("java:dsFlujo");
			
			//con = registro.getConnection();
			con = this.conexionFlujo();

			String sql = "select NOMBRES, APE_PATERNO, APE_MATERNO, DIRECCION, TELEFONO, EMAIL "+
						 "from TAB_REQUIRENTES tr "+
						 "where tr.RUT_REQUIRENTE = '"+rut+"'";

			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs!=null){
				if(rs.next()){ 
					requirenteVO = new RequirenteVO();
					
					requirenteVO.setNombres(rs.getString("NOMBRES"));
					requirenteVO.setApellidoPaterno(rs.getString("APE_PATERNO"));
					requirenteVO.setApellidoMaterno(rs.getString("APE_MATERNO"));
					requirenteVO.setDireccion(rs.getString("DIRECCION"));
					requirenteVO.setTelefono(rs.getString("TELEFONO"));
					requirenteVO.setEmail(rs.getString("EMAIL"));
					
					return requirenteVO;
				}   				
			}

		}finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if (con != null) {
				try {
					con.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}        
		return null;
	} 
	
	
}