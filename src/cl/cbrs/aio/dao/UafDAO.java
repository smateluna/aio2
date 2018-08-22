package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.UafBienDTO;
import cl.cbrs.aio.dto.UafPersonaDTO;

public class UafDAO extends AbstractJdbcDao {
	private static final Logger logger = Logger.getLogger(UafDAO.class);
	
	public UafDAO(){
		
	}
	
	
	public ArrayList<UafPersonaDTO> buscarPersonas(String nInscripciones, String ano) throws SQLException, ServiceLocatorException,GeneralException{
		Connection conexion = this.conexionFlujo();
		ResultSet rs = null;
	    Statement stm = null;
	    ArrayList<UafPersonaDTO> lista = new ArrayList<UafPersonaDTO>();

		try {
			String sql = "SELECT DISTINCT  CARATULA, NOMBRES, APELL_PATERNO, APELL_MATERNO, RUT " +
						"FROM REGISTRO.DBO.TAB_PERSONA AS T " +
						"WHERE " +     
						"(COD_TIPO_PERSONA = 'co') " +
						"AND (RUT <> '') " +
						"AND (AÑO_CARATULA = "+ano+") " +
						"and (select count(RUT) from REGISTRO.DBO.TAB_PERSONA where T.RUT=RUT AND (COD_TIPO_PERSONA = 'co') AND  (AÑO_CARATULA = "+ano+") AND FOJA_P<>0 AND NUMERO_P<>0 AND ANO_P<>0 )>= " +nInscripciones+  " " +
						"AND FOJA_P<>0 AND NUMERO_P<>0 AND ANO_P<>0 " +
						"and LEFT(t.CARATULA,7)  not in (select caratula from flujo.dbo.mae_formulario AS M where M.CARATULA =LEFT(t.CARATULA,7) AND M.CODIGO<>0) " +
						"ORDER BY APELL_PATERNO,APELL_MATERNO,NOMBRES,RUT,CARATULA";

			Statement ps = conexion.createStatement();
			rs = ps.executeQuery(sql);

			if(rs!=null){
				while(rs.next()){ 
					UafPersonaDTO dto = new UafPersonaDTO();
					
					String nombres = rs.getString("NOMBRES");
					String apePaterno = rs.getString("APELL_PATERNO");
					String apeMaterno = rs.getString("APELL_MATERNO");
					
					String fullName = "";
					if(StringUtils.isNotBlank(nombres)){
						fullName = nombres.trim();
					}
					
					if(StringUtils.isNotBlank(apePaterno)){
						fullName = fullName + " "+apePaterno.trim();
					}
					
					if(StringUtils.isNotBlank(apeMaterno)){
						fullName = fullName + " "+apeMaterno.trim();
					}	
					
					dto.setNombre(fullName);
					dto.setRut(rs.getString("RUT"));
					dto.setCaratula(rs.getString("CARATULA"));

					lista.add(dto);
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
		return lista;
	} 
	
	public ArrayList<UafBienDTO> buscarBienes(String nInscripciones, String anyo, ArrayList<String> listaNaturalezas) throws SQLException, ServiceLocatorException,GeneralException{
		Connection conexion = this.conexionFlujo();
		ResultSet rs = null;
	    Statement stm = null;
	    ArrayList<UafBienDTO> lista = new ArrayList<UafBienDTO>();

		try {
			StringBuffer sql = new StringBuffer("select distinct p.BORRADOR,p.FOLIO, p.FECHA_EST_T,p.FOJA_P,p.NUMERO_P,p.ANO_P,p.BIS_P, m.COD_NAT, n.DESC_NAT, n.REGISTRO " +
						"from folio_real.dbo.proreal as p inner join folio_real.dbo.M_PROP as m on p.FOJA_P=m.FOJA_P and p.NUMERO_P=m.NUMERO_P and p.ANO_P=m.ANO_P and p.BIS_P=m.BIS_P " +
						"inner join folio_real.dbo.T_NAT as n on m.COD_NAT = n.COD_NAT " +
						"where " + 
						"p.ano_p="+anyo+" " +
						"and (select count(borrador) from folio_real.dbo.proreal where BORRADOR=p.borrador and FOLIO=p.FOLIO and ano_p="+anyo+" ) >= "+nInscripciones+" " +
						"and m.CARATULA not in  (select caratula from flujo.dbo.mae_formulario AS mm where mm.CARATULA =m.CARATULA AND mm.CODIGO<>0) " +
						"and m.COD_NAT in (");
						for(int i=0; i<listaNaturalezas.size(); i++){
							sql.append(listaNaturalezas.get(i));
							if(i+1<listaNaturalezas.size())
								sql.append(",");
						}
						sql.append(") order by BORRADOR,FOLIO,ano_p,NUMERO_P");

			Statement ps = conexion.createStatement();
			rs = ps.executeQuery(sql.toString());

			if(rs!=null){
				while(rs.next()){ 
					UafBienDTO dto = new UafBienDTO();
					
					Integer borrador = rs.getInt("BORRADOR");
					Integer folio = rs.getInt("FOLIO");
					Date fechaEstado = rs.getTimestamp("FECHA_EST_T");
					Integer foja = rs.getInt("FOJA_P");
					Integer numero = rs.getInt("NUMERO_P");
					Integer ano = rs.getInt("ANO_P");
					Boolean bis = rs.getBoolean("BIS_P");	
					Integer codNaturaleza = rs.getInt("COD_NAT");
					String descNaturaleza = rs.getString("DESC_NAT");
					String regNaturaleza = rs.getString("REGISTRO");
					
					dto.setBorrador(borrador);
					dto.setFolio(folio);
					dto.setFechaEstado(fechaEstado);
					dto.setFoja(foja);
					dto.setNumero(numero);
					dto.setAno(ano);
					dto.setBis(bis);
					dto.setCodNaturaleza(codNaturaleza);
					dto.setDescNaturaleza(descNaturaleza);
					dto.setRegNaturaleza(regNaturaleza);

					lista.add(dto);
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
		return lista;
	} 
}