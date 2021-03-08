package com.milosvalovic.sigfoxparking.classes.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.milosvalovic.sigfoxparking.classes.Consts;
import com.milosvalovic.sigfoxparking.classes.StringConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static String authorizationString;

    public static Retrofit generate(String baseUrl, final Context cnt, Boolean forceDisableToken) {

        if (cnt != null) {
            /*authorizationString = (!Helper.isStringEmpty(SharedData.getFromPrefString(cnt, "token", "")))
                    ? "Bearer " + SharedData.getFromPrefString(cnt, "token", "")
                    : "";*/
        } else {
            authorizationString = "";
        }

        authorizationString = (forceDisableToken) ? "" : authorizationString;

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createClient(cnt))
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    private static OkHttpClient createClient(final Context cnt) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Consts.API_TRUST_ALL_SLL) {
            builder.sslSocketFactory(createTrustAllSll());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        builder.followRedirects(Consts.API_FOLLOW_REDIRECTS);
        builder.followSslRedirects(Consts.API_FOLLOW_REDIRECTS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Consts.API_LOG_LEVEL);
        builder.addInterceptor(interceptor);

        if (cnt != null && Consts.API_PERSISTENT_COOKIE) {
            CookieManager cookieManager = new CookieManager(new PersistentCookieStore(cnt), CookiePolicy.ACCEPT_ALL);
            builder.cookieJar(new JavaNetCookieJar(cookieManager));
        }

        if (Consts.API_FORCE_HTTP_1_PROTOCOL)
            builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));



        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request;
                if(getStoredToken(cnt).equals(""))
                    request = original.newBuilder()
                            .header("User-Agent", Consts.PROJECT_NAME + " Android App")
                            .header("Device", DeviceCode.getCode(cnt))
                            .header("Accept-Encoding", "identity")
                            .header("Connection", "close")
                            .method(original.method(), original.body())
                            .build();
                else
                    request = original.newBuilder()
                            .header("User-Agent", Consts.PROJECT_NAME + " Android App")
                            .header("Device", DeviceCode.getCode(cnt))
                            .header("Accept-Encoding", "identity")
                            .header("Connection", "close")
                            .header("Authorization", "Bearer " + getStoredToken(cnt))
                            .method(original.method(), original.body())
                            .build();
                if (Consts.API_LOG_CUSTOM_HEADERS) {
                    // FOR LOG HEADERS
                    Headers headers = request.headers();
                    for (int i = 0, count = headers.size(); i < count; i++) {
                        String name = headers.name(i);
                        // Skip headers from the request body as they are explicitly logged above.
                        if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                            Log.i(Consts.APP_TAG, name + ": " + headers.value(i));
                        }
                    }
                }

                return chain.proceed(request);
            }
        });

        return builder.build();
    }

    public static String getStoredToken(Context cnt) {
        SharedPreferences sharedPreferences = cnt.getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        return token;
    }

    private static SSLSocketFactory createTrustAllSll() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }
}
