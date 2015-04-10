package br.edu.ecommerce.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;

import br.edu.ecommerce.custom.model.Usuario;

public class WebServiceUtil {
	
	private static String URL = "http://192.168.0.102:8080/LoginWebService/services/Login?wsdl";
	
	private static String NAMESPACE = "http://login.devmedia.edu.br/";
	
	/**
	 * Método usando SOAP
	 * 
	 * @param usuario
	 * @param senha
	 * @return
	 */
	public static boolean validarLogin(String usuario, String senha) {
		boolean status = false;
		
		SoapObject soapObject = new SoapObject(NAMESPACE, "logar");
		
		PropertyInfo userPI = new PropertyInfo();
		PropertyInfo passPI = new PropertyInfo();
		
		userPI.setName("user");
		userPI.setValue(usuario);
		userPI.setType(String.class);
		
		passPI.setName("pass");
		passPI.setValue(senha);
		passPI.setType(String.class);
		
		soapObject.addProperty(userPI);
		soapObject.addProperty(passPI);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(soapObject);
		
		HttpTransportSE transportSE = new HttpTransportSE(URL);
		
		try {
			transportSE.call(NAMESPACE + "logar", envelope);
			
			SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
			status = Boolean.parseBoolean(resp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	public static Usuario validarLoginRest(String login, String senha) {
		Usuario usuario = null;
		try {
			java.net.URL url = new java.net.URL("http://192.168.0.104:8080/eCommerce-web/login/logar?usuario=" + login
					+ "&senha=" + senha);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedInputStream buffer = new BufferedInputStream(connection.getInputStream());
			
			usuario = new Gson().fromJson(converterToString(buffer), Usuario.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return usuario;
	}

	private static String converterToString(InputStream in) {
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		
		StringBuilder builder = new StringBuilder();
		String linha = null;
		try {
			while ((linha = buffer.readLine()) != null) {
				builder.append(linha);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
}
