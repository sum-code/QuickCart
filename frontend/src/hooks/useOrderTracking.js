import { useCallback, useEffect, useState } from 'react'
import { getOrderTracking } from '../services/orderService'
import { getErrorMessage } from '../utils/auth'

export function useOrderTracking(orderId) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchTracking = useCallback(async () => {
    if (!orderId) {
      return
    }

    setLoading(true)
    setError('')
    try {
      const tracking = await getOrderTracking(orderId)
      setData(tracking)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to load order tracking details.'))
    } finally {
      setLoading(false)
    }
  }, [orderId])

  useEffect(() => {
    fetchTracking()
  }, [fetchTracking])

  return { data, loading, error, refresh: fetchTracking }
}
