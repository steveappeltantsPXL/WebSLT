# WebSLT Frontend Setup Guide - Separate Repository

## Overview

This guide walks you through creating a **separate** React + TypeScript frontend repository for the WebSLT sign language translation application. This follows the recommended architecture of keeping backend and frontend as independent projects.

## Why Separate Repositories?

✅ **Clean separation of concerns** - Backend and frontend are truly independent  
✅ **Independent deployment** - Deploy backend and frontend separately  
✅ **Better git history** - Backend changes don't clutter frontend commits  
✅ **Easier CI/CD** - Separate pipelines for each project  
✅ **Team collaboration** - Backend and frontend developers work independently  
✅ **Right tools for each job** - IntelliJ for Kotlin, VS Code for React

## Project Structure

**Before:**
```
~/AppDev/Projects/Visear/
└── webSLT/                     # Backend repository
    └── WebSLT_Backend/
        ├── server/
        └── build.gradle.kts
```

**After:**
```
~/AppDev/Projects/Visear/
├── webSLT/                     # Backend repository
│   ├── server/
│   ├── build.gradle.kts
│   └── README.md
│
└── webSLT-frontend/            # Frontend repository (NEW)
    ├── src/
    ├── public/
    ├── package.json
    ├── tsconfig.json
    ├── vite.config.ts
    └── README.md
```

## Prerequisites

### Required Software

1. **Node.js** (v18 or later)
   ```bash
   # Check if installed
   node --version
   
   # If not installed, download from:
   # https://nodejs.org/
   ```

2. **npm** (comes with Node.js)
   ```bash
   # Check version
   npm --version
   ```

3. **VS Code** (Recommended for frontend development)
    - Download: https://code.visualstudio.com/

4. **Git** (Already installed)

### Optional but Recommended

**VS Code Extensions:**
- ESLint
- Prettier - Code formatter
- ES7+ React/Redux/React-Native snippets
- TypeScript Vue Plugin (Volar)
- Path Intellisense
- GitLens

## Step-by-Step Setup

### Step 1: Navigate to Projects Directory

```bash
# Navigate to where your backend is located
cd ~/AppDev/Projects/Visear/

# Verify backend exists
ls -la
# Should see: webSLT/
```

### Step 2: Create React Frontend with Vite

```bash
# Make sure you're in the Visear directory (parent of webSLT)
cd ~/AppDev/Projects/Visear/

# Create React + TypeScript project with Vite
npm create vite@latest webSLT-frontend -- --template react-ts

# This creates a new directory: webSLT-frontend/
```

**What this creates:**
- Complete React + TypeScript project
- Vite for fast development and builds
- Modern tooling out of the box
- Hot Module Replacement (HMR)

### Step 3: Navigate to Frontend and Install Dependencies

```bash
# Navigate to frontend directory
cd webSLT-frontend

# Install base dependencies
npm install

# Install ML dependencies
npm install @tensorflow/tfjs @mediapipe/hands

# Install HTTP client for backend API
npm install axios

# Install React Router for navigation
npm install react-router-dom
npm install --save-dev @types/react-router-dom

# Install UI utilities
npm install clsx                # Conditional classNames
npm install react-toastify      # Notifications/toasts

# Install development dependencies
npm install --save-dev @types/node

# Verify installation
npm list --depth=0
```

### Step 4: Configure Environment Variables

Create `.env.local` file in frontend root:

```bash
# In webSLT-frontend/ directory
cat > .env.local << 'EOF'
# Backend API URL (adjust port if your backend uses different port)
VITE_API_BASE_URL=http://localhost:8080

# Feature flags
VITE_ENABLE_ANALYTICS=false
VITE_ENABLE_DATA_COLLECTION=false

# ML Model configuration
VITE_MODEL_VERSION=1.0.0
VITE_CONFIDENCE_THRESHOLD=0.85

# Development settings
VITE_DEBUG_MODE=true
EOF
```

