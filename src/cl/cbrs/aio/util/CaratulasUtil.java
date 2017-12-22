package cl.cbrs.aio.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;

import cl.cbr.botondepagoweb.vo.TransaccionWebVO;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dao.RegistroDAO;
import cl.cbrs.aio.dto.CaratulaDTO;
import cl.cbrs.aio.dto.EstadoActualCaratulaDTO;
import cl.cbrs.aio.dto.InscripcionDigitalDTO;
import cl.cbrs.aio.dto.LiquidacionCaratulaDTO;
import cl.cbrs.aio.dto.ProductoWebDTO;
import cl.cbrs.aio.dto.SeccionDTO;
import cl.cbrs.aio.dto.TareaDTO;
import cl.cbrs.aio.dto.TipoFormularioDTO;
import cl.cbrs.aio.dto.TransaccionWebDTO;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.FuncionarioDTO;
import cl.cbrs.aio.dto.estado.RegistroDTO;
import cl.cbrs.aio.dto.firmaElectronica.CertificadoFNADTO;
import cl.cbrs.aio.dto.firmaElectronica.RegistroFirmaElectronicaDTO;
import cl.cbrs.aio.dto.firmaElectronica.TipoDocumentoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.BitacoraOrigenVO;
import cl.cbrs.caratula.flujo.vo.BitacoraTipoEventoVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CausalRechazoVO;
import cl.cbrs.caratula.flujo.vo.EstadoActualCaratulaVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.ProductoGlosaVO;
import cl.cbrs.caratula.flujo.vo.ProductoVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TareaVO;
import cl.cbrs.caratula.flujo.vo.TipoFormularioVO;
import cl.cbrs.caratula.flujo.vo.TipoTareaVO;
import cl.cbrs.delegate.botondepago.WsBotonDePagoClienteDelegate;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.entregaenlinea.cliente.ws.delegate.WsEntregaEnLineaDelegate;
import cl.cbrs.entregaenlinea.ws.vo.EstadoTransaccionVO;
import cl.cbrs.entregaenlinea.ws.vo.ObtenerDocumentosPorCaratulaResponse;
import cl.cbrs.firmaelectronica.delagate.ClienteWsFirmadorDelegate;
import cl.cbrs.firmaelectronica.vo.RegistroFirmaElectronicaVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;

public class CaratulasUtil {
	private static final Logger logger = Logger.getLogger(CaratulasUtil.class);

	//private static Integer CARATULAS_EN_PROCESO = 0;
	//private static Integer CARATULAS_TERMINADA = 1;

	public CaratulasUtil(){	
	}

