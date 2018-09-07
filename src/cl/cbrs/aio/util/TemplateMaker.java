package cl.cbrs.aio.util;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import cl.cbrs.aio.dto.estado.BitacoraDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class TemplateMaker {
	private static Logger logger = Logger.getLogger(TemplateMaker.class);
	
	private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
	
	static{
		// 1. Configure FreeMarker
		//
		// You should do this ONLY ONCE, when your application starts,
		// then reuse the same Configuration object elsewhere.


		// Where do we load the templates from:
		cfg.setClassForTemplateLoading(TemplateMaker.class, "templates");

		// Some other recommended settings:
		cfg.setIncompatibleImprovements(new Version(2, 3, 23));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.getDefault());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}
	
	
	public static String getNotificacion(BitacoraDTO bitacoraDTO, Long caratula) throws Exception{
		String transformedTemplate = "";
		
		
		Map<String, Object> input = new HashMap<String, Object>();

		input.put("bitacoraDTO", bitacoraDTO);
		input.put("caratula", caratula.toString());
		input.put("fecha", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
		
		Template template = cfg.getTemplate("base.html");
		
		Writer out = new StringWriter();
		template.process(input, out);

		transformedTemplate = out.toString();		
		
		//transformedTemplate = StringUtils.stripAccents(transformedTemplate);
		
		logger.debug(transformedTemplate);
		
		return transformedTemplate;
	}
} 

