package cl.cbrs.aio.util;

import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.CanalDTO;
import cl.cbrs.aio.dto.estado.CaratulaEstadoDTO;
import cl.cbrs.aio.dto.estado.CitadoDTO;
import cl.cbrs.aio.dto.estado.CuentaCorrienteDTO;
import cl.cbrs.aio.dto.estado.DatosFormularioDTO;
import cl.cbrs.aio.dto.estado.EstadoActualDTO;
import cl.cbrs.aio.dto.estado.FuncionarioDTO;
import cl.cbrs.aio.dto.estado.IngresoEgresoDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.dto.estado.PosesionEfectivaDTO;
import cl.cbrs.aio.dto.estado.ProductoWebDTO;
import cl.cbrs.aio.dto.estado.RegistroDTO;
import cl.cbrs.aio.dto.estado.RepertorioDTO;
import cl.cbrs.aio.dto.estado.RequirenteDTO;
import cl.cbrs.aio.dto.estado.SeccionDTO;
import cl.cbrs.aio.dto.estado.TareaDTO;
import cl.cbrs.aio.dto.estado.TipoFormularioDTO;
import cl.cbrs.caratula.flujo.vo.AnulaCaratulaVO;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.BitacoraOrigenVO;
import cl.cbrs.caratula.flujo.vo.BitacoraTipoEventoVO;
import cl.cbrs.caratula.flujo.vo.CanalVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CausalRechazoVO;
import cl.cbrs.caratula.flujo.vo.CtaCteVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.IngresoEgresoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.ProductoGlosaVO;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TareaVO;
import cl.cbrs.caratula.flujo.vo.TipoFormularioVO;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.delegate.repertorio.WsRepertorioClienteDelegate;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;
import cl.cbrs.ws.cliente.usuarioweb.WsUsuarioWebDelegate;

public class CaratulaEstadoUtil {

	public CaratulaEstadoUtil(){

	}

	public CaratulaEstadoDTO getCaratulaEstadoDTO(CaratulaVO caratula){
		CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();

		if(caratula==null)
			return null;

		DatosFormularioDTO datosFormularioDTO = getDatosFormulario(caratula);
		ProductoWebDTO productoWebDTO = getProductoWebDTOLite(caratula);
		ArrayList<CitadoDTO> citadoDTO = getCitadoDTO(caratula);
		EstadoActualDTO estadoActualDTO = getEstadoActualDTO(caratula);
		ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs = getIngresoEgresoDTO(caratula.getNumeroCaratula());
		CuentaCorrienteDTO cuentaCorrienteDTO = getCuentaCorrienteDTO(caratula.getCodigo());
		ArrayList<MovimientoDTO> movimientoDTOs = getMovimientoDTOs(caratula);
		RequirenteDTO requirenteDTO = getRequirenteDTO(caratula);		
		ArrayList<BitacoraDTO> bitacoraDTOs = getBitacoraDTOs(caratula.getBitacoraCaratulaVO());		
		ArrayList<TareaDTO> tareaDTOs = getTareaDTOs(caratula);
		//		ArrayList<RepertorioDTO> repertorioDTOs = getRepertorioDTO(caratula.getNumeroCaratula());
		//		ArrayList<PosesionEfectivaDTO> posesionEfectivaDTOs = getPosesionEfectivaDTO(caratula.getNumeroCaratula());

		caratulaEstadoDTO.setDatosFormularioDTO(datosFormularioDTO);
		caratulaEstadoDTO.setProductoWebDTO(productoWebDTO);
		caratulaEstadoDTO.setCitadoDTOs(citadoDTO);
		caratulaEstadoDTO.setEstadoActualDTO(estadoActualDTO);
		caratulaEstadoDTO.setIngresoEgresoDTOs(ingresoEgresoDTOs);
		caratulaEstadoDTO.setMovimientoDTOs(movimientoDTOs);
		caratulaEstadoDTO.setRequirenteDTO(requirenteDTO);
		caratulaEstadoDTO.setCuentaCorrienteDTO(cuentaCorrienteDTO);
		caratulaEstadoDTO.setBitacoraDTOs(bitacoraDTOs);
		caratulaEstadoDTO.setTareaDTOs(tareaDTOs);
		//		caratulaEstadoDTO.setRepertorioDTOs(repertorioDTOs);
		//		caratulaEstadoDTO.setPosesionEfectivaDTOs(posesionEfectivaDTOs);

		return caratulaEstadoDTO;		
	}

