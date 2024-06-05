package com.huu.khai.Controller;
import com.huu.khai.model.Product;
import com.huu.khai.service.CategoryService;
import com.huu.khai.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject

// Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
                             @RequestParam("avatarFile") MultipartFile avatarFile) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }

        if (!avatarFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(avatarFile.getOriginalFilename());
            product.setAvatar(fileName);

            // Lưu ảnh vào thư mục lưu trữ (ví dụ: trong thư mục resources/static/images/)
            String uploadDir = "src/main/resources/static/images/";
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = avatarFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Could not save avatar image: " + e.getMessage());
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + e.getMessage());
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("avatarFile") MultipartFile avatarFile) {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            return "/products/update-product";
        }

        // Xử lý tải lên hình ảnh mới nếu có
        if (!avatarFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(avatarFile.getOriginalFilename());
            product.setAvatar(fileName);
            // Lưu hình ảnh vào thư mục lưu trữ
            String uploadDir = "src/main/resources/static/images/";
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = avatarFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Could not save avatar image: " + e.getMessage());
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + e.getMessage());
            }
        }

        // Sau khi xử lý hình ảnh, cập nhật thông tin sản phẩm
        productService.updateProduct(product);
        return "redirect:/products";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}