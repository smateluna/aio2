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
import cl.cbrs.borrador.vo.ProrealIdVO;
import cl.cbrs.borrador.vo.ProrealVO;


public class FolioRealDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(FolioRealDAO.class);
	
	public FolioRealDAO(){
		
	}
	
	public List<ProrealVO> getBorradoresDesdePH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph) throws SQLException, ServiceLocatorException,GeneralException{
		List<ProrealVO> prorealVOs = new ArrayList<ProrealVO>();
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "select FOLIO, BORRADOR, DIGITO, prop.FOJA_P, prop.NUMERO_P, prop.ANO_P, prop.BIS_P, VIGENTE_T, FECHA_EST_T from Folio_Real.dbo.PROPROH proph inner join "
					+ "Folio_Real.dbo.PROREAL prop on proph.FOJA_P=prop.FOJA_P and "
					+ "proph.NUMERO_P=prop.NUMERO_P and proph.ANO_P=prop.ANO_P "
					+ "where proph.FOJA_PH="+fojaph+" and proph.NUMERO_PH="+numeroph+" and proph.ANO_PH="+anoph+" and proph.BIS_PH='"+bisph+"'";

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			while (rs != null && rs.next()){          	

				ProrealVO prorealVO = new ProrealVO();
				
				Integer folio = rs.getInt("FOLIO");
				Integer borrador = rs.getInt("BORRADOR");
				Short digito = rs.getShort("DIGITO");
				Integer fojaP = rs.getInt("FOJA_P");
				Integer numeroP = rs.getInt("NUMERO_P");
				Short anoP = rs.getShort("ANO_P");
				Boolean bisP = rs.getBoolean("BIS_P");
				Boolean vigenteT = rs.getBoolean("VIGENTE_T");
				Timestamp fechaEstT = rs.getTimestamp("FECHA_EST_T");
				
				ProrealIdVO proRealId = new ProrealIdVO();
				proRealId.setFolio(folio);
				proRealId.setBorrador(borrador);
				proRealId.setDigito(digito);
				proRealId.setFojaP(fojaP);
				proRealId.setNumeroP(numeroP);
				proRealId.setAnoP(anoP);
				proRealId.setBisP(bisP);
				prorealVO.setVigenteT(vigenteT);
				prorealVO.setFechaEstT(fechaEstT);
				
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

			String sql = "select COUNT(*) AS cantidad from Folio_Real.dbo.PROPROH proph inner join "
					+ "Folio_Real.dbo.PROREAL prop on proph.FOJA_P=prop.FOJA_P and "
					+ "proph.NUMERO_P=prop.NUMERO_P and proph.ANO_P=prop.ANO_P "
					+ "where proph.FOJA_PH="+fojaph+" and proph.NUMERO_PH="+numeroph+" and proph.ANO_PH="+anoph+" and proph.BIS_PH='"+bisph+"'";

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