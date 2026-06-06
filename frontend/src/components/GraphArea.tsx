import { useState, useEffect, useRef } from 'react';
import ForceGraph2D from 'react-force-graph-2d';

const data = {
  nodes: [
    { id: 'Graph DB', color: '#cc785c', val: 20 },
    { id: 'Neo4j', color: '#cc785c', val: 15 },
    { id: 'RAG', color: '#181715', val: 18 },
    { id: 'LLM', color: '#181715', val: 12 },
    { id: 'Context', color: '#181715', val: 10 },
  ],
  links: [
    { source: 'Graph DB', target: 'Neo4j' },
    { source: 'Graph DB', target: 'RAG' },
    { source: 'RAG', target: 'LLM' },
    { source: 'RAG', target: 'Context' },
  ],
};

const GraphArea = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [dimensions, setDimensions] = useState({ width: 800, height: 600 });

  useEffect(() => {
    if (containerRef.current) {
      const resizeObserver = new ResizeObserver((entries) => {
        for (const entry of entries) {
          const { width, height } = entry.contentRect;
          setDimensions({ width, height });
        }
      });

      resizeObserver.observe(containerRef.current);
      return () => resizeObserver.disconnect();
    }
  }, []);

  return (
    <div 
      ref={containerRef} 
      role="figure"
      aria-label="Knowledge Graph Visualization"
      style={{ 
        width: '100%', 
        height: '100%', 
        position: 'relative', 
        backgroundColor: 'var(--canvas)',
        overflow: 'hidden'
      }}
    >
      <ForceGraph2D
        graphData={data}
        width={dimensions.width}
        height={dimensions.height}
        backgroundColor="#faf9f5"
        linkColor={() => '#d1d0cc'}
        nodeColor={(node: any) => node.color}
        nodeVal={(node: any) => node.val}
        nodeLabel={(node: any) => node.id}
      />
      
      {/* Insights Panel */}
      <div 
        style={{
          position: 'absolute',
          top: 'var(--space-md)',
          left: 'var(--space-md)',
          width: '280px',
          padding: 'var(--space-md)',
          backgroundColor: 'rgba(250, 249, 245, 0.8)', // Matching --canvas with alpha
          backdropFilter: 'blur(12px)',
          WebkitBackdropFilter: 'blur(12px)',
          border: '1px solid var(--hairline)',
          borderRadius: 'var(--rounded-lg)',
          zIndex: 10,
          pointerEvents: 'auto', 
          boxShadow: '0 4px 12px rgba(20, 20, 19, 0.05)'
        }}
      >
        <h3 className="serif" style={{ 
          marginBottom: 'var(--space-xs)', 
          fontSize: '1.25rem',
          letterSpacing: '-0.3px',
          color: 'var(--ink)'
        }}>
          Knowledge Insights
        </h3>
        <p style={{ 
          fontSize: '0.875rem', 
          color: 'var(--body)', 
          lineHeight: '1.55',
          fontFamily: 'var(--font-sans)'
        }}>
          Exploring the synergy between Graph Databases and RAG. Nodes in <span style={{ color: 'var(--primary)', fontWeight: 500 }}>Coral</span> represent infrastructure.
        </p>
      </div>
    </div>
  );
};

export default GraphArea;
