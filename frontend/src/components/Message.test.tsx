import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import Message from './Message'
import { describe, it, expect } from 'vitest'

describe('Message Component', () => {
  it('renders assistant message with serif font and correct avatar', () => {
    render(<Message role="assistant" content="Hello, I am your assistant." />)
    
    const content = screen.getByText('Hello, I am your assistant.')
    expect(content).toBeInTheDocument()
    expect(content).toHaveClass('serif')
    
    const avatar = screen.getByText('A')
    expect(avatar).toBeInTheDocument()
    expect(avatar.parentElement).toHaveStyle({
      backgroundColor: 'var(--primary)'
    })
  })

  it('renders user message with sans-serif font and correct avatar', () => {
    render(<Message role="user" content="How does RAG work?" />)
    
    const content = screen.getByText('How does RAG work?')
    expect(content).toBeInTheDocument()
    expect(content).not.toHaveClass('serif')
    
    const avatar = screen.getByText('U')
    expect(avatar).toBeInTheDocument()
    expect(avatar.parentElement).toHaveStyle({
      backgroundColor: 'var(--surface-dark)'
    })
  })

  it('applies slightly recessed background for user messages', () => {
    const { container } = render(<Message role="user" content="User message" />)
    // Check if the outer container has a recessed background
    const messageContainer = container.firstChild as HTMLElement
    expect(messageContainer).toHaveClass('bg-[var(--surface-soft)]')
  })
})
