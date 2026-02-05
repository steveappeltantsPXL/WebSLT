# Coding Rules

## General

- Keep code simple and readable
- Follow existing project conventions
- Write meaningful commit messages

## Kotlin

- Follow Kotlin coding conventions (https://kotlinlang.org/docs/coding-conventions.html)
- Use data classes for DTOs
- Prefer immutable values (`val`) over mutable (`var`)
- Use coroutines for async operations

## TypeScript / React

- Use functional components with hooks
- Keep components small and focused
- Use TypeScript strict mode

## Gradle

- Manage dependency versions in `gradle/libs.versions.toml`
- Keep build scripts minimal and declarative
## Project Context

### Current Setup: Backend (Ktor) + Frontend (Separated)
```
Backend (WebSLT):
- Framework: Ktor
- Language: Kotlin
- Purpose: Data collection API, user management (optional)
- Does NOT perform: ML inference (client-side only)

Frontend (To be created):
- Framework: React or Vue.js
- Language: TypeScript
- Purpose: UI, client-side ML with TensorFlow.js
- Handles: All gesture recognition and translation
```

### Architecture Overview
- **Backend**: REST API for data collection, not for translation
- **Frontend**: Client-side ML translation, calls backend for optional data upload
- **Separation**: Two independent projects with different tech stacks

## General Principles

### Code Quality Standards
1. **Readability over cleverness**: Write code for humans first, computers second
2. **SOLID principles**: Follow Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
3. **DRY (Don't Repeat Yourself)**: Abstract common patterns, but avoid premature abstraction
4. **KISS (Keep It Simple, Stupid)**: Prefer simple solutions over complex ones
5. **YAGNI (You Aren't Gonna Need It)**: Don't build features until they're needed

### Clean Architecture Rules
- **Dependency Rule**: Dependencies point inward only (Presentation → Domain ← Data)
- **Domain layer purity**: No framework dependencies in domain layer
- **Use cases single responsibility**: One use case = one business operation
- **Repository pattern**: Abstract data sources behind interfaces

## Kotlin Coding Standards

### Naming Conventions

#### Classes and Interfaces
```kotlin
// Use PascalCase for classes and interfaces
class GestureDetector
interface TranslationRepository
data class HandLandmark
sealed class TranslationResult

// Use descriptive names that reflect purpose
✓ GOOD: MediaPipeHandDetector
✗ BAD: MPHDetector, Detector1

// Suffix interfaces with base name (not 'I' prefix)
✓ GOOD: GestureRepository (interface), GestureRepositoryImpl (implementation)
✗ BAD: IGestureRepository, GestureRepositoryInterface
```

#### Functions and Variables
```kotlin
// Use camelCase for functions and variables
fun detectGesture(): SignGesture
val currentTranslation: String
private var isProcessing: Boolean

// Boolean variables start with is/has/can/should
val isRecording: Boolean
val hasPermission: Boolean
val canTranslate: Boolean
val shouldUpdateModel: Boolean

// Use meaningful names, avoid abbreviations
✓ GOOD: translationConfidenceThreshold
✗ BAD: tct, transConfThrsh
```

#### Constants
```kotlin
// Use UPPER_SNAKE_CASE for constants
const val MAX_GESTURE_DURATION_MS = 3000
const val MIN_CONFIDENCE_THRESHOLD = 0.85
const val DEFAULT_FRAME_RATE = 30

// Group related constants in companion objects or objects
object MLConfig {
    const val MODEL_VERSION = "1.0.0"
    const val INPUT_SIZE = 63
    const val NUM_CLASSES = 50
}
```

### File Organization

#### File Naming
- One public class per file
- File name matches class name: `GestureDetector.kt`
- Use descriptive package names: `com.app.signlanguage.domain.usecases`

#### Package Structure
```
com.app.signlanguage/
├── domain/
│   ├── entities/        # Business objects
│   ├── repositories/    # Repository interfaces
│   └── usecases/        # Business logic
├── data/
│   ├── repositories/    # Repository implementations
│   ├── datasources/     # Data source interfaces and implementations
│   └── models/          # DTOs and data models
├── presentation/
│   ├── viewmodels/      # ViewModels
│   └── ui/              # Compose UI components
└── infrastructure/
    ├── ml/              # ML model wrappers
    ├── camera/          # Camera implementations
    └── storage/         # Storage implementations
```

### Code Structure

#### Class Structure Order
```kotlin
class GestureDetector(
    // 1. Primary constructor parameters
    private val mediaPipeDetector: MediaPipeDetector,
    private val modelInterpreter: TensorFlowInterpreter
) {
    // 2. Companion object
    companion object {
        private const val TAG = "GestureDetector"
        private const val CONFIDENCE_THRESHOLD = 0.85f
    }
    
    // 3. Properties (in order: public, internal, protected, private)
    var isEnabled: Boolean = true
        private set
    
    private val gestureBuffer = mutableListOf<HandLandmark>()
    
    // 4. Init blocks
    init {
        require(modelInterpreter.isLoaded) { "Model must be loaded" }
    }
    
    // 5. Public methods
    suspend fun detectGesture(frame: CameraFrame): Result<SignGesture> {
        // Implementation
    }
    
    // 6. Internal methods
    internal fun resetBuffer() {
        gestureBuffer.clear()
    }
    
    // 7. Protected methods
    protected fun validateLandmarks(landmarks: List<HandLandmark>): Boolean {
        // Implementation
    }
    
    // 8. Private methods
    private fun preprocessFrame(frame: CameraFrame): ProcessedFrame {
        // Implementation
    }
    
    // 9. Nested classes/interfaces
    data class DetectionResult(
        val gesture: SignGesture,
        val confidence: Float
    )
}
```

### Kotlin Language Features

#### Null Safety
```kotlin
// Avoid null whenever possible, use nullable types only when necessary
✓ GOOD:
data class Translation(
    val text: String,
    val confidence: Float
)

✗ BAD:
data class Translation(
    val text: String?,
    val confidence: Float?
)

// Use safe calls and Elvis operator
val translation = gesture?.translate() ?: Translation.empty()

// Use requireNotNull for preconditions
val landmarks = handLandmarks.requireNotNull { "Landmarks must not be null" }

// Use let for null-safe transformations
gesture?.let { detected ->
    translationRepository.save(detected)
}
```

#### Immutability
```kotlin
// Prefer val over var
✓ GOOD: val gesture = detectGesture()
✗ BAD: var gesture = detectGesture()

// Use immutable collections
✓ GOOD: val landmarks: List<HandLandmark>
✗ BAD: val landmarks: MutableList<HandLandmark>

// Use data classes for immutable models
data class HandLandmark(
    val x: Float,
    val y: Float,
    val z: Float
) {
    // All properties are val by default
}
```

#### Extension Functions
```kotlin
// Use extension functions for utility operations
fun List<HandLandmark>.normalize(): List<HandLandmark> {
    val minX = minOf { it.x }
    val maxX = maxOf { it.x }
    return map { it.copy(x = (it.x - minX) / (maxX - minX)) }
}

// Keep extensions in appropriate files
// File: HandLandmarkExtensions.kt
fun HandLandmark.distance(other: HandLandmark): Float {
    return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
}
```

#### Scope Functions
```kotlin
// Use appropriate scope function for the context
// let: transform and return result
val translation = gesture?.let { translateGesture(it) }

// apply: configure object
val detector = GestureDetector().apply {
    threshold = 0.9f
    maxBufferSize = 30
}

// also: perform side effects
val result = detectGesture()
    .also { log("Detected: ${it.name}") }

// run: execute block and return result
val confidence = run {
    val predictions = model.predict(features)
    predictions.max()
}

// with: operate on object without returning it
with(gestureBuffer) {
    add(landmark)
    if (size > maxSize) removeAt(0)
}
```

### Coroutines and Flow

#### Coroutine Usage
```kotlin
// Use structured concurrency
class TranslationViewModel(
    private val translateUseCase: TranslateGestureUseCase
) : ViewModel() {
    
    fun translate(gesture: SignGesture) {
        viewModelScope.launch {
            try {
                val result = translateUseCase(gesture)
                _translation.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

// Use appropriate dispatcher
suspend fun loadModel() = withContext(Dispatchers.IO) {
    // I/O operations
}

suspend fun processFrame(frame: CameraFrame) = withContext(Dispatchers.Default) {
    // CPU-intensive work
}

// Cancel coroutines properly
private var detectionJob: Job? = null

fun startDetection() {
    detectionJob?.cancel()
    detectionJob = viewModelScope.launch {
        // Detection logic
    }
}

fun stopDetection() {
    detectionJob?.cancel()
    detectionJob = null
}
```

#### Flow Best Practices
```kotlin
// Use Flow for streams of data
interface CameraManager {
    fun frameStream(): Flow<CameraFrame>
}

// Transform flows with operators
val translations: Flow<Translation> = cameraManager
    .frameStream()
    .map { frame -> detectGesture(frame) }
    .filter { gesture -> gesture.confidence > threshold }
    .map { gesture -> translateGesture(gesture) }
    .catch { e -> emit(Translation.error(e.message)) }

// Use StateFlow for UI state
private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Use SharedFlow for events
private val _events = MutableSharedFlow<TranslationEvent>()
val events: SharedFlow<TranslationEvent> = _events.asSharedFlow()
```

### Compose Multiplatform UI

#### Component Structure
```kotlin
@Composable
fun TranslationScreen(
    viewModel: TranslationViewModel = koinViewModel()
) {
    // Collect state
    val uiState by viewModel.uiState.collectAsState()
    
    // Scaffold with standard structure
    Scaffold(
        topBar = { TranslationTopBar() },
        bottomBar = { TranslationBottomBar() }
    ) { paddingValues ->
        TranslationContent(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
            onEvent = viewModel::handleEvent
        )
    }
}

// Separate content composable for preview and testing
@Composable
private fun TranslationContent(
    modifier: Modifier = Modifier,
    state: TranslationUiState,
    onEvent: (TranslationEvent) -> Unit
) {
    // Content implementation
}

// Preview with sample data
@Preview
@Composable
private fun TranslationContentPreview() {
    AppTheme {
        TranslationContent(
            state = TranslationUiState.sample(),
            onEvent = {}
        )
    }
}
```

#### State Management
```kotlin
// Use sealed classes for UI state
sealed interface TranslationUiState {
    data object Idle : TranslationUiState
    data object Loading : TranslationUiState
    data class Success(val translation: Translation) : TranslationUiState
    data class Error(val message: String) : TranslationUiState
}

// Use sealed classes for UI events
sealed interface TranslationEvent {
    data class GestureDetected(val gesture: SignGesture) : TranslationEvent
    data object ClearTranslation : TranslationEvent
    data object RetryDetection : TranslationEvent
}
```

#### Modifiers
```kotlin
// Apply modifiers in consistent order
@Composable
fun GestureCard(gesture: SignGesture) {
    Card(
        modifier = Modifier
            // 1. Size and layout
            .fillMaxWidth()
            .height(120.dp)
            // 2. Padding and spacing
            .padding(16.dp)
            // 3. Visual effects
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            // 4. Interactions
            .clickable { /* handle click */ }
    ) {
        // Content
    }
}
```

### Testing Standards

#### Unit Test Structure
```kotlin
class GestureDetectorTest {
    // Use descriptive test names
    @Test
    fun `detectGesture returns success when valid landmarks provided`() = runTest {
        // Given
        val landmarks = listOf(
            HandLandmark(0.5f, 0.5f, 0.0f),
            // ... more landmarks
        )
        val detector = GestureDetector(mockMediaPipe, mockModel)
        
        // When
        val result = detector.detectGesture(landmarks)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(SignGesture.ThumbsUp, result.getOrNull())
    }
    
    @Test
    fun `detectGesture returns error when insufficient landmarks provided`() = runTest {
        // Given
        val insufficientLandmarks = listOf(HandLandmark(0.5f, 0.5f, 0.0f))
        val detector = GestureDetector(mockMediaPipe, mockModel)
        
        // When
        val result = detector.detectGesture(insufficientLandmarks)
        
        // Then
        assertTrue(result.isFailure)
    }
}
```

## Error Handling

### Result Type Usage
```kotlin
// Use Result for operations that can fail
suspend fun translateGesture(gesture: SignGesture): Result<Translation> {
    return runCatching {
        // Translation logic that might throw
        translationEngine.translate(gesture)
    }.onFailure { exception ->
        logger.error("Translation failed", exception)
    }
}

// Handle results explicitly
when (val result = translateGesture(gesture)) {
    is Result.Success -> display(result.value)
    is Result.Failure -> showError(result.exception)
}
```

### Exception Handling
```kotlin
// Use specific exception types
class ModelNotLoadedException : Exception("ML model not loaded")
class InvalidLandmarksException(message: String) : Exception(message)
class CameraPermissionDeniedException : Exception("Camera permission required")

// Document exceptions in KDoc
/**
 * Detects sign language gesture from camera frame.
 * 
 * @throws ModelNotLoadedException if ML model is not loaded
 * @throws InvalidLandmarksException if landmarks are malformed
 */
suspend fun detectGesture(frame: CameraFrame): SignGesture
```

## Performance Guidelines

### Memory Management
```kotlin
// Release resources in lifecycle methods
class CameraManager {
    private var camera: Camera? = null
    
    fun start() {
        camera = Camera.open()
    }
    
    fun stop() {
        camera?.release()
        camera = null
    }
}

// Use sequences for large collections
val processedLandmarks = landmarks
    .asSequence()
    .filter { it.confidence > threshold }
    .map { it.normalize() }
    .toList()
```

### ML Model Optimization
```kotlin
// Reuse interpreters, don't recreate
class TensorFlowInterpreter(modelPath: String) {
    private val interpreter = Interpreter(loadModelFile(modelPath))
    
    // Reuse input/output buffers
    private val inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * 4)
    private val outputBuffer = ByteBuffer.allocateDirect(OUTPUT_SIZE * 4)
    
    fun predict(features: FloatArray): FloatArray {
        inputBuffer.rewind()
        features.forEach { inputBuffer.putFloat(it) }
        
        interpreter.run(inputBuffer, outputBuffer)
        
        outputBuffer.rewind()
        return FloatArray(OUTPUT_SIZE) { outputBuffer.float }
    }
    
    fun close() {
        interpreter.close()
    }
}
```

## Documentation Standards

### KDoc Comments
```kotlin
/**
 * Detects sign language gestures from hand landmarks.
 * 
 * This detector uses MediaPipe for landmark extraction and TensorFlow
 * for gesture classification. It maintains a temporal buffer for
 * sequence-based gesture recognition.
 * 
 * @property mediaPipeDetector Detector for extracting hand landmarks
 * @property modelInterpreter TensorFlow interpreter for gesture classification
 * @constructor Creates a gesture detector with specified dependencies
 * 
 * @sample
 * ```
 * val detector = GestureDetector(mediaPipe, model)
 * val result = detector.detectGesture(cameraFrame)
 * ```
 */
class GestureDetector(
    private val mediaPipeDetector: MediaPipeDetector,
    private val modelInterpreter: TensorFlowInterpreter
) {
    /**
     * Detects gesture from camera frame.
     * 
     * @param frame Camera frame containing hand to detect
     * @return Result containing detected gesture or error
     * @throws ModelNotLoadedException if model is not initialized
     */
    suspend fun detectGesture(frame: CameraFrame): Result<SignGesture> {
        // Implementation
    }
}
```

### Code Comments
```kotlin
// Use comments for WHY, not WHAT
✓ GOOD:
// Normalize coordinates to [-1, 1] range to be invariant to hand position
val normalizedX = (x - centerX) / maxDistance

✗ BAD:
// Subtract centerX from x and divide by maxDistance
val normalizedX = (x - centerX) / maxDistance

// Explain complex algorithms
// Using Savitzky-Golay filter to smooth landmark trajectories
// while preserving sharp transitions between gestures
val smoothedLandmarks = savitzkyGolayFilter(landmarks, windowSize = 5)
```

## Code Review Checklist

### Before Committing
- [ ] Code follows Kotlin coding conventions
- [ ] All public APIs have KDoc documentation
- [ ] Unit tests cover new functionality
- [ ] No compiler warnings
- [ ] Code formatted with ktlint
- [ ] No hardcoded strings (use resources)
- [ ] No magic numbers (use named constants)
- [ ] Error handling is appropriate
- [ ] Nullable types are avoided where possible
- [ ] Coroutines use structured concurrency

### Architecture Compliance
- [ ] Dependencies point inward (no domain dependencies on data/presentation)
- [ ] Use cases have single responsibility
- [ ] Repository pattern used for data access
- [ ] Platform-specific code uses expect/actual correctly
- [ ] No business logic in ViewModels or Composables

### Performance
- [ ] No blocking operations on main thread
- [ ] Collections use appropriate types (List vs MutableList)
- [ ] Resources are released properly
- [ ] No memory leaks (coroutine cancellation, lifecycle awareness)
- [ ] ML models reuse buffers and interpreters

## Healthcare Compliance

### Data Handling
```kotlin
// Always encrypt sensitive data
object EncryptionUtil {
    fun encryptGesture(gesture: SignGesture): EncryptedData {
        return AESEncryption.encrypt(
            data = gesture.toByteArray(),
            key = getEncryptionKey()
        )
    }
}

// Log only non-sensitive information
logger.info("Gesture detected: ${gesture.type}") // OK
logger.info("User: ${user.name}") // NOT OK in healthcare context

// Provide audit trails when required
interface AuditLogger {
    fun logAccess(userId: String, action: String, timestamp: Instant)
    fun logDataExport(userId: String, dataType: String, destination: String)
}
```

### Consent Management
```kotlin
// Always check permissions before sensitive operations
suspend fun startRecording(): Result<Unit> {
    return when {
        !hasCameraPermission() -> Result.failure(CameraPermissionDeniedException())
        !hasStoragePermission() -> Result.failure(StoragePermissionDeniedException())
        !userHasConsentedToRecording() -> Result.failure(ConsentRequiredException())
        else -> Result.success(startCameraCapture())
    }
}
```

## Accessibility

### Compose Accessibility
```kotlin
@Composable
fun GestureButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.semantics {
            // Provide semantic information for screen readers
            contentDescription = "Button to $text"
            role = Role.Button
        }
    ) {
        Text(text)
    }
}
```

## Version Control

### Commit Messages
```
feat: Add gesture detection for ASL alphabet
fix: Resolve memory leak in camera preview
refactor: Extract landmark normalization to extension function
perf: Optimize TensorFlow model inference by 30%
docs: Update architecture documentation with ML pipeline
test: Add unit tests for TranslationUseCase
```

### Branch Naming
```
feature/gesture-detection-pipeline
bugfix/camera-permission-crash
refactor/clean-architecture-implementation
hotfix/model-loading-failure
```

## Ktor Backend Coding Standards

### Route Organization

```kotlin
// Organize routes by feature/resource
// routes/TrainingDataRoutes.kt
fun Route.trainingDataRoutes() {
    route("/api/training-data") {
        post {
            val request = call.receive<TrainingDataRequest>()
            // Validate request
            val result = trainingDataService.save(request)
            call.respond(HttpStatusCode.Created, result)
        }
        
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest, 
                ApiResponse.error("Missing id")
            )
            val data = trainingDataService.findById(id)
            call.respond(data)
        }
    }
}
```

### Request/Response Models

```kotlin
// Use kotlinx.serialization for JSON
@Serializable
data class TrainingDataRequest(
    val userId: String,
    val gestureType: String,
    val landmarks: List<LandmarkDto>,
    val videoUrl: String? = null,
    val timestamp: Instant = Clock.System.now()
) {
    // Add validation
    fun validate(): ValidationResult {
        return when {
            userId.isBlank() -> ValidationResult.Error("userId is required")
            gestureType.isBlank() -> ValidationResult.Error("gestureType is required")
            landmarks.size != 21 -> ValidationResult.Error("Expected 21 landmarks")
            else -> ValidationResult.Success
        }
    }
}

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
) {
    companion object {
        fun <T> success(data: T) = ApiResponse(success = true, data = data)
        fun error(message: String) = ApiResponse<Unit>(success = false, error = message)
    }
}
```

### Error Handling

```kotlin
// Centralized error handling
fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error(cause.message ?: "Validation failed")
            )
        }
        
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse.error(cause.message ?: "Resource not found")
            )
        }
        
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.error("Internal server error")
            )
        }
    }
}

// Custom exceptions
class ValidationException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
```

### Service Layer Pattern

```kotlin
// Service interface
interface TrainingDataService {
    suspend fun save(request: TrainingDataRequest): TrainingDataResponse
    suspend fun findById(id: String): TrainingData?
    suspend fun findByUserId(userId: String): List<TrainingData>
}

// Service implementation
class TrainingDataServiceImpl(
    private val repository: TrainingDataRepository,
    private val storageService: StorageService
) : TrainingDataService {
    
    override suspend fun save(request: TrainingDataRequest): TrainingDataResponse {
        // Validate
        request.validate().let { result ->
            if (result is ValidationResult.Error) {
                throw ValidationException(result.message)
            }
        }
        
        // Save to database
        val data = repository.save(request.toEntity())
        
        // Upload video if present
        request.videoUrl?.let { url ->
            storageService.upload(url, "training-videos/${data.id}")
        }
        
        return data.toResponse()
    }
    
    override suspend fun findById(id: String): TrainingData? {
        return repository.findById(id)
    }
    
    override suspend fun findByUserId(userId: String): List<TrainingData> {
        return repository.findByUserId(userId)
    }
}
```

### Repository Pattern

```kotlin
// Repository interface
interface TrainingDataRepository {
    suspend fun save(data: TrainingData): TrainingData
    suspend fun findById(id: String): TrainingData?
    suspend fun findByUserId(userId: String): List<TrainingData>
}

// Implementation with Exposed (SQL)
class TrainingDataRepositoryImpl(
    private val database: Database
) : TrainingDataRepository {
    
    override suspend fun save(data: TrainingData): TrainingData = dbQuery {
        TrainingDataTable.insert {
            it[userId] = data.userId
            it[gestureType] = data.gestureType
            it[landmarks] = Json.encodeToString(data.landmarks)
            it[createdAt] = data.createdAt
        }.resultedValues?.firstOrNull()?.toTrainingData() ?: throw IllegalStateException()
    }
    
    override suspend fun findById(id: String): TrainingData? = dbQuery {
        TrainingDataTable.select { TrainingDataTable.id eq id }
            .map { it.toTrainingData() }
            .singleOrNull()
    }
    
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            transaction(database) {
                block()
            }
        }
}
```

### Dependency Injection (Koin)

```kotlin
// di/AppModule.kt
val appModule = module {
    // Services
    single<TrainingDataService> { TrainingDataServiceImpl(get(), get()) }
    single<StorageService> { CloudStorageService(get()) }
    
    // Repositories
    single<TrainingDataRepository> { TrainingDataRepositoryImpl(get()) }
    
    // Database
    single { DatabaseFactory.create() }
}

// Application.kt
fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(appModule)
    }
}

// Use in routes
fun Route.trainingDataRoutes() {
    val trainingDataService by inject<TrainingDataService>()
    
    post("/api/training-data") {
        val request = call.receive<TrainingDataRequest>()
        val result = trainingDataService.save(request)
        call.respond(HttpStatusCode.Created, result)
    }
}
```

### CORS Configuration

```kotlin
fun Application.configureCORS() {
    install(CORS) {
        // Allow frontend origins
        allowHost("localhost:5173")  // Vite dev server
        allowHost("webslt.app", schemes = listOf("https"))
        
        // Allow specific methods
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        
        // Allow headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        
        // Allow credentials
        allowCredentials = true
        
        // Max age
        maxAgeInSeconds = 3600
    }
}
```

### Logging Best Practices

```kotlin
// Use SLF4J with Logback
class TrainingDataService(
    private val repository: TrainingDataRepository
) {
    private val logger = LoggerFactory.getLogger(TrainingDataService::class.java)
    
    suspend fun save(request: TrainingDataRequest): TrainingDataResponse {
        logger.info("Saving training data for user: ${request.userId}")
        
        try {
            val result = repository.save(request.toEntity())
            logger.info("Successfully saved training data: ${result.id}")
            return result.toResponse()
        } catch (e: Exception) {
            logger.error("Failed to save training data for user: ${request.userId}", e)
            throw e
        }
    }
}

// Configure logging in logback.xml
// NEVER log sensitive data (passwords, tokens, personal info)
✓ GOOD: logger.info("User login attempt: userId=${user.id}")
✗ BAD: logger.info("User login: email=${user.email}, password=${user.password}")
```

### Testing Ktor Applications

```kotlin
class TrainingDataRoutesTest {
    
    @Test
    fun `POST training data returns 201 Created`() = testApplication {
        // Setup
        application {
            configureSerialization()
            configureRouting()
            configureDependencyInjection()
        }
        
        // Given
        val request = TrainingDataRequest(
            userId = "test-user",
            gestureType = "thumbs_up",
            landmarks = List(21) { LandmarkDto(0.5f, 0.5f, 0f) }
        )
        
        // When
        val response = client.post("/api/training-data") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        
        // Then
        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.body<ApiResponse<TrainingDataResponse>>()
        assertTrue(body.success)
        assertNotNull(body.data)
    }
    
    @Test
    fun `POST training data validates required fields`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        
        val response = client.post("/api/training-data") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
```

## Frontend Coding Standards (TypeScript/React - When Created)

### Component Structure

```typescript
// Use functional components with TypeScript
interface GestureDetectorProps {
  onGestureDetected: (gesture: Gesture) => void;
  confidence: number;
}

export const GestureDetector: React.FC<GestureDetectorProps> = ({
  onGestureDetected,
  confidence
}) => {
  // State
  const [isDetecting, setIsDetecting] = useState(false);
  const [currentGesture, setCurrentGesture] = useState<Gesture | null>(null);
  
  // Effects
  useEffect(() => {
    // Setup ML model
    return () => {
      // Cleanup
    };
  }, []);
  
  // Event handlers
  const handleDetection = useCallback((gesture: Gesture) => {
    setCurrentGesture(gesture);
    onGestureDetected(gesture);
  }, [onGestureDetected]);
  
  // Render
  return (
    <div className="gesture-detector">
      {/* UI */}
    </div>
  );
};
```

### TensorFlow.js Best Practices

```typescript
// services/ml/GestureModel.ts
export class GestureModel {
  private model: tf.LayersModel | null = null;
  
  async load(): Promise<void> {
    this.model = await tf.loadLayersModel('/models/gesture-model.json');
    // Warm up model
    const dummyInput = tf.zeros([1, 63]);
    this.model.predict(dummyInput);
    dummyInput.dispose();
  }
  
  predict(landmarks: Float32Array): { gesture: string; confidence: number } {
    if (!this.model) throw new Error('Model not loaded');
    
    return tf.tidy(() => {
      const input = tf.tensor2d([Array.from(landmarks)], [1, 63]);
      const output = this.model!.predict(input) as tf.Tensor;
      const predictions = output.dataSync();
      
      const maxIndex = predictions.indexOf(Math.max(...predictions));
      return {
        gesture: GESTURE_CLASSES[maxIndex],
        confidence: predictions[maxIndex]
      };
    });
  }
  
  dispose(): void {
    this.model?.dispose();
  }
}

// IMPORTANT: Always use tf.tidy() to prevent memory leaks
// IMPORTANT: Dispose tensors when done
```

### API Client Pattern

```typescript
// services/api/BackendClient.ts
export class BackendClient {
  private baseUrl: string;
  
  constructor(baseUrl: string = import.meta.env.VITE_API_BASE_URL) {
    this.baseUrl = baseUrl;
  }
  
  async submitTrainingData(data: TrainingDataRequest): Promise<ApiResponse> {
    const response = await fetch(`${this.baseUrl}/api/training-data`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    return response.json();
  }
}

// Use with React Query for caching and error handling
export const useSubmitTrainingData = () => {
  const client = new BackendClient();
  
  return useMutation({
    mutationFn: (data: TrainingDataRequest) => client.submitTrainingData(data),
    onSuccess: () => {
      toast.success('Training data submitted successfully');
    },
    onError: (error) => {
      toast.error('Failed to submit training data');
      console.error(error);
    },
  });
};
```

## Dependencies Management

### Backend Version Catalog (gradle/libs.versions.toml)
```toml
[versions]
kotlin = "1.9.20"
ktor = "2.3.5"
coroutines = "1.7.3"
logback = "1.4.11"
koin = "3.5.0"
postgresql = "42.6.0"
hikari = "5.0.1"
exposed = "0.44.0"

[libraries]
# Ktor
ktor-server-core = { module = "io.ktor:ktor-server-core", version.ref = "ktor" }
ktor-server-netty = { module = "io.ktor:ktor-server-netty", version.ref = "ktor" }
ktor-server-content-negotiation = { module = "io.ktor:ktor-server-content-negotiation", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-server-cors = { module = "io.ktor:ktor-server-cors", version.ref = "ktor" }
ktor-server-auth = { module = "io.ktor:ktor-server-auth", version.ref = "ktor" }
ktor-server-auth-jwt = { module = "io.ktor:ktor-server-auth-jwt", version.ref = "ktor" }
ktor-server-status-pages = { module = "io.ktor:ktor-server-status-pages", version.ref = "ktor" }
ktor-server-tests = { module = "io.ktor:ktor-server-tests", version.ref = "ktor" }

# Coroutines
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }

# Database
postgresql = { module = "org.postgresql:postgresql", version.ref = "postgresql" }
hikari = { module = "com.zaxxer:HikariCP", version.ref = "hikari" }
exposed-core = { module = "org.jetbrains.exposed:exposed-core", version.ref = "exposed" }
exposed-dao = { module = "org.jetbrains.exposed:exposed-dao", version.ref = "exposed" }
exposed-jdbc = { module = "org.jetbrains.exposed:exposed-jdbc", version.ref = "exposed" }

# Dependency Injection
koin-ktor = { module = "io.insert-koin:koin-ktor", version.ref = "koin" }
koin-logger-slf4j = { module = "io.insert-koin:koin-logger-slf4j", version.ref = "koin" }

# Logging
logback-classic = { module = "ch.qos.logback:logback-classic", version.ref = "logback" }

# Testing
kotlin-test = { module = "org.jetbrains.kotlin:kotlin-test", version.ref = "kotlin" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
ktor = { id = "io.ktor.plugin", version.ref = "ktor" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### Frontend Dependencies (package.json - When Created)
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "@tensorflow/tfjs": "^4.11.0",
    "@mediapipe/hands": "^0.4.1646424915",
    "axios": "^1.5.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "typescript": "^5.2.0",
    "vite": "^4.5.0",
    "@vitejs/plugin-react": "^4.1.0",
    "vitest": "^0.34.0",
    "@testing-library/react": "^14.0.0"
  }
}
```

## Security

### API Keys and Secrets
```kotlin
// NEVER hardcode secrets
✗ BAD:
const val API_KEY = "sk_live_abc123..."

✓ GOOD:
// Use BuildConfig or secure storage
val apiKey: String
    get() = BuildConfig.API_KEY

// Or environment variables
val apiKey: String
    get() = System.getenv("API_KEY") ?: throw IllegalStateException("API_KEY not set")
```

### Input Validation
```kotlin
// Always validate inputs
fun setConfidenceThreshold(threshold: Float) {
    require(threshold in 0.0f..1.0f) {
        "Confidence threshold must be between 0.0 and 1.0"
    }
    this.confidenceThreshold = threshold
}
```

## Resources
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Guidelines](https://developer.android.com/jetpack/compose/designsystems/custom)
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Effective Kotlin by Marcin Moskala](https://kt.academy/book/effectivekotlin)