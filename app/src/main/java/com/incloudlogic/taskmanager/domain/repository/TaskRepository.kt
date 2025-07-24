package com.incloudlogic.taskmanager.domain.repository

import com.incloudlogic.taskmanager.domain.entity.Task
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository interface for Task operations
 */
interface TaskRepository {
    
    // Local operations
    suspend fun getAllLocalTasks(): Flow<List<Task>>
    suspend fun getLocalTaskById(id: UUID): Task?
    suspend fun insertLocalTask(task: Task)
    suspend fun updateLocalTask(task: Task)
    suspend fun deleteLocalTask(id: UUID)
    
    // Remote operations
    suspend fun getAllRemoteTasks(): Result<List<Task>>
    suspend fun getRemoteTaskById(id: String): Result<Task>
    suspend fun createRemoteTask(task: Task): Result<Task>
    suspend fun updateRemoteTask(task: Task): Result<Task>
    suspend fun deleteRemoteTask(id: String): Result<Unit>
    
    // Sync operations
    suspend fun syncTasks(): Result<Unit>
}