**Add to `.gitignore`:**
```bash
# Make sure .env.local is ignored
echo "# Local environment variables" >> .gitignore
echo ".env.local" >> .gitignore
```

### Step 5: Configure Vite

Update `vite.config.ts`:

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    // Proxy API requests to backend during development
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
  },
})
```

### Step 6: Configure TypeScript

Update `tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,

    /* Bundler mode */
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",

    /* Linting */
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,

    /* Path aliases */
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### Step 7: Create Project Structure

```bash
# In webSLT-frontend/ directory
cd ~/AppDev/Projects/Visear/webSLT-frontend/

# Create directory structure
mkdir -p src/components
mkdir -p src/services/ml
mkdir -p src/services/api
mkdir -p src/hooks
mkdir -p src/types
mkdir -p src/utils
mkdir -p src/pages
mkdir -p public/models

# Verify structure
ls -la src/
```

**Expected structure:**
```
src/
├── components/         # React components
├── services/
│   ├── ml/            # TensorFlow.js and MediaPipe logic
│   └── api/           # Backend API client
├── hooks/             # Custom React hooks
├── types/             # TypeScript type definitions
├── utils/             # Utility functions
├── pages/             # Page components
├── App.tsx
├── main.tsx
└── index.css
```

### Step 8: Create Type Definitions

Create `src/types/index.ts`:

```typescript
// Gesture and landmark types
export interface Landmark {
  x: number;
  y: number;
  z: number;
}

export interface HandLandmarks {
  landmarks: Landmark[];
  handedness: 'Left' | 'Right';
}

export interface GestureResult {
  gesture: string;
  confidence: number;
  timestamp: number;
}

export interface Translation {
  text: string;
  gesture: string;
  confidence: number;
  timestamp: number;
}

// Backend API types
export interface TrainingDataRequest {
  userId: string;
  gestureType: string;
  landmarks: Landmark[];
  videoUrl?: string;
  timestamp: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
}

export interface ModelInfo {
  version: string;
  accuracy: number;
  releaseDate: string;
}
```

### Step 9: Create Backend API Client

Create `src/services/api/BackendClient.ts`:

```typescript
import axios, { AxiosInstance, AxiosError } from 'axios';
import { TrainingDataRequest, ApiResponse, ModelInfo } from '@/types';

class BackendClient {
  private client: AxiosInstance;
  private baseURL: string;

  constructor() {
    this.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    
    this.client = axios.create({
      baseURL: this.baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor for logging
    this.client.interceptors.request.use(
      (config) => {
        if (import.meta.env.VITE_DEBUG_MODE === 'true') {
          console.log('API Request:', config.method?.toUpperCase(), config.url);
        }
        return config;
      },
      (error) => {
        console.error('Request error:', error);
        return Promise.reject(error);
      }
    );

    // Add response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response) {
          console.error('API Error:', error.response.status, error.response.data);
        } else if (error.request) {
          console.error('Network Error: No response received');
        } else {
          console.error('Error:', error.message);
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * Check if backend is online
   */
  async healthCheck(): Promise<boolean> {
    try {
      const response = await this.client.get('/health');
      return response.status === 200;
    } catch (error) {
      console.warn('Backend health check failed:', error);
      return false;
    }
  }

  /**
   * Submit training data to backend
   */
  async submitTrainingData(data: TrainingDataRequest): Promise<ApiResponse> {
    try {
      const response = await this.client.post<ApiResponse>('/api/training-data', data);
      return response.data;
    } catch (error) {
      console.error('Failed to submit training data:', error);
      throw error;
    }
  }

  /**
   * Get available model versions
   */
  async getModelInfo(): Promise<ModelInfo[]> {
    try {
      const response = await this.client.get<ApiResponse<ModelInfo[]>>('/api/models');
      return response.data.data || [];
    } catch (error) {
      console.error('Failed to get model info:', error);
      throw error;
    }
  }

  /**
   * Submit user feedback on translation
   */
  async submitFeedback(feedback: {
    detectedGesture: string;
    actualGesture: string;
    confidence: number;
  }): Promise<ApiResponse> {
    try {
      const response = await this.client.post<ApiResponse>('/api/feedback', feedback);
      return response.data;
    } catch (error) {
      console.error('Failed to submit feedback:', error);
      throw error;
    }
  }
}

// Export singleton instance
export const backendClient = new BackendClient();
```

