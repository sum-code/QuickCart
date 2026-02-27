import { useCallback, useEffect, useState } from 'react'
import { listProducts } from '../services/productService'
import { getErrorMessage } from '../utils/auth'
import useDebouncedValue from './useDebouncedValue'

function normalizeNumber(value) {
  if (value === '' || value === null || value === undefined) {
    return undefined
  }

  const parsed = Number(value)
  return Number.isNaN(parsed) ? undefined : parsed
}

function normalizePrice(value) {
  const parsed = normalizeNumber(value)
  if (parsed === undefined || parsed <= 0) {
    return undefined
  }

  return parsed
}

function useProductCatalogQuery({
  search,
  category,
  minPrice,
  maxPrice,
  minRating,
  page,
  size,
  sortBy,
  direction,
}) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const debouncedSearch = useDebouncedValue(search, 400)
  const debouncedMinPrice = useDebouncedValue(minPrice, 250)
  const debouncedMaxPrice = useDebouncedValue(maxPrice, 250)

  const fetchProducts = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const normalizedMinPrice = normalizePrice(debouncedMinPrice)
      const normalizedMaxPrice = normalizePrice(debouncedMaxPrice)

      const [effectiveMinPrice, effectiveMaxPrice] =
        normalizedMinPrice !== undefined &&
        normalizedMaxPrice !== undefined &&
        normalizedMinPrice > normalizedMaxPrice
          ? [normalizedMaxPrice, normalizedMinPrice]
          : [normalizedMinPrice, normalizedMaxPrice]

      const response = await listProducts({
        page,
        size,
        sortBy,
        direction,
        search: debouncedSearch?.trim() || undefined,
        category: category || undefined,
        minPrice: effectiveMinPrice,
        maxPrice: effectiveMaxPrice,
        minRating: normalizeNumber(minRating),
      })

      setData(response)
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load products.'))
    } finally {
      setLoading(false)
    }
  }, [category, debouncedMaxPrice, debouncedMinPrice, debouncedSearch, direction, minRating, page, size, sortBy])

  useEffect(() => {
    fetchProducts()
  }, [fetchProducts])

  return { data, loading, error, fetchProducts }
}

export default useProductCatalogQuery
