package com.incloudlogic.taskmanager.domain.usecase

import com.incloudlogic.taskmanager.domain.entity.Task
import com.incloudlogic.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting tasks
 */
class GetTasksUseCase(private val repository: TaskRepository) {
    
    suspend fun getLocalTasks(): Flow<List<Task>> {
        return repository.getAllLocalTasks()
    }
    
    suspend fun getRemoteTasks(): Result<List<Task>> {
        return repository.getAllRemoteTasks()
    }
    
    suspend fun syncTasks(): Result<Unit> {
        return repository.syncTasks()
    }
}
