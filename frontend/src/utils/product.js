export function optimizeCloudinaryUrl(url, transformation = 'f_auto,q_auto,w_600') {
  if (!url || typeof url !== 'string' || !url.includes('/upload/')) {
    return url
  }

  const [prefix, suffix] = url.split('/upload/')
  return `${prefix}/upload/${transformation}/${suffix}`
}

export function formatCurrency(value) {
  if (typeof value !== 'number' && typeof value !== 'string') {
    return '$0.00'
  }

  const numeric = Number(value)
  if (Number.isNaN(numeric)) {
    return '$0.00'
  }

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(numeric)
}

export function normalizeRating(value) {
  const numeric = Number(value)
  if (Number.isNaN(numeric) || numeric < 0) {
    return 0
  }
  return Math.min(5, numeric)
}

export function getRatingStars(rating) {
  const normalized = normalizeRating(rating)
  return Array.from({ length: 5 }, (_, index) => index < Math.round(normalized))
}
