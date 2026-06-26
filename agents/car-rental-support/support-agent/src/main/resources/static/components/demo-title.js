import { LitElement, html, css } from 'lit';

export class DemoTitle extends LitElement {

    static styles = css`
      :host {
        display: block;
        position: fixed;
        bottom: 2.5rem;
        left: 2.5rem;
        z-index: 10;
        max-width: 22rem;
      }

      .card {
        padding: 1.25rem 1.5rem;
        border-radius: 1rem;
        background: rgba(255, 255, 255, 0.82);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border: 1px solid rgba(255, 255, 255, 0.6);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
      }

      .badge {
        display: inline-flex;
        align-items: center;
        gap: 0.4rem;
        font-size: 0.7rem;
        font-weight: 600;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: var(--accent-blue, #2196f3);
        margin-bottom: 0.6rem;
      }

      .badge::before {
        content: '';
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: var(--accent-blue, #2196f3);
        animation: pulse 2s ease-in-out infinite;
      }

      @keyframes pulse {
        0%, 100% { opacity: 1; transform: scale(1); }
        50% { opacity: 0.5; transform: scale(0.85); }
      }

      h3 {
        margin: 0 0 0.5rem;
        font-family: "Red Hat Display", "Red Hat Text", sans-serif;
        font-size: 1.15rem;
        font-weight: 700;
        color: #1a1a1a;
        line-height: 1.3;
      }

      p {
        margin: 0;
        font-size: 0.875rem;
        line-height: 1.5;
        color: #555;
      }

      @media (max-width: 768px) {
        :host {
          left: 1rem;
          right: 1rem;
          bottom: 5.5rem;
          max-width: none;
        }
      }
    `;

    render() {
        return html`
            <div class="card">
                <div class="badge">AI Support Agent</div>
                <h3>Need help with your rental?</h3>
                <p>Ask about bookings, pricing, or policies — our assistant is ready 24/7.</p>
            </div>
        `;
    }
}

customElements.define('demo-title', DemoTitle);
