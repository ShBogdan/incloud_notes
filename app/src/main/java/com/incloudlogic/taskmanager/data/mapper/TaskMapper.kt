package com.incloudlogic.taskmanager.data.mapper

import com.incloudlogic.taskmanager.data.dto.TaskDto
import com.incloudlogic.taskmanager.domain.entity.Task
import java.util.UUID

/**
 * Mapper to convert between DTOs and domain entities
 */
object TaskMapper {
    
    fun mapToDomain(dto: TaskDto): Task {
        return Task(
            id = UUID.fromString(dto.id),
            title = dto.title,
            content = dto.content,
            priority = dto.priority,
            isCompleted = dto.isCompleted,
            isLocal = false // Remote tasks are not local
        )
    }
    
    fun mapToDto(domain: Task): TaskDto {
        return TaskDto(
            id = domain.id.toString(),
            title = domain.title,
            content = domain.content,
            priority = domain.priority,
            isCompleted = domain.isCompleted
        )
    }
    
    fun mapToDomainList(dtoList: List<TaskDto>): List<Task> {
        return dtoList.map { mapToDomain(it) }
    }
    
    fun mapToDtoList(domainList: List<Task>): List<TaskDto> {
        return domainList.map { mapToDto(it) }
    }
}
