import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './TweetList.css';

const TweetList = ({ userId }) => {
  const [tweets, setTweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTweets = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`http://localhost:3000/tweet/findByUserId?userId=${userId}`);
        setTweets(response.data);
        setLoading(false);
      } catch (err) {
        setError('Error fetching tweets. Please try again later.');
        setLoading(false);
        console.error('Error fetching tweets:', err);
      }
    };

    fetchTweets();
  }, [userId]);

  if (loading) {
    return <div className="loading">Loading tweets...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (tweets.length === 0) {
    return <div className="no-tweets">No tweets found for this user.</div>;
  }

  return (
    <div className="tweet-list">
      <h2>Tweets</h2>
      {tweets.map((tweet) => (
        <div key={tweet.id} className="tweet">
          <div className="tweet-header">
            <span className="username">{tweet.user.username}</span>
            <span className="date">{tweet.createdAt}</span>
          </div>
          <div className="tweet-content">{tweet.content}</div>
          <div className="tweet-actions">
            <button className="like-button">❤️ {tweet.likeCount || 0}</button>
            <button className="comment-button">💬 {tweet.commentCount || 0}</button>
            <button className="retweet-button">🔄 {tweet.retweetCount || 0}</button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default TweetList;
