package com.example.diu.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context,0,news);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView=convertView;
        if(listItemView==null){
            listItemView=LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        News curentNews=getItem(position);
        TextView newsTitleView=(TextView)listItemView.findViewById(R.id.news_title);
        newsTitleView.setText(curentNews.getNews_title());
        TextView newsSectionNameView=(TextView)listItemView.findViewById(R.id.section_name);
        newsSectionNameView.setText(curentNews.getNews_section());
        TextView newsPublishDate=(TextView)listItemView.findViewById(R.id.publish_date);
        newsPublishDate.setText(curentNews.getNews_publish_date());
        return listItemView;
    }
}
