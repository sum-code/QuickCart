import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthLayout from '../../components/auth/AuthLayout'
import FormMessage from '../../components/ui/FormMessage'
import { getCurrentUserRequest } from '../../services/authService'
import { useAuthStore } from '../../store/useAuthStore'
import { getAuthRedirectPath } from '../../utils/auth'

function OAuth2CallbackPage() {
  const navigate = useNavigate()
  const setAuthSession = useAuthStore((state) => state.setAuthSession)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const [error, setError] = useState('')

  useEffect(() => {
    const run = async () => {
      const params = new URLSearchParams(window.location.search)
      const token = params.get('token')
      const email = params.get('email')
      const rolesParam = params.get('roles')
      const oauthError = params.get('error')

      if (oauthError) {
        setError('Google login failed. Please try again.')
        return
      }

      if (!token) {
        setError('Missing token from Google login callback.')
        return
      }

      const roles = rolesParam
        ? rolesParam
            .split(',')
            .map((role) => role.trim())
            .filter(Boolean)
        : []

      if (email && roles.length > 0) {
        const user = { email, roles }
        setAuthSession({ accessToken: token, user })
        navigate(getAuthRedirectPath(user), { replace: true })
        return
      }

      try {
        setAuthSession({ accessToken: token, user: null })
        const user = await getCurrentUserRequest()
        setAuthSession({ accessToken: token, user })
        navigate(getAuthRedirectPath(user), { replace: true })
      } catch {
        clearAuth()
        setError('Could not complete Google login. Please try again.')
      }
    }

    run()
  }, [clearAuth, navigate, setAuthSession])

  return (
    <AuthLayout title="Connecting Google Account" subtitle="Finalizing secure sign-in...">
      {error ? (
        <FormMessage error={error} />
      ) : (
        <p className="rounded-xl bg-brand-ocean/10 px-4 py-3 text-sm font-medium text-brand-deep">Please wait...</p>
      )}
    </AuthLayout>
  )
}

export default OAuth2CallbackPage
