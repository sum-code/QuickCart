function HeroBanner() {
  return (
    <section className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-brand-ink via-brand-deep to-brand-ocean p-10 text-white shadow-premium">
      <div className="absolute -right-24 -top-20 h-72 w-72 rounded-full bg-brand-copper/30 blur-3xl" />
      <div className="absolute -left-12 bottom-0 h-56 w-56 rounded-full bg-brand-aurora/20 blur-2xl" />
      <div className="relative max-w-2xl">
        <p className="text-xs uppercase tracking-[0.16em] text-brand-copper">Premium Essentials</p>
        <h1 className="mt-3 text-4xl font-semibold leading-tight md:text-5xl">Everything you need, delivered at speed.</h1>
        <p className="mt-4 text-sm text-slate-100 md:text-base">
          Curated products, transparent pricing, and lightning-fast fulfillment built for modern shoppers.
        </p>
      </div>
    </section>
  )
}

export default HeroBanner
