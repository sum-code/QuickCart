import { Navigate, Route, Routes } from 'react-router-dom'
import PublicOnlyRoute from './routes/PublicOnlyRoute'
import ProtectedRoute from './routes/ProtectedRoute'
import AdminRoute from './routes/AdminRoute'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import OAuth2CallbackPage from './pages/auth/OAuth2CallbackPage'
import AdminDashboardPage from './pages/admin/AdminDashboardPage'
import CustomerHomePage from './pages/customer/CustomerHomePage'
import NotFoundPage from './pages/shared/NotFoundPage'

function App() {
  return (
    <Routes>
      <Route path="/auth" element={<Navigate to="/auth/login" replace />} />
      <Route
        path="/auth/login"
        element={
          <PublicOnlyRoute>
            <LoginPage />
          </PublicOnlyRoute>
        }
      />
      <Route
        path="/auth/register"
        element={
          <PublicOnlyRoute>
            <RegisterPage />
          </PublicOnlyRoute>
        }
      />
      <Route path="/auth/oauth2/callback" element={<OAuth2CallbackPage />} />
      <Route
        path="/admin/dashboard"
        element={
          <AdminRoute>
            <AdminDashboardPage />
          </AdminRoute>
        }
      />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <CustomerHomePage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

export default App
