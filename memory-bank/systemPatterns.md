# System Patterns

## System Architecture
- Android app structured with MVVM and repository pattern.
- Separation of concerns between UI, domain, and data layers.

## Key Technical Decisions
- Kotlin as the primary language.
- Use of Room for local database management.
- Retrofit for network operations.

## Design Patterns in Use
- MVVM (Model-View-ViewModel)
- Repository pattern
- Data Mapper pattern

## Component Relationships
- Activities interact with ViewModels.
- ViewModels communicate with repositories.
- Repositories manage data sources (local and remote).

## Critical Implementation Paths
- Task creation, editing, deletion, and completion flows.
- User authentication and session management.

---
_Last updated: 2025-07-21_
