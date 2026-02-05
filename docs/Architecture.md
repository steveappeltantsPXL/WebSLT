# Sign Language Translation Application - Architecture

## Current Project Setup

### Backend Project Configuration (WebSLT)
```
Project Name: WebSLT
Location: ~\AppDev\Projects\Visear\webSLT
Group ID: be.tinvision
Artifact ID: WebSLT
JDK: Oracle OpenJDK 21.0.8
Build Tool: Gradle (Kotlin DSL)

Components:
✅ Server (Ktor framework) - Backend API for data collection
✅ Web (Separated UI) - Frontend developed separately in React/Vue
✅ Tests - Testing infrastructure enabled
```

### Project Architecture Strategy

**Backend (Ktor):**
- Purpose: Data collection API, optional user management
- NOT for: ML inference or real-time translation (handled client-side)
- Stack: Kotlin, Ktor, kotlinx.serialization, Coroutines

**Frontend (Separate Project):**
- Purpose: User interface, client-side ML translation
- Stack: React/Vue.js, TensorFlow.js, TypeScript
- ML Execution: In-browser for speed, privacy, and offline capability

**Why Separated Architecture:**
- ⚡ Client-side ML is faster (no network latency)
- 🔒 Better privacy (data stays on device)
- 💰 Lower server costs (no ML compute)
- 📶 Works offline

## Overview
Cross-platform sign language translation application with a client-side ML approach. The backend (Ktor) handles data collection for model training, while the frontend (React/Vue + TensorFlow.js) performs real-time gesture recognition and translation in the browser.

## Technology Stack

### Backend (Ktor Server)
- **Language**: Kotlin
- **Framework**: Ktor (Netty or CIO engine)
- **Serialization**: kotlinx.serialization
- **Concurrency**: Kotlin Coroutines + Flow
- **Testing**: Kotlin Test, Ktor Test
- **Purpose**: Data collection APIs, optional user management

### Frontend (Separate Project - To Be Created)
- **Framework**: React.js or Vue.js
- **Language**: TypeScript/JavaScript
- **ML Library**: TensorFlow.js
- **Build Tool**: Vite or Create React App
- **Purpose**: UI, client-side ML translation

### Mobile (Future - Optional)
- **UI Framework**: Compose Multiplatform
- **Target Platforms**: Android, iOS
- **ML Library**: TensorFlow Lite

### AI/ML Stack
- **Web**: TensorFlow.js (browser-based inference)
- **Mobile**: TensorFlow Lite (on-device inference)
- **Training**: TensorFlow/PyTorch (offline, on development machine)
- **Hand Detection**: MediaPipe (client-side)
- **Model Format**: TensorFlow.js models for web, TFLite for mobile

## System Architecture

### Overall System Design

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React/Vue)                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  User Interface Layer                                 │  │
│  │  - Camera capture                                     │  │
│  │  - Text selection from webpage                        │  │
│  │  - Translation display                                │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Client-Side ML Pipeline (TensorFlow.js)             │  │
│  │  - MediaPipe hand detection                           │  │
│  │  - Gesture recognition model                          │  │
│  │  - Translation engine                                 │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                     REST API (HTTPS)
                              │
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              Backend (Ktor) - WebSLT                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  API Layer (Ktor Routes)                             │  │
│  │  - POST /api/training-data (collect gesture data)    │  │
│  │  - POST /api/feedback (user corrections)             │  │
│  │  - GET /api/models (model versions)                  │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Business Logic Layer                                 │  │
│  │  - Data validation                                    │  │
│  │  - User authentication (optional)                     │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Data Layer                                           │  │
│  │  - PostgreSQL/MongoDB for training data              │  │
│  │  - Cloud storage for video/gesture datasets          │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Layer Structure (Backend - Ktor)

### Layer Structure (Backend - Ktor)

