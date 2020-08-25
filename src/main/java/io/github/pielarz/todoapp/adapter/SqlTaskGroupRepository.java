package io.github.pielarz.todoapp.repository;

import io.github.pielarz.todoapp.model.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SqlTaskGroupRepository extends TaskGroupRepository, JpaRepository<TaskGroup, Integer> {

    @Override
    List<TaskGroup> findAll();

    @Override
    Optional<TaskGroup> findById(Integer id);

    @Override
    TaskGroup save(TaskGroup entity);
}