	public JSONArray getCaratulas(Long foja, String numero, Long ano, Integer bis, Integer estadoCaratula){
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
		List<CaratulaVO> caratulaVOList;
		JSONArray caratulas = new JSONArray();

		try {

			caratulaVOList = caratulaClienteDelegate.obtenerCaratulasPorTitulo(foja, numero, ano, bis, estadoCaratula);

			caratulas = getJSONArrayCaratulaDTO(caratulaVOList);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

		return caratulas;
	}

	public CaratulaDTO getCaratulaDTO(Long caratula){
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
		CaratulaVO caratulaVO = null;
		CaratulaDTO caratulaDTO = null;

		try {

			caratulaVO = caratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratula);
			if(caratulaVO!=null)
				caratulaDTO = getCaratulaDTO(caratulaVO);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		return caratulaDTO;
	}	

	public CaratulaDTO getCaratulaDTO(JSONObject caratulaJSON){

		CaratulaDTO caratulaDTO = new CaratulaDTO();

		caratulaDTO.setNumeroCaratula(new Long(caratulaJSON.get("numeroCaratula").toString()));
		if(caratulaJSON.containsKey("origenCreacion") && caratulaJSON.get("origenCreacion")!=null)
			caratulaDTO.setOrigenCreacion( new Integer(caratulaJSON.get("origenCreacion").toString()));
		if(caratulaJSON.containsKey("codigo") && caratulaJSON.get("codigo")!=null)
			caratulaDTO.setCodigo(new Integer(caratulaJSON.get("codigo").toString()));

		JSONObject inscripcionDigitalDTOJSON = null;
		InscripcionDigitalDTO inscripcionDigitalDTO = null;
		if(caratulaJSON.get("inscripcionDigitalDTO") instanceof JSONObject){
			inscripcionDigitalDTOJSON = (JSONObject)caratulaJSON.get("inscripcionDigitalDTO");
			inscripcionDigitalDTO = new InscripcionDigitalDTO();

			if(inscripcionDigitalDTOJSON.get("foja")!=null && inscripcionDigitalDTOJSON.get("numero")!=null && inscripcionDigitalDTOJSON.get("ano")!=null){
				inscripcionDigitalDTO.setFoja(new Long(inscripcionDigitalDTOJSON.get("foja").toString()));
				inscripcionDigitalDTO.setNumero(inscripcionDigitalDTOJSON.get("numero").toString());
				inscripcionDigitalDTO.setAno(new Long(inscripcionDigitalDTOJSON.get("ano").toString()));
				inscripcionDigitalDTO.setBis((Boolean)inscripcionDigitalDTOJSON.get("bis"));
			}

			if(inscripcionDigitalDTOJSON.get("registroDTO") instanceof JSONObject){
				JSONObject registroDTOJSON = (JSONObject)inscripcionDigitalDTOJSON.get("registroDTO");
				Long idRegistro = new Long( (registroDTOJSON).get("id").toString() );
				String descripcionRegistro = (registroDTOJSON).get("descripcion").toString();
				inscripcionDigitalDTO.setRegistro(idRegistro.intValue());
				RegistroDTO registroDTO = new RegistroDTO();
				registroDTO.setId(idRegistro.intValue());
				registroDTO.setDescripcion(descripcionRegistro);
				inscripcionDigitalDTO.setRegistroDTO(registroDTO );
			}

			caratulaDTO.setInscripcionDigitalDTO(inscripcionDigitalDTO);
		}

		if(caratulaJSON.get("tipoFormularioDTO") instanceof JSONObject){
			JSONObject tipoFormularioDTOJSON = (JSONObject)caratulaJSON.get("tipoFormularioDTO");
			TipoFormularioDTO tipoFormularioDTO = new TipoFormularioDTO();
			Long idTipoFormulario = new Long( tipoFormularioDTOJSON.get("id").toString() );
			tipoFormularioDTO.setId(idTipoFormulario.intValue());
			tipoFormularioDTO.setDescripcion((String)tipoFormularioDTOJSON.get("descripcion"));
			caratulaDTO.setTipoFormularioDTO(tipoFormularioDTO );

			caratulaDTO.setTipoFormularioDTO(tipoFormularioDTO);
		}

		System.out.println(caratulaJSON);
		if(caratulaJSON.containsKey("productoWebDTO") && !caratulaJSON.get("productoWebDTO").equals("")){
			JSONObject productoWebDTOJSON = (JSONObject)caratulaJSON.get("productoWebDTO");
			Long idTransaccion = (Long) productoWebDTOJSON.get("idTransaccion");
			caratulaDTO.setIdTransaccion(idTransaccion);
		}

		return caratulaDTO;
	}

	public CaratulaDTO updateCaratulaDTO(CaratulaDTO caratulaDTO) throws Exception{
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
		CaratulaVO caratulaVO = null;

		try {
			caratulaVO = caratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratulaDTO.getNumeroCaratula());


			if(caratulaDTO.getInscripcionDigitalDTO()!=null && caratulaDTO.getInscripcionDigitalDTO().getAno()!=null
					&& caratulaDTO.getInscripcionDigitalDTO().getFoja()!=null && caratulaDTO.getInscripcionDigitalDTO().getNumero()!=null
					&& !"".equals(caratulaDTO.getInscripcionDigitalDTO().getNumero())){
				InscripcionCitadaVO[] inscripcionCitadaVOs = caratulaVO.getInscripciones();
				if(inscripcionCitadaVOs==null || inscripcionCitadaVOs.length<1){
					inscripcionCitadaVOs = new InscripcionCitadaVO[1];				
					inscripcionCitadaVOs[0] = new InscripcionCitadaVO();
					caratulaVO.setInscripciones(inscripcionCitadaVOs);
				}

				caratulaVO.getInscripciones()[0].setAno(caratulaDTO.getInscripcionDigitalDTO().getAno().intValue());
				caratulaVO.getInscripciones()[0].setFoja(caratulaDTO.getInscripcionDigitalDTO().getFoja().intValue());
				caratulaVO.getInscripciones()[0].setNumero(new Integer(caratulaDTO.getInscripcionDigitalDTO().getNumero()));
				if(caratulaDTO.getInscripcionDigitalDTO().getBis()!=null && caratulaDTO.getInscripcionDigitalDTO().getBis())
					caratulaVO.getInscripciones()[0].setBis(1);
				else
					caratulaVO.getInscripciones()[0].setBis(0);
				if(caratulaDTO.getInscripcionDigitalDTO().getImpTicket()!=null)
					caratulaVO.getInscripciones()[0].setImpTicket(caratulaDTO.getInscripcionDigitalDTO().getImpTicket());
				caratulaVO.getInscripciones()[0].setRegistro(caratulaDTO.getInscripcionDigitalDTO().getRegistro());
			}

			TipoFormularioVO tipoFormulario = new TipoFormularioVO(caratulaDTO.getTipoFormularioDTO().getId(), caratulaDTO.getTipoFormularioDTO().getDescripcion());
			caratulaVO.setTipoFormulario(tipoFormulario );

			if(!caratulaDTO.getEstadoForm().equals(""))
				caratulaVO.setEstadoFormulario(caratulaDTO.getEstadoForm());

			caratulaClienteDelegate.actualizarCaratula(caratulaVO);

			caratulaDTO = getCaratulaDTO(caratulaVO);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}

		return caratulaDTO;
	}	

