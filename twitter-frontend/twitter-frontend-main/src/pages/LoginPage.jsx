import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/AuthApi";

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    const token = await loginUser(username, password);

    if (token) {
      navigate("/tweets"); // Başarılı giriş sonrası tweet listesine yönlendir
    } else {
      alert("Giriş başarısız!");
    }
  };

  return (
    <div>
      <h2>Giriş Yap</h2>
      <form onSubmit={handleLogin}>
        <input
          type="text"
          placeholder="Kullanıcı Adı"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Şifre"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Giriş Yap</button>
      </form>
    </div>
  );
};

export default LoginPage;