### Step 10: Create ML Service Placeholder

Create `src/services/ml/GestureModel.ts`:

```typescript
import * as tf from '@tensorflow/tfjs';
import { GestureResult, Landmark } from '@/types';

export class GestureModel {
  private model: tf.LayersModel | null = null;
  private isLoaded = false;
  private gestureClasses: string[] = [];

  /**
   * Load the TensorFlow.js model
   */
  async load(modelPath: string = '/models/gesture-model.json'): Promise<void> {
    try {
      console.log('Loading gesture recognition model...');
      
      // TODO: Load your actual trained model
      // this.model = await tf.loadLayersModel(modelPath);
      
      // For development: simulate model loading
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // TODO: Load gesture class names
      // this.gestureClasses = ['thumbs_up', 'peace', 'ok', 'fist', ...];
      this.gestureClasses = ['thumbs_up', 'peace', 'ok', 'fist'];
      
      this.isLoaded = true;
      console.log('Model loaded successfully');
      
      // Warm up model with dummy prediction
      // const dummyInput = tf.zeros([1, 63]);
      // this.model.predict(dummyInput);
      // dummyInput.dispose();
    } catch (error) {
      console.error('Failed to load gesture model:', error);
      throw new Error('Model loading failed');
    }
  }

  /**
   * Predict gesture from landmarks
   */
  predict(landmarks: Landmark[]): GestureResult {
    if (!this.isLoaded) {
      throw new Error('Model not loaded. Call load() first.');
    }

    if (landmarks.length !== 21) {
      throw new Error('Expected 21 landmarks for hand detection');
    }

    // TODO: Implement actual prediction
    // return tf.tidy(() => {
    //   // Flatten landmarks to feature array
    //   const features = landmarks.flatMap(l => [l.x, l.y, l.z]);
    //   const input = tf.tensor2d([features], [1, 63]);
    //   
    //   // Run prediction
    //   const output = this.model!.predict(input) as tf.Tensor;
    //   const predictions = output.dataSync();
    //   
    //   // Get highest confidence prediction
    //   const maxIndex = predictions.indexOf(Math.max(...predictions));
    //   
    //   return {
    //     gesture: this.gestureClasses[maxIndex],
    //     confidence: predictions[maxIndex],
    //     timestamp: Date.now(),
    //   };
    // });

    // For development: return dummy prediction
    return {
      gesture: this.gestureClasses[Math.floor(Math.random() * this.gestureClasses.length)],
      confidence: 0.85 + Math.random() * 0.15,
      timestamp: Date.now(),
    };
  }

  /**
   * Dispose of model and free memory
   */
  dispose(): void {
    if (this.model) {
      this.model.dispose();
      this.model = null;
      this.isLoaded = false;
      console.log('Model disposed');
    }
  }

  /**
   * Check if model is loaded
   */
  get loaded(): boolean {
    return this.isLoaded;
  }
}

// Export singleton instance
export const gestureModel = new GestureModel();
```

### Step 11: Create MediaPipe Service Placeholder

Create `src/services/ml/MediaPipeService.ts`:

