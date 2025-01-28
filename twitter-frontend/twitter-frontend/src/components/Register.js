import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import './Register.css';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [successful, setSuccessful] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleRegister = (e) => {
    e.preventDefault();
    
    setMessage('');
    setSuccessful(false);

    if (!username.trim() || !email.trim() || !password.trim()) {
      setMessage('All fields are required!');
      return;
    }

    AuthService.register(username, email, password).then(
      (response) => {
        setMessage(response.data.message || 'Registration successful! You can now log in.');
        setSuccessful(true);
        setTimeout(() => {
          navigate('/login');
        }, 3000);
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setMessage(resMessage);
        setSuccessful(false);
      }
    );
  };

  return (
    <div className="register-container">
      <div className="register-form-card">
        <img
          src="https://abs.twimg.com/responsive-web/client-web/icon-default.522d363a.png"
          alt="Twitter Logo"
          className="twitter-logo"
        />
        <h2>Create your account</h2>

        <form onSubmit={handleRegister}>
          {!successful && (
            <>
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
                  type="email"
                  className="form-control"
                  placeholder="Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
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
                <small className="form-text text-muted">
                  Password must be at least 6 characters.
                </small>
              </div>

              <div className="form-group">
                <button className="register-button">Sign up</button>
              </div>
            </>
          )}

          {message && (
            <div className={successful ? "success-message" : "error-message"}>
              {message}
            </div>
          )}
        </form>

        <div className="register-footer">
          <span>Already have an account?</span>
          <a href="/login" className="login-link">
            Log in
          </a>
        </div>
      </div>
    </div>
  );
};

export default Register;
