# Proposal: Zen Campus Integrated Service System

## 1 Topic

Zen Campus Integrated Service System Design and Implementation

## 2 Problem Statement

Campus service workflows are fragmented across multiple tools. Booking conflicts and approval inefficiencies become critical in high-traffic periods.

## 3 Goals

- Provide one unified service portal.
- Support student/teacher/admin role model.
- Build complete workflows for classroom, lecture, equipment, bus, and seat booking.
- Ensure robust concurrency safety using distributed locks.

## 4 Technical Route

- Backend: Spring Boot + MyBatis + MySQL
- Concurrency: Redis + Redisson locks
- Frontend: Vue 3 + Vite + ECharts
- Real-time: WebSocket (STOMP)
- Security: JWT with interceptor-based authentication

## 5 Feasibility

The selected stack is mature and already integrated in the current codebase. Core modules are implemented and build successfully.

## 6 Plan

- Week 1: requirement and architecture finalization
- Week 2-4: core module completion
- Week 5-6: admin/report optimization and integration testing
- Week 7: documentation and defense materials

## 7 Expected Deliverables

- Running full-stack system
- Schema/API/design documents
- Thesis, proposal, task book, and architecture diagrams