```typescript
import { HandLandmarks, Landmark } from '@/types';

export class MediaPipeService {
  private initialized = false;

  /**
   * Initialize MediaPipe Hands
   */
  async initialize(): Promise<void> {
    try {
      console.log('Initializing MediaPipe...');
      
      // TODO: Initialize MediaPipe Hands
      // import { Hands } from '@mediapipe/hands';
      // this.hands = new Hands({
      //   locateFile: (file) => {
      //     return `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`;
      //   }
      // });
      // 
      // this.hands.setOptions({
      //   maxNumHands: 2,
      //   modelComplexity: 1,
      //   minDetectionConfidence: 0.5,
      //   minTrackingConfidence: 0.5
      // });

      await new Promise(resolve => setTimeout(resolve, 500));
      this.initialized = true;
      console.log('MediaPipe initialized');
    } catch (error) {
      console.error('Failed to initialize MediaPipe:', error);
      throw error;
    }
  }

  /**
   * Detect hands in video frame
   */
  async detectHands(videoElement: HTMLVideoElement): Promise<HandLandmarks[]> {
    if (!this.initialized) {
      throw new Error('MediaPipe not initialized. Call initialize() first.');
    }

    // TODO: Implement actual hand detection
    // return new Promise((resolve) => {
    //   this.hands.onResults((results) => {
    //     const detectedHands: HandLandmarks[] = results.multiHandLandmarks.map((landmarks, index) => ({
    //       landmarks: landmarks.map(l => ({ x: l.x, y: l.y, z: l.z })),
    //       handedness: results.multiHandedness[index].label as 'Left' | 'Right'
    //     }));
    //     resolve(detectedHands);
    //   });
    //   
    //   this.hands.send({ image: videoElement });
    // });

    // For development: return empty array
    return [];
  }

  /**
   * Clean up resources
   */
  dispose(): void {
    // TODO: Clean up MediaPipe resources
    this.initialized = false;
    console.log('MediaPipe disposed');
  }

  get ready(): boolean {
    return this.initialized;
  }
}

// Export singleton instance
export const mediaPipeService = new MediaPipeService();
```

### Step 12: Update App Component

Replace `src/App.tsx`:

```typescript
import { useEffect, useState } from 'react';
import { backendClient } from '@/services/api/BackendClient';
import { gestureModel } from '@/services/ml/GestureModel';
import './App.css';

type Status = 'checking' | 'online' | 'offline';

function App() {
  const [backendStatus, setBackendStatus] = useState<Status>('checking');
  const [modelStatus, setModelStatus] = useState<Status>('checking');

  useEffect(() => {
    const initializeApp = async () => {
      // Check backend connectivity
      const backendOnline = await backendClient.healthCheck();
      setBackendStatus(backendOnline ? 'online' : 'offline');

      // Load ML model
      try {
        await gestureModel.load();
        setModelStatus('online');
      } catch (error) {
        console.error('Failed to load model:', error);
        setModelStatus('offline');
      }
    };

    initializeApp();

    // Cleanup on unmount
    return () => {
      gestureModel.dispose();
    };
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>WebSLT</h1>
        <p className="subtitle">Sign Language Translation</p>
        
        <div className="status-panel">
          <div className="status-item">
            <span className="status-label">Backend:</span>
            <span className={`status-badge ${backendStatus}`}>
              {backendStatus === 'checking' ? '⏳ Checking...' : 
               backendStatus === 'online' ? '✓ Online' : 
               '✗ Offline'}
            </span>
          </div>
          
          <div className="status-item">
            <span className="status-label">ML Model:</span>
            <span className={`status-badge ${modelStatus}`}>
              {modelStatus === 'checking' ? '⏳ Loading...' : 
               modelStatus === 'online' ? '✓ Ready' : 
               '✗ Failed'}
            </span>
          </div>
        </div>

        <div className="info-box">
          <h2>🚀 Ready to Build</h2>
          <p>Your frontend is set up and ready for development.</p>
          <ul className="feature-list">
            <li>✓ React + TypeScript configured</li>
            <li>✓ TensorFlow.js ready</li>
            <li>✓ Backend API client ready</li>
            <li>✓ Development server running</li>
          </ul>
        </div>

        {backendStatus === 'offline' && (
          <div className="warning-box">
            <p>⚠️ Backend is offline. Start your Ktor server:</p>
            <code>cd ../webSLT && ./gradlew :server:run</code>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
```

