import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import { getAuthRedirectPath } from '../utils/auth'

function PublicOnlyRoute({ children }) {
  const location = useLocation()
  const user = useAuthStore((state) => state.user)
  const accessToken = useAuthStore((state) => state.accessToken)

  if (user && accessToken) {
    return <Navigate to={getAuthRedirectPath(user)} replace state={{ from: location }} />
  }

  return children
}

export default PublicOnlyRoute
