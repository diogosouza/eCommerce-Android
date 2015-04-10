package br.edu.ecommerce.bo;

import android.content.Context;
import br.edu.ecommerce.R;
import br.edu.ecommerce.custom.model.Usuario;
import br.edu.ecommerce.dominio.ValidacaoLogin;
import br.edu.ecommerce.util.WebServiceUtil;

public class LoginBO {
	
	private Context context;
	
//	private LoginOpenHelper loginOpenHelper;
	
	public LoginBO(Context context) {
		this.context = context;
//		loginOpenHelper = new LoginOpenHelper(context);
	}

	public ValidacaoLogin validarLogin(String login, String senha) {
		ValidacaoLogin retorno = new ValidacaoLogin();
		Usuario usuario = null;
		if (login == null || login.equals("")) {
			retorno.setValido(false);
			retorno.setMensagem(context.getString(R.string.msg_login_obg));
		} else if (senha == null || senha.equals("")) {
			retorno.setValido(false);
			retorno.setMensagem(context.getString(R.string.msg_senha_obg));
		} else if ((usuario = WebServiceUtil.validarLoginRest(login, senha)).isLogado()) {
			retorno.setValido(true);
			retorno.setMensagem(context.getString(R.string.msg_login_sucesso));
		} else {
			retorno.setValido(false);
			retorno.setMensagem(context.getString(R.string.msg_login_invalido));
		}
		retorno.setUsuario(usuario);
		return retorno;
	}
	
}
