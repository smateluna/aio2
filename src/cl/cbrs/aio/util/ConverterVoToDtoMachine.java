package cl.cbrs.aio.util;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.FlujoDAO;
import cl.cbrs.aio.dto.AnotacionDTO;
import cl.cbrs.aio.dto.EstadoActualCaratulaDTO;
import cl.cbrs.aio.dto.EstadoAnotacionDTO;
import cl.cbrs.aio.dto.EstadoBitacoraDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.TipoAnotacionDTO;
import cl.cbrs.inscripciondigital.vo.AnotacionVO;
import cl.cbrs.inscripciondigital.vo.EstadoAnotacionVO;
import cl.cbrs.inscripciondigital.vo.EstadoBitacoraVO;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.inscripciondigital.vo.TipoAnotacionVO;

public class ConverterVoToDtoMachine {
	
	private static final Logger logger = Logger.getLogger(ConverterVoToDtoMachine.class);
	
	public ConverterVoToDtoMachine(){
		
	}
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
			dto.setIdInscripcion(vo.getIdInscripcion());
			dto.setFechaCreacion(vo.getFechaCreacion());
			dto.setFechaActualizacion(vo.getFechaActualizacion());
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
			dto.setFechaFolio(vo.getFechaFolio());
			
				
			if(conAnotacion){
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionDestino());
				dto.setAnotacionsForIdInscripcionDestino(anotacionsForIdInscripcionDestino);
				
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionOrigen());		
				dto.setAnotacionsForIdInscripcionOrigen(anotacionsForIdInscripcionOrigen);			
			}
			
//			ArrayList<BitacoraDTO> bitacoras = getBitacoraDTOArrayList(vo.getBitacoras());
//			dto.setBitacoras(bitacoras);
		}

		return dto;	
	}	
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
			dto.setIdInscripcion(vo.getIdInscripcion());
			dto.setFechaCreacion(vo.getFechaCreacion());
			dto.setFechaActualizacion(vo.getFechaActualizacion());
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
			dto.setFechaFolio(vo.getFechaFolio());
			
				
			if(conAnotacion){
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionDestino());
				dto.setAnotacionsForIdInscripcionDestino(anotacionsForIdInscripcionDestino);
				
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionOrigen());		
				dto.setAnotacionsForIdInscripcionOrigen(anotacionsForIdInscripcionOrigen);			
			}
			