	public void moverCaratulaSeccion(Long numeroCaratula, EstadoCaratulaVO estadoCaratulaVO) throws Exception{
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();

		try {
			caratulaClienteDelegate.moverCaratulaSeccion(numeroCaratula, estadoCaratulaVO);

		} catch (Exception e) {
			logger.error(e);
			throw e;
		}		
	}	

	public void rechazarCaratula(Long numeroCaratula, String rutFuncionario, Integer codigoCausaRechazo, String motivo) throws Exception{
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();

		try {
			caratulaClienteDelegate.rechazarCaratula(numeroCaratula, rutFuncionario, motivo, codigoCausaRechazo);

		} catch (Exception e) {
			logger.error(e);
			throw e;
		}		
	}	

	public CaratulaDTO getCaratulaDTO(CaratulaVO caratulaVO) throws Exception{
		CaratulaDTO caratulaDTO = new CaratulaDTO();

		Long numeroCaratula = caratulaVO.getNumeroCaratula();
		caratulaDTO.setNumeroCaratula(numeroCaratula);		
		caratulaDTO.setOrigenCreacion(caratulaVO.getOrigenCreacion());
		caratulaDTO.setIdTransaccion(caratulaVO.getIdTransaccion());
		caratulaDTO.setCodigo(caratulaVO.getCodigo());

		if(caratulaVO.getFechaCreacion()!=null){
			caratulaDTO.setFechaCreacionL(caratulaVO.getFechaCreacion().getTime());
			caratulaDTO.setFechaCreacion(caratulaVO.getFechaCreacion());
		}

		if(caratulaVO.getProducto()!=null){
			ProductoVO productoVO = caratulaVO.getProducto();
			ProductoWebDTO productoWebDTO = new ProductoWebDTO();
			productoWebDTO.setDescripcionProducto(productoVO.getDescripcionProducto());
			productoWebDTO.setIdTransaccion(productoVO.getIdTransaccion());

			if(caratulaVO.getProducto().getListaProductoGlosaVO()!=null){
				ArrayList<String> glosa = new ArrayList<String>();
				ProductoGlosaVO[] glosaProd = caratulaVO.getProducto().getListaProductoGlosaVO();

				if(glosaProd!=null && glosaProd.length>0){

					for(ProductoGlosaVO pg : glosaProd){
						glosa.add(pg.getGlosa());
					}			
				}
				productoWebDTO.setProductoGlosa(glosa);
			}

			caratulaDTO.setProductoWebDTO(productoWebDTO);
		}

		if(caratulaVO.getTareas()!=null && caratulaVO.getTareas().length>0){
			ArrayList<TareaDTO> listaTareas = new ArrayList<TareaDTO>();
			for(TareaVO tareaVO : caratulaVO.getTareas()){
				TipoTareaVO tipoTareaVO = tareaVO.getTipo();
				if(tipoTareaVO!=null){
					TareaDTO tareaDTO = new TareaDTO();
					tareaDTO.setId(tipoTareaVO.getCodigo());
					tareaDTO.setDescripcion(tipoTareaVO.getDescripcion());
					listaTareas.add(tareaDTO);
				}				
			}
			caratulaDTO.setTareaDTOs(listaTareas);
		}

		caratulaDTO.setValorPagado(caratulaVO.getValorPagado());
		caratulaDTO.setValorReal(caratulaVO.getValorReal());
		if(caratulaVO.getValorPagado()!=null && caratulaVO.getValorReal()!=null){
			Long diferencia = caratulaVO.getValorPagado() - caratulaVO.getValorReal();					
			caratulaDTO.setDiferencia(diferencia);
		}

		if(null != caratulaVO.getCodigoDocumentoElectronico())
			caratulaDTO.setCodigoDocumentoElectronico(caratulaVO.getCodigoDocumentoElectronico());
		else
			caratulaDTO.setCodigoDocumentoElectronico("0");

		if(null != caratulaVO.getIdNotarioElectronico())
			caratulaDTO.setIdNotarioElectronico(caratulaVO.getIdNotarioElectronico());
		else
			caratulaDTO.setIdNotarioElectronico(0L);

		if(null!=caratulaVO.getCanal()){
			caratulaDTO.setIdCanal(caratulaVO.getCanal().getId());
		}else{
			caratulaDTO.setIdCanal(1);
		}

		if(caratulaDTO.getIdCanal()==2)
			caratulaDTO.setCanalTexto("WEB");
		else
			caratulaDTO.setCanalTexto("CAJA");

		if(null!=caratulaVO.getEstadoDescargaEscri())
			caratulaDTO.setEstadoDescargaEscri(caratulaVO.getEstadoDescargaEscri());
		else
			caratulaDTO.setEstadoDescargaEscri(0);

		if(null!=caratulaVO.getEmpresa())
			caratulaDTO.setEmpresa(caratulaVO.getEmpresa());
		else
			caratulaDTO.setEmpresa(0);

		TipoFormularioVO tipoFormularioVO = caratulaVO.getTipoFormulario();
		TipoFormularioDTO tipoFormularioDTO = new TipoFormularioDTO();
		if(tipoFormularioVO!=null){					
			tipoFormularioDTO.setId(tipoFormularioVO.getTipo());
			tipoFormularioDTO.setDescripcion(tipoFormularioVO.getDescripcion());/**/
			tipoFormularioDTO.setIdDescripcion(tipoFormularioVO.getTipo()+"-"+tipoFormularioVO.getDescripcion());
		}

		EstadoActualCaratulaVO estadoActualCaratulaVO = caratulaVO.getEstadoActualCaratula();
		if(estadoActualCaratulaVO!=null){		
			Date fechaMov = estadoActualCaratulaVO.getFechaMov();

			EstadoActualCaratulaDTO estadoActualCaratulaDTO = new EstadoActualCaratulaDTO();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			String fechaMovS = sdf.format(fechaMov);
			Long fechaMovL = fechaMov.getTime();

			estadoActualCaratulaDTO.setFechaMov(fechaMovS);
			estadoActualCaratulaDTO.setFechaMovL(fechaMovL);
			estadoActualCaratulaDTO.setDescripcionEnFlujo(estadoActualCaratulaVO.getDescripcionEnFlujo());

			SeccionDTO seccionDTO = new SeccionDTO();	
			SeccionVO seccionVO = estadoActualCaratulaVO.getSeccion();
			if(seccionVO!=null){
				seccionDTO.setCodigo(seccionVO.getCodigo());
				seccionDTO.setDescripcion(seccionVO.getDescripcion());	
				estadoActualCaratulaDTO.setSeccionDTO(seccionDTO);
			}

			caratulaDTO.setEstadoActualCaratulaDTO(estadoActualCaratulaDTO);
		}

		caratulaDTO.setTipoFormularioDTO(tipoFormularioDTO);		

		//		ArrayList<BitacoraDTO> bitacoraDTOs = getBitacoraDTOs(caratulaVO.getBitacoraCaratulaVO());
		//		caratulaDTO.setBitacoraDTOs(bitacoraDTOs);


		InscripcionDigitalDTO inscripcionDigitalDTO = null;
		if(caratulaVO.getInscripciones()!=null && caratulaVO.getInscripciones().length>0){
			inscripcionDigitalDTO = new InscripcionDigitalDTO();
			InscripcionCitadaVO inscripcionCitadaVO = caratulaVO.getInscripciones()[0];

			if(inscripcionCitadaVO!=null){
				//inscripcionDigitalDTO.setIdInscripcion(new Long(inscripcionCitadaVO.getId()));
				inscripcionDigitalDTO.setFoja(new Long(inscripcionCitadaVO.getFoja()));
				inscripcionDigitalDTO.setNumero(String.valueOf(inscripcionCitadaVO.getNumero()));
				inscripcionDigitalDTO.setAno(new Long(inscripcionCitadaVO.getAno()));
				if(inscripcionCitadaVO.getBis()!=null && inscripcionCitadaVO.getBis().intValue()==1)
					inscripcionDigitalDTO.setBis(true);
				else
					inscripcionDigitalDTO.setBis(false);
				if(inscripcionCitadaVO.getRegistro()!=null){
					inscripcionDigitalDTO.setRegistro(inscripcionCitadaVO.getRegistro());
					RegistroDTO registroDTO = new RegistroDTO();
					registroDTO.setId(inscripcionCitadaVO.getRegistro());
					switch(inscripcionCitadaVO.getRegistro()){
					case 1: registroDTO.setDescripcion("Propiedades"); break;
					case 2: registroDTO.setDescripcion("Hipotecas"); break;
					case 3: registroDTO.setDescripcion("Prohibiciones"); break;
					case 4: registroDTO.setDescripcion("Comercio"); break;
					case 5: registroDTO.setDescripcion("Aguas"); break;
					}

					inscripcionDigitalDTO.setRegistroDTO(registroDTO );
				}
				if(inscripcionCitadaVO.getFecha()!=null){
					Date fechaActualizacion = inscripcionCitadaVO.getFecha();
					Long fechaActL = fechaActualizacion.getTime();

					inscripcionDigitalDTO.setFechaActualizacion(fechaActualizacion);
					inscripcionDigitalDTO.setFechaActualizacionL(fechaActL);
				}

				caratulaDTO.setInscripcionDigitalDTO(inscripcionDigitalDTO);
			}
		}

		return caratulaDTO;		
	}

