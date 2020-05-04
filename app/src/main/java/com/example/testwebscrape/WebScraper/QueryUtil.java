package com.example.testwebscrape.WebScraper;

import android.util.Log;

import com.example.testwebscrape.DataModel.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryUtil {

    public static final String LOG_TAG = QueryUtil.class.getSimpleName();

    private static String url;
    private static String superUrl;

    public QueryUtil() {
    }


    //Query the online website and return website data
    public static List<Products> fetchWebsiteData(String requestUrl, String supervaluUrl) {
        url = requestUrl;
        superUrl = supervaluUrl;

        return extractShoppingData();
    }

    //Method is used to web scrape the online websites
    public static ArrayList<Products> extractShoppingData() {

        //Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Products> products = new ArrayList<>();
        ArrayList<Products> products1 = new ArrayList<>();

        //Building up a list of Product objects with the corresponding data.
        Document doc = null;
        Document superVdoc = null;
        try {
            doc = Jsoup.connect(url).get();
            superVdoc = Jsoup.connect(superUrl).get();

            //TESCO Webscraping
            for (Element row : doc.select("div.desc")) {
                Products pro;

                if (!row.select("a.id").text().equals("")) {
                    continue;
                } else {
                    String imageurl = row.select("img[src]").attr("src");
                    String productLink = "https://www.tesco.ie";
                    String s = row.select("a[href]").attr("href");
                    s = s.replace(" ", "+");
                    productLink += s;
                    String productdescription = row.select("h3.inBasketInfoContainer").text();
                    String NewPrice = "";//Price is in a different element so got outside for loop
                    String oldPrice = row.select("em").text();
                    String imglogo = "https://www.retail-fmcg.ro/wp-content/uploads/2010/11/Copy-of-tesco_logo.jpeg";

                    pro = new Products(productdescription, oldPrice, imageurl, productLink, imglogo, NewPrice);
                }
                products.add(pro);
            }
            //Getting Prices for Tesco Products which are in a separate element
            int count = 0;
            for (Element row : doc.select("div.quantity")) {
                Products pro = null;

                if (!row.select("a.id").text().equals("")) {
                    continue;
                } else {
                    String NewPrice = row.select("span.linePrice").text();
                    products.get(count).setNewPrice(NewPrice);
                    count++;
                }
            }

            //SUPERVALU Webscraping
            for (Element row : superVdoc.select("div.product-list-item-details")) {
                Products pro1;

                if (row.select("h4.product-list-item-details-title").text().equals("")) {
                    continue;
                } else {
                    String productLink = row.select("a[href]").attr("href");
                    String productdescription = row.select("h4.product-list-item-details-title").text();
                    String NewPrice = row.select("span[data-unit-price]").attr("data-unit-price"); //row.select("span.linePrice").text();
                    String oldPrice = "";
                    //String oldPrice = row.select("product-details-promotion-name.span").text();
                    String imglogo = "https://www.independent.ie/business/personal-finance/article31444718.ece/5fab8/AUTOCROP/w620/2015-08-13_bus_11776288_I4.JPG";
                    String Imageurll = null;

                    pro1 = new Products(productdescription, oldPrice, Imageurll, productLink, imglogo, NewPrice);
                }
                products1.add(pro1);
            }

            //Getting Images For Supervalu's products which are in a separate element
            int county = 0;
            for (Element row : superVdoc.select("div.product-list-item-display")) {
                Products pro1 = null;
                if (!row.select("img.src").text().equals("")) {
                    continue;
                } else if (products1.get(county).getImageLogo().equals("https://www.independent.ie/business/personal-finance/article31444718.ece/5fab8/AUTOCROP/w620/2015-08-13_bus_11776288_I4.JPG")) {
                    String Imageurll = row.select("img[data-src]").attr("data-src");
                    products1.get(county).setNewImage(Imageurll);
                    county++;
                } else {
                    county++;
                }
            }

            //Getting The Offers on Supervalu Products so they can be displayed to the user
            int county1 = 0;
            for (Element row : superVdoc.select("div.product-details-promotion-name")) {
                Products pro1 = null;
                if (products1.get(county1).getImageLogo().equals("https://www.independent.ie/business/personal-finance/article31444718.ece/5fab8/AUTOCROP/w620/2015-08-13_bus_11776288_I4.JPG")) {
                    String oldPricee = row.select("span").text();
                    products1.get(county1).setpriceOld(oldPricee);
                    county1++;
                } else {
                    county1++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message with the message from the exception.
            Log.e("QueryUtil", "Problem parsing results", e);
        }

        // Return the list of products
        products.addAll(products1);//Merge both lists
        return products;
    }
}