package com.example.testwebscrape.supervalu;

import com.example.testwebscrape.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class supervaluScrape {

    //used to get the data from website
    public static ArrayList<Products> getData(){

        ArrayList<Products> supervaluproduct = null;

        try {
            Document doc= Jsoup.connect(buildUrl()).get();
           String tex= doc.text();

            supervaluproduct.add(new Products(tex,tex));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return supervaluproduct;
    }

    //building the url
    public static String buildUrl(){
        String url="https://centra.ie/offers?category_id=512#special-offer-filter";

        return url;
    }

}
