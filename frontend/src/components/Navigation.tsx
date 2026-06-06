interface NavigationProps {
  activeTab: 'chat' | 'graph'
  onTabChange: (tab: 'chat' | 'graph') => void
}

const Navigation = ({ activeTab, onTabChange }: NavigationProps) => {
  const tabs = [
    { id: 'chat', label: 'Chat' },
    { id: 'graph', label: 'Graph' },
  ] as const

  return (
    <nav className="sticky top-0 z-10 flex items-center justify-between w-full bg-canvas/80 backdrop-blur-md" style={{
      height: '64px',
      borderBottom: '1px solid var(--hairline)',
      padding: '0 24px',
    }}>
      <div className="flex items-center" style={{ gap: '12px' }}>
        <div className="flex items-center justify-center w-8 h-8 rounded-sm bg-[var(--surface-dark)] text-white">
          <span className="text-xs font-bold">UG</span>
        </div>
        <span className="serif" style={{ fontWeight: 400, fontSize: '1.35rem', letterSpacing: '-0.025em', color: 'var(--ink)' }}>UniGraph</span>
      </div>
      
      <div className="flex items-center" role="tablist" style={{ gap: '8px', backgroundColor: 'var(--surface-soft)', padding: '4px', borderRadius: 'var(--rounded-md)' }}>
        {tabs.map((tab) => (
          <button
            key={tab.id}
            role="tab"
            aria-selected={activeTab === tab.id}
            onClick={() => onTabChange(tab.id as 'chat' | 'graph')}
            className="flex items-center justify-center transition-all duration-200"
            style={{
              padding: '6px 16px',
              borderRadius: 'var(--rounded-md)',
              fontSize: '14px',
              fontWeight: 500,
              backgroundColor: activeTab === tab.id ? 'var(--surface-card)' : 'transparent',
              color: activeTab === tab.id ? 'var(--primary)' : 'var(--muted)',
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>
      
      <div className="flex items-center justify-end" style={{ width: '120px' }}>
        <button className="flex items-center justify-center transition-colors duration-200 hover:bg-surface-soft" style={{
          padding: '8px 16px',
          borderRadius: 'var(--rounded-md)',
          fontSize: '14px',
          fontWeight: 500,
          backgroundColor: 'var(--canvas)',
          color: 'var(--ink)',
          border: '1px solid var(--hairline)',
        }}>
          Share
        </button>
      </div>
    </nav>
  )
}

export default Navigation
