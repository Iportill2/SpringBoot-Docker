# api-rest — Tests

Test suites for the **api-rest** module (the REST API protected with JWT).

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
.\mvnw.cmd test -Dtest=UserControllerTests
```

**Linux / macOS:**
```
./mvnw test -Dtest=UserControllerTests
```

Run a single method:

**Windows:**
```
.\mvnw.cmd test -Dtest=UserControllerTests#deleteUserRemovesIt
```

**Linux / macOS:**
```
./mvnw test -Dtest=UserControllerTests#deleteUserRemovesIt
```

**No MySQL or Docker needed:** the tests use an **in-memory H2** database and a role seed, defined in `src/test/resources/application-test.yml`.

> The test profile disables the production `data.sql` (`spring.sql.init.mode: never`); the tests seed their own roles with `@Sql`.

## Test infrastructure

| Element                  | Description |
|--------------------------|-------------|
| `ApiRestTest`            | Composite annotation: `@SpringBootTest` + `@AutoConfigureMockMvc` + `test` profile + `@Transactional` + role seed (`EMPLEADO`, `ADMIN`, `PENDIENTE`). Used by all the suites. |
| `ApiRestApplicationTests`| Checks that the application context starts with the test profile. |

## Summary

**Total: 61 tests.**

| Class                          | Nº tests | What it checks |
|--------------------------------|----------|----------------|
| `SecurityHttpTests`            |        4 | JWT access control |
| `AuthHttpTests`                |        7 | API login |
| `UserControllerTests`          |       12 | User CRUD |
| `TimeEntryControllerTests`     |        6 | Clock in/out |
| `BreakControllerTests`         |        4 | Break management |
| `QuestionsControllerTests`     |        6 | Security questions CRUD |
| `UserQuestionControllerTests`  |       10 | Each user's security answers |
| `RolesControllerTests`         |        7 | Role management |
| `JwtAuthFlowTests`             |        4 | Real token generation and validation flow |
| `ApiRestApplicationTests`      |        1 | Application context startup |

## Security (JWT) — `SecurityHttpTests`

- `protectedEndpointWithoutTokenReturns401` — a protected endpoint without a token returns **401**.
- `protectedEndpointWithInvalidTokenReturns401` — an invalid token returns **401**.
- `protectedEndpointWithValidTokenReturns200` — a valid token grants access with **200**.
- `publicEndpointWithoutTokenReturns200` — a public endpoint (registration) works without a token.

## Login — `AuthHttpTests`

- `loginWithValidCredentialsReturnsToken` — valid credentials return a token and user data.
- `loginWithWrongPasswordReturns401` — wrong password → **401**.
- `loginWithBlockedUserReturns403` — blocked user → **403**.
- `loginWithBannedUserReturns403` — banned user → **403**.
- `loginWithPendingRoleReturns403` — user with role `PENDIENTE` → **403**.
- `loginWithBlankFieldsReturns400` — blank fields → **400**.
- `loginWithUnknownUserReturns401` — unknown user → **401**.

## Users — `UserControllerTests`

- `createUserIsPublicAndReturns201` — `POST /api/user` is public and creates with **201**.
- `findAllReturnsUsersWithToken` — `GET /api/user` returns the list with a token.
- `findByIdReturnsUser` — `GET /api/user/{id}` returns the user.
- `findByIdWithUnknownIdReturns404` — unknown id → **404**.
- `findByIdWithInvalidIdReturns400` — non-numeric id → **400**.
- `findByEmailAndUsernameReturnUser` — searches by email and by username.
- `blockedAndBannedEndpointsReturnBooleans` — `blocked` and `banned` return booleans.
- `patchApproveRoleUpdatesUser` — `PATCH` with role `ADMIN` updates the role.
- `patchUpdateFieldsModifiesUser` — `PATCH` updates other fields.
- `patchWithUnknownIdReturns404` — `PATCH` on an unknown id → **404**.
- `deleteUserRemovesIt` — `DELETE` removes the user.
- `deleteWithUnknownIdReturns404` — `DELETE` on an unknown id → **404**.

## Clock in — `TimeEntryControllerTests`

- `startEntryReturns200WithStartTime` — starting the clock returns the entry with the start time.
- `startEntryWithUnknownUserReturns400` — unknown user → **400**.
- `stopEntryReturns200WithEndTime` — closing the entry returns the end time.
- `stopUnknownEntryReturns400` — unknown entry → **400**.
- `findByMonthReturnsEntries` — list the user's entries for a month.
- `findByMonthWithUnknownUserReturnsEmptyList` — a user without entries returns an empty list.

## Breaks — `BreakControllerTests`

- `startBreakReturns200WithStartTime` — starting a break returns the created break.
- `startBreakWithUnknownEntryReturns400` — unknown entry → **400**.
- `endBreakReturns200WithEndTime` — ending a break returns the end time.
- `endBreakWithUnknownIdReturns400` — unknown break → **400**.

## Security questions — `QuestionsControllerTests`

- `findAllIsPublicAndReturnsQuestions` — `GET /api/questions` is public.
- `findByIdReturnsQuestion` — get a question by id.
- `createRequiresAuthAndReturns200` — creating a question requires a token.
- `createWithoutTokenReturns401` — creating without a token → **401**.
- `updateQuestionReturns200` — update a question.
- `deleteQuestionReturns200` — delete a question.

## Security answers — `UserQuestionControllerTests`

- `createFromDtoIsPublicAndReturns201` — `POST /api/userquestion/from-dto` is public.
- `createFromDtoWithUnknownUserReturns400` — unknown user → **400**.
- `createReturns200` — direct saving of an answer.
- `findAllRequiresAuth` — listing all requires a token.
- `findByUserReturnsQuestions` — list a user's answers.
- `checkAnswerReturnsTrueForCorrect` — correct answer check → `true`.
- `checkAnswerReturnsFalseForWrong` — wrong answer → `false`.
- `checkAnswerWithUnknownUserReturns404` — unknown user → **404**.
- `findByIdReturns200` — get an answer by id.
- `deleteReturns200` — delete an answer.

## Roles — `RolesControllerTests`

- `findAllReturnsSeededRoles` — returns the seeded roles (compares them without a fixed order).
- `findAllWithoutTokenReturns401` — listing roles without a token → **401**.
- `findByIdReturnsRole` — role by id.
- `findByIdWithUnknownIdReturns404` — unknown id → **404**.
- `findByNameReturnsRole` — role by name.
- `findByNameWithUnknownNameReturns404` — unknown name → **404**.
- `createAndDeleteRole` — create a role and then delete it.

## JWT flow — `JwtAuthFlowTests`

- `jwtTokenRoundTrip` — a generated token is validated correctly.
- `loginReturnsTokenForValidCredentials` — correct login issues a token.
- `loginRejectsWrongPassword` — wrong password → 401.
- `loginRejectsNotApprovedRole` — non-approved role → 403.
