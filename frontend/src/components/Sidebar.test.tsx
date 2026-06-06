/**
 * @vitest-environment jsdom
 */
import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import * as matchers from '@testing-library/jest-dom/matchers'
import Sidebar from './Sidebar'

expect.extend(matchers)

describe('Sidebar Component', () => {
  it('renders the "New Chat" button with correct aria-label', () => {
    render(<Sidebar />)
    const button = screen.getByLabelText('Start a new chat')
    expect(button).toBeInTheDocument()
    expect(screen.getByText('New Chat')).toBeInTheDocument()
  })

  it('renders the "History" section header with correct semantic ID', () => {
    render(<Sidebar />)
    const header = screen.getByText('History')
    expect(header).toBeInTheDocument()
    expect(header.id).toBe('sidebar-history-title')
  })

  it('renders mock session items as list items', () => {
    render(<Sidebar />)
    expect(screen.getByText('Exploring Graph Theory')).toBeInTheDocument()
    expect(screen.getByText('Neo4j Setup Guide')).toBeInTheDocument()
    expect(screen.getByText('Data Modeling 101')).toBeInTheDocument()
    
    // Check for list items
    const listItems = screen.getAllByRole('listitem')
    expect(listItems).toHaveLength(3)
  })

  it('has correct background color and width', () => {
    render(<Sidebar />)
    const aside = screen.getByLabelText('Chat History Sidebar')
    expect(aside).toHaveStyle({
      width: '260px',
      backgroundColor: 'var(--surface-dark)',
      color: 'var(--on-dark)'
    })
  })

  it('uses nav and ul for semantic history list', () => {
    render(<Sidebar />)
    expect(screen.getByRole('navigation')).toBeInTheDocument()
    expect(screen.getByRole('list')).toBeInTheDocument()
  })
})
