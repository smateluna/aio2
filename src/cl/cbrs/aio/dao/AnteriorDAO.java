package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.jboss.logging.Logger;

import cl.cbr.util.locator.ServiceLocator;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.BorradorDTO;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;

public class AnteriorDAO {
	private static final Logger logger = Logger.getLogger(AnteriorDAO.class);

	public AnteriorDAO(){

	}


	public ArrayList<BorradorDTO> buscaBorrador(Long foja, Long numero, Long ano, Integer bis) throws SQLException, ServiceLocatorException{
		Connection con = null;
		ResultSet rs = null;

		ArrayList<BorradorDTO> dtos = new ArrayList<BorradorDTO>();

		try {
			DataSource registro = ServiceLocator.getInstance().getDataSource("java:jboss/datasources/dsFolioReal");

			con = registro.getConnection();

			String sql = "SELECT FOLIO, BORRADOR "+
					"FROM PROREAL "+
					"WHERE (FOJA_P = "+foja+") " +
					"AND (NUMERO_P = "+numero+") " +
					"AND (ANO_P = "+ano+") " +
					"AND (BIS_P = "+bis+")";

			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs!=null){
				while(rs.next()){ 
					BorradorDTO dto = new BorradorDTO();

					Integer folio = rs.getInt("FOLIO");
					Integer borrador = rs.getInt("BORRADOR");

					dto.setBorrador(borrador);
					dto.setFolio(folio);

					dtos.add(dto);
				}   				
			}

		}finally {
			if (con != null) {
				try {
					con.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}        
		return dtos;
	} 	
		
	public ArrayList<ConsultaDocumentoDTO> consultaBorradorFolio(Integer folio, Integer borrador) throws SQLException, ServiceLocatorException{
		Connection con = null;
		ResultSet rs = null;

		ArrayList<ConsultaDocumentoDTO> dtos = new ArrayList<ConsultaDocumentoDTO>();

		try {
			DataSource registro = ServiceLocator.getInstance().getDataSource("java:jboss/datasources/dsFolioReal");

			con = registro.getConnection();

			String sql = "SELECT M_PROP.FOJA_P FOJA_P, M_PROP.NUMERO_P NUMERO_P, M_PROP.ANO_P ANO_P, M_PROP.BIS_P BIS_P " +
						 "FROM FOLIO_REAL INNER JOIN " +
		                 "PROREAL ON FOLIO_REAL.BORRADOR = PROREAL.BORRADOR AND FOLIO_REAL.FOLIO = PROREAL.FOLIO INNER JOIN " +
		                 "M_PROP ON PROREAL.NUMERO_P = M_PROP.NUMERO_P " +
		                 "AND PROREAL.ANO_P = M_PROP.ANO_P " +
		                 "AND PROREAL.FOJA_P = M_PROP.FOJA_P " +
		                 "AND PROREAL.BIS_P = M_PROP.BIS_P " +
		                 "WHERE (FOLIO_REAL.BORRADOR = "+borrador+") AND  (FOLIO_REAL.FOLIO = "+folio+") " +
		                 "ORDER BY M_PROP.ANO_P DESC, M_PROP.NUMERO_P DESC, M_PROP.FOJA_P DESC ";

			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs!=null){
				while(rs.next()){ 
					ConsultaDocumentoDTO dto = new ConsultaDocumentoDTO();
					
					Integer foja = rs.getInt("FOJA_P");
					Integer numero = rs.getInt("NUMERO_P");
					Integer ano = rs.getInt("ANO_P");
					Boolean bis = rs.getBoolean("BIS_P");

//					Integer bisi = 0;
//					
//					if(bis){
//						bisi = 1;
//					}
					
					dto.setFoja(foja.longValue());
					dto.setNumero(numero.longValue());
					dto.setAno(ano.longValue());
					dto.setBis(bis);
									
					dtos.add(dto);			
				}   				
			}

		}finally {
			if (con != null) {
				try {
					con.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}        
		return dtos;
	}		
}