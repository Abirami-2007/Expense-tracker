export default function ExpenseList({ expenses, onEdit, onDelete }) {
  if (!expenses || expenses.length === 0) {
    return (
      <div className="empty-state">
        No expenses logged yet. Add your first one above.
      </div>
    )
  }

  return (
    <table className="expense-table">
      <thead>
        <tr>
          <th>Title</th>
          <th>Category</th>
          <th>Date</th>
          <th>Amount</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {expenses.map((expense) => (
          <tr key={expense.id}>
            <td>{expense.title}</td>
            <td>
              <span className="category-pill">{expense.category}</span>
            </td>
            <td className="mono">{expense.expensedate}</td>
            <td className="amount">{expense.amount?.toFixed(2)}</td>
            <td>
              <div className="row-actions">
                <button className="icon-btn" onClick={() => onEdit(expense)}>
                  Edit
                </button>
                <button className="icon-btn" onClick={() => onDelete(expense.id)}>
                  Delete
                </button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
