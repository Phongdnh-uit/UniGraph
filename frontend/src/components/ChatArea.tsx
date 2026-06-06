import { useEffect, useRef } from 'react'
import Message from './Message'
import ChatInput from './ChatInput'

const ChatArea = () => {
  const scrollRef = useRef<HTMLDivElement>(null)
  
  const messages = [
    { role: 'assistant' as const, content: 'Welcome to UniGraph. I am your research agent. How can I help you explore your knowledge today?' },
    { role: 'user' as const, content: 'Can you show me the relationship between Graph Databases and RAG?' },
    { role: 'assistant' as const, content: 'Certainly. Graph databases provide a structured way to represent entities and their connections, which enhances RAG (Retrieval-Augmented Generation) by providing deeper context and path-based reasoning that traditional vector search might miss.' },
  ]

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [messages])

  return (
    <div className="flex flex-col h-full" role="main" aria-label="Chat interface">
      <div 
        ref={scrollRef}
        className="flex-1 overflow-y-auto" 
        role="log" 
        aria-label="Chat conversation" 
        aria-live="polite"
      >
        <div className="flex flex-col">
          {messages.map((m, i) => (
            <Message key={i} {...m} />
          ))}
          <div className="h-8" aria-hidden="true" /> {/* Bottom spacer */}
        </div>
      </div>
      <ChatInput />
    </div>
  )
}

export default ChatArea
