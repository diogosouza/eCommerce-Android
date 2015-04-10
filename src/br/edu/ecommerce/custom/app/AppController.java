package br.edu.ecommerce.custom.app;

import br.edu.ecommerce.custom.util.BitMapCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.text.TextUtils;

public class AppController extends Application {

	private static final String TAG = AppController.class.getSimpleName();

	private static AppController appController;

	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		appController = this;
	}

	public static synchronized AppController getInstance() {
		return appController;
	}

	public RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return requestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (imageLoader == null) {
			imageLoader = new ImageLoader(requestQueue, new BitMapCache());
		}
		return imageLoader;
	}

	public <T> void addToRequestQueue(Request<T> request, String tag) {
		request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(request);
	}

	public <T> void addToRequestQueue(Request<T> request) {
		request.setTag(TAG);
		getRequestQueue().add(request);
	}

	public void cancelarRequestPending(Object tag) {
		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}
	}

}
