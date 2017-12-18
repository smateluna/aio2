package cl.cbrs.aio.util;

import java.util.ArrayList;

import cl.cbrs.aio.dto.CartelDTO;
import cl.cbrs.aio.dto.CertificadoCartelDTO;
import cl.cbrs.aio.dto.EstadoCertificadoDTO;
import cl.cbrs.aio.dto.TipoArchivoDTO;
import cl.cbrs.carteles.vo.CertificadoCartelVO;
import cl.cbrs.carteles.vo.EstadoCertificadoVO;
import cl.cbrs.carteles.vo.TipoArchivoVO;

public class CartelConverter {
	
	public CartelConverter(){
		
	}
	
	public TipoArchivoDTO obtenerTipoArchivoDTO(TipoArchivoVO vo){
		TipoArchivoDTO dto = new TipoArchivoDTO();
		
		dto.setTipoArchivo(vo.getTipo());
		dto.setDescripcion(vo.getDescripcion());
				
		return dto;
	}
	
	public EstadoCertificadoDTO obtenerEstadoCertificadoDTO(EstadoCertificadoVO vo){
		EstadoCertificadoDTO dto = new EstadoCertificadoDTO();
		
		dto.setDescripcion(vo.getDescripcion());
		dto.setIdEstado(vo.getIdEstado());
				
		return dto;
	}
	
	public CertificadoCartelDTO obtenerCertificadoCartelDTO(CertificadoCartelVO vo){
		CertificadoCartelDTO dto = new CertificadoCartelDTO();
			
		dto.setAno(vo.getAno());
		dto.setBis(vo.getBis());
		dto.setCaratula(vo.getCaratula());
		
		EstadoCertificadoDTO estadoCertificadoDTO = obtenerEstadoCertificadoDTO(vo.getEstado());
		dto.setEstadoCertificadoDTO(estadoCertificadoDTO);
		
		dto.setFechaCreacion(vo.getFechaCreacion());
		dto.setFechaFirma(vo.getFechaFirma());
		dto.setIdCertificado(vo.getIdCertificado());
		dto.setMes(vo.getMes());
		dto.setNumero(vo.getNumero());
		dto.setPagDesde(vo.getPagDesde());
		dto.setPagHasta(vo.getPagHasta());
		dto.setIdReg(vo.getIdReg());
		
		TipoArchivoDTO tipoArchivoDTO = obtenerTipoArchivoDTO(vo.getTipoArchivoVO());
		dto.setTipoArchivoDTO(tipoArchivoDTO);
		
		return dto;
	}
	
	public ArrayList<CertificadoCartelDTO> obtenerArrayCertificadoCartelDTO(CertificadoCartelVO[] vos){
		ArrayList<CertificadoCartelDTO> dtos = new ArrayList<CertificadoCartelDTO>();
		
		if(vos!=null){
			
			for(CertificadoCartelVO vo: vos){
				
				CertificadoCartelDTO dto = obtenerCertificadoCartelDTO(vo);
				
				dtos.add(dto);
			}			
		}	
		
		return dtos;
	}
}