	//	private ArrayList<BitacoraDTO> getBitacoraDTOs(BitacoraCaratulaVO[] bitacoraCaratulaVOs){
	//		ArrayList<BitacoraDTO> bitacoraDTOs = new ArrayList<BitacoraDTO>();
	//			
	//		if(bitacoraCaratulaVOs!=null && bitacoraCaratulaVOs.length>0)
	//			for(BitacoraCaratulaVO vo : bitacoraCaratulaVOs)
	//				bitacoraDTOs.add(getBitacoraDTO(vo));
	//
	//		return bitacoraDTOs;
	//	}	

	private BitacoraDTO getBitacoraDTO(BitacoraCaratulaVO vo) {
		BitacoraDTO dto = new BitacoraDTO();

		dto.setFechaL(vo.getFecha().getTime());
		dto.setFecha(vo.getFecha());
		dto.setComentario(vo.getObservacion());

		String apellidoMaterno = vo.getApellidoMaternoFuncionario();
		String apellidoPaterno = vo.getApellidoPaternoFuncionario();
		String nombre = vo.getNombreFuncionario();
		String rut = vo.getRutFuncionario();

		FuncionarioDTO funcionarioDTO = new FuncionarioDTO();			
		funcionarioDTO.setApellidoMaterno(apellidoMaterno);
		funcionarioDTO.setApellidoPaterno(apellidoPaterno);
		funcionarioDTO.setNombre(nombre);
		funcionarioDTO.setRut(rut);

		dto.setFuncionario(funcionarioDTO);

		CausalRechazoVO causalRechazoVO = vo.getCausalRechazoVO();
		BitacoraOrigenVO bitacoraOrigenVO = vo.getBitacoraOrigenVO();
		BitacoraTipoEventoVO bitacoraTipoEventoVO = vo.getBitacoraTipoEventoVO();

		if(causalRechazoVO!=null){
			dto.setIdCausal(causalRechazoVO.getCodigoCausaRechazo());
			dto.setDescCausal(causalRechazoVO.getDescripcionCausaRechazo());
		}

		if(bitacoraOrigenVO!=null){
			dto.setIdOrigen(bitacoraOrigenVO.getId());
			dto.setDescOrigen(bitacoraOrigenVO.getDescripcion());
		}

		if(bitacoraTipoEventoVO!=null){
			dto.setIdTipo(bitacoraTipoEventoVO.getIdTipoEvento());
			dto.setDescTipo(bitacoraTipoEventoVO.getDescripcionTipoEvento());
		}

		if(vo.getCategoria()!=null){
			dto.setCategoria(vo.getCategoria());
		}

		return dto;
	}	

