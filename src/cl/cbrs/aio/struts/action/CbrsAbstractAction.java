package cl.cbrs.aio.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.ThreadContext;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.keycloak.KeycloakSecurityContext;

/**
 * 
 * @author dcasanueva
 *
 */
//TODO dcasanueva: mover esta clase a proyecto comun para web
public abstract class CbrsAbstractAction extends DispatchAction{	
	
	/**
	 * Subclases deben implementar este metodo, se llama cuando parametro de request especificado para metodo es nulo. 
	 * Enviar a pagina de error o controlar.
	 */
    public abstract ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response);

    /**
     * Sobreescribe dispatchMethod para capturar NoSuchMethodException, retornando unspecified
     */
    @Override
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		try{
			//request.setCharacterEncoding("UTF-8"); 
			KeycloakSecurityContext context = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
			//String usuario = request.getRemoteUser()!=null?request.getRemoteUser():"";
			String usuario =context.getIdToken().getPreferredUsername();			usuario = usuario.replaceAll("CBRS\\\\", "");
			ThreadContext.put("extra_key", usuario);
			
			return super.dispatchMethod(mapping, form, request, response, name);	
		} catch (NoSuchMethodException exception){
			return this.unspecified(mapping, form, request, response);
		}
	} 
}