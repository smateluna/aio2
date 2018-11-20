package cl.cbrs.aio.struts.action.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import cl.cbr.botondepagoweb.vo.TransaccionWebVO;
import cl.cbr.common.exception.GeneralException;
import cl.cbr.util.StringUtil;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.ConsultaDocumentoDTO;
import cl.cbrs.aio.servlet.CacheAIO;
import cl.cbrs.aio.struts.action.CbrsAbstractAction;
import cl.cbrs.aio.util.CaratulasUtil;
import cl.cbrs.aio.util.ConstantesPortalConservador;
import cl.cbrs.aio.util.InscripcionDigitalUtil;
import cl.cbrs.botondepagoweb.ws.response.SolicitarTransaccionPagoWebResponse;
import cl.cbrs.caratula.flujo.vo.BitacoraCaratulaVO;
import cl.cbrs.caratula.flujo.vo.CanalVO;
import cl.cbrs.caratula.flujo.vo.CaratulaTempVO;
import cl.cbrs.caratula.flujo.vo.CaratulaVO;
import cl.cbrs.caratula.flujo.vo.CausalRechazoVO;
import cl.cbrs.caratula.flujo.vo.EstadoCaratulaVO;
import cl.cbrs.caratula.flujo.vo.FuncionarioVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaTempVO;
import cl.cbrs.caratula.flujo.vo.InscripcionCitadaVO;
import cl.cbrs.caratula.flujo.vo.ProductoReceptorEmailVO;
import cl.cbrs.caratula.flujo.vo.ProductoVO;
import cl.cbrs.caratula.flujo.vo.RequirenteVO;
import cl.cbrs.caratula.flujo.vo.SeccionVO;
import cl.cbrs.caratula.flujo.vo.TareaIdTempVO;
import cl.cbrs.caratula.flujo.vo.TareaTempVO;
import cl.cbrs.caratula.flujo.vo.TipoFormularioVO;
import cl.cbrs.caratula.flujo.vo.TipoProductoVO;
import cl.cbrs.cuentacorriente.delegate.WsCuentaCorrienteClienteDelegate;
import cl.cbrs.cuentacorriente.vo.ListaNominaCtaCteAioVO;
import cl.cbrs.cuentacorriente.vo.NominaCtaCteAioVO;
import cl.cbrs.delegate.botondepago.WsBotonDePagoClienteDelegate;
import cl.cbrs.delegate.caratula.WsCaratulaClienteDelegate;
import cl.cbrs.inscripciondigital.delegate.WsInscripcionDigitalDelegate;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;
import cl.cbrs.usuarioweb.vo.ReceptorEmailVO;
import cl.cbrs.usuarioweb.vo.UsuarioWebVO;
import cl.cbrs.ws.cliente.usuarioweb.WsUsuarioWebDelegate;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CaratulaServiceAction extends CbrsAbstractAction {

	private static final Logger logger = Logger.getLogger(CaratulaServiceAction.class);
	private static final String ARCHIVO = "portal_conservador.parametros";

	public static final String COPIA_DOMINIO_VIGENTE = "COPIA_DOMINIO_VIGENTE";
	public static final String COPIA_DOMINIO_SIN_VIGENTE = "COPIA_DOMINIO_SIN_VIGENTE";
	public static final String CERTIFICADO_GP = "CERTIFICADO_GP";
	public static final String TITULO_10 = "TITULO_10";
	public static final String CANCELACION_ALZAMIENTO = "CANCELACION_ALZAMIENTO";	
	public static final String INSCRIBIR_PROPIEDAD = "INSCRIBIR_PROPIEDAD";	

	public static final int WF_COPIA_DOMINIO_SIN_VIGENTE = 39;
	public static final int WF_COPIA_DOMINIO_VIGENTE = 40;
	public static final int WF_CERTIFICADO_GP = 41;
	public static final int WF_TITULO_10 = 42;
	public static final int WF_CANCELACION_ALZAMIENTO = 43;
	public static final int WF_INSCRIBIR_PROPIEDAD = 44;

	public static final Integer CARATULAS_EN_PROCESO=0;
	public static final Integer CARATULAS_FINALIZADAS=1;
	public static final Integer CARATULAS_RECHAZADAS=2;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public ActionForward unspecified(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response){
		return null; //this.init(mapping, form, request, response);
	}

	@SuppressWarnings("unchecked")
	public void savePreCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray tramites = new JSONArray();
		Boolean status = false;
		String msg = "";
		Long numeroColilla = 0L;
		String registro = "";
		Integer foja = 0;
		Integer numero = 0;
		String ano = "0";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");

		try{

			foja = Integer.parseInt(request.getParameter("foja")==""?"0":request.getParameter("foja"));
			numero = Integer.parseInt(request.getParameter("numero")==""?"0":request.getParameter("numero"));
			ano = request.getParameter("ano");
			registro = request.getParameter("registro");
			String rut = request.getParameter("rut");
			String nombre = cambiaEncoding(request.getParameter("nombre")); 
			String apep = cambiaEncoding(request.getParameter("apep"));
			String apem = cambiaEncoding(request.getParameter("apem"));
			String direccion = cambiaEncoding(request.getParameter("direccion"));
			String telefono = request.getParameter("telefono");
			String correo = request.getParameter("correo");
			Boolean copiaDominioCV = Boolean.parseBoolean(request.getParameter("tramite1"));
			Boolean copiaDominioSV = Boolean.parseBoolean(request.getParameter("tramite2"));
			Boolean gp = Boolean.parseBoolean(request.getParameter("tramite3"));
			Boolean gpLitigio = Boolean.parseBoolean(request.getParameter("tramite4"));
			Boolean certificadoTitulo = Boolean.parseBoolean(request.getParameter("tramite5"));
			String obsCopiaCV = cambiaEncoding(request.getParameter("obs1"));
			String obsCopiaSV = cambiaEncoding(request.getParameter("obs2"));
			String obsGP = cambiaEncoding(request.getParameter("obs3"));
			String obsGPLitigio = cambiaEncoding(request.getParameter("obs4"));
			String obsCertiTitulo = cambiaEncoding(request.getParameter("obs5"));

			String dv = rut.substring(rut.length()-1, rut.length()); 
			rut = rut.substring(0, rut.length()-1);
			UsuarioWebVO usuarioWebVO = new UsuarioWebVO();
			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			RequirenteVO requirenteVO = wsCaratulaClienteDelegate.obtenerRequirentePorRut(usuarioWebVO, rut, dv.charAt(0));

			if(!"summit".equals(usuario)){
				if(requirenteVO != null){

					requirenteVO.setNombres(nombre);
					requirenteVO.setApellidoPaterno(apep);
					requirenteVO.setApellidoMaterno(apem);
					requirenteVO.setDireccion(direccion);
					requirenteVO.setEmail(correo);
					if(telefono!=null && !telefono.isEmpty()){
						requirenteVO.setTelefono(telefono);
					} 										
					//Existe requirente por lo que se procede a actualizar.
					wsCaratulaClienteDelegate.actualizarRequirente(usuarioWebVO, requirenteVO);

				} else {
					//No existe requirente, por lo tanto se crea un nuevo requirente
					usuarioWebVO.setRut(rut);
					usuarioWebVO.setDv(dv);
					usuarioWebVO.setNombres(nombre);
					usuarioWebVO.setApellidoPaterno(apep);
					usuarioWebVO.setApellidoMaterno(apem);
					usuarioWebVO.setDireccion(direccion);
					usuarioWebVO.setEMail(correo);
					if(telefono!=null && !telefono.isEmpty()){
						usuarioWebVO.setTelefono(telefono);
					} 														

					wsCaratulaClienteDelegate.crearNuevoRequirente(usuarioWebVO);
				}

				numeroColilla = wsCaratulaClienteDelegate.obtenerNumeroColilla();
				String rutCompleto = StringUtil.completaPorLaIzquierda(rut.trim(), 8, '0').trim() + dv.trim();

				Long valorCaratula=0L;
				if(!"com".equalsIgnoreCase(registro)){
					if(copiaDominioCV){
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 8, "COPIA_DOMINIO_VIGENTE", "Copia Con Vigencia", "44", foja, numero, ano, rutCompleto, obsCopiaCV,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia Con Vigencia");
						fila.put("obs", obsCopiaCV);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(copiaDominioSV){
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 9, "COPIA_DOMINIO_SIN_VIGENTE", "Copia Sin Vigencia", "43", foja, numero, ano, rutCompleto, obsCopiaSV,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia Sin Vigencia");
						fila.put("obs", obsCopiaSV);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(gp){
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP", "GP", "46", foja, numero, ano, rutCompleto, obsGP,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "GP");
						fila.put("obs", obsGP);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(gpLitigio){
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP_LITIGIO", "GP con Litigio", "47", foja, numero, ano, rutCompleto, obsGPLitigio,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "GP con Litigio");
						fila.put("obs", obsGPLitigio);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(certificadoTitulo){
						//				COPIA
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 8, "COPIA_DOMINIO_VIGENTE", "Copia Con Vigencia", "44", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia Con Vigencia");
						fila.put("obs", obsCertiTitulo);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
						//				GP
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP", "GP", "46", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
						JSONObject fila2 = new JSONObject();
						fila2.put("nombre", "GP");
						fila2.put("obs", obsCertiTitulo);
						fila.put("valor", valorCaratula);
						tramites.add(fila2);
						//				TITULO_10
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 10, "TITULO_10", "Tï¿½tulo Ultimos 10 Aï¿½os", "107", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
						JSONObject fila3 = new JSONObject();
						fila3.put("nombre", "Certificado Titulo");
						fila3.put("obs", obsCertiTitulo);
						fila.put("valor", valorCaratula);
						tramites.add(fila3);
					}
				}else if("com".equalsIgnoreCase(registro)){//Comercio
					if(copiaDominioCV){//COPIA_INSCRIPCION_CON_VIGENCIA - Comercio
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 4, "COPIA_INS_VIGENTE", "Copia Con Vigencia Com.", "108", foja, numero, ano, rutCompleto, obsCopiaCV,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia Con Vigenca Com.");
						fila.put("obs", obsCopiaCV);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(copiaDominioSV){//COPIA_INSCRIPCION_SIN_VIGENCIA - Comercio
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 3, "COPIA_INS_SIN_VIGENTE", "Copia Sin Vigencia Com.", "109", foja, numero, ano, rutCompleto, obsCopiaSV,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia Sin Vigenca Com.");
						fila.put("obs", obsCopiaSV);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(gp){//CERTIFICADO_VIGENCIA_SOC - Comercio
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_VIGENCIA_SOC", "Certificado Vigencia Soc. Com.", "45", foja, numero, ano, rutCompleto, obsGP,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Certificado Vigencia Soc. Com.");
						fila.put("obs", obsGP);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(gpLitigio){//CERTIFICADO_VIGENCIA_PODER - Comercio
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_VIGENCIA_PODER", "Certificado de Poder", "122", foja, numero, ano, rutCompleto, obsGPLitigio,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Copia de Poder");
						fila.put("obs", obsGPLitigio);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}
					if(certificadoTitulo){//CERTIFICADO_CAPITAL - Comercio
						valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_CAPITAL", "Certificado de Capital", "121", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
						JSONObject fila = new JSONObject();
						fila.put("nombre", "Certificado de Capital");
						fila.put("obs", obsCertiTitulo);
						fila.put("valor", valorCaratula);
						tramites.add(fila);
					}

				}

			}else if("summitDESHABILITADO20170911".equals(usuario)){
//				System.out.println("entrando precaratula summit");	
				Long valorCaratula=0L;
				Long montoTotal=0L;
				WsUsuarioWebDelegate deUsuarioWebDelegate = new WsUsuarioWebDelegate();
				WsBotonDePagoClienteDelegate delegate = new WsBotonDePagoClienteDelegate();
				UsuarioWebVO usuarioWeb = deUsuarioWebDelegate.obtenerUsuario(250023L);//evillar cta cte
//				System.out.println("consulte usuario evillar cta cte");
				
				ReceptorEmailVO receptorEmailVO = new ReceptorEmailVO();
				receptorEmailVO.setEMail(correo);
				receptorEmailVO.setNombreCompleto(nombre+" "+apep+" "+apem);
				receptorEmailVO.setNombreCorto("summit "+nombre+" "+apep+" "+apem);
				receptorEmailVO.setEstado(1);
				receptorEmailVO.setUsuarioWeb(usuarioWeb);
				receptorEmailVO = deUsuarioWebDelegate.agregarReceptorEmail(usuarioWeb, receptorEmailVO);
//				System.out.println("usuario summit agregarReceptorEmail");
								
				InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
				List<ProductoVO> productosTx = new ArrayList<ProductoVO>();
				ConsultaDocumentoDTO dto = digitalUtil.getConsultaDocumentoDTO(foja.longValue(), numero.longValue(), Long.valueOf(ano), false, 1);
//				System.out.println("usuario summit getConsultaDocumentoDTO");
				if(!"com".equalsIgnoreCase(registro)){

					if(copiaDominioCV){

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "COPIA_DOMINIO_VIGENTE").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,40L,valorCaratula,foja, numero,Integer.valueOf(ano),"Copia con Vigencia o Dominio Vigente",receptorEmailVO);

						productosTx.add(productoVO);

					}
					if(copiaDominioSV){

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "COPIA_DOMINIO_SIN_VIGENTE").longValue();
//						System.out.println("usuario summit valorDocumento");
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,39L,valorCaratula,foja, numero,Integer.valueOf(ano),"Copia sin Vigencia del Registro de Propiedad",receptorEmailVO);
//						System.out.println("usuario summit obtenerProductoVO");
						
						productosTx.add(productoVO);
					}
					if(gp){

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_GP").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,41L,valorCaratula,foja, numero,Integer.valueOf(ano),"Certificado de Hipotecas, Grav\u00E1menes y Prohibiciones (GP)",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(gpLitigio){

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_GP_LITIGIO").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,41L,valorCaratula,foja, numero,Integer.valueOf(ano),"Certificado de Hipotecas, Grav\u00E1menes y Prohibiciones (GP)",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(certificadoTitulo){
						//				COPIA

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "COPIA_DOMINIO_VIGENTE").longValue();
						montoTotal=montoTotal+valorCaratula;

						//				GP

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_GP").longValue();
						montoTotal=montoTotal+valorCaratula;

						//				TITULO_10

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "TITULO_10").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,42L,valorCaratula,foja, numero,Integer.valueOf(ano),"Carpeta de Tï¿½tulos ï¿½ltimos 10 Aï¿½os (Incluye GP y Copia con Vigencia)",receptorEmailVO);

						productosTx.add(productoVO);
					}
				}else if("com".equalsIgnoreCase(registro)){//Comercio
					if(copiaDominioCV){//COPIA_INSCRIPCION_CON_VIGENCIA - Comercio

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "COPIA_INS_VIGENTE").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,51L,valorCaratula,foja, numero,Integer.valueOf(ano),"Copia de Inscripci\u00F3n con Vigencia del Registro de Comercio",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(copiaDominioSV){//COPIA_INSCRIPCION_SIN_VIGENCIA - Comercio

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "COPIA_INS_SIN_VIGENTE").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,52L,valorCaratula,foja, numero,Integer.valueOf(ano),"Copia de Inscripci\u00F3n sin Vigencia del Registro de Comercio",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(gp){//CERTIFICADO_VIGENCIA_SOC - Comercio

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_VIGENCIA_SOC").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,53L,valorCaratula,foja, numero,Integer.valueOf(ano),"Certificado de Vigencia de Sociedad del Registro de Comercio",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(gpLitigio){//CERTIFICADO_VIGENCIA_PODER - Comercio

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_VIGENCIA_PODER").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,54L,valorCaratula,foja, numero,Integer.valueOf(ano),"Certificado de Vigencia de Poder del Registro de Comercio",receptorEmailVO);

						productosTx.add(productoVO);
					}
					if(certificadoTitulo){//CERTIFICADO_CAPITAL - Comercio

						valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, "CERTIFICADO_CAPITAL").longValue();
						montoTotal=montoTotal+valorCaratula;

						ProductoVO productoVO = this.obtenerProductoVO(usuarioWeb,55L,valorCaratula,foja, numero,Integer.valueOf(ano),"Certificado de Capital para Publicaci\u00F3n en Diario Oficial",receptorEmailVO);

						productosTx.add(productoVO);
					}

				}

				TransaccionWebVO  transaccionWebVO = new TransaccionWebVO();

				transaccionWebVO.setUsuarioWeb(usuarioWeb);

				Integer[] receptorEmailArr = new Integer[1];
				receptorEmailArr[0] = receptorEmailVO.getId().intValue();
				
				transaccionWebVO.setReceptoresEmail(receptorEmailArr);
				transaccionWebVO.setMontoTransaccion(montoTotal);
				transaccionWebVO.setEstado(1);
				
				ProductoVO[] productoArr = new ProductoVO[productosTx.size()];
				productoArr = productosTx.toArray(productoArr);
				transaccionWebVO.setProductos(productoArr);

				try{
					SolicitarTransaccionPagoWebResponse solicitarTransaccionPagoWebResponse = delegate.solicitarTransaccionPagoWebSinException(transaccionWebVO);
//					System.out.println("usuario summit solicitarTransaccionPagoWebResponse " + solicitarTransaccionPagoWebResponse.getEstadoTransaccion().getEstado());

					if (!solicitarTransaccionPagoWebResponse.getEstadoTransaccion().getEstado().equals("OK")){
						//pude escribir la tx en mis tablas pero fallï¿½ otra cosa, TGR??
						if(solicitarTransaccionPagoWebResponse.getResponse()!=null){
//							System.out.println(solicitarTransaccionPagoWebResponse.getResponse());
						}

					} else {
//						System.out.println(solicitarTransaccionPagoWebResponse.getResponse());
					}	
				} catch (GeneralException e) {

					logger.error(e.getMessage());
//					System.out.println(e.getMessage());	

				}
			}

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (UnsupportedEncodingException e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("colilla", "0"+numeroColilla.toString());
		respuesta.put("tramites", tramites);

		String reg = "";
		if("prop".equalsIgnoreCase(registro))
			reg="Propiedad";
		else if("com".equalsIgnoreCase(registro))
			reg="Comercio";
		else if("proh".equalsIgnoreCase(registro))
			reg="Prohibiciones";
		else if("hip".equalsIgnoreCase(registro))
			reg="Hipoteca";

		respuesta.put("registro", reg);
		respuesta.put("foja", foja);
		respuesta.put("numero", numero);
		respuesta.put("ano", ano);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void savePreCaratulaMasiva(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray colillas = new JSONArray();
		JSONArray colillascanvas = new JSONArray();
		JSONArray tramitesGeneral = new JSONArray();
		Boolean status = false;
		String msg = "";
		Long numeroColilla = 0L;
		String registro = "";
		Integer foja = 0;
		Integer numero = 0;
		String ano = "0";
		String vigente = "";

		String usuario = request.getUserPrincipal().getName();
		usuario = usuario.replaceAll("CBRS\\\\", "");

		try{

			foja = Integer.parseInt(request.getParameter("foja")==""?"0":request.getParameter("foja"));
			numero = Integer.parseInt(request.getParameter("numero")==""?"0":request.getParameter("numero"));
			ano = request.getParameter("ano");
			registro = request.getParameter("registro");
			String rut = request.getParameter("rut");
			String nombre = cambiaEncoding(request.getParameter("nombre")); 
			String apep = cambiaEncoding(request.getParameter("apep"));
			String apem = cambiaEncoding(request.getParameter("apem"));
			String direccion = cambiaEncoding(request.getParameter("direccion"));
			String telefono = request.getParameter("telefono");
			String correo = request.getParameter("correo");
			Boolean copiaDominioCV = Boolean.parseBoolean(request.getParameter("tramite1"));
			Boolean copiaDominioSV = Boolean.parseBoolean(request.getParameter("tramite2"));
			Boolean gp = Boolean.parseBoolean(request.getParameter("tramite3"));
			Boolean gpLitigio = Boolean.parseBoolean(request.getParameter("tramite4"));
			Boolean certificadoTitulo = Boolean.parseBoolean(request.getParameter("tramite5"));
			String obsCopiaCV = cambiaEncoding(request.getParameter("obs1"));
			String obsCopiaSV = cambiaEncoding(request.getParameter("obs2"));
			String obsGP = cambiaEncoding(request.getParameter("obs3"));
			String obsGPLitigio = cambiaEncoding(request.getParameter("obs4"));
			String obsCertiTitulo = cambiaEncoding(request.getParameter("obs5"));
			String[] titulos = request.getParameterValues("titulos");

			String dv = rut.substring(rut.length()-1, rut.length()); 
			rut = rut.substring(0, rut.length()-1);
			UsuarioWebVO usuarioWebVO = new UsuarioWebVO();
			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			RequirenteVO requirenteVO = wsCaratulaClienteDelegate.obtenerRequirentePorRut(usuarioWebVO, rut, dv.charAt(0));

			if(requirenteVO != null){

				requirenteVO.setNombres(nombre);
				requirenteVO.setApellidoPaterno(apep);
				requirenteVO.setApellidoMaterno(apem);
				requirenteVO.setDireccion(direccion);
				requirenteVO.setEmail(correo);
				if(telefono!=null && !telefono.isEmpty()){
					requirenteVO.setTelefono(telefono);
				} 										
				//Existe requirente por lo que se procede a actualizar
				wsCaratulaClienteDelegate.actualizarRequirente(usuarioWebVO, requirenteVO);
			} else {
				//No existe requirente, por lo tanto se crea un nuevo requirente
				usuarioWebVO.setRut(rut);
				usuarioWebVO.setDv(dv);
				usuarioWebVO.setNombres(nombre);
				usuarioWebVO.setApellidoPaterno(apep);
				usuarioWebVO.setApellidoMaterno(apem);
				usuarioWebVO.setDireccion(direccion);
				usuarioWebVO.setEMail(correo);
				if(telefono!=null && !telefono.isEmpty()){
					usuarioWebVO.setTelefono(telefono);
				} 														

				wsCaratulaClienteDelegate.crearNuevoRequirente(usuarioWebVO);
			}

			String rutCompleto = StringUtil.completaPorLaIzquierda(rut.trim(), 8, '0').trim() + dv.trim();

			if(titulos!=null){
				JSONParser jsonParser = new JSONParser();

				for(String titulo:titulos){
					JSONArray tramites = new JSONArray();
					JSONObject tituloJSON = (JSONObject)jsonParser.parse(titulo);
					foja = Integer.parseInt(tituloJSON.get("foja").toString());
					numero = Integer.parseInt(tituloJSON.get("numero").toString());
					ano = tituloJSON.get("anio").toString();
					vigente = tituloJSON.get("vigente").toString();

					numeroColilla = wsCaratulaClienteDelegate.obtenerNumeroColilla();

					Long valorCaratula=0L;
					if(!"com".equalsIgnoreCase(registro)){
						if("false".equalsIgnoreCase(vigente)){
							copiaDominioCV = false;
							copiaDominioSV = true; //si no es dominio vigente se dara solo Copia s/vigencia
							gp = false; 
							gpLitigio = false; 
							certificadoTitulo = false; 
						}else{
							copiaDominioCV = Boolean.parseBoolean(request.getParameter("tramite1"));
							copiaDominioSV = Boolean.parseBoolean(request.getParameter("tramite2"));
							gp = Boolean.parseBoolean(request.getParameter("tramite3"));
							gpLitigio = Boolean.parseBoolean(request.getParameter("tramite4"));
							certificadoTitulo = Boolean.parseBoolean(request.getParameter("tramite5"));
						}

						if(copiaDominioCV){
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 8, "COPIA_DOMINIO_VIGENTE", "Copia Con Vigencia", "44", foja, numero, ano, rutCompleto, obsCopiaCV,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia Con Vigencia");
							fila.put("obs", obsCopiaCV);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(copiaDominioSV){
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 9, "COPIA_DOMINIO_SIN_VIGENTE", "Copia Sin Vigencia", "43", foja, numero, ano, rutCompleto, obsCopiaSV,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia Sin Vigencia");
							fila.put("obs", obsCopiaSV);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(gp){
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP", "GP", "46", foja, numero, ano, rutCompleto, obsGP,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "GP");
							fila.put("obs", obsGP);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(gpLitigio){
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP_LITIGIO", "GP con Litigio", "47", foja, numero, ano, rutCompleto, obsGPLitigio,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "GP con Litigio");
							fila.put("obs", obsGPLitigio);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(certificadoTitulo){
							//				COPIA
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 8, "COPIA_DOMINIO_VIGENTE", "Copia Con Vigencia", "44", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia Con Vigencia");
							fila.put("obs", obsCertiTitulo);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
							//				GP
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 5, "CERTIFICADO_GP", "GP", "46", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
							JSONObject fila2 = new JSONObject();
							fila2.put("nombre", "GP");
							fila2.put("obs", obsCertiTitulo);
							fila.put("valor", valorCaratula);

							tramites.add(fila2);
							//				TITULO_10
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 10, "TITULO_10", "Tï¿½tulo Ultimos 10 Aï¿½os", "107", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
							JSONObject fila3 = new JSONObject();
							fila3.put("nombre", "Certificado Titulo");
							fila3.put("obs", obsCertiTitulo);
							fila.put("valor", valorCaratula);

							tramites.add(fila3);
						}

					}else if("com".equalsIgnoreCase(registro)){//Comercio
						if(copiaDominioCV){//COPIA_INSCRIPCION_CON_VIGENCIA - Comercio
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 4, "COPIA_INS_VIGENTE", "Copia Con Vigencia Com.", "108", foja, numero, ano, rutCompleto, obsCopiaCV,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia Con Vigenca Com.");
							fila.put("obs", obsCopiaCV);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(copiaDominioSV){//COPIA_INSCRIPCION_SIN_VIGENCIA - Comercio
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 3, "COPIA_INS_SIN_VIGENTE", "Copia Sin Vigencia Com.", "109", foja, numero, ano, rutCompleto, obsCopiaSV,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia Sin Vigenca Com.");
							fila.put("obs", obsCopiaSV);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(gp){//CERTIFICADO_VIGENCIA_SOC - Comercio
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_VIGENCIA_SOC", "Certificado Vigencia Soc. Com.", "45", foja, numero, ano, rutCompleto, obsGP,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Certificado Vigencia Soc. Com.");
							fila.put("obs", obsGP);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(gpLitigio){//CERTIFICADO_VIGENCIA_PODER - Comercio
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_VIGENCIA_PODER", "Certificado de Poder", "122", foja, numero, ano, rutCompleto, obsGPLitigio,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Copia de Poder");
							fila.put("obs", obsGPLitigio);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}
						if(certificadoTitulo){//CERTIFICADO_CAPITAL - Comercio
							valorCaratula=crearCaratulaTemp(wsCaratulaClienteDelegate, 7, "CERTIFICADO_CAPITAL", "Certificado de Capital", "121", foja, numero, ano, rutCompleto, obsCertiTitulo,registro,numeroColilla,usuario);
							JSONObject fila = new JSONObject();
							fila.put("nombre", "Certificado de Capital");
							fila.put("obs", obsCertiTitulo);
							fila.put("valor", valorCaratula);

							tramites.add(fila);
						}

					}

					tramitesGeneral.add(tramites);
					colillas.add("0"+numeroColilla);
					colillascanvas.add(numeroColilla+""+numero+""+ano);


				}
			}

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1.getMessage(),e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);
		respuesta.put("colilla", colillas);
		respuesta.put("colillacanvas", colillascanvas);
		//		respuesta.put("tramites", tramites);
		respuesta.put("tramitesGeneral", tramitesGeneral);

		String reg = "";
		if("prop".equalsIgnoreCase(registro))
			reg="Propiedad";
		else if("com".equalsIgnoreCase(registro))
			reg="Comercio";
		else if("proh".equalsIgnoreCase(registro))
			reg="Prohibiciones";
		else if("hip".equalsIgnoreCase(registro))
			reg="Hipoteca";

		respuesta.put("registro", reg);
		respuesta.put("foja", foja);
		respuesta.put("numero", numero);
		respuesta.put("ano", ano);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCantidadCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";
		int totalCaratulasEnProceso = 0;
		int totalCaratulasTerminadas = 0;

		try{

			Long foja = Long.parseLong(request.getParameter("foja"));
			String numero = request.getParameter("numero");
			Long ano = Long.parseLong(request.getParameter("ano"));

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			totalCaratulasEnProceso = wsCaratulaClienteDelegate.cantidadCaratulasPorTitulo(foja, numero, ano, 0, CARATULAS_EN_PROCESO);
			totalCaratulasTerminadas = wsCaratulaClienteDelegate.cantidadCaratulasPorTitulo(foja, numero, ano, 0, CARATULAS_FINALIZADAS);

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("totalCaratulasEnProceso", totalCaratulasEnProceso);
		respuesta.put("totalCaratulasTerminadas", totalCaratulasTerminadas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private Long crearCaratulaTemp(WsCaratulaClienteDelegate wsCaratulaClienteDelegate, Integer tipoFormularioCaratula, String nombreDoc, String descripTarea, String codTarea, Integer foja, Integer numero, String ano, String rutRequirente, String obs,String registro,Long numeroColilla,String usuarioCreador) throws Exception{

		Long numeroCaratulaTemp;
		Long valorCaratula = 0L;

		numeroCaratulaTemp = wsCaratulaClienteDelegate.obtenerNumeroCaratulaTemp();

		CaratulaTempVO caratulaTempVO=new CaratulaTempVO();
		caratulaTempVO.setCaratula(numeroCaratulaTemp.intValue());
		TipoFormularioVO tipoFormulario = new TipoFormularioVO();
		tipoFormulario.setTipo(tipoFormularioCaratula);
		caratulaTempVO.setTipoFormulario(tipoFormulario);

		InscripcionDigitalUtil digitalUtil = new InscripcionDigitalUtil();
		ConsultaDocumentoDTO dto = digitalUtil.getConsultaDocumentoDTO(foja.longValue(), numero.longValue(), Long.valueOf(ano), false, 1);

		valorCaratula = valorDocumento(dto.getCantidadPaginas(), 0, 0, nombreDoc).longValue();
		//		valorCaratula = new Long(TablaValores.getValor(ARCHIVO, nombreDoc, "base"));

		caratulaTempVO.setValorPagado(valorCaratula.intValue());
		caratulaTempVO.setValorTasado(valorCaratula.intValue());
		caratulaTempVO.setIdCaja("INFO");
		//t=tasado
		caratulaTempVO.setEstado("t");
		caratulaTempVO.setCapital(0L);
		caratulaTempVO.setCodMoneda("$");
		caratulaTempVO.setFecha(new Date());
		caratulaTempVO.setObs(obs);

		TareaTempVO[] tareaLista = new TareaTempVO[1];
		TareaTempVO tar = new TareaTempVO();
		TareaIdTempVO tarid = new TareaIdTempVO();
		tarid.setCodTarea(codTarea);
		tarid.setCapital(0L);
		tarid.setIdCaja("INFO");
		tarid.setTotalTarea(valorCaratula);
		tarid.setDescTarea(descripTarea);
		tarid.setCodMoneda("$");
		tarid.setCaratula(numeroCaratulaTemp.intValue());

		tar.setId(tarid);
		tareaLista[0]=tar;
		caratulaTempVO.setTareas(tareaLista);

		InscripcionCitadaTempVO[] insLista = new InscripcionCitadaTempVO[1];
		InscripcionCitadaTempVO ins = new InscripcionCitadaTempVO();
		ins.setFoja(foja);
		ins.setNumero(numero);
		ins.setAño(ano);
		ins.setTipoForm(tipoFormularioCaratula.toString());
		if("prop".equals(registro))
			ins.setRegistro("1");
		else if("hip".equals(registro))
			ins.setRegistro("2");
		else if("proh".equals(registro))	
			ins.setRegistro("3");
		else if("com".equals(registro))	
			ins.setRegistro("4");

		ins.setBorrador(0);
		ins.setFolio(0);
		ins.setFechaTasacion(new Date());
		ins.setUsuario(usuarioCreador);
		ins.setMaquina("192.168.100.1");
		ins.setRutRequirente(rutRequirente);
		ins.setValorTasado(valorCaratula.intValue());
		ins.setIdColilla(numeroColilla.intValue());
		ins.setDigital(false);
		CaratulaTempVO cc = new CaratulaTempVO();
		cc.setCaratula(numeroCaratulaTemp.intValue());
		ins.setCaratulaTempVO(cc);
		insLista[0] = ins;
		caratulaTempVO.setInscripciones(insLista);

		wsCaratulaClienteDelegate.agregarCaratulaTemp(caratulaTempVO);

		return(valorCaratula);


	}

	private String cambiaEncoding(String campo) throws UnsupportedEncodingException{
		String campoConEncoding=new String("");
		if(null!=campo){
			if(!"".equals(campo)){
				campoConEncoding = new String(campo.getBytes(),"UTF-8");
			}
		}

		return campoConEncoding;

	}

	private static Double valorDocumento(long carillas, long capital, long capitalDerechos, String tipoACTO)
			throws Exception {
		Double carilla = null;
		Double base = null;
		Double factor_capital = null;
		Double factor_derecho = null;
		carilla = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "carilla"));//300
		base = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "base"));
		factor_capital = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "factor_capital"));//0
		factor_derecho = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "derecho"));//0
		Double minimo = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "minimo"));//4600
		Double maximo = new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "maximo"));//150000

		Double diferenciaMinina= 0.0D;
		if("COPIA_DOMINIO_VIGENTE".equals(tipoACTO) || "COPIA_DOMINIO_SIN_VIGENTE".equals(tipoACTO) || "COPIA_INS_VIGENTE".equals(tipoACTO) || "COPIA_INS_SIN_VIGENTE".equals(tipoACTO))
			diferenciaMinina=new Double(TablaValores.getValor(ARCHIVO,tipoACTO, "diferencia_minima"));//2400

		double valor = 0.0D;
		valor = carilla.doubleValue() * (double) carillas + base.doubleValue() + factor_capital.doubleValue()
		* (double) capital + factor_derecho.doubleValue() * (double) capitalDerechos;
		if (valor < minimo.doubleValue()) {
			valor = minimo.doubleValue();
		}
		if (valor > maximo.doubleValue()) {
			valor = maximo.doubleValue();
		}
		//if (tipoACTO.equals("COPIA_VIGENCIA") && valor - minimo.doubleValue() < 2400D) {
		if ((valor - minimo.doubleValue()) < diferenciaMinina.doubleValue()) {
			return minimo;
		}
		else {
			return Double.valueOf(valor);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCaratulasPorFoja(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray caratulas = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<CaratulaVO> listaCaratulas = null;

		try{

			Long foja = Long.parseLong(request.getParameter("foja"));
			String numero = request.getParameter("numero");
			Long ano = Long.parseLong(request.getParameter("ano"));
			int estado = Integer.parseInt(request.getParameter("estado"));

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			listaCaratulas = wsCaratulaClienteDelegate.obtenerCaratulasPorTitulo(foja, numero, ano, 0, estado);
			if(listaCaratulas!=null){ 
				if(listaCaratulas.size()>0){
					for(CaratulaVO caratulaVO: listaCaratulas){
						JSONObject fila = new JSONObject();
						fila.put("numerocaratula", caratulaVO.getNumeroCaratula());
						fila.put("codtipoform", caratulaVO.getTipoFormulario().getTipo());
						fila.put("tipoform", caratulaVO.getTipoFormulario().getDescripcion());
						fila.put("estadoactual", caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion());
						caratulas.add(fila);
					}
				}	
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacaratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCausalesRechazo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray causales = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<CausalRechazoVO> listaCausales = null;

		try{

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			listaCausales = wsCaratulaClienteDelegate.obtenerCausalesRechazo();
			if(listaCausales!=null){ 
				if(listaCausales.size()>0){
					for(CausalRechazoVO causalRechazoVO: listaCausales){
						JSONObject fila = new JSONObject();
						fila.put("codigo", causalRechazoVO.getCodigoCausaRechazo());
						fila.put("descripcion", causalRechazoVO.getDescripcionCausaRechazo());
						fila.put("template", causalRechazoVO.getTemplateCausaRechazo());
						causales.add(fila);
					}
				}	
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacausales", causales);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void actualizarInscripcion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try{

			Long caratula = Long.parseLong(request.getParameter("caratula"));
			Integer foja = Integer.parseInt(request.getParameter("foja"));
			Integer numero = Integer.parseInt(request.getParameter("numero"));
			Integer ano = Integer.parseInt(request.getParameter("ano"));
			Integer bis = Integer.parseInt(request.getParameter("bis"));

			UsuarioWebVO usuarioWebVO = new UsuarioWebVO();
			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			CaratulaVO caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(usuarioWebVO, caratula);

			if(null != caratulaVO){

				InscripcionCitadaVO[] ins = caratulaVO.getInscripciones();
				InscripcionCitadaVO inscripcionCitadaVO = ins[0];
				inscripcionCitadaVO.setFoja(foja);
				inscripcionCitadaVO.setNumero(numero);
				inscripcionCitadaVO.setAno(ano);
				inscripcionCitadaVO.setBis(bis);
				ins[0]=inscripcionCitadaVO;
				caratulaVO.setInscripciones(ins);
				//Existe requirente por lo que se procede a actualizar
				wsCaratulaClienteDelegate.actualizarCaratula(caratulaVO);

				//Moviendo caratula a En proceso de Copia Electronica para que se rehaga la digitalizacion  
				String rutEnvia = (String)request.getSession().getAttribute("rutUsuario");	        	

				EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

				FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
				funcionarioEnviaVO.setRutFuncionario(rutEnvia);
				estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

				FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
				funcionarioResponsableVO.setRutFuncionario("00000105");
				estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	
				
				SeccionVO seccionVO = new SeccionVO();
				if(inscripcionCitadaVO.getRegistro().equals(3))
					seccionVO.setCodigo("P0"); //En Proceso Copia Electronica PH
				else if(inscripcionCitadaVO.getRegistro().equals(2))
					seccionVO.setCodigo("C9"); //En Proceso Copia Electronica H
				else
					seccionVO.setCodigo("C4"); //En Proceso Copia Electronica
				
				estadoCaratulaVO.setSeccion(seccionVO);
				estadoCaratulaVO.setMaquina("Sistema Certificacion");

				estadoCaratulaVO.setFechaMovimiento(new Date());

				wsCaratulaClienteDelegate.moverCaratulaSeccion(caratula, estadoCaratulaVO);

			} 

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerInscripcionBis(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String caratulap = request.getParameter("caratula");
		String fojap = request.getParameter("foja");
		String numero = request.getParameter("numero");
		String anop = request.getParameter("ano");	
		String bisp = request.getParameter("bis");	

		Long caratula = null;
		try {
			caratula = Long.parseLong(caratulap);
		} catch (Exception e1) {

		}

		Long foja = null;
		try {
			foja = Long.parseLong(fojap);
		} catch (Exception e1) {

		}

		Long ano = null;
		try {
			ano = Long.parseLong(anop);
		} catch (Exception e1) {

		}

		Boolean bis = false;
		if("true".equals(bisp)){
			bis = true;	
		}

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		String rutEnvia = null;	        	

		if(!"".equals(caratula)){
			try{
				WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();

				CaratulaVO caratulaVO = delegateCaratula.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratula);
				InscripcionCitadaVO[] ins = caratulaVO.getInscripciones();
				for(InscripcionCitadaVO in:ins){
					//asigno BIS a inscripcion caratula
					if(in.getFoja().equals(foja.intValue()) && in.getNumero().equals(Integer.parseInt(numero)) && in.getAno().equals(ano.intValue()))
						in.setBis(1);
				}
				caratulaVO.setInscripciones(ins);

				delegateCaratula.actualizarCaratula(caratulaVO);

				//Moviendo caratula a Copia BIS  
				rutEnvia = (String)request.getSession().getAttribute("rutUsuario");	 

				EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

				FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
				funcionarioEnviaVO.setRutFuncionario(rutEnvia);
				estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

				FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
				funcionarioResponsableVO.setRutFuncionario("00000105");
				estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	

				SeccionVO seccionVO = new SeccionVO();
				if(ins[0].getRegistro().equals(3))
					seccionVO.setCodigo("P0"); 
				else if(ins[0].getRegistro().equals(2))
					seccionVO.setCodigo("C9"); 
				else
					seccionVO.setCodigo("C4"); 
				estadoCaratulaVO.setSeccion(seccionVO);
				estadoCaratulaVO.setMaquina("Sistema Certificacion");

				estadoCaratulaVO.setFechaMovimiento(new Date());

				delegateCaratula.moverCaratulaSeccion(caratula, estadoCaratulaVO);

				status=true;

			} catch (GeneralException e1) {
				logger.error(e1);

				status = false;
				msg = "Se ha detectado un problema en el servidor.";	
			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);	

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void moverCartulaRedistribucion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String caratulap = request.getParameter("caratula");
		String seccionp = request.getParameter("seccion");

		Long caratula = null;
		try {
			caratula = Long.parseLong(caratulap);
		} catch (Exception e1) {

		}

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		String rutEnvia = null;	        	

		if(!"".equals(caratula)){
			try{
				WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();

				//Moviendo caratula a Copia BIS  
				rutEnvia = (String)request.getSession().getAttribute("rutUsuario");	 
				rutEnvia = StringUtil.rellenaPorLaIzquierda(rutEnvia, 9, '0');

				EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();

				FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
				funcionarioEnviaVO.setRutFuncionario(rutEnvia);
				estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

				FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();
				funcionarioResponsableVO.setRutFuncionario(rutEnvia);
				estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	

				SeccionVO seccionVO = new SeccionVO();
				seccionVO.setCodigo(seccionp); 
				estadoCaratulaVO.setSeccion(seccionVO);
				estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));

				estadoCaratulaVO.setFechaMovimiento(new Date());

				delegateCaratula.moverCaratulaSeccion(caratula, estadoCaratulaVO);

				status=true;

			} catch (GeneralException e1) {
				logger.error(e1);

				status = false;
				msg = "Se ha detectado un problema en el servidor.";	
			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);	

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void rechazarCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		Boolean status = false;
		String msg = "";

		try{

			Long caratula = Long.parseLong(request.getParameter("caratula"));
			String codigoRechazo = request.getParameter("codigoRechazo");
			String descripcion = new String(request.getParameter("descripcionRechazo").getBytes(),"UTF-8");

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			String rutFuncionario = (String)request.getSession().getAttribute("rutUsuario");
			rutFuncionario = StringUtil.rellenaPorLaIzquierda(rutFuncionario, 9, '0');
			wsCaratulaClienteDelegate.rechazarCaratula(caratula, rutFuncionario, descripcion, new Integer(codigoRechazo));

			status = true;

			respuesta.put("status", status);
			respuesta.put("msg", "Se ha rechazado la caratula");

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	

			respuesta.put("status", status);
			respuesta.put("msg", msg);
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";

			respuesta.put("status", status);
			respuesta.put("msg", msg);
		}

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerBitacoraCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray bitacoras = new JSONArray();
		Boolean status = false;
		String msg = "";
		CaratulaVO caratulaVO = null;

		try{
			String caratulast = request.getParameter("caratula");
			Long caratula = Long.parseLong(request.getParameter("caratula"));

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratula);
			if(caratulaVO!=null){ 
				BitacoraCaratulaVO[] bitacoraCaratulaVOs = caratulaVO.getBitacoraCaratulaVO();
				if(null!=bitacoraCaratulaVOs){
					for(BitacoraCaratulaVO bitacora:bitacoraCaratulaVOs){
						JSONObject fila = new JSONObject();
						fila.put("id", bitacora.getIdBitacora());
						fila.put("observacion", bitacora.getObservacion());
						fila.put("fecha", sdf.format(bitacora.getFecha()));
						fila.put("funcionario", bitacora.getNombreFuncionario()+ " " +  bitacora.getApellidoPaternoFuncionario());
						bitacoras.add(fila);
					}
				}
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listabitacoras", bitacoras);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerCaratula(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONObject fila = new JSONObject();
		Boolean status = false;
		Boolean muestra = false;
		String msg = "";
		CaratulaVO caratulaVO = null;

		final String TABLA_PARAMETROS="ProcesaImpresion.parametros";
		Integer FECHA_REHACER_IMAGEN= Integer.parseInt(TablaValores.getValor(TABLA_PARAMETROS,"FECHA_REHACER_IMAGEN", "valor"));

		try{
			String caratulast = request.getParameter("caratula");
			if(caratulast.length()>=7){
				Long caratula = Long.parseLong(caratulast.trim());

				WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
				WsInscripcionDigitalDelegate insdelegate = new WsInscripcionDigitalDelegate();
				caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(),caratula);
				if(caratulaVO!=null){ 

					fila.put("numeroCaratula", caratulaVO.getNumeroCaratula());
					fila.put("tipoform", caratulaVO.getTipoFormulario().getDescripcion());
					fila.put("estadoactual", caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion());
					fila.put("codSeccion", caratulaVO.getEstadoActualCaratula().getSeccion().getCodigo());
					fila.put("usuarioactual", caratulaVO.getEstadoActualCaratula().getFuncionario().getNombre()+" "+caratulaVO.getEstadoActualCaratula().getFuncionario().getApellidoPaterno());
					fila.put("usuarioLoginActual", caratulaVO.getEstadoActualCaratula().getFuncionario().getNombreUsuario().toLowerCase());
					fila.put("fechaseccion", sdf.format(caratulaVO.getEstadoActualCaratula().getFechaMov()));
					fila.put("estadoDescargaEscri", caratulaVO.getEstadoDescargaEscri());

					InscripcionCitadaVO[] inscripcionCitadaVOs = caratulaVO.getInscripciones();
					if(null != inscripcionCitadaVOs){
						for(InscripcionCitadaVO ins : inscripcionCitadaVOs){

							fila.put("fechaCreacion", sdf.format(caratulaVO.getEstadoActualCaratula().getFechaMov()));

							if(null != caratulaVO.getTipoFormulario()){
								fila.put("tipoFormulario", caratulaVO.getTipoFormulario().getDescripcion());
								fila.put("idtipoFormulario", caratulaVO.getTipoFormulario().getTipo());
							}else{
								fila.put("tipoFormulario", "");
								fila.put("idtipoFormulario", "");
							}	

							fila.put("foja", ins.getFoja());
							fila.put("numero", ins.getNumero());
							fila.put("ano", ins.getAno());
							fila.put("bis", ins.getBis());

							if(FECHA_REHACER_IMAGEN <= ins.getAno())
								fila.put("rehaceImagen", 0);
							else	
								fila.put("rehaceImagen", 1);

							InscripcionDigitalVO inscripcionDigitalVO = insdelegate.obtenerInscripcionDigital(new Long(ins.getFoja()),ins.getNumero().toString(),new Long(ins.getAno()), false);

							if(null!=inscripcionDigitalVO){
								fila.put("fechaDoc", sdf.format(inscripcionDigitalVO.getFechaActualizacion()));
								fila.put("fechaDocLong", inscripcionDigitalVO.getFechaActualizacion().getTime());
							}

						}
					}

					muestra=true;
				}	
			}
			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("caratula", fila);
		respuesta.put("muestra", muestra);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getListadoCaratulasPorCtaCte(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray caratulas = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<CaratulaVO> listaCaratulas = null;

		try{
			//			 final String ARCHIVO_CARATULA="ws_caratula.parametros";


			String fechaDesdeStr = request.getParameter("fechaDesde")==null?"":request.getParameter("fechaDesde");
			String fechaHastaStr = request.getParameter("fechaHasta")==null?"":request.getParameter("fechaHasta");
			Long numCuenta = request.getParameter("numCuenta")==null || request.getParameter("numCuenta").equals("")?null:Long.parseLong(request.getParameter("numCuenta"));
			Integer estado = request.getParameter("estado")==null || request.getParameter("estado").equals("")?null:Integer.parseInt(request.getParameter("estado"));

			Date fechaDesde = null;
			Date fechaHasta = null;

			if(fechaDesdeStr!="")
				fechaDesde = new SimpleDateFormat("yyyy-MM-dd").parse(fechaDesdeStr);
			if(fechaHastaStr!="")
				fechaHasta = new SimpleDateFormat("yyyy-MM-dd").parse(fechaHastaStr);

			//			 String cuentasStr = TablaValores.getValor(ARCHIVO_CARATULA,"CTAS_CORRIENTES_CON_CODIGO", "valor");
			//			 Boolean buscarNumeroOperacion = false;
			//			 if(cuentasStr!=null){
			//				 for(String cta : cuentasStr.split(","))
			//					 if(new Long(cta).equals(numCuenta))
			//						 buscarNumeroOperacion = true;
			//			 }


			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			listaCaratulas = wsCaratulaClienteDelegate.obtenerCaratulasPorCtaCte(numCuenta, estado, fechaDesde, fechaHasta);
			if(listaCaratulas!=null){ 
				if(listaCaratulas.size()>0){
					for(CaratulaVO caratulaVO: listaCaratulas){
						JSONObject fila = new JSONObject();
						fila.put("caratula", caratulaVO.getNumeroCaratula());
						fila.put("fechacreacion",  sdf.format(caratulaVO.getFechaCreacion()));
						fila.put("fechacreacionL",  caratulaVO.getFechaCreacion().getTime());
						fila.put("estadoactual", caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion());
						fila.put("estado", caratulaVO.getEstadoActualCaratula().getDescripcionEnFlujo());
						fila.put("fechaseccion",  sdf.format(caratulaVO.getEstadoActualCaratula().getFechaMov()));
						fila.put("fechaseccionL",  caratulaVO.getEstadoActualCaratula().getFechaMov().getTime());
						fila.put("clienteCtaCte", caratulaVO.getClienteCtaCte());

						if(caratulaVO.getProducto()!=null && caratulaVO.getProducto().getListaProductoGlosaVO()!=null &&
								caratulaVO.getProducto().getListaProductoGlosaVO()[0]!=null && 
								caratulaVO.getProducto().getListaProductoGlosaVO()[0].getGlosa().indexOf("cliente:")>=0
								)
							fila.put("glosaProducto", caratulaVO.getProducto().getListaProductoGlosaVO()[0].getGlosa().substring(20));
						//						 if(buscarNumeroOperacion){
						//							 caratulaVO = wsCaratulaClienteDelegate.obtenerCaratulaPorNumero(new UsuarioWebVO(), caratulaVO.getNumeroCaratula());
						//							 if(caratulaVO.getProducto()!=null && caratulaVO.getProducto().getListaProductoGlosaVO()!=null){
						//									ProductoGlosaVO[] glosaProd = caratulaVO.getProducto().getListaProductoGlosaVO();
						//									if(glosaProd!=null && glosaProd.length>0){	
						//										for(ProductoGlosaVO pg : glosaProd){
						//											if(pg.getGlosa().indexOf("Observaciï¿½n cliente:")>=0)
						//												fila.put("glosaProducto", pg.getGlosa().substring(20));
						//										}			
						//									}
						//							 }								 
						//								 
						//							 
						//						 }


						caratulas.add(fila);
					}
				}	
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacaratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getCierreCtaCte(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray detalleNominas = new JSONArray();
		Boolean status = false;
		String msg = "";
		NominaCtaCteAioVO nominaCtaCteAioVO = null;
		ListaNominaCtaCteAioVO[] lista = null;
		SimpleDateFormat sdfcierre = new SimpleDateFormat("dd/MM/yyyy");
		String urlBoleta = ConstantesPortalConservador.getParametro("PATH_DESCARGA_BOLETA");

		try{

			String cuenta = request.getParameter("cuenta")==null?"":request.getParameter("cuenta");
			String fechaStr = request.getParameter("fecha")==null?"":request.getParameter("fecha");
			String tipoCierre = request.getParameter("tipoCierre")==null?"":request.getParameter("tipoCierre");

			if(tipoCierre.equals(""))
				tipoCierre="0";

			fechaStr = fechaStr+"-01";

			Date fecha = null;

			if(fechaStr!="")
				fecha = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);

			WsCuentaCorrienteClienteDelegate delegate = new WsCuentaCorrienteClienteDelegate();
			nominaCtaCteAioVO = delegate.obtenerCierreCtaCte(cuenta, fecha, Integer.parseInt(tipoCierre));
			lista = nominaCtaCteAioVO.getListaNominaCtaCteAioVO();
			if(lista!=null){
				for(ListaNominaCtaCteAioVO detallenomina: lista){
					JSONObject fila = new JSONObject();
					fila.put("caratula", detallenomina.getCaratula());
					fila.put("fechacierre",  sdfcierre.format(detallenomina.getFechaCierre()));
					fila.put("monto", detallenomina.getMonto());
					fila.put("obs", detallenomina.getObs());

					detalleNominas.add(fila);
				}
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("detalleNominas", detalleNominas);
		respuesta.put("total", nominaCtaCteAioVO.getTotal());
		respuesta.put("fechaInicioCierre", nominaCtaCteAioVO.getFechaInicioCierre());
		respuesta.put("fechaFinCierre", nominaCtaCteAioVO.getFechaFinCierre());
		respuesta.put("numeroBoleta", nominaCtaCteAioVO.getNumeroBoleta());
		respuesta.put("urlBoleta", urlBoleta+nominaCtaCteAioVO.getNumeroBoleta());
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void exportarNominaExcel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONArray detalleNominas = new JSONArray();
		NominaCtaCteAioVO nominaCtaCteAioVO = null;
		ListaNominaCtaCteAioVO[] lista = null;
		SimpleDateFormat sdfcierre = new SimpleDateFormat("dd/MM/yyyy");

		String tipoCierre = request.getParameter("tipoCierre");
		String cuenta = request.getParameter("cuenta");
		String fechaStr = request.getParameter("mes");	

		fechaStr = fechaStr+"-01";

		Date fecha = null;

		if(fechaStr!="")
			try {
				fecha = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
			} catch (ParseException e1) {
				logger.error(e1.getMessage(),e1);
			}

		WsCuentaCorrienteClienteDelegate delegate = new WsCuentaCorrienteClienteDelegate();
		try {
			nominaCtaCteAioVO = delegate.obtenerCierreCtaCte(cuenta, fecha, Integer.parseInt(tipoCierre));
		} catch (NumberFormatException e1) {
			logger.error(e1.getMessage(),e1);
		} catch (GeneralException e1) {
			logger.error(e1.getMessage(),e1);
		}
		lista = nominaCtaCteAioVO.getListaNominaCtaCteAioVO();
		if(lista!=null){
			for(ListaNominaCtaCteAioVO detallenomina: lista){
				JSONObject fila = new JSONObject();
				fila.put("caratula", detallenomina.getCaratula());
				fila.put("fechacierre",  sdfcierre.format(detallenomina.getFechaCierre()));
				fila.put("monto", detallenomina.getMonto());
				fila.put("obs", detallenomina.getObs());

				detalleNominas.add(fila);
			}
		}

		ByteArrayOutputStream archivo = CaratulaServiceAction.exportNominaExcel(detalleNominas);

		ServletOutputStream out;

		String contentType = "application/ms-excel";
		String filename = "nomina_"+new Date().getTime()+".xls";

		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename="+filename);

		try {
			out = response.getOutputStream();

			out.write(archivo.toByteArray());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

	}

	private static ByteArrayOutputStream exportNominaExcel(JSONArray listaaData){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int MAX_FILAS = 65000;

		try {


			WritableWorkbook w = Workbook.createWorkbook(out);

			//NOMINA
			if(listaaData!=null && listaaData.size()>0){

				double resto = listaaData.size()%MAX_FILAS;
				int cantidadHojas = listaaData.size()/MAX_FILAS;

				if (resto!=0)
					cantidadHojas = cantidadHojas+1;

				for(int z=0;z<cantidadHojas;z++){

					WritableSheet s = w.createSheet("NOMINA"+z, 0);

					//Tamaï¿½o de celdas
					s.setColumnView(0, 10);
					s.setColumnView(1, 10);
					s.setColumnView(2, 10);
					s.setColumnView(3, 80);

					jxl.write.DateFormat customDateFormat = new jxl.write.DateFormat("dd/mm/yy hh:mm:ss");	 
					WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat); 
					dateFormat.setWrap(true);
					//				dateFormat.setBackground(Colour.GRAY_25);

					WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
					WritableCellFormat arial12format = new WritableCellFormat (arial12font); 
					arial12format.setBackground(Colour.LIGHT_GREEN);

					WritableFont subtitles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
					WritableCellFormat subtitlesformat = new WritableCellFormat (subtitles); 
					//				subtitlesformat.setBackground(Colour.GRAY_25);
					subtitlesformat.setWrap(true);

					s.addCell(new jxl.write.Label(0, 0, "Caratula",arial12format));  //Caratula
					s.addCell(new jxl.write.Label(1, 0, "Monto",arial12format));  //Monto
					s.addCell(new jxl.write.Label(2, 0, "Fecha Cierre",arial12format));  //Fecha Cierre
					s.addCell(new jxl.write.Label(3, 0, "Observacion",arial12format));  //Observacion

					int cuentaRegistros=0;
					int fila=1;

					for(int i=z*MAX_FILAS;i<listaaData.size();i++){
						if(cuentaRegistros==MAX_FILAS){
							break;
						}

						JSONObject rec = (JSONObject) listaaData.get(i);

						Long caratula = (Long) rec.get("caratula");
						Long monto = (Long) rec.get("monto");
						String fechacierre = (String) rec.get("fechacierre");
						String obs = (String) rec.get("obs");
						s.addCell(new jxl.write.Label(0, fila, caratula.toString(), subtitlesformat));  //Caratula
						s.addCell(new jxl.write.Label(1, fila, monto.toString(), subtitlesformat));  //Monto
						s.addCell(new jxl.write.Label(2, fila, fechacierre.toString(), subtitlesformat));  //Fecha Cierre
						s.addCell(new jxl.write.Label(3, fila, obs, subtitlesformat));  //Observacion

						cuentaRegistros++;
						fila++;
					}

				}
			} 	

			w.write();
			w.close();

		} catch (IOException e) {
			// Error al crear el fichero.
			e.printStackTrace();
		} catch (RowsExceededException e) {
			logger.error(e.getMessage(),e);
		} catch (WriteException e) {
			// Error al escribir el fichero.
			e.printStackTrace();
		}
		return out;	
	}

	@SuppressWarnings("unchecked")
	public void getListadoCaratulasPorSeccion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray caratulas = new JSONArray();
		Boolean status = false;
		String msg = "";
		List<CaratulaVO> listaCaratulas = null;

		try{

			String seccionStr = request.getParameter("seccion")==null?"":request.getParameter("seccion");
			String usuario = request.getUserPrincipal().getName().replaceAll("CBRS\\\\", "");

			WsCaratulaClienteDelegate wsCaratulaClienteDelegate = new WsCaratulaClienteDelegate();
			listaCaratulas = wsCaratulaClienteDelegate.obtenerCaratulasPorSeccionUsuario(seccionStr,usuario);
			if(listaCaratulas!=null){ 
				if(listaCaratulas.size()>0){
					for(CaratulaVO caratulaVO: listaCaratulas){
						JSONObject fila = new JSONObject();

						fila.put("caratula", caratulaVO.getNumeroCaratula());
						fila.put("fechacreacion",  sdf.format(caratulaVO.getFechaCreacion()));
						fila.put("fechacreacionL",  caratulaVO.getFechaCreacion().getTime());
						fila.put("estadoactual", caratulaVO.getEstadoActualCaratula().getSeccion().getDescripcion());
						fila.put("estado", caratulaVO.getEstadoActualCaratula().getDescripcionEnFlujo());
						fila.put("fechaseccion",  sdf.format(caratulaVO.getEstadoActualCaratula().getFechaMov()));
						fila.put("clienteCtaCte", caratulaVO.getCodigo());

						caratulas.add(fila);
					}
				}	
			}	

			status = true;

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacaratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void movercaratulaLiquidacion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String caratulasp = request.getParameter("caratulas");
		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		if(!StringUtils.isBlank(caratulasp)){
			JSONParser parser = new JSONParser();
			try {
				JSONArray array = (JSONArray)parser.parse(caratulasp);
				WsCaratulaClienteDelegate delegateCaratula = new WsCaratulaClienteDelegate();

				for(int i=0;i<array.size();i++){
					JSONObject jobj = (JSONObject)array.get(i);
					JSONObject caratulaJson = (JSONObject)jobj.get("id");

					Boolean seleccionado = Boolean.parseBoolean(caratulaJson.get("Selected")==null?"false":caratulaJson.get("Selected").toString());;


					if(seleccionado){

						Long caratula = (Long)caratulaJson.get("caratula");
						Long esctactep = (Long)caratulaJson.get("esCtaCte");

						EstadoCaratulaVO estadoCaratulaVO = new EstadoCaratulaVO();
						FuncionarioVO funcionarioEnviaVO = new FuncionarioVO();
						SeccionVO seccionVO = new SeccionVO();
						FuncionarioVO funcionarioResponsableVO = new FuncionarioVO();

						funcionarioEnviaVO.setRutFuncionario(rutUsuario);
						estadoCaratulaVO.setEnviadoPor(funcionarioEnviaVO);

						if(esctactep==1)
							funcionarioResponsableVO.setRutFuncionario("083367725");//pedro pablo torres (ctas ctes)
						else				
							funcionarioResponsableVO.setRutFuncionario("078437413");//manuel perez osorio

						estadoCaratulaVO.setResponsable(funcionarioResponsableVO);	

						seccionVO.setCodigo("08"); //entrega de docs
						estadoCaratulaVO.setSeccion(seccionVO);
						estadoCaratulaVO.setMaquina(CacheAIO.CACHE_CONFIG_AIO.get("SISTEMA"));

						estadoCaratulaVO.setFechaMovimiento(new Date());

						delegateCaratula.moverCaratulaSeccion(caratula, estadoCaratulaVO);

						//Eliminar de tabla temporal
						Client client = Client.create();
						String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
						String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
						String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");

						String url = "http://"+ip+":"+port+"/CaratulaRest/caratula/eliminarCaratulaPendiente/"+caratula.intValue();

						WebResource wr = client.resource(new URI(url));

						ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).delete(ClientResponse.class);
						com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

						if(statusRespuesta.getStatusCode() == 200){
						}
					}

				}

				status=true;

			}catch (GeneralException e1) {
				logger.error(e1);

				status = false;
				msg = "Se ha detectado un problema en el servidor.";	
			}catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);	

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getListadoCaratulasPendientesPorUsuario(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray caratulas = new JSONArray();
		Boolean status = false;
		String msg = "";

		try{

			String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");
			Client client = Client.create();
			String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
			String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
			String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");

			WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerCaratulasPendientesPorUsuario/"+rutUsuario));

			ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
			com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

			if(statusRespuesta.getStatusCode() == 200){
				caratulas = (JSONArray) getResponse(clientResponse);
				status = true;
			}

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacaratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void getListadoCaratulasPendientesPorSeccion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject respuesta = new JSONObject();
		JSONArray caratulas = new JSONArray();
		Boolean status = false;
		String msg = "";

		try{

			String seccionStr = request.getParameter("seccion")==null?"":request.getParameter("seccion");
			Client client = Client.create();
			String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
			String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
			String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");

			WebResource wr = client.resource(new URI("http://"+ip+":"+port+"/CaratulaRest/caratula/obtenerCaratulasPendientesPorSeccion/"+seccionStr));

			ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).get(ClientResponse.class);
			com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

			if(statusRespuesta.getStatusCode() == 200){
				caratulas = (JSONArray) getResponse(clientResponse);
				status = true;
			}

		} catch (GeneralException e1) {
			logger.error(e1);

			status = false;
			msg = "Se ha detectado un problema en el servidor.";	
		} catch (Exception e) {
			logger.error(e);

			status = false;
			msg = "Se ha detectado un problema, comunicar area soporte.";
		}

		respuesta.put("listacaratulas", caratulas);
		respuesta.put("status", status);
		respuesta.put("msg", msg);

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void eliminarCaratulaPendiente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String caratula = request.getParameter("caratula");
		String rutUsuario = (String)request.getSession().getAttribute("rutUsuario");

		JSONObject respuesta = new JSONObject();

		Boolean status = false;
		String msg = "";

		if(!StringUtils.isBlank(caratula)){
			try {
				Long caratulaL = Long.parseLong(caratula);

				//Eliminar de tabla temporal
				Client client = Client.create();
				String ARCHIVO_PARAMETROS_CARATULA = "ws_caratula.parametros";	
				String ip = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "IP_WS", "valor");
				String port = TablaValores.getValor(ARCHIVO_PARAMETROS_CARATULA, "PORT_WS", "valor");

				String url = "http://"+ip+":"+port+"/CaratulaRest/caratula/eliminarCaratulaPendiente/"+caratulaL.intValue();

				WebResource wr = client.resource(new URI(url));

				ClientResponse clientResponse = wr.type(MediaType.TEXT_HTML).delete(ClientResponse.class);
				com.sun.jersey.api.client.ClientResponse.Status statusRespuesta = clientResponse.getClientResponseStatus();

				if(statusRespuesta.getStatusCode() == 200){
					//Agregar bitacora
					try{
						CaratulasUtil caratulasUtil = new CaratulasUtil();
						caratulasUtil.agregarBitacoraCaratula(caratulaL, rutUsuario, "Liquidacion de caratula: Se elimino caratula de lista pendientes a Entrega Documentos.", BitacoraCaratulaVO.OBSERVACION_INTERNA);					
					} catch (Exception e) {
						logger.error("Error: " + e.getMessage(), e);
					}

					status=true;
				}




			} catch (Exception e) {
				logger.error(e);

				status = false;
				msg = "Se ha detectado un problema, comunicar area soporte.";
			}
		}

		respuesta.put("status", status);
		respuesta.put("msg", msg);	

		try {
			respuesta.writeJSONString(response.getWriter());
		} catch (IOException e) {
			logger.error(e);
		}
	}	

	private static Object getResponse(ClientResponse response) throws HTTPException, Exception {
		Object respuesta = null;
		if(response!=null && response.getStatus() == Status.OK.getStatusCode() ){
			if(MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(response.getType()))
				respuesta = IOUtils.toByteArray(response.getEntity(InputStream.class));			
			else if(MediaType.APPLICATION_JSON_TYPE.equals(response.getType()))
				respuesta = new JSONParser().parse(response.getEntity(String.class));
			else
				respuesta = response.getEntity(String.class);

		} else if(response!=null)
			throw new HTTPException(response.getStatus());
		else
			throw new Exception("Sin respuesta del servicio");
		return respuesta;
	}      

	private ProductoVO obtenerProductoVO(UsuarioWebVO usuarioWebVO, long tipoProductoLong,Long valorCaratula,Integer foja, Integer numero, Integer ano, String descripcionProducto, ReceptorEmailVO receptorEmailVO) {
		ProductoVO productoVO=new ProductoVO();
		productoVO.setIdReceptorBoleta(null);	
		productoVO.setNombreBoleta(usuarioWebVO.getNombres());
		productoVO.setApellidoPaternoBoleta(usuarioWebVO.getApellidoPaterno());
		productoVO.setApellidoMaternoBoleta(usuarioWebVO.getApellidoMaterno());
		productoVO.setRutBoleta(usuarioWebVO.getRut());
		productoVO.setDvBoleta(usuarioWebVO.getDv());
		productoVO.setDireccionBoleta(usuarioWebVO.getDireccion());
		productoVO.setTelefonoBoleta(usuarioWebVO.getTelefono());
		productoVO.seteMailBoleta(usuarioWebVO.getEMail());		   
		productoVO.setValorReal(valorCaratula);
		productoVO.setValorUnitario(valorCaratula);
		productoVO.setFoja(foja);
		productoVO.setNumero(numero);
		productoVO.setIdUsuarioWeb(usuarioWebVO.getIdUsuario());
		productoVO.setAno(Integer.valueOf(ano));
		TipoProductoVO tipoProducto = new TipoProductoVO();	  
		tipoProducto.setId(tipoProductoLong);
		productoVO.setTipoProducto(tipoProducto);
		CanalVO canalVO = new CanalVO();
		canalVO.setId(2);
		productoVO.setCanal(canalVO);
		productoVO.setRegistro(1);
		productoVO.setCantidad(1);
		productoVO.setDescripcionProducto(descripcionProducto);
		
		ProductoReceptorEmailVO productoReceptorEmailVO= new ProductoReceptorEmailVO();
		productoReceptorEmailVO.setIdReceptorEmail(receptorEmailVO.getId());
		productoReceptorEmailVO.seteMail(receptorEmailVO.getEMail());
		productoReceptorEmailVO.setNombreCompleto(receptorEmailVO.getNombreCompleto());
		productoReceptorEmailVO.setNombreCorto(receptorEmailVO.getNombreCorto());
		
		ProductoReceptorEmailVO[] listaProductoReceptorEmailVO =  new ProductoReceptorEmailVO[1];
		listaProductoReceptorEmailVO[0] = productoReceptorEmailVO;
		productoVO.setListaProductoReceptorEmailVO(listaProductoReceptorEmailVO);
				
		return productoVO;
	}

}
