package cl.cbrs.aio.util;

import org.apache.log4j.Logger;

import cl.cbr.util.GeneralException;
import cl.cbrs.aio.dto.FojaIniFojaFinCuadernilloDTO;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.FojaIniFojaFinCuadernilloVO;

public class FojaIniFojaFinCuadernilloUtil {
	private static final Logger logger = Logger.getLogger(FojaIniFojaFinCuadernilloUtil.class);
	
	public FojaIniFojaFinCuadernilloUtil(){
		
	}
	
	public FojaIniFojaFinCuadernilloDTO getFojaIniFojaFinCuadernillo(Long cuadernillo,Long ano){
		FojaIniFojaFinCuadernilloDTO dto = null;
		
		try {

			FojaIniFojaFinCuadernilloVO fojaIniFojaFinCuadernilloVO = new FojaIniFojaFinCuadernilloVO();

			WsInscripcionDigitalDelegate delegate = new WsInscripcionDigitalDelegate();

			fojaIniFojaFinCuadernilloVO  =  delegate.obtenerFojaIniFojaFinCuadernillo(cuadernillo,ano);

			if(fojaIniFojaFinCuadernilloVO!=null){
				dto = new FojaIniFojaFinCuadernilloDTO();
				dto.setFojaIni(fojaIniFojaFinCuadernilloVO.getFojaIni());
				dto.setFojaFin(fojaIniFojaFinCuadernilloVO.getFojaFin());
			}
			
		} catch (GeneralException e) {
			logger.error(e);
		}
		return dto;
	}
	
	
}