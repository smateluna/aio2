package cl.cbrs.aio.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

import cl.cbrs.aio.dto.AnoBodegaDTO;
import cl.cbrs.aio.dto.BodegaDTO;
import cl.cbrs.aio.dto.EstadoDTO;
import cl.cbrs.aio.dto.ObservacionSolicitudDTO;
import cl.cbrs.aio.dto.OrigenDTO;
import cl.cbrs.aio.dto.SolicitudDTO;
import cl.cbrs.aio.dto.TipoObservacionDTO;
import cl.cbrs.aio.dto.TrazaSolicitudDTO;
import cl.cbrs.inscripciondigital.vo.AnoBodegaVO;
import cl.cbrs.inscripciondigital.vo.BodegaVO;
import cl.cbrs.inscripciondigital.vo.EstadoVO;
import cl.cbrs.inscripciondigital.vo.ObservacionSolicitudVO;
import cl.cbrs.inscripciondigital.vo.OrigenVO;
import cl.cbrs.inscripciondigital.vo.SolicitudVO;
import cl.cbrs.inscripciondigital.vo.TipoObservacionVO;
import cl.cbrs.inscripciondigital.vo.TrazaSolicitudVO;

public class SolicitudConverter {

	public SolicitudConverter(){

	}

	public SolicitudDTO getSolicitudDTO(SolicitudVO vo){
		SolicitudDTO dto = new SolicitudDTO();


		//		private Long idSolicitud;
		//		private Date fechaSolicitud;
		//		private OrigenDTO origenDTO;
		//		private String usuario;
		//		private Long rut;
		//		private String dv;
		//		private EstadoDTO estadoDTO;
		//		private Date fechaEstado;
		//		private BodegaDTO bodegaDTO;

		dto.setIdSolicitud(vo.getIdSolicitud());
		dto.setFechaSolicitud(vo.getFechaSolicitud());

		//origen block!
		OrigenDTO origenDTO = new OrigenDTO();	
		if(vo.getOrigen()!=null){			
			OrigenVO origenVO = vo.getOrigen();

			origenDTO.setIdOrigen(origenVO.getIdOrigen());		
			origenDTO.setDescripcion(origenVO.getDescripcion());

			//TODO
//			if(origenVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: origenVO.getListaSolicitudVO()){
//
//				}	
//			}
		}

		dto.setOrigenDTO(origenDTO);

		//fin origen block!

		dto.setUsuario(vo.getUsuario());
		dto.setRut(vo.getRut());
		dto.setDv(vo.getDv());


		//estado block

		EstadoDTO estadoDTO = new EstadoDTO();
		if(vo.getEstado()!=null){
			EstadoVO estadoVO = vo.getEstado();

			estadoDTO.setDescripcion(estadoVO.getDescripcion());
			estadoDTO.setIdEstado(estadoVO.getIdEstado());

			//TODO
//			if(estadoVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: estadoVO.getListaSolicitudVO()){
//
//				}	
//			}	
		}

		dto.setEstadoDTO(estadoDTO);
		//fin estado block!

		dto.setFechaEstado(vo.getFechaEstado());

		//bodega block

		BodegaDTO bodegaDTO = new BodegaDTO();
		if(vo.getBodega()!=null){
			BodegaVO bodegaVO = vo.getBodega();

			bodegaDTO.setDescripcion(bodegaVO.getDescripcion());
			bodegaDTO.setIdBodega(bodegaVO.getIdBodega());


			ArrayList<AnoBodegaDTO> anoBodegaDTOs = new ArrayList<AnoBodegaDTO>();

			//TODO
			if(bodegaVO.getListaAnoBodegaVO()!=null){

				for(AnoBodegaVO anoBodegaVO: bodegaVO.getListaAnoBodegaVO()){

					AnoBodegaDTO anoBodegaDTO = new AnoBodegaDTO();

					anoBodegaDTO.setAno(anoBodegaVO.getAno());
					anoBodegaDTO.setId(anoBodegaVO.getId());		
					
					//TODO
					//anoBodegaDTO.setBodegaDTO(bodegaDTO)

					anoBodegaDTOs.add(anoBodegaDTO);
				}	
			}
			
			bodegaDTO.setListaAnoBodegaDTO(anoBodegaDTOs);

		}
		
		dto.setBodegaDTO(bodegaDTO);
		//fin bodega block!		

		dto.setImpreso(vo.getImpreso());
		dto.setFoja(vo.getFoja());
		dto.setNumero(vo.getNumero());
		dto.setAno(vo.getAno());
		dto.setBis(vo.getBis());
		dto.setNombre(vo.getNombre());
		dto.setTomo(vo.getTomo());
		
		//lista traza solicitud block
        ArrayList<TrazaSolicitudDTO> listaTrazaSolicitudDTO = new ArrayList<TrazaSolicitudDTO>();
		
		
		if(vo.getListaTrazaSolicitudVO()!=null){
			
			for(TrazaSolicitudVO trazaSolicitudVO: vo.getListaTrazaSolicitudVO()){
				TrazaSolicitudDTO trazaSolicitudDTO = new TrazaSolicitudDTO();
				
				trazaSolicitudDTO.setFechaEstado(trazaSolicitudVO.getFechaEstado());
				trazaSolicitudDTO.setIdEstado(trazaSolicitudVO.getIdEstado());
				trazaSolicitudDTO.setIdTraza(trazaSolicitudVO.getIdTraza());
				
				//TODO EL SET DE SOLICITUD DTO VO
				
				listaTrazaSolicitudDTO.add(trazaSolicitudDTO);
				
			}

		}
		
		dto.setListaTrazaSolicitudDTO(listaTrazaSolicitudDTO);
		//fin  traza solicitud block
		
		
		
		
		//observacion solicitud block
		
		ObservacionSolicitudDTO observacionSolicitudDTO = new ObservacionSolicitudDTO();
		if(vo.getObservacionSolicitudVO()!=null){
			ObservacionSolicitudVO observacionSolicitudVO = vo.getObservacionSolicitudVO();
			
			Integer idObservacion = observacionSolicitudVO.getIdObservacion();
			String tituloObservacion = observacionSolicitudVO.getTituloObservacion();
			String textoObservacion = observacionSolicitudVO.getObsTexto();
			
			observacionSolicitudDTO.setId(idObservacion);
			observacionSolicitudDTO.setTitulo(tituloObservacion);
			observacionSolicitudDTO.setTexto(textoObservacion);
			
			TipoObservacionVO tipoObservacionVO = observacionSolicitudVO.getTipoObservacionVO();
			TipoObservacionDTO tipoObservacionDTO = new TipoObservacionDTO();
			if(tipoObservacionVO!=null){
				Integer idTipoObservacion = tipoObservacionVO.getIdTipoObservacion();
				String descripcionTipo = tipoObservacionVO.getDescripcionTipo();
				
				tipoObservacionDTO.setId(idTipoObservacion);
				tipoObservacionDTO.setDescripcion(descripcionTipo);
			}

			observacionSolicitudDTO.setTipoObservacionDTO(tipoObservacionDTO);

		}

		dto.setObservacionSolicitudDTO(observacionSolicitudDTO);
		
		return dto;
	}
	