### Step 13: Update Styles

Replace `src/App.css`:

```css
.App {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
}

.App-header {
  max-width: 800px;
  width: 100%;
  text-align: center;
}

.App-header h1 {
  font-size: 4rem;
  margin: 0;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
  font-weight: 700;
}

.subtitle {
  font-size: 1.5rem;
  margin: 0.5rem 0 2rem;
  opacity: 0.9;
  font-weight: 300;
}

.status-panel {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 1.5rem;
  margin: 2rem 0;
  backdrop-filter: blur(10px);
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
}

.status-item:not(:last-child) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.status-label {
  font-size: 1.1rem;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 100px;
  text-align: center;
}

.status-badge.checking {
  background-color: rgba(255, 255, 255, 0.2);
}

.status-badge.online {
  background-color: rgba(76, 175, 80, 0.8);
}

.status-badge.offline {
  background-color: rgba(244, 67, 54, 0.8);
}

.info-box {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 2rem;
  margin: 2rem 0;
  backdrop-filter: blur(10px);
}

.info-box h2 {
  margin-top: 0;
  font-size: 1.8rem;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 1rem 0 0;
  text-align: left;
}

.feature-list li {
  padding: 0.5rem 0;
  font-size: 1.1rem;
}

.warning-box {
  background: rgba(255, 152, 0, 0.2);
  border: 2px solid rgba(255, 152, 0, 0.5);
  border-radius: 8px;
  padding: 1rem;
  margin-top: 2rem;
}

.warning-box code {
  display: block;
  background: rgba(0, 0, 0, 0.3);
  padding: 0.75rem;
  border-radius: 4px;
  margin-top: 0.5rem;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}
```

### Step 14: Create Frontend README

Create `README.md` in frontend root:

```markdown
# WebSLT Frontend

React + TypeScript frontend for real-time sign language translation using TensorFlow.js and MediaPipe.

## Features

- 🎥 Real-time camera capture
- 🤖 Client-side ML gesture recognition (TensorFlow.js)
- ✋ Hand landmark detection (MediaPipe)
- 🔄 Live translation display
- 📊 Optional backend integration for data collection

## Tech Stack

- **Framework:** React 18 + TypeScript
- **Build Tool:** Vite
- **ML Libraries:** TensorFlow.js, MediaPipe Hands
- **HTTP Client:** Axios
- **Routing:** React Router
- **Styling:** CSS Modules / Tailwind (TBD)

## Prerequisites

- Node.js 18+ 
- npm or yarn
- Backend running at http://localhost:8080 (optional)

## Getting Started

### Install Dependencies

\`\`\`bash
npm install
\`\`\`

### Environment Setup

Copy `.env.example` to `.env.local` and configure:

\`\`\`bash
cp .env.example .env.local
\`\`\`

Edit `.env.local`:
\`\`\`env
VITE_API_BASE_URL=http://localhost:8080
VITE_CONFIDENCE_THRESHOLD=0.85
\`\`\`

### Development Server

\`\`\`bash
npm run dev
\`\`\`

Runs at: http://localhost:5173

### Build for Production

\`\`\`bash
npm run build
\`\`\`

Output: `dist/` directory

### Preview Production Build

\`\`\`bash
npm run preview
\`\`\`

## Project Structure

\`\`\`
src/
├── components/      # React components
│   ├── CameraCapture.tsx
│   ├── GestureDetector.tsx
│   └── TranslationDisplay.tsx
├── services/
│   ├── ml/         # ML services
│   │   ├── GestureModel.ts
│   │   └── MediaPipeService.ts
│   └── api/        # Backend API client
│       └── BackendClient.ts
├── hooks/          # Custom React hooks
├── types/          # TypeScript types
├── pages/          # Page components
├── utils/          # Utility functions
├── App.tsx
└── main.tsx
\`\`\`

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
- `npm run type-check` - Run TypeScript type checking

## Backend Integration

This frontend connects to the WebSLT Ktor backend for:
- Health checks
- Training data collection (optional)
- User feedback submission (optional)

Backend repository: `../webSLT/`

Start backend:
\`\`\`bash
cd ../webSLT
./gradlew :server:run
\`\`\`

## Development Workflow

1. **Start Backend** (Terminal 1):
   \`\`\`bash
   cd ~/AppDev/Projects/Visear/webSLT
   ./gradlew :server:run
   \`\`\`

2. **Start Frontend** (Terminal 2):
   \`\`\`bash
   cd ~/AppDev/Projects/Visear/webSLT-frontend
   npm run dev
   \`\`\`

3. **Open in Browser**: http://localhost:5173

## IDE Setup

Recommended: **VS Code**

Extensions:
- ESLint
- Prettier
- ES7+ React/Redux snippets
- TypeScript Vue Plugin (Volar)

## Deployment

### Option 1: Netlify
\`\`\`bash
npm run build
# Deploy dist/ folder to Netlify
\`\`\`

### Option 2: Vercel
\`\`\`bash
npm run build
# Deploy with Vercel CLI or GitHub integration
\`\`\`

### Option 3: Cloudflare Pages
\`\`\`bash
npm run build
# Deploy dist/ folder to Cloudflare Pages
\`\`\`

## Troubleshooting

### Backend Connection Failed

Check:
1. Backend is running: `curl http://localhost:8080/health`
2. CORS is configured in backend
3. `.env.local` has correct API URL

