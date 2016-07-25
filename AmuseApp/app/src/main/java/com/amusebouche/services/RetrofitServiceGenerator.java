package com.amusebouche.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Retrofit service generator.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It creates a new retrofit service with or without token authentication.
 */
public class RetrofitServiceGenerator {
    public static final String API_BASE_URL = "http://amuse-bouche.noeliarcado.es/";


    /**
     * Creates a basic retrofit service.
     * @param serviceClass service class
     * @return new service instance
     */
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, false);
    }

    /**
     * Creates a basic retrofit service.
     * @param serviceClass service class
     * @param enableLogging boolean to enable logging
     * @return new service instance
     */
    public static <S> S createService(Class<S> serviceClass, boolean enableLogging) {
        return createService(serviceClass, null, enableLogging);
    }

    /**
     * Creates a basic retrofit service.
     * @param serviceClass service class
     * @param authToken authorization token
     * @return new service instance
     */
    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        return createService(serviceClass, authToken, false);
    }

    /**
     * Create a retrofit service that uses token authorization by implementing an interceptor
     * that sends the token in an authorization header.
     * @param serviceClass service class
     * @param authToken authorization token
     * @param enableLogging boolean to enable logging
     * @return new service instance
     */
    public static <S> S  createService(Class<S> serviceClass, final String authToken,
                                       boolean enableLogging) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL);

        if (authToken != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Token " + authToken)
                        .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        if (enableLogging) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // set the desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // add logging as last interceptor
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
