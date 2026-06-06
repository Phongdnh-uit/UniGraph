import { Plus, MessageSquare } from 'lucide-react';

const Sidebar = () => {
  const sessions = [
    { id: 1, title: "Exploring Graph Theory" },
    { id: 2, title: "Neo4j Setup Guide" },
    { id: 3, title: "Data Modeling 101" },
  ];

  return (
    <aside 
      className="flex flex-col" 
      style={{
        width: '260px',
        backgroundColor: 'var(--surface-dark)',
        color: 'var(--on-dark)',
        padding: 'var(--space-md)',
        height: '100%',
        borderRight: '1px solid var(--hairline-soft)',
      }}
      aria-label="Chat History Sidebar"
    >
      <button 
        className="flex items-center justify-center transition-colors duration-200" 
        style={{
          width: '100%',
          padding: '12px 20px',
          backgroundColor: 'var(--surface-dark-elevated)',
          color: 'var(--on-dark)',
          borderRadius: 'var(--rounded-md)',
          gap: 'var(--space-xs)',
          marginBottom: 'var(--space-xl)',
          fontSize: '14px',
          fontWeight: 500,
          border: '1px solid transparent',
        }}
        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'var(--surface-dark-soft)'}
        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'var(--surface-dark-elevated)'}
        aria-label="Start a new chat"
      >
        <Plus size={16} aria-hidden="true" />
        <span>New Chat</span>
      </button>

      <nav className="flex flex-col flex-1 overflow-hidden" aria-labelledby="sidebar-history-title">
        <h3 
          id="sidebar-history-title"
          style={{
            fontSize: '11px',
            fontWeight: 700,
            textTransform: 'uppercase',
            letterSpacing: '1.5px',
            color: 'var(--on-dark-soft)',
            marginBottom: 'var(--space-md)',
            fontFamily: 'var(--font-sans)',
            paddingLeft: 'var(--space-xs)',
          }}
        >
          History
        </h3>
        
        <ul className="flex flex-col overflow-auto" style={{ gap: '4px', listStyle: 'none', padding: 0 }}>
          {sessions.map((session) => (
            <li key={session.id}>
              <button
                className="flex items-center transition-colors duration-200"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  borderRadius: 'var(--rounded-md)',
                  gap: '10px',
                  textAlign: 'left',
                  backgroundColor: 'transparent',
                }}
                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'var(--surface-dark-elevated)'}
                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                aria-label={`Open chat: ${session.title}`}
              >
                <MessageSquare 
                  size={14} 
                  style={{ color: 'var(--on-dark-soft)', flexShrink: 0 }} 
                  aria-hidden="true"
                />
                <span style={{
                  fontSize: '14px',
                  color: 'var(--on-dark)',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  fontWeight: 400,
                }}>
                  {session.title}
                </span>
              </button>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;
