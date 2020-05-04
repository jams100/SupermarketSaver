package com.example.testwebscrape.DataModel;

public class Products {

    private String imageLogo;
    private String imageProduct;
    private String productDescription;
    private String PriceOld;
    private String priceNew;
    private String urlLink;
    public boolean isImageChanged;

    public Products() {
    }

    public Products(String ProductDes, String priceOld, String imageP, String urlLink, String imageLogo, String PriceNew) {
        this.productDescription = ProductDes;
        this.PriceOld = priceOld;
        this.priceNew = PriceNew;
        this.urlLink = urlLink;
        this.imageProduct = imageP;
        this.imageLogo = imageLogo;
    }

    public String getImageLogo() {
        return imageLogo;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public String getPriceOld() {
        return PriceOld;
    }

    public String getPriceNew() {
        return priceNew;
    }

    public void setNewImage(String image) {
        this.imageProduct = image;
    }

    public void setNewPrice(String price) {
        this.priceNew = price;
    }

    public void setpriceOld(String price) {
        this.PriceOld = price;
    }

    public boolean isImageChanged() {
        return isImageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        isImageChanged = imageChanged;
    }
}