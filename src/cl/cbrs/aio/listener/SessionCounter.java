package cl.cbrs.aio.listener;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cl.cbrs.aio.dto.UsuarioAIODTO;

public class SessionCounter implements HttpSessionListener, Serializable {
	
    private static final long serialVersionUID = 5510264175093946205L;
    public static HashMap<String, UsuarioAIODTO> USUARIOS = new HashMap<String, UsuarioAIODTO>();

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();       
        UsuarioAIODTO usuarioAIO = new UsuarioAIODTO();
        usuarioAIO.setHttpSession(session);
        USUARIOS.put(session.getId(), usuarioAIO);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        USUARIOS.remove(session.getId());
    }

}