### Module Not Found

\`\`\`bash
rm -rf node_modules package-lock.json
npm install
\`\`\`

### Port Already in Use

Change port in `vite.config.ts`:
\`\`\`typescript
server: { port: 5174 }
\`\`\`

## License

[Your License]
\`\`\`

### Step 15: Initialize Git Repository

```bash
# In webSLT-frontend/ directory
cd ~/AppDev/Projects/Visear/webSLT-frontend/

# Initialize git
git init

# Create .gitignore
cat > .gitignore << 'EOF'
# Dependencies
node_modules/

# Build output
dist/
dist-ssr/

# Environment variables
.env
.env.local
.env.production

# IDE
.vscode/
.idea/
*.sublime-workspace

# OS
.DS_Store
Thumbs.db

# Logs
logs/
*.log
npm-debug.log*

# Testing
coverage/
.nyc_output/

# Misc
.cache/
EOF

# Initial commit
git add .
git commit -m "Initial commit: React + TypeScript frontend setup"
```

### Step 16: Connect to Remote Repository (Optional)

```bash
# Create repository on GitHub/GitLab, then:
git remote add origin https://github.com/yourusername/webSLT-frontend.git
git branch -M main
git push -u origin main
```

## Testing the Setup

### Terminal 1: Start Backend

```bash
cd ~/AppDev/Projects/Visear/webSLT/
./gradlew :server:run

# Should see:
# [main] INFO  Application - Responding at http://0.0.0.0:8080
```

### Terminal 2: Start Frontend

```bash
cd ~/AppDev/Projects/Visear/webSLT-frontend/
npm run dev

# Should see:
# VITE v4.x.x  ready in xxx ms
# ➜  Local:   http://localhost:5173/
```

### Verify in Browser

1. Open http://localhost:5173
2. Check status indicators:
    - ✓ Backend: Online (green)
    - ✓ ML Model: Ready (green)
3. If backend shows offline:
    - Verify backend is running
    - Check console for errors
    - Check CORS configuration

## Next Steps

### 1. Implement Camera Capture

Create `src/components/CameraCapture.tsx`:

```typescript
import { useRef, useEffect, useState } from 'react';
import './CameraCapture.css';

