package backend.Model;

import jakarta.persistence.*;
import lombok.Data;


    @Entity
    @Data
    public class Category {

        @Id
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "category_seq"
        )
        @SequenceGenerator(
                name = "category_seq",
                sequenceName = "category_seq",
                allocationSize = 1
        )
        private Long id;

        private String name;
    }