	@SuppressWarnings("unchecked")
	public JSONArray getJSONArrayCaratulaDTO(List<CaratulaVO> caratulaVOList) throws Exception{
		JSONArray caratulas = new JSONArray();

		if(caratulaVOList!=null && caratulaVOList.size()>0){

			for(CaratulaVO caratulaVO: caratulaVOList){

				caratulas.add(getCaratulaDTO(caratulaVO));
			}
		}		
		return caratulas;
	}

	public BitacoraDTO agregarBitacoraCaratula(Long numeroCaratula, String rutFuncionario, String observacion, Integer categoria) throws Exception{
		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();
		BitacoraDTO bitacoraDTO = new BitacoraDTO();

		try {
			//observacionReq
			BitacoraCaratulaVO bitacoraCaratulaVO = new BitacoraCaratulaVO();

			BitacoraOrigenVO bitacoraOrigenVO = new BitacoraOrigenVO();
			bitacoraOrigenVO.setId(5);
			bitacoraOrigenVO.setDescripcion(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));
			bitacoraCaratulaVO.setBitacoraOrigenVO(bitacoraOrigenVO );
			bitacoraCaratulaVO.setFecha(new Date());			
			bitacoraCaratulaVO.setObservacion(observacion);

			bitacoraCaratulaVO.setNumeroCaratula(numeroCaratula);
			BitacoraTipoEventoVO bitacoraTipoEventoVO = new BitacoraTipoEventoVO();
			bitacoraTipoEventoVO.setIdTipoEvento(5);
			bitacoraTipoEventoVO.setDescripcionTipoEvento("Observación");
			bitacoraCaratulaVO.setBitacoraTipoEventoVO(bitacoraTipoEventoVO );
			bitacoraCaratulaVO.setRutFuncionario(rutFuncionario);
			bitacoraCaratulaVO.setCategoria(categoria);

			//        	if(funcionarioVO!=null && funcionarioVO.getTabFuncionariosVO()!=null){
			//        		bitacoraCaratulaVO.setNombreFuncionario(funcionarioVO.getTabFuncionariosVO().getNombreFuncionario());
			//    			bitacoraCaratulaVO.setApellidoMaternoFuncionario(funcionarioVO.getTabFuncionariosVO().getApellMaterno());
			//    			bitacoraCaratulaVO.setApellidoPaternoFuncionario(funcionarioVO.getTabFuncionariosVO().getApellPaterno());
			//    			bitacoraCaratulaVO.setRutFuncionario(funcionarioVO.getTabFuncionariosVO().getRutFuncionario());
			//        	}
			bitacoraCaratulaVO.setUsuarioWebVO(new UsuarioWebVO());

			bitacoraCaratulaVO = caratulaClienteDelegate.agregarBitacoraCaratula(bitacoraCaratulaVO);


			bitacoraDTO.setId(bitacoraCaratulaVO.getIdBitacora().intValue());
			bitacoraDTO.setComentario(bitacoraCaratulaVO.getObservacion());
			bitacoraDTO.setCategoria(bitacoraCaratulaVO.getCategoria());
			bitacoraDTO.setFechaL(bitacoraCaratulaVO.getFecha().getTime());
			bitacoraDTO.setFecha(bitacoraCaratulaVO.getFecha());
			FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
			funcionarioDTO.setNombre(bitacoraCaratulaVO.getNombreFuncionario());
			funcionarioDTO.setApellidoPaterno(bitacoraCaratulaVO.getApellidoPaternoFuncionario());
			funcionarioDTO.setApellidoMaterno(bitacoraCaratulaVO.getApellidoMaternoFuncionario());
			funcionarioDTO.setRut(bitacoraCaratulaVO.getRutFuncionario());
			bitacoraDTO.setFuncionario(funcionarioDTO );
			bitacoraDTO.setIdOrigen(bitacoraCaratulaVO.getBitacoraOrigenVO().getId());
			bitacoraDTO.setDescOrigen(bitacoraCaratulaVO.getBitacoraOrigenVO().getDescripcion());
			bitacoraDTO.setIdTipo(bitacoraCaratulaVO.getBitacoraTipoEventoVO().getIdTipoEvento());
			bitacoraDTO.setDescTipo(bitacoraCaratulaVO.getBitacoraTipoEventoVO().getDescripcionTipoEvento());

		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

		return bitacoraDTO;

	}	

