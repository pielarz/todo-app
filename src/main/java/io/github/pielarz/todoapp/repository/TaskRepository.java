package io.github.pielarz.todoapp.repository;

import io.github.pielarz.todoapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;


@RepositoryRestResource
public interface TaskRepository extends JpaRepository<Task, Integer> {


    @Override
    @RestResource(exported = false)
    void deleteById(Integer integer);


    @Override
    @RestResource(exported = false)
    void delete(Task entity);

    @RestResource(path = "done", rel = "done")
    List<Task> findByDone(@Param("state") boolean done);

    //localhost:8080/tasks/search/done&desc?state=false&state2=testing
    @RestResource(path = "done&desc", rel = "done&desc")
    List<Task> findByDoneAndDescription(@Param("state") boolean done, @Param("state2") String description);
}
