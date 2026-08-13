# rest-client — Tests

Test suites for the **rest-client** module (the Thymeleaf web client that consumes the API with `RestClient` and an HTTP session).

## Running the tests

From this folder:

**Windows (CMD or PowerShell):**
```
.\mvnw.cmd test
```

**Linux / macOS:**
```
./mvnw test
```

Run a single class:

**Windows:**
```
.\mvnw.cmd test -Dtest=WorkClockControllerTests
```

**Linux / macOS:**
```
./mvnw test -Dtest=WorkClockControllerTests
```

Run a single method:

**Windows:**
```
.\mvnw.cmd test -Dtest=AuthControllerTests#loginPostSuccessStoresSessionAndRedirects
```

**Linux / macOS:**
```
./mvnw test -Dtest=AuthControllerTests#loginPostSuccessStoresSessionAndRedirects
```

**No running API or MySQL needed:** service tests use a `MockRestServiceServer` and controller tests mock with `@MockitoBean`.

## Test infrastructure

| Element                     | Description |
|------------------------------|-------------|
| `RestClientTestSupport`      | Base class for the service tests: sets up `MockRestServiceServer` + `RestClient` with the real `JwtRequestInterceptor` and utilities to simulate a session with a token. |
| `ClientRestApplicationTests` | Checks that the application context starts. |

## Summary

**Total: 86 tests** (18 suites: 9 controllers + 8 services + 1 context).

### Service tests (HTTP methods each service calls)

| Class                          | Nº | What it checks |
|--------------------------------|----|----------------|
| `JwtRequestInterceptorTests`   |  4 | The interceptor that adds the `Authorization: Bearer` header |
| `AuthServiceTests`             |  1 | Login `POST /api/auth/login` |
| `UserServiceTests`             | 10 | User CRUD and security questions |
| `TimeEntryServiceTests`        |  3 | Clock start/stop and monthly listing |
| `BreakClientServiceTests`      |  2 | Breaks start/end |
| `AdminServiceTests`            |  4 | Administration management (approve, block, delete) |
| `BackupClientServiceTests`     |  5 | Calls to the backup-service |
| `CrmServiceTests`              |  6 | Task and customer calls to the API |

### Controller tests (views, model, session and redirects)

| Class                           | Nº | What it checks |
|---------------------------------|----|----------------|
| `AuthControllerTests`           |  9 | Login, registration (2 steps), logout and root |
| `WorkClockControllerTests`      |  7 | Clock: start, pause, resume, stop, reset |
| `ProfileControllerTests`        |  1 | Profile view |
| `CalendarControllerTests`       |  3 | Monthly hours calendar |
| `MenuControllerTests`           |  1 | Main menu view |
| `AdminControllerTests`          |  8 | Admin panel: pending users and backups |
| `BackupControllerTests`         | 14 | Backup management from the client |
| `CrmControllerTests`            |  6 | CRM page and task actions |
| `GlobalModelAttributesTests`    |  1 | The `@ControllerAdvice` adds `username` to the model |

> Note: the endpoints protected by the `SessionInterceptor` (any endpoint other than `/login`, `/register`, etc.) redirect to `/login` if the session has no `userId`. That is why the controller tests include `.sessionAttr("userId", ...)`.
>
> Note: the admin panel and the user's backup view share the same `BackupClientService` (the two previous clients, `BackupApiClient` and `BackupClientService`, were unified into a single one).

## JWT header — `JwtRequestInterceptorTests`

- `addsBearerTokenWhenSessionHasJwt` — with a token in session it adds `Authorization: Bearer ...`.
- `doesNotAddHeaderWhenNoSession` — without a session it adds no header.
- `doesNotAddHeaderWhenSessionTokenIsBlank` — a blank session token adds no header.
- `interceptorCanBeUsedStandalone` — it can be used in isolation without a web context.

## Services

### `AuthServiceTests`
- `loginPostsCredentialsAndReturnsToken` — does `POST /api/auth/login` with the credentials and parses the token.

