package io.github.pielarz.todoapp.service;

import io.github.pielarz.todoapp.config.TaskConfigurationProperties;
import io.github.pielarz.todoapp.model.DTO.GroupReadModel;
import io.github.pielarz.todoapp.model.Project;
import io.github.pielarz.todoapp.model.Task;
import io.github.pielarz.todoapp.model.TaskGroup;
import io.github.pielarz.todoapp.repository.ProjectRepository;
import io.github.pielarz.todoapp.repository.TaskGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(final Project toSave) {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one unfinished group from project is allowed");
        }
        TaskGroup result = repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new TaskGroup();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(project.getSteps().stream()
                            .map(projectStep -> new Task(projectStep.getDescription(), deadline.plusDays(projectStep.getDaysToDeadline())))
                            .collect(Collectors.toSet())
                    );
                    return targetGroup;
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return new GroupReadModel(result);
    }
}
