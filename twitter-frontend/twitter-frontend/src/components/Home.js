import React, { useState, useEffect } from 'react';
import TweetService from '../services/tweet.service';
import AuthService from '../services/auth.service';
import './Home.css';
import CreateTweet from './CreateTweet';

const Home = () => {
  const [tweets, setTweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentUser, setCurrentUser] = useState(undefined);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }

    fetchTweets();
  }, []);

  const fetchTweets = () => {
    setLoading(true);
    TweetService.getAllTweets()
      .then((response) => {
        setTweets(response.data);
        setLoading(false);
      })
      .catch((error) => {
        setError('Error fetching tweets. Please try again later.');
        setLoading(false);
        console.error('Error fetching tweets:', error);
      });
  };

  const handleTweetCreated = () => {
    fetchTweets();
  };

  const handleLike = (tweetId) => {
    if (!currentUser) {
      alert('Please login to like tweets.');
      return;
    }

    TweetService.likeTweet(tweetId)
      .then(() => {
        fetchTweets();
      })
      .catch((error) => {
        console.error('Error liking tweet:', error);
      });
  };

  const handleRetweet = (tweetId) => {
    if (!currentUser) {
      alert('Please login to retweet.');
      return;
    }

    TweetService.retweet(tweetId)
      .then(() => {
        fetchTweets();
      })
      .catch((error) => {
        console.error('Error retweeting:', error);
      });
  };

  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  if (loading) {
    return <div className="loading-container">Loading tweets...</div>;
  }

  if (error) {
    return <div className="error-container">{error}</div>;
  }

  return (
    <div className="home-container">
      <div className="tweets-section">
        <h2 className="timeline-title">Home</h2>
        
        {currentUser && (
          <CreateTweet onTweetCreated={handleTweetCreated} />
        )}

        {tweets.length === 0 ? (
          <div className="no-tweets">No tweets yet. Be the first to tweet!</div>
        ) : (
          <div className="tweets-list">
            {tweets.map((tweet) => (
              <div key={tweet.id} className="tweet-card">
                <div className="tweet-header">
                  <img
                    src={`https://ui-avatars.com/api/?name=${tweet.user.username}&background=random`}
                    alt="Avatar"
                    className="avatar"
                  />
                  <div className="user-info">
                    <span className="username">{tweet.user.username}</span>
                    <span className="date">{formatDate(tweet.createdAt)}</span>
                  </div>
                </div>
                <div className="tweet-content">{tweet.content}</div>
                <div className="tweet-actions">
                  <button 
                    className="action-button like-button"
                    onClick={() => handleLike(tweet.id)}
                  >
                    ❤️ {tweet.likeCount || 0}
                  </button>
                  <button 
                    className="action-button comment-button"
                  >
                    💬 {tweet.commentCount || 0}
                  </button>
                  <button 
                    className="action-button retweet-button"
                    onClick={() => handleRetweet(tweet.id)}
                  >
                    🔄 {tweet.retweetCount || 0}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
