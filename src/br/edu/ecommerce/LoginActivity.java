package br.edu.ecommerce;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.edu.ecommerce.bo.LoginBO;
import br.edu.ecommerce.custom.model.Usuario;
import br.edu.ecommerce.dominio.ValidacaoLogin;
import br.edu.ecommerce.util.MensagemUtil;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginActivity extends Activity {

	private LoginBO loginBO;
	
	private EditText edtLogin;
	private EditText edtSenha;
	
	private Button btnLoginFacebook;
	
	private static String APP_ID = "834829726567835";
	
	private Facebook facebook = new Facebook(APP_ID);
	private SharedPreferences preferences;
	private AsyncFacebookRunner facebookRunner = new AsyncFacebookRunner(facebook);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		edtLogin = (EditText) findViewById(R.id.edt_login);
		edtSenha = (EditText) findViewById(R.id.edt_senha);
		
		btnLoginFacebook = (Button) findViewById(R.id.btn_fblogin);
		
		preferences = getPreferences(MODE_PRIVATE);
		int usuarioId = preferences.getInt("usuario", 0);
		String token = preferences.getString("access_token", null);
		long expires = preferences.getLong("access_expires", 0);
		if ((token != null && expires != 0) || usuarioId != 0) {
			Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
			startActivity(i);
			finish();
		}
	}
	
	public void logar(View view) {
		new LoadingAsync().execute();
	}
	
	public void verPerfil(View view) {
		facebookRunner.request("me", new RequestListener() {
			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
			}
			
			@Override
			public void onIOException(IOException e, Object state) {
			}
			
			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
			}
			
			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
			
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Informações do Perfil: ", response);
				try {
					JSONObject perfil = new JSONObject(response);
					
					final String nome = perfil.getString("name");
					final String email = perfil.getString("email");
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String msg = "Nome: " + nome + "\nEmail: " + email;
							MensagemUtil.addMsg(LoginActivity.this, msg);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void loginFacebook(View view) {
		preferences = getPreferences(MODE_PRIVATE);
		
		String token = preferences.getString("access_token", null);
		long expires = preferences.getLong("access_expires", 0);
		
		if (token != null) {
			facebook.setAccessToken(token);
			
			btnLoginFacebook.setVisibility(View.INVISIBLE);
			Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
			startActivity(i);
			finish();
		}
		
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		
		if (!facebook.isSessionValid()) {
			facebook.authorize(LoginActivity.this, 
				new String[] {"email", "publish_stream"}, 
					new DialogListener() {
						@Override
						public void onFacebookError(FacebookError e) {
						}
						
						@Override
						public void onError(DialogError e) {
						}
						
						@Override
						public void onComplete(Bundle values) {
							Editor editor = preferences.edit();
							editor.putString("access_token", facebook.getAccessToken());
							editor.putLong("access_expires", facebook.getAccessExpires());
							editor.commit();
							
							btnLoginFacebook.setVisibility(View.INVISIBLE);
							Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
							startActivity(i);
							finish();
						}
						
						@Override
						public void onCancel() {
						}
					});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 1, 1, "Sair");
//		menu.add(0, 2, 2, "Info");
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int idMenuItem = item.getItemId();
		switch (idMenuItem) {
		case R.id.menuSair:
			MensagemUtil.addMsgConfirm(LoginActivity.this, getString(R.string.lbl_sair), 
					getString(R.string.msg_logout), R.drawable.logout, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			break;
		case R.id.menuSobre:
			MensagemUtil.addMsgOk(LoginActivity.this, getString(R.string.lbl_sobre), 
					getString(R.string.msg_sobre), R.drawable.about);
			break;
		}
		
		return true;
	}
	
	private class LoadingAsync extends AsyncTask<Void, Void, ValidacaoLogin> {

		private ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Carregando...");
			progressDialog.show();
		}
		
		@Override
		protected ValidacaoLogin doInBackground(Void... params) {
			String login = edtLogin.getText().toString();
			String senha = edtSenha.getText().toString();
			
			loginBO = new LoginBO(LoginActivity.this);
			
			return loginBO.validarLogin(login, senha);
		}

		@Override
		protected void onPostExecute(ValidacaoLogin validacao) {
			progressDialog.dismiss();
			
			if (validacao.isValido()) {
				Usuario usuario = validacao.getUsuario();
				Editor editor = preferences.edit();
				editor.putInt("usuario", usuario.getId());
				editor.commit();
				
				Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
				i.putExtra("msg", validacao.getMensagem());
				startActivity(i);
				finish();
			} else {
				MensagemUtil.addMsg(LoginActivity.this, validacao.getMensagem());
			}
		}
	}

}
