package cl.cbrs.aio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.locator.ServiceLocatorException;
import cl.cbrs.aio.dto.AtencionInformacionesDTO;
import cl.cbrs.aio.dto.InformacionesDTO;
import cl.cbrs.caratula.flujo.vo.CanalVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoActualCaratulaVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TipoFormularioVO;


public class InformacionesDAO extends AbstractJdbcDao {	
	
	private static final Logger logger = Logger.getLogger(InformacionesDAO.class);
	
	public InformacionesDAO(){
		
	}
	
	public boolean create(InformacionesDTO informacionesDTO) throws SQLException, ServiceLocatorException,GeneralException{
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = this.conexionFlujo();

			String sql = "INSERT INTO INFORMACIONES_ATENCION (USUARIO_INFORMACIONES, FECHA_ATENCION, RUT_CLIENTE) "+
			"VALUES (?, CONVERT(DATETIME, GETDATE(), 102), ?)";

			PreparedStatement ps = conn.prepareStatement(sql);
					
			ps.setString(1, informacionesDTO.getUsuario());
			ps.setString(2, informacionesDTO.getRut());
			
			ps.execute();
			
			return true;
          
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
	
	
	public ArrayList<AtencionInformacionesDTO> getAll() throws SQLException, ServiceLocatorException,GeneralException{

		Connection conn = null;
		ResultSet rs = null;
		ArrayList<AtencionInformacionesDTO> listaInformaciones = new ArrayList<AtencionInformacionesDTO>();

		try {
			conn = this.conexionFlujo();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			Date fecha = new Date();

			String sql = "SELECT COUNT(*) AS TOTAL, USUARIO_INFORMACIONES "
					+ " FROM flujo.dbo.INFORMACIONES_ATENCION "
					+ " WHERE (FECHA_ATENCION>=CONVERT(DATETIME, '" + sdf.format(fecha) + " 00:00:00', 102) AND FECHA_ATENCION<=CONVERT(DATETIME, '" + sdf.format(fecha) + " 23:59:59', 102)) "
					+ " GROUP BY USUARIO_INFORMACIONES";

			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()){ 
						
				AtencionInformacionesDTO info = new AtencionInformacionesDTO();
				
				Long total = rs.getLong("TOTAL");
				String usuario = rs.getString("USUARIO_INFORMACIONES");
							
				info.setTotal(total);
				info.setUsuario(usuario);
				
				listaInformaciones.add(info);
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
		return listaInformaciones;
	} 	
	
	public AtencionInformacionesDTO get(String usuarioLog) throws SQLException, ServiceLocatorException,GeneralException{

		Connection conn = null;
		ResultSet rs = null;
		AtencionInformacionesDTO informaciones = null;

		try {
			conn = this.conexionFlujo();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			Date fecha = new Date();

			String sql = "SELECT count(distinct(rut_cliente)) AS TOTAL "
					+ " FROM flujo.dbo.INFORMACIONES_ATENCION "
					+ " WHERE (FECHA_ATENCION>=CONVERT(DATETIME, '" + sdf.format(fecha) + " 00:00:00', 102) AND FECHA_ATENCION<=CONVERT(DATETIME, '" + sdf.format(fecha) + " 23:59:59', 102)) " 
					+ " AND usuario_informaciones='"+usuarioLog+"'";

			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()){ 
						
				informaciones = new AtencionInformacionesDTO();
				
				Long total = rs.getLong("TOTAL");
							
				informaciones.setTotal(total);
				
			}   
			
			if(informaciones==null){
				informaciones = new AtencionInformacionesDTO();
				informaciones.setTotal(0L);
				informaciones.setUsuario(usuarioLog);
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
		return informaciones;
	} 
	
	public List<CaratulaVO> getAllPorSeccionCanalTipoFormulario() throws SQLException, ServiceLocatorException,GeneralException{

		Connection conn = null;
		ResultSet rs = null;
		List<CaratulaVO> listaCaratulas = new ArrayList<CaratulaVO>();

		try {
			conn = this.conexionFlujo();

			String sql = "select EAC.CARATULA, EAC.FECHA_MOV, EC.ID_CANAL from flujo.dbo.ESTADO_ACTUAL_CARATULA EAC inner join"+ 
			" flujo.dbo.ENCABEZADO_CARATULA EC on EAC.CARATULA = EC.CARATULA inner join"+
			" flujo.dbo.T_INSCRIPCION TI on EC.CARATULA = TI.CARATULA"+
			" where EAC.COD_SECCION='01' "+
			" and TI.REGISTRO=1 "+
			" and TI.TIPO_FORM=1 "+
			" and ec.CARATULA not in (select CARATULA from flujo.dbo.REL_FORMULARIO_TAREAS where CARATULA=ec.CARATULA and COD_TAREA='116')"+
			" ORDER BY EAC.CARATULA desc";

			PreparedStatement ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while(rs.next()){ 
						
				CaratulaVO caratulaVO = new CaratulaVO();
				
				Long caratula = rs.getLong("CARATULA");
				Timestamp fechaMov = rs.getTimestamp("FECHA_MOV");
				Date date = new java.util.Date(fechaMov.getTime());
				Integer canalInt = rs.getInt("ID_CANAL");
							
				caratulaVO.setNumeroCaratula(caratula);
				EstadoActualCaratulaVO eacVO = new EstadoActualCaratulaVO();
				eacVO.setFechaMov(date);
				SeccionVO seccion = new SeccionVO();
				seccion.setDescripcion("CAJA");
				eacVO.setSeccion(seccion);
				eacVO.setDescripcionEnFlujo("En Proceso");
				CanalVO canal = new CanalVO();
				if(canalInt==1){
					canal.setId(1);
					canal.setDescripcion("CAJA");
				}else{
					canal.setId(2);
					canal.setDescripcion("WEB");
				}
				caratulaVO.setCanal(canal);
				caratulaVO.setEstadoActualCaratula(eacVO);
				InscripcionCitadaVO[] ins = new InscripcionCitadaVO[1];
				InscripcionCitadaVO ins1 = new InscripcionCitadaVO();
				ins1.setFoja(0);
				ins1.setNumero(0);
				ins1.setAno(0);
				ins[0]=ins1;
				caratulaVO.setInscripciones(ins);
				TipoFormularioVO tipForm = new TipoFormularioVO();
				tipForm.setTipo(1);
				tipForm.setDescripcion("Inscripción");
				caratulaVO.setTipoFormulario(tipForm);
				
				listaCaratulas.add(caratulaVO);
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
		return listaCaratulas;
	}
	
		
}