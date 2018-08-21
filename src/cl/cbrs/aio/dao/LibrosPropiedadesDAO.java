package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jboss.logging.Logger;

import cl.cbrs.aio.dto.DespachoCuadernilloDTO;


public class LibrosPropiedadesDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(LibrosPropiedadesDAO.class);
	
	public LibrosPropiedadesDAO(){
		
	}

	public ArrayList<DespachoCuadernilloDTO> obtenerDespachoCuadernillos(Date fechaIni, Date fechaFin) throws Exception{
		ArrayList<DespachoCuadernilloDTO> listaDespachoCuadernilloDTO = new ArrayList<DespachoCuadernilloDTO>();
		Connection conn = null;
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		try {
			conn = this.conexionFlujo();
	
			String sql = "select ID, CUADERNILLO, USUARIO, FECHA from Libros_Propiedades.dbo.DESPACHO_CUADERNILLO " +
			"where fecha>='" + sdf.format(fechaIni) + " 00:00:00' and fecha <='" + sdf.format(fechaFin) + " 23:59:59'";
	
			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
	
			while(rs.next()){     
				Long id = rs.getLong("ID");
				String usuario = rs.getString("USUARIO");
				Date fecha = rs.getTimestamp("FECHA");
				Integer cuadernillo = rs.getInt("CUADERNILLO");
				
				DespachoCuadernilloDTO despachoCuadernilloDTO = new DespachoCuadernilloDTO();
				despachoCuadernilloDTO.setId(id);
				despachoCuadernilloDTO.setCuadernillo(cuadernillo);
				despachoCuadernilloDTO.setFecha(fecha);
				despachoCuadernilloDTO.setUsuario(usuario);
				
				listaDespachoCuadernilloDTO.add(despachoCuadernilloDTO);
			}           
		} finally {
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
		return listaDespachoCuadernilloDTO;
	}  	
	
	public DespachoCuadernilloDTO obtenerDespachoCuadernillo(Integer cuadernillo) throws Exception{
		DespachoCuadernilloDTO despachoCuadernilloDTO = null;
		Connection conn = null;
		ResultSet rs = null;
	
		try {
			conn = this.conexionFlujo();
	
			String sql = "select ID, CUADERNILLO, USUARIO, FECHA from Libros_Propiedades.dbo.DESPACHO_CUADERNILLO " +
			"where cuadernillo=" + cuadernillo;
	
			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
	
			if(rs.next()){     
				despachoCuadernilloDTO = new DespachoCuadernilloDTO();
				Long id = rs.getLong("ID");
				String usuario = rs.getString("USUARIO");
				Date fecha = rs.getDate("FECHA");
				
				despachoCuadernilloDTO.setId(id);
				despachoCuadernilloDTO.setCuadernillo(cuadernillo);
				despachoCuadernilloDTO.setFecha(fecha);
				despachoCuadernilloDTO.setUsuario(usuario);
			}           
		} finally {
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
		return despachoCuadernilloDTO;
	}	
	
	public void agregarDespachoCuadernillo(DespachoCuadernilloDTO dto) throws Exception{
		Connection conn = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		try {
			conn = this.conexionFlujo();
			String sql = "insert into Libros_Propiedades.dbo.DESPACHO_CUADERNILLO ( " +
						"CUADERNILLO, USUARIO, FECHA) VALUES ( " +
						dto.getCuadernillo() + ", " +
						"'" + dto.getUsuario() + "', " +
						"'" + sdf.format(dto.getFecha())+"'"+
						")";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();	           
		} finally {
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		}        		
	} 	
	
	public void updateDespachoCuadernillo(DespachoCuadernilloDTO dto) throws Exception{
		Connection conn = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		try {
			conn = this.conexionFlujo();
			String sql = "update Libros_Propiedades.dbo.DESPACHO_CUADERNILLO set " +
						"USUARIO='"+dto.getUsuario()+"', FECHA='"+sdf.format(dto.getFecha())+"' "+
						"WHERE ID="+dto.getId();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				}catch (SQLException e) {
					throw e;
				}
			}
		}        		
	} 		
	
	
}