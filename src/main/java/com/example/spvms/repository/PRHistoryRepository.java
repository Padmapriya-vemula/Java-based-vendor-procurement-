package com.example.spvms.repository;

import com.example.spvms.model.PRHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PRHistoryRepository extends JpaRepository<PRHistory, Long> {
    List<PRHistory> findByPrIdOrderByCreatedAtDesc(Long prId);
}
