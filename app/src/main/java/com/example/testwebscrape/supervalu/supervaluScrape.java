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
        String url="https://www.tesco.ie/groceries/product/browse/default.aspx?N=4294848143&Ne=4294954028";

        return url;
    }

}
