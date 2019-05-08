package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.borrador.vo.FolioRealVO;
import cl.cbrs.borrador.vo.ProrealIdVO;
import cl.cbrs.borrador.vo.ProrealVO;


public class FolioRealDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(FolioRealDAO.class);
	
	public FolioRealDAO(){
		
	}
	
	public List<ProrealVO> getBorradoresDesdeH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph) throws SQLException, ServiceLocatorException,GeneralException{
		List<ProrealVO> prorealVOs = new ArrayList<ProrealVO>();
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();
			
			int bis = bisph?1:0;

			String sql = "select borfol.FOLIO, borfol.BORRADOR, borfol.DIGITO, borfol.FOJA_H, borfol.NUMERO_H, borfol.ANO_H, borfol.BIS_H, freal.DIR "
					+ " from Folio_Real.dbo.BORFOLHIPO borfol "
					+ " inner join Folio_Real.dbo.FOLIO_REAL freal on freal.FOLIO = borfol.FOLIO AND freal.BORRADOR = borfol.BORRADOR AND freal.DIGITO = borfol.DIGITO " 
					+ " where borfol.FOJA_H="+fojaph+" and borfol.NUMERO_H="+numeroph+" and borfol.ANO_H="+anoph+" and borfol.BIS_H="+bis;

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			while (rs != null && rs.next()){          	

				ProrealVO prorealVO = new ProrealVO();
				
				Integer folio = rs.getInt("FOLIO");
				Integer borrador = rs.getInt("BORRADOR");
				Short digito = rs.getShort("DIGITO");
				Integer fojaP = rs.getInt("FOJA_H");
				Integer numeroP = rs.getInt("NUMERO_H");
				Short anoP = rs.getShort("ANO_H");
				Boolean bisP = rs.getBoolean("BIS_H");
				String direccion = rs.getString("DIR");
				
				ProrealIdVO proRealId = new ProrealIdVO();
				proRealId.setFolio(folio);
				proRealId.setBorrador(borrador);
				proRealId.setDigito(digito);
				proRealId.setFojaP(fojaP);
				proRealId.setNumeroP(numeroP);
				proRealId.setAnoP(anoP);
				proRealId.setBisP(bisP);
				prorealVO.setProRealId(proRealId);
				FolioRealVO folioReal = new FolioRealVO();
				folioReal.setDir(direccion);
				prorealVO.setFolioReal(folioReal );
				
				prorealVOs.add(prorealVO);
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
		return prorealVOs;
	} 
	
	public Integer getCantidadBorradoresDesdeH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph) throws SQLException, ServiceLocatorException,GeneralException{
		Integer cantidad = 0;
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();
			
			int bis = bisph?1:0;

			String sql = "select COUNT(*) AS cantidad "
					+ " from Folio_Real.dbo.BORFOLHIPO borfol "
					+ " inner join Folio_Real.dbo.FOLIO_REAL freal on freal.FOLIO = borfol.FOLIO AND freal.BORRADOR = borfol.BORRADOR AND freal.DIGITO = borfol.DIGITO " 
					+ " where borfol.FOJA_H="+fojaph+" and borfol.NUMERO_H="+numeroph+" and borfol.ANO_H="+anoph+" and borfol.BIS_H="+bis;

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			if(rs != null && rs.next()){          					
				cantidad = rs.getInt("cantidad");				
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
		return cantidad;
	}	
	
	public List<ProrealVO> getBorradoresDesdePH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph) throws SQLException, ServiceLocatorException,GeneralException{
		List<ProrealVO> prorealVOs = new ArrayList<ProrealVO>();
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();
			
			int bis = bisph?1:0;

			String sql = "select borfol.FOLIO, borfol.BORRADOR, borfol.DIGITO, borfol.FOJA_PH, borfol.NUMERO_PH, borfol.ANO_PH, borfol.BIS_PH, freal.DIR "
					+ " from Folio_Real.dbo.BORFOLPROH borfol "
					+ " inner join Folio_Real.dbo.FOLIO_REAL freal on freal.FOLIO = borfol.FOLIO AND freal.BORRADOR = borfol.BORRADOR AND freal.DIGITO = borfol.DIGITO " 
					+ " where borfol.FOJA_PH="+fojaph+" and borfol.NUMERO_PH="+numeroph+" and borfol.ANO_PH="+anoph+" and borfol.BIS_PH="+bis;

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			while (rs != null && rs.next()){          	

				ProrealVO prorealVO = new ProrealVO();
				
				Integer folio = rs.getInt("FOLIO");
				Integer borrador = rs.getInt("BORRADOR");
				Short digito = rs.getShort("DIGITO");
				Integer fojaP = rs.getInt("FOJA_PH");
				Integer numeroP = rs.getInt("NUMERO_PH");
				Short anoP = rs.getShort("ANO_PH");
				Boolean bisP = rs.getBoolean("BIS_PH");
				String direccion = rs.getString("DIR");
				
				ProrealIdVO proRealId = new ProrealIdVO();
				proRealId.setFolio(folio);
				proRealId.setBorrador(borrador);
				proRealId.setDigito(digito);
				proRealId.setFojaP(fojaP);
				proRealId.setNumeroP(numeroP);
				proRealId.setAnoP(anoP);
				proRealId.setBisP(bisP);
				prorealVO.setProRealId(proRealId);
				FolioRealVO folioReal = new FolioRealVO();
				folioReal.setDir(direccion);
				prorealVO.setFolioReal(folioReal );
				
				prorealVOs.add(prorealVO);
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
		return prorealVOs;
	} 
	
	public Integer getCantidadBorradoresDesdePH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph) throws SQLException, ServiceLocatorException,GeneralException{
		Integer cantidad = 0;
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();
			
			int bis = bisph?1:0;

			String sql = "select COUNT(*) AS cantidad "
					+ " from Folio_Real.dbo.BORFOLPROH borfol "
					+ "inner join Folio_Real.dbo.FOLIO_REAL freal on freal.FOLIO = borfol.FOLIO AND freal.BORRADOR = borfol.BORRADOR AND freal.DIGITO = borfol.DIGITO " 
					+ "where borfol.FOJA_PH="+fojaph+" and borfol.NUMERO_PH="+numeroph+" and borfol.ANO_PH="+anoph+" and borfol.BIS_PH="+bis;

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			if(rs != null && rs.next()){          					
				cantidad = rs.getInt("cantidad");				
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
		return cantidad;
	} 
	
	public List<ProrealVO> consultaTitulosAnteriores(Integer fojaParam, Integer numeroParam, Short anoParam, Boolean bisParam) throws SQLException, ServiceLocatorException,GeneralException{
		List<ProrealVO> lista = new ArrayList<ProrealVO>();
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "select distinct p2.FOJA_P,p2.NUMERO_P,p2.ANO_P,p2.BIS_P, p2.VIGENTE_T " +   
						"from Folio_Real.dbo.proreal as p " +  
						"full outer join  Folio_Real.dbo.proreal as p2 on p2.BORRADOR=p.BORRADOR and p2.FOLIO=p.FOLIO " +  
						"where p.foja_p="+fojaParam+" and p.NUMERO_P="+numeroParam+" and p.ANO_P="+anoParam+" and p.BIS_P="+(bisParam?1:0)+" and p.VIGENTE_T = 1 " + 
						"order by p2.ANO_P,p2.NUMERO_P,p2.FOJA_P,p2.BIS_P";

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			while (rs != null && rs.next()){          	

				ProrealVO dto = new ProrealVO();
				
				Integer foja = rs.getInt("FOJA_P");
				Integer numero = rs.getInt("NUMERO_P");
				Short ano = rs.getShort("ANO_P");
				Boolean bis = rs.getBoolean("BIS_P");
				Boolean vigente = rs.getBoolean("VIGENTE_T");
				
				ProrealIdVO id = new ProrealIdVO();
				id.setFojaP(foja);
				id.setNumeroP(numero);
				id.setAnoP(ano);
				id.setBisP(bis);
				dto.setProRealId(id);
				dto.setVigenteT(vigente);
				
				lista.add(dto);
			}           
		}catch (SQLException e) {
			logger.error(e);
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
		return lista;
	} 
	
}