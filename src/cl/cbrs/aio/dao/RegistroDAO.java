package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import cl.cbrs.aio.dto.firmaElectronica.CertificadoFNADTO;


public class RegistroDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(RegistroDAO.class);
	
	public RegistroDAO(){
		
	}
	
	public ArrayList<CertificadoFNADTO> obtenerCertificadoFNA(Long idc) throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		ArrayList<CertificadoFNADTO> lista = new ArrayList<CertificadoFNADTO>();

		try {
			conn = this.conexionFlujo();

			String sql = "SELECT registro, derechos "
					+ " FROM registro.dbo.mae_certificacion_fna "
					+ " WHERE idc = " + idc; 

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				Integer registro = rs.getInt("registro");
				Integer derechos = rs.getInt("derechos");
				CertificadoFNADTO certificadoFNADTO = new CertificadoFNADTO();
				certificadoFNADTO.setRegistro(registro);
				certificadoFNADTO.setDerechos(derechos);
				lista.add(certificadoFNADTO);
			}
           
		} finally {
			if (rs != null)
		        try {
		            rs.close();
		        } catch (SQLException e1) {
		        	logger.error(e1.getMessage(),e1);
		        }
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		} 
		
		return lista;
	} 
	
	public String obtenerGlosaCobroMaeCertificacion(String codFirma) throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		String glosa = "";

		try {
			conn = this.conexionFlujo();

			String sql = "select GLOSA_COBRO from registro.dbo.GLOSA_COBRO_MAE_CERTIFICACION "
					+ " WHERE codigo_firma_e = '" + codFirma + "'"; 

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				glosa = rs.getString("GLOSA_COBRO");
			}
           
		} finally {
			if (rs != null)
		        try {
		            rs.close();
		        } catch (SQLException e1) {
		        	logger.error(e1.getMessage(),e1);
		        }
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		} 
		
		return glosa;
	} 	
}