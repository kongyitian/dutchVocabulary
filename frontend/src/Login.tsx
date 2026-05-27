import React, { useState } from 'react';
import { api } from './api';

interface Props {
  onLogin: () => void;
}

const Login: React.FC<Props> = ({ onLogin }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        await api.login(username, password);
      } else {
        await api.register(username, password, displayName || username);
      }
      onLogin();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>{isLogin ? '👋 Welcome Back!' : '🎉 Create Account'}</h2>
        <p className="auth-subtitle">
          {isLogin ? 'Sign in to continue learning Dutch' : 'Start your Dutch learning journey'}
        </p>

        {error && (
          <div className="error-message" role="alert">
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter your username"
              required
              autoComplete="username"
              disabled={loading}
              aria-describedby={error ? 'error-message' : undefined}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
              autoComplete={isLogin ? 'current-password' : 'new-password'}
              disabled={loading}
              minLength={4}
            />
          </div>

          {!isLogin && (
            <div className="form-group">
              <label htmlFor="displayName">Display Name (optional)</label>
              <input
                id="displayName"
                type="text"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                placeholder="How should we call you?"
                disabled={loading}
              />
            </div>
          )}

          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? (
              <span className="loading-spinner-small"></span>
            ) : isLogin ? (
              '🔓 Sign In'
            ) : (
              '🚀 Create Account'
            )}
          </button>
        </form>

        <div className="auth-switch">
          {isLogin ? (
            <p>
              Don't have an account?{' '}
              <button
                type="button"
                className="link-button"
                onClick={() => {
                  setIsLogin(false);
                  setError('');
                }}
              >
                Sign up
              </button>
            </p>
          ) : (
            <p>
              Already have an account?{' '}
              <button
                type="button"
                className="link-button"
                onClick={() => {
                  setIsLogin(true);
                  setError('');
                }}
              >
                Sign in
              </button>
            </p>
          )}
        </div>

        <div className="auth-features">
          <h3>🇳🇱 Learn Dutch with:</h3>
          <ul>
            <li>📊 Progress tracking & statistics</li>
            <li>🏆 Achievements & streaks</li>
            <li>🧠 Smart quizzes with spaced repetition</li>
            <li>📚 90+ Dutch words & phrases</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Login;

