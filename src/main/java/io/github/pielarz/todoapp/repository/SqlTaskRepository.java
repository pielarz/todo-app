package io.github.pielarz.todoapp.repository;

import io.github.pielarz.todoapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlTaskRepository extends TaskRepository, JpaRepository<Task, Integer> {

    @Override
    @Query(nativeQuery = true, value="select count(*) > 0 from tasks where id=:id")
    boolean existsById(Integer id);
}