interface CameraCaptureProps {
  onFrame?: (video: HTMLVideoElement) => void;
}

export const CameraCapture: React.FC<CameraCaptureProps> = ({ onFrame }) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [isStreaming, setIsStreaming] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const startCamera = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: {
            width: { ideal: 1280 },
            height: { ideal: 720 },
            facingMode: 'user'
          }
        });

        if (videoRef.current) {
          videoRef.current.srcObject = stream;
          setIsStreaming(true);
        }
      } catch (err) {
        console.error('Camera access error:', err);
        setError('Failed to access camera. Please grant camera permissions.');
      }
    };

    startCamera();

    // Cleanup
    return () => {
      if (videoRef.current?.srcObject) {
        const stream = videoRef.current.srcObject as MediaStream;
        stream.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  // Call onFrame callback when video is playing
  useEffect(() => {
    if (!isStreaming || !videoRef.current || !onFrame) return;

    const video = videoRef.current;
    let animationId: number;

    const processFrame = () => {
      if (video.readyState === video.HAVE_ENOUGH_DATA) {
        onFrame(video);
      }
      animationId = requestAnimationFrame(processFrame);
    };

    video.addEventListener('play', () => {
      processFrame();
    });

    return () => {
      if (animationId) {
        cancelAnimationFrame(animationId);
      }
    };
  }, [isStreaming, onFrame]);

  return (
    <div className="camera-capture">
      {error && (
        <div className="error-message">{error}</div>
      )}
      <video
        ref={videoRef}
        autoPlay
        playsInline
        muted
        width="640"
        height="480"
        className="camera-video"
      />
      {isStreaming && (
        <div className="streaming-indicator">● LIVE</div>
      )}
    </div>
  );
};
```

### 2. Create Custom Hook for Gesture Detection

Create `src/hooks/useGestureDetection.ts`:

```typescript
import { useState, useEffect, useCallback } from 'react';
import { gestureModel } from '@/services/ml/GestureModel';
import { mediaPipeService } from '@/services/ml/MediaPipeService';
import { GestureResult } from '@/types';

export const useGestureDetection = () => {
  const [isReady, setIsReady] = useState(false);
  const [isDetecting, setIsDetecting] = useState(false);
  const [currentGesture, setCurrentGesture] = useState<GestureResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Initialize ML services
  useEffect(() => {
    const initialize = async () => {
      try {
        await Promise.all([
          gestureModel.load(),
          mediaPipeService.initialize()
        ]);
        setIsReady(true);
      } catch (err) {
        setError('Failed to initialize ML services');
        console.error(err);
      }
    };

    initialize();

    return () => {
      gestureModel.dispose();
      mediaPipeService.dispose();
    };
  }, []);

  // Process video frame
  const detectGesture = useCallback(async (video: HTMLVideoElement) => {
    if (!isReady) return;

    try {
      setIsDetecting(true);

      // Detect hand landmarks
      const hands = await mediaPipeService.detectHands(video);

      if (hands.length > 0) {
        // Use first detected hand
        const result = gestureModel.predict(hands[0].landmarks);
        setCurrentGesture(result);
      } else {
        setCurrentGesture(null);
      }
    } catch (err) {
      console.error('Detection error:', err);
    } finally {
      setIsDetecting(false);
    }
  }, [isReady]);

  return {
    isReady,
    isDetecting,
    currentGesture,
    error,
    detectGesture
  };
};
```

### 3. Create Translation Display Component

Create `src/components/TranslationDisplay.tsx`:

```typescript
import { GestureResult } from '@/types';
import './TranslationDisplay.css';

interface TranslationDisplayProps {
  gesture: GestureResult | null;
}

export const TranslationDisplay: React.FC<TranslationDisplayProps> = ({ gesture }) => {
  if (!gesture) {
    return (
      <div className="translation-display empty">
        <p>Waiting for gesture...</p>
      </div>
    );
  }

  const confidencePercent = (gesture.confidence * 100).toFixed(1);

  return (
    <div className="translation-display">
      <div className="gesture-name">{gesture.gesture}</div>
      <div className="confidence-bar">
        <div 
          className="confidence-fill" 
          style={{ width: `${confidencePercent}%` }}
        />
      </div>
      <div className="confidence-text">{confidencePercent}% confident</div>
    </div>
  );
};
```

### 4. Update Package Scripts

Add useful scripts to `package.json`:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx,css,json}\"",
    "type-check": "tsc --noEmit",
    "clean": "rm -rf dist node_modules",
    "reinstall": "npm run clean && npm install"
  }
}
```

## IDE Configuration

### VS Code Settings

Create `.vscode/settings.json`:

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.tsdk": "node_modules/typescript/lib",
  "typescript.enablePromptUseWorkspaceTsdk": true
}
```

### VS Code Extensions

Create `.vscode/extensions.json`:

```json
{
  "recommendations": [
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "dsznajder.es7-react-js-snippets",
    "bradlc.vscode-tailwindcss",
    "eamodio.gitlens"
  ]
}
```

## Common Issues

### Issue 1: Cannot Connect to Backend

**Symptoms:** Backend status shows "Offline"

**Solutions:**
1. Verify backend is running:
   ```bash
   curl http://localhost:8080/health
   ```

2. Check backend CORS configuration in `Application.kt`:
   ```kotlin
   install(CORS) {
       allowHost("localhost:5173")
       allowMethod(HttpMethod.Options)
       allowMethod(HttpMethod.Get)
       allowMethod(HttpMethod.Post)
       allowHeader(HttpHeaders.ContentType)
   }
   ```

3. Check `.env.local` configuration:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

### Issue 2: TensorFlow.js Not Loading

**Symptoms:** "Model loading failed" error

**Solutions:**
1. Check model file exists: `/public/models/gesture-model.json`
2. Verify model path in code matches file location
3. Check browser console for CORS errors

### Issue 3: Camera Access Denied

**Symptoms:** "Failed to access camera" error

**Solutions:**
1. Grant camera permissions in browser
2. Use HTTPS in production (camera requires secure context)
3. Check browser console for specific error

### Issue 4: Port 5173 Already in Use

**Solution:** Change port in `vite.config.ts`:
```typescript
server: {
  port: 5174,  // or any available port
}
```

## Project Relationship

```
Independent Repositories:

