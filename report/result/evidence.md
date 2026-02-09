# Evidence for Thesis Materials

## Codebase Facts

- Controllers: 22 (`src/main/java/com/hbnu/zen/controller`)
- Services: 18 (`src/main/java/com/hbnu/zen/service`)
- Frontend views: 35 (`zen-ui/src/views`)
- API endpoints: 84 (mapping annotation scan)
- Core tables: 18 (`src/main/resources/schema.sql`)

## Build Verification

- Backend command: `mvn -DskipTests compile` -> BUILD SUCCESS
- Frontend command: `cd zen-ui && npm run build` -> build success

## Implemented Highlights

- Distributed locking across all booking domains.
- Rule configurability through `sys_config`.
- Template-based in-site messaging.
- Real-time seat status via WebSocket `/topic/seat-status`.
