# SpringBoot-Docker



| Service            | Description                           | Port                            |
| ------------------ | ------------------------------------- | ------------------------------- |
| `api-rest`         | REST API + JPA/MySQL                  | 8080                            |
| `rest-client`      | Thymeleaf client (UI)                 | 8081                            |
| `backup-service`   | MySQL backups/restore (Docker only)   | 8082                            |
| `mysql`            | MySQL database                        | 3307 (host) / 3306 (internal)   |
| `nginx`            | HTTPS proxy to `api-rest` and `rest-client` | 80 / 443                  |

## Requirements

- Docker Desktop (with WSL2 or Windows backend)
- JDK 21 (local mode only)
- No Maven installation needed: each module includes `mvnw.cmd` (Windows) / `mvnw` (Linux/macOS)

## Configuration (required)

Credentials are centralized in the root `.env` file:

```bash
cp .env.example .env
# edit .env and change the passwords
```

> `mysql` and `backup-service` always run in Docker (backups need `mysqldump`, which is not available on Windows). `api-rest`, `rest-client` and `nginx` are marked with the `full` docker-compose profile, enabled by default with `COMPOSE_PROFILES=full` in `.env`.

## Mode A: Everything in Docker

```bash
# Build images and start everything
docker compose up -d --build

# Check status (all should be healthy)
docker compose ps

# Stop everything
docker compose down

# View logs of a service
docker compose logs -f rest-client
```

Access:

- Web: `https://127.0.0.1` (nginx, HTTPS with self-signed certificate)
- Direct API: `http://localhost:8080`
- Swagger API: `http://localhost:8080/swagger-ui.html`

## HTTPS access from other devices (nginx + self-signed certificate)

nginx serves the app over HTTPS on ports `80` (redirects to 443) and `443`, proxying to `api-rest` and `rest-client`. The `server_name` and the self-signed certificate are both generated dynamically from the `SERVER_IP` variable in the root `.env`.

**Which IP to use?** Your machine's IP on the active adapter (Wi-Fi or Ethernet). Get it with:

```bash
ipconfig
```

Look for **IPv4 Address** (`Dirección IPv4`) in the active adapter (Wi-Fi or Ethernet). **Do NOT** use the **Default Gateway** (`Puerta de enlace predeterminada`): that is the router's IP, not your machine's.

**To access from another device on the same network** (e.g. `https://<SERVER_IP>`), or after changing network/adapter/computer:

1. Edit `SERVER_IP` in `.env` to the machine's current IP.
2. Recreate nginx so it regenerates the configuration and the certificate:

   ```bash
   docker compose up -d --build --force-recreate nginx
   ```

Because the certificate is self-signed and bound to that IP, the browser will show a privacy/security warning. It is expected: proceed/continue to access. No changes are needed to access from `https://127.0.0.1` locally.

## JWT Authentication

The API (`api-rest`) is protected with Spring Security + JWT (stateless, CSRF disabled):

- `POST /api/auth/login` with `{"username": "...", "pass": "..."}` returns a `token` (and user data). The token expires after 24h by default.
- All other endpoints require the `Authorization: Bearer <token>` header.
- Public endpoints (2-step registration): `POST /api/user`, `GET /api/user/name/exist/{username}`, `GET /api/user/email/exist/{email}`, `GET /api/questions`, `POST /api/userquestion/from-dto`.
- Configuration in `.env`: `JWT_SECRET` (signing key, at least 32 bytes) and `JWT_EXPIRATION_MS`.
- The client (`rest-client`) stores the token in the web session after login and automatically forwards it on every API call through an interceptor.

## Mode B: Local (hybrid)

Idea: `mysql` and `backup-service` stay in Docker; `api-rest` and `rest-client` run from the IDE or from the jars. The local apps read `.env` and use `localhost:3307`, `localhost:8080` and `localhost:8082`.

