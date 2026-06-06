import { useEffect, useRef, useState } from 'react'
import Message from './Message'
import ChatInput from './ChatInput'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1/chat'

interface MessageData {
  id: string
  role: 'user' | 'assistant'
  content: string
}

const ChatArea = () => {
  const scrollRef = useRef<HTMLDivElement>(null)
  const [messages, setMessages] = useState<MessageData[]>([
    { 
      id: 'initial-msg',
      role: 'assistant', 
      content: 'Welcome to UniGraph. I am your research agent. How can I help you explore your knowledge today?' 
    }
  ])
  const [isTyping, setIsTyping] = useState(false)

  const handleSendMessage = async (content: string) => {
    const userMessage: MessageData = { 
      id: crypto.randomUUID(),
      role: 'user', 
      content 
    }
    setMessages(prev => [...prev, userMessage])
    setIsTyping(true)

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message: content }),
      })

      if (!response.ok) {
        throw new Error('Failed to fetch response')
      }

      const data = await response.json()
      const assistantMessage: MessageData = { 
        id: crypto.randomUUID(),
        role: 'assistant', 
        content: data.data || 'Sorry, I couldn\'t process that.' 
      }
      setMessages(prev => [...prev, assistantMessage])
    } catch (error) {
      console.error('Chat Error:', error)
      setMessages(prev => [...prev, { 
        id: crypto.randomUUID(),
        role: 'assistant', 
        content: 'Error: Could not connect to the UniGraph engine.' 
      }])
    } finally {
      setIsTyping(false)
    }
  }

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [messages, isTyping])

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
          {messages.map((m) => (
            <Message key={m.id} role={m.role} content={m.content} />
          ))}
          {isTyping && (
            <div className="p-4 text-[var(--muted)] italic text-sm animate-pulse">
              UniGraph is thinking...
            </div>
          )}
          <div className="h-8" aria-hidden="true" /> {/* Bottom spacer */}
        </div>
      </div>
      <ChatInput onSend={handleSendMessage} disabled={isTyping} />
    </div>
  )
}

export default ChatArea
