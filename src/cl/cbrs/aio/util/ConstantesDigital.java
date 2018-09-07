package cl.cbrs.aio.util;

import cl.cbr.util.TablaValores;

public class ConstantesDigital{

	public static String PARAMETROS_LIBROS = "digital_libros.parametros";


    public static String AMBIENTE;



    public static String getParametro(String parametro){
        if (AMBIENTE !=null && AMBIENTE.equals("LINUX")){
            String valor = TablaValores.getValor(PARAMETROS_LIBROS, parametro, "valor").replaceAll("z:", "/mnt/z");
            valor = valor.replaceAll("y:", "/mnt/y");
            valor = valor.replaceAll("x:", "/mnt/x");
            valor = valor.replaceAll("\\\\", "/");
            return valor;
        }else{
            return TablaValores.getValor(PARAMETROS_LIBROS, parametro, "valor");
        }
    }
    
    public static String getParametro(String parametro,String clave){
        if (AMBIENTE !=null && AMBIENTE.equals("LINUX")){
            String valor = TablaValores.getValor(PARAMETROS_LIBROS, parametro, clave).replaceAll("z:", "/mnt/z");
            valor = valor.replaceAll("y:", "/mnt/y");
            valor = valor.replaceAll("x:", "/mnt/x");
            valor = valor.replaceAll("\\\\", "/");
            return valor;
        } else {
            return TablaValores.getValor(PARAMETROS_LIBROS, parametro, clave);
        }
    }

    
    
}