//			ArrayList<BitacoraDTO> bitacoras = getBitacoraDTOArrayList(vo.getBitacoras());
//			dto.setBitacoras(bitacoras);
		}

		return dto;	
	}	
	
	public InscripcionDigitalDTO getInscripcionDigitalDTO(boolean conAnotacion, cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO vo){
		InscripcionDigitalDTO dto = new InscripcionDigitalDTO();
		
		if(vo!=null){
			dto.setIdInscripcion(vo.getIdInscripcion());
			dto.setFechaCreacion(vo.getFechaCreacion());
			dto.setFechaActualizacion(vo.getFechaActualizacion());
			dto.setFoja(vo.getFoja());
			dto.setNumero(vo.getNumero());
			dto.setAno(vo.getAno());
			dto.setBis(vo.getBis());	
			dto.setFechaFolio(vo.getFechaFolio());
			
				
			if(conAnotacion){
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionDestino = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionDestino());
				dto.setAnotacionsForIdInscripcionDestino(anotacionsForIdInscripcionDestino);
				
				ArrayList<AnotacionDTO> anotacionsForIdInscripcionOrigen = getAnotacionDTOArrayList(vo.getAnotacionsForIdInscripcionOrigen());		
				dto.setAnotacionsForIdInscripcionOrigen(anotacionsForIdInscripcionOrigen);			
			}
			
//			ArrayList<BitacoraDTO> bitacoras = getBitacoraDTOArrayList(vo.getBitacoras());
//			dto.setBitacoras(bitacoras);
		}

		return dto;	
	}	
	
	public ArrayList<AnotacionDTO> getAnotacionDTOArrayList(AnotacionVO[] anotacionVOs){
		ArrayList<AnotacionDTO> anotacionDTOs = new ArrayList<AnotacionDTO>();
		
		if(anotacionVOs!=null){	
			for(int i = anotacionVOs.length-1; i>=0; i--){				
				AnotacionVO anotacionVO = anotacionVOs[i];
				AnotacionDTO anotacionDTO = getAnotacionDTO(anotacionVO);
				anotacionDTOs.add(anotacionDTO);				
			}
			
		}	
		
		return anotacionDTOs;
	}
	
	public ArrayList<AnotacionDTO> getAnotacionDTOArrayList(cl.cbrs.inscripciondigitalh.vo.AnotacionVO[] anotacionVOs){
		ArrayList<AnotacionDTO> anotacionDTOs = new ArrayList<AnotacionDTO>();
		
		if(anotacionVOs!=null){	
			for(int i = anotacionVOs.length-1; i>=0; i--){				
				cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO = anotacionVOs[i];
				AnotacionDTO anotacionDTO = getAnotacionDTO(anotacionVO);
				anotacionDTOs.add(anotacionDTO);				
			}
			
		}	
		
		return anotacionDTOs;
	}
	
	public ArrayList<AnotacionDTO> getAnotacionDTOArrayList(cl.cbrs.inscripciondigitalph.vo.AnotacionVO[] anotacionVOs){
		ArrayList<AnotacionDTO> anotacionDTOs = new ArrayList<AnotacionDTO>();
		
		if(anotacionVOs!=null){	
			for(int i = anotacionVOs.length-1; i>=0; i--){				
				cl.cbrs.inscripciondigitalph.vo.AnotacionVO anotacionVO = anotacionVOs[i];
				AnotacionDTO anotacionDTO = getAnotacionDTO(anotacionVO);
				anotacionDTOs.add(anotacionDTO);				
			}
			
		}	
		
		return anotacionDTOs;
	}	
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(EstadoAnotacionVO estadoAnotacionVO){		
		EstadoAnotacionDTO estadoAnotacionDTO = new EstadoAnotacionDTO();
		estadoAnotacionDTO.setDescripcion(estadoAnotacionVO.getDescripcion());
		estadoAnotacionDTO.setIdEstado(estadoAnotacionVO.getIdEstado());
		
		return estadoAnotacionDTO;
	}
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.EstadoAnotacionVO estadoAnotacionVO){		
		EstadoAnotacionDTO estadoAnotacionDTO = new EstadoAnotacionDTO();
		estadoAnotacionDTO.setDescripcion(estadoAnotacionVO.getDescripcion());
		estadoAnotacionDTO.setIdEstado(estadoAnotacionVO.getIdEstado());
		
		return estadoAnotacionDTO;
	}	
	
	public EstadoAnotacionDTO getEstadoAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.EstadoAnotacionVO estadoAnotacionVO){		
		EstadoAnotacionDTO estadoAnotacionDTO = new EstadoAnotacionDTO();
		estadoAnotacionDTO.setDescripcion(estadoAnotacionVO.getDescripcion());
		estadoAnotacionDTO.setIdEstado(estadoAnotacionVO.getIdEstado());
		
		return estadoAnotacionDTO;
	}	
	
	public TipoAnotacionDTO getTipoAnotacionDTO(TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO tipoAnotacionDTO = new TipoAnotacionDTO();
		tipoAnotacionDTO.setDescripcion(tipoAnotacionVO.getDescripcion());
		tipoAnotacionDTO.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
		
		return tipoAnotacionDTO;
	}
	
	public TipoAnotacionDTO getTipoAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO tipoAnotacionDTO = new TipoAnotacionDTO();
		tipoAnotacionDTO.setDescripcion(tipoAnotacionVO.getDescripcion());
		tipoAnotacionDTO.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
		
		return tipoAnotacionDTO;
	}
	
	public TipoAnotacionDTO getTipoAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.TipoAnotacionVO tipoAnotacionVO){
		TipoAnotacionDTO tipoAnotacionDTO = new TipoAnotacionDTO();
		tipoAnotacionDTO.setDescripcion(tipoAnotacionVO.getDescripcion());
		tipoAnotacionDTO.setIdTipoAnotacion(tipoAnotacionVO.getIdTipoAnotacion());
		
		return tipoAnotacionDTO;
	}	
	
	public AnotacionDTO getAnotacionDTO(AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();
		
		
		anotacionDTO.setActo(anotacionVO.getActo());
		if(anotacionVO.getActo()!=null && anotacionVO.getEstadoAnotacionVo()!=null && anotacionVO.getEstadoAnotacionVo().getIdEstado()!=7){
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
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setFolio(anotacionVO.getFolio());
		
		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCodigoFirma(anotacionVO.getCodigoFirma());
		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setFechaFirma(anotacionVO.getFechaFirma());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
		anotacionDTO.setIdUsuarioCreador(anotacionVO.getIdUsuarioCreador());
		anotacionDTO.setIdUsuarioFirmador(anotacionVO.getIdUsuarioFirmador());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());
		anotacionDTO.setFechaEliminacion(anotacionVO.getFechaEliminacion());
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);
		
		EstadoAnotacionDTO estadoAnotacionDTO = getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo());
		anotacionDTO.setEstadoAnotacionDTO(estadoAnotacionDTO);
					
		TipoAnotacionDTO tipoAnotacionDTO = getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo());
		anotacionDTO.setTipoAnotacionDTO(tipoAnotacionDTO);	
		
		
		InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		InscripcionDigitalVO inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);
		
		
		return anotacionDTO;
	}
	
	public AnotacionDTO getAnotacionDTO(cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();
		
		
		anotacionDTO.setActo(anotacionVO.getActo());
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setFolio(anotacionVO.getFolio());
		
		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCodigoFirma(anotacionVO.getCodigoFirma());
		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setFechaFirma(anotacionVO.getFechaFirma());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
		anotacionDTO.setIdUsuarioCreador(anotacionVO.getIdUsuarioCreador());
		anotacionDTO.setIdUsuarioFirmador(anotacionVO.getIdUsuarioFirmador());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());			
		anotacionDTO.setFechaEliminacion(anotacionVO.getFechaEliminacion());
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);
		
		EstadoAnotacionDTO estadoAnotacionDTO = getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo());
		anotacionDTO.setEstadoAnotacionDTO(estadoAnotacionDTO);
					
		TipoAnotacionDTO tipoAnotacionDTO = getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo());
		anotacionDTO.setTipoAnotacionDTO(tipoAnotacionDTO);	
		
		
		cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);
		
		
		return anotacionDTO;
	}	
	
	public AnotacionDTO getAnotacionDTO(cl.cbrs.inscripciondigitalph.vo.AnotacionVO anotacionVO){
		AnotacionDTO anotacionDTO = new AnotacionDTO();
		
		
		anotacionDTO.setActo(anotacionVO.getActo());
		anotacionDTO.setBorrador(anotacionVO.getBorrador());
		anotacionDTO.setDireccion(anotacionVO.getDireccion());
		anotacionDTO.setRepertorio(anotacionVO.getRepertorio());
		anotacionDTO.setFolio(anotacionVO.getFolio());
		
		anotacionDTO.setCaratula(anotacionVO.getCaratula());
		anotacionDTO.setCodigoFirma(anotacionVO.getCodigoFirma());
		anotacionDTO.setFechaCreacion(anotacionVO.getFechaCreacion());
		anotacionDTO.setFechaFirma(anotacionVO.getFechaFirma());
		anotacionDTO.setIdAnotacion(anotacionVO.getIdAnotacion());
		anotacionDTO.setIdNota(anotacionVO.getIdNota());
		anotacionDTO.setIdUsuarioCreador(anotacionVO.getIdUsuarioCreador());
		anotacionDTO.setIdUsuarioFirmador(anotacionVO.getIdUsuarioFirmador());
		anotacionDTO.setTexto(anotacionVO.getTexto());
		anotacionDTO.setCaratulaMatriz(anotacionVO.getCaratulaMatriz());
		anotacionDTO.setFechaEliminacion(anotacionVO.getFechaEliminacion());
		
		//Se omite el "CBRS\" guardado en BD
		String usuarioCreador = anotacionVO.getNombreUsuarioCreador()!=null?anotacionVO.getNombreUsuarioCreador().replaceAll("CBRS\\\\", ""):"";
		String usuarioFirmador = anotacionVO.getNombreUsuarioFirmador()!=null?anotacionVO.getNombreUsuarioFirmador().replaceAll("CBRS\\\\", ""):"";
		String usuarioEliminador = anotacionVO.getNombreUsuarioEliminador()!=null?anotacionVO.getNombreUsuarioEliminador().replaceAll("CBRS\\\\", ""):"";		
		anotacionDTO.setNombreUsuarioCreador(usuarioCreador);
		anotacionDTO.setNombreUsuarioFirmador(usuarioFirmador);
		anotacionDTO.setNombreUsuarioEliminador(usuarioEliminador);
		
		EstadoAnotacionDTO estadoAnotacionDTO = getEstadoAnotacionDTO(anotacionVO.getEstadoAnotacionVo());
		anotacionDTO.setEstadoAnotacionDTO(estadoAnotacionDTO);
					
		TipoAnotacionDTO tipoAnotacionDTO = getTipoAnotacionDTO(anotacionVO.getTipoAnotacionVo());
		anotacionDTO.setTipoAnotacionDTO(tipoAnotacionDTO);	
		
		
		cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO  inscripcionDigitalByIdInscripcionDestinoVo = anotacionVO.getInscripcionDigitalByIdInscripcionDestinoVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionDestinoDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionDestinoVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionDestinoDTO(inscripcionDigitalByIdInscripcionDestinoDTO);
		
		cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO inscripcionDigitalByIdInscripcionOrigenVo = anotacionVO.getInscripcionDigitalByIdInscripcionOrigenVo();
		InscripcionDigitalDTO inscripcionDigitalByIdInscripcionOrigenDTO = getInscripcionDigitalDTO(false, inscripcionDigitalByIdInscripcionOrigenVo);
		
		anotacionDTO.setInscripcionDigitalByIdInscripcionOrigenDTO(inscripcionDigitalByIdInscripcionOrigenDTO);
		
		
		return anotacionDTO;
	}	
	
	public EstadoBitacoraDTO getEstadoBitacoraDTO(EstadoBitacoraVO estadoBitacoraVO){
		EstadoBitacoraDTO estadoBitacoraDTO = new EstadoBitacoraDTO();
		
		estadoBitacoraDTO.setDescripcion(estadoBitacoraVO.getDescripcion());
		estadoBitacoraDTO.setIdEstado(estadoBitacoraVO.getIdEstado());
		estadoBitacoraDTO.setTipo(estadoBitacoraVO.getTipo());
		
		return estadoBitacoraDTO;
	}
	
