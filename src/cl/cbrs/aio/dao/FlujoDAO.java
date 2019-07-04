package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.CierreCtasCtesFinalDTO;
import cl.cbrs.aio.dto.EstadoActualCaratulaDTO;
import cl.cbrs.aio.dto.RecepcionPlanoDTO;
import cl.cbrs.aio.dto.SeccionDTO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.caratula.util.ConstantesCaratula;


public class FlujoDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(FlujoDAO.class);
	
	public FlujoDAO(){
		
	}
	
	public ArrayList<String> getCiudades() throws Exception{
		ArrayList<String> listaCiudades = new ArrayList<String>();
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "SELECT NOMBRE "
					+ "FROM flujo.dbo.TAB_CIUDAD "
					+ "ORDER by nombre asc ";

			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();

			while(rs.next()){          					
				String nombreCiudad = rs.getString("NOMBRE").trim();
				listaCiudades.add(nombreCiudad);				
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
		return listaCiudades;
	} 


	public CierreCtasCtesFinalDTO getCierreFinalCaratula(Long caratula) throws Exception{
		CierreCtasCtesFinalDTO ctasCtesFinalDTO = null;
		Connection conn = null;
		ResultSet rs = null;
	
		try {
			conn = this.conexionFlujo();
	
			String sql = "select CARATULA, CODIGO, VALOR_REAL, CLIENTE_CTA_CTE, FECHA_REV, MITAD_DE_MES, CIERRE_ACTUAL " + 
					" FROM flujo.dbo.CIERRE_CTAS_CTES_FINAL where CARATULA=" + caratula;
	
			PreparedStatement ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
	
			if(rs.next()){     
				ctasCtesFinalDTO = new CierreCtasCtesFinalDTO();
				Date fechaRev = rs.getDate("FECHA_REV");
				Integer cierreActual = rs.getInt("CIERRE_ACTUAL");
				Integer mitadDeMes = rs.getInt("MITAD_DE_MES");
				
				ctasCtesFinalDTO.setFechaRev(fechaRev);
				ctasCtesFinalDTO.setCierreActual(cierreActual);
				ctasCtesFinalDTO.setMitadDeMes(mitadDeMes);
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
		return ctasCtesFinalDTO;
	} 
	
	public void eliminarCierreFinalCaratula(Long caratula) throws Exception{
		Connection conn = null;
	
		try {
			conn = this.conexionFlujo();
			String sql = "DELETE FROM flujo.dbo.CIERRE_CTAS_CTES_FINAL where CARATULA=" + caratula;
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
	
	public ArrayList<CuentaCorrienteDTO> getCtasCtes() throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		ArrayList<CuentaCorrienteDTO> listaCtasCtes = new ArrayList<CuentaCorrienteDTO>();
	
		try {
			conn = this.conexionFlujo();
			String sql = "SELECT CODIGO, RUT, INSTITUCION, ENCARGADO, TELEFONO, TIPO, BOLETA, EMAIL, DIRECCION, DIRECCION_ENTREGA, ENCARGADO_ENTREGA " +
						 " FROM flujo.dbo.CTAS_CTES";
			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				CuentaCorrienteDTO dto = new CuentaCorrienteDTO();
				Integer codigo = rs.getInt("CODIGO");
				String rut = rs.getString("RUT");
				String institucion = rs.getString("INSTITUCION");
				String encargado = rs.getString("ENCARGADO");
				String telefono = rs.getString("TELEFONO");
				String tipo = rs.getString("TIPO");
				String boleta = rs.getString("BOLETA");
				String email = rs.getString("EMAIL");
				String direccion = rs.getString("DIRECCION");
				String direccionEntrega = rs.getString("DIRECCION_ENTREGA");
				String encargadoEntrega = rs.getString("ENCARGADO_ENTREGA");
				
				dto.setCodigo(codigo);
				dto.setRut(rut);
				dto.setInstitucion(institucion);
				dto.setEncargado(encargado);
				dto.setTelefono(telefono);
				dto.setTipo(tipo);
				dto.setBoleta(boleta);
				dto.setEmail(email);
				dto.setDireccion(direccion);
				dto.setDireccionEntrega(direccionEntrega);dto.setEncargadoEntrega(encargadoEntrega);
				
				listaCtasCtes.add(dto);
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
		
		return listaCtasCtes;
	}	
	
	public void actualizarCtaCte(CuentaCorrienteDTO dto) throws Exception{
		Connection conn = null;
	
		try {
			conn = this.conexionFlujo();
			String sql = "UPDATE flujo.dbo.CTAS_CTES set " +
			"RUT='" + dto.getRut() + "' " +
			",INSTITUCION='" + dto.getInstitucion() + "' " +
			",ENCARGADO='" + dto.getEncargado() + "' " +
			",TELEFONO='" + dto.getTelefono() + "' " +
			",TIPO='" + dto.getTipo() + "' " +
			",BOLETA='" + dto.getBoleta() + "' " +
			",EMAIL='" + dto.getEmail() + "' " +
			",DIRECCION='" + dto.getDireccion() + "' " +
			",DIRECCION_ENTREGA='" + dto.getDireccionEntrega() + "' " +
			",ENCARGADO_ENTREGA='" + dto.getEncargadoEntrega() + "' " +
			"where codigo=" + dto.getCodigo();
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
	
	public void ingresarCtaCte(CuentaCorrienteDTO dto) throws Exception{
		Connection conn = null;
	
		try {
			conn = this.conexionFlujo();
			String sql = "INSERT into flujo.dbo.CTAS_CTES (RUT, INSTITUCION,ENCARGADO,TELEFONO,TIPO,BOLETA,EMAIL,DIRECCION,DIRECCION_ENTREGA,ENCARGADO_ENTREGA) " +
			" values( " +
				"'" + dto.getRut() + "' " +
				",'" + dto.getInstitucion() + "' " +
				",'" + dto.getEncargado() + "' " +
				",'" + dto.getTelefono() + "' " +
				",'" + dto.getTipo() + "' " +
				",'" + dto.getBoleta() + "' " +
				",'" + dto.getEmail() + "' " +
				",'" + dto.getDireccion() + "' " +
				",'" + dto.getDireccionEntrega() + "' " +
				",'" + dto.getEncargadoEntrega() + "' " +
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
	
	public ArrayList<RecepcionPlanoDTO> getRecepcionPlanosPendientes() throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		ArrayList<RecepcionPlanoDTO> listaPrestamos = new ArrayList<RecepcionPlanoDTO>();
	
		try {
			conn = this.conexionFlujo();
			String sql = "SELECT id, numero_plano, letra_plano, categoria_plano, requirente, fecha_retiro, fecha_devolucion, observacion " +
						 " FROM flujo.dbo.recepcion_planos WHERE fecha_devolucion is null";
			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				RecepcionPlanoDTO dto = new RecepcionPlanoDTO();
				Long id = rs.getLong("id");
				Integer numero = rs.getInt("numero_plano");
				String letra = rs.getString("letra_plano");
				String categoria = rs.getString("categoria_plano");
				String requirente = rs.getString("requirente");
				Date fechaRetiro = rs.getTimestamp("fecha_retiro");
				Date fechaDevolucion = rs.getTimestamp("fecha_devolucion");
				String observacion = rs.getString("observacion");
				
				dto.setId(id);
				dto.setNumero(numero);
				dto.setLetra(letra);
				dto.setCategoria(categoria);
//				dto.setRequirenteDTO(requirenteDTO);
				dto.setRequirente(requirente);
				dto.setFechaRetiro(fechaRetiro);
				dto.setFechaDevolucion(fechaDevolucion);
				dto.setObservacion(observacion);
				
				listaPrestamos.add(dto);
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
		
		return listaPrestamos;
	}	
	
	public void visarCaratula(Long caratula) throws Exception{
		Connection conn = null;
	
		try {
			conn = this.conexionFlujo();
			String sql = "UPDATE flujo.dbo.ENCABEZADO_CARATULA set " +
			"VISADO='S' " +
			"where CARATULA=" + caratula;
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
	
	public EstadoActualCaratulaDTO getEstadoActualCaratulaPendiente(Long caratula) throws Exception{
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = conexionFlujo();
			
			String COD_SEC_FINALIZADOS = ConstantesCaratula.getParametro("TAB_SECCIONES.FINALIZADOS");

			String sql = "select eac.FECHA_MOV, eac.CARATULA, eac.COD_SECCION, ts.DESC_SECCION from flujo.dbo.ESTADO_ACTUAL_CARATULA eac, flujo.dbo.TAB_SECCIONES ts " +
						" where eac.COD_SECCION = ts.COD_SECCION and  eac.caratula="+caratula+" and eac.COD_SECCION not in ("+COD_SEC_FINALIZADOS+")";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs!=null){
				if(rs.next()){ 		
					EstadoActualCaratulaDTO dto = new EstadoActualCaratulaDTO();
					Timestamp fechaMov = rs.getTimestamp("FECHA_MOV");
					String codSeccion = rs.getString("COD_SECCION");
					String descSeccion = rs.getString("DESC_SECCION");
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String fechaMovS = sdf.format(fechaMov);
					
					dto.setFechaMov(fechaMovS);
					dto.setFechaMovL(fechaMov.getTime());
					SeccionDTO seccionDTO = new SeccionDTO();
					seccionDTO.setCodigo(codSeccion);
					seccionDTO.setDescripcion(descSeccion);
					dto.setSeccionDTO(seccionDTO );
					return dto;
				}   				
			}
			
			return null;

		}catch(SQLException sqle){
			logger.error(sqle);
		}catch(Exception e){
			logger.error(e);
		}finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if (ps != null) {
				try {
					ps.close();
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