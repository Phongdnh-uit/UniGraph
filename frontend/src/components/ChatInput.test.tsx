import { render, screen, fireEvent } from '@testing-library/react'
import '@testing-library/jest-dom'
import ChatInput from './ChatInput'
import { describe, it, expect, vi } from 'vitest'

describe('ChatInput Component', () => {
  it('updates input value on change', () => {
    render(<ChatInput onSend={() => {}} />)
    const textarea = screen.getByPlaceholderText('Ask UniGraph...')
    
    fireEvent.change(textarea, { target: { value: 'Hello' } })
    expect(textarea).toHaveValue('Hello')
  })

  it('calls onSend when button is clicked', () => {
    const onSend = vi.fn()
    render(<ChatInput onSend={onSend} />)
    const textarea = screen.getByPlaceholderText('Ask UniGraph...')
    const button = screen.getByLabelText('Send message')
    
    fireEvent.change(textarea, { target: { value: 'Hello' } })
    fireEvent.click(button)
    
    expect(onSend).toHaveBeenCalledWith('Hello')
    expect(textarea).toHaveValue('')
  })

  it('calls onSend when Enter is pressed without Shift', () => {
    const onSend = vi.fn()
    render(<ChatInput onSend={onSend} />)
    const textarea = screen.getByPlaceholderText('Ask UniGraph...')
    
    fireEvent.change(textarea, { target: { value: 'Hello' } })
    fireEvent.keyDown(textarea, { key: 'Enter', shiftKey: false })
    
    expect(onSend).toHaveBeenCalledWith('Hello')
    expect(textarea).toHaveValue('')
  })

  it('does not call onSend when Enter is pressed with Shift', () => {
    const onSend = vi.fn()
    render(<ChatInput onSend={onSend} />)
    const textarea = screen.getByPlaceholderText('Ask UniGraph...')
    
    fireEvent.change(textarea, { target: { value: 'Hello' } })
    fireEvent.keyDown(textarea, { key: 'Enter', shiftKey: true })
    
    expect(onSend).not.toHaveBeenCalled()
  })

  it('disables input and button when disabled prop is true', () => {
    render(<ChatInput onSend={() => {}} disabled={true} />)
    const textarea = screen.getByPlaceholderText('Ask UniGraph...')
    const button = screen.getByLabelText('Send message')
    
    expect(textarea).toBeDisabled()
    expect(button).toBeDisabled()
  })

  it('disables button when input is empty', () => {
    render(<ChatInput onSend={() => {}} />)
    const button = screen.getByLabelText('Send message')
    expect(button).toBeDisabled()
  })
})
