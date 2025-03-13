import { useEffect, useState } from "react";
import { getUserTweets, getTweetsByUserId } from "../api/TweetApi";
import { checkAuthStatus } from "../api/AuthApi";

const TweetListPage = () => {
  const [tweets, setTweets] = useState([]);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchTweets = async () => {
      const currentUser = await checkAuthStatus(); // Kullanıcı giriş yapmış mı kontrol et
      setUser(currentUser);

      if (currentUser) {
        const userTweets = await getUserTweets();
        setTweets(userTweets);
      } else {
        const publicTweets = await getTweetsByUserId(1); // Örnek olarak userId = 1 olan tweetleri getir
        setTweets(publicTweets);
      }
    };

    fetchTweets();
  }, []);

  return (
    <div>
      <h2>Tweetler</h2>
      {user && <p>Hoş geldin, {user}!</p>}
      <ul>
        {tweets.map((tweet) => (
          <li key={tweet.id}>{tweet.content}</li>
        ))}
      </ul>
    </div>
  );
};

export default TweetListPage;