	public CaratulaEstadoDTO getCaratulaEstadoDTO(AnulaCaratulaVO anulaCaratulaVO){
		Long numeroCaratula = new Long(anulaCaratulaVO.getCaratula());
		CaratulaEstadoDTO caratulaEstadoDTO = new CaratulaEstadoDTO();

		WsCaratulaClienteDelegate caratulaClienteDelegate = new WsCaratulaClienteDelegate();

		try{

			DatosFormularioDTO datosFormularioDTO = new DatosFormularioDTO();
			datosFormularioDTO.setNumeroCaratula(numeroCaratula);
			datosFormularioDTO.setFechaIngresoL(anulaCaratulaVO.getFechaIngreso().getTime());
			datosFormularioDTO.setFechaIngreso(anulaCaratulaVO.getFechaIngreso());
			if(anulaCaratulaVO.getTipoFormularioVO()!=null){
				TipoFormularioDTO tipoFormularioDTO = new TipoFormularioDTO();
				tipoFormularioDTO.setId(anulaCaratulaVO.getTipoFormularioVO().getTipo());
				tipoFormularioDTO.setDescripcion(anulaCaratulaVO.getTipoFormularioVO().getDescripcion());

				datosFormularioDTO.setTipoFormularioDTO(tipoFormularioDTO);
			}

			//datosFormularioDTO.setEstado("ANULADA"); //?
			datosFormularioDTO.setValorPagado(new Long(anulaCaratulaVO.getValorPagado()));
			datosFormularioDTO.setValorReal(new Long(anulaCaratulaVO.getValorReal()));
			datosFormularioDTO.setValorTasado(new Long(anulaCaratulaVO.getValorTasado()));
			Integer dif = anulaCaratulaVO.getValorReal() - anulaCaratulaVO.getValorPagado();

			datosFormularioDTO.setDiferencia(new Long(dif));

			caratulaEstadoDTO.setDatosFormularioDTO(datosFormularioDTO);

			if(anulaCaratulaVO.getRequirenteVO()!=null){
				RequirenteDTO requirenteDTO = new RequirenteDTO();

				String rut = anulaCaratulaVO.getRequirenteVO().getRut();

				if(StringUtils.isNotBlank(rut)){						
					String rut1 = "";
					String dv1 = "";

					try{
						rut1 = rut.substring(0,8);
						dv1 = rut.substring(8,9);

						requirenteDTO.setRut(rut1);
						requirenteDTO.setDv(dv1);
					}catch (Exception e) {
						requirenteDTO.setRut(rut);
						requirenteDTO.setDv("");
					}
				}

				requirenteDTO.setApellidoPaterno(anulaCaratulaVO.getRequirenteVO().getApellidoPaterno());
				requirenteDTO.setApellidoMaterno(anulaCaratulaVO.getRequirenteVO().getApellidoMaterno());
				requirenteDTO.setNombres(anulaCaratulaVO.getRequirenteVO().getNombres());
				requirenteDTO.setGiro(anulaCaratulaVO.getRequirenteVO().getGiro());
				requirenteDTO.setDireccion(anulaCaratulaVO.getRequirenteVO().getDireccion());
				requirenteDTO.setTelefono(anulaCaratulaVO.getRequirenteVO().getTelefono());
				requirenteDTO.setEmail(anulaCaratulaVO.getRequirenteVO().getEmail());


				caratulaEstadoDTO.setRequirenteDTO(requirenteDTO );		
			}

			List<BitacoraCaratulaVO> bitacoraCaratulaVOs = caratulaClienteDelegate.obtenerBitacoraCaratula(numeroCaratula.intValue());
			caratulaEstadoDTO.setBitacoraDTOs(getBitacoraDTOs(bitacoraCaratulaVOs));


			//MOVIMIENTOS
			MovimientoDTO movimientoDTO = new MovimientoDTO();
			movimientoDTO.setFechaL(anulaCaratulaVO.getFechaAnula().getTime());
			movimientoDTO.setFecha(anulaCaratulaVO.getFechaAnula());

			if(anulaCaratulaVO.getCajeroVO()!=null){
				FuncionarioDTO envia = new FuncionarioDTO();
				String nombreAnula = anulaCaratulaVO.getCajeroVO().getNombre();
				String apellidoAnula = anulaCaratulaVO.getCajeroVO().getApellidoPaterno();

				if(StringUtils.isNotBlank(nombreAnula))
					nombreAnula = nombreAnula.trim();

				if(StringUtils.isNotBlank(apellidoAnula))
					apellidoAnula = apellidoAnula.trim();

				envia.setNombre(nombreAnula);
				envia.setApellidoPaterno(apellidoAnula);			
				movimientoDTO.setEnvia(envia);
			}

			if(anulaCaratulaVO.getAnulaVO()!=null){					
				FuncionarioDTO responsable = new FuncionarioDTO();
				String nombreResponsable = anulaCaratulaVO.getAnulaVO().getNombre();
				String apellidoResponsable = anulaCaratulaVO.getAnulaVO().getApellidoPaterno();

				if(StringUtils.isNotBlank(nombreResponsable))
					nombreResponsable = nombreResponsable.trim();

				if(StringUtils.isNotBlank(apellidoResponsable))
					apellidoResponsable = apellidoResponsable.trim();

				responsable.setNombre(nombreResponsable);
				responsable.setApellidoPaterno(apellidoResponsable);
				movimientoDTO.setResponsable(responsable );
			}

			SeccionDTO seccionDTO = new SeccionDTO();
			seccionDTO.setDescripcion("Caja");
			movimientoDTO.setSeccionDTO(seccionDTO );


			ArrayList<MovimientoDTO> movimientoDTOs = new ArrayList<MovimientoDTO>();
			movimientoDTOs.add(movimientoDTO);
			caratulaEstadoDTO.setMovimientoDTOs(movimientoDTOs );



			//ESTADO ACTUAL
			EstadoActualDTO estadoActualDTO = new EstadoActualDTO();
			estadoActualDTO.setDescripcionEnFlujo("Anulada");
			caratulaEstadoDTO.setEstadoActualDTO(estadoActualDTO );

			//REPERTORIOS
			caratulaEstadoDTO.setRepertorioDTOs(getRepertorioDTO(numeroCaratula));

			//INGRESOS-EGRESOS
			caratulaEstadoDTO.setIngresoEgresoDTOs(getIngresoEgresoDTO(numeroCaratula));

		} catch(Exception e){
			e.printStackTrace();
		}

		return caratulaEstadoDTO;
	}

	private ArrayList<TareaDTO> getTareaDTOs(CaratulaVO caratula){
		ArrayList<TareaDTO> tareaDTOs = new ArrayList<TareaDTO>();


		if(caratula.getTareas()!=null && caratula.getTareas().length>0){

			for(TareaVO vo : caratula.getTareas()){

				if(vo.getTipo()!=null){
					TareaDTO dto = new TareaDTO();
					dto.setId(vo.getTipo().getCodigo());
					dto.setDescripcion(vo.getTipo().getDescripcion());

					tareaDTOs.add(dto);
				}
			}
		}

		return tareaDTOs;
	}

	private ArrayList<BitacoraDTO> getBitacoraDTOs(List<BitacoraCaratulaVO> bitacoraCaratulaVOs){
		ArrayList<BitacoraDTO> bitacoraDTOs = new ArrayList<BitacoraDTO>();

		if(bitacoraCaratulaVOs!=null && bitacoraCaratulaVOs.size()>0)
			for(BitacoraCaratulaVO vo : bitacoraCaratulaVOs)
				bitacoraDTOs.add(getBitacoraDTO(vo));

		return bitacoraDTOs;
	}

	private ArrayList<BitacoraDTO> getBitacoraDTOs(BitacoraCaratulaVO[] bitacoraCaratulaVOs){
		ArrayList<BitacoraDTO> bitacoraDTOs = new ArrayList<BitacoraDTO>();

		if(bitacoraCaratulaVOs!=null && bitacoraCaratulaVOs.length>0)
			for(BitacoraCaratulaVO vo : bitacoraCaratulaVOs)
				bitacoraDTOs.add(getBitacoraDTO(vo));

		return bitacoraDTOs;
	}

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

	public CuentaCorrienteDTO getCuentaCorrienteDTO(Integer codigo){
		CuentaCorrienteDTO cuentaCorrienteDTO = new CuentaCorrienteDTO();

		if(codigo!=null){
			WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();

			try {
				CtaCteVO ctaCteVO =  caratulaDelegate.obtenerCtaCte(codigo);

				if(ctaCteVO!=null){				
					cuentaCorrienteDTO.setCodigo(ctaCteVO.getCodigo());	
					cuentaCorrienteDTO.setInstitucion(ctaCteVO.getInstitucion());
					cuentaCorrienteDTO.setRut(ctaCteVO.getRut());
				}				

			} catch (GeneralException e) {
				e.printStackTrace();
			}
		}

		return cuentaCorrienteDTO;
	}


