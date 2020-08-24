package io.github.pielarz.todoapp.controller;

import io.github.pielarz.todoapp.model.Task;
import io.github.pielarz.todoapp.repository.SqlTaskRepository;
import io.github.pielarz.todoapp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Controller
public class TaskController {
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;

    public TaskController(SqlTaskRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/tasks", params = {"!sort", "!page", "!size"}, method = RequestMethod.GET)
    ResponseEntity<List<Task>> readAllTasks() {
        logger.warn("Exposing all the tasks!");
        return ResponseEntity.ok(repository.findAll());
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    ResponseEntity<List<Task>> readAllTasks(Pageable pageable) {
        logger.info("Custom pageable.");
        return ResponseEntity.ok(repository.findAll(pageable).getContent());
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    ResponseEntity<Task> readById(@PathVariable Integer id, @RequestBody Task task){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findById(id).get());
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    ResponseEntity<Task> createTask(@RequestBody Task task) throws URISyntaxException {
        repository.save(task);
        return ResponseEntity.created(new URI("localhost:8080/tasks")).build();
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT)
    ResponseEntity<Task> updateTask(@PathVariable Integer id, @RequestBody Task task){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        task.setId(id);
        repository.save(task);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Task> deleteTask(@PathVariable Integer id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional  //METHOD HAS TO BE PUBLIC!! Otherwise no changes occur.
    @RequestMapping(value="/tasks/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Task> toggleTask(@PathVariable Integer id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }
}
