import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import AuthLayout from '../../components/auth/AuthLayout'
import AuthInput from '../../components/auth/AuthInput'
import SubmitButton from '../../components/ui/SubmitButton'
import FormMessage from '../../components/ui/FormMessage'
import GoogleLoginButton from '../../components/auth/GoogleLoginButton'
import { registerRequest } from '../../services/authService'
import { useAuthStore } from '../../store/useAuthStore'
import { getAuthRedirectPath, getErrorMessage } from '../../utils/auth'

function RegisterPage() {
  const navigate = useNavigate()
  const setAuthSession = useAuthStore((state) => state.setAuthSession)

  const [form, setForm] = useState({
    email: '',
    password: '',
    role: 'USER',
    adminCode: '',
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
      const payload = {
        email: form.email,
        password: form.password,
        role: form.role,
      }

      if (form.role === 'ADMIN') {
        payload.adminCode = form.adminCode
      }

      const response = await registerRequest(payload)
      setAuthSession(response)
      navigate(getAuthRedirectPath(response.user), { replace: true })
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'Registration failed.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout title="Create Account" subtitle="Register as customer or admin using your admin code.">
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
          placeholder="Create a secure password"
          required
        />

        <label className="block text-sm font-medium text-slate-700">
          <span>Role</span>
          <select
            name="role"
            value={form.role}
            onChange={onChange}
            className="mt-2 w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-brand-ocean focus:ring-2 focus:ring-brand-ocean/25"
          >
            <option value="USER">Customer</option>
            <option value="ADMIN">Admin</option>
          </select>
        </label>

        {form.role === 'ADMIN' && (
          <AuthInput
            label="Admin Registration Code"
            type="password"
            name="adminCode"
            value={form.adminCode}
            onChange={onChange}
            placeholder="Enter admin code"
            required
          />
        )}

        <SubmitButton label="Create Account" loading={loading} />
        <FormMessage error={error} />
      </form>

      <div className="my-6 flex items-center gap-3">
        <div className="h-px flex-1 bg-slate-300" />
        <span className="text-xs font-semibold uppercase tracking-[0.12em] text-slate-500">or</span>
        <div className="h-px flex-1 bg-slate-300" />
      </div>

      <GoogleLoginButton label="Sign up with Google" />

      <p className="mt-6 text-sm text-slate-600">
        Already have an account?{' '}
        <Link to="/auth/login" className="font-semibold text-brand-ocean hover:text-brand-deep">
          Login here
        </Link>
      </p>
    </AuthLayout>
  )
}

export default RegisterPage
