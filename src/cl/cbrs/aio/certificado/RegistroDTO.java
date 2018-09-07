package cl.cbrs.aio.certificado;

import java.io.Serializable;


public class RegistroDTO implements Serializable{

    private Integer id;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    private String nombre;
}
