import { Send } from 'lucide-react'

const ChatInput = () => {
  return (
    <div className="p-4 flex justify-center sticky bottom-0 z-10" style={{ 
      backgroundColor: 'var(--canvas)',
      borderTop: '1px solid var(--hairline-soft)'
    }}>
      <div className="max-w-2xl w-full relative">
        <textarea
          placeholder="Ask UniGraph..."
          aria-label="Message UniGraph"
          className="w-full bg-[var(--canvas)] rounded-md px-4 py-3 pr-12 resize-none transition-all border border-[var(--hairline)] focus:border-[var(--primary)] focus:ring-4 focus:outline-none"
          style={{ 
            minHeight: '52px',
            maxHeight: '160px',
            fontSize: '16px'
          }}
          rows={1}
        />
        <button 
          className="absolute right-2 top-2 p-2 text-white rounded-md transition-all active:transform active:scale-95"
          style={{ 
            backgroundColor: 'var(--primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
          aria-label="Send message"
        >
          <Send size={18} color="white" aria-hidden="true" />
        </button>
      </div>
    </div>
  )
}

export default ChatInput
