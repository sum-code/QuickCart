import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import AuthLayout from '../../components/auth/AuthLayout'
import AuthInput from '../../components/auth/AuthInput'
import SubmitButton from '../../components/ui/SubmitButton'
import FormMessage from '../../components/ui/FormMessage'
import { loginRequest } from '../../services/authService'
import { useAuthStore } from '../../store/useAuthStore'
import { getAuthRedirectPath, getErrorMessage } from '../../utils/auth'

function LoginPage() {
  const navigate = useNavigate()
  const setAuthSession = useAuthStore((state) => state.setAuthSession)

  const [form, setForm] = useState({
    email: '',
    password: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const onSubmit = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      const response = await loginRequest(form)
      setAuthSession(response)
      navigate(getAuthRedirectPath(response.user), { replace: true })
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'Login failed.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout title="Welcome Back" subtitle="Sign in to continue shopping or manage your store.">
      <form className="space-y-4" onSubmit={onSubmit}>
        <AuthInput
          label="Email"
          type="email"
          name="email"
          value={form.email}
          onChange={onChange}
          placeholder="you@example.com"
          required
        />
        <AuthInput
          label="Password"
          type="password"
          name="password"
          value={form.password}
          onChange={onChange}
          placeholder="Enter your password"
          required
        />
        <SubmitButton label="Login" loading={loading} />
        <FormMessage error={error} />
      </form>

      <p className="mt-6 text-sm text-slate-600">
        New to QuickCart?{' '}
        <Link to="/auth/register" className="font-semibold text-brand-ocean hover:text-brand-deep">
          Create account
        </Link>
      </p>
    </AuthLayout>
  )
}

export default LoginPage
