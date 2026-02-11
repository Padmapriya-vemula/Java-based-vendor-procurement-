package com.example.spvms.repository;

import com.example.spvms.model.POHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface POHistoryRepository extends JpaRepository<POHistory, Long> {
    List<POHistory> findByPoIdOrderByCreatedAtDesc(Long poId);
}
