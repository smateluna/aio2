package cl.cbrs.aio.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
	
	public static HashMap<Integer, ReglaReingresoDTO> CACHE_REGLAS_REINGRESO = new HashMap<Integer, ReglaReingresoDTO>();
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
		for (Integer key: CACHE_REGLAS_REINGRESO.keySet()){			
			out.println(key + ": [" + ToStringBuilder.reflectionToString(CACHE_REGLAS_REINGRESO.get(key), ToStringStyle.SHORT_PREFIX_STYLE)+"] <br>");
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
		
		System.out.println("Cargando reglas de reingreso...");
		try {
				for( int i=1; i<=10; i++){
					System.out.println("Cargando regla de reingreso " + i +" de 10");					
					String seccion = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+i,  "seccion" );
					String idEstado = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+i,  "estado" );
					String idUsuario = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+i,  "usuario" );
					String rutFuncionario = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+i,  "rut_funcionario" );
					String registro = TablaValores.getValor("reingreso.parametros", "TIPO_FORMULARIO_"+i,  "registro" );
					ReglaReingresoDTO reglaReingresoDTO = new ReglaReingresoDTO();
					reglaReingresoDTO.setCodSeccion(seccion);
					reglaReingresoDTO.setIdEstado(!"".equals(idEstado)?new Integer(idEstado):null);
					reglaReingresoDTO.setIdTipoFormulario(i);
					reglaReingresoDTO.setIdUsuarioComercio(!"".equals(idUsuario)?new Integer(idUsuario):null);
					reglaReingresoDTO.setRegistro(registro);
					reglaReingresoDTO.setRutFuncionario(rutFuncionario);
						
					CACHE_REGLAS_REINGRESO.put(i, reglaReingresoDTO);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Cargando configuracion AIO");
		try{
			//Valores por defecto
			CACHE_CONFIG_AIO.put("SISTEMA", "AIO");
			CACHE_CONFIG_AIO.put("ANO_ARCHIVO_NACIONAL", "1934");
			
			String sistema = TablaValores.getValor("aio.parametros", "SISTEMA",  "valor" );
			String anoArchivoNacional = TablaValores.getValor("aio.parametros", "ANO_ARCHIVO_NACIONAL",  "valor" );
			
			if(sistema!=null && !"".equals(sistema))
				CACHE_CONFIG_AIO.put("SISTEMA", sistema);
			if(anoArchivoNacional!=null && !"".equals(anoArchivoNacional))
				CACHE_CONFIG_AIO.put("ANO_ARCHIVO_NACIONAL", anoArchivoNacional);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Imprimir config
		System.out.println("Configuracion AIO");
		for (String key: CACHE_CONFIG_AIO.keySet()){
			System.out.println(key + ": [" + CACHE_CONFIG_AIO.get(key)+"]");
		}
			
		
	}

}
