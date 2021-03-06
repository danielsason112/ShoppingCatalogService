package il.ac.afeka.shoppingcatalogservice.layout;

public class ProductBoundary {
    private String id;
    private String name;
    private double price;
    private String image;
    private ProductDetails details;
    private CategoryBoundary category;

    public ProductBoundary() {
    }

    public ProductBoundary(String id, String name, double price, String image, ProductDetails details, CategoryBoundary category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.details = details;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductDetails getProductDetails() {
        return details;
    }

    public void setProductDetails(ProductDetails details) {
        this.details = details;
    }

    public CategoryBoundary getCategory() {
        return category;
    }

    public void setCategory(CategoryBoundary category) {
        this.category = category;
    }
}
