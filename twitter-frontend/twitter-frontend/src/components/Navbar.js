import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import './Navbar.css';

const Navbar = () => {
  const [currentUser, setCurrentUser] = useState(undefined);
  const navigate = useNavigate();

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    
    if (user) {
      setCurrentUser(user);
    }
  }, []);

  const logOut = () => {
    AuthService.logout();
    setCurrentUser(undefined);
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="logo-container">
          <Link to="/" className="logo">
            <img
              src="https://abs.twimg.com/responsive-web/client-web/icon-default.522d363a.png"
              alt="Twitter Logo"
              className="logo-image"
            />
          </Link>
        </div>

        <div className="nav-links">
          <Link to="/" className="nav-link">
            <span>Home</span>
          </Link>

          {currentUser ? (
            <>
              <Link to={`/profile/${currentUser.id}`} className="nav-link">
                <span>Profile</span>
              </Link>
              <button onClick={logOut} className="logout-button">
                <span>Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">
                <span>Login</span>
              </Link>
              <Link to="/register" className="nav-link">
                <span>Sign Up</span>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
