import { useState } from 'react'
import Navigation from './components/Navigation'
import Sidebar from './components/Sidebar'
import ChatArea from './components/ChatArea'
import GraphArea from './components/GraphArea'
import './App.css'

type Tab = 'chat' | 'graph'

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('chat')

  return (
    <div className="flex flex-col h-screen w-full bg-[var(--canvas)]">
      <Navigation activeTab={activeTab} onTabChange={setActiveTab} />
      <div className="flex flex-1 overflow-hidden">
        {activeTab === 'chat' && <Sidebar />}
        <main className="flex-1 relative overflow-hidden">
          {activeTab === 'chat' ? <ChatArea /> : <GraphArea />}
        </main>
      </div>
    </div>
  )
}

export default App