	private RequirenteDTO getRequirenteDTO(CaratulaVO caratula){
		RequirenteDTO requirenteDTO = new RequirenteDTO();

		if(caratula.getRequirente()!=null){
			RequirenteVO requirenteVO = caratula.getRequirente();

			String rut = requirenteVO.getRut();	
			String ruts = "";

			try {

				if(rut!=null){
					rut = rut.trim();

					Long rl = Long.parseLong(rut);

					ruts = NumberFormat.getInstance(new Locale("ES", "CL")).format(rl);
				}
			} catch (Exception e) {

			}

			String dv = requirenteVO.getDv();

			String nombres = requirenteVO.getNombres();
			String apellidoPaterno = requirenteVO.getApellidoPaterno();
			String apellidoMaterno = requirenteVO.getApellidoMaterno();
			String email = requirenteVO.getEmail();
			String giro = requirenteVO.getGiro();
			String direccion = requirenteVO.getDireccion();
			String telefono = requirenteVO.getTelefono();		

			requirenteDTO.setApellidoMaterno(apellidoMaterno);
			requirenteDTO.setApellidoPaterno(apellidoPaterno);
			requirenteDTO.setDireccion(direccion);
			requirenteDTO.setDv(dv);
			requirenteDTO.setEmail(email);
			requirenteDTO.setGiro(giro);
			requirenteDTO.setNombres(nombres);
			requirenteDTO.setRut(ruts);
			requirenteDTO.setTelefono(telefono);
		}

		return requirenteDTO;	
	}


	private ArrayList<MovimientoDTO> getMovimientoDTOs(CaratulaVO caratula){
		ArrayList<MovimientoDTO> movimientoDTOs = new ArrayList<MovimientoDTO>();

		if(caratula.getEstados()!=null && caratula.getEstados().length>0){

			for(EstadoCaratulaVO estadoCaratulaVO : caratula.getEstados()){
				MovimientoDTO movimientoDTO = new MovimientoDTO();

				movimientoDTO.setFechaL(estadoCaratulaVO.getFechaMovimiento().getTime());
				movimientoDTO.setFecha(estadoCaratulaVO.getFechaMovimiento());

				FuncionarioVO enviadoPor = estadoCaratulaVO.getEnviadoPor();				
				FuncionarioDTO envia = new FuncionarioDTO();

				if(enviadoPor!=null){
					String nombre = enviadoPor.getNombreUsuario();
					String apellidoPaterno = enviadoPor.getApellidoPaterno();

					envia.setNombre(nombre);
					envia.setApellidoPaterno(apellidoPaterno);
				}

				movimientoDTO.setEnvia(envia);

				FuncionarioVO responsableVO = estadoCaratulaVO.getResponsable();		
				FuncionarioDTO responsable = new FuncionarioDTO();

				if(responsableVO!=null){
					String nombre = responsableVO.getNombre();
					String apellidoPaterno = responsableVO.getApellidoPaterno();
					String rut = responsableVO.getRutFuncionario();

					responsable.setNombre(nombre);
					responsable.setApellidoPaterno(apellidoPaterno);
					responsable.setRut(rut);
				}

				movimientoDTO.setResponsable(responsable);

				SeccionDTO seccionDTO = new SeccionDTO();

				if(estadoCaratulaVO.getSeccion()!=null){
					seccionDTO.setId(estadoCaratulaVO.getSeccion().getCodigo());
					seccionDTO.setDescripcion(estadoCaratulaVO.getSeccion().getDescripcion());	
				}

				movimientoDTO.setSeccionDTO(seccionDTO);

				movimientoDTOs.add(movimientoDTO);
			}

		}

		return movimientoDTOs;
	}

	public ArrayList<IngresoEgresoDTO> getIngresoEgresoDTO(Long numeroCaratula){
		ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs = new ArrayList<IngresoEgresoDTO>();

		WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();

		try {
			IngresoEgresoCaratulaVO[] ingresoEgresoCaratulaVOs =  caratulaDelegate.obtenerIngresosEgresos(numeroCaratula.intValue());

			if(ingresoEgresoCaratulaVOs!=null && ingresoEgresoCaratulaVOs.length>0){
				for(IngresoEgresoCaratulaVO ingresoEgresoCaratulaVO : ingresoEgresoCaratulaVOs){
					IngresoEgresoDTO ingresoEgresoDTO = new IngresoEgresoDTO();
					ingresoEgresoDTO.setCaja(ingresoEgresoCaratulaVO.getCaja());
					ingresoEgresoDTO.setDescMedio(ingresoEgresoCaratulaVO.getMedio().getDescripcion());
					ingresoEgresoDTO.setDescTipo(ingresoEgresoCaratulaVO.getTipoIngresoEgresoVO().getDescripcion());
					ingresoEgresoDTO.setFechaL(ingresoEgresoCaratulaVO.getFecha().getTime());
					ingresoEgresoDTO.setFecha(ingresoEgresoCaratulaVO.getFecha());
					ingresoEgresoDTO.setId(ingresoEgresoCaratulaVO.getId());
					ingresoEgresoDTO.setIdMedio(ingresoEgresoCaratulaVO.getMedio().getId());
					ingresoEgresoDTO.setIdTipo(ingresoEgresoCaratulaVO.getTipoIngresoEgresoVO().getId());
					ingresoEgresoDTO.setIdTransaccion(ingresoEgresoCaratulaVO.getIdTransaccion());
					ingresoEgresoDTO.setMonto(ingresoEgresoCaratulaVO.getMonto());		

					ingresoEgresoDTOs.add(ingresoEgresoDTO);
				}
			}


		} catch (GeneralException e) {
			e.printStackTrace();
		}

		return ingresoEgresoDTOs;
	}

	private EstadoActualDTO getEstadoActualDTO(CaratulaVO caratula){
		EstadoActualDTO estadoActualDTO = new EstadoActualDTO();

		if(caratula.getEstadoActualCaratula()!=null){
			Date fechaMov = caratula.getEstadoActualCaratula().getFechaMov();

			estadoActualDTO.setFechaL(fechaMov.getTime());
			estadoActualDTO.setFecha(fechaMov);

			SeccionVO seccionVO = caratula.getEstadoActualCaratula().getSeccion();

			SeccionDTO seccionDTO = new SeccionDTO();

			if(seccionVO!=null){			
				seccionDTO.setId(seccionVO.getCodigo());
				seccionDTO.setDescripcion(seccionVO.getDescripcion());
			}

			estadoActualDTO.setDescripcionEnFlujo(caratula.getEstadoActualCaratula().getDescripcionEnFlujo());

			estadoActualDTO.setSeccionDTO(seccionDTO);
		}

		return estadoActualDTO;
	}

