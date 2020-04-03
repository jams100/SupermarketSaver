package com.example.testwebscrape;

public class Products {

    private String imageLogo;
    private String imageProduct;
    private String ProductDescription;
    private String PriceOld;
    private String PriceNew;
    private String urlLink;
    public boolean isImageChanged;

    public Products() {}

    public Products(String price) {
        PriceOld = price;
    }

    public Products(String imageP, String urlLink) {
        imageProduct = imageP;
        this.urlLink = urlLink;
    }

    public Products(String ProductDes, String imageP, String urlLink) {
        ProductDescription = ProductDes;
        imageProduct = imageP;
        this.urlLink = urlLink;
    }

    public Products(String ProductDes, String PriceNew, String imageP, String urlLink, String imageLogo) {
        this.ProductDescription = ProductDes;
        this.PriceNew = PriceNew;
        this.imageProduct = imageP;
        this.urlLink = urlLink;
        this.imageLogo = imageLogo;
    }

    public Products(String ProductDes, String priceOld, String imageP, String urlLink, String imageLogo, String PriceNew) {
        this.ProductDescription = ProductDes;
        this.PriceOld = priceOld;
        this.PriceNew = PriceNew;
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
        return ProductDescription;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public String getPriceOld() {
        return PriceOld;
    }

    public String getPriceNew() {
        return PriceNew;
    }

    public void setNewImage(String image) {
        this.imageProduct = image;
    }

    public void setNewPrice(String price) {
        this.PriceNew = price;
    }

    public void setpriceOld(String price) {
        this.PriceOld = price;
    }

    public boolean isImageChanged() { return isImageChanged; }

    public void setImageChanged(boolean imageChanged) { isImageChanged = imageChanged; }
}