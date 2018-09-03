package cl.cbrs.aio.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import cl.cbr.util.TablaValores;
import cl.cbrs.aio.dto.estado.BitacoraDTO;
import cl.cbrs.aio.dto.estado.IngresoEgresoDTO;
import cl.cbrs.aio.dto.estado.MovimientoDTO;
import cl.cbrs.aio.dto.estado.TareaDTO;
import cl.cbrs.repertorio.flujo.vo.RepertorioVO;

public class ReporteUtil {
	
	private final static String jasperPath = TablaValores.getValor("jasper.parametros", "path" , "valor");

	public ReporteUtil(){

	}

	public static ByteArrayOutputStream export (HashMap<String, Object> map, ArrayList<TareaDTO> tareas, List<RepertorioVO> repertorioVOs, HashMap<String, String> map2, 
			ArrayList<MovimientoDTO> historial, String tipo, ArrayList<BitacoraDTO> bitacora, 
			boolean hayIngresoEgreso, ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs) throws JRException{
		
		List<JasperPrint> jasperPrintList  = new ArrayList<JasperPrint>();
		JasperDesign jd = null;
		
		String canal = (String) map.get("canal");
		if(canal.equalsIgnoreCase("Web"))
			jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio_web" , "valor"));
		else
			jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio" , "valor"));
		System.out.println(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio_web" , "valor"));
		System.out.println(jasperPath + TablaValores.getValor("jasper.parametros", "estado_aio" , "valor"));
		JasperReport report = JasperCompileManager.compileReport(jd);
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(tareas);
		System.out.println(jd.getName());
		System.out.println(map.toString());
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

		JasperDesign jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_historial_aio" , "valor"));

		JasperReport report = JasperCompileManager.compileReport(jd);

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}	

	private static JasperPrint export_bitacora (HashMap<String, String> map, ArrayList<BitacoraDTO> bitacora) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(bitacora);

		JasperDesign jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_bitacora_aio" , "valor"));

		JasperReport report = JasperCompileManager.compileReport(jd);

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
	
	private static JasperPrint export_repertorios (HashMap<String, String> map, List<RepertorioVO> bitacora) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(bitacora);

		JasperDesign jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_repertorio_aio" , "valor"));

		JasperReport report = JasperCompileManager.compileReport(jd);

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
	
	private static JasperPrint export_ingreso_egreso (HashMap<String, String> map, ArrayList<IngresoEgresoDTO> ingresoEgresoDTOs) throws JRException{
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource(ingresoEgresoDTOs);

		JasperDesign jd = JRXmlLoader.load(jasperPath + TablaValores.getValor("jasper.parametros", "estado_ingreso_egreso_aio" , "valor"));

		JasperReport report = JasperCompileManager.compileReport(jd);

		JasperPrint jasperPrint = JasperFillManager.fillReport(report,map, ds);

		return jasperPrint;	
	}
}