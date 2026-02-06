# Frontend Context - WebSLT

**Agents**: general-purpose, Explore (for frontend work)
**Last Updated**: Feb 6, 2026
**Status**: Scaffolding Only (Phase 0)

---

## Quick Facts

| Aspect | Implementation |
|--------|----------------|
| **Framework** | React 18 + TypeScript |
| **Build Tool** | Vite 7.1.6 |
| **Dev Server** | Port 5173 (Vite default) |
| **Backend Port** | 8080 (Ktor server) |
| **Shared Module** | Kotlin/JS compiled library |
| **ML Framework** | TensorFlow.js (planned) + MediaPipe Hands |
| **State Management** | Not yet implemented (planned: Zustand or Context) |
| **Routing** | Not yet implemented (planned: react-router-dom) |

---

## Architecture Overview

### Critical Constraint

**ALL ML inference runs in-browser.** The backend is ONLY for:
- User authentication (JWT)
- Training data submission (landmark JSON)
- Model downloads (.tflite files)
- Analytics events

**The frontend handles:**
- Camera capture
- Hand landmark detection (MediaPipe)
- Gesture recognition (TensorFlow.js)
- Real-time translation display

### Current Structure

```
webApp/
├── index.html                        # Entry point
├── src/
│   ├── index.tsx                     # React root, renders App
│   ├── components/
│   │   ├── Greeting/                 # Demo component (uses Kotlin/JS shared)
│   │   │   ├── Greeting.tsx
│   │   │   └── Greeting.css
│   │   └── JSLogo/                   # Demo logo component
│   │       ├── JSLogo.tsx
│   │       └── JSLogo.css
├── package.json                      # Dependencies
├── tsconfig.json                     # TypeScript config (strict mode)
├── vite.config.ts                    # Vite build config
└── node_modules/
```

---

## Current Implementation (Phase 0)

### ✅ What Exists

**Demo Components:**
- `Greeting` - Demonstrates Kotlin/JS shared module integration
- `JSLogo` - SVG logo component

**Shared Module Integration:**
```typescript
import { Greeting } from 'shared'

const greeting = new Greeting()
console.log(greeting.greet())  // "Hello, Kotlin/JS!"
```

**Build Setup:**
- TypeScript strict mode enabled
- Vite dev server configured
- Hot module replacement (HMR) working

### 📋 What's Planned

**Phase 3 (Frontend Foundation):**
- Camera capture component
- MediaPipe hand detection integration
- TensorFlow.js gesture recognition service
- Translation display component
- Backend API client (axios or fetch wrapper)
- Routing (react-router-dom)
- State management (Zustand or Context API)

**Phase 4 (Integration):**
- Auth flow (login, register, token management)
- Training data contribution UI
- Model download and caching
- Analytics event tracking

---

## Kotlin/JS Shared Module

### How It Works

**Build Process:**
```bash
# 1. Build Kotlin/JS library
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution

# 2. Output location
build/dist/js/developmentLibrary/

# 3. Linked via npm workspaces
webApp/node_modules/shared -> ../../build/dist/js/developmentLibrary/
```

**Usage in TypeScript:**
```typescript
// Import from shared module
import { Greeting, Constants } from 'shared'

// Use Kotlin classes
const greeting = new Greeting()
const platform = greeting.greet()  // "Hello, Kotlin/JS!"

// Access constants
const serverPort = Constants.SERVER_PORT  // 8080
```

**Current Exports:**
- `Greeting` - Demo class with `greet()` method
- `Constants.SERVER_PORT` - Server port number (8080)

**Future Exports (Planned):**
- API endpoint paths (`/api/v1/auth/register`, etc.)
- Shared data models (landmark structures)
- Validation functions (email, password)

---

## Component Patterns

### Functional Components Only

```typescript
// ✅ Good - Functional component with TypeScript
interface GreetingProps {
  name: string
  onGreet?: () => void
}

export const Greeting: React.FC<GreetingProps> = ({ name, onGreet }) => {
  const [count, setCount] = useState(0)

  useEffect(() => {
    console.log(`Greeting rendered: ${count}`)
  }, [count])

  return (
    <div className="greeting">
      <h1>Hello, {name}!</h1>
      <button onClick={() => setCount(count + 1)}>Count: {count}</button>
    </div>
  )
}

// ❌ Bad - Class component (don't use)
class Greeting extends React.Component { ... }
```

### Component File Structure

```
components/
├── ComponentName/
│   ├── ComponentName.tsx         # Component logic
│   ├── ComponentName.css         # Component styles
│   └── ComponentName.test.tsx    # Component tests (planned)
```

---

## Planned ML Pipeline

### 1. Camera Capture

```typescript
// services/cameraService.ts
export class CameraService {
  private videoElement: HTMLVideoElement

  async startCamera(): Promise<MediaStream> {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { width: 1280, height: 720 }
    })
    this.videoElement.srcObject = stream
    return stream
  }

  stopCamera(): void {
    const stream = this.videoElement.srcObject as MediaStream
    stream?.getTracks().forEach(track => track.stop())
  }
}
```

### 2. MediaPipe Hand Detection

```typescript
// services/landmarkDetectionService.ts
import { Hands } from '@mediapipe/hands'

export class LandmarkDetectionService {
  private hands: Hands

  async initialize(): Promise<void> {
    this.hands = new Hands({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
    })

    this.hands.setOptions({
      maxNumHands: 2,
      modelComplexity: 1,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5
    })
  }

  async detectLandmarks(videoFrame: HTMLVideoElement): Promise<HandLandmarks[]> {
    return new Promise((resolve) => {
      this.hands.onResults((results) => {
        resolve(results.multiHandLandmarks || [])
      })
      this.hands.send({ image: videoFrame })
    })
  }
}
```

