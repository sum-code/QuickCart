import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import { getAuthRedirectPath, getPrimaryRole } from '../utils/auth'

function ProtectedRoute({ children, requiredRole }) {
  const location = useLocation()
  const user = useAuthStore((state) => state.user)
  const accessToken = useAuthStore((state) => state.accessToken)

  if (!user || !accessToken) {
    return <Navigate to="/auth/login" replace state={{ from: location }} />
  }

  if (requiredRole && getPrimaryRole(user) !== requiredRole) {
    return <Navigate to={getAuthRedirectPath(user)} replace />
  }

  return children
}

export default ProtectedRoute
