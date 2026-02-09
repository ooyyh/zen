# Zen Campus Integrated Service System: Design and Implementation

## Abstract

This paper presents Zen Campus, an integrated campus service system for classroom reservations, lecture registrations, equipment borrowing, campus bus booking, and study-room seat reservations. The backend is built with Spring Boot, MyBatis, MySQL, Redis, and Redisson. The frontend is built with Vue 3 and Vite, with ECharts for dashboards and STOMP WebSocket for real-time seat status updates. The system supports three roles: student, teacher, and administrator.

The project focuses on two engineering goals: (1) strong concurrency control for resource booking and (2) runtime configurability for business rules and message templates. Distributed locks are implemented across all high-contention booking domains, and reservation rules are maintained through admin configuration APIs.

Keywords: campus service system, distributed lock, WebSocket, RBAC, Spring Boot, Vue 3

## 1 Introduction

### 1.1 Background

University service workflows are often split across multiple systems. This causes fragmented user experience, duplicated approval work, and inconsistent booking rules.

### 1.2 Objectives

- Build one integrated service portal.
- Support student/teacher/admin role permissions.
- Implement complete booking workflows with approval and messaging.
- Ensure system build and deployment readiness.

## 2 Requirement Analysis

### 2.1 Functional Requirements

- Local account login/register with JWT authentication.
- Classroom reservation and approval workflow.
- Lecture publish, signup, waitlist, and check-in.
- Equipment borrowing, approval, rejection, and return.
- Campus bus route/trip booking with waitlist promotion.
- Study-room seat reservation with real-time status sync.
- In-site message center and template-based notifications.
- Admin dashboards and report aggregation.

### 2.2 Non-Functional Requirements

- Concurrency safety for booking resources.
- Configurable reservation rules.
- Role-based access control.
- Maintainable layered architecture.

## 3 System Design

### 3.1 Architecture

The system uses a frontend-backend separation architecture:

- Frontend: Vue 3 + Router + ECharts + STOMP client.
- Backend: Spring Boot with Controller/Service/Mapper layers.
- Data: MySQL for persistence, Redis for locks and temporary states.
- Communication: REST APIs and WebSocket.

### 3.2 Core Modules

- Authentication and RBAC
- Classroom reservation and approval
- Lecture management and registration
- Equipment borrowing workflow
- Bus booking and waitlist promotion
- Study-room seat reservation and real-time sync
- Notification templates and message center
- Admin operations and reporting

### 3.3 Database Design

The current schema includes 18 core tables:

`user`, `classroom`, `reservation`, `approval_task`, `sys_config`, `message_template`, `message`, `lecture`, `lecture_signup`, `lecture_checkin`, `equipment`, `equipment_borrow`, `bus_route`, `bus_trip`, `bus_booking`, `study_room`, `seat`, `seat_reservation`.

## 4 System Implementation

### 4.1 Backend Implementation

The backend currently contains 22 controllers, 18 services, and 84 API endpoints. All booking domains use Redisson-based distributed locks for high-contention operations.

### 4.2 Frontend Implementation

The frontend contains 35 views and a grouped admin sidebar. Dashboard and report pages use ECharts for real data visualization.

### 4.3 Key Engineering Features

- Distributed lock keys by domain: `lock:classroom:*`, `lock:lecture:*`, `lock:equipment:*`, `lock:bus:trip:*`, `lock:seat:*`.
- Configurable reservation rules through `sys_config` and admin APIs.
- Template-based message dispatch to in-site inbox.
- Real-time seat status broadcasts via `/topic/seat-status`.

## 5 Testing and Verification

### 5.1 Build Verification

- Backend: `mvn -DskipTests compile` passed (BUILD SUCCESS).
- Frontend: `npm run build` passed in `zen-ui`.

### 5.2 Functional Coverage

The implemented flows cover booking creation, cancellation, approval transitions, waitlist promotion, check-in, and in-site messaging.

## 6 Deployment Design

- Frontend static assets served by Nginx.
- Spring Boot service as core application node(s).
- MySQL database `zen`.
- Redis for lock and temporary state handling.
- WebSocket endpoint for seat-state synchronization.

## 7 Conclusion and Future Work

Zen Campus provides a complete integrated workflow for campus services with practical concurrency control and admin configurability. Future work includes SSO integration, stronger automated tests, and deeper observability.
