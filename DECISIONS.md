# TALA Project Decisions

## Phase 0 Decisions

1. **Language and Platform**: Kotlin, Android 7.0+ minSdk 24, compileSdk 34
   - Rationale: Stable, widely supported
   - Alternative considered: Java (rejected for kotlinx.coroutines, Hilt, Compose)

2. **Build System**: Gradle Kotlin DSL with version catalog
   - Rationale: Modern, maintainable dependency management
   - Alternative: Groovy DSL (chosen Kotlin for consistency with source)

3. **Dependency Injection**: Hilt
   - Rationale: Standard for Android, works with Jetpack Compose
   - Alternative: Koin (not chosen - Hilt provides better scope management)

4. **Data Persistence**: Room + DataStore
   - Rationale: Room for complex queries, DataStore for settings
   - Alternative: SQLite directly (rejected for productivity)

5. **Navigation**: Jetpack Navigation Compose
   - Rationale: Declarative, route-based navigation
   - Alternative: Navigation Component + SafeArgs (Compose-specific)

6. **UI Framework**: Jetpack Compose + Material 3
   - Rationale: Modern, declarative, built for Compose
   - Alternative: XML layouts (rejected for future-proofing)

## Phase 2: Audio and narration

1. **Lifecycle ownership**: an Activity-retained Hilt ViewModel owns the narration engine and SoundPool; MainActivity pauses/resumes it with lifecycle callbacks, and navigation stops narration when a route changes.
   - Rationale: configuration changes retain the engine, while leaving a lesson or destroying the activity releases playback resources.

2. **Audio focus**: speech requests transient focus; transient interruptions pause and resume the active utterance, and completed speech abandons focus. Effects use the same focus path and are stopped on backgrounding.
   - Rationale: narration should not compete with other apps or continue after TALA leaves the foreground.

3. **TTS locale fallback**: request fil-PH or en-PH and fall back to en-US only after checking engine availability. The lesson API exposes availability and subtitle state so the UI can keep text visible when speech is unavailable.
   - If Filipino speech is requested but fil-PH is missing and en-US exists, the pupil continues to see the Filipino JSON line as a subtitle and hears that same Filipino text through the installed English voice. If neither voice exists, the text remains available as a subtitle and speech reports unavailable.
   - Rationale: missing language packs should degrade gracefully rather than silently failing.

4. **Key resolution**: look for `assets/audio/{lang}/{key}.ogg` first, then use `content/strings_{lang}.json` for TTS text and optional `soundText`; MediaPlayer failures fall through to the same localized TTS text.
   - Rationale: recorded narration takes precedence while every key still has an offline text-to-speech path.

5. **Teacher recovery**: the retained audio ViewModel turns subtitles on as soon as TTS is unavailable or falling back. A one-time recovery notice is shown only on the teacher setup route, which is behind the parent gate/PIN; it offers Android TTS settings or install-data intent.
   - Rationale: pupil screens never show system setup or error dialogs.

6. **Original SFX assets**: `tools/generate_phase2_sfx.js` generates all seven WAVs from synthesized sine waves and short note sequences. The earlier tap, success, and celebration WAVs had no provenance recorded in the repository; they have been replaced by the generated versions.
   - Rationale: keep every effect reproducible, audibly distinct, and free of external or copyrighted audio samples.

## Phase 3: Data layer and progress recording

1. **Room schema**: added the full Phase 3 entity set for students, level progress, activity results, quest attempts, star wallet, star transactions, avatar ownership, badge rewards, item mastery, student settings, and play sessions.
   - Rationale: the guide requires these tables to support offline progress tracking and teacher dashboards.
   - Alternative considered: storing progress in DataStore only (rejected because level progress and mastery need relational queries and transactions).

2. **Repository wiring**: the repository is exposed through Hilt and includes all Room DAOs needed by the app.
   - Rationale: keeps runtime access centralized and consistent with the MVVM + Hilt architecture.

3. **Progress recorder**: implemented as a Room transaction that records each activity result and immediately updates the related progress, wallet, mastery, and badge state in one atomic write.
   - Rationale: this prevents partial progress updates when a level result is processed.

4. **Scoring rules**: star mapping, mastery checks, replay-only extra stars, and unlock progression are centralized in a pure scoring utility so tests can validate behavior without UI dependencies.
   - Rationale: the rules are business logic and belong outside the UI layer.

## Phase 13: Star shop spending

1. **Spending is not implemented in Phase 3**: the repository currently exposes independent wallet and transaction persistence, but no purchase operation that updates them atomically.
   - Phase 13 requirement: a successful purchase must reduce `StarWallet.balance` and write one negative `StarTransaction` in the same transaction; `lifetimeStars` must never decrease. Insufficient balance must leave all three unchanged.
   - Rationale: spending belongs with the Star Shop purchase flow and should be tested alongside its ownership and confirmation behavior, not simulated as an exposed Phase 3 feature.

## App orientation

Both activities use `android:screenOrientation="unspecified"`: phones use their natural portrait orientation by default while allowing landscape, and landscape-first tablets use their natural orientation. Do not add a portrait/landscape lock; stateful flows use saveable Compose state or retained ViewModels across configuration changes.
   - Emulator verification on API 37 used `adb emu rotate` because the Android Studio Device Manager UI was unavailable. Splash, login, parent gate, and teacher setup were inspected in both orientations; parent-gate challenge/answer, teacher form values, narration subtitle, and the active repeat toggle survived observed rotation. Full supported-device matrix and audible output verification remain pending.

## Icon Assets

NOT CREATED YET - will need TALA logo, avatars, items icons.

**Next Steps Phase 1**: Create vector/icon assets, theme, and build environment.

---

## Formatting Decisions

- **File naming**: camelCase for Kotlin files, camelCase for classes/functions, kebab-case for directories
- **Commit messages**: Phase 0: [component] short description
- **Code style**: KtLint compatible, Google Java Style Guide
- **Spacing**: 2 spaces for Kotlin indentation