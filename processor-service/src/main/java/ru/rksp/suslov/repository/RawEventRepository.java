package ru.rksp.suslov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rksp.suslov.entity.RawEvent;

@Repository
public interface RawEventRepository extends JpaRepository<RawEvent, Long> {
}
