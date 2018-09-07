package cl.cbrs.aio.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import cl.cbrs.aio.dao.FolioRealDAO;
import cl.cbrs.aio.dto.BorradorDTO;
import cl.cbrs.borrador.delegate.WsBorradorDelegate;
import cl.cbrs.borrador.vo.FolioRealVO;
import cl.cbrs.borrador.vo.ProrealIdVO;
import cl.cbrs.borrador.vo.ProrealVO;

public class BorradoresUtil {
	private static final Logger logger = Logger.getLogger(BorradoresUtil.class);


	public BorradoresUtil(){	
	}
	
	
	public JSONArray getBorradores(Integer foja, Integer numero, Short ano, Boolean bis){
		WsBorradorDelegate wsBorradorDelegate = new WsBorradorDelegate();						
		List<ProrealVO> proRealVOList;
		JSONArray borradores = new JSONArray();

		try {
			proRealVOList = wsBorradorDelegate.obtenerBorradores(foja, numero, ano, bis);
			
			borradores = getJSONArrayBorradorDTO(proRealVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return borradores;
	}
	
	public JSONArray getBorradoresDesdePH(Integer fojaph, Integer numeroph, Short anoph, Boolean bisph){
		
		FolioRealDAO folioRealDAO = new FolioRealDAO();						
		List<ProrealVO> proRealVOList;
		JSONArray borradores = new JSONArray();

		try {
			proRealVOList = folioRealDAO.getBorradoresDesdePH(fojaph, numeroph, anoph, bisph);
			
			borradores = getJSONArrayBorradorDTO(proRealVOList);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return borradores;
	}
	

	public BorradorDTO getBorradorDTO(ProrealVO prorealVO){
		BorradorDTO borradorDTO = new BorradorDTO();
		
		FolioRealVO folioRealVO = prorealVO.getFolioReal();
		
		String direccion = folioRealVO.getDir();
		
		ProrealIdVO prorealIdVO = prorealVO.getProRealId();
		
		Integer borrador = prorealIdVO.getBorrador();
		Integer folio = prorealIdVO.getFolio();
		
		borradorDTO.setBorrador(borrador);
		borradorDTO.setDireccion(direccion);
		borradorDTO.setFolio(folio);
		
		return borradorDTO;		
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayBorradorDTO(List<ProrealVO> prorealVOList){
		JSONArray borradores = new JSONArray();
		
		if(prorealVOList!=null && prorealVOList.size()>0){
			
			for(ProrealVO prorealVO: prorealVOList){
				
				borradores.add(getBorradorDTO(prorealVO));
			}
		}
		
//		Random randomGenerator = new Random();
//		for(int i = 0; i<10;i++){
//			BorradorDTO borradorDTO = new BorradorDTO();
//			
//			 int borrador = randomGenerator.nextInt(10000);
//			 int folio = randomGenerator.nextInt(10000);
//			
//			borradorDTO.setBorrador(borrador);
//			borradorDTO.setDireccion("Los Nogales #8282, Santiago");
//			borradorDTO.setFolio(folio);
//			
//			borradores.add(borradorDTO);
//		}
		
		
		
		
		
		
		return borradores;
	}
}