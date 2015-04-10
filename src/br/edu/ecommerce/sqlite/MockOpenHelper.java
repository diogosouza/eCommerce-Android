package br.edu.ecommerce.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.edu.ecommerce.comum.Constantes;
import br.edu.ecommerce.dto.PessoaDTO;

public class MockOpenHelper extends SQLiteOpenHelper {

	private static ResourceBundle config = 
			ResourceBundle.getBundle(Constantes.DB_CONFIG_PROPS, Locale.getDefault());
	
	public MockOpenHelper(Context context) {
		super(context, config.getString(Constantes.DB_CONFIG_NOME), null, 
				Integer.parseInt(config.getString(Constantes.DB_CONFIG_VERSAO)));
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	public void cadastrar(PessoaDTO pessoaDTO) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("NOME", pessoaDTO.getNome());
		values.put("ENDERECO", pessoaDTO.getEndereco());
		values.put("CPF", pessoaDTO.getCpf());
		values.put("PROFISSAO", pessoaDTO.getProfissao());
		values.put("SEXO", String.valueOf(pessoaDTO.getSexo()));
		
		db.insert("TB_PESSOA", null, values);
	}
	
	public List<PessoaDTO> listar() {
		List<PessoaDTO> lista = new ArrayList<PessoaDTO>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(true, "TB_PESSOA", null, null, null, null, null, "ID_PESSOA", null, null);
		while (cursor.moveToNext()) {
			PessoaDTO pessoaDTO = new PessoaDTO();
			pessoaDTO.setIdPessoa(cursor.getInt(cursor.getColumnIndex("ID_PESSOA")));
			pessoaDTO.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
			pessoaDTO.setEndereco(cursor.getString(cursor.getColumnIndex("ENDERECO")));
			pessoaDTO.setCpf(cursor.getLong(cursor.getColumnIndex("CPF")));
			pessoaDTO.setProfissao(cursor.getInt(cursor.getColumnIndex("PROFISSAO")));
			pessoaDTO.setSexo(cursor.getString(cursor.getColumnIndex("SEXO")).charAt(0));
			
			lista.add(pessoaDTO);
		}
		
		return lista;
	}
	
	public PessoaDTO consultarPessoaPorId(Integer idPessoa) {
		PessoaDTO pessoaDTO = new PessoaDTO();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query("TB_PESSOA", null, "ID_PESSOA=?", 
				new String[] {idPessoa.toString()}, null, null, "ID_PESSOA");
		while (cursor.moveToNext()) {
			pessoaDTO.setIdPessoa(cursor.getInt(cursor.getColumnIndex("ID_PESSOA")));
			pessoaDTO.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
			pessoaDTO.setEndereco(cursor.getString(cursor.getColumnIndex("ENDERECO")));
			pessoaDTO.setCpf(cursor.getLong(cursor.getColumnIndex("CPF")));
			pessoaDTO.setProfissao(cursor.getInt(cursor.getColumnIndex("PROFISSAO")));
			pessoaDTO.setSexo(cursor.getString(cursor.getColumnIndex("SEXO")).charAt(0));
		}
		
		return pessoaDTO;
	}
	
	public void removerPessoaPorId(Integer idPessoa) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete("TB_PESSOA", "ID_PESSOA=?", new String[]{idPessoa.toString()});
	}
	
	public void atualizarPessoa(PessoaDTO pessoaDTO) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("NOME", pessoaDTO.getNome());
		values.put("ENDERECO", pessoaDTO.getEndereco());
		values.put("CPF", pessoaDTO.getCpf());
		values.put("PROFISSAO", pessoaDTO.getProfissao());
		values.put("SEXO", String.valueOf(pessoaDTO.getSexo()));
		
		db.update("TB_PESSOA", values, "ID_PESSOA=?", new String[]{pessoaDTO.getIdPessoa().toString()});
	}
}
