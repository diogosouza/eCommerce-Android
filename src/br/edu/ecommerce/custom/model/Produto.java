package br.edu.ecommerce.custom.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Produto implements Serializable {

	private static final long serialVersionUID = 3990466591798951963L;

	private int id;
	
	private String titulo;

	@SerializedName("imagem")
	private String urlImg;

	private int qtde;
	
	private String sku;

	private double valor;

	@SerializedName("categoria")
	private List<String> categorias = new ArrayList<String>();

	public Produto() {
	}

	public Produto(String titulo, String urlImg, int qtde, double valor,
			List<String> categorias) {
		this.titulo = titulo;
		this.urlImg = urlImg;
		this.qtde = qtde;
		this.valor = valor;
		this.categorias = categorias;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getUrlImg() {
		return urlImg;
	}

	public void setUrlImg(String urlImg) {
		this.urlImg = urlImg;
	}

	public int getQtde() {
		return qtde;
	}

	public void setQtde(int qtde) {
		this.qtde = qtde;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

}