	public LiquidacionCaratulaDTO getLiquidacionCaratulaDTO(CaratulaVO caratula, boolean conPapeles, boolean esEntrega) throws Exception{
		LiquidacionCaratulaDTO liquidacionCaratulaDTO = new LiquidacionCaratulaDTO();

		//		liquidacionCaratulaDTO.setCaratula(getCaratulaDTO(caratula.getNumeroCaratula()));
		liquidacionCaratulaDTO.setCaratula(getCaratulaDTO(caratula));

		//TOPES
		String topeDiferenciaS = ConstantesPortalConservador.getParametroEntregaDocumentos("TOPE_DIFERENCIA");
		String topePagoDifenciaCVs = ConstantesPortalConservador.getParametroEntregaDocumentos("TOPE_PAGO_DIFERENCIA_CERTIFICADO_VIGENCIA");

		Long topeDiferencia = 2400L;
		Long topePagoDifenciaCV = 2300L;
		try {
			topeDiferencia = Long.parseLong(topeDiferenciaS);
			topePagoDifenciaCV = Long.parseLong(topePagoDifenciaCVs);
		} catch (Exception e) {

		}

		//TX DIFERENCIA
		liquidacionCaratulaDTO.setHayTxDiferencia(false);
		Long idTxPagoDiferencia = caratula.getIdTransaccionPagoDiferencia();

		if(idTxPagoDiferencia!=null){
			WsBotonDePagoClienteDelegate delegateBotonPago = new WsBotonDePagoClienteDelegate();
			TransaccionWebVO txDiferencia = delegateBotonPago.obtenerTransaccionPorId(idTxPagoDiferencia);
			if(txDiferencia!=null){											
				TransaccionWebDTO transaccionWebDTO = new TransaccionWebDTO();
				transaccionWebDTO.setId(txDiferencia.getId());
				if(txDiferencia.getFechaTransaccion()!=null){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String pagoFechaTransaccionS = sdf.format(txDiferencia.getFechaTransaccion());
					transaccionWebDTO.setFechaTransaccion(pagoFechaTransaccionS);
					transaccionWebDTO.setFechaTransaccionL(txDiferencia.getFechaTransaccion().getTime());
				}
				transaccionWebDTO.setMontoTransaccion(txDiferencia.getMontoTransaccion());
				transaccionWebDTO.setEstadoActualTransaccion(txDiferencia.getEstadoActualTransaccion());
				if(txDiferencia.getPagoFechaPago()!=null){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String pagoFechaPagoS = sdf.format(txDiferencia.getPagoFechaPago());
					transaccionWebDTO.setPagoFechaPago(pagoFechaPagoS);
					transaccionWebDTO.setPagoFechaPagoL(txDiferencia.getPagoFechaPago().getTime());
				}
				transaccionWebDTO.setPagoNombreBanco(txDiferencia.getPagoNombreBanco());	
				liquidacionCaratulaDTO.setTxDiferenciaDTO(transaccionWebDTO);
			}
			liquidacionCaratulaDTO.setHayTxDiferencia(true);
		}					

		//DIFERENCIA
		Long valorPagado = caratula.getValorPagado()==null?0L:caratula.getValorPagado();
		Long valorReal = caratula.getValorReal()==null?0L:caratula.getValorReal();

		Long diferencia = valorReal - valorPagado;	
		liquidacionCaratulaDTO.setDiferencia(diferencia);	

		TipoFormularioVO tipoFormularioVO = caratula.getTipoFormulario();

		if(tipoFormularioVO!=null && tipoFormularioVO.getTipo().intValue() == 8){

			if(diferencia.longValue() > topePagoDifenciaCV.longValue()){
				liquidacionCaratulaDTO.setHayDiferencia(true);
			}else{
				liquidacionCaratulaDTO.setHayDiferencia(false);		
			}			
		}else{
			if(diferencia.longValue() > topeDiferencia.longValue()){
				liquidacionCaratulaDTO.setHayDiferencia(true);
			}else{
				liquidacionCaratulaDTO.setHayDiferencia(false);		
			}			
		}

		if(conPapeles){

			if(esEntrega){
				getPapelesEntrega(liquidacionCaratulaDTO);				
			}else{
				getPapeles(liquidacionCaratulaDTO);					
			}		
		}

		return liquidacionCaratulaDTO;
	}	

