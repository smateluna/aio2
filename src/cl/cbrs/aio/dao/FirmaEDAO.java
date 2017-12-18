package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class FirmaEDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(FirmaEDAO.class);
	
	public FirmaEDAO(){
		
	}
	
	public int updateCaratulaFirma(Long caratulaOld, Long caratulaNew) throws Exception{
		Connection conn = null;
		int res;

		try {
			conn = this.conexionFlujo();

			String sql = "UPDATE Firma_E.dbo.REGISTRO_FIRMA_ELECT "
					+ " SET caratula = '" + caratulaNew + "'"
					+ " WHERE caratula = '" + caratulaOld + "'"
					+ " AND vigente=1";

			PreparedStatement ps = conn.prepareStatement(sql);
			
			res = ps.executeUpdate();
           
		} finally {
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		} 
		
		return res;
	} 
}