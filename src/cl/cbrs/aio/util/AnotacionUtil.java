package cl.cbrs.aio.util;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import cl.cbr.util.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.AnotacionDTO;
import cl.cbrs.aio.dto.EstadoActualCaratulaDTO;
import cl.cbrs.aio.dto.EstadoAnotacionDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.TipoAnotacionDTO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalHDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalPHDelegate;
import cl.cbrs.inscripciondigital.vo.AnotacionVO;
import cl.cbrs.inscripciondigital.vo.EstadoAnotacionVO;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.inscripciondigital.vo.TipoAnotacionVO;

public class AnotacionUtil {
	private static final Logger logger = Logger.getLogger(AnotacionUtil.class);


	public AnotacionUtil(){	
	}
	
	
	public JSONArray getAnotaciones(Long fojaini, Long fojafin, Long ano) throws GeneralException{
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionPorRevisar(fojaini,fojafin,ano);
		anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);
		
		return anotaciones;
	}
	
	public JSONArray getAnotacionesResumen(Long caratula,Integer borrador,Integer folio) throws GeneralException{
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionCaratulaBorradorFolio(caratula,borrador,folio,null);			
		anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);
		
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
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayAnotacionDTOProhibiciones(List<cl.cbrs.inscripciondigitalph.vo.AnotacionVO> anotacionVOList){
		JSONArray anotaciones = new JSONArray();
		
		if(anotacionVOList!=null && anotacionVOList.size()>0){
			
			for(cl.cbrs.inscripciondigitalph.vo.AnotacionVO anotacionVO: anotacionVOList){
				anotaciones.add(this.getAnotacionDTO(anotacionVO));
			}
		}
		
		return anotaciones;
	}
	
	public AnotacionDTO getAnotacionDTO(AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();

		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setActo(anotacionVO.getActo());
		
		if(anotacionVO.getActo()!=null){
			try{			
				//Si acto es transferencia, revisar estado caratula (Anotacion pendiente entrega)
				String strActosTransferencia = TablaValores.getValor("ws_inscripciondigital.parametros", "ACTOS_TRANSFERENCIA", "valor");
				if(strActosTransferencia!=null){
					if(ArrayUtils.contains( strActosTransferencia.split(","), anotacionVO.getActo().trim() )){
						Long caratula = anotacionVO.getCaratula();
						EstadoActualCaratulaDTO eacDTO = new FlujoDAO().getEstadoActualCaratulaPendiente(caratula);
						if(eacDTO != null){
							anotacionDTO.setPendiente(true);
							anotacionDTO.setEstadoActualCaratulaPendienteDTO(eacDTO);
						}
					}
				}
			} catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		
		InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		InscripcionDigitalVO  inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);		
		anotacionDTO.setEstadoAnotacionDTO(this.getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo()));
		anotacionDTO.setTipoAnotacionDTO(this.getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo()));	
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);
		
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
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
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
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);

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
	
	public AnotacionDTO getAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();

		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setActo(anotacionVO.getActo());
		
		cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = this.getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);		
		anotacionDTO.setEstadoAnotacionDTO(this.getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo()));
		anotacionDTO.setTipoAnotacionDTO(this.getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo()));	
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);

		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setFolio(anotacionVO.getFolio());		
		
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
			dto.setFechaFolio(vo.getFechaFolio());
			
				
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
			dto.setFechaFolio(vo.getFechaFolio());
			
				
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
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
			dto.setFechaFolio(vo.getFechaFolio());
		}

		return dto;	
	}
	
	public JSONArray getAnotacionesInscripcion(Long foja, String numero, Long ano, Boolean bis) throws GeneralException{
		WsInscripcionDigitalDelegate wsInscripcionDigitalDelegate = new WsInscripcionDigitalDelegate();						
		List<AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		anotacionVOList = wsInscripcionDigitalDelegate.obtenerAnotacionesInscripcion(foja, numero, ano, bis);
		anotaciones = getJSONArrayAnotacionDTO(anotacionVOList);
		
		return anotaciones;
	}
	
	public JSONArray getAnotacionesInscripcionHipotecas(Long foja, String numero, Long ano, Boolean bis) throws GeneralException{
		WsInscripcionDigitalHDelegate wsInscripcionDigitalHDelegate = new WsInscripcionDigitalHDelegate();
		List<cl.cbrs.inscripciondigitalh.vo.AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();
		
		anotacionVOList = wsInscripcionDigitalHDelegate.obtenerAnotacionesInscripcion(foja, numero, ano, bis);	
		anotaciones = getJSONArrayAnotacionDTOHipotecas(anotacionVOList);
		
		return anotaciones;
	}
	
	public JSONArray getAnotacionesInscripcionProhibiciones(Long foja, String numero, Long ano, Boolean bis) throws GeneralException{
		WsInscripcionDigitalPHDelegate wsInscripcionDigitalPHDelegate = new WsInscripcionDigitalPHDelegate();
		List<cl.cbrs.inscripciondigitalph.vo.AnotacionVO> anotacionVOList;
		JSONArray anotaciones = new JSONArray();

		anotacionVOList = wsInscripcionDigitalPHDelegate.obtenerAnotacionesInscripcion(foja, numero, ano, bis);	
		anotaciones = getJSONArrayAnotacionDTOProhibiciones(anotacionVOList);
		
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
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.EstadoAnotacionVO estadoAnotacionVO){
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
	
	public TipoAnotacionDTO getTipoAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO dto = new TipoAnotacionDTO();
		
		if(tipoAnotacionVO!=null){
			dto.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
			dto.setDescripcion(tipoAnotacionVO.getDescripcion());
		}

		return dto;	
	}	
}