### 3. TensorFlow.js Gesture Recognition

```typescript
// services/gestureRecognitionService.ts
import * as tf from '@tensorflow/tfjs'

export class GestureRecognitionService {
  private model: tf.LayersModel

  async loadModel(modelUrl: string): Promise<void> {
    this.model = await tf.loadLayersModel(modelUrl)
  }

  async predictGesture(landmarks: number[][]): Promise<string> {
    const input = tf.tensor2d([landmarks.flat()])
    const prediction = this.model.predict(input) as tf.Tensor
    const result = await prediction.data()
    return this.getTopPrediction(result)
  }
}
```

---

## API Client Pattern (Planned)

### API Service

```typescript
// services/apiClient.ts
import axios, { AxiosInstance } from 'axios'

class ApiClient {
  private client: AxiosInstance
  private accessToken: string | null = null

  constructor() {
    this.client = axios.create({
      baseURL: 'http://localhost:8080/api/v1',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    // Add auth interceptor
    this.client.interceptors.request.use((config) => {
      if (this.accessToken) {
        config.headers.Authorization = `Bearer ${this.accessToken}`
      }
      return config
    })
  }

  setToken(token: string): void {
    this.accessToken = token
  }

  // Auth endpoints
  async register(email: string, password: string) {
    const response = await this.client.post('/auth/register', { email, password })
    return response.data
  }

  async login(email: string, password: string) {
    const response = await this.client.post('/auth/login', { email, password })
    this.setToken(response.data.data.accessToken)
    return response.data
  }

  // Training data
  async submitTrainingData(landmarks: number[][], label: string) {
    return this.client.post('/training-data', { landmarks, label })
  }
}

export const apiClient = new ApiClient()
```

---

## State Management (Planned)

### Zustand Store

```typescript
// stores/authStore.ts
import create from 'zustand'

interface AuthState {
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  isAuthenticated: false,

  login: async (email, password) => {
    const response = await apiClient.login(email, password)
    set({
      user: response.data.user,
      accessToken: response.data.accessToken,
      isAuthenticated: true
    })
  },

  logout: () => {
    set({ user: null, accessToken: null, isAuthenticated: false })
  }
}))
```

---

## Dependencies to Add

### ML & Camera
```bash
npm install @tensorflow/tfjs @mediapipe/hands
```

### API Client
```bash
npm install axios
```

### Routing
```bash
npm install react-router-dom
npm install --save-dev @types/react-router-dom
```

### State Management
```bash
npm install zustand
```

### Testing
```bash
npm install --save-dev vitest @testing-library/react @testing-library/jest-dom
```

---

## Code Style & Conventions

### TypeScript Strict Mode

**tsconfig.json:**
```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  }
}
```

**Never use `any`:**
```typescript
// ❌ Bad
const data: any = await fetch(...)

// ✅ Good
interface ApiResponse<T> {
  success: boolean
  data: T | null
  error: string | null
}

const data: ApiResponse<User> = await fetch(...)
```

### Import Organization

```typescript
// 1. React imports
import React, { useState, useEffect } from 'react'

// 2. Third-party imports
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

// 3. Shared module imports
import { Constants } from 'shared'

// 4. Local imports
import { apiClient } from '../services/apiClient'
import { useAuthStore } from '../stores/authStore'
import './ComponentName.css'
```

### Component Props

```typescript
// ✅ Good - Interface for props
interface ButtonProps {
  label: string
  onClick: () => void
  disabled?: boolean
  variant?: 'primary' | 'secondary'
}

export const Button: React.FC<ButtonProps> = ({
  label,
  onClick,
  disabled = false,
  variant = 'primary'
}) => { ... }
```

---

## Performance Considerations

### ML Inference Optimization

- Use TensorFlow.js WebGL backend for GPU acceleration
- Throttle landmark detection (max 30 FPS)
- Use Web Workers for heavy computations
- Cache loaded models in IndexedDB
- Optimize model size (quantization)

### React Performance

- Use `React.memo()` for expensive components
- Use `useMemo()` for expensive calculations
- Use `useCallback()` for event handlers passed as props
- Lazy load routes with `React.lazy()`
- Virtualize long lists (react-window)

---

## Build & Dev Workflow

### Development

```bash
# 1. Build shared module (required first)
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution

# 2. Install frontend dependencies
cd webApp && npm install

# 3. Start dev server
npm run dev  # Vite dev server on http://localhost:5173
```

### Production Build

```bash
# Build optimized production bundle
cd webApp && npm run build

# Preview production build
npm run preview
```

---

## Current Status

**Implemented:**
- ✅ React 18 + TypeScript + Vite setup
- ✅ Kotlin/JS shared module integration
- ✅ Demo components (Greeting, JSLogo)
- ✅ HMR working

**Not Yet Implemented:**
- ⏳ Camera capture
- ⏳ MediaPipe integration
- ⏳ TensorFlow.js integration
- ⏳ API client
- ⏳ Routing
- ⏳ State management
- ⏳ Auth UI
- ⏳ Testing setup

---

## Key References

- **Frontend Rules**: `.claude/rules/frontend.md`
- **Shared Module**: `.claude/rules/shared.md`
- **Architecture**: `docs/Architecture.md`
- **Setup Commands**: `docs/Frontend-Setup-Commands.md`
- **Current Code**: `webApp/src/`

---

**When implementing frontend features:**
1. Build shared module first (`./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`)
2. Use functional components with TypeScript
3. Keep ML inference in browser (never call backend for translation)
4. Use strict TypeScript (no `any`)
5. Co-locate component files (Component.tsx + Component.css)
6. Test with `npm run dev` and check console for errors
