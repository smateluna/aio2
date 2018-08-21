package cl.cbrs.aio.util;

import java.util.List;

import org.jboss.logging.Logger;
import org.json.simple.JSONArray;

import cl.cbrs.aio.dto.AnotacionDTO;
import cl.cbrs.aio.dto.EstadoAnotacionDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.TipoAnotacionDTO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.inscripciondigital.vo.AnotacionVO;
import cl.cbrs.inscripciondigital.vo.EstadoAnotacionVO;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.inscripciondigital.vo.TipoAnotacionVO;

public class AnotacionUtil {
	private static final Logger logger = Logger.getLogger(AnotacionUtil.class);


	public AnotacionUtil(){	
	}
	
	
	public JSONArray getAnotaciones(Long fojaini, Long fojafin, Long ano){
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		try {
			anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionPorRevisar(fojaini,fojafin,ano);
			
			anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return anotaciones;
	}
	
	public JSONArray getAnotacionesResumen(Long caratula,Integer borrador,Integer folio){
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		try {

			anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionCaratulaBorradorFolio(caratula,borrador,folio,null);
			
			anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return anotaciones;
	}
	

//	public BorradorDTO getAnotacionDTO(AnotacionVO anotacionVO){
//		BorradorDTO borradorDTO = new BorradorDTO();
//		
//		String direccion = folioRealVO.getDir();
//		
//		ProrealIdVO prorealIdVO = prorealVO.getProRealId();
//		
//		Integer borrador = prorealIdVO.getBorrador();
//		Integer folio = prorealIdVO.getFolio();
//		
//		borradorDTO.setBorrador(borrador);
//		borradorDTO.setDireccion(direccion);
//		borradorDTO.setFolio(folio);
//		
//		return borradorDTO;		
//	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayAnotacionDTO(List<AnotacionVO> anotacionVOList){
		JSONArray anotaciones = new JSONArray();
		
		if(anotacionVOList!=null && anotacionVOList.size()>0){
			
			for(AnotacionVO anotacionVO: anotacionVOList){
				anotaciones.add(this.getAnotacionDTO(anotacionVO));
			}
		}
		
		return anotaciones;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayAnotacionDTOHipotecas(List<cl.cbrs.inscripciondigitalh.vo.AnotacionVO> anotacionVOList){
		JSONArray anotaciones = new JSONArray();
		
		if(anotacionVOList!=null && anotacionVOList.size()>0){
			
			for(cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO: anotacionVOList){
				anotaciones.add(this.getAnotacionDTO(anotacionVO));
			}
		}
		
		return anotaciones;
	}	
	
	public AnotacionDTO getAnotacionDTO(AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();

		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setActo(anotacionVO.getActo());
		
		InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		InscripcionDigitalVO  inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);		
		anotacionDTO.setEstadoAnotacionDTO(this.getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo()));
		anotacionDTO.setTipoAnotacionDTO(this.getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo()));		
		anotacionDTO.setNombreUsuarioCreador(anotacionVO.getNombreUsuarioCreador());
		anotacionDTO.setNombreUsuarioFirmador(anotacionVO.getNombreUsuarioFirmador());
		anotacionDTO.setNombreUsuarioEliminador(anotacionVO.getNombreUsuarioEliminador());
		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setFolio(anotacionVO.getFolio());		
		anotacionDTO.setFechaAprobacion(anotacionVO.getFechaAprobacion());
		anotacionDTO.setNombreUsuarioAprobador(anotacionVO.getNombreUsuarioAprobador());
		
		return anotacionDTO;
	}
	
	public AnotacionDTO getAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();

		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setActo(anotacionVO.getActo());
		
		cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);		
		anotacionDTO.setEstadoAnotacionDTO(this.getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo()));
		anotacionDTO.setTipoAnotacionDTO(this.getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo()));		
		anotacionDTO.setNombreUsuarioCreador(anotacionVO.getNombreUsuarioCreador());
		anotacionDTO.setNombreUsuarioFirmador(anotacionVO.getNombreUsuarioFirmador());
		anotacionDTO.setNombreUsuarioEliminador(anotacionVO.getNombreUsuarioEliminador());
		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setFolio(anotacionVO.getFolio());		
