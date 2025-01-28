// Authentication service for handling login, registration, and token management
import axios from 'axios';

const API_URL = 'http://localhost:3000';

class AuthService {
  login(username, password) {
    return axios
      .post(`${API_URL}/auth/login`, {
        username,
        password
      })
      .then(response => {
        if (response.data.accessToken) {
          localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
      });
  }

  logout() {
    localStorage.removeItem('user');
  }

  register(username, email, password) {
    return axios.post(`${API_URL}/auth/register`, {
      username,
      email,
      password
    });
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  getAuthHeader() {
    const user = this.getCurrentUser();
    if (user && user.accessToken) {
      return { Authorization: 'Bearer ' + user.accessToken };
    } else {
      return {};
    }
  }
}

export default new AuthService();
