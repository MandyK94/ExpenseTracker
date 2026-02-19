package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByIdAndUserId(Integer id, Integer userId);

    List<Category> findByUserId(Integer userId);

    @Query("""
            select c from Category c 
            where c.userId=:userId
            order by c.name ASC
            """)
    List<Category> findByUserIdSorted(Integer userId);
}