	private ArrayList<CitadoDTO> getCitadoDTO(CaratulaVO caratula){
		ArrayList<CitadoDTO> citadoDTOs = new ArrayList<CitadoDTO>();

		if(caratula.getInscripciones()!=null && caratula.getInscripciones().length>0){
			for(int i=0; i<caratula.getInscripciones().length; i++){
				InscripcionCitadaVO citadaVO = caratula.getInscripciones()[i];
				CitadoDTO citadoDTO = new CitadoDTO();
				citadoDTO.setAno(citadaVO.getAno());
				citadoDTO.setFoja(citadaVO.getFoja());
				citadoDTO.setNumero(citadaVO.getNumero());
				citadoDTO.setBis(citadaVO.getBis());

				if(citadaVO.getRegistro()!=null){
					RegistroDTO registroDTO = new RegistroDTO();

					registroDTO.setId(citadaVO.getRegistro());
					switch(citadaVO.getRegistro()){
					case 1: registroDTO.setDescripcion("Propiedad"); break;
					case 2: registroDTO.setDescripcion("Hipotecas"); break;
					case 3: registroDTO.setDescripcion("Prohibiciones"); break;
					case 4: registroDTO.setDescripcion("Comercio"); break;
					case 5: registroDTO.setDescripcion("Aguas"); break;
					}

					citadoDTO.setRegistroDTO(registroDTO);
				}

				citadoDTOs.add(citadoDTO);
			}
		}
		return citadoDTOs;
	}

	private ProductoWebDTO getProductoWebDTOLite(CaratulaVO caratula){
		ProductoWebDTO productoWebDTO = new ProductoWebDTO();

		ArrayList<String> glosa = new ArrayList<String>();

		if(caratula.getProducto()!=null){
			productoWebDTO.setDescripcionProducto(caratula.getProducto().getDescripcionProducto());
			productoWebDTO.setIdTransaccion(caratula.getProducto().getIdTransaccion());
			productoWebDTO.setIdUsuarioWeb(caratula.getProducto().getIdUsuarioWeb());

			//			WsUsuarioWebDelegate usuarioWebDelegate = new WsUsuarioWebDelegate();
			//			
			//			try {
			//				UsuarioWebVO usuarioWebVO =  usuarioWebDelegate.obtenerUsuario(caratula.getProducto().getIdUsuarioWeb());
			//				
			//				productoWebDTO.setNameUsuarioWeb(usuarioWebVO.getEMail());
			//				
			//			} catch (GeneralException e) {
			//				e.printStackTrace();
			//			}


			if(caratula.getProducto().getListaProductoGlosaVO()!=null){
				ProductoGlosaVO[] glosaProd = caratula.getProducto().getListaProductoGlosaVO();

				if(glosaProd!=null && glosaProd.length>0){

					for(ProductoGlosaVO pg : glosaProd){
						glosa.add(pg.getGlosa());
					}			
				}
			}

		}

		productoWebDTO.setProductoGlosa(glosa);

		return productoWebDTO;
	}

	public ProductoWebDTO getProductoWebDTOFull(ProductoWebDTO productoWebDTO){

		if(productoWebDTO!=null && productoWebDTO.getIdUsuarioWeb()!=null){


			WsUsuarioWebDelegate usuarioWebDelegate = new WsUsuarioWebDelegate();

			try {
				UsuarioWebVO usuarioWebVO =  usuarioWebDelegate.obtenerUsuario(productoWebDTO.getIdUsuarioWeb());
				productoWebDTO.setNameUsuarioWeb(usuarioWebVO.getEMail());

			} catch (GeneralException e) {
				e.printStackTrace();
			}

		}

		return productoWebDTO;
	}

	public ProductoWebDTO getProductoWebDTO(JSONObject json){

		ProductoWebDTO productoWebDTO = new ProductoWebDTO();
		if(json!=null){

			productoWebDTO.setDescripcionProducto((String)json.get("descripcionProducto"));
			productoWebDTO.setIdTransaccion((Long)json.get("idTransaccion"));
			productoWebDTO.setIdUsuarioWeb((Long)json.get("idUsuarioWeb"));
			productoWebDTO.setNameUsuarioWeb((String)json.get("nameUsuarioWeb"));

			ArrayList<String> listaGlosa = new ArrayList<String>();
			JSONArray productoGlosaJSON = (JSONArray)json.get("productoGlosa");
			for(int i=0; i<productoGlosaJSON.size(); i++){
				listaGlosa.add((String)productoGlosaJSON.get(i));
			}
			productoWebDTO.setProductoGlosa(listaGlosa);

		}

		return productoWebDTO;
	}	

	private CanalDTO getCanalDTO(CaratulaVO caratula){
		CanalDTO canalDTO = new CanalDTO();

		if(caratula.getCanal()!=null){
			CanalVO canalVO = caratula.getCanal();

			canalDTO.setId(canalVO.getId());
			canalDTO.setDescripcion(canalVO.getDescripcion());
		}

		return canalDTO;
	}

	private TipoFormularioDTO getTipoFormularioDTO(CaratulaVO caratula){
		TipoFormularioDTO tipoFormularioDTO = new TipoFormularioDTO();

		if(caratula.getTipoFormulario()!=null){
			TipoFormularioVO tipoFormularioVO = caratula.getTipoFormulario();

			tipoFormularioDTO.setId(tipoFormularioVO.getTipo());
			tipoFormularioDTO.setDescripcion(tipoFormularioVO.getDescripcion());
		}

		return tipoFormularioDTO;
	}

	private DatosFormularioDTO getDatosFormulario(CaratulaVO caratula){
		DatosFormularioDTO datosFormularioDTO = new DatosFormularioDTO();

		Long ncaratula = caratula.getNumeroCaratula();

		Long fechaIngreso = 0L;
		if(caratula.getFechaCreacion()!=null){
			fechaIngreso = caratula.getFechaCreacion().getTime();
		}

		datosFormularioDTO.setNumeroCaratula(ncaratula);
		datosFormularioDTO.setFechaIngresoL(fechaIngreso);
		datosFormularioDTO.setFechaIngreso(caratula.getFechaCreacion());

		TipoFormularioDTO tipoFormularioDTO = getTipoFormularioDTO(caratula);		
		datosFormularioDTO.setTipoFormularioDTO(tipoFormularioDTO);

		CanalDTO canalDTO = getCanalDTO(caratula);		
		datosFormularioDTO.setCanalDTO(canalDTO);

		datosFormularioDTO.setValorTasado(caratula.getValorTasado());
		datosFormularioDTO.setValorPagado(caratula.getValorPagado());
		datosFormularioDTO.setValorReal(caratula.getValorReal());

		Long diferencia = caratula.getValorReal() - caratula.getValorPagado();		

		datosFormularioDTO.setDiferencia(diferencia);		

		datosFormularioDTO.setEstado(caratula.getEstadoFormulario());


		datosFormularioDTO.setCodigo(caratula.getCodigo());
		datosFormularioDTO.setClienteCuentaCorriente(caratula.getClienteCtaCte());
		datosFormularioDTO.setUsuarioCreador(caratula.getUsuarioCreador());

		return datosFormularioDTO;
	}

