# UrbanIssues (CivicPulse)

> **"A containerised, role-secured civic triage platform for neighbourhood issue reporting — built on AWS, Kubernetes, Docker, Spring Boot, and OpenStreetMap."**

## What is this?

UrbanIssues is a cloud-native full-stack web application that allows residents to report neighbourhood civic issues (potholes, broken streetlights, garbage overflow, etc.) and track them through to resolution. Administrators can triage, verify, and update issue statuses in real time.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML5 / CSS3 / Vanilla JS |
| Map | Leaflet.js + OpenStreetMap |
| Backend | Spring Boot 3 (Java 17) |
| Security | Spring Security + JWT |
| Database | PostgreSQL 15 |
| Containers | Docker (multi-stage build) |
| Orchestration | Kubernetes (Minikube → AWS EKS) |
| CI/CD | GitHub Actions |
| Cloud Registry | AWS ECR |
| Rate Limiting | Bucket4j |

---

## Project Structure

```
UrbanIssues/
├── frontend/          # Nginx + HTML/CSS/JS
├── backend/           # Spring Boot 3 REST API
├── k8s/               # Kubernetes manifests
├── .github/workflows/ # CI/CD pipeline
└── docker-compose.yml # Local development
```

---

## Quick Start (Local Development)

### Prerequisites
- Docker Desktop installed and running
- Java 17+ (for running backend outside Docker)

### Run everything with Docker Compose

```bash
# Clone the repo
git clone https://github.com/bhuvii2005/UrbanIssues-web-app.git
cd UrbanIssues-web-app

# Create a .env file with your secrets
echo "DB_PASSWORD=yourpassword" > .env
echo "JWT_SECRET=your-256-bit-secret-key-here" >> .env

# Build and start all services
docker-compose up --build
```

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL**: localhost:5432

---

## User Roles

| Role | Permissions |
|---|---|
| **Guest** | View map + issue list |
| **Resident** | + Submit issues, upvote |
| **Admin** | + Change statuses, delete reports, ban users |

---

## API Overview

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | None | Create account |
| POST | `/auth/login` | None | Get JWT token |
| GET | `/api/issues` | None | List all issues |
| POST | `/api/issues` | Resident | Submit issue |
| POST | `/api/issues/{id}/upvote` | Resident | Upvote |
| PATCH | `/api/issues/{id}/status` | Admin | Update status |
| DELETE | `/api/issues/{id}` | Admin | Delete report |
| GET | `/api/admin/users` | Admin | List all users |
| PATCH | `/api/admin/users/{id}/ban` | Admin | Ban/unban user |

---

## Milestones

- [x] M1 — Project scaffold + Git init
- [ ] M2 — Spring Boot models, repos, DTOs
- [ ] M3 — JWT security + Auth API
- [ ] M4 — Issue, Upvote & Admin APIs
- [ ] M5 — Frontend (Leaflet map + all pages)
- [ ] M6 — Docker (multi-stage build + compose)
- [ ] M7 — Kubernetes manifests
- [ ] M8 — GitHub Actions CI/CD pipeline

---

## License

MIT