	public void getPapelesEntrega(LiquidacionCaratulaDTO liquidacionCaratulaDTO) throws Exception{
		//PAPELES
		WsEntregaEnLineaDelegate el = new WsEntregaEnLineaDelegate();
		//String rutPortal = ConstantesPortalConservador.getParametro("RUT_PORTAL");
		//PortalUtil pu = new PortalUtil();

		ObtenerDocumentosPorCaratulaResponse respuestaVO = el.obtenerDocumentosPorCaratula(liquidacionCaratulaDTO.getCaratula().getNumeroCaratula());
		EstadoTransaccionVO et = respuestaVO.getEstadoTransaccionVO();

		if("OK".equals(et.getEstado())){

			RegistroFirmaElectronicaVO[] papeles = respuestaVO.getDocumentosElectronicosVigentes();

			if(papeles!=null && papeles.length>0){	
				ArrayList<RegistroFirmaElectronicaDTO> listaPapeles = new ArrayList<RegistroFirmaElectronicaDTO>();
				for(RegistroFirmaElectronicaVO registroFirmaElectronicaVO : papeles){
					Boolean noMostrar=false;
					RegistroFirmaElectronicaDTO registroFirmaElectronicaDTO = new RegistroFirmaElectronicaDTO();
					registroFirmaElectronicaDTO.setCodArchivoAlpha(registroFirmaElectronicaVO.getCodArchivoAlpha());
					registroFirmaElectronicaDTO.setValorDocumento(registroFirmaElectronicaVO.getValorDocumento());
					registroFirmaElectronicaDTO.setNombreArchivoVersion(registroFirmaElectronicaVO.getNombreArchivoVersion());
					if(registroFirmaElectronicaVO.getFechaPdf()!=null)
						registroFirmaElectronicaDTO.setFechaPdf(registroFirmaElectronicaVO.getFechaPdf().getTime());
					registroFirmaElectronicaDTO.setUsuario(registroFirmaElectronicaVO.getUsuario());
					if(registroFirmaElectronicaVO.getTipoDocumentoVO()!=null){
						TipoDocumentoDTO tipoDocumentoDTO = new TipoDocumentoDTO();
						tipoDocumentoDTO.setCodigo(registroFirmaElectronicaVO.getTipoDocumentoVO().getCodigo());
						tipoDocumentoDTO.setDescripcion(registroFirmaElectronicaVO.getTipoDocumentoVO().getDescripcion());
						registroFirmaElectronicaDTO.setTipoDocumentoDTO(tipoDocumentoDTO );

						//Si es Certificacion de Escritura, se recupera id certificado del nombre archivo
						//							try{
						//								if(tipoDocumentoDTO.getCodigo().equals(22L)){
						//									Long idCertificado = Long.parseLong(registroFirmaElectronicaDTO.getNombreArchivoVersion().split("_")[2]);									
						//									RegistroDAO registroDAO = new RegistroDAO();
						//									ArrayList<CertificadoFNADTO> certificadoFNADTOs = registroDAO.obtenerCertificadoFNA(idCertificado);
						//									registroFirmaElectronicaDTO.setCertificadoFNADTOs(certificadoFNADTOs);
						//								}
						//							} catch(Exception e){
						//								logger.error("Error al obtener idCertificado: " + e.getMessage(),e);
						//							}
						
						if(tipoDocumentoDTO.getCodigo()==16 || tipoDocumentoDTO.getCodigo()==17 || tipoDocumentoDTO.getCodigo()==18)
							noMostrar=true;
					}

					//Buscar glosa documento
					RegistroDAO registroDAO = new RegistroDAO();
					String glosa = registroDAO.obtenerGlosaCobroMaeCertificacion(registroFirmaElectronicaVO.getCodArchivoAlpha());
					registroFirmaElectronicaDTO.setGlosaCobroCertificado(glosa);
					
					//Notas Propiedad, Hipoteca y Prohibiciones no deben mostrarse
					if(!noMostrar)
						listaPapeles.add(registroFirmaElectronicaDTO);
					
				}
				liquidacionCaratulaDTO.setPapeles(listaPapeles);									
			}

			if(respuestaVO.getTieneBoletaElectronica()!=null && respuestaVO.getTieneBoletaElectronica()){
				liquidacionCaratulaDTO.setHayBoleta(true);
				liquidacionCaratulaDTO.setNumeroBoleta(respuestaVO.getNumeroBoleta());
			}												
		}				

		if(respuestaVO.getAdvertencias()!=null && respuestaVO.getAdvertencias().length>0){
			liquidacionCaratulaDTO.setAdvertencias(new ArrayList<String>(Arrays.asList(respuestaVO.getAdvertencias())));
		}   
	}