```
┌─────────────────────────────────────────────┐
│         API Layer (Ktor Routes)              │
│  - REST endpoints                            │
│  - Request/Response handling                 │
│  - CORS configuration                        │
└─────────────────────────────────────────────┘
                    ↓↑
┌─────────────────────────────────────────────┐
│           Business Logic Layer               │
│  - Data validation                           │
│  - Authentication (optional)                 │
│  - Business rules                            │
└─────────────────────────────────────────────┘
                    ↓↑
┌─────────────────────────────────────────────┐
│              Data Layer                      │
│  - Database repositories                     │
│  - Cloud storage integration                 │
│  - Data models                               │
└─────────────────────────────────────────────┘
```

## Module Structure

### Backend Project Structure (WebSLT)
```
webSLT/
├─ server/                          # Ktor backend module
│  ├─ src/
│  │  ├─ main/kotlin/be/tinvision/webslt/
│  │  │  ├─ Application.kt          # Main entry point
│  │  │  ├─ plugins/
│  │  │  │  ├─ Routing.kt           # API routes
│  │  │  │  ├─ Serialization.kt     # JSON config
│  │  │  │  ├─ Security.kt          # Auth (optional)
│  │  │  │  └─ CORS.kt              # Cross-origin setup
│  │  │  ├─ routes/
│  │  │  │  ├─ TrainingDataRoutes.kt
│  │  │  │  ├─ FeedbackRoutes.kt
│  │  │  │  └─ ModelRoutes.kt
│  │  │  ├─ models/
│  │  │  │  ├─ TrainingDataRequest.kt
│  │  │  │  ├─ FeedbackRequest.kt
│  │  │  │  └─ ApiResponse.kt
│  │  │  ├─ services/
│  │  │  │  ├─ TrainingDataService.kt
│  │  │  │  └─ StorageService.kt
│  │  │  └─ repositories/
│  │  │     ├─ TrainingDataRepository.kt
│  │  │     └─ UserRepository.kt (optional)
│  │  ├─ test/kotlin/be/tinvision/webslt/
│  │  │  ├─ ApplicationTest.kt
│  │  │  ├─ routes/
│  │  │  │  └─ TrainingDataRoutesTest.kt
│  │  │  └─ services/
│  │  │     └─ TrainingDataServiceTest.kt
│  │  └─ resources/
│  │     └─ application.conf         # Ktor configuration
│  └─ build.gradle.kts
│
├─ build.gradle.kts                  # Root build configuration
├─ settings.gradle.kts               # Module settings
├─ gradle.properties                 # Gradle properties
└─ README.md
```

### Frontend Project Structure (To Be Created Separately)
```
webslt-frontend/
├─ src/
│  ├─ components/
│  │  ├─ CameraCapture.tsx
│  │  ├─ GestureDetector.tsx
│  │  ├─ TranslationDisplay.tsx
│  │  └─ TextSelector.tsx
│  ├─ services/
│  │  ├─ ml/
│  │  │  ├─ MediaPipeService.ts      # Hand detection
│  │  │  ├─ GestureModel.ts          # TensorFlow.js model
│  │  │  └─ TranslationEngine.ts
│  │  └─ api/
│  │     └─ BackendClient.ts         # API calls to Ktor
│  ├─ hooks/
│  │  ├─ useCamera.ts
│  │  ├─ useGestureDetection.ts
│  │  └─ useTranslation.ts
│  ├─ models/
│  │  └─ gesture-model.json          # TensorFlow.js model
│  ├─ App.tsx
│  └─ main.tsx
├─ public/
├─ package.json
├─ tsconfig.json
└─ vite.config.ts
```

## Component Architecture

### Backend API Endpoints (Ktor)
```
POST /api/training-data
├─ Purpose: Receive gesture training data from users
├─ Input: { userId, gestureType, landmarks, videoUrl, timestamp }
├─ Response: { success, dataId }
└─ Use: Collect data for improving ML models

POST /api/feedback
├─ Purpose: Receive user corrections on translations
├─ Input: { userId, detectedGesture, actualGesture, confidence }
├─ Response: { success }
└─ Use: Identify model weaknesses

GET /api/models
├─ Purpose: List available model versions
├─ Response: [{ version, accuracy, size, releaseDate }]
└─ Use: Allow frontend to check for updates

POST /api/users (optional)
├─ Purpose: User registration/authentication
└─ Use: Track contributions, preferences
```

