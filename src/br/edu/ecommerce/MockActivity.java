package br.edu.ecommerce;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import br.edu.ecommerce.bo.MockBO;
import br.edu.ecommerce.comum.Profissao;
import br.edu.ecommerce.dominio.ValidacaoMock;
import br.edu.ecommerce.dto.PessoaDTO;
import br.edu.ecommerce.util.MensagemUtil;

public class MockActivity extends Activity {

	private EditText edtNome = null;
	private EditText edtEndereco = null;
	private EditText edtCPF = null;
	private Spinner spnProfissao = null;
	private RadioGroup rgpSexo = null;
	private RadioButton rbtMasc = null;
	private RadioButton rbtFem = null;
	private MockBO mockBO;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro_mock);
		setTitle("Castro de Pessoa");
		
		mockBO = new MockBO(this);
		
		edtNome = (EditText) findViewById(R.id.edt_nome);
		edtEndereco = (EditText) findViewById(R.id.edt_endereco);
		edtCPF = (EditText) findViewById(R.id.edt_cpf);
		spnProfissao = (Spinner) findViewById(R.id.spn_profissao);
		rgpSexo = (RadioGroup) findViewById(R.id.rgp_sexo);
		rbtMasc = (RadioButton) findViewById(R.id.rbt_masculino);
		rbtFem = (RadioButton) findViewById(R.id.rbt_feminino);
		
		List<String> valores = new ArrayList<String>();
		for (Profissao p : Profissao.values()) {
			valores.add(p.getDescricao());
		}
		ArrayAdapter adapter = new ArrayAdapter(MockActivity.this, android.R.layout.simple_spinner_item, 
				valores);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnProfissao.setAdapter(adapter);
	}

	public void cadastrar(View view) {
		PessoaDTO pessoaDTO = new PessoaDTO();
		pessoaDTO.setNome(edtNome.getText().toString());
		pessoaDTO.setEndereco(edtEndereco.getText().toString());
		pessoaDTO.setCpf(Long.parseLong(edtCPF.getText().toString()));
		pessoaDTO.setProfissao(spnProfissao.getSelectedItemPosition() + 1);
		pessoaDTO.setSexo(rbtMasc.isChecked() ? 'M' : 'F');
		
		ValidacaoMock resultado = mockBO.castrarPessoa(pessoaDTO);
		MensagemUtil.addMsg(this, resultado.getMensagem());
		
		Intent i = new Intent(this, MockListActivity.class);
		startActivity(i);
		finish();
	}
}
