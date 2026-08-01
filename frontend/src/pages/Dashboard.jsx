import { useCallback, useEffect, useMemo, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import ExpenseForm from '../components/ExpenseForm'
import ExpenseList from '../components/ExpenseList'

const PAGE_SIZE = 8

export default function Dashboard() {
  const [expenses, setExpenses] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editingExpense, setEditingExpense] = useState(null)

  const [insights, setInsights] = useState([])
  const [insightsLoading, setInsightsLoading] = useState(true)
  const [insightsError, setInsightsError] = useState('')

  const loadExpenses = useCallback(async (targetPage) => {
    setLoading(true)
    setError('')
    try {
      const res = await api.get('/api/expense/page', { params: { page: targetPage, size: PAGE_SIZE } })
      setExpenses(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch (err) {
      setError('Could not load expenses.')
    } finally {
      setLoading(false)
    }
  }, [])

  const loadInsights = useCallback(async () => {
    setInsightsLoading(true)
    setInsightsError('')
    try {
      const res = await api.get('/api/advisor/insights')
      setInsights(res.data.insights || [])
    } catch (err) {
      setInsightsError(
        err.response?.data ||
        'AI advisor is unavailable. Make sure Ollama is running locally.'
      )
    } finally {
      setInsightsLoading(false)
    }
  }, [])

  useEffect(() => {
    loadExpenses(page)
  }, [page, loadExpenses])

  useEffect(() => {
    loadInsights()
  }, [loadInsights])

  const stats = useMemo(() => {
    const total = expenses.reduce((sum, e) => sum + (e.amount || 0), 0)
    const categories = new Set(expenses.map((e) => e.category))
    return { total, count: expenses.length, categories: categories.size }
  }, [expenses])

  const handleCreate = async (payload) => {
    await api.post('/api/expense', payload)
    await loadExpenses(page)
    await loadInsights()
  }

  const handleUpdate = async (payload) => {
    await api.put(`/api/expense/${editingExpense.id}`, payload)
    setEditingExpense(null)
    await loadExpenses(page)
    await loadInsights()
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this expense?')) return
    await api.delete(`/api/expense/${id}`)
    await loadExpenses(page)
    await loadInsights()
  }

  return (
    <Layout>
      <h1 className="page-title">Dashboard</h1>
      <p className="page-subtitle">Your spending, this page at a glance.</p>

      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-label">This page total</div>
          <div className="stat-value rust mono">{stats.total.toFixed(2)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Entries on this page</div>
          <div className="stat-value teal mono">{stats.count}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Categories on this page</div>
          <div className="stat-value gold mono">{stats.categories}</div>
        </div>
      </div>

      <div className="two-col">
        <div>
          <div className="card" style={{ marginBottom: 20 }}>
            <div className="section-heading">
              {editingExpense ? 'Edit expense' : 'Add an expense'}
            </div>
            <ExpenseForm
              key={editingExpense?.id || 'new'}
              initialValue={editingExpense}
              submitLabel={editingExpense ? 'Save changes' : 'Add expense'}
              onSubmit={editingExpense ? handleUpdate : handleCreate}
              onCancel={editingExpense ? () => setEditingExpense(null) : undefined}
            />
          </div>

          <div className="card">
            <div className="section-heading">Expenses</div>
            {error && <div className="auth-error">{error}</div>}
            {loading ? (
              <div className="empty-state">Loading…</div>
            ) : (
              <>
                <ExpenseList expenses={expenses} onEdit={setEditingExpense} onDelete={handleDelete} />
                {totalPages > 1 && (
                  <div className="pagination">
                    <button
                      className="icon-btn"
                      disabled={page === 0}
                      onClick={() => setPage((p) => Math.max(0, p - 1))}
                    >
                      ← Prev
                    </button>
                    <span>
                      Page {page + 1} of {totalPages}
                    </span>
                    <button
                      className="icon-btn"
                      disabled={page + 1 >= totalPages}
                      onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                    >
                      Next →
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        <div className="card">
          <div className="section-heading">
            AI Insights
            <button className="icon-btn" onClick={loadInsights} disabled={insightsLoading}>
              {insightsLoading ? '…' : 'Refresh'}
            </button>
          </div>

          {insightsLoading && <div className="empty-state">Thinking about your spending…</div>}

          {!insightsLoading && insightsError && (
            <div className="auth-error">{String(insightsError)}</div>
          )}

          {!insightsLoading && !insightsError && insights.length === 0 && (
            <div className="empty-state">Add a few expenses to unlock insights.</div>
          )}

          {!insightsLoading && !insightsError && insights.length > 0 && (
            <div className="insights-list">
              {insights.map((tip, i) => (
                <div className="insight-row" key={i}>
                  {tip}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </Layout>
  )
}
