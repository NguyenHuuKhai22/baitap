package com.huu.khai.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;

    private String avatar;
    @Transient
    private MultipartFile avatarFile; // thêm trường này
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}