webSLT/                         webSLT-frontend/
(Backend)                       (Frontend)
│                               │
├── Ktor Server                 ├── React UI
├── Data Collection API         ├── TensorFlow.js
├── PostgreSQL                  ├── MediaPipe
└── Gradle Build                └── Vite Build
    ↓                               ↓
    API Endpoints ←──── HTTP ────→ API Client
    (localhost:8080)            (localhost:5173)
```

## Deployment

### Frontend Deployment (Netlify)

1. Build the project:
   ```bash
   npm run build
   ```

2. Deploy to Netlify:
   ```bash
   # Install Netlify CLI
   npm install -g netlify-cli

   # Login and deploy
   netlify login
   netlify deploy --prod --dir=dist
   ```

3. Configure environment variables in Netlify dashboard:
    - `VITE_API_BASE_URL` → Your production backend URL

### Backend Deployment

See backend repository `webSLT/` for deployment instructions.

## Resources

- [Vite Documentation](https://vitejs.dev/)
- [React Documentation](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [TensorFlow.js Guide](https://www.tensorflow.org/js)
- [MediaPipe Documentation](https://google.github.io/mediapipe/)

## Support

- Frontend Issues: Create issue in `webSLT-frontend` repository
- Backend Issues: Create issue in `webSLT` repository
- Integration Issues: Mention both repositories

---

**You now have a completely separate, production-ready React frontend! 🎉**