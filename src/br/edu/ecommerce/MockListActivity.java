package br.edu.ecommerce;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.edu.ecommerce.bo.MockBO;
import br.edu.ecommerce.comum.Profissao;
import br.edu.ecommerce.dto.PessoaDTO;
import br.edu.ecommerce.util.MensagemUtil;

public class MockListActivity extends Activity {
	
	private MockBO mockBO;

	private List<PessoaDTO> lista;
	
	private ListView lstPessoas;
	
	private Long posicao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_mock);
		setTitle("Lista de Pessoas");

		mockBO = new MockBO(this);
		this.listarPessoas();
		this.consultarPorId();
	}

	public void novoCadastro(View view) {
		Intent intent = new Intent(this, MockActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void listarPessoas() {
		lista = mockBO.listarPessoas();
		
		lstPessoas = (ListView) findViewById(R.id.lst_pessoas);
		
		List<CharSequence> valores = new ArrayList<CharSequence>();
		for (PessoaDTO pessoaDTO : lista) {
			valores.add(pessoaDTO.getNome());
		}
		
		ArrayAdapter<CharSequence> adapter = 
				new ArrayAdapter<CharSequence>(this, 
						android.R.layout.simple_list_item_1, valores); 
		lstPessoas.setAdapter(adapter);
		
		lstPessoas.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view,
					int id, long position) {
				setPosicao(position);
				return false;
			}
		});
		
		lstPessoas.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, 1, 1, "Editar");
				menu.add(0, 2, 2, "Deletar");
			}
		});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			PessoaDTO pessoa = lista.get(posicao.intValue());
			
			Intent i = new Intent(this, MockEditActivity.class);
			i.putExtra("pessoa", pessoa);
			startActivity(i);
			finish();
			break;
		case 2:
			MensagemUtil.addMsgConfirm(this, "Alerta", "Deseja realmente remover esta pessoa?", 
					R.drawable.delete, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PessoaDTO pessoa = lista.get(posicao.intValue());
							mockBO.removerPessoaPorId(pessoa.getIdPessoa());
							
							MensagemUtil.addMsg(MockListActivity.this, "Pessoa removida com sucesso!");
							Intent i = new Intent(MockListActivity.this, MockListActivity.class);
							startActivity(i);
							finish();
						}
					});
			
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	private void consultarPorId() {
		lstPessoas.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int id, long position) {
				PessoaDTO pessoa = lista.get((int) position);
				PessoaDTO pessoaDTO = mockBO.consultarPessoaPorId(pessoa.getIdPessoa());
				
				String msg = "Nome: " + pessoaDTO.getNome() +
						"\nEndereço: " + pessoaDTO.getEndereco() + 
						"\nCPF: " + pessoaDTO.getCpf() + 
						"\nSexo: " + (pessoaDTO.getSexo() == 'M' ? "Masculino" : "Feminino");
				String profissao = null;
				if (pessoaDTO.getProfissao() != 0) {
					profissao = Profissao.getProfissao(pessoaDTO.getProfissao()).getDescricao();
				}
				msg += "\nProfissão:" + (profissao != null ? profissao : "Código errado!");
				
				MensagemUtil.addMsgOk(MockListActivity.this, "Info", msg, R.drawable.about);
			}
		});		
	}

	public Long getPosicao() {
		return posicao;
	}

	public void setPosicao(Long posicao) {
		this.posicao = posicao;
	}
	
}
