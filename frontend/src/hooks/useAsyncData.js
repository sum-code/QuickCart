import { useEffect, useState } from 'react'
import { getErrorMessage } from '../utils/auth'

function useAsyncData(asyncFn, deps = []) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    const run = async () => {
      setLoading(true)
      setError('')

      try {
        const result = await asyncFn()
        if (active) {
          setData(result)
        }
      } catch (err) {
        if (active) {
          setError(getErrorMessage(err))
        }
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    run()

    return () => {
      active = false
    }
  }, deps)

  return { data, setData, loading, error, setError, setLoading }
}

export default useAsyncData