//	public BitacoraDTO getBitacoraDTO(BitacoraVO bitacoraVO){
//		BitacoraDTO bitacoraDTO = new BitacoraDTO();
//			
//		bitacoraDTO.setFecha(bitacoraVO.getFecha());
//		bitacoraDTO.setIdBitacora(bitacoraVO.getIdBitacora());
//		bitacoraDTO.setIdUsuario(bitacoraVO.getIdUsuario());
//		bitacoraDTO.setNombreUsuario(bitacoraVO.getNombreUsuario());
//		
//		EstadoBitacoraDTO estadoBitacoraDTO = getEstadoBitacoraDTO(bitacoraVO.getEstadoBitacoraVo());
//		
//		bitacoraDTO.setEstadoBitacoraDTO(estadoBitacoraDTO);
//		return bitacoraDTO;
//	}
//	
//	public ArrayList<BitacoraDTO> getBitacoraDTOArrayList(BitacoraVO[] bitacoraVOs){
//		ArrayList<BitacoraDTO> bitacoraDTOs = new ArrayList<BitacoraDTO>();
//		
//		if(bitacoraVOs!=null){	
//			for(BitacoraVO bitacoraVO: bitacoraVOs){	
//				BitacoraDTO bitacoraDTO = getBitacoraDTO(bitacoraVO);
//				bitacoraDTOs.add(bitacoraDTO);				
//			}
//		}			
//		return bitacoraDTOs;
//	}	
}