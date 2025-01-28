import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import AuthService from '../services/auth.service';
import TweetService from '../services/tweet.service';
import './Profile.css';

const Profile = () => {
  const { id } = useParams();
  const [userProfile, setUserProfile] = useState(null);
  const [userTweets, setUserTweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentUser, setCurrentUser] = useState(undefined);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }

    // Fetch user profile
    // In a real app, you would have a dedicated service for this
    // For now, we'll simulate it with available data
    fetchUserProfile();
    fetchUserTweets();
  }, [id]);

  const fetchUserProfile = () => {
    // This would typically be a fetch to your user endpoint
    // For demo purposes, we'll create a mock profile
    setTimeout(() => {
      setUserProfile({
        id: id,
        username: 'user' + id,
        bio: 'Twitter clone user',
        joinDate: '2023-01-01'
      });
    }, 500);
  };

  const fetchUserTweets = () => {
    setLoading(true);
    TweetService.getTweetsByUserId(id)
      .then((response) => {
        setUserTweets(response.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Error fetching user tweets:', error);
        setError('Error fetching tweets. Please try again later.');
        setLoading(false);
      });
  };

  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  if (loading && !userProfile) {
    return <div className="loading-container">Loading profile...</div>;
  }

  if (error && !userProfile) {
    return <div className="error-container">{error}</div>;
  }

  return (
    <div className="profile-container">
      {userProfile && (
        <>
          <div className="profile-header">
            <div className="profile-banner"></div>
            <div className="profile-info">
              <div className="profile-avatar-container">
                <img
                  src={`https://ui-avatars.com/api/?name=${userProfile.username}&background=random&size=120`}
                  alt="Profile"
                  className="profile-avatar"
                />
              </div>
              <h2 className="profile-name">@{userProfile.username}</h2>
              <p className="profile-bio">{userProfile.bio}</p>
              <p className="profile-joined">
                <span role="img" aria-label="calendar">📅</span> Joined {formatDate(userProfile.joinDate)}
              </p>
            </div>
          </div>

          <div className="profile-tabs">
            <button className="tab-button active">Tweets</button>
            <button className="tab-button">Likes</button>
          </div>

          <div className="profile-tweets">
            <h3 className="section-title">Tweets</h3>
            
            {loading ? (
              <div className="loading-message">Loading tweets...</div>
            ) : error ? (
              <div className="error-message">{error}</div>
            ) : userTweets.length === 0 ? (
              <div className="no-tweets-message">No tweets yet.</div>
            ) : (
              <div className="tweets-list">
                {userTweets.map((tweet) => (
                  <div key={tweet.id} className="tweet-card">
                    <div className="tweet-header">
                      <img
                        src={`https://ui-avatars.com/api/?name=${userProfile.username}&background=random`}
                        alt="Avatar"
                        className="tweet-avatar"
                      />
                      <div className="tweet-user-info">
                        <span className="tweet-username">@{userProfile.username}</span>
                        <span className="tweet-date">{formatDate(tweet.createdAt)}</span>
                      </div>
                    </div>
                    <div className="tweet-content">{tweet.content}</div>
                    <div className="tweet-stats">
                      <div className="tweet-stat">
                        <span role="img" aria-label="like">❤️</span> {tweet.likeCount || 0}
                      </div>
                      <div className="tweet-stat">
                        <span role="img" aria-label="comment">💬</span> {tweet.commentCount || 0}
                      </div>
                      <div className="tweet-stat">
                        <span role="img" aria-label="retweet">🔄</span> {tweet.retweetCount || 0}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default Profile;
