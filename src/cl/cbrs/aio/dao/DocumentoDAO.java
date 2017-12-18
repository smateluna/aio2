package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.jboss.logging.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.DocumentoDTO;


public class DocumentoDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(DocumentoDAO.class);
	
	public DocumentoDAO(){
		
	}
	
	public DocumentoDTO getDocumento(Long caratulap, Integer versionp, Long idTipoDocumentop) throws SQLException, ServiceLocatorException,GeneralException{
		DocumentoDTO documentoDTO = null;
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "SELECT ID_DOCUMENTO, ID_TIPO_DOCUMENTO, ID_REG, NOMBRE_ARCHIVO, NOMBRE_ARCHIVO_VERSION, "
					+ "VERSION, INDICE, VIGENTE, FECHA_PROCESA, CARATULA, CODIGO_FIRMA_E, FECHA_DIGITALIZA "
					+ "FROM documentos.dbo.DOCUMENTO "
					+ "WHERE CARATULA = "+caratulap
					+ " AND VERSION=" + versionp
					+ " AND ID_TIPO_DOCUMENTO = " + idTipoDocumentop
					+ " AND ID_REG = 0 "
					+ " AND VIGENTE = 1 ";

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			if(rs != null && rs.next()){          	

				documentoDTO = new DocumentoDTO();
				
				Long idDocumento = rs.getLong("ID_DOCUMENTO");
				Long idTipoDocumento = rs.getLong("ID_TIPO_DOCUMENTO");
				Long idReg = rs.getLong("ID_REG");
				String nombreArchivo = rs.getString("NOMBRE_ARCHIVO");
				String nombreArchivoVersion = rs.getString("NOMBRE_ARCHIVO_VERSION");
				Long version = rs.getLong("VERSION");
				Long indice = rs.getLong("INDICE");
				Boolean vigente = rs.getBoolean("VIGENTE");
				Timestamp fechaProcesa = rs.getTimestamp("FECHA_PROCESA");
				Long caratula = rs.getLong("CARATULA");
				String codFirmaE = rs.getString("CODIGO_FIRMA_E");
				Timestamp fechaDigitaliza = rs.getTimestamp("FECHA_DIGITALIZA");
						
				documentoDTO.setIdDocumento(idDocumento);
				documentoDTO.setIdTipoDocumento(idTipoDocumento);
				documentoDTO.setIdReg(idReg);
				documentoDTO.setNombreArchivo(nombreArchivo);
				documentoDTO.setNombreArchivoVersion(nombreArchivoVersion);
				documentoDTO.setVersion(version);
				documentoDTO.setIndice(indice);
				documentoDTO.setVigente(vigente);
				documentoDTO.setFechaProcesa(fechaProcesa);
				documentoDTO.setCaratula(caratula);
				documentoDTO.setCodFirmaE(codFirmaE);
				documentoDTO.setFechaDigitaliza(fechaDigitaliza);
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
		return documentoDTO;
	} 
}