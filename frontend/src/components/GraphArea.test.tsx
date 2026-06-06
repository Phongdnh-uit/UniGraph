import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import GraphArea from './GraphArea';

// Mock ResizeObserver
class ResizeObserverMock {
  observe = vi.fn();
  unobserve = vi.fn();
  disconnect = vi.fn();
}

vi.stubGlobal('ResizeObserver', ResizeObserverMock);

// Mock react-force-graph-2d
vi.mock('react-force-graph-2d', () => {
  return {
    default: (props: any) => (
      <div data-testid="force-graph-2d" style={{ width: props.width, height: props.height }}>
        Mock Graph
      </div>
    ),
  };
});

describe('GraphArea', () => {
  it('renders the Knowledge Insights panel', () => {
    render(<GraphArea />);
    
    // Check for title
    const title = screen.getByText('Knowledge Insights');
    expect(title).toBeDefined();
    expect(title.className).toContain('serif');
    
    // Check for description
    const description = screen.getByText(/Exploring the synergy between Graph Databases and RAG/i);
    expect(description).toBeDefined();
    expect(description.textContent).toContain('Nodes in Coral represent infrastructure.');
  });

  it('renders the graph container', () => {
    render(<GraphArea />);
    const graph = screen.getByTestId('force-graph-2d');
    expect(graph).toBeDefined();
  });
});
