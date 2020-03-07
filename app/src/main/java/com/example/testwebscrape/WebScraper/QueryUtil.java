package com.example.testwebscrape.WebScraper;

import android.util.Log;

import com.example.testwebscrape.Products;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueryUtil {

    public static final String LOG_TAG = QueryUtil.class.getSimpleName();

    static String url;
    static String superUrl;

    public QueryUtil() {

    }

    /**
     * Query the online website and return an {@link List} object to represent a single earthquake.
     */
    public static List<Products> fetchWebsiteData(String requestUrl, String supervaluUrl) {
        url = requestUrl;
        superUrl = supervaluUrl;
        // Create URL object
        URL url = createUrl(requestUrl);

        //**PROBLEM HERE????????***//

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

    /*** Make an HTTP request to the given URL and return a String as the response.*/
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

    /*** Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.*/
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
        Document doc = null;
        Document superVdoc = null;
        try {
            doc = Jsoup.connect(url).get();
            superVdoc = Jsoup.connect(superUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtil", "Problem parsing results", e);
        }

        //TESCO Webscraping
        for (Element row : doc.select("div.desc")) {
            Products pro;

            if (!row.select("a.id").text().equals("")) {
                continue;
            } else {
                String imageurl = row.select("img[src]").attr("src");
                String productLink = row.select("a[href]").attr("href");

//                String productLink = "https://www.tesco.ie";
//                String s = row.select("a[ref]").attr("href");
//                s = s.replace(" ", "+");
//                productLink += s;

                //String productLink = "https://www.tesco.ie/groceries/Product/Details/?id=259009761";

                String productdescription = row.select("h3.inBasketInfoContainer").text();
                String NewPrice = "";   //row.select("span.linePrice").text();
                String oldPrice = "";
                String imglogo = "https://www.retail-fmcg.ro/wp-content/uploads/2010/11/Copy-of-tesco_logo.jpeg";

                pro = new Products(productdescription, oldPrice, imageurl, productLink, imglogo, NewPrice);
            }
            products.add(pro);

        }
        //To Get Price for Tesco Products
        int count = 0;
        for (Element row : doc.select("div.quantity")) {
            Products pro = null;

            if (!row.select("a.id").text().equals("")) {
                continue;
            } else {
                String NewPrice = row.select("span.linePrice").text();
                //String priceOld=row.select("em").text();

                products.get(count).setNewPrice(NewPrice);
                //products.get(count).setpriceOld(priceOld);

                count++;
            }
        }
//
//        //Used for sorting Tesco prices
//        Collections.sort(products, new Comparator<Products>() {
//            @Override
//            public int compare(Products o1, Products o2) {
//                String p1=o1.PriceNew;
//                String p2=o2.PriceNew;
//
//                return p1.compareTo(p2);
//            }
//        });

        //SUPERVALU Webscraping
        for (Element row : superVdoc.select("div.product-list-item-details")) {
            Products pro1;

            if (row.select("h4.product-list-item-details-title").text().equals("")) {
                continue;
            } else {
                //String imageurl = "https://d2wwnnx8tks4e8.cloudfront.net/images/app/medium/5011001295277_2.JPG";
                String productLink = row.select("a[href]").attr("href");
                String productdescription = row.select("h4.product-list-item-details-title").text();
                String NewPrice = row.select("span[data-unit-price]").attr("data-unit-price"); //row.select("span.linePrice").text();
                String oldPrice = "";
                String imglogo = "https://www.independent.ie/business/personal-finance/article31444718.ece/5fab8/AUTOCROP/w620/2015-08-13_bus_11776288_I4.JPG";
                String Imageurl = null;

                pro1 = new Products(productdescription, oldPrice, Imageurl, productLink, imglogo, NewPrice);
            }
            products.add(pro1);
        }

//        //To Get Image For Supervalu
//        int county = 0;
//        for (Element row : superVdoc.select("div.product-list-item-display")) {
//            Products pro1 = null;
//
//            if (!row.select("img.src").text().equals("")) {
//                continue;
//            } else {
//                String Imageurl = row.select("img[data-src]").attr("data-src");
//                products.get(county).setNewImage(Imageurl);
//                county++;
//            }
//        }

        //Sorts Tesco & Supervalu prices
        Collections.sort(products, new Comparator<Products>() {
            @Override
            public int compare(Products o1, Products o2) {
                String p1 = o1.PriceNew;
                String p2 = o2.PriceNew;

                return p1.compareTo(p2);
            }
        });

        // Return the list of products
        return products;
    }
}