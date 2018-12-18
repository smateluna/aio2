package cl.cbrs.aio.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import cl.cbr.common.exception.BussinesException;
import cl.cbr.util.StringUtil;
import cl.cbr.util.TextosUtil;
import cl.cbrs.aio.certificado.HeaderNotasHelper;
import cl.cbrs.inscripciondigital.vo.AnotacionVO;
import cl.cbrs.inscripciondigital.vo.InscripcionDigitalVO;

public class NotaElectronicaUtil {
	
	private static final Logger logger = Logger.getLogger(NotaElectronicaUtil.class);
	
	public InputStream getNotasInscripcion(List<AnotacionVO> listaNotas,Long foja,String numero,Long anno) throws BussinesException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Document document = new Document(new Rectangle(20, 50, 645, 910), 120, 120, 50, 50);

			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			writer.setPageEvent(new HeaderNotasHelper());

			logger.debug("verificando las notas que corresponden a la inscripcion");
			
			List<String> listaNotasRepertorio = new ArrayList<String>();

			document.open();
			int contadorDeNotas = 0;

			if(listaNotas!=null){

				document.add(getCellfna("FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]"));				
					
				for(AnotacionVO anotacion : listaNotas){

					//Anotacion internas o eliminadas no haga nada
					if(3 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion() || 7 == anotacion.getEstadoAnotacionVo().getIdEstado()){
						continue;
					}

					String inscrito=""; 

					String texto="";
					if(anotacion.getCaratula()!=null)
						texto += "Carátula: "+anotacion.getCaratula()+"<br>";
					texto+=anotacion.getTexto();

					if (anotacion.getTexto()!=null){
						//	            			txt=anotacion.getTexto().replaceAll("\\n", "");
						texto=StringUtil.reemplazaTodo(texto, "PROHIBICION: A", "");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal : A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal: A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "EMBARGO: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "MEDIDA PRECAUTORIA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PRECAUTORIA:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "TOTAL ", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA DE VENTA: A", "");
					}

					String acto=anotacion.getActo().trim();
					if (acto!=null){
						acto=StringUtil.reemplazaTodo(acto, "Transmitido:", "Transmisión");
						acto=StringUtil.reemplazaTodo(acto, "Transferencia:", "Transferencia");
						acto=StringUtil.reemplazaTodo(acto, "REPERTORIO", "Repertorio");
						acto=StringUtil.reemplazaTodo(acto, "Adjudicado", "Adjudicación");
					}

					if(1 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if(null==anotacion.getInscripcionDigitalByIdInscripcionOrigenVo()){
							if(!acto.equalsIgnoreCase("Repertorio"))
								inscrito = "Inscrito a foja [] Número [] del Año [] <br>";

							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}

							if(!acto.equalsIgnoreCase("Repertorio"))	
								inscrito=inscrito+"Santiago, <br>";

						}else{
							InscripcionDigitalVO inscripcionOrigen = anotacion.getInscripcionDigitalByIdInscripcionOrigenVo();
							inscrito = "Inscrito a foja "+inscripcionOrigen.getFoja()+" Número "+inscripcionOrigen.getNumero()+" del Año "+inscripcionOrigen.getAno()+" <br>";

							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}

							inscrito=inscrito+"Santiago, "+TextosUtil.fechaEnPalabras(inscripcionOrigen.getFechaFolio(),false,false,false)+".<br>";
						}	
					}
					else if(2 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if((2 == anotacion.getEstadoAnotacionVo().getIdEstado() || 4 == anotacion.getEstadoAnotacionVo().getIdEstado() || 3 == anotacion.getEstadoAnotacionVo().getIdEstado()) && anotacion.getNombreUsuarioFirmador()!=null && StringUtils.isNotBlank(anotacion.getNombreUsuarioFirmador().trim())){							
							texto = texto.trim() + " " + anotacion.getNombreUsuarioFirmador().trim()+".";
						}
					}

