package br.edu.ecommerce.custom.util;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Classe responsável por manter o cache das informações de imagens em disco.
 * 
 * @author DevMedia
 * 
 */
public class BitMapCache extends LruCache<String, Bitmap> implements ImageCache {

	public BitMapCache() {
		this(getDefaultCacheSize());
	}

	public BitMapCache(int maxSize) {
		super(maxSize);
	}

	public static int getDefaultCacheSize() {
		final int memoriaMax = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int tamanhoCache = memoriaMax / 8;

		return tamanhoCache;
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap valor) {
		put(url, valor);
	}

}
