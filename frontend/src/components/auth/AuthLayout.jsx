function AuthLayout({ title, subtitle, children }) {
  return (
    <main className="relative min-h-screen overflow-hidden bg-slate-950 px-4 py-8">
      <div className="pointer-events-none absolute -left-20 top-10 h-72 w-72 rounded-full bg-brand-ocean/40 blur-3xl" />
      <div className="pointer-events-none absolute right-0 top-0 h-80 w-80 rounded-full bg-brand-aurora/30 blur-3xl" />
      <div className="pointer-events-none absolute bottom-0 left-1/3 h-64 w-64 rounded-full bg-brand-copper/30 blur-3xl" />

      <section className="relative mx-auto w-full max-w-md rounded-3xl border border-white/10 bg-white/95 p-8 shadow-premium backdrop-blur-sm">
        <div className="mb-6">
          <p className="mb-2 inline-flex rounded-full bg-brand-ink px-3 py-1 text-xs font-semibold uppercase tracking-[0.12em] text-white">
            QuickCart Secure Access
          </p>
          <h1 className="text-3xl font-semibold text-brand-ink">{title}</h1>
          <p className="mt-2 text-sm text-slate-600">{subtitle}</p>
        </div>
        {children}
      </section>
    </main>
  )
}

export default AuthLayout
