package com.example.myinformant.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.myinformant.Article;
import com.example.myinformant.MainActivity;
import com.example.myinformant.R;
import com.example.myinformant.Utils;
import com.example.myinformant.news_detail.NewsDetailActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private List<Article> articles;
    private Activity activity;

    public NewsAdapter(Context mContext, List<Article> articles, Activity activity) {
        this.mContext = mContext;
        this.articles = articles;
        this.activity = activity;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_layout, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final NewsAdapter.ViewHolder holder, final int position) {

        Article model = articles.get(position);

        Glide.with(mContext).load(model.getUrlToImage())
                .apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_size_select_actual_24))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.newsImage);

        holder.title.setText(model.getTitle());
        holder.author.setText(model.getAuthor());
        holder.desc.setText(model.getDescription());
        holder.source.setText(model.getSource().getName());
        holder.time.setText(" \u2022" + Utils.DateToTimeFormat(model.getPublishedAt()));
        holder.publishedAt.setText(Utils.DateFormat(model.getPublishedAt()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = holder.itemView.findViewById(R.id.newsImage);

                Intent newsDetailIntent = new Intent(mContext, NewsDetailActivity.class);

                newsDetailIntent.putExtra("url", articles.get(position).getUrl());
                newsDetailIntent.putExtra("img", articles.get(position).getUrlToImage());
                newsDetailIntent.putExtra("title", articles.get(position).getTitle());
                newsDetailIntent.putExtra("source", articles.get(position).getSource().getName());
                newsDetailIntent.putExtra("date", articles.get(position).getPublishedAt());
                newsDetailIntent.putExtra("author", articles.get(position).getAuthor());

                mContext.startActivity(newsDetailIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView newsImage;
        private TextView author,publishedAt,title,desc,source,time;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.newsImage);
            author = itemView.findViewById(R.id.author);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            title = itemView.findViewById(R.id.titleTextView);
            desc = itemView.findViewById(R.id.desc);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);

            progressBar = itemView.findViewById(R.id.progressBar);

        }


    }
}
