package br.edu.ecommerce;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import br.edu.ecommerce.custom.AdapterListViewCustom;
import br.edu.ecommerce.custom.ItemDash;
import br.edu.ecommerce.dominio.DashBoardListItem;

public class DashBoardListActivity extends Activity {
	
	private ListView lstDash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard_list);
		
		lstDash = (ListView) findViewById(R.id.lstDash);
		
		List<ItemDash> listaItems = new ArrayList<ItemDash>();
		for (DashBoardListItem boardListItem : DashBoardListItem.values()) {
			ItemDash itemDash = new ItemDash(boardListItem.getIdImg(), boardListItem.getTitulo());
			listaItems.add(itemDash);
		}

		AdapterListViewCustom adapter = 
				new AdapterListViewCustom(this, R.layout.item_listview_dash, listaItems);
		lstDash.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Em quadros");
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case 1:
			Intent intent = new Intent(this, DashBoardActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		return true;
	}

}
