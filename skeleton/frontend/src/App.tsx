import { Routes, Route } from 'react-router-dom'

function Welcome() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-purple-50">
      <div className="max-w-xl text-center px-6 py-12 bg-white/80 backdrop-blur rounded-2xl shadow-xl border border-indigo-100">
        <div className="text-6xl mb-4">🚀</div>
        <h1 className="text-4xl font-bold tracking-tight bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
          Welcome to @grails.app.name@
        </h1>
        <p className="mt-4 text-gray-600 leading-relaxed">
          Grails 7 + React 19 + Vite 6 + TypeScript + Tailwind v4
        </p>
        <p className="mt-6 text-sm text-gray-500">
          Edit <code className="px-1.5 py-0.5 rounded bg-gray-100 text-indigo-700 font-mono text-xs">frontend/src/App.tsx</code> to get started.
        </p>
      </div>
    </div>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Welcome />} />
    </Routes>
  )
}