### Frontend ML Pipeline (Client-Side)
```
Camera/Video Input
       ↓
Frame Preprocessing
       ↓
MediaPipe Hand Detection (TensorFlow.js)
       ↓
Hand Landmarks (21 points × 3 coords)
       ↓
Feature Extraction & Normalization
       ↓
Gesture Recognition Model (TensorFlow.js)
       ↓
Gesture Classification + Confidence
       ↓
Translation Engine
       ↓
Text/Speech Output
```

### 1. Frontend Camera Pipeline (Client-Side)
```
Camera Input → Frame Preprocessor → MediaPipe Detector
                                           ↓
                                    Hand Landmarks
                                           ↓
                                  Feature Extractor
                                           ↓
                                   TensorFlow Model
                                           ↓
                                  Gesture Recognition
```

### 2. Frontend Translation Pipeline (Client-Side)
```
Recognized Gesture → Gesture Buffer → Context Analyzer
                                            ↓
                                    Translation Engine
                                            ↓
                                    Text/Speech Output
```

### 3. Backend Data Collection Flow
```
Frontend sends training data
       ↓
Ktor API receives POST /api/training-data
       ↓
Data validation (schema, size limits)
       ↓
Store in Database (PostgreSQL/MongoDB)
       ↓
Upload video/images to Cloud Storage (S3/GCS)
       ↓
Return success response to frontend
       ↓
Later: Data scientist downloads for model training
```

## Data Flow

### Real-time Translation Flow (All Client-Side)
1. **Camera captures frame** (30-60 FPS) in browser
2. **MediaPipe detects hand landmarks** (21 points per hand) via TensorFlow.js
3. **Feature extraction** normalizes and processes landmarks (JavaScript)
4. **TensorFlow.js model inference** predicts gesture in browser
5. **Gesture buffer** maintains sequence context (in memory)
6. **Translation engine** converts gesture sequence to text/speech
7. **UI updates** display translation results (React/Vue component)

**All happens locally - no server calls during translation**

### Training Data Collection Flow (Client-to-Server)
1. **User opts-in** to contribute training data
2. **Frontend captures** gesture video + landmarks
3. **Frontend sends** data to backend via `POST /api/training-data`
4. **Backend validates** and stores data in database
5. **Backend uploads** video to cloud storage
6. **Backend responds** with confirmation
7. **Later**: Data scientist retrieves data for offline training

### Offline Training Flow (Development Machine)
1. **Dataset ingestion** from local/remote sources
2. **Data preprocessing** and augmentation
3. **Model training** with TensorFlow
4. **Model validation** and metrics evaluation
5. **Model optimization** and quantization
6. **TFLite conversion** for mobile deployment
7. **Model deployment** to application

## Key Design Decisions

### Multiplatform Strategy
- **Shared**: Business logic, domain models, use cases (90%)
- **Platform-specific**: Camera access, ML inference, storage (10%)
- **Expect/Actual pattern** for platform-dependent implementations

### ML Model Architecture
- **Input**: 21 hand landmarks × 3 coordinates (x, y, z) = 63 features
- **Architecture**: LSTM or Transformer for sequence modeling
- **Output**: Probability distribution over gesture classes
- **Model size**: < 10MB for mobile deployment

### Performance Targets
- **Latency**: < 100ms for gesture recognition
- **FPS**: 30+ frames per second processing
- **Accuracy**: > 95% for trained gestures
- **Battery**: < 5% per hour of active use

### Data Storage
- **Local DB**: SQLDelight for gesture history and offline data
- **Cache**: In-memory LRU cache for frequently used translations
- **Models**: Local storage for TFLite models with versioning

### Healthcare Compliance Considerations
- **Data Privacy**: All processing on-device by default
- **HIPAA Alignment**: Optional encrypted cloud sync
- **Accessibility**: WCAG 2.1 AA compliance for UI
- **Audit Logging**: Optional usage tracking for healthcare settings

## Security Architecture

