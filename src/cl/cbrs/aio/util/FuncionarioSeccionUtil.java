package cl.cbrs.aio.util;

import cl.cbrs.funcionarios.delegate.WsFuncionariosDelegate;
import cl.cbrs.funcionarios.vo.FuncionariosSeccionVO;

public class FuncionarioSeccionUtil {

	public FuncionariosSeccionVO obtenerFuncionarioVO(String nombre) throws Exception{
		FuncionariosSeccionVO funcionariosSeccionVO = new FuncionariosSeccionVO();
		
		WsFuncionariosDelegate delegateFuncionarios = new WsFuncionariosDelegate();
		funcionariosSeccionVO = delegateFuncionarios.obtenerFuncionario(nombre);
		
		return funcionariosSeccionVO;
	}
}
