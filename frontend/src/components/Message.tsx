interface MessageProps {
  role: 'user' | 'assistant'
  content: string
}

const Message = ({ role, content }: MessageProps) => {
  return (
    <div 
      className={`py-8 w-full flex justify-center ${role === 'user' ? 'bg-[var(--surface-soft)]' : ''}`}
      role="article"
      aria-label={`${role === 'assistant' ? 'AI response' : 'User message'}`}
    >
      <div className="max-w-2xl w-full px-6 flex gap-6">
        <div 
          className={`w-8 h-8 rounded-sm flex items-center justify-center flex-shrink-0 ${
            role === 'assistant' ? 'bg-[var(--primary)]' : 'bg-[var(--surface-dark)]'
          } text-white`}
          aria-hidden="true"
        >
          <span className="text-[10px] font-bold uppercase">{role[0].toUpperCase()}</span>
        </div>
        <div className="flex-1">
          <p className={`${role === 'assistant' ? 'serif text-lg leading-relaxed' : 'text-md leading-relaxed'} text-[var(--ink)]`}
             style={{ 
               lineHeight: role === 'assistant' ? '1.75' : '1.55'
             }}>
            {content}
          </p>
        </div>
      </div>
    </div>
  )
}

export default Message