1. Start only the infrastructure services:

   ```bash
   docker compose up -d mysql backup-service
   ```

   > With `COMPOSE_PROFILES=full` in `.env`, a plain `docker compose up -d` would also start the apps. To run them locally, name the services explicitly, or stop them after starting them (step 2).

2. **IMPORTANT:** if any `api-rest`, `rest-client` or `nginx` container is still running, stop it to free the ports:

   ```bash
   docker compose stop api-rest rest-client nginx
   ```

3. Start the apps from the IDE (STS/IntelliJ) with the `local` profile (the default profile), or from jars:

   ```bash
   # api-rest (port 8080)
   cd api-rest && ./mvnw.cmd spring-boot:run

   # rest-client (port 8081) - in another terminal
   cd rest-client && ./mvnw.cmd spring-boot:run
   ```

   Local access: `http://localhost:8081` (rest-client) and `http://localhost:8080` (api-rest).

> **Hybrid mode:** you can also mix. For example, start everything in Docker and then run only `api-rest` locally: the containers use `host.docker.internal`, so calls between containers and local apps work without touching configuration.

## Building the jars

```bash
cd api-rest && ./mvnw.cmd clean package -DskipTests
cd rest-client && ./mvnw.cmd clean package -DskipTests
cd backup-service && ./mvnw.cmd clean package -DskipTests
```

> If you change code and want to see it in Docker, you must rebuild the jar AND the image:
> `docker compose up -d --build`

## Backups

- Create/restore backups from the web: `https://127.0.0.1/backups`
- The `.sql.gz` files are stored in the Docker volume `backups` (`/backup` inside the container).
- Direct backup-service API: `http://localhost:8082`

## CRM (tasks)

The `rest-client` exposes a task management page at `/menu/crm`, backed by two new API resources in `api-rest`:

- `GET/POST /api/cliente` and `GET/PUT/DELETE /api/cliente/{id}` — customers.
- `GET/POST /api/tarea` and `GET/PUT/DELETE /api/tarea/{id}` — tasks, with query filters:
  - `?responsableId=<id>` — tasks assigned to a user.
  - `?sinAsignar=true` — tasks with no responsible (the pool).
  - `?clienteId=<id>` and `?estado=<PENDIENTE|EN_CURSO|COMPLETADA>`.
- `POST /api/tarea/{id}/asignar/{userId}` — assign a task to a user.
- `PUT /api/tarea/{id}/horas` — update the `horasEmpleadas` field.

Behaviour by role on `/menu/crm`:

- **ADMIN:** sees all tasks and can create, edit and delete them.
- **EMPLEADO:** sees their own tasks plus the pool of unassigned tasks, can assign themselves a pool task and record their worked hours.

## Initial data (seed)

The database is created automatically the first time `api-rest` starts (`createDatabaseIfNotExist` + `ddl-auto: update`) and `api-rest/src/main/resources/data.sql` runs on every startup idempotently (`INSERT IGNORE`), seeding **20 sample rows per table** (except `roles`, kept at 3):

- **Roles:** `EMPLEADO` (1), `ADMIN` (2) and `PENDIENTE` (3).
- **Security questions:** 20 default questions for the 2-step registration.
- **Users:** 20 (the `admin`/`admin123` administrator, a pending `nuevo_usuario`, and employees `carlos`, `lucia`, ..., `sofia`, all with password `123456`).
- **Customers:** 20 sample clients (Empresa Alpha, Bodegas del Sol, ...).
- **Tasks:** 20 tasks with a mix of states and priorities, some without a responsible (the pool).
- **Time entries, breaks and user security answers:** 20 rows each, referencing the seeded users.

> If you drop the database (e.g. `DROP DATABASE aplicacion` from MySQL Workbench), simply restart `api-rest`: the DB, the tables and the sample data are recreated on their own. `INSERT IGNORE` never overwrites existing rows, so manual changes survive restarts. To change the default admin credentials, edit `data.sql`.
