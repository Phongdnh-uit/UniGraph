import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import '@testing-library/jest-dom'
import ChatArea from './ChatArea'
import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock the fetch API
vi.stubGlobal('fetch', vi.fn())

describe('ChatArea Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders initial assistant message', () => {
    render(<ChatArea />)
    expect(screen.getByText(/Welcome to UniGraph/)).toBeInTheDocument()
  })

  it('sends a message and displays assistant response', async () => {
    const mockResponse = { data: 'This is a test response from AI.' }
    ;(fetch as any).mockResolvedValue({
      ok: true,
      json: async () => mockResponse,
    })

    render(<ChatArea />)
    const input = screen.getByPlaceholderText('Ask UniGraph...')
    const sendButton = screen.getByLabelText('Send message')

    fireEvent.change(input, { target: { value: 'What is RAG?' } })
    fireEvent.click(sendButton)

    // Check if user message appears
    expect(screen.getByText('What is RAG?')).toBeInTheDocument()

    // Check if typing indicator appears
    expect(screen.getByText('UniGraph is thinking...')).toBeInTheDocument()

    // Wait for assistant message to appear
    await waitFor(() => {
      expect(screen.getByText('This is a test response from AI.')).toBeInTheDocument()
    })

    // Check if typing indicator disappears
    expect(screen.queryByText('UniGraph is thinking...')).not.toBeInTheDocument()
  })

  it('displays error message when API fails', async () => {
    ;(fetch as any).mockResolvedValue({
      ok: false,
    })

    render(<ChatArea />)
    const input = screen.getByPlaceholderText('Ask UniGraph...')
    const sendButton = screen.getByLabelText('Send message')

    fireEvent.change(input, { target: { value: 'Failure test' } })
    fireEvent.click(sendButton)

    await waitFor(() => {
      expect(screen.getByText(/Error: Could not connect to the UniGraph engine/)).toBeInTheDocument()
    })
  })
})
