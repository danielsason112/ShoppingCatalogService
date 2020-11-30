package il.ac.afeka.shoppingcatalogservice.logic;

import il.ac.afeka.shoppingcatalogservice.data.*;
import il.ac.afeka.shoppingcatalogservice.errors.InternalErrorException;
import il.ac.afeka.shoppingcatalogservice.errors.NotFoundException;
import il.ac.afeka.shoppingcatalogservice.layout.CategoryBoundary;
import il.ac.afeka.shoppingcatalogservice.layout.ProductBoundary;
import il.ac.afeka.shoppingcatalogservice.layout.ProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class ShoppingCatalogServiceDb implements ShoppingCatalogService {
    private ProductsDao productsDao;
    private CategoryDao categoryDao;
    private ProductDetailDao productDetailDao;

    @Autowired
    public ShoppingCatalogServiceDb(ProductsDao productsDao,
                                    CategoryDao categoryDao,
                                    ProductDetailDao productDetailDao) {
        this.productsDao = productsDao;
        this.categoryDao = categoryDao;
        this.productDetailDao = productDetailDao;
    }

    @Override
    @Transactional
    public void createCategory(CategoryBoundary value) {
        CategoryEntity category = new CategoryEntity();
        category.setName(value.getName());
        category.setDescription(value.getDescription());
        categoryDao.save(category);
    }

    @Override
    @Transactional
    public ProductBoundary createProduct(ProductBoundary value) {
        ProductEntity entity = ConvertToProductEntity(value);
        CategoryEntity category = categoryDao.findById(value.getCategory().getName()).orElse(null);
        // Check category is already exist, or return 500 Error code
        if (category == null)
            throw new InternalErrorException("category must exist in the database");
        // Check if product with the same id is already exist, or return 500 error code
        ProductEntity product = productsDao.findById(value.getId()).orElse(null);
        if (product != null)
            throw new InternalErrorException("product with the same id is already exists.");
        ProductDetailsEntity productDetailsEntity = productDetailDao.save(entity.getDetails());
        entity.setDetails(productDetailsEntity);
        ProductEntity saved = productsDao.save(entity);
        return new ProductBoundary(saved.getId(),
                                saved.getName(),
                                saved.getPrice(),
                                saved.getImage(),
                                new ProductDetails(saved.getDetails().getParts(), saved.getDetails().getManufacturer(), saved.getDetails().getCollectable()),
                                new CategoryBoundary(saved.getCategory().getName(), saved.getCategory().getDescription()));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryBoundary[] searchCategories(String sortAttr, String sortOrder, int page, int size) {
        return categoryDao.
                findAll(PageRequest.of(page,
                                    size,
                                    sortOrder.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                                    sortAttr))
                .stream()
                .map(e -> new CategoryBoundary(e.getName(), e.getDescription()))
                .collect(Collectors.toList())
                .toArray(CategoryBoundary[]::new);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBoundary getProductById(String productId) {
        ProductEntity entity = productsDao.findById(productId).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Product with the requested catalog number wasn't found.");
        }

        return new ProductBoundary(entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getImage(),
                new ProductDetails(entity.getDetails().getParts(), entity.getDetails().getManufacturer(), entity.getDetails().getCollectable()),
                new CategoryBoundary(entity.getCategory().getName(), entity.getCategory().getDescription()));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBoundary[] searchProducts(String filterType, String filterValue, String sortBy, String sortOrder, int page, int size) {
        // TODO: make search products with filter as eyal describes
        return new ProductBoundary[0];
    }

    @Override
    public void delete() {
        this.categoryDao.deleteAll();
        this.productDetailDao.deleteAll();
        this.productsDao.deleteAll();
    }

    private ProductEntity ConvertToProductEntity(ProductBoundary value) {
        ProductEntity entity = new ProductEntity();

        if (value.getId() != null)
            entity.setId(value.getId());
        if (value.getName() != null)
            entity.setName(value.getName());
        entity.setPrice(value.getPrice());
        if (value.getImage() != null)
            entity.setImage(value.getImage());
        if (value.getDetails() != null) {
            entity.setDetails(ConvertToProductDetailsEntity(value.getDetails()));
        }
        if (value.getCategory() != null) {
            entity.setCategory(ConvertToCategoryEntity(value.getCategory()));
        }

        return entity;
    }

    private CategoryEntity ConvertToCategoryEntity(CategoryBoundary category) {
        CategoryEntity entity = new CategoryEntity();

        if (category.getName() != null)
            entity.setName(category.getName());
        if (category.getDescription() != null)
            entity.setDescription(category.getDescription());

        return entity;
    }

    private ProductDetailsEntity ConvertToProductDetailsEntity(ProductDetails details) {
        ProductDetailsEntity entity = new ProductDetailsEntity();

        if (details.getManufacturer() != null)
            entity.setManufacturer(details.getManufacturer());
        if (details.getParts() != null)
            entity.setParts(details.getParts());
        entity.setCollectable(details.getCollectable());

        return entity;
    }
}
