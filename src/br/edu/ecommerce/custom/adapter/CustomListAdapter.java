package br.edu.ecommerce.custom.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import br.edu.ecommerce.R;
import br.edu.ecommerce.custom.app.AppController;
import br.edu.ecommerce.custom.model.Produto;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomListAdapter extends BaseAdapter {

	private Activity activity;
	private List<Produto> produtos = new ArrayList<Produto>();

	private LayoutInflater inflater;

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	
	private ProductListAdapterListener listener;

	public CustomListAdapter(Activity activity, List<Produto> produtos, ProductListAdapterListener listener) {
		this.activity = activity;
		this.produtos = produtos;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return produtos.size();
	}

	@Override
	public Object getItem(int position) {
		return produtos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null) {
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.linha_lista, null);
		}

		if (imageLoader == null) {
			imageLoader = AppController.getInstance().getImageLoader();
		}

		NetworkImageView imageProd = (NetworkImageView) convertView.findViewById(R.id.imgProduto);
		TextView txtTitulo = (TextView) convertView.findViewById(R.id.txtTitulo);
		TextView txtQtde = (TextView) convertView.findViewById(R.id.txtQtde);
		TextView txtCategoria = (TextView) convertView.findViewById(R.id.txtCategoria);
		TextView txtValor = (TextView) convertView.findViewById(R.id.txtValor);

		// Recuperando o produto atual da iteração
		final Produto prod = produtos.get(position);

		imageProd.setImageUrl(prod.getUrlImg(), imageLoader);

		txtTitulo.setText(prod.getTitulo());
		txtQtde.setText("Qtde Vendidos: " + String.valueOf(prod.getQtde()));
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt"));
		txtValor.setText("R$ " + String.valueOf(format.format(prod.getValor())));

		String concat = "";
		for (String cat : prod.getCategorias()) {
			concat += cat + ", ";
		}
		concat = concat.length() > 0 ? concat.substring(0, concat.length() - 2) : concat;

		txtCategoria.setText(concat);

		Button btnAddToCart = (Button) convertView.findViewById(R.id.btnAddToCart);

		btnAddToCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onAddCarrrinhoPressed(prod);
			}
		});

		return convertView;
	}

	public interface ProductListAdapterListener {
		public void onAddCarrrinhoPressed(Produto produto);
	}

}
