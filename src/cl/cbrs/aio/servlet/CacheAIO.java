package cl.cbrs.aio.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.ReglaReingresoDTO;

public class CacheAIO extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public static ArrayList<ReglaReingresoDTO> CACHE_REGLAS_REINGRESO = new ArrayList<ReglaReingresoDTO>();
	public static HashMap<String, String> CACHE_CONFIG_AIO = new HashMap<String, String>();

	/**
	 * Constructor of the object.
	 */
	public CacheAIO() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		System.out.println("recargar config");
		init();
		
		out.println("<h3>Configuracion AIO</h3>");
		for (String key: CACHE_CONFIG_AIO.keySet()){
			out.println(key + ": [" + CACHE_CONFIG_AIO.get(key)+"] <br>");
		}
		
		out.println("<br>");
		
		out.println("<h3>Reglas reingreso</h3>");
		int i=1;
		for (ReglaReingresoDTO key: CACHE_REGLAS_REINGRESO){			
			out.println(i++ + ": [" + ToStringBuilder.reflectionToString(key, ToStringStyle.SHORT_PREFIX_STYLE)+"] <br>");
		}	
		
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		
		cargaReglasReingreso();
		
		System.out.println("Cargando configuracion AIO");
		try{
			//Valores por defecto
			CACHE_CONFIG_AIO.put("SISTEMA", "AIO");
			CACHE_CONFIG_AIO.put("ANO_ARCHIVO_NACIONAL", "1934");
			
			String sistema = TablaValores.getValor("aio.parametros", "SISTEMA",  "valor" );
			String anoArchivoNacional = TablaValores.getValor("aio.parametros", "ANO_ARCHIVO_NACIONAL",  "valor" );
			String anoDigitalPropiedades = TablaValores.getValor("aio.parametros", "ANO_DIGITAL_PROPIEDADES",  "valor" );
			String anoDigitalHipotecas = TablaValores.getValor("aio.parametros", "ANO_DIGITAL_HIPOTECAS",  "valor" );
			String anoDigitalProhibiciones = TablaValores.getValor("aio.parametros", "ANO_DIGITAL_PROHIBICIONES",  "valor" );
			String fojasDigitalProhibiciones = TablaValores.getValor("aio.parametros", "FOJAS_DIGITAL_PROHIBICIONES",  "valor" );
			
			if(sistema!=null && !"".equals(sistema))
				CACHE_CONFIG_AIO.put("SISTEMA", sistema);
			if(anoArchivoNacional!=null && !"".equals(anoArchivoNacional))
				CACHE_CONFIG_AIO.put("ANO_ARCHIVO_NACIONAL", anoArchivoNacional);
			if(anoDigitalPropiedades!=null && !"".equals(anoDigitalPropiedades))
				CACHE_CONFIG_AIO.put("ANO_DIGITAL_PROPIEDADES", anoDigitalPropiedades);
			if(anoDigitalHipotecas!=null && !"".equals(anoDigitalHipotecas))
				CACHE_CONFIG_AIO.put("ANO_DIGITAL_HIPOTECAS", anoDigitalHipotecas);
			if(anoDigitalProhibiciones!=null && !"".equals(anoDigitalProhibiciones))
				CACHE_CONFIG_AIO.put("ANO_DIGITAL_PROHIBICIONES", anoDigitalProhibiciones);
			if(fojasDigitalProhibiciones!=null && !"".equals(fojasDigitalProhibiciones))
				CACHE_CONFIG_AIO.put("FOJAS_DIGITAL_PROHIBICIONES", fojasDigitalProhibiciones);				
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Imprimir config
		System.out.println("Configuracion AIO");
		for (String key: CACHE_CONFIG_AIO.keySet()){
			System.out.println(key + ": [" + CACHE_CONFIG_AIO.get(key)+"]");
		}
			
		
	}

	public static void cargaReglasReingreso() {
		System.out.println("Cargando reglas de reingreso...");
		try {
				int i=1;
				CACHE_REGLAS_REINGRESO.clear();
				while( TablaValores.getValor("reingreso.parametros", "R_"+i,  "tipo_formulario" )!=null ){
					System.out.println("Cargando regla de reingreso " + i);					
					String seccion = TablaValores.getValor("reingreso.parametros", "R_"+i,  "seccion" );
					String idEstado = TablaValores.getValor("reingreso.parametros", "R_"+i,  "estado" );
					String idUsuario = TablaValores.getValor("reingreso.parametros", "R_"+i,  "usuario" );
					String rutFuncionario = TablaValores.getValor("reingreso.parametros", "R_"+i,  "rut_funcionario" );
					String registro = TablaValores.getValor("reingreso.parametros", "R_"+i,  "registro" );
					String tipoFormulario = TablaValores.getValor("reingreso.parametros", "R_"+i,  "tipo_formulario" );
					ReglaReingresoDTO reglaReingresoDTO = new ReglaReingresoDTO();
					reglaReingresoDTO.setCodSeccion(seccion);
					reglaReingresoDTO.setIdEstado(!"".equals(idEstado)?new Integer(idEstado):null);
					reglaReingresoDTO.setIdTipoFormulario(new Integer(tipoFormulario));
					reglaReingresoDTO.setIdUsuarioComercio(!"".equals(idUsuario)?new Integer(idUsuario):null);
					reglaReingresoDTO.setRegistro(registro);
					reglaReingresoDTO.setRutFuncionario(rutFuncionario);
						
					CACHE_REGLAS_REINGRESO.add(reglaReingresoDTO);
					i++;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