	public SolicitudDTO getSolicitudDTO(cl.cbrs.inscripciondigitalh.vo.SolicitudVO vo){
		SolicitudDTO dto = new SolicitudDTO();

		dto.setIdSolicitud(vo.getIdSolicitud());
		dto.setFechaSolicitud(vo.getFechaSolicitud());

		//origen block!
		OrigenDTO origenDTO = new OrigenDTO();	
		if(vo.getOrigen()!=null){			
			cl.cbrs.inscripciondigitalh.vo.OrigenVO origenVO = vo.getOrigen();

			origenDTO.setIdOrigen(origenVO.getIdOrigen());		
			origenDTO.setDescripcion(origenVO.getDescripcion());

			//TODO
//			if(origenVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: origenVO.getListaSolicitudVO()){
//
//				}	
//			}
		}

		dto.setOrigenDTO(origenDTO);

		//fin origen block!

		dto.setUsuario(vo.getUsuario());
		dto.setRut(vo.getRut());
		dto.setDv(vo.getDv());


		//estado block

		EstadoDTO estadoDTO = new EstadoDTO();
		if(vo.getEstado()!=null){
			cl.cbrs.inscripciondigitalh.vo.EstadoVO estadoVO = vo.getEstado();

			estadoDTO.setDescripcion(estadoVO.getDescripcion());
			estadoDTO.setIdEstado(estadoVO.getIdEstado());

			//TODO
//			if(estadoVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: estadoVO.getListaSolicitudVO()){
//
//				}	
//			}	
		}

		dto.setEstadoDTO(estadoDTO);
		//fin estado block!

		dto.setFechaEstado(vo.getFechaEstado());

		//bodega block

		BodegaDTO bodegaDTO = new BodegaDTO();
		if(vo.getBodega()!=null){
			cl.cbrs.inscripciondigitalh.vo.BodegaVO bodegaVO = vo.getBodega();

			bodegaDTO.setDescripcion(bodegaVO.getDescripcion());
			bodegaDTO.setIdBodega(bodegaVO.getIdBodega());


			ArrayList<AnoBodegaDTO> anoBodegaDTOs = new ArrayList<AnoBodegaDTO>();

			//TODO
			if(bodegaVO.getListaAnoBodegaVO()!=null){

				for(cl.cbrs.inscripciondigitalh.vo.AnoBodegaVO anoBodegaVO: bodegaVO.getListaAnoBodegaVO()){

					AnoBodegaDTO anoBodegaDTO = new AnoBodegaDTO();

					anoBodegaDTO.setAno(anoBodegaVO.getAno());
					anoBodegaDTO.setId(anoBodegaVO.getId());		
					
					//TODO
					//anoBodegaDTO.setBodegaDTO(bodegaDTO)

					anoBodegaDTOs.add(anoBodegaDTO);
				}	
			}
			
			bodegaDTO.setListaAnoBodegaDTO(anoBodegaDTOs);

		}
		
		dto.setBodegaDTO(bodegaDTO);
		//fin bodega block!		

		dto.setImpreso(vo.getImpreso());
		dto.setFoja(vo.getFoja());
		dto.setNumero(vo.getNumero());
		dto.setAno(vo.getAno());
		dto.setBis(vo.getBis());
		dto.setNombre(vo.getNombre());
		dto.setTomo(vo.getTomo());
		
		//lista traza solicitud block
        ArrayList<TrazaSolicitudDTO> listaTrazaSolicitudDTO = new ArrayList<TrazaSolicitudDTO>();
		
		
		if(vo.getListaTrazaSolicitudVO()!=null){
			
			for(cl.cbrs.inscripciondigitalh.vo.TrazaSolicitudVO trazaSolicitudVO: vo.getListaTrazaSolicitudVO()){
				TrazaSolicitudDTO trazaSolicitudDTO = new TrazaSolicitudDTO();
				
				trazaSolicitudDTO.setFechaEstado(trazaSolicitudVO.getFechaEstado());
				trazaSolicitudDTO.setIdEstado(trazaSolicitudVO.getIdEstado());
				trazaSolicitudDTO.setIdTraza(trazaSolicitudVO.getIdTraza());
				
				//TODO EL SET DE SOLICITUD DTO VO
				
				listaTrazaSolicitudDTO.add(trazaSolicitudDTO);
				
			}

		}
		
		dto.setListaTrazaSolicitudDTO(listaTrazaSolicitudDTO);
		//fin  traza solicitud block
		
		
		
		
		//observacion solicitud block
		
		ObservacionSolicitudDTO observacionSolicitudDTO = new ObservacionSolicitudDTO();
		if(vo.getObservacionSolicitudVO()!=null){
			cl.cbrs.inscripciondigitalh.vo.ObservacionSolicitudVO observacionSolicitudVO = vo.getObservacionSolicitudVO();
			
			Integer idObservacion = observacionSolicitudVO.getIdObservacion();
			String tituloObservacion = observacionSolicitudVO.getTituloObservacion();
			String textoObservacion = observacionSolicitudVO.getObsTexto();
			
			observacionSolicitudDTO.setId(idObservacion);
			observacionSolicitudDTO.setTitulo(tituloObservacion);
			observacionSolicitudDTO.setTexto(textoObservacion);
			
			cl.cbrs.inscripciondigitalh.vo.TipoObservacionVO tipoObservacionVO = observacionSolicitudVO.getTipoObservacionVO();
			TipoObservacionDTO tipoObservacionDTO = new TipoObservacionDTO();
			if(tipoObservacionVO!=null){
				Integer idTipoObservacion = tipoObservacionVO.getIdTipoObservacion();
				String descripcionTipo = tipoObservacionVO.getDescripcionTipo();
				
				tipoObservacionDTO.setId(idTipoObservacion);
				tipoObservacionDTO.setDescripcion(descripcionTipo);
			}

			observacionSolicitudDTO.setTipoObservacionDTO(tipoObservacionDTO);

		}

		dto.setObservacionSolicitudDTO(observacionSolicitudDTO);
		
		return dto;
	}
	
