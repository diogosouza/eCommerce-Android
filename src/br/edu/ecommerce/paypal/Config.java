package br.edu.ecommerce.paypal;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {

	// PayPal app configuration
	public static final String PAYPAL_CLIENT_ID = "AY-603rRjtyN8mhlqIh7MpYljXK8Vbmd448PGjdonA15gBW3y2idzkVfo1zJNeLfF7F9pVXxoFgoy80k";
	public static final String PAYPAL_CLIENT_SECRET = "EJunpGLethNAh5oF0Rah6KMr7nsPb9TMaAMtiqVr4DExtXBDnza2Gl9I5zxnbJEA7ugjdiV5515uGMyA";

	public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
	public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
	public static final String DEFAULT_CURRENCY = "BRL";

	// PayPal server urls
	public static final String URL_PRODUTOS = "http://192.168.0.104:8080/eCommerce-web/produto/produtos";
	public static final String URL_VER_PAGTOS = "http://192.168.0.104:8080/eCommerce-web/produto/checkPagto";

}