### `UserServiceTests`
- `createPostsUserAndReturnsCreated` — `POST /api/user` with the user body.
- `findAllGetsUsersWithBearerToken` — `GET /api/user` sending the session token.
- `findByIdCallsCorrectUri` — `GET /api/user/{id}`.
- `findByUsernameCallsCorrectUri` — `GET /api/user/name/{username}`.
- `existsByUsernameReturnsBoolean` — `GET /api/user/name/exist/{username}`.
- `isBlockedReturnsBoolean` — `GET /api/user/blocked/{username}`.
- `updatePutsUser` — `PUT /api/user/{id}` with the body.
- `deleteSendsDeleteRequest` — `DELETE /api/user/{id}`.
- `saveQuestionPostsFromDto` — `POST /api/userquestion/from-dto` with the answer.
- `findAllQuestionsGetsPublicQuestions` — `GET /api/questions` (public, without a token).

### `TimeEntryServiceTests`
- `startPostsStartUri` — `POST /api/time-entry/start/{userId}`.
- `stopPostsStopUri` — `POST /api/time-entry/stop/{timeEntryId}`.
- `findByMonthGetsEntriesWithQueryParams` — `GET /api/time-entry/user/{userId}?year=...&month=...`.

### `BreakClientServiceTests`
- `startPostsStartUri` — `POST /api/break/start/{timeEntryId}`.
- `endPostsEndUri` — `POST /api/break/end/{breakId}`.

### `AdminServiceTests`
- `findPendingUsersFiltersRoleThreeOrNull` — filters users with role `PENDIENTE` (id 3) or no role.
- `approvePatchesRoleToAdmin` — `PATCH` assigning the admin role.
- `blockPatchesBlockedFlag` — `PATCH` marking `blocked`.
- `deleteRemovesUserQuestionsAndUser` — first deletes the security answers and then the user.

### `BackupClientServiceTests`
- `listBackupsGetsList` — `GET /backups` (uses the backup client, without JWT).
- `createBackupPostsAndReturnsMessage` — `POST /backups`.
- `restoreBackupPostsAndReturnsBoolean` — `POST /backups/restore/{fileName}`.
- `deleteBackupDeletesAndReturnsBoolean` — `DELETE /backups/{fileName}`.
- `downloadBackupGetsResource` — `GET /backups/{fileName}`.

### `CrmServiceTests`
- `findAllTareasGetsTareas` — `GET /api/tarea` (with the bearer token).
- `findTareasByResponsableUsesQueryParam` — `GET /api/tarea?responsableId=<id>`.
- `findAllClientesGetsClientes` — `GET /api/cliente`.
- `crearTareaPostsTarea` — `POST /api/tarea` with the task body.
- `actualizarTareaPutsTarea` — `PUT /api/tarea/{id}` with the task body.
- `eliminarTareaDeletesTarea` — `DELETE /api/tarea/{id}`.

## Controllers

### `AuthControllerTests`
- `loginGetShowsLoginView` — `GET /login` shows `auth/login` with the `LoginDTO`.
- `loginPostSuccessStoresSessionAndRedirects` — correct login stores `jwt`, `userId` and `username` in the session and redirects to `/clock-in`.
- `loginPostWithApiErrorShowsErrorMessage` — API error (401) shows the message extracted from the JSON.
- `registerGetShowsRegisterView` — `GET /register` shows `auth/register`.
- `registerPostRedirectsToQuestions` — successful registration redirects to `/register/questions?id=...`.
- `registerQuestionsGetShowsSecondStep` — loads the questions and the 2nd step DTO.
- `registerQuestionsPostRedirectsToLogin` — saves the 3 answers and redirects to `/login`.
- `logoutInvalidatesSessionAndRedirects` — `POST /logout` invalidates the session and redirects to `/login`.
- `rootPathShowsLoginView` — `GET /` shows the login.

