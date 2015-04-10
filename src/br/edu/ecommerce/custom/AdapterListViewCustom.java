package br.edu.ecommerce.custom;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.edu.ecommerce.R;

public class AdapterListViewCustom extends ArrayAdapter<ItemDash> {

	private Context context;
	
	public AdapterListViewCustom(Context context, int resourceId,
			List<ItemDash> listaItems) {
		super(context, resourceId, listaItems);
		this.context = context;
	}

	private class ViewSelecao {
		private ImageView imgTitulo;
		
		private TextView txtTitulo;

		public ImageView getImgTitulo() {
			return imgTitulo;
		}

		public void setImgTitulo(ImageView imgTitulo) {
			this.imgTitulo = imgTitulo;
		}

		public TextView getTxtTitulo() {
			return txtTitulo;
		}

		public void setTxtTitulo(TextView txtTitulo) {
			this.txtTitulo = txtTitulo;
		}
	}
	
	public View getView(int posicao, View viewConverter, ViewGroup group) {
		ViewSelecao selecao = null;
		
		ItemDash itemDash = getItem(posicao);
		
		LayoutInflater layoutInflater = (LayoutInflater) 
				context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (viewConverter == null) {
			viewConverter = layoutInflater.inflate(R.layout.item_listview_dash, null);
			selecao = new ViewSelecao();
			selecao.setImgTitulo((ImageView) viewConverter.findViewById(R.id.imgTitulo));
			selecao.setTxtTitulo((TextView) viewConverter.findViewById(R.id.txtTitulo));
			viewConverter.setTag(selecao);
		} else {
			selecao = (ViewSelecao) viewConverter.getTag();
		}
		
		selecao.getImgTitulo().setImageResource(itemDash.getIdImg());
		selecao.getTxtTitulo().setText(itemDash.getTitulo());
		return viewConverter;
	}
	
}