//		anotacionDTO.setFechaAprobacion(anotacionVO.getFechaAprobacion());
//		anotacionDTO.setNombreUsuarioAprobador(anotacionVO.getNombreUsuarioAprobador());
		
		return anotacionDTO;
	}	
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
//			dto.setIdInscripcion(vo.getIdInscripcion());
//			dto.setFechaCreacion(vo.getFechaCreacion());
//			dto.setFechaActualizacion(vo.getFechaActualizacion());
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
//			dto.setFechaFolio(vo.getFechaFolio());
			
				
//			if(conAnotacion){
//				ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionDestino());		
//				dto.setAnotacionsForIdInscripcionDestino(anotacionsForIdInscripcionDestino);
//				
//				ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionOrigen());		
//				dto.setAnotacionsForIdInscripcionOrigen(anotacionsForIdInscripcionOrigen);			
//			}

		}

		return dto;	
	}
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
//			dto.setIdInscripcion(vo.getIdInscripcion());
//			dto.setFechaCreacion(vo.getFechaCreacion());
//			dto.setFechaActualizacion(vo.getFechaActualizacion());
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
//			dto.setFechaFolio(vo.getFechaFolio());
			
				
//			if(conAnotacion){
//				ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionDestino());		
//				dto.setAnotacionsForIdInscripcionDestino(anotacionsForIdInscripcionDestino);
//				
//				ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionOrigen());		
//				dto.setAnotacionsForIdInscripcionOrigen(anotacionsForIdInscripcionOrigen);			
//			}

		}

		return dto;	
	}	
	
	public JSONArray getAnotacionesInscripcion(Long foja, String numero, Long ano, Boolean bis){
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		try {

			anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionesInscripcion(foja, numero, ano, bis);
			
			anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return anotaciones;
	}
	
	public JSONArray getAnotacionesInscripcionHipotecas(Long foja, String numero, Long ano, Boolean bis){
		WsInscripcionDigitalHDelegate wsInscripcionDigitalHDelegate = new WsInscripcionDigitalHDelegate();
		List<cl.cbrs.inscripciondigitalh.vo.AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		try {

			anotacionVOList = wsInscripcionDigitalHDelegate.obtenerAnotacionesInscripcion(foja, numero, ano, bis);
			
			anotaciones = getJSONArrayAnotacionDTOHipotecas(anotacionVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return anotaciones;
	}
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(EstadoAnotacionVO estadoAnotacionVO){
		EstadoAnotacionDTO dto = new EstadoAnotacionDTO();
		
		if(estadoAnotacionVO!=null){
			dto.setIdEstado(estadoAnotacionVO.getIdEstado());
			dto.setDescripcion(estadoAnotacionVO.getDescripcion());
		}

		return dto;	
	}
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.EstadoAnotacionVO estadoAnotacionVO){
		EstadoAnotacionDTO dto = new EstadoAnotacionDTO();
		
		if(estadoAnotacionVO!=null){
			dto.setIdEstado(estadoAnotacionVO.getIdEstado());
			dto.setDescripcion(estadoAnotacionVO.getDescripcion());
		}

		return dto;	
	}
	
	public TipoAnotacionDTO getTipoAnotacionDTO(TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO dto = new TipoAnotacionDTO();
		
		if(tipoAnotacionVO!=null){
			dto.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
			dto.setDescripcion(tipoAnotacionVO.getDescripcion());
		}

		return dto;	
	}
	
	public TipoAnotacionDTO getTipoAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO dto = new TipoAnotacionDTO();
		
		if(tipoAnotacionVO!=null){
			dto.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
			dto.setDescripcion(tipoAnotacionVO.getDescripcion());
		}

		return dto;	
	}	
}