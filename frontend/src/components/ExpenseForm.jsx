import { useEffect, useState } from 'react'

const emptyForm = { title: '', amount: '', category: '', expensedate: '' }

export default function ExpenseForm({ initialValue, onSubmit, onCancel, submitLabel = 'Add expense' }) {
  const [form, setForm] = useState(emptyForm)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (initialValue) {
      setForm({
        title: initialValue.title || '',
        amount: initialValue.amount ?? '',
        category: initialValue.category || '',
        expensedate: initialValue.expensedate || '',
      })
    } else {
      setForm(emptyForm)
    }
  }, [initialValue])

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      await onSubmit({
        title: form.title,
        amount: parseFloat(form.amount),
        category: form.category,
        expensedate: form.expensedate,
      })
      if (!initialValue) setForm(emptyForm)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-field">
        <label htmlFor="title">Title</label>
        <input id="title" type="text" value={form.title} onChange={handleChange('title')} required placeholder="e.g. Groceries" />
      </div>
      <div className="form-field">
        <label htmlFor="amount">Amount</label>
        <input id="amount" type="number" step="0.01" min="0" value={form.amount} onChange={handleChange('amount')} required placeholder="0.00" />
      </div>
      <div className="form-field">
        <label htmlFor="category">Category</label>
        <input id="category" type="text" value={form.category} onChange={handleChange('category')} required placeholder="e.g. Food" />
      </div>
      <div className="form-field">
        <label htmlFor="expensedate">Date</label>
        <input id="expensedate" type="date" value={form.expensedate} onChange={handleChange('expensedate')} required />
      </div>

      <div style={{ display: 'flex', gap: 10 }}>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving…' : submitLabel}
        </button>
        {onCancel && (
          <button type="button" className="btn btn-ghost" onClick={onCancel}>
            Cancel
          </button>
        )}
      </div>
    </form>
  )
}
