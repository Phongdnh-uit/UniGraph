import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import Navigation from './Navigation'
import { describe, it, expect, vi } from 'vitest'

describe('Navigation Component', () => {
  const mockOnTabChange = vi.fn()

  it('renders the logo with "UG" text', () => {
    render(<Navigation activeTab="chat" onTabChange={mockOnTabChange} />)
    const logoText = screen.getByText('UG')
    expect(logoText).toBeInTheDocument()
    // Check if it's in a box (simplified check)
    expect(logoText.parentElement).toHaveStyle({
      width: '24px',
      height: '24px',
      backgroundColor: 'var(--ink)'
    })
  })

  it('renders the "UniGraph" title with correct styling', () => {
    render(<Navigation activeTab="chat" onTabChange={mockOnTabChange} />)
    const title = screen.getByText('UniGraph')
    expect(title).toBeInTheDocument()
    expect(title).toHaveClass('serif')
    // This might fail if the style is not applied yet
    expect(title).toHaveStyle({
      letterSpacing: '-0.025em'
    })
  })

  it('renders capitalized tab names', () => {
    render(<Navigation activeTab="chat" onTabChange={mockOnTabChange} />)
    // If we want the actual text content to be capitalized
    expect(screen.getByText('Chat')).toBeInTheDocument()
    expect(screen.getByText('Graph')).toBeInTheDocument()
  })

  it('applies correct colors for active and inactive tabs', () => {
    const { rerender } = render(<Navigation activeTab="chat" onTabChange={mockOnTabChange} />)
    
    const chatButton = screen.getByText('Chat').closest('button')
    const graphButton = screen.getByText('Graph').closest('button')

    expect(chatButton).toHaveStyle({ color: 'var(--primary)' })
    expect(graphButton).toHaveStyle({ color: 'var(--muted)' })

    rerender(<Navigation activeTab="graph" onTabChange={mockOnTabChange} />)
    expect(chatButton).toHaveStyle({ color: 'var(--muted)' })
    expect(graphButton).toHaveStyle({ color: 'var(--primary)' })
  })

  it('has a 64px tall nav bar with a bottom border', () => {
    render(<Navigation activeTab="chat" onTabChange={mockOnTabChange} />)
    const nav = screen.getByRole('navigation')
    expect(nav).toHaveStyle('height: 64px')
    expect(nav).toHaveStyle('border-bottom: 1px solid var(--hairline)')
  })
})