	public ArrayList<RepertorioDTO> getRepertorioDTO(Long numeroCaratula){
		ArrayList<RepertorioDTO> repertorioDTOs = new ArrayList<RepertorioDTO>();

		if(numeroCaratula!=null){
			WsRepertorioClienteDelegate delegate = new WsRepertorioClienteDelegate();

			try {
				List<RepertorioVO> repertorioVOs =  delegate.existeCaratulaConRepertorio(numeroCaratula);

				if(repertorioVOs!=null){	
					for(RepertorioVO repertorioVO : repertorioVOs){
						RepertorioDTO repertorioDTO = new RepertorioDTO();
						repertorioDTO.setNumeroRepertorio(repertorioVO.getRepertorioIdVO().getNumRepertorioProp());
						repertorioDTO.setAno(repertorioVO.getRepertorioIdVO().getAnoRepertorioProp());
						repertorioDTO.setVigente(repertorioVO.getVigente());
						repertorioDTO.setObservacion(repertorioVO.getObservacion());
						if(repertorioVO.getFechaIngreso()!=null){
							repertorioDTO.setFechaIngresoL(repertorioVO.getFechaIngreso().getTime());
							repertorioDTO.setFechaIngreso(repertorioVO.getFechaIngreso());
						}

						repertorioDTOs.add(repertorioDTO);
					}
				}				

			} catch (GeneralException e) {
				e.printStackTrace();
			}
		}

		return repertorioDTOs;
	}	

	public ArrayList<PosesionEfectivaDTO> getPosesionEfectivaDTO(Long numeroCaratula) throws HTTPException, Exception {

		ArrayList<PosesionEfectivaDTO> posesionEfectivaDTOs = new ArrayList<PosesionEfectivaDTO>();
		JSONArray posesionesEfectivas = new JSONArray();

		Client client = Client.create();
		String ARCHIVO_PARAMETROS_POSEFE = "ws_posesionefectiva.parametros";	
		String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_POSEFE, "IP_WS", "valor");
		String port = TablaValores.getValor(ARCHIVO_PARAMETROS_POSEFE, "PORT_WS", "valor");

//		WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/api/pe/caratula/"+numeroCaratula));
		WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/api/pe/caratula/14584904"));
		
		ClientResponse clientResponse = wr.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

		if(statusRespuesta.getStatusCode() == 200){
			posesionesEfectivas = (JSONArray) getResponse(clientResponse);
			
			if (posesionesEfectivas != null) { 

				Gson gson = new GsonBuilder()
						.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

				posesionEfectivaDTOs = gson.fromJson(posesionesEfectivas.toString(), new TypeToken<List<PosesionEfectivaDTO>>(){}.getType());
			
			} 
		}

