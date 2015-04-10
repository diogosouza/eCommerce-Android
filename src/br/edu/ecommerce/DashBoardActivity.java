package br.edu.ecommerce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.edu.ecommerce.util.MensagemUtil;

public class DashBoardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		String msg = null;
		if (getIntent().getExtras() != null && (msg = getIntent().getExtras().getString("msg")) != null) {
			MensagemUtil.addMsg(DashBoardActivity.this, msg);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Lista");
		return true;
	}
	
	public void cadastrar(View v) {
		Intent intent = new Intent(this, MockActivity.class);
		startActivity(intent);
	}
	
	public void listarProdutos(View view) {
		Intent intent = new Intent(this, ListaProdutosActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case 1:
			Intent intent = new Intent(this, DashBoardListActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		return true;
	}
	
}
