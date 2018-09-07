package cl.cbrs.aio.util;

import java.util.ArrayList;

import cl.cbr.common.exception.GeneralException;
import cl.cbrs.aio.dto.firmaElectronica.BoletaDTO;
import cl.cbrs.aio.dto.firmaElectronica.EntregaEnLineaDTO;
import cl.cbrs.aio.dto.firmaElectronica.RegistroFirmaElectronicaDTO;
import cl.cbrs.aio.dto.firmaElectronica.TipoDocumentoDTO;
import cl.cbrs.entregaenlinea.cliente.ws.delegate.WsEntregaEnLineaDelegate;
import cl.cbrs.entregaenlinea.ws.vo.EstadoTransaccionVO;
import cl.cbrs.entregaenlinea.ws.vo.ObtenerDocumentosPorCaratulaResponse;
import cl.cbrs.firmaelectronica.vo.RegistroFirmaElectronicaVO;

public class EntregaEnLineaUtil {

	public EntregaEnLineaUtil(){
		
	}

	public EntregaEnLineaDTO getDocumentosFirma(Long numeroCaratula) throws GeneralException{

		
		EntregaEnLineaDTO enLineaDTO = new EntregaEnLineaDTO();
		ArrayList<RegistroFirmaElectronicaDTO> documentos = new ArrayList<RegistroFirmaElectronicaDTO>();

		WsEntregaEnLineaDelegate el = new WsEntregaEnLineaDelegate();

		ObtenerDocumentosPorCaratulaResponse respuestaVO = el.obtenerDocumentosPorCaratula(numeroCaratula);
		EstadoTransaccionVO et = respuestaVO.getEstadoTransaccionVO();

		if("OK".equals(et.getEstado())){		
			enLineaDTO.setEstado(true);
		}else{
			enLineaDTO.setEstado(false);
		}
		
		BoletaDTO boletaDTO = new BoletaDTO();

		if(respuestaVO.getTieneBoletaElectronica()!=null && respuestaVO.getTieneBoletaElectronica()){
			boletaDTO.setNumero(respuestaVO.getNumeroBoleta());
		}
		
		enLineaDTO.setBoleta(boletaDTO);

		if(et.getMensaje()!=null){			
			enLineaDTO.setMensaje(et.getMensaje());
		}

		if(et.getMsgWeb()!=null){
			enLineaDTO.setMensajeWeb(et.getMsgWeb());
		}

		RegistroFirmaElectronicaVO[] papeles = respuestaVO.getDocumentosElectronicosVigentes();

		if(papeles!=null && papeles.length>0){
			documentos = getPapeles(papeles);	
		}
		enLineaDTO.setDocumentos(documentos);

		ArrayList<String> advertencias = new ArrayList<String>();
		if(respuestaVO.getAdvertencias()!=null && respuestaVO.getAdvertencias().length>0){
			
			for(String adv: respuestaVO.getAdvertencias()){
				advertencias.add(adv);
			}	
		}   
		
		enLineaDTO.setAdvertencias(advertencias);
		
		return enLineaDTO;
	}


	public RegistroFirmaElectronicaDTO getRegistroFirmaElectronicaDTOfromVO(RegistroFirmaElectronicaVO papel){
		RegistroFirmaElectronicaDTO doc = new RegistroFirmaElectronicaDTO();

		doc.setCodArchivoAlpha(papel.getCodArchivoAlpha());
		doc.setEnviado(papel.getEnviado());						
		doc.setNombreArchivo(papel.getNombreArchivo());
		doc.setNombreArchivoVersion(papel.getNombreArchivoVersion());
		doc.setUsuario(papel.getUsuario());
		doc.setValorDocumento(papel.getValorDocumento());
		doc.setVigente(papel.getVigente());

		TipoDocumentoDTO tipoDocumentoDTO = new TipoDocumentoDTO();
		if(papel.getTipoDocumentoVO()!=null){
			tipoDocumentoDTO.setCodigo(papel.getTipoDocumentoVO().getCodigo());
			tipoDocumentoDTO.setDescripcion(papel.getTipoDocumentoVO().getDescripcion());
		}

		doc.setTipoDocumentoDTO(tipoDocumentoDTO);

		if(papel.getFechaFirma()!=null){
			doc.setFechaFirma(papel.getFechaFirma().getTime());
		}else{
			doc.setFechaFirma(null);
		}

		if(papel.getFechaPdf()!=null){
			doc.setFechaPdf(papel.getFechaPdf().getTime());
		}else{
			doc.setFechaPdf(null);
		}
		
		return doc;
	}

	public ArrayList<RegistroFirmaElectronicaDTO> getPapeles(RegistroFirmaElectronicaVO[] papeles){
		ArrayList<RegistroFirmaElectronicaDTO> documentos = new ArrayList<RegistroFirmaElectronicaDTO>();

		for(RegistroFirmaElectronicaVO papel: papeles){

			if(papel.getTipoDocumentoVO()!=null &&
					papel.getTipoDocumentoVO().getCodigo()!=null &&
					papel.getTipoDocumentoVO().getCodigo().intValue()!=7 &&
					papel.getTipoDocumentoVO().getCodigo().intValue()!=16 &&
					papel.getTipoDocumentoVO().getCodigo().intValue()!=17 &&
					papel.getTipoDocumentoVO().getCodigo().intValue()!=18 &&
					papel.getEnviado()!=null && papel.getEnviado().intValue()==1 &&
					papel.getVigente()!=null && papel.getVigente().intValue()==1){

				RegistroFirmaElectronicaDTO doc = getRegistroFirmaElectronicaDTOfromVO(papel);

				documentos.add(doc);
			}
		}	
		return documentos;
	}
}