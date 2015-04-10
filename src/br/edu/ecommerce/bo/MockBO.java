package br.edu.ecommerce.bo;

import java.util.List;

import android.content.Context;
import br.edu.ecommerce.dominio.ValidacaoMock;
import br.edu.ecommerce.dto.PessoaDTO;
import br.edu.ecommerce.sqlite.MockOpenHelper;

public class MockBO {
	
	private MockOpenHelper mockOpenHelper;
	
	public MockBO(Context context) {
		mockOpenHelper = new MockOpenHelper(context);
	}

	public ValidacaoMock castrarPessoa(PessoaDTO pessoaDTO) {
		ValidacaoMock retorno = new ValidacaoMock();
		mockOpenHelper.cadastrar(pessoaDTO);
		retorno.setValido(true);
		retorno.setMensagem("Cadastro de Pessoa efetuado com sucesso!");
		
		return retorno;
	}
	
	public List<PessoaDTO> listarPessoas() {
		return mockOpenHelper.listar();
	}
	
	public PessoaDTO consultarPessoaPorId(Integer idPessoa) {
		return mockOpenHelper.consultarPessoaPorId(idPessoa);
	}

	public void removerPessoaPorId(Integer idPessoa) {
		mockOpenHelper.removerPessoaPorId(idPessoa);
	}
	
	public void atualizarPessoa(PessoaDTO pessoaDTO) {
		mockOpenHelper.atualizarPessoa(pessoaDTO);
	}
}
