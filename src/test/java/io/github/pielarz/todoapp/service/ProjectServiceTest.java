package io.github.pielarz.todoapp.service;

import io.github.pielarz.todoapp.config.TaskConfigurationProperties;
import io.github.pielarz.todoapp.model.DTO.GroupReadModel;
import io.github.pielarz.todoapp.model.Project;
import io.github.pielarz.todoapp.model.ProjectStep;
import io.github.pielarz.todoapp.model.TaskGroup;
import io.github.pielarz.todoapp.repository.ProjectRepository;
import io.github.pielarz.todoapp.repository.TaskGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("Test should throw IllegalStateException when configured to allow just one group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_and_openGroups_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under test
        var toTest = new ProjectService(null, mockGroupRepository, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one unfinished group");

    }

    @Test
    @DisplayName("Test should throw IllegalArgumentException when configuration is ok and no projects for a given id")
    void createGroup_configurationOk_and_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, null, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("Test should throw IllegalArgumentException when configured to allow just one group and no groups and no projects for a given id")
    void createGroup_noMultipleGroupsConfig_and_noUndoneGroupExists_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("Test should create a new group from project")
    void createGroup_configurationOk_existingProjects_createAndSavesGroup() {
        //given
        var today = LocalDate.now().atStartOfDay();
        //and
        var project = projectWith("bar", Set.of(-1, -2));
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));
        //and
        InMemoryGroupRepository inMemoryGroupRepository = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepository.count();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, inMemoryGroupRepository, mockConfig);

        //when
        GroupReadModel result = toTest.createGroup(today, 1);

        //then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall + 1)
                .isEqualTo(inMemoryGroupRepository.count());


    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline) {
        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository {

        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count() {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if (entity.getId() == 0) {
                try {
                    var field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(final Integer projectId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);
        }
    }
}