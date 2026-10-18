# Phase 1 Implementation Complete: Data Honesty Fixes

## Summary

Fixed all data honesty violations identified in the validation report. The HomeScreen now reads real data from Room database and content JSON files instead of displaying hardcoded fake progress.

## Changes Made

### 1. Module Definitions (`ModuleDefinitions.kt`)
Created canonical enum defining all 5 modules with:
- Module IDs (PHONICS, NUMBERS, SHAPES_COLORS_ARTS, VALUES, QUEST)
- Display names, guide characters, accent colors
- Expected level counts (20, 15, 15, 10, 10)

### 2. Content JSON Files
Expanded/created all module content files:
- **phonics.json**: Expanded from 2 to 20 levels (letters A-Z, blending, sight words)
- **numbers.json**: Created with 15 levels (1-10, counting, comparison, addition/subtraction)
- **shapes_colors_arts.json**: Created with 15 levels (shapes, colors, mixing, patterns)
- **values.json**: Created with 10 levels (kindness, honesty, helping, respect)
- **quest.json**: Created with 10 levels (mixed review rounds)

All content follows the established schema with proper indices, titles, prompts, choices, and guide characters.

### 3. HomeViewModel (`HomeViewModel.kt`)
Created ViewModel following established patterns:
- Injects `StudentProgressRepository` and `TalaContentLoader`
- Reads `studentId` from `SavedStateHandle` (navigation argument)
- Exposes `wallet: StateFlow<StarWalletEntity?>` from database
- Exposes `modules: StateFlow<List<HomeModuleData>>` combining:
  - Content JSON (titles, level counts, descriptions)
  - Room progress (completed counts, current level, per-level stars)
- Uses `SharingStarted.WhileSubscribed(5_000)` per project patterns
- Computes current level as first level with `bestStars == 0`
- Computes completed count from levels where `bestStars > 0`

### 4. HomePathLogic Cleanup (`HomePathLogic.kt`)
Removed all fake data:
- Deleted `modules()` function returning 4 hardcoded modules
- Deleted `HomeModule` data class with fake progress
- Deleted `levelStatusFor()` function
- Kept only pure utility: `nodeStateFor()` for deriving node visual state

### 5. HomeScreen Updates (`HomeScreen.kt`)
Wired ViewModel to UI:
- Added `studentId: String` parameter
- Injected `HomeViewModel` with `hiltViewModel()`
- Replaced `HomePathLogic.modules()` with `viewModel.modules.collectAsState()`
- Replaced `StarWallet(stars = 128)` with `wallet?.balance ?: 0`
- Updated `ModulePathCard` to accept `HomeModuleData` instead of `HomeModule`
- Fixed level star display to use actual `bestStars` from database instead of hardcoded 5/3/0

### 6. Navigation Updates (`TalaNavigation.kt`, `Routes.kt`)
Added studentId flow:
- Login now captures student name and passes to Home: `"${Routes.Home}/$studentName"`
- Home route changed from static to parameterized: `"${Routes.Home}/{studentId}"`
- Added `navArgument("studentId")` with `NavType.StringType`
- HomeScreen receives studentId from `backStackEntry.arguments`
- StoryIntro redirects back to Login (simplified for now, proper flow in Phase 5)

## Data Flow

```
Login (student name) 
  → Navigation passes name as studentId
  → HomeScreen receives studentId parameter
  → HomeViewModel queries:
    - repository.observeWallet(studentId) → wallet balance
    - repository.getModuleProgress(studentId, moduleId) → level progress
    - contentLoader.loadModule(moduleId) → level counts, titles
  → Derives current/completed from actual database state
  → UI displays real stars, real progress, real level counts
```

## Verification

All hardcoded data removed:
- ✅ No `stars = 128` wallet display
- ✅ No `levels = 5` fake level counts
- ✅ No `currentLevel = 3, completedLevels = 2` fake progress
- ✅ No hardcoded 5/3/0 star displays
- ✅ Quest Quiz module now included (5 modules total, not 4)
- ✅ Real level counts from JSON: 20/15/15/10/10

## Known Limitations

1. **Student Identity**: Currently using student name as temporary ID. Proper student entity creation with UUID will be added in Phase 5.
2. **Empty Database**: On first launch with a new student, wallet will be null (0 stars) and no progress rows exist (all levels locked except level 1 current). This is honest behavior.
3. **Content Loading**: If content JSON files fail to load, ViewModel will expose empty module lists. Error handling will be enhanced in Phase 5.

## Next Steps (Phase 5)

- Add proper student entity creation in StudentLoginViewModel
- Add active student persistence in AppSettingsRepository (DataStore)
- Add error states for content loading failures
- Implement full Star Path UI with winding path visualization
- Add bottom navigation (Path/Closet/Shop/Sky Map tabs)
- Add level popup dialog
- Add constellation progress strip
- Add guide character speech bubbles

## Files Modified

- `app/src/main/java/com/capstone/tala/domain/content/ModuleDefinitions.kt` (new)
- `app/src/main/java/com/capstone/tala/feature/home/HomeViewModel.kt` (new)
- `app/src/main/java/com/capstone/tala/feature/home/HomePathLogic.kt` (modified)
- `app/src/main/java/com/capstone/tala/feature/home/HomeScreen.kt` (modified)
- `app/src/main/java/com/capstone/tala/navigation/TalaNavigation.kt` (modified)
- `app/src/main/assets/content/phonics.json` (modified)
- `app/src/main/assets/content/numbers.json` (new)
- `app/src/main/assets/content/shapes_colors_arts.json` (new)
- `app/src/main/assets/content/values.json` (new)
- `app/src/main/assets/content/quest.json` (new)
