import { useEffect, useMemo, useState } from 'react'

const emptyForm = {
  name: '',
  brand: '',
  description: '',
  price: '',
  stockQuantity: '',
  category: '',
}

function ProductModal({ isOpen, mode, initialProduct, onClose, onSubmit, loading }) {
  const [form, setForm] = useState(emptyForm)
  const [imageFile, setImageFile] = useState(null)
  const [previewUrl, setPreviewUrl] = useState('')

  useEffect(() => {
    if (!initialProduct) {
      setForm(emptyForm)
      setPreviewUrl('')
      setImageFile(null)
      return
    }

    setForm({
      name: initialProduct.name ?? '',
      brand: initialProduct.brand ?? '',
      description: initialProduct.description ?? '',
      price: initialProduct.price ?? '',
      stockQuantity: initialProduct.stockQuantity ?? '',
      category: initialProduct.category ?? '',
    })
    setPreviewUrl(initialProduct.imageUrl ?? '')
    setImageFile(null)
  }, [initialProduct, isOpen])

  const title = useMemo(() => (mode === 'create' ? 'Add Product' : 'Edit Product'), [mode])

  if (!isOpen) {
    return null
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleFileChange = (event) => {
    const file = event.target.files?.[0]
    if (!file) {
      return
    }

    setImageFile(file)
    setPreviewUrl(URL.createObjectURL(file))
  }

  const submit = (event) => {
    event.preventDefault()
    onSubmit(
      {
        ...form,
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
      },
      imageFile,
    )
  }

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/70 p-4 backdrop-blur-sm">
      <form onSubmit={submit} className="w-full max-w-2xl rounded-2xl border border-white/10 bg-slate-900 p-6 text-white">
        <h3 className="text-2xl font-semibold">{title}</h3>

        <div className="mt-5 grid gap-4 md:grid-cols-2">
          <input name="name" value={form.name} onChange={handleChange} placeholder="Name" className="input" required />
          <input name="brand" value={form.brand} onChange={handleChange} placeholder="Brand" className="input" required />
          <input name="category" value={form.category} onChange={handleChange} placeholder="Category" className="input" required />
          <input
            name="price"
            value={form.price}
            onChange={handleChange}
            type="number"
            step="0.01"
            min="0.01"
            placeholder="Price"
            className="input"
            required
          />
          <input
            name="stockQuantity"
            value={form.stockQuantity}
            onChange={handleChange}
            type="number"
            min="0"
            placeholder="Stock Quantity"
            className="input"
            required
          />
          <label className="rounded-xl border border-dashed border-white/30 bg-white/5 p-3 text-sm text-slate-300">
            Product Image
            <input type="file" accept="image/*" onChange={handleFileChange} className="mt-2 block w-full text-xs" />
          </label>
        </div>

        <textarea
          name="description"
          value={form.description}
          onChange={handleChange}
          placeholder="Description"
          className="input mt-4 min-h-24"
        />

        {previewUrl && (
          <img src={previewUrl} alt="Preview" className="mt-4 h-40 w-full rounded-xl object-cover" />
        )}

        <div className="mt-6 flex justify-end gap-3">
          <button type="button" onClick={onClose} className="rounded-xl border border-white/20 px-4 py-2 text-sm">
            Cancel
          </button>
          <button type="submit" disabled={loading} className="rounded-xl bg-brand-ocean px-4 py-2 text-sm font-semibold">
            {loading ? 'Saving...' : mode === 'create' ? 'Create Product' : 'Update Product'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default ProductModal
