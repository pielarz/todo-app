package io.github.pielarz.todoapp.service;

import io.github.pielarz.todoapp.config.TaskConfigurationProperties;
import io.github.pielarz.todoapp.model.DTO.GroupReadModel;
import io.github.pielarz.todoapp.model.DTO.GroupWriteModel;
import io.github.pielarz.todoapp.model.TaskGroup;
import io.github.pielarz.todoapp.repository.TaskGroupRepository;
import io.github.pielarz.todoapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source) {
        TaskGroup result = repository.save(source.toGroup());
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId) {
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("Group has unfinished tasks. Do all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
        result.setDone(!result.isDone());
    }
}
