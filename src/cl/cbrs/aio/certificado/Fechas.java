package cl.cbrs.aio.certificado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import cl.cbr.util.TablaValores;


public class Fechas {

    public static Calendar obtenerHabilSiguiente(int cantidadDias,Calendar cal){
        int diasSuma =1;
        if(cantidadDias<0)
            diasSuma =-1;
        cal.add(Calendar.DATE, cantidadDias);
        System.out.println(cal.get(Calendar.DAY_OF_WEEK));
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
            return obtenerHabilSiguiente(diasSuma,cal);
        String mes = ""+(cal.get(Calendar.MONTH)+1);
        if(mes.length()<2)
            mes ="0"+mes;
        String dia=""+cal.get(Calendar.DAY_OF_MONTH);
        if(dia.length()<2)
            dia="0"+dia;
        String fecha=cal.get(Calendar.YEAR)+"/"+mes+"/"+dia;
        if(esFeriado(fecha))
            return obtenerHabilSiguiente(diasSuma,cal); 
        System.out.println("dias:"+cantidadDias+"::"+cal.get(Calendar.DAY_OF_WEEK) +","+ cal.get(Calendar.DATE) +"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR));
        return cal;
    } 
    
    private static boolean esFeriado(String fecha){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            System.out.println("fecha:"+fecha);
            conn=getConnection();
            pstmt = conn.prepareStatement("SELECT ID_F FROM TAB_FERIADOS WHERE CONVERT(VARCHAR(10),FERIADO,111)=?");
            pstmt.setString(1,fecha);
            rs = pstmt.executeQuery();
            if(rs.next())
                return true;
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{if(conn!=null)conn.close();}catch(SQLException e){}
            try{if(pstmt!=null)pstmt.close();}catch(SQLException e){}
            try{if(rs!=null)rs.close();}catch(SQLException e){}
        }
        return false;
    }
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        String REGISTRO_URL="jdbc:jtds:sqlserver://"+TablaValores.getValor("conexiones.properties", "REGISTRO", "valor")+":1433;databaseName=REGISTRO";
        String REGISTRO_USERNAME = TablaValores.getValor("conexiones.properties", "REGISTRO_USUARIO", "username");
        String REGISTRO_PASSWORD = TablaValores.getValor("conexiones.properties", "REGISTRO_USUARIO", "password");
        
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        Connection conexion = DriverManager.getConnection(REGISTRO_URL, REGISTRO_USERNAME, REGISTRO_PASSWORD);
        return conexion;
    }
    
  /*  public static void main(String[] args){
        System.setProperty("jboss.server.home.dir", "C:\\jboss-5.1.0.GA\\server\\default");
        Calendar cal = Calendar.getInstance();
        cal = obtenerHabilSiguiente(-1,cal);
        
        System.out.println(cal.toString());
        
        
        
    }
    */
 }