	public void getPapeles(LiquidacionCaratulaDTO liquidacionCaratulaDTO) throws Exception{
		//PAPELES
		ClienteWsFirmadorDelegate clienteWsFirmadorDelegate = new ClienteWsFirmadorDelegate();

		//papeles carátula
		RegistroFirmaElectronicaVO[] papeles =  clienteWsFirmadorDelegate.obtenerListaDocumentosPorCaratula(liquidacionCaratulaDTO.getCaratula().getNumeroCaratula());

		if(papeles!=null && papeles.length>0){
			ArrayList<RegistroFirmaElectronicaDTO> listaPapeles = new ArrayList<RegistroFirmaElectronicaDTO>();
			for(RegistroFirmaElectronicaVO registroFirmaElectronicaVO: papeles){
				Boolean noMostrar=false;
				
				if( (registroFirmaElectronicaVO.getVigente()!=null && registroFirmaElectronicaVO.getVigente().intValue()==1) && 
						(registroFirmaElectronicaVO.getEnviado()!=null && registroFirmaElectronicaVO.getEnviado().intValue()==1) &&
						registroFirmaElectronicaVO.getUsuario()!=null && !"".equals(registroFirmaElectronicaVO.getUsuario())){

					RegistroFirmaElectronicaDTO registroFirmaElectronicaDTO = new RegistroFirmaElectronicaDTO();
					registroFirmaElectronicaDTO.setCodArchivoAlpha(registroFirmaElectronicaVO.getCodArchivoAlpha());
					registroFirmaElectronicaDTO.setValorDocumento(registroFirmaElectronicaVO.getValorDocumento());
					registroFirmaElectronicaDTO.setNombreArchivoVersion(registroFirmaElectronicaVO.getNombreArchivoVersion());
					if(registroFirmaElectronicaVO.getFechaPdf()!=null)
						registroFirmaElectronicaDTO.setFechaPdf(registroFirmaElectronicaVO.getFechaPdf().getTime());
					registroFirmaElectronicaDTO.setUsuario(registroFirmaElectronicaVO.getUsuario());
					if(registroFirmaElectronicaVO.getTipoDocumentoVO()!=null){
						TipoDocumentoDTO tipoDocumentoDTO = new TipoDocumentoDTO();
						tipoDocumentoDTO.setCodigo(registroFirmaElectronicaVO.getTipoDocumentoVO().getCodigo());
						tipoDocumentoDTO.setDescripcion(registroFirmaElectronicaVO.getTipoDocumentoVO().getDescripcion());
						registroFirmaElectronicaDTO.setTipoDocumentoDTO(tipoDocumentoDTO );


						//							try{
						String sufijo = "";

						String[] datosCertificado = registroFirmaElectronicaVO.getNombreArchivoVersion().split("_");
						if(tipoDocumentoDTO.getCodigo().equals(22L)){
							
							String tipoCertificado = registroFirmaElectronicaVO.getNombreArchivoVersion().split("-")[0].split("_")[4];
							sufijo = " - " + TablaValores.getValor("certificacion_matriceria.parametros", new StringBuilder("CERT_").append(tipoCertificado).toString(), "valor");
							if (!datosCertificado[3].equals("0"))
								sufijo = " (copia Escritura)";

							//									Long idCertificado = Long.parseLong(registroFirmaElectronicaDTO.getNombreArchivoVersion().split("_")[2]);									
							//									RegistroDAO registroDAO = new RegistroDAO();
							//									ArrayList<CertificadoFNADTO> certificadoFNADTOs = registroDAO.obtenerCertificadoFNA(idCertificado);
							//									registroFirmaElectronicaDTO.setCertificadoFNADTOs(certificadoFNADTOs);
						}
						//							} catch(Exception e){
						//								logger.error("Error al obtener idCertificado: " + e.getMessage(),e);
						//							}
						
						if(tipoDocumentoDTO.getCodigo()==16 || tipoDocumentoDTO.getCodigo()==17 || tipoDocumentoDTO.getCodigo()==18)
							noMostrar=true;
						
					}

					//Buscar glosa documento
					RegistroDAO registroDAO = new RegistroDAO();
					String glosa = registroDAO.obtenerGlosaCobroMaeCertificacion(registroFirmaElectronicaVO.getCodArchivoAlpha());
					registroFirmaElectronicaDTO.setGlosaCobroCertificado(glosa);

					//Notas Propiedad, Hipoteca y Prohibiciones no deben mostrarse
					if(!noMostrar)
						listaPapeles.add(registroFirmaElectronicaDTO);						
				}				
			}

			liquidacionCaratulaDTO.setPapeles(listaPapeles);

		}
	}	
}