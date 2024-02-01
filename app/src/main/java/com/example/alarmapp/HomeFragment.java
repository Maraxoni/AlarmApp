package com.example.alarmapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;
import com.example.alarmapp.quoteapi.QuoteApi;
import com.example.alarmapp.quoteapi.QuoteApiClient;
import com.example.alarmapp.quoteapi.QuoteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView quoteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_tab_fragment, container, false);
        quoteTextView = view.findViewById(R.id.quoteTextView);
        loadRandomQuote();
        return view;
    }

    private void loadRandomQuote() {
        QuoteApi quoteApi = QuoteApiClient.getQuoteApi();
        Call<QuoteResponse> call = quoteApi.getRandomQuote();

        call.enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuoteResponse quote = response.body();
                    String quoteText = quote.getContent() + "\n- " + quote.getAuthor();
                    quoteTextView.setText(quoteText);
                } else {
                    quoteTextView.setText("Failed to fetch quote");
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                quoteTextView.setText("Failed to fetch quote");
            }
        });
    }
}