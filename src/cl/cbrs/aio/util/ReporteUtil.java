package cl.cbrs.aio.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.IngresoEgresoDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.dto.estado.TareaDTO;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ReporteUtil {
	
	private static String jasperPath = TablaValores.getValor("jasper.parametros", "path" , "valor");
	private final static Logger logger= Logger.getLogger(ReporteUtil.class);
			
	public ReporteUtil(){

	}

	public static ByteArrayOutputStream export (HashMap<String, Object> map, ArrayList<TareaDTO> tareas, List<RepertorioVO> repertorioVOs, HashMap<String, String> map2, 
			ArrayList<MovimientoDTO> historial, String tipo, ArrayList<BitacoraDTO> bitacora, 
			boolean hayIngresoEgreso, ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs) throws JRException{
		
		List<JasperPrint> jasperPrintList  = new ArrayList<JasperPrint>();
		JasperReport report = null;
		jasperPath = "C:\\jboss-5.1.0.GA\\server\\default\\tablas\\reporte\\";
//		String canal = (String) map.get("canal");
//		if(canal.equalsIgnoreCase("Web"))
//			report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio_web" , "valor"));
//		else
//			report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio" , "valor"));
			report = (JasperReport) JRLoader.loadObject(jasperPath + "EstadoAIO.jasper");
				
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(tareas);
		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);
		jasperPrintList.add(jasperPrint);

		if(bitacora!=null && bitacora.size()>0){
			JasperPrint jasperPrint3 = export_bitacora (map2, bitacora);
			jasperPrintList.add(jasperPrint3);			
		}
		
		if(historial!=null && historial.size()>0){
			JasperPrint jasperPrint2 = export_historial (map2, historial);
			jasperPrintList.add(jasperPrint2);			
		}
		
		if(repertorioVOs!=null && repertorioVOs.size()>0){
			JasperPrint jasperPrint5 = export_repertorios (map2, repertorioVOs);
			jasperPrintList.add(jasperPrint5);			
		}		

		if(hayIngresoEgreso && (ingresoEgresoDTOs!=null && ingresoEgresoDTOs.size()>0)){
			JasperPrint jasperPrint4 = export_ingreso_egreso (map2, ingresoEgresoDTOs);
			jasperPrintList.add(jasperPrint4);			
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		if("pdf".equals(tipo)){
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out); 

			exporter.exportReport();			
		}else{
			JRHtmlExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			//exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,"image?image="); 
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean(false));
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out); 

			exporter.exportReport();	
		}

		return out;	
	}


	private static JasperPrint export_historial (HashMap<String, String> map, ArrayList<MovimientoDTO> historial) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(historial);

//		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_historial_aio" , "valor"));
		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + "EstadoHistorialAIO.jasper");

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}	

	private static JasperPrint export_bitacora (HashMap<String, String> map, ArrayList<BitacoraDTO> bitacora) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(bitacora);

//		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_bitacora_aio" , "valor"));
		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + "EstadoBitacoraAIO.jasper");

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
	
	private static JasperPrint export_repertorios (HashMap<String, String> map, List<RepertorioVO> bitacora) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(bitacora);

//		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_repertorio_aio" , "valor"));
		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + "EstadoRepertorioAIO.jasper");

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
	
	private static JasperPrint export_ingreso_egreso (HashMap<String, String> map, ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(ingresoEgresoDTOs);

//		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + TablaValores.getValor("jasper.parametros", "estado_ingreso_egreso_aio" , "valor"));
		JasperReport report = (JasperReport) JRLoader.loadObject(jasperPath + "EstadoIngresoEgresoAIO.jasper");

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
}