					if(!acto.equalsIgnoreCase("Repertorio") && (null!=anotacion.getRepertorio() && null!=anotacion.getCaratula())){
						String anoRepertorio = "";

						if(null!=anotacion.getFechaRepertorio()){
							logger.debug("tiene guardada fecha repertorio");
							Calendar cal = Calendar.getInstance();
						    cal.setTime(anotacion.getFechaRepertorio());
						    int year = cal.get(Calendar.YEAR);
							anoRepertorio="-"+year;
						}
							
//						texto = "Repertorio: "+anotacion.getRepertorio()+anoRepertorio+", Caratula: "+anotacion.getCaratula()+"<br>"+texto; 
						texto = texto.replaceFirst("Carátula: "+anotacion.getCaratula()+"<br>", "");
						texto = "Carátula: "+anotacion.getCaratula()+", Repertorio: "+anotacion.getRepertorio()+anoRepertorio+(texto.equals("")?"":"<br>"+texto);
					}

					if(!acto.equalsIgnoreCase("Repertorio"))
						document.add(getCell("<b>"+acto+".- </b>" + texto + "<br>" + inscrito));
					else{
						if(9 != anotacion.getEstadoAnotacionVo().getIdEstado() && 8 != anotacion.getEstadoAnotacionVo().getIdEstado())
							listaNotasRepertorio.add("<b>"+acto+".- </b>" + texto);
					}

