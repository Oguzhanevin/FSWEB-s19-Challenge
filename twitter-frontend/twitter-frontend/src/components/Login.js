import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import './Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();
    
    setMessage('');
    setLoading(true);

    if (!username.trim() || !password.trim()) {
      setMessage('All fields are required!');
      setLoading(false);
      return;
    }

    AuthService.login(username, password).then(
      () => {
        navigate('/');
        window.location.reload();
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setLoading(false);
        setMessage(resMessage);
      }
    );
  };

  return (
    <div className="login-container">
      <div className="login-form-card">
        <img
          src="https://abs.twimg.com/responsive-web/client-web/icon-default.522d363a.png"
          alt="Twitter Logo"
          className="twitter-logo"
        />
        <h2>Log in to Twitter</h2>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <input
              type="text"
              className="form-control"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div className="form-group">
            <input
              type="password"
              className="form-control"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <div className="form-group">
            <button className="login-button" disabled={loading}>
              {loading ? (
                <span className="loading-spinner"></span>
              ) : (
                "Log in"
              )}
            </button>
          </div>

          {message && (
            <div className="error-message">
              {message}
            </div>
          )}
        </form>

        <div className="login-footer">
          <span>Don't have an account?</span>
          <a href="/register" className="signup-link">
            Sign up
          </a>
        </div>
      </div>
    </div>
  );
};

export default Login;
