# study-aid-api

Spring Boot API that calls the Claude API as a downstream service to help students
studying **English** and **Mathematics**. Responses are forced into structured JSON
(not free-form chat) via Claude's tool-use `tool_choice` mechanism, so the API is
easy to render in any frontend.

## Design

- **Stateless**: the server holds no session state. Each request carries the full
  prior conversation (`history`) if the client wants continuity; omit it for a fresh question.
- **Structured output**: each subject has a `@JsonClassDescription`-annotated response
  class (`MathTutorResponse`, `EnglishTutorResponse`). The Anthropic Java SDK derives
  a JSON schema from that class, forces Claude to call it as a tool, and the tool's
  parsed input *is* the response — no free text parsing needed.
- **Hint levels** (1–3) control how much the tutor reveals: nudge only → partial →
  full worked answer/correction. This is a prompt-level policy in each `*TutorService`.

## Requirements

- Java 21
- An Anthropic API key

## Setup

The API key is loaded from a `.env` file that lives **outside the jar** — it's read at
startup from the current working directory (via `spring-dotenv`), never baked into the
build or committed to git (`.env` is gitignored).

```bash
# create a .env file in the directory you'll run the app from:
echo "ANTHROPIC_API_KEY=sk-ant-your-real-key" > .env
```

Run the jar/app from the same directory as `.env` (or drop `.env` alongside wherever
you deploy the jar — it does not need to be next to `pom.xml`, just next to the working
directory the process starts in). A real environment variable (`export ANTHROPIC_API_KEY=...`)
still works too and takes precedence — `.env` is just a convenient alternative for local dev.

## Run

```bash
mvn spring-boot:run
```

## Endpoints

### POST /api/tutor/math/ask

```json
{
  "studentId": "student-123",
  "question": "Solve for x: 2x + 5 = 17",
  "hintLevel": 2,
  "history": [
    { "role": "user", "content": "What is algebra?" },
    { "role": "assistant", "content": "..." }
  ]
}
```

Returns a `MathTutorResponse`: `steps`, `hint`, `finalAnswer`, `encouragement`
(fields populated according to `hintLevel`).

### POST /api/tutor/english/ask

```json
{
  "studentId": "student-123",
  "question": "She don't like apples and going to the store yesterday.",
  "hintLevel": 3
}
```

Returns an `EnglishTutorResponse`: `correctedText`, `errors[]` (`original`,
`correction`, `rule`), `explanation`, `encouragement`.

### POST /api/tutor/exercises/generate

Generates a batch of practice exercises for a class/age and subject.

```json
{
  "studentId": "student-123",
  "subject": "MATH",
  "classOrAge": "Year 5",
  "topic": "fractions",
  "count": 5,
  "difficulty": 2
}
```

Returns `ExerciseGenerationResponse`: a list of `{ exerciseId, question, type, options, topic }`.
**Correct answers are never sent to the client** — they're kept server-side (in-memory,
keyed by `exerciseId`) for grading in `/submit`.

### POST /api/tutor/exercises/submit

Submits answers to previously generated exercises for grading.

```json
{
  "studentId": "student-123",
  "answers": [
    { "exerciseId": "...", "studentAnswer": "3/4" },
    { "exerciseId": "...", "studentAnswer": "1/2" }
  ]
}
```

Returns `ExerciseSubmissionResponse`: `score`, `total`, per-question `results[]`
(with feedback), and an `improvementSuggestion`. Claude grades semantically
(accepts equivalent phrasing), not by exact string match. Each submission also
appends a `PerformanceRecord` (per subject, in-memory, keyed by `studentId`) used
by the two endpoints below.

### GET /api/tutor/progress/{studentId}/next-activity

Suggests the single next activity to focus on, based on the student's full record
across both subjects (whichever is weakest wins). Returns an `ActivitySuggestion`:
`subject`, `focusTopic`, `rationale`, `suggestedActivities[]`.

### POST /api/tutor/progress/suggest

Same idea, scoped to one subject:

```json
{ "studentId": "student-123", "subject": "ENGLISH", "classOrAge": "Year 5" }
```

If there's no record yet for that subject, it falls back to a sensible starting
suggestion for the given `classOrAge`.

## Next steps to consider

- Auth (API key or JWT per student) before exposing this beyond local use, since
  every request costs a Claude API call.
- Per-student rate limiting.
- Swap the `anthropic.model` property for a cheaper/faster model if cost becomes
  a concern for high-volume practice questions.
- If you later want server-side session memory instead of client-supplied `history`,
  the `claude-multiturn-tools` project already has a `ConversationStore` pattern to reuse.
- `ExerciseStore` and `PerformanceStore` are plain in-memory maps (agreed for now) —
  they're wiped on restart and won't work across multiple instances. Swap for
  Postgres/Redis once this needs to persist for real.
- Grading maps Claude's response back to each exercise by `exerciseId`, which the
  prompt asks Claude to echo back verbatim — reliable in practice but not
  contractually guaranteed by the model; a missing echo falls back to an
  "could not be graded automatically" result for that question rather than crashing
  the whole batch.
