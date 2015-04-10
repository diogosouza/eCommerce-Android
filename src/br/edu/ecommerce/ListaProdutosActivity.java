package br.edu.ecommerce;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import br.edu.ecommerce.custom.UTF8ParseJson;
import br.edu.ecommerce.custom.adapter.CustomListAdapter;
import br.edu.ecommerce.custom.adapter.CustomListAdapter.ProductListAdapterListener;
import br.edu.ecommerce.custom.app.AppController;
import br.edu.ecommerce.custom.model.Produto;
import br.edu.ecommerce.paypal.Config;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class ListaProdutosActivity extends Activity implements SearchView.OnQueryTextListener, ProductListAdapterListener {

	private static final String TAG = ListaProdutosActivity.class.getSimpleName();

	private ListView lstProd;
	private List<Produto> produtos = new ArrayList<Produto>();
	private CustomListAdapter adapter;
	private SearchView searchView;

	private ProgressDialog progressDialog;

	// To store the products those are added to cart
	private List<PayPalItem> produtosCarrinho = new ArrayList<PayPalItem>();

	private Button btnCheckout;

	private static final int CODIGO_PAGTO = 1;

	// PayPal configuration
	private static PayPalConfiguration paypalConfig = 
			new PayPalConfiguration().environment(Config.PAYPAL_ENVIRONMENT).clientId(Config.PAYPAL_CLIENT_ID).languageOrLocale("pt_BR");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listagem_produtos);

		lstProd = (ListView) findViewById(R.id.listaProd);
		btnCheckout = (Button) findViewById(R.id.btnCheckout);

		adapter = new CustomListAdapter(this, produtos, this);
		lstProd.setAdapter(adapter);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Carregando...");
		progressDialog.show();

		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1b1b1b")));

		// Starting PayPal service
		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
		startService(intent);

		// Checkout button click listener
		btnCheckout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check for empty cart
				if (produtosCarrinho.size() > 0) {
					executarPagtoPayPal();
				} else {
					Toast.makeText(getApplicationContext(), "Carrinho vazio! Favor adicionar produtos!", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// Fetching products from server
		executarRequestProdutos(null);
	}

	private void executarRequestProdutos(String param) {
		produtos.clear();
		if (param != null) {
			try {
				param = URLEncoder.encode(param, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		JsonArrayRequest requestProds = new UTF8ParseJson(param != null ? Config.URL_PRODUTOS + "?str=" + param : Config.URL_PRODUTOS, new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray jsonArray) {
				esconderDialog();

				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject jsonObj = jsonArray.getJSONObject(i);

						Gson gson = new Gson();
						Produto produto = gson.fromJson(jsonObj.toString(), Produto.class);

						produtos.add(produto);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				adapter.notifyDataSetChanged();
			}
		}, 
		new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Erro: " + error.getMessage());
				esconderDialog();
			}
		});
		AppController.getInstance().addToRequestQueue(requestProds);
	}

	private void esconderDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		esconderDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.produtos, menu);

		MenuItem item = menu.findItem(R.id.pesquisaProdutos);
		searchView = (SearchView) item.getActionView();
		configurarWidgetSearch();
		return true;
	}

	private void configurarWidgetSearch() {
		searchView.setIconifiedByDefault(true);

		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (manager != null) {
			List<SearchableInfo> global = manager.getSearchablesInGlobalSearch();

			SearchableInfo info = manager.getSearchableInfo(getComponentName());
			for (SearchableInfo i : global) {
				if (i.getSuggestAuthority() != null
						&& i.getSuggestAuthority().startsWith("applications")) {
					info = i;
				}
			}
			searchView.setSearchableInfo(info);
		}
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText != null) {
			executarRequestProdutos(newText);
		}
		return false;
	}

	/**
	 * Verifying the mobile payment on the server to avoid fraudulent payment
	 * */
	private void verificarPagtoServidor(final String idPagto, final String jsonClientePagto) {
		// Showing progress dialog before making request
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Verificando pagamento...");
		progressDialog.show();

		StringRequest verifyReq = new StringRequest(Method.POST, Config.URL_VER_PAGTOS, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d(TAG, "verify payment: " + response.toString());

				try {
					JSONObject res = new JSONObject(response);
					boolean erro = res.getBoolean("erro");
					String msg = res.getString("msg");

					// user error boolean flag to check for errors
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

					if (!erro) {
						// empty the cart
						produtosCarrinho.clear();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// hiding the progress dialog
				esconderDialog();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Erro de verificação: " + error.getMessage());
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
				// hiding the progress dialog
				esconderDialog();
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				SharedPreferences preferences = getPreferences(MODE_PRIVATE);
				
				Integer idUsuario = preferences.getInt("usuario", 0);
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("idPagto", idPagto);
				params.put("idUsuario", idUsuario != 0 ? String.valueOf(idUsuario) : "1");
				params.put("jsonClientePagto", jsonClientePagto);

				return params;
			}
		};

		// Setting timeout to volley request as verification request takes sometime
		int socketTimeout = 60000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		verifyReq.setRetryPolicy(policy);

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(verifyReq);
	}

	/**
	 * Preparing final cart amount that needs to be sent to PayPal for payment
	 * */
	private PayPalPayment prepararCarrinhoFinal() {
		PayPalItem[] itens = new PayPalItem[produtosCarrinho.size()];
		itens = produtosCarrinho.toArray(itens);

		// Total amount
		BigDecimal subtotal = PayPalItem.getItemTotal(itens);

		// If you have frete cost, add it here
		BigDecimal frete = new BigDecimal("0.0");

		// If you have tax, add it here
		BigDecimal taxa = new BigDecimal("0.0");

		PayPalPaymentDetails detalhesPagto = new PayPalPaymentDetails(frete, subtotal, taxa);

		BigDecimal quantia = subtotal.add(frete).add(taxa);

		PayPalPayment pagto = new PayPalPayment(
				quantia,
				Config.DEFAULT_CURRENCY,
				"Transação de compra na Loja DevMedia sendo processada...",
				Config.PAYMENT_INTENT);

		pagto.items(itens).paymentDetails(detalhesPagto);

		// Custom field like invoice_number etc.,
		pagto.custom("Texto a ser associado com o pagto que a app vai usar.");

		return pagto;
	}

	/**
	 * Launching PalPay payment activity to complete the payment
	 * */
	private void executarPagtoPayPal() {
		PayPalPayment coisasPraComprar = prepararCarrinhoFinal();

		Intent intent = new Intent(ListaProdutosActivity.this, PaymentActivity.class);

		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, coisasPraComprar);

		startActivityForResult(intent, CODIGO_PAGTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODIGO_PAGTO) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.e(TAG, confirm.toJSONObject().toString(4));
						Log.e(TAG, confirm.getPayment().toJSONObject().toString(4));

						String pagtoId = confirm.toJSONObject().getJSONObject("response").getString("id");

						String jsonClientePagto = confirm.getPayment().toJSONObject().toString();

						Log.e(TAG, "pagtoId: " + pagtoId + ", jsonClientePagto: " + jsonClientePagto);

						// Now verify the payment on the server side
						verificarPagtoServidor(pagtoId, jsonClientePagto);
					} catch (JSONException e) {
						Log.e(TAG, "Um erro ocorreu: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.e(TAG, "O usuário cancelou a operação.");
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.e(TAG, "Um pagamento inválido foi submetido.");
			}
		}
	}

	@Override
	public void onAddCarrrinhoPressed(Produto produto) {
		PayPalItem item = new PayPalItem(produto.getTitulo(), 1,
				new BigDecimal(produto.getValor()).setScale(2, RoundingMode.CEILING), Config.DEFAULT_CURRENCY, produto.getSku());

		produtosCarrinho.add(item);

		Toast.makeText(getApplicationContext(), item.getName() + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
	}
}
