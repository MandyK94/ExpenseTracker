package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryRepository, Integer> {

    Optional<Category> findByIs(Integer id);
}
