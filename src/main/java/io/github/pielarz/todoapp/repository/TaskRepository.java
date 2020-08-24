package io.github.pielarz.todoapp.repository;

import io.github.pielarz.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource
public interface TaskRepository {

    List<Task> findByDone(@Param("state") boolean done);

    boolean existsById(Integer id);

    List<Task> findAll();

    Page<Task> findAll(Pageable page);

    Task save(Task entity);

    Optional<Task> findById(Integer id);

    void deleteById(Integer id);
}
