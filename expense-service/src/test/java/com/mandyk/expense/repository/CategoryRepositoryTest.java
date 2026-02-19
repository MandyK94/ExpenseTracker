package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Category category1;
    private Category category2;
    private Category categoryOtherUser;

    @BeforeEach
    void setup() {
        category1 = new Category();
        category1.setName("Travel");
        category1.setUserId(1);

        category2 = new Category();
        category2.setName("Food");
        category2.setUserId(1);

        categoryOtherUser = new Category();
        categoryOtherUser.setName("Bills");
        categoryOtherUser.setUserId(2);

        testEntityManager.persist(category1);
        testEntityManager.persist(category2);
        testEntityManager.persist(categoryOtherUser);

        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    void shouldFindCategoryByIdAndUserId() {
        Optional<Category> found = categoryRepository.findByIdAndUserId(category1.getId(), 1);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Travel");
    }

    @Test
    void shouldNotFindCategoryIfUserIdDoesNotMatch() {
        Optional<Category> found = categoryRepository.findByIdAndUserId(category1.getId(), 2);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllCategoriesForUser() {
        List<Category> categories = categoryRepository.findByUserId(1);
        assertThat(categories).hasSize(2);
        assertThat(categories)
                .extracting(Category::getName).contains("Food", "Travel");
    }

    @Test
    void shouldReturnEmptyIfUserHasNoCategories() {
        List<Category> categories = categoryRepository.findByUserId(999);
        assertThat(categories).isEmpty();
    }

    @Test
    void shouldReturnCategoriesSortedByNameAsc() {
        List<Category> categories = categoryRepository.findByUserIdSorted(1);
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getName()).isEqualTo("Food");
        assertThat(categories.get(1).getName()).isEqualTo("Travel");
    }

    @Test
    void shouldSaveCategory() {
        Category category = new Category();
        category.setName("Shopping");
        category.setUserId(1);

        Category saved = categoryRepository.save(category);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldDeleteCategory() {
        categoryRepository.deleteById(category1.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Category> result = categoryRepository.findById(category1.getId());

        assertThat(result).isEmpty();
    }

}