	public SolicitudDTO getSolicitudDTO(cl.cbrs.inscripciondigitalph.vo.SolicitudVO vo){
		SolicitudDTO dto = new SolicitudDTO();

		dto.setIdSolicitud(vo.getIdSolicitud());
		dto.setFechaSolicitud(vo.getFechaSolicitud());

		//origen block!
		OrigenDTO origenDTO = new OrigenDTO();	
		if(vo.getOrigen()!=null){			
			cl.cbrs.inscripciondigitalph.vo.OrigenVO origenVO = vo.getOrigen();

			origenDTO.setIdOrigen(origenVO.getIdOrigen());		
			origenDTO.setDescripcion(origenVO.getDescripcion());

			//TODO
//			if(origenVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: origenVO.getListaSolicitudVO()){
//
//				}	
//			}
		}

		dto.setOrigenDTO(origenDTO);

		//fin origen block!

		dto.setUsuario(vo.getUsuario());
		dto.setRut(vo.getRut());
		dto.setDv(vo.getDv());


		//estado block

		EstadoDTO estadoDTO = new EstadoDTO();
		if(vo.getEstado()!=null){
			cl.cbrs.inscripciondigitalph.vo.EstadoVO estadoVO = vo.getEstado();

			estadoDTO.setDescripcion(estadoVO.getDescripcion());
			estadoDTO.setIdEstado(estadoVO.getIdEstado());

			//TODO
//			if(estadoVO.getListaSolicitudVO()!=null){
//
//				for(SolicitudVO solicitudVO: estadoVO.getListaSolicitudVO()){
//
//				}	
//			}	
		}

		dto.setEstadoDTO(estadoDTO);
		//fin estado block!

		dto.setFechaEstado(vo.getFechaEstado());

		//bodega block

		BodegaDTO bodegaDTO = new BodegaDTO();
		if(vo.getBodega()!=null){
			cl.cbrs.inscripciondigitalph.vo.BodegaVO bodegaVO = vo.getBodega();

			bodegaDTO.setDescripcion(bodegaVO.getDescripcion());
			bodegaDTO.setIdBodega(bodegaVO.getIdBodega());


			ArrayList<AnoBodegaDTO> anoBodegaDTOs = new ArrayList<AnoBodegaDTO>();

			//TODO
			if(bodegaVO.getListaAnoBodegaVO()!=null){

				for(cl.cbrs.inscripciondigitalph.vo.AnoBodegaVO anoBodegaVO: bodegaVO.getListaAnoBodegaVO()){

					AnoBodegaDTO anoBodegaDTO = new AnoBodegaDTO();

					anoBodegaDTO.setAno(anoBodegaVO.getAno());
					anoBodegaDTO.setId(anoBodegaVO.getId());		
					
					//TODO
					//anoBodegaDTO.setBodegaDTO(bodegaDTO)

					anoBodegaDTOs.add(anoBodegaDTO);
				}	
			}
			
			bodegaDTO.setListaAnoBodegaDTO(anoBodegaDTOs);

		}
		
		dto.setBodegaDTO(bodegaDTO);
		//fin bodega block!		

		dto.setImpreso(vo.getImpreso());
		dto.setFoja(vo.getFoja());
		dto.setNumero(vo.getNumero());
		dto.setAno(vo.getAno());
		dto.setBis(vo.getBis());
		dto.setNombre(vo.getNombre());
		dto.setTomo(vo.getTomo());
		
		//lista traza solicitud block
        ArrayList<TrazaSolicitudDTO> listaTrazaSolicitudDTO = new ArrayList<TrazaSolicitudDTO>();
		
		
		if(vo.getListaTrazaSolicitudVO()!=null){
			
			for(cl.cbrs.inscripciondigitalph.vo.TrazaSolicitudVO trazaSolicitudVO: vo.getListaTrazaSolicitudVO()){
				TrazaSolicitudDTO trazaSolicitudDTO = new TrazaSolicitudDTO();
				
				trazaSolicitudDTO.setFechaEstado(trazaSolicitudVO.getFechaEstado());
				trazaSolicitudDTO.setIdEstado(trazaSolicitudVO.getIdEstado());
				trazaSolicitudDTO.setIdTraza(trazaSolicitudVO.getIdTraza());
				
				//TODO EL SET DE SOLICITUD DTO VO
				
				listaTrazaSolicitudDTO.add(trazaSolicitudDTO);
				
			}

		}
		
		dto.setListaTrazaSolicitudDTO(listaTrazaSolicitudDTO);
		//fin  traza solicitud block
		
		
		
		
		//observacion solicitud block
		
		ObservacionSolicitudDTO observacionSolicitudDTO = new ObservacionSolicitudDTO();
		if(vo.getObservacionSolicitudVO()!=null){
			cl.cbrs.inscripciondigitalph.vo.ObservacionSolicitudVO observacionSolicitudVO = vo.getObservacionSolicitudVO();
			
			Integer idObservacion = observacionSolicitudVO.getIdObservacion();
			String tituloObservacion = observacionSolicitudVO.getTituloObservacion();
			String textoObservacion = observacionSolicitudVO.getObsTexto();
			
			observacionSolicitudDTO.setId(idObservacion);
			observacionSolicitudDTO.setTitulo(tituloObservacion);
			observacionSolicitudDTO.setTexto(textoObservacion);
			
			cl.cbrs.inscripciondigitalph.vo.TipoObservacionVO tipoObservacionVO = observacionSolicitudVO.getTipoObservacionVO();
			TipoObservacionDTO tipoObservacionDTO = new TipoObservacionDTO();
			if(tipoObservacionVO!=null){
				Integer idTipoObservacion = tipoObservacionVO.getIdTipoObservacion();
				String descripcionTipo = tipoObservacionVO.getDescripcionTipo();
				
				tipoObservacionDTO.setId(idTipoObservacion);
				tipoObservacionDTO.setDescripcion(descripcionTipo);
			}

			observacionSolicitudDTO.setTipoObservacionDTO(tipoObservacionDTO);

		}

		dto.setObservacionSolicitudDTO(observacionSolicitudDTO);
		
		return dto;
	}	
	
}