		return posesionEfectivaDTOs;
	}

	@SuppressWarnings("unchecked")
	public JSONObject estadoReporteJSON(CaratulaEstadoDTO caratulaEstadoDTO, Long numeroCaratula){	
		JSONObject respuesta = new JSONObject();
		JSONArray errors = new JSONArray();
		JSONObject data = new JSONObject();	

		data.put("rut", "");
		data.put("rutsf", "");
		data.put("dv", "");
		data.put("rut2", "");
		data.put("dv2", "");
		data.put("nombres", "-");
		data.put("apaterno", "-");
		data.put("amaterno", "-");
		data.put("giro", "-");
		data.put("direccion", "-");
		data.put("telefono", "-");
		data.put("email", "-");
		data.put("ccc", "-");

		data.put("codSeccionActual", "-");
		data.put("seccionActual", "-");
		data.put("fechaActual", "-");

		data.put("vtasado", "-");
		data.put("vpagado", "-");
		data.put("vreal", "-");
		data.put("diferencia", "-");

		data.put("repertorio", "-");

		data.put("hayCC", false);	
		data.put("institucion", "-");
		data.put("codigocc", "-");
		data.put("rutcc", "-");
		data.put("institucion", "-");	

		data.put("fechaIngreso", "-");

		data.put("anulada", false);
		data.put("obs", "");
		//		data.put("repertorioVigente", true);

		data.put("citadoFoja", "-");
		data.put("citadoNum", "-");
		data.put("citadoAno", "-");
		data.put("citadoRegistro", "-");		
		data.put("citadoRegistroNombre", "-");		

		data.put("hayCitado", false);
		data.put("canal", "");
		data.put("hayCanal", false);

		data.put("estadoForm", "-");
		data.put("estadoFormDesc", "-");


		String obs = "";

		WsCaratulaClienteDelegate caratulaDelegate = new WsCaratulaClienteDelegate();

		try {
			DecimalFormatSymbols dfs  = new DecimalFormatSymbols(new Locale("es", "cl"));
			DecimalFormat df = new DecimalFormat("#,##0", dfs);

			AnulaCaratulaVO[] anulaCaratulaVOs = caratulaDelegate.obtenerCaratulasAnuladas(numeroCaratula);
			//AnulaCaratulaDTO anulaDTO = DataManager.checkAnulada(numeroCaratula);

			//Repertorios
			WsRepertorioClienteDelegate repertorioClienteDelegate = new WsRepertorioClienteDelegate();
			List<RepertorioVO> repertorioVOs = repertorioClienteDelegate.existeCaratulaConRepertorio(numeroCaratula);

			StringBuffer repertorios = new StringBuffer();
			if(repertorioVOs!=null){
				for(RepertorioVO repertorioVO : repertorioVOs){
					if(repertorios.length()>0)
						repertorios.append(", ");
					repertorios.append(repertorioVO.getRepertorioIdVO().getNumRepertorioProp() );

					if(repertorioVO.getVigente()!=null && repertorioVO.getVigente().intValue() == 0){
						data.put("repertorioVigente", false);
						obs = obs+ "Repertorio " + repertorioVO.getRepertorioIdVO().getNumRepertorioProp() + " NO VIGENTE. ";
					}

					if(repertorioVO.getObservacion()!=null && repertorioVO.getObservacion().trim().length()>0)
						obs = obs+ repertorioVO.getObservacion()+". ";	
				}
			}				
			data.put("repertorio", repertorios.toString());

			if( caratulaEstadoDTO!=null && caratulaEstadoDTO.getDatosFormularioDTO()!=null){
				respuesta.put("success", true);

				data.put("ncaratula", caratulaEstadoDTO.getDatosFormularioDTO().getNumeroCaratula());
				data.put("nform", caratulaEstadoDTO.getDatosFormularioDTO().getTipoFormularioDTO().getId()); 
				data.put("tform", caratulaEstadoDTO.getDatosFormularioDTO().getTipoFormularioDTO().getDescripcion());					
				data.put("estadoForm", caratulaEstadoDTO.getDatosFormularioDTO().getEstado());

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	
				String fechaIngreso = sdf.format(caratulaEstadoDTO.getDatosFormularioDTO().getFechaIngreso());
				data.put("fechaIngreso", fechaIngreso);

				data.put("codSeccionActual", caratulaEstadoDTO.getEstadoActualDTO().getSeccionDTO().getId());
				data.put("seccionActual", caratulaEstadoDTO.getEstadoActualDTO().getSeccionDTO().getDescripcion());

				String fechaActual = sdf.format(caratulaEstadoDTO.getEstadoActualDTO().getFecha());
				data.put("fechaActual", fechaActual);

				if(caratulaEstadoDTO.getDatosFormularioDTO()!=null){
					if("d".equalsIgnoreCase(caratulaEstadoDTO.getDatosFormularioDTO().getEstado().trim())){
						data.put("estadoFormDesc", "Despachada");
					}else if("p".equalsIgnoreCase(caratulaEstadoDTO.getDatosFormularioDTO().getEstado().trim())){
						data.put("estadoFormDesc", "Proceso");
					}else if("w".equalsIgnoreCase(caratulaEstadoDTO.getDatosFormularioDTO().getEstado().trim())){
						data.put("estadoFormDesc", "Auditoría");
					}

					String ccc = caratulaEstadoDTO.getDatosFormularioDTO().getClienteCuentaCorriente();
					if(StringUtils.isNotBlank(ccc)){
						data.put("ccc", ccc);					
					}

				}

				//RepertorioDTO repertorioDTO = DataManager.getRepertorio(numeroCaratula);
				//					WsRepertorioClienteDelegate repertorioClienteDelegate = new WsRepertorioClienteDelegate();
				//					repertorioClienteDelegate.existeCaratulaConRepertorio(caratulaEstadoDTO.getDatosFormularioDTO().getNumeroCaratula());

				if(caratulaEstadoDTO.getDatosFormularioDTO()!=null){

					//						if(!repertorioDTO.isVigente() caratulaEstadoDTO.getDatosFormularioDTO().get){
					//							data.put("repertorioVigente", false);
					//							obs = obs+ "Repertorio NO VIGENTE. ";
					//						}
					//
					//						if(repertorioDTO.getObserva()!=null && repertorioDTO.getObserva().trim().length()>0){
					//							obs = obs+ repertorioDTO.getObserva().trim()+". ";
					//						}
					data.put("obs", obs);


					if(caratulaEstadoDTO.getDatosFormularioDTO().getValorPagado()!=null){
						String pagado = df.format(caratulaEstadoDTO.getDatosFormularioDTO().getValorPagado());
						data.put("vpagado", pagado);
					}

					if(caratulaEstadoDTO.getDatosFormularioDTO().getValorReal()!=null){
						String real = df.format(caratulaEstadoDTO.getDatosFormularioDTO().getValorReal());
						data.put("vreal", real);					
					}

					if(caratulaEstadoDTO.getDatosFormularioDTO().getValorTasado()!=null){
						String tasado = df.format(caratulaEstadoDTO.getDatosFormularioDTO().getValorTasado());
						data.put("vtasado", tasado);					
					}

					if(caratulaEstadoDTO.getDatosFormularioDTO().getValorPagado()!=null && caratulaEstadoDTO.getDatosFormularioDTO().getValorReal()!=null){
						Integer dif = caratulaEstadoDTO.getDatosFormularioDTO().getValorReal().intValue() - caratulaEstadoDTO.getDatosFormularioDTO().getValorPagado().intValue();

						String diferencia = df.format(dif);
						data.put("diferencia", diferencia);					
					}
				}

				if(caratulaEstadoDTO.getRequirenteDTO()!=null){
					String rut = caratulaEstadoDTO.getRequirenteDTO().getRut();

					if(StringUtils.isNotBlank(rut)){
						try {							
							data.put("rutsf", rut.trim());

							String rut1 = "";
							String dv1 = "";

							try{
								rut1 = rut.substring(0,8);
								dv1 = rut.substring(8,9);

								String rutformat = df.format(Integer.valueOf(rut1));	

								data.put("rut", rutformat);
								data.put("dv", dv1);
								data.put("rut2", rutformat+"-"+dv1);									
							}catch (Exception e) {
								data.put("rut", rut);
								data.put("dv", "");
								data.put("rut2", rut);
							}


							String nombres = caratulaEstadoDTO.getRequirenteDTO().getNombres();
							String app = caratulaEstadoDTO.getRequirenteDTO().getApellidoPaterno();
							String apm = caratulaEstadoDTO.getRequirenteDTO().getApellidoMaterno();

							if(StringUtils.isNotBlank(nombres)){
								data.put("nombres", nombres);								
							}

							if(StringUtils.isNotBlank(app)){
								data.put("apaterno", app);							
							}

							if(StringUtils.isNotBlank(apm)){
								data.put("amaterno", apm);							
							}


							String giro = caratulaEstadoDTO.getRequirenteDTO().getGiro();

							if(StringUtils.isNotBlank(giro)){
								data.put("giro", giro);								
							}


							String direccion = caratulaEstadoDTO.getRequirenteDTO().getDireccion();
							String telefono = caratulaEstadoDTO.getRequirenteDTO().getTelefono();
							String email = caratulaEstadoDTO.getRequirenteDTO().getEmail();

							if(StringUtils.isNotBlank(direccion)){
								data.put("direccion", direccion);								
							}

							if(StringUtils.isNotBlank(telefono)){
								data.put("telefono", telefono);								
							}

							if(StringUtils.isNotBlank(email)){
								data.put("email", email);								
							}
						} catch (Exception e) {

						}
					}								
				}

				data.put("historial", caratulaEstadoDTO.getMovimientoDTOs());
				data.put("tareas", caratulaEstadoDTO.getTareaDTOs());

				if(caratulaEstadoDTO.getCuentaCorrienteDTO()!=null && caratulaEstadoDTO.getCuentaCorrienteDTO().getCodigo()!=null && caratulaEstadoDTO.getCuentaCorrienteDTO().getCodigo().intValue()!=0){
					data.put("hayCC", true);

					data.put("codigocc", caratulaEstadoDTO.getCuentaCorrienteDTO().getCodigo());

					String rutcc = caratulaEstadoDTO.getCuentaCorrienteDTO().getRut();
					String rutformat = "";
					String dv1 = "";
					try{
						String rut1 = rutcc.substring(0,8);
						dv1 = rutcc.substring(8,9);

						rutformat = df.format(Integer.valueOf(rut1));
						data.put("rutcc", rutformat+"-"+dv1);
					}catch(Exception e){
						data.put("rutcc", rutcc);
					}

					if(StringUtils.isNotBlank(caratulaEstadoDTO.getCuentaCorrienteDTO().getInstitucion())){
						data.put("institucion", caratulaEstadoDTO.getCuentaCorrienteDTO().getInstitucion());
					}
				}				

				if(caratulaEstadoDTO.getCitadoDTOs()!=null && caratulaEstadoDTO.getCitadoDTOs().size()>0){
					CitadoDTO citadoDTO = caratulaEstadoDTO.getCitadoDTOs().get(0);
					if(citadoDTO.getFoja()!=null && 
							citadoDTO.getNumero()!=null && 
							citadoDTO.getAno()!=null){
						data.put("citadoFoja", citadoDTO.getFoja());
						data.put("citadoNum", citadoDTO.getNumero());								
						data.put("citadoAno", citadoDTO.getAno());		

						if(citadoDTO.getRegistroDTO()!=null){
							data.put("citadoRegistro", citadoDTO.getRegistroDTO().getId());		
							data.put("citadoRegistroNombre", citadoDTO.getRegistroDTO().getDescripcion());
						}
						data.put("hayCitado", true);
					}
				}

				JSONArray glosaArray = new JSONArray();
				if(caratulaEstadoDTO.getDatosFormularioDTO().getCanalDTO()!=null){
					data.put("canal", caratulaEstadoDTO.getDatosFormularioDTO().getCanalDTO().getId());
					data.put("hayCanal", true);

					if(caratulaEstadoDTO.getProductoWebDTO().getProductoGlosa()!=null && caratulaEstadoDTO.getProductoWebDTO().getProductoGlosa().size()>0){

						for(String texto: caratulaEstadoDTO.getProductoWebDTO().getProductoGlosa()){
							JSONObject g = new JSONObject();

							g.put("texto", texto);

							glosaArray.add(g);							
						}			
					}
				}
				data.put("glosa", glosaArray);	

				//bitacora			
				data.put("bitacora", caratulaEstadoDTO.getBitacoraDTOs());

				//ingreso-egreso				
				data.put("ingresoEgreso", caratulaEstadoDTO.getIngresoEgresoDTOs());

			}else{

				//Buscar anulacion si hay
				if(anulaCaratulaVOs!=null && anulaCaratulaVOs.length>0){
					AnulaCaratulaVO anulaDTO = anulaCaratulaVOs[0]; //Ultima Anulacion
					respuesta.put("success", true);
					data.put("anulada", true);

					//data.put("obs", true);

					data.put("ncaratula", anulaDTO.getCaratula());

					if(anulaDTO.getTipoFormularioVO()!=null){
						data.put("nform", anulaDTO.getTipoFormularioVO().getTipo());
						data.put("tform", anulaDTO.getTipoFormularioVO().getDescripcion());
					}

					data.put("tareas", new ArrayList<TareaDTO>());




					if(anulaDTO.getValorPagado()!=null){
						String pagado = df.format(anulaDTO.getValorPagado());
						data.put("vpagado", pagado);
					}

					if(anulaDTO.getValorReal()!=null){
						String real = df.format(anulaDTO.getValorReal());
						data.put("vreal", real);					
					}

					if(anulaDTO.getValorTasado()!=null){
						String tasado = df.format(anulaDTO.getValorTasado());
						data.put("vtasado", tasado);					
					}

					if(anulaDTO.getValorPagado()!=null && anulaDTO.getValorReal()!=null){
						Integer dif = anulaDTO.getValorReal() - anulaDTO.getValorPagado();

						String diferencia = df.format(dif);
						data.put("diferencia", diferencia);					
					}

					obs = obs + "Carátula ANULADA ";

					if(anulaDTO.getAnulaVO()!=null){
						String nombreAnula = anulaDTO.getAnulaVO().getNombre();
						String apellidoAnula = anulaDTO.getAnulaVO().getApellidoPaterno();
						String nombreCompleto = "";

						if(StringUtils.isNotBlank(nombreAnula)){
							nombreCompleto = nombreAnula.trim();
						}

						if(StringUtils.isNotBlank(apellidoAnula)){
							nombreCompleto = nombreCompleto +" "+apellidoAnula.trim();
						}

						//obs = obs + "Por "+nombreCompleto+" (RUT: "+anulaDTO.getRutAnula()+"). ";	
						obs = obs + "por "+nombreCompleto+", ";	

					}

					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");			
					String fechaAnula = sdf.format(anulaDTO.getFechaAnula());

					obs = obs + "con fecha "+fechaAnula+". " ;	

					//				if(anulaDTO.getCaja()!=null){
					//					obs = obs + "Caja "+anulaDTO.getCaja().trim()+". ";					
					//				}

					String fechaIngreso = sdf.format(anulaDTO.getFechaIngreso());
					data.put("fechaIngreso", fechaIngreso);

					ArrayList<MovimientoDTO> movimientoDTOs = new ArrayList<MovimientoDTO>();
					MovimientoDTO movimiento = new MovimientoDTO();
					movimiento.setFechaL(anulaDTO.getFechaIngreso().getTime());
					movimiento.setFecha(anulaDTO.getFechaIngreso());

					if(anulaDTO.getCajeroVO()!=null){
						String nombreAnula = anulaDTO.getCajeroVO().getNombre();
						String apellidoAnula = anulaDTO.getCajeroVO().getApellidoPaterno();					

						if(StringUtils.isNotBlank(nombreAnula)){
							nombreAnula = nombreAnula.trim();
						}

						if(StringUtils.isNotBlank(apellidoAnula)){
							apellidoAnula = apellidoAnula.trim();
						}

						FuncionarioDTO envia = new FuncionarioDTO();
						envia.setNombre(nombreAnula);
						envia.setApellidoPaterno(apellidoAnula);

						movimiento.setEnvia(envia);
					}

					if(anulaDTO.getAnulaVO()!=null){
						String nombreResponsable = anulaDTO.getAnulaVO().getNombre();
						String apellidoResponsable = anulaDTO.getAnulaVO().getApellidoPaterno();					

						if(StringUtils.isNotBlank(nombreResponsable)){
							nombreResponsable = nombreResponsable.trim();
						}

						if(StringUtils.isNotBlank(apellidoResponsable)){
							apellidoResponsable = apellidoResponsable.trim();
						}

						FuncionarioDTO responsable = new FuncionarioDTO();
						responsable.setNombre(nombreResponsable);
						responsable.setApellidoPaterno(apellidoResponsable);

						movimiento.setResponsable(responsable);
					}					

					SeccionDTO seccionDTO = new SeccionDTO();
					seccionDTO.setDescripcion("Caja");
					seccionDTO.setId("01");
					movimiento.setSeccionDTO(seccionDTO );					

					movimientoDTOs.add(movimiento);
					data.put("historial", movimientoDTOs);

					data.put("obs", obs);

					//TabRequirentes requirente = DataManager.getRequirente(anulaDTO.getRutRequirente());

					if(anulaDTO.getRequirenteVO()!=null){
						String rut = anulaDTO.getRequirenteVO().getRut();

						if(StringUtils.isNotBlank(rut)){
							try {							
								data.put("rutsf", rut.trim());

								String rut1 = "";
								String dv1 = "";

								try{
									rut1 = rut.substring(0,8);
									dv1 = rut.substring(8,9);

									String rutformat = df.format(Integer.valueOf(rut1));	

									data.put("rut", rutformat);
									data.put("dv", dv1);
									data.put("rut2", rutformat+"-"+dv1);									
								}catch (Exception e) {
									data.put("rut", rut);
									data.put("dv", "");
									data.put("rut2", rut);
								}


								String nombres = anulaDTO.getRequirenteVO().getNombres();
								String app = anulaDTO.getRequirenteVO().getApellidoPaterno();
								String apm = anulaDTO.getRequirenteVO().getApellidoMaterno();

								if(StringUtils.isNotBlank(nombres)){
									data.put("nombres", nombres);								
								}

								if(StringUtils.isNotBlank(app)){
									data.put("apaterno", app);							
								}

								if(StringUtils.isNotBlank(apm)){
									data.put("amaterno", apm);							
								}


								String giro = anulaDTO.getRequirenteVO().getGiro();

								if(StringUtils.isNotBlank(giro)){
									data.put("giro", giro);								
								}


								String direccion = anulaDTO.getRequirenteVO().getDireccion();
								String telefono = anulaDTO.getRequirenteVO().getTelefono();
								String email = anulaDTO.getRequirenteVO().getEmail();

								if(StringUtils.isNotBlank(direccion)){
									data.put("direccion", direccion);								
								}

								if(StringUtils.isNotBlank(telefono)){
									data.put("telefono", telefono);								
								}

								if(StringUtils.isNotBlank(email)){
									data.put("email", email);								
								}
							} catch (Exception e) {

							}
						}								
					}

					List<BitacoraCaratulaVO> bitacoraCaratulaVOs = caratulaDelegate.obtenerBitacoraCaratula(numeroCaratula.intValue());
					ArrayList<BitacoraDTO> bitacoraDTOs = new ArrayList<BitacoraDTO>();
					if(bitacoraCaratulaVOs!=null)
						for(BitacoraCaratulaVO bitacoraCaratulaVO : bitacoraCaratulaVOs)
							bitacoraDTOs.add(getBitacoraDTO(bitacoraCaratulaVO));

					data.put("bitacora", bitacoraDTOs);

					//ingreso-egreso				
					data.put("ingresoEgreso", getIngresoEgresoDTO(numeroCaratula));

				} else{				
					respuesta.put("success", false);
					respuesta.put("errorMessage", "Carátula "+numeroCaratula+" no existe");
				}
			}				


		} catch (NumberFormatException e1) {
			respuesta.put("success", false);	
			respuesta.put("errorMessage", "Carátula "+numeroCaratula+" no válida");
		} catch (GeneralException e) {
			respuesta.put("success", false);	
			respuesta.put("errorMessage", "Error en el server");
		}

		respuesta.put("data", data);
		respuesta.put("errors", errors);
		respuesta.put("errormsg","");
		respuesta.put("tipo","");	

		return respuesta;		
	}	

	@SuppressWarnings("unchecked")
	public JSONArray getBitacora(ArrayList<BitacoraDTO> bitacora, Long ncaratula){
		JSONArray bitacoraJSON = new JSONArray();

		try {
			if(bitacora!=null && bitacora.size()>0){

				for(BitacoraDTO record: bitacora){
					JSONObject recordJSON = new JSONObject();

					if(record.getFuncionario()!=null){
						recordJSON.put("apellidoMaternoFuncionario", record.getFuncionario().getApellidoMaterno());
						recordJSON.put("apellidoPaternoFuncionario", record.getFuncionario().getApellidoPaterno());
						recordJSON.put("nombreFuncionario", record.getFuncionario().getNombre());
					}

					recordJSON.put("boId", record.getIdOrigen());
					recordJSON.put("boDescripcion", record.getDescOrigen());

					recordJSON.put("bteId", record.getIdTipo());
					recordJSON.put("bteDescripcion", record.getDescTipo());	

					recordJSON.put("causalId", "");
					recordJSON.put("causalDescripcion", "");

					if(record.getIdCausal()!=null){
						recordJSON.put("causalId", record.getIdCausal());
						recordJSON.put("causalDescripcion", record.getDescCausal());						
					}

					Date fecha = record.getFecha();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String fechaFinal = sdf.format(fecha);
					recordJSON.put("fecha", fechaFinal);

					String nombreUsuarioWeb = "";
					String apellidoPaternoUsuarioWeb = "";
					String rutUsuarioWeb = "";
					String dvUsuarioWeb = "";

					recordJSON.put("rutUsuarioWeb", rutUsuarioWeb);
					recordJSON.put("dvUsuarioWeb", dvUsuarioWeb);
					recordJSON.put("nombreUsuarioWeb", nombreUsuarioWeb);
					recordJSON.put("apellidoPaternoUsuarioWeb", apellidoPaternoUsuarioWeb);

					recordJSON.put("idBitacora", record.getId());					
					recordJSON.put("numeroCaratula", ncaratula);
					recordJSON.put("observacion", record.getComentario());


					bitacoraJSON.add(recordJSON);	
				}			

			}
		} catch (Exception e) {

		}	

		return bitacoraJSON;
	}

	private static Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(response.getType()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(response.getType().toString().startsWith("application/json"))
				respuesta = new JSONParser().parse(response.getEntity(String.class));
			else
				respuesta = response.getEntity(String.class);

		} else if(response!=null)
			throw new HTTPException(response.getStatus());
		else
			throw new Exception("Sin respuesta del servicio");
		return respuesta;
	}      
}