### `WorkClockControllerTests`
- `clockInGetShowsClockInViewWithSessionAttributes` — `GET /clock-in` shows `app/clock-in` with the session values.
- `startStoresEntryInSessionAndRedirects` — starts the clock and stores `timeEntryId` and `startTime`.
- `pauseStoresBreakInSessionAndRedirects` — pauses and stores `breakId`, `pauseTime` and `breakOpen=true`.
- `resumeEndsBreakAndRedirects` — resumes: closes the break, clears `breakId` and sets `breakOpen=false`.
- `resumeWithoutBreakIdOnlyClearsSession` — resuming without an active break does not fail.
- `stopEndsBreakAndTimeEntryThenRedirects` — stops: closes the break and the clock entry and stores `endTime`.
- `resetClearsAllSessionAttributes` — resets and clears all session variables.

### `ProfileControllerTests`
- `profileGetLoadsUserFromSessionAndShowsView` — loads the user by session `username` and shows `app/profile`.

### `CalendarControllerTests`
- `calendarGetWithoutParamsShowsCurrentMonth` — without parameters it uses the current month.
- `calendarGetWithYearAndMonthBuildsHoursByDay` — with `year`/`month` it builds `hoursByDay` ("2h 10m").
- `calendarGetIgnoresEntriesWithoutTotalMinutesWorked` — entries without worked minutes are ignored.

### `CrmControllerTests`
- `crmGetAsAdminShowsAllTareas` — `GET /menu/crm` as ADMIN loads all tasks.
- `crmGetAsEmployeeShowsOnlyOwnTareas` — as EMPLEADO loads the user's own tasks (`findTareasByResponsable`).
- `crmGetWithoutRoleShowsCrmView` — `GET /menu/crm` without a role still renders the CRM view.
- `crearRedirectsAndCallsService` — `POST /menu/crm/crear` creates the task and redirects.
- `editarRedirectsAndCallsService` — `POST /menu/crm/editar/{id}` updates the task and redirects.
- `eliminarRedirectsAndCallsService` — `POST /menu/crm/eliminar/{id}` deletes the task and redirects.

### `MenuControllerTests`
- `menuGetShowsBaseAppLayout` — `GET /menu` shows the base layout.

### `AdminControllerTests`
- `listUsersShowsPendingUsers` — `GET /menu/admin` adds pending users to the model.
- `createBackupCallsClientAndRedirects` — `POST /menu/admin/backup` calls the backup client.
- `downloadBackupReturnsAttachmentHeader` — `GET /menu/admin/download/{file}` returns the file with the `Content-Disposition` header.
- `restoreBackupSuccessShowsMessage` — successful restore from the admin panel adds a flash message.
- `restoreBackupFailureShowsError` — failed restore from the admin panel adds a flash error.
- `approveRedirectsAndCallsService` — approve a user and redirect.
- `blockRedirectsAndCallsService` — block a user and redirect.
- `deleteRedirectsAndCallsService` — delete a user and redirect.

### `BackupControllerTests`
- `createBackupRedirectsToList` — `POST /menu/backups/create` redirects to `/menu/backups` and shows the popup.
- `listBackupsShowsViewWithModel` — `GET /menu/backups` shows `app/backups` with the model.
- `restoreBackupSuccessShowsPopup` — successful restore adds a popup.
- `restoreBackupFailureShowsError` — failed restore adds a flash error.
- `restoreBackupWithApiErrorShowsDetailedError` — API exception shows the detail.
- `downloadBackupReturnsAttachmentHeader` — `GET /menu/backups/download/{file}` returns the file with the `Content-Disposition` header.
- `deleteBackupSuccessShowsPopup` — successful delete shows the popup.
- `deleteBackupFailureShowsError` — failed delete adds a flash error.
- `deleteBackupWithApiErrorShowsDetailedError` — API exception shows the detail.

### `GlobalModelAttributesTests`
- `addsUsernameFromSessionToEveryModel` — the `@ControllerAdvice` adds `username` to all views.
