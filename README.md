# SpringBoot-Docker



| Servicio         | Descripción                                  | Puerto                              |
---------------------------------------------------------------------------------------------------------
| `api-rest`       |       API REST + JPA/MySQL                   | 8080                                |
| `rest-client`    | Cliente Thymeleaf (interfaz)                 | 8081                                |
| `backup-service` | Backups/restauración MySQL (solo Docker)     | 8082                                |
| `mysql`          | Base de datos MySQL                          | 3307 (host) / 3306 (red interna)    |
| `nginx`          | Proxy HTTPS hacia `api-rest` y `rest-client` | 80 / 443                            |
---------------------------------------------------------------------------------------------------------
## Requisitos

- Docker Desktop (con WSL2 o backend de Windows)
- JDK 21 (solo para modo local)
- No hace falta Maven instalado: cada módulo incluye `mvnw.cmd` (Windows) / `mvnw` (Linux/macOS)

## Configuración (obligatorio)

Las credenciales se centralizan en el archivo `.env` de la raíz :

```bash
cp .env.example .env
# edita .env y cambia las contraseñas
```

> `mysql` y `backup-service` se levantan SIEMPRE en Docker (el backup necesita `mysqldump`, que no existe en Windows). `api-rest`, `rest-client` y `nginx` están marcados con el perfil `full` de docker-compose, activado por defecto con `COMPOSE_PROFILES=full` en el `.env`.

## Modo A: Todo en Docker

```bash
# Construir imágenes y levantar todo
docker compose up -d --build

# Comprobar estado (todos deben estar healthy)
docker compose ps

# Parar todo
docker compose down

# Ver logs de un servicio
docker compose logs -f rest-client
```

Acceso:

- Web: `https://127.0.0.1` (nginx, HTTPS con certificado autofirmado)
- API directa: `http://localhost:8080`
- Swagger API: `http://localhost:8080/swagger-ui.html`

## Modo B: Local (híbrido)

Idea: `mysql` y `backup-service` siguen en Docker; `api-rest` y `rest-client` corren desde el IDE o desde los jars. Las apps locales leen el `.env` y usan `localhost:3307`, `localhost:8080` y `localhost:8082`.

1. Levantar solo los servicios de infraestructura:

   ```bash
   docker compose up -d mysql backup-service
   ```

   > Con `COMPOSE_PROFILES=full` en el `.env`, un `docker compose up -d` simple levantaría también las apps. Para correrlas en local indica los servicios explícitamente, o páralos tras levantarlos (paso 2).

2. **IMPORTANTE:** si algún contenedor de `api-rest`, `rest-client` o `nginx` sigue arriba, páralo para liberar puertos:

   ```bash
   docker compose stop api-rest rest-client nginx
   ```

3. Arrancar las apps desde el IDE (STS/IntelliJ) con el perfil `local` (es el perfil por defecto), o desde jars:

   ```bash
   # api-rest (puerto 8080)
   cd api-rest && ./mvnw.cmd spring-boot:run

   # rest-client (puerto 8081) - en otra terminal
   cd rest-client && ./mvnw.cmd spring-boot:run
   ```

   Acceso local: `http://localhost:8081` (rest-client) y `http://localhost:8080` (api-rest).

> **Modo híbrido:** también puedes mezclar. Por ejemplo, levantar todo en Docker y luego ejecutar solo `api-rest` en local: los contenedores usan `host.docker.internal`, así que las llamadas entre contenedores y apps locales funcionan sin tocar configuración.

## Build de los jars

```bash
cd api-rest && ./mvnw.cmd clean package -DskipTests
cd rest-client && ./mvnw.cmd clean package -DskipTests
cd backup-service && ./mvnw.cmd clean package -DskipTests
```

> Si cambias código y quieres verlo en Docker, hay que reconstruir el jar Y la imagen:
> `docker compose up -d --build`

## Backups

- Crear/restaurar backups desde la web: `https://127.0.0.1/backups`
- Los archivos `.sql.gz` se guardan en el volumen Docker `backups` (`/backup` dentro del contenedor).
- API directa del backup-service: `http://localhost:8082`

