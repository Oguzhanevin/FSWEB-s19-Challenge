import axios from "axios";

const API_URL = "http://localhost:3000/auth";

export const checkAuthStatus = async () => {
  try {
    const token = localStorage.getItem("jwtToken");
    if (!token) return null;

    const response = await axios.get(`${API_URL}/check`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data; // Kullanıcı adı döner
  } catch (error) {
    console.error("Kullanıcı oturumu doğrulanamadı:", error);
    return null;
  }
};

export const loginUser = async (username, password) => {
  try {
    const response = await axios.post(`${API_URL}/login`, {
      username,
      password,
    });

    const token = response.data.split("JWT Token: ")[1];
    localStorage.setItem("jwtToken", token);
    return token;
  } catch (error) {
    console.error("Giriş başarısız:", error.response?.data || error);
    return null;
  }
};

export const registerUser = async (username, email, password) => {
  try {
    const response = await axios.post(`${API_URL}/register`, {
      username,
      email,
      password,
    });

    const token = response.data.split("JWT Token: ")[1];
    localStorage.setItem("jwtToken", token);
    return token;
  } catch (error) {
    console.error("Kayıt başarısız:", error.response?.data || error);
    return null;
  }
};
