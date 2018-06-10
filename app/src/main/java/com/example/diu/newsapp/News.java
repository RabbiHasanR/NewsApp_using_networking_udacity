package com.example.diu.newsapp;

public class News {
    private String news_title;
    private String news_section;
    private String news_publish_date;
    private String news_url;

    public News(String news_title, String news_section, String news_publish_date,String news_url) {
        this.news_title = news_title;
        this.news_section = news_section;
        this.news_publish_date = news_publish_date;
        this.news_url=news_url;
    }

    public String getNews_title() {
        return news_title;
    }

    public String getNews_section() {
        return news_section;
    }

    public String getNews_publish_date() {
        return news_publish_date;
    }

    public String getNews_url() {
        return news_url;
    }
}
