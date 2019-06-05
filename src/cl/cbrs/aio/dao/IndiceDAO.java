package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.IndicePropDTO;


public class IndiceDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(IndiceDAO.class);
	
	public IndiceDAO(){
		
	}
	
	public int updateIndiceProp(IndicePropDTO indice) throws Exception{
		Connection conn = null;
		int res;

		try {
			conn = this.conexionFlujo();

			String sql = "UPDATE Indices.dbo.M_INDICES_PROP "
					+ " SET foja = '" + indice.getFoja() + "'"
					+ " , numero = '" + indice.getNumero() + "'"
					+ " , año = '" + indice.getAno() + "'"
					+ " , nombres_com = '" + indice.getNombresCom() + "'"
					+ " , dir_antigua = '" + indice.getDirAntigua() + "'"
					+ " , procesado = 0 "
					+ " WHERE id_i = '" + indice.getIdI() + "'"
					+ " AND procesado=1";

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
	
	public void getIndiceProp(Long id) throws SQLException, ServiceLocatorException,GeneralException{
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "SELECT * "
					+ "FROM indices.dbo.M_INDICES_PROP "
					+ "WHERE ID_I = "+id;

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			if(rs != null && rs.next()){          	

			}           
		}catch (SQLException e) {
			throw e;		
		}finally {
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		}        
	} 
}