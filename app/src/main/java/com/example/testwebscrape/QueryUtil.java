package com.example.testwebscrape;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtil {

    public static final String LOG_TAG =QueryUtil.class.getSimpleName();

    public QueryUtil(){
    }

    /**
     * Query the online website and return an {@link List} object to represent a single earthquake.
     */
    public static List<Products> fetchWebsiteData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Return the {@link Event}
        return extractShoppingData(jsonResponse);
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String webScrapeResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return webScrapeResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                webScrapeResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return webScrapeResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static ArrayList<Products> extractShoppingData(String sample) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Products> products = new ArrayList<>();

            // build up a list of Product objects with the corresponding data.
            Document doc= null;
            try {
                doc = Jsoup.connect("https://shop.supervalu.ie/shopping/frozen-foods-fish/c-150200350").get();
            } catch (IOException e) {
                e.printStackTrace();
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtil", "Problem parsing  results", e);
            }

            //for (Element row:doc.select("section.products.-mabaya div.sku.-gallery")) {

            for (Element row:doc.select("div.product-list-item-details")) {
                Products pro = null;

                if (row.select("h4.product-list-item-details-title").text().equals("")){
                    continue;
                }else{
                    //String imageurl=row.select("img[src]").attr("src");
                    String productLink=row.select("a[ref]").attr("href");

                    String productdescription=row.select("h4.product-list-item-details-title").text();
                    String productPrice=row.select("span.product-details-price-item").text();
                    //String imglogo="https://banner2.cleanpng.com/20180611/itu/kisspng-smith-s-supervalu-grocery-store-supervalu-donegal-5b1e9e2b8afe73.6000034015287332275693.jpg";

                    pro = new Products(productdescription, productPrice, productLink);
                }
                products.add(pro);
            }

        // Return the list of products
        return products;
    }
}