function AuthInput({ label, type = 'text', value, onChange, placeholder, ...props }) {
  return (
    <label className="block text-sm font-medium text-slate-700">
      <span>{label}</span>
      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="mt-2 w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-brand-ocean focus:ring-2 focus:ring-brand-ocean/25"
        {...props}
      />
    </label>
  )
}

export default AuthInput
