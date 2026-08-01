import { useEffect, useRef, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'

const STARTER_PROMPTS = [
  'Where is most of my money going?',
  'How does this month compare to last month?',
  'Give me one habit I could change to save more.',
]

export default function Advisor() {
  const [messages, setMessages] = useState([
    { role: 'assistant', content: "Hi! I'm your AI advisor. Ask me anything about your spending — I'll answer based on the expenses you've actually logged." },
  ])
  const [input, setInput] = useState('')
  const [sending, setSending] = useState(false)
  const [error, setError] = useState('')
  const scrollRef = useRef(null)

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [messages, sending])

  const sendMessage = async (text) => {
    if (!text.trim() || sending) return
    setError('')

    const history = messages.map(({ role, content }) => ({ role, content }))
    const nextMessages = [...messages, { role: 'user', content: text }]
    setMessages(nextMessages)
    setInput('')
    setSending(true)

    try {
      const res = await api.post('/api/advisor/chat', { message: text, history })
      setMessages((prev) => [...prev, { role: 'assistant', content: res.data.reply }])
    } catch (err) {
      const message = err.response?.data || 'The advisor is unavailable. Make sure Ollama is running locally.'
      setError(typeof message === 'string' ? message : 'The advisor is unavailable.')
    } finally {
      setSending(false)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    sendMessage(input)
  }

  return (
    <Layout>
      <h1 className="page-title">AI Advisor</h1>
      <p className="page-subtitle">Runs on a local model via Ollama — your data never leaves your machine.</p>

      <div className="card chat-window">
        <div className="chat-messages" ref={scrollRef}>
          {messages.map((m, i) => (
            <div key={i} className={`chat-bubble ${m.role}`}>
              {m.content}
            </div>
          ))}
          {sending && <div className="chat-bubble assistant thinking">Thinking…</div>}
        </div>

        {error && <div className="auth-error">{error}</div>}

        {messages.length <= 1 && (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
            {STARTER_PROMPTS.map((p) => (
              <button key={p} type="button" className="icon-btn" onClick={() => sendMessage(p)}>
                {p}
              </button>
            ))}
          </div>
        )}

        <form className="chat-input-row" onSubmit={handleSubmit}>
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Ask about your spending…"
            disabled={sending}
          />
          <button type="submit" className="btn btn-primary" disabled={sending || !input.trim()}>
            Send
          </button>
        </form>
      </div>
    </Layout>
  )
}
