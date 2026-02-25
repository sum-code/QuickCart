const BACKEND_BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL || 'http://localhost:8080'

function GoogleLoginButton({ label = 'Continue with Google' }) {
  return (
    <a
      href={`${BACKEND_BASE_URL}/oauth2/authorization/google`}
      className="inline-flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:border-brand-ocean hover:text-brand-ink"
    >
      <svg aria-hidden="true" viewBox="0 0 24 24" className="h-5 w-5">
        <path
          fill="#EA4335"
          d="M12 10.2v3.9h5.5c-.2 1.3-1.5 3.9-5.5 3.9a6 6 0 1 1 0-12c2.3 0 3.9 1 4.8 1.8l3.3-3.2A10.9 10.9 0 0 0 12 1.5 10.5 10.5 0 0 0 1.5 12 10.5 10.5 0 0 0 12 22.5c6.1 0 10.1-4.3 10.1-10.3 0-.7-.1-1.2-.2-2H12Z"
        />
      </svg>
      {label}
    </a>
  )
}

export default GoogleLoginButton
