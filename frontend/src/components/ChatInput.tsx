import React, { useState, KeyboardEvent } from 'react'
import { Send } from 'lucide-react'

interface ChatInputProps {
  onSend: (message: string) => void
  disabled?: boolean
}

const ChatInput: React.FC<ChatInputProps> = ({ onSend, disabled }) => {
  const [inputValue, setInputValue] = useState('')

  const handleSend = () => {
    if (inputValue.trim() && !disabled) {
      onSend(inputValue.trim())
      setInputValue('')
    }
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  return (
    <div className="p-4 flex justify-center sticky bottom-0 z-10 bg-canvas border-t">
      <div className="max-w-2xl w-full relative">
        <textarea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={disabled}
          placeholder="Ask UniGraph..."
          aria-label="Message UniGraph"
          className="w-full bg-[var(--canvas)] rounded-md px-4 py-3 pr-12 resize-none transition-all border border-[var(--hairline)] focus:border-[var(--primary)] focus:ring-4 focus:outline-none disabled:opacity-50 chat-input-textarea"
          rows={1}
        />
        <button 
          onClick={handleSend}
          disabled={disabled || !inputValue.trim()}
          className="absolute right-2 top-2 p-2 text-white rounded-md transition-all active:transform active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed btn-primary"
          aria-label="Send message"
        >
          <Send size={18} color="white" aria-hidden="true" />
        </button>
      </div>
    </div>
  )
}

export default ChatInput
