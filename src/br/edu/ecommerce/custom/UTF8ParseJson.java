package br.edu.ecommerce.custom;

import org.json.JSONArray;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

public class UTF8ParseJson extends JsonArrayRequest {

	public UTF8ParseJson(String url, Listener<JSONArray> listener,
			ErrorListener errorListener) {
		super(url, listener, errorListener);
	}
	
	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		Response<JSONArray> array = null;
		
		try {
			String string = new String(response.data, "UTF-8");
			array = Response.success(new JSONArray(string), HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return array;
	}

}
