package br.edu.ecommerce.dominio;

import br.edu.ecommerce.custom.model.Usuario;

public class ValidacaoLogin {

	private boolean valido;

	private String mensagem;
	
	private Usuario usuario;

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
