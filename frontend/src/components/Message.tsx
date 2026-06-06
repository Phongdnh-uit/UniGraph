import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { User, Sparkles } from 'lucide-react'

interface MessageProps {
  role: 'user' | 'assistant'
  content: string
}

const Message = ({ role, content }: MessageProps) => {
  const isAssistant = role === 'assistant'

  return (
    <div 
      className={`message-container ${!isAssistant ? 'message-user' : 'message-assistant'}`}
      role="article"
      aria-label={`${isAssistant ? 'AI response' : 'User message'}`}
    >
      <div className={`max-w-2xl w-full px-6 flex gap-6 ${isAssistant ? 'message-assistant-wrapper' : ''}`}>
        <div 
          className={`w-8 h-8 rounded-sm flex items-center justify-center flex-shrink-0 ${
            isAssistant ? 'bg-[var(--primary)]' : 'bg-[var(--surface-dark)]'
          } text-white`}
          aria-hidden="true"
        >
          {isAssistant ? (
            <Sparkles size={16} color="white" />
          ) : (
            <User size={16} color="white" />
          )}
        </div>
        <div className="flex-1 overflow-x-auto">
          {!isAssistant ? (
            <p className="text-md leading-relaxed text-[var(--ink)]">
              {content}
            </p>
          ) : (
            <div className="markdown-content serif text-lg text-[var(--ink)]">
              <ReactMarkdown 
                remarkPlugins={[remarkGfm]}
                components={{
                  code(props) {
                    const { node: _node, ref: _ref, className, children, ...rest } = props;
                    void _node;
                    void _ref;
                    const match = /language-(\w+)/.exec(className || '')
                    return match ? (
                      <SyntaxHighlighter
                        style={vscDarkPlus as any}
                        language={match[1]}
                        PreTag="div"
                        {...rest}
                      >
                        {String(children).replace(/\n$/, '')}
                      </SyntaxHighlighter>
                    ) : (
                      <code className={className} {...rest}>
                        {children}
                      </code>
                    )
                  }
                }}
              >
                {content}
              </ReactMarkdown>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default Message
