package com.example.myinformant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myinformant.adapter.NewsAdapter;
import com.example.myinformant.api.ApiClient;
import com.example.myinformant.api.ApiInterface;
import com.example.myinformant.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY = "c0e3db5046b8490488cd58ebaf107e7b";

    public Activity activity = MainActivity.this;

    private RecyclerView recyclerView;
    private TextView topHeadLinesTextView;

    private List<Article> articles = new ArrayList<>();

    private NewsAdapter newsAdapter;

    private String TAG = MainActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;

    private ConstraintLayout errorConatinerLayout;
    private ImageView errorImage;
    private TextView erroeTitle, errorMessage;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        topHeadLinesTextView = findViewById(R.id.topHeadlinesTextView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLauout);

        swipeRefreshLayout.setOnRefreshListener(this);

        errorConatinerLayout = findViewById(R.id.erroeContainerLayout);
        errorImage = findViewById(R.id.errorImage);
        erroeTitle = findViewById(R.id.erroeTitle);
        errorMessage = findViewById(R.id.errorMessage);
        retryButton = findViewById(R.id.retryButton);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoading("");

    }

    public void loadJSON(final String keyword){

        errorConatinerLayout.setVisibility(View.GONE);

        topHeadLinesTextView.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<NewsModel> call ;

        if(keyword.length() > 0){

            call = apiInterface.getSearchedNews(keyword, language, "publishedAt", API_KEY);

        }else{

            call = apiInterface.getNews(country, API_KEY);

        }

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {

                if(response.isSuccessful() && response.body().getArticles() != null){

                    if(!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    Log.d("Articles", ""+articles);

                    newsAdapter = new NewsAdapter(MainActivity.this, articles, activity);
                    recyclerView.setAdapter(newsAdapter);

                    newsAdapter.notifyDataSetChanged();

                    topHeadLinesTextView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);


                }else{
                    topHeadLinesTextView.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch(response.code()){

                        case 404 :
                            errorCode = "404 not found";
                            break;

                        case 500 :
                            errorCode = "500 server broken";
                            break;

                        default :
                            errorCode = "unknown error";
                            break;

                    }

                    showError(R.drawable.error_image, "No Results", "Sorry try again\n" + errorCode);

                }

            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                topHeadLinesTextView.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                showError(R.drawable.error_image, "Oops..", "Network failure. Please try again\n" + t.toString());

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchIcon);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        if(query.length() > 2){
            onLoading(query);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRefresh() {
        loadJSON("");
    }

    private void onLoading(final String keyword){

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                loadJSON(keyword);

            }
        });

    }

    private void showError(int image, String title, String message){

        if(errorConatinerLayout.getVisibility() == View.GONE){

            errorConatinerLayout.setVisibility(View.VISIBLE);

        }

        errorImage.setImageResource(image);
        erroeTitle.setText(title);
        errorMessage.setText(message);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onLoading("");

            }
        });

    }

}