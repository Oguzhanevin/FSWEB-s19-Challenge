import axios from "axios";

const API_URL = "http://localhost:3000/tweet";

export const getUserTweets = async () => {
  try {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      console.error("JWT Token bulunamadı. Kullanıcı giriş yapmamış.");
      return [];
    }

    const response = await axios.get(`${API_URL}/findByUserId`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    console.error(
      "Tweetleri çekerken hata oluştu:",
      error.response?.data || error
    );
    return [];
  }
};
export const getTweetsByUserId = async (userId) => {
  try {
    const response = await axios.get(`${API_URL}/findByUserId/${userId}`);
    return response.data;
  } catch (error) {
    console.error(
      "Tweetleri çekerken hata oluştu:",
      error.response?.data || error
    );
    return [];
  }
};

export const createTweet = async (content) => {
  try {
    const token = localStorage.getItem("jwtToken");
    if (!token) throw new Error("Giriş yapmadan tweet atamazsın.");

    const response = await axios.post(
      `${API_URL}`,
      { content },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    return response.data;
  } catch (error) {
    console.error("Tweet atılamadı:", error.response?.data || error);
    return null;
  }
};