					contadorDeNotas++;

				}
				
				//luego agrego las notas repertorio al final del certificado
				if(listaNotasRepertorio.size()>0){
					document.add(getCell("<center>----------------------Fin Copia----------------------</center>"));
					String textoPluralRepertorio = "existen repertorios vigentes";
					if(listaNotasRepertorio.size()==1)
						textoPluralRepertorio = "existe repertorio vigente";
					
					document.add(getCell("Se deja constancia que para esta inscripción <b>FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]</b> "+textoPluralRepertorio+":<br>"));
					for(String notaRepertorio:listaNotasRepertorio){
						document.add(getCell(notaRepertorio));
					}
				}
			}

			logger.debug("Contador de notas="+contadorDeNotas);
			document.close();

			if (contadorDeNotas == 0) {
				return null;
			}
			outputStream.flush();
			InputStream salida = new ByteArrayInputStream(outputStream.toByteArray());
			return salida;
		}
		catch (Exception e) {
			logger.debug(e.getMessage(),e);
			return null;
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				logger.debug(e.getMessage(),e);
			}
		}
	}
	
	public InputStream getNotasInscripcionH(List<cl.cbrs.inscripciondigitalh.vo.AnotacionVO> listaNotas,Long foja,String numero,Long anno) throws BussinesException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Document document = new Document(new Rectangle(20, 50, 645, 910), 120, 120, 50, 50);

			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			writer.setPageEvent(new HeaderNotasHelper());			
			List<String> listaNotasRepertorio = new ArrayList<String>();

			document.open();
			int contadorDeNotas = 0;

			if(listaNotas!=null){

				document.add(getCellfna("FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]"));

				for(cl.cbrs.inscripciondigitalh.vo.AnotacionVO anotacion : listaNotas){

					//Anotacion internas o eliminadas no haga nada
					if(3 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion() || 7 == anotacion.getEstadoAnotacionVo().getIdEstado()){
						continue;
					}

					String inscrito=""; 
					String texto="";
					if(anotacion.getCaratula()!=null)
						texto += "Carátula: "+anotacion.getCaratula()+"<br>";
					texto+=anotacion.getTexto();

					if (anotacion.getTexto()!=null){
						texto=StringUtil.reemplazaTodo(texto, "PROHIBICION: A", "");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal : A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal: A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "EMBARGO: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "MEDIDA PRECAUTORIA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PRECAUTORIA:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "TOTAL ", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA DE VENTA: A", "");
					}

					String acto=anotacion.getActo().trim();
					if (acto!=null){
						acto=StringUtil.reemplazaTodo(acto, "Transmitido:", "Transmisión");
						acto=StringUtil.reemplazaTodo(acto, "Transferencia:", "Transferencia");
						acto=StringUtil.reemplazaTodo(acto, "REPERTORIO", "Repertorio");
						acto=StringUtil.reemplazaTodo(acto, "Adjudicado", "Adjudicación");
					}

					if(1 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if(null==anotacion.getInscripcionDigitalByIdInscripcionOrigenVo()){
							if(!acto.equalsIgnoreCase("Repertorio"))
								inscrito = "Inscrito a foja [] Número [] del Año [] <br>";
							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}
							if(!acto.equalsIgnoreCase("Repertorio"))	
								inscrito=inscrito+"Santiago, <br>";

						}else{
							cl.cbrs.inscripciondigitalh.vo.InscripcionDigitalVO inscripcionOrigen = anotacion.getInscripcionDigitalByIdInscripcionOrigenVo();
							inscrito = "Inscrito a foja "+inscripcionOrigen.getFoja()+" Número "+inscripcionOrigen.getNumero()+" del Año "+inscripcionOrigen.getAno()+" <br>";

							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}
							inscrito=inscrito+"Santiago, "+TextosUtil.fechaEnPalabras(inscripcionOrigen.getFechaFolio(),false,false,false)+".<br>";
						}	
					}
					else if(2 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if((2 == anotacion.getEstadoAnotacionVo().getIdEstado() || 4 == anotacion.getEstadoAnotacionVo().getIdEstado() || 3 == anotacion.getEstadoAnotacionVo().getIdEstado()) && anotacion.getNombreUsuarioFirmador()!=null && StringUtils.isNotBlank(anotacion.getNombreUsuarioFirmador().trim()))
							texto = texto.trim() + " " + anotacion.getNombreUsuarioFirmador().trim()+".";
					}

					if(!acto.equalsIgnoreCase("Repertorio") && (null!=anotacion.getRepertorio() && null!=anotacion.getCaratula())){
						String anoRepertorio = "";
//						if(null!=anotacion.getFechaRepertorio()){
//							logger.debug("tiene guardada fecha repertorio");
//							Calendar cal = Calendar.getInstance();
//						    cal.setTime(anotacion.getFechaRepertorio());
//						    int year = cal.get(Calendar.YEAR);
//							anoRepertorio="-"+year;
//						}							
//						texto = "Repertorio: "+anotacion.getRepertorio()+anoRepertorio+", Caratula: "+anotacion.getCaratula()+"<br>"+texto;
						texto=texto.replaceFirst("Carátula: "+anotacion.getCaratula()+"<br>", "");
						texto = "Carátula: "+anotacion.getCaratula()+", Repertorio: "+anotacion.getRepertorio()+anoRepertorio+(texto.equals("")?"":"<br>"+texto);
					}

					if(!acto.equalsIgnoreCase("Repertorio"))
						document.add(getCell("<b>"+acto+".- </b>" + texto + "<br>" + inscrito));
					else{
						if(9 != anotacion.getEstadoAnotacionVo().getIdEstado() && 8 != anotacion.getEstadoAnotacionVo().getIdEstado())
							listaNotasRepertorio.add("<b>"+acto+".- </b>" + texto);
					}

					contadorDeNotas++;

				}
				
				//luego agrego las notas repertorio al final del certificado
				if(listaNotasRepertorio.size()>0){
					document.add(getCell("<center>----------------------Fin Copia----------------------</center>"));
					String textoPluralRepertorio = "existen repertorios vigentes";
					if(listaNotasRepertorio.size()==1)
						textoPluralRepertorio = "existe repertorio vigente";
					
					document.add(getCell("Se deja constancia que para esta inscripción <b>FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]</b> "+textoPluralRepertorio+":<br>"));
					for(String notaRepertorio:listaNotasRepertorio){
						document.add(getCell(notaRepertorio));
					}
				}
			}
			document.close();

			if (contadorDeNotas == 0) {
				return null;
			}
			outputStream.flush();
			InputStream salida = new ByteArrayInputStream(outputStream.toByteArray());
			return salida;
		}
		catch (Exception e) {
			logger.debug(e.getMessage(),e);
			return null;
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				logger.debug(e.getMessage(),e);
			}
		}
	}	
	
	public InputStream getNotasInscripcionPH(List<cl.cbrs.inscripciondigitalph.vo.AnotacionVO> listaNotas,Long foja,String numero,Long anno) throws BussinesException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Document document = new Document(new Rectangle(20, 50, 645, 910), 120, 120, 50, 50);

			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			writer.setPageEvent(new HeaderNotasHelper());			
			List<String> listaNotasRepertorio = new ArrayList<String>();

			document.open();
			int contadorDeNotas = 0;

			if(listaNotas!=null){

				document.add(getCellfna("FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]"));

				for(cl.cbrs.inscripciondigitalph.vo.AnotacionVO anotacion : listaNotas){

					//Anotacion internas o eliminadas no haga nada
					if(3 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion() || 7 == anotacion.getEstadoAnotacionVo().getIdEstado()){
						continue;
					}

					String inscrito=""; 
					String texto="";
					if(anotacion.getCaratula()!=null)
						texto += "Carátula: "+anotacion.getCaratula()+"<br>";
					texto+=anotacion.getTexto();

					if (anotacion.getTexto()!=null){
						texto=StringUtil.reemplazaTodo(texto, "PROHIBICION: A", "");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal : A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "Prohibición Legal: A", " Legal\n");
						texto=StringUtil.reemplazaTodo(texto, "EMBARGO: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "MEDIDA PRECAUTORIA: A", "");
						texto=StringUtil.reemplazaTodo(texto, "PRECAUTORIA:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "TOTAL ", "");
						texto=StringUtil.reemplazaTodo(texto, "Transferencia:", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:TOTAL", "");
						texto=StringUtil.reemplazaTodo(texto, "Transmitido:", "");
						texto=StringUtil.reemplazaTodo(texto, "PROMESA DE VENTA: A", "");
					}

					String acto=anotacion.getActo().trim();
					if (acto!=null){
						acto=StringUtil.reemplazaTodo(acto, "Transmitido:", "Transmisión");
						acto=StringUtil.reemplazaTodo(acto, "Transferencia:", "Transferencia");
						acto=StringUtil.reemplazaTodo(acto, "REPERTORIO", "Repertorio");
						acto=StringUtil.reemplazaTodo(acto, "Adjudicado", "Adjudicación");
					}

					if(1 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if(null==anotacion.getInscripcionDigitalByIdInscripcionOrigenVo()){
							if(!acto.equalsIgnoreCase("Repertorio"))
								inscrito = "Inscrito a foja [] Número [] del Año [] <br>";
							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}
							if(!acto.equalsIgnoreCase("Repertorio"))	
								inscrito=inscrito+"Santiago, <br>";

						}else{
							cl.cbrs.inscripciondigitalph.vo.InscripcionDigitalVO inscripcionOrigen = anotacion.getInscripcionDigitalByIdInscripcionOrigenVo();
							inscrito = "Inscrito a foja "+inscripcionOrigen.getFoja()+" Número "+inscripcionOrigen.getNumero()+" del Año "+inscripcionOrigen.getAno()+" <br>";

							if((anotacion.getEstadoAnotacionVo().getIdEstado()==1 || anotacion.getEstadoAnotacionVo().getIdEstado()==10) && (acto.equalsIgnoreCase("Transferencia") || acto.contains("Transferido"))){
								if(null!=anotacion.getRepertorio())	
									inscrito=inscrito+"Repertorio: "+anotacion.getRepertorio()+"<br>";
							}
							inscrito=inscrito+"Santiago, "+TextosUtil.fechaEnPalabras(inscripcionOrigen.getFechaFolio(),false,false,false)+".<br>";
						}	
					}
					else if(2 == anotacion.getTipoAnotacionVo().getIdTipoAnotacion()){
						if((2 == anotacion.getEstadoAnotacionVo().getIdEstado() || 4 == anotacion.getEstadoAnotacionVo().getIdEstado() || 3 == anotacion.getEstadoAnotacionVo().getIdEstado()) && anotacion.getNombreUsuarioFirmador()!=null && StringUtils.isNotBlank(anotacion.getNombreUsuarioFirmador().trim()))
							texto = texto.trim() + " " + anotacion.getNombreUsuarioFirmador().trim()+".";
					}

					if(!acto.equalsIgnoreCase("Repertorio") && (null!=anotacion.getRepertorio() && null!=anotacion.getCaratula())){
						String anoRepertorio = "";
//						if(null!=anotacion.getFechaRepertorio()){
//							logger.debug("tiene guardada fecha repertorio");
//							Calendar cal = Calendar.getInstance();
//						    cal.setTime(anotacion.getFechaRepertorio());
//						    int year = cal.get(Calendar.YEAR);
//							anoRepertorio="-"+year;
//						}							
//						texto = "Repertorio: "+anotacion.getRepertorio()+anoRepertorio+", Caratula: "+anotacion.getCaratula()+"<br>"+texto;
						texto=texto.replaceFirst("Carátula: "+anotacion.getCaratula()+"<br>", "");
						texto = "Carátula: "+anotacion.getCaratula()+", Repertorio: "+anotacion.getRepertorio()+anoRepertorio+(texto.equals("")?"":"<br>"+texto);
					}

					if(!acto.equalsIgnoreCase("Repertorio"))
						document.add(getCell("<b>"+acto+".- </b>" + texto + "<br>" + inscrito));
					else{
						if(9 != anotacion.getEstadoAnotacionVo().getIdEstado() && 8 != anotacion.getEstadoAnotacionVo().getIdEstado())
							listaNotasRepertorio.add("<b>"+acto+".- </b>" + texto);
					}

					contadorDeNotas++;

				}
				
				//luego agrego las notas repertorio al final del certificado
				if(listaNotasRepertorio.size()>0){
					document.add(getCell("<center>----------------------Fin Copia----------------------</center>"));
					String textoPluralRepertorio = "existen repertorios vigentes";
					if(listaNotasRepertorio.size()==1)
						textoPluralRepertorio = "existe repertorio vigente";
					
					document.add(getCell("Se deja constancia que para esta inscripción <b>FOJA ["+foja+"] NUMERO ["+ numero +"] Año ["+anno+"]</b> "+textoPluralRepertorio+":<br>"));
					for(String notaRepertorio:listaNotasRepertorio){
						document.add(getCell(notaRepertorio));
					}
				}
			}
			document.close();

			if (contadorDeNotas == 0) {
				return null;
			}
			outputStream.flush();
			InputStream salida = new ByteArrayInputStream(outputStream.toByteArray());
			return salida;
		}
		catch (Exception e) {
			logger.debug(e.getMessage(),e);
			return null;
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				logger.debug(e.getMessage(),e);
			}
		}
	}		
	
	private static Paragraph getCell(String text) throws Exception {
		java.io.Reader notesReader = null;
		Font font = new Font(Font.COURIER, 10);
		Paragraph para = new Paragraph(font.leading(2.0f), "", font);
		ArrayList list = null;
		try {
			notesReader = new StringReader(text);
			list = HTMLWorker.parseToList(notesReader, null);
		} catch (Exception parseExpection) {
			//logger.error(ErroresUtil.extraeStackTrace(parseExpection));
		}
		for (int i = 0; i < list.size(); i++) {
			para.add((Element) list.get(i));
		}
		para.setAlignment(Paragraph.ALIGN_JUSTIFIED);
		return para;
	}

	private static Paragraph getCellfna(String text) throws Exception {
		Font font = new Font(Font.COURIER, 12, Font.BOLD);
		Paragraph para= new Paragraph(text,font);
		para.setAlignment(Paragraph.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell(para);
		cell.setBorder(PdfCell.NO_BORDER);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setPaddingBottom(10);
		return para;
	}
	
}
