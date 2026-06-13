import { useState } from 'react'
import './App.css'
import {
  fineCategories,
  processPayment,
  verifyFine,
} from './services/fineService.js'

function App() {
  const [referenceNumber, setReferenceNumber] = useState('TF-2026-WP-00001')
  const [categoryCode, setCategoryCode] = useState('SPD01')
  const [paymentMethod, setPaymentMethod] = useState('CARD')
  const [fine, setFine] = useState(null)
  const [receipt, setReceipt] = useState(null)
  const [loadingState, setLoadingState] = useState('idle')
  const [statusMessage, setStatusMessage] = useState(
    'Enter the reference number and category code printed on the fine sheet.',
  )
  const [errorMessage, setErrorMessage] = useState('')

  const handleVerifyFine = async (event) => {
    event.preventDefault()
    setLoadingState('verifying')
    setErrorMessage('')
    setStatusMessage('Verifying fine details...')

    try {
      const result = await verifyFine(referenceNumber, categoryCode)
      setFine(result)
      setReceipt(null)
      setStatusMessage('Fine verified. The payment amount is now locked.')
    } catch (error) {
      setFine(null)
      setReceipt(null)
      setErrorMessage(error.message)
      setStatusMessage('Verification failed. Check the reference number and category code.')
    } finally {
      setLoadingState('idle')
    }
  }

  const handleProcessPayment = async (event) => {
    event.preventDefault()

    if (!fine) {
      setErrorMessage('Verify the fine before submitting payment.')
      return
    }

    setLoadingState('paying')
    setErrorMessage('')
    setStatusMessage('Processing payment and generating receipt...')

    try {
      const result = await processPayment({
        referenceNumber,
        categoryCode,
        amount: fine.amount,
        paymentMethod,
        paymentChannel: 'WEB_PORTAL',
      })

      setReceipt(result)
      setStatusMessage('Payment successful. A receipt is ready to present to the officer.')
    } catch (error) {
      setErrorMessage(error.message)
      setStatusMessage('Payment could not be completed.')
    } finally {
      setLoadingState('idle')
    }
  }

  const amountFormatter = new Intl.NumberFormat('en-LK', {
    style: 'currency',
    currency: 'LKR',
    maximumFractionDigits: 0,
  })

  const formatDateTime = (value) =>
    new Intl.DateTimeFormat('en-LK', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value))

  const statusStep = receipt ? 3 : fine ? 2 : 1
  const isBusy = loadingState !== 'idle'
  const paymentLocked = !fine || loadingState === 'paying'
  const selectedCategory = fineCategories.find(({ code }) => code === categoryCode)

  return (
    <main className="portal-shell">
      <section className="hero-panel">
        <div className="hero-copy">
          <span className="eyebrow">Sri Lanka Police Traffic Fine Payment</span>
          <h1>Settle a fine online, verify the amount, and generate the receipt in one flow.</h1>
          <p className="hero-text">
            Use the reference number and category code from the physical fine sheet. The portal locks the amount after verification and creates a payment receipt instantly.
          </p>

          <div className="hero-metrics" aria-label="Portal highlights">
            <div>
              <strong>Public lookup</strong>
              <span>Verify without login</span>
            </div>
            <div>
              <strong>Fixed amount</strong>
              <span>Matches the category rate</span>
            </div>
            <div>
              <strong>Instant receipt</strong>
              <span>Ready for officer confirmation</span>
            </div>
          </div>
        </div>

        <aside className="status-card">
          <div className="status-badge">Step {statusStep} of 3</div>
          <h2>Payment progress</h2>
          <ol className="steps-list">
            <li className={statusStep >= 1 ? 'active' : ''}>Look up the fine</li>
            <li className={statusStep >= 2 ? 'active' : ''}>Review and pay</li>
            <li className={statusStep >= 3 ? 'active' : ''}>Collect the receipt</li>
          </ol>

          <div className="status-note" role="status" aria-live="polite">
            {statusMessage}
          </div>
          {errorMessage ? (
            <div className="error-note" role="alert">
              {errorMessage}
            </div>
          ) : null}
        </aside>
      </section>

      <section className="workspace-grid">
        <form className="lookup-card surface" onSubmit={handleVerifyFine}>
          <div className="section-heading">
            <span>Fine verification</span>
            <h2>Search by reference number and category code</h2>
          </div>

          <div className="field-grid">
            <label>
              Reference number
              <input
                value={referenceNumber}
                onChange={(event) => setReferenceNumber(event.target.value.toUpperCase())}
                placeholder="TF-2026-WP-00001"
                autoComplete="off"
                spellCheck="false"
              />
            </label>

            <label>
              Category code
              <select value={categoryCode} onChange={(event) => setCategoryCode(event.target.value)}>
                {fineCategories.map((category) => (
                  <option key={category.code} value={category.code}>
                    {category.code} - {category.shortLabel}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <div className="help-row">
            <span>Example: TF-2026-WP-00001</span>
            <span>Selected category amount: {selectedCategory ? amountFormatter.format(selectedCategory.amount) : 'LKR 0'}</span>
          </div>

          <button type="submit" className="primary-button" disabled={loadingState === 'verifying'}>
            {loadingState === 'verifying' ? 'Verifying...' : 'Verify fine'}
          </button>

          <div className="chip-row">
            {fineCategories.slice(0, 4).map((category) => (
              <button
                key={category.code}
                type="button"
                className="chip"
                onClick={() => setCategoryCode(category.code)}
              >
                {category.code}
              </button>
            ))}
          </div>
        </form>

        <div className="details-stack">
          <section className="surface detail-card">
            <div className="section-heading">
              <span>Fine details</span>
              <h2>Review the driver and violation data</h2>
            </div>

            {fine ? (
              <>
                <div className="detail-summary">
                  <div>
                    <strong>{fine.driverName}</strong>
                    <span>{fine.vehicleNumber}</span>
                  </div>
                  <div className={`status-pill status-${fine.status.toLowerCase()}`}>{fine.status}</div>
                </div>

                <dl className="detail-grid">
                  <div>
                    <dt>Reference number</dt>
                    <dd>{fine.referenceNumber}</dd>
                  </div>
                  <div>
                    <dt>Category</dt>
                    <dd>{fine.categoryCode}</dd>
                  </div>
                  <div>
                    <dt>Violation</dt>
                    <dd>{fine.categoryDescription}</dd>
                  </div>
                  <div>
                    <dt>Amount due</dt>
                    <dd>{amountFormatter.format(fine.amount)}</dd>
                  </div>
                  <div>
                    <dt>Officer</dt>
                    <dd>{fine.officerName}</dd>
                  </div>
                  <div>
                    <dt>Location</dt>
                    <dd>{fine.location}</dd>
                  </div>
                </dl>
              </>
            ) : (
              <p className="empty-state">
                Verified fine details will appear here. The payment form unlocks after a successful lookup.
              </p>
            )}
          </section>

          <form className="surface payment-card" onSubmit={handleProcessPayment}>
            <div className="section-heading">
              <span>Payment</span>
              <h2>Choose a payment method and submit</h2>
            </div>

            <div className="payment-summary">
              <div>
                <span>Amount locked</span>
                <strong>{fine ? amountFormatter.format(fine.amount) : 'LKR 0'}</strong>
              </div>
              <div>
                <span>Channel</span>
                <strong>WEB_PORTAL</strong>
              </div>
              <div>
                <span>Transaction ref</span>
                <strong>{receipt?.transactionRef || 'Generated on payment'}</strong>
              </div>
            </div>

            <fieldset className="method-fieldset" disabled={paymentLocked}>
              <legend>Payment method</legend>
              <label className={`method-option ${paymentMethod === 'CARD' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="CARD"
                  checked={paymentMethod === 'CARD'}
                  onChange={(event) => setPaymentMethod(event.target.value)}
                />
                Card payment
              </label>
              <label className={`method-option ${paymentMethod === 'MOBILE_WALLET' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="MOBILE_WALLET"
                  checked={paymentMethod === 'MOBILE_WALLET'}
                  onChange={(event) => setPaymentMethod(event.target.value)}
                />
                Mobile wallet
              </label>
            </fieldset>

            <button type="submit" className="primary-button" disabled={paymentLocked}>
              {loadingState === 'paying' ? 'Processing payment...' : 'Pay now'}
            </button>

            <p className="fine-print">
              Payment is processed securely. Make sure your fine is verified.
            </p>
          </form>
        </div>
      </section>

      <section className="receipt-card surface">
        <div className="section-heading">
          <span>Receipt</span>
          <h2>Payment confirmation</h2>
        </div>

        {receipt ? (
          <div className="receipt-grid">
            <div>
              <dt>Payment ID</dt>
              <dd>{receipt.id}</dd>
            </div>
            <div>
              <dt>Fine ID</dt>
              <dd>{receipt.fineId}</dd>
            </div>
            <div>
              <dt>Reference number</dt>
              <dd>{receipt.referenceNumber}</dd>
            </div>
            <div>
              <dt>Driver</dt>
              <dd>{receipt.driverName}</dd>
            </div>
            <div>
              <dt>Vehicle</dt>
              <dd>{receipt.vehicleNumber}</dd>
            </div>
            <div>
              <dt>Amount paid</dt>
              <dd>{amountFormatter.format(receipt.amountPaid)}</dd>
            </div>
            <div>
              <dt>Payment method</dt>
              <dd>{receipt.paymentMethod}</dd>
            </div>
            <div>
              <dt>Transaction ref</dt>
              <dd>{receipt.transactionRef}</dd>
            </div>
            <div>
              <dt>Paid at</dt>
              <dd>{formatDateTime(receipt.paidAt)}</dd>
            </div>
          </div>
        ) : (
          <p className="empty-state">
            The receipt will appear here after payment. Use it as the confirmation record when the officer checks the fine status.
          </p>
        )}
      </section>

    </main>
  )
}

export default App
