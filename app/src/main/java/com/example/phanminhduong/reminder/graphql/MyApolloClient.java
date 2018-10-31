package com.example.phanminhduong.reminder.graphql;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

public class MyApolloClient {

    private static final String BASE_URL = "https://reminder.phanmduong.xyz/graphql";
    private static ApolloClient apolloClient;

    public static ApolloClient getApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();

        return apolloClient;
    }
}
