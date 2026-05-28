import React, { useState, useEffect } from 'react';
import Dashboard from './Dashboard';
import Quiz from './Quiz';
import WordList from './WordList';
import Login from './Login';
import { isAuthenticated, api } from './api';

type View = 'dashboard' | 'quiz' | 'words';

const App: React.FC = () => {
  const [currentView, setCurrentView] = useState<View>('dashboard');
  const [loggedIn, setLoggedIn] = useState(isAuthenticated());

  useEffect(() => {
    setLoggedIn(isAuthenticated());
  }, []);

  const handleLogout = () => {
    api.logout();
    setLoggedIn(false);
  };

  if (!loggedIn) {
    return (
      <div className="app">
        <header className="header">
          <h1>🇳🇱 Dutch Vocabulary</h1>
          <p>Learn Dutch words with interactive quizzes</p>
        </header>
        <Login onLogin={() => setLoggedIn(true)} />
      </div>
    );
  }

  return (
    <div className="app">
      <header className="header">
        <h1>🇳🇱 Dutch Vocabulary</h1>
        <p>Learn Dutch words with interactive quizzes</p>
      </header>

      <nav className="nav">
        <button
          className={currentView === 'dashboard' ? 'active' : ''}
          onClick={() => setCurrentView('dashboard')}
        >
          📊 Dashboard
        </button>
        <button
          className={currentView === 'quiz' ? 'active' : ''}
          onClick={() => setCurrentView('quiz')}
        >
          🎯 Practice
        </button>
        <button
          className={currentView === 'words' ? 'active' : ''}
          onClick={() => setCurrentView('words')}
        >
          📚 Word List
        </button>
        <button onClick={handleLogout} style={{ marginLeft: 'auto' }}>
          🚪 Logout
        </button>
      </nav>

      <main>
        {currentView === 'dashboard' && (
          <Dashboard onStartQuiz={() => setCurrentView('quiz')} />
        )}
        {currentView === 'quiz' && (
          <Quiz onBack={() => setCurrentView('dashboard')} />
        )}
        {currentView === 'words' && (
          <WordList />
        )}
      </main>

      <footer style={{
        textAlign: 'center',
        padding: '30px',
        color: 'rgba(255,255,255,0.7)',
        fontSize: '0.9rem'
      }}>
        Made with ❤️ to help you learn Dutch
      </footer>
    </div>
  );
};

export default App;