### Data Protection
- **Encryption at rest**: AES-256 for stored gestures/translations
- **Encryption in transit**: TLS 1.3 for API communications
- **Local processing**: Default mode for sensitive environments

### Privacy Features
- **Anonymization**: Option to disable data collection
- **Camera permissions**: Explicit user consent required
- **Data retention**: User-controlled deletion policies

## Scalability Considerations

### Model Updates
- **Over-the-air updates**: Download new models without app updates
- **A/B testing**: Compare model versions
- **Rollback capability**: Revert to previous model if issues occur

### Extension Points
- **Plugin architecture**: Support for additional sign languages
- **Custom gestures**: User-defined gesture training
- **API integration**: Connect to translation services

## Testing Strategy Reference
See `testing-rules.md` for comprehensive testing guidelines including:
- Unit testing approach for each layer
- Integration testing for ML pipeline
- UI testing with Compose multiplatform
- Performance testing benchmarks

## Deployment Architecture

### Production Setup

```
┌──────────────────────────────────────────────────────────────┐
│                 Frontend Deployment                           │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Static Hosting (Netlify/Vercel/Cloudflare Pages)     │  │
│  │  - React/Vue build artifacts                           │  │
│  │  - TensorFlow.js models                                │  │
│  │  - Serves static files via CDN                         │  │
│  │  - Client-side ML execution                            │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                     HTTPS API Calls
                              ↓
┌──────────────────────────────────────────────────────────────┐
│                 Backend Deployment                            │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Ktor Server (AWS/GCP/DigitalOcean)                   │  │
│  │  - API endpoints                                        │  │
│  │  - Data collection                                      │  │
│  │  - Connected to PostgreSQL/MongoDB                     │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Cloud Storage (AWS S3/Google Cloud Storage)          │  │
│  │  - Training videos and datasets                        │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### CI/CD Pipeline
```
Backend (Ktor):
  Code Commit → GitHub Actions
       ↓
  Gradle Build → Run Tests
       ↓
  Build Docker Image
       ↓
  Deploy to Cloud Provider (AWS/GCP)
       ↓
  Health Check

Frontend (React/Vue):
  Code Commit → GitHub Actions
       ↓
  npm install → npm test
       ↓
  npm run build (production)
       ↓
  Deploy to Netlify/Vercel
       ↓
  CDN Cache Invalidation
```

### Environment Configuration

**Backend:**
- **Development**: Local Ktor server, H2 in-memory database, test models
- **Staging**: Cloud-hosted, PostgreSQL, production-like data
- **Production**: Scalable cloud deployment, PostgreSQL/MongoDB, monitoring

**Frontend:**
- **Development**: Local dev server (Vite), local TensorFlow.js models
- **Staging**: Staging deployment, connects to staging backend
- **Production**: CDN-hosted, optimized bundles, production models

## Monitoring and Analytics

### Key Metrics
- **Performance**: Inference time, FPS, memory usage
- **Accuracy**: Gesture recognition rate, false positives
- **User Experience**: Session duration, feature usage, crashes
- **Model Performance**: Precision, recall, F1 score per gesture

### Logging Strategy
- **Error tracking**: Crash reporting with stack traces
- **Performance monitoring**: APM for latency tracking
- **User analytics**: Optional feature usage statistics
- **Model analytics**: Prediction confidence and accuracy

## Future Architecture Considerations

### Planned Enhancements
1. **Multi-hand support**: Recognize two-handed gestures
2. **Facial expressions**: Incorporate facial features for context
3. **Body pose**: Full-body sign language recognition
4. **Real-time collaboration**: Multi-user translation sessions
5. **Federated learning**: Improve models without centralizing data

### Integration Points
- **Healthcare systems**: HL7/FHIR integration for medical settings
- **Telehealth platforms**: Video conferencing integration
- **Education platforms**: LMS integration for deaf education
- **Social media**: Share translations across platforms

## Documentation References
- Architecture decisions: See `architecture-decisions.md`
- Coding standards: See `coding-rules.md`
- Development workflows: See `development-workflows.md`
- Testing strategy: See `testing-rules.md`