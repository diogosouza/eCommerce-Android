package br.edu.ecommerce;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import br.edu.ecommerce.bo.MockBO;
import br.edu.ecommerce.comum.Profissao;
import br.edu.ecommerce.dto.PessoaDTO;
import br.edu.ecommerce.util.MensagemUtil;

public class MockEditActivity extends Activity {
	
	private EditText edtNome = null;
	private EditText edtEndereco = null;
	private EditText edtCPF = null;
	private Spinner spnProfissao = null;
	private RadioGroup rgpSexo = null;
	private RadioButton rbtMasc = null;
	private RadioButton rbtFem = null;
	private Button btnAtualizar = null;
	private MockBO mockBO;
	private PessoaDTO pessoa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro_mock);
		setTitle("Atualizar Pessoa");

		mockBO = new MockBO(this);
		this.initEdit();
		this.montarTelaEdicao();
	}
	
	private void initEdit() {
		pessoa = (PessoaDTO) getIntent().getExtras().getSerializable("pessoa");
		
		edtNome = (EditText) findViewById(R.id.edt_nome);
		edtEndereco = (EditText) findViewById(R.id.edt_endereco);
		edtCPF = (EditText) findViewById(R.id.edt_cpf);
		spnProfissao = (Spinner) findViewById(R.id.spn_profissao);
		rgpSexo = (RadioGroup) findViewById(R.id.rgp_sexo);
		rbtMasc = (RadioButton) findViewById(R.id.rbt_masculino);
		rbtFem = (RadioButton) findViewById(R.id.rbt_feminino);
		btnAtualizar = (Button) findViewById(R.id.btn_cadastrar);
		
		List<String> valores = new ArrayList<String>();
		for (Profissao p : Profissao.values()) {
			valores.add(p.getDescricao());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
				valores);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnProfissao.setAdapter(adapter);
		
		btnAtualizar.setText("Atualizar");
		btnAtualizar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				atualizar();
			}
		});
	}
	
	private void montarTelaEdicao() {
		edtNome.setText(pessoa.getNome());
		edtEndereco.setText(pessoa.getEndereco());
		edtCPF.setText(pessoa.getCpf().toString());
		spnProfissao.setSelection(pessoa.getProfissao() - 1);
		if (pessoa.getSexo() == 'M') {
			rbtMasc.setChecked(true);
		} else {
			rbtFem.setChecked(true);
		}
	}
	
	private void atualizar() {
		PessoaDTO pessoaDTO = new PessoaDTO();
		pessoaDTO.setIdPessoa(pessoa.getIdPessoa());
		pessoaDTO.setNome(edtNome.getText().toString());
		pessoaDTO.setEndereco(edtEndereco.getText().toString());
		pessoaDTO.setCpf(Long.parseLong(edtCPF.getText().toString()));
		pessoaDTO.setProfissao(spnProfissao.getSelectedItemPosition() + 1);
		pessoaDTO.setSexo(rbtMasc.isChecked() ? 'M' : 'F');
		
		mockBO.atualizarPessoa(pessoaDTO);
		
		MensagemUtil.addMsg(this, "Pessoa atualizada com sucesso!");
		
		Intent i = new Intent(this, MockListActivity.class);
		startActivity(i);
		finish();
	}
	
}
