import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import Message from './Message'
import { describe, it, expect, vi } from 'vitest'

// Mock react-syntax-highlighter as it can be slow or problematic in JSDOM
vi.mock('react-syntax-highlighter', () => ({
  Prism: ({ children }: { children: React.ReactNode }) => <pre>{children}</pre>
}))

// Mock lucide-react icons
vi.mock('lucide-react', () => ({
  User: () => <svg data-testid="user-icon" />,
  Sparkles: () => <svg data-testid="sparkles-icon" />
}))

describe('Message Component', () => {
  it('renders assistant message with Markdown container', () => {
    render(<Message role="assistant" content="Hello **bold**" />)
    
    // Check for bold element rendered by react-markdown
    const boldElement = screen.getByText('bold')
    expect(boldElement.tagName).toBe('STRONG')
    
    // Check for Sparkles icon
    const icon = screen.getByTestId('sparkles-icon')
    expect(icon).toBeInTheDocument()
    
    // Check for background class
    expect(icon.parentElement).toHaveClass('bg-[var(--primary)]')
  })

  it('renders user message as plain text', () => {
    render(<Message role="user" content="How does RAG work?" />)
    
    const content = screen.getByText('How does RAG work?')
    expect(content).toBeInTheDocument()
    expect(content.tagName).toBe('P')
    
    // Check for User icon
    const icon = screen.getByTestId('user-icon')
    expect(icon).toBeInTheDocument()
    
    // Check for background class
    expect(icon.parentElement).toHaveClass('bg-[var(--surface-dark)]')
  })

  it('applies correct CSS classes for messages', () => {
    const { container: userContainer } = render(<Message role="user" content="User message" />)
    const userMessage = userContainer.firstChild as HTMLElement
    expect(userMessage).toHaveClass('message-container')
    expect(userMessage).toHaveClass('message-user')

    const { container: assistantContainer } = render(<Message role="assistant" content="AI message" />)
    const assistantMessage = assistantContainer.firstChild as HTMLElement
    expect(assistantMessage).toHaveClass('message-container')
    expect(assistantMessage).toHaveClass('message-assistant')
  })

  it('renders code blocks using SyntaxHighlighter', () => {
    const codeContent = "```javascript\nconst x = 1;\n```"
    render(<Message role="assistant" content={codeContent} />)
    
    // Since we mocked Prism to render a pre tag
    const preElement = screen.getByText('const x = 1;')
    expect(preElement.tagName).toBe('PRE')
  })
})
