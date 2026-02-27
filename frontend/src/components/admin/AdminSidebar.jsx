function AdminSidebar({ collapsed, onToggle, activeView, setActiveView }) {
  const nav = [
    { id: 'inventory', label: 'Inventory' },
    { id: 'orders', label: 'Orders' },
    { id: 'analytics', label: 'Analytics' },
  ]

  return (
    <div className={`relative shrink-0 transition-all duration-300 ${collapsed ? 'w-0' : 'w-64'}`}>
      <aside className="h-full overflow-hidden border-r border-white/10 bg-slate-950/90">
        <div className="px-4 py-8">
          <p className="text-xs uppercase tracking-[0.16em] text-brand-copper">QuickCart Admin</p>
          <h2 className="mt-2 text-xl font-semibold text-white">Dashboard</h2>
        </div>

        <nav className="space-y-2 px-3">
          {nav.map((item) => (
            <button
              key={item.id}
              type="button"
              onClick={() => setActiveView(item.id)}
              className={`w-full rounded-xl px-3 py-2 text-left text-sm transition ${
                activeView === item.id ? 'bg-brand-ocean text-white' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </aside>

      <button
        type="button"
        onClick={onToggle}
        className={`absolute top-6 z-30 h-8 w-8 rounded-full border border-white/20 bg-slate-900 text-xs text-white shadow-lg ${
          collapsed ? 'left-2' : '-right-3'
        }`}
      >
        {collapsed ? '>' : '<'}
      </button>
    </div>
  )
}

export default AdminSidebar
