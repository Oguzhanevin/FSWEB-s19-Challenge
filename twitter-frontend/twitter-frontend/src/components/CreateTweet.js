import React, { useState } from 'react';
import TweetService from '../services/tweet.service';
import './CreateTweet.css';

const CreateTweet = ({ onTweetCreated }) => {
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const maxLength = 280;

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setError('Tweet cannot be empty');
      return;
    }

    if (content.length > maxLength) {
      setError(`Tweet exceeds maximum length of ${maxLength} characters`);
      return;
    }

    setLoading(true);
    setError('');

    TweetService.createTweet(content)
      .then(() => {
        setContent('');
        setLoading(false);
        if (onTweetCreated) {
          onTweetCreated();
        }
      })
      .catch((error) => {
        setLoading(false);
        setError('Error creating tweet. Please try again.');
        console.error('Error creating tweet:', error);
      });
  };

  const remainingChars = maxLength - content.length;

  return (
    <div className="create-tweet-container">
      <form onSubmit={handleSubmit} className="tweet-form">
        <textarea
          placeholder="What's happening?"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="tweet-textarea"
          maxLength={maxLength}
        />
        
        <div className="tweet-form-footer">
          <span className={`char-counter ${remainingChars < 20 ? 'warning' : ''}`}>
            {remainingChars}
          </span>
          
          <button 
            type="submit" 
            className="tweet-button"
            disabled={loading || !content.trim() || content.length > maxLength}
          >
            {loading ? 'Posting...' : 'Tweet'}
          </button>
        </div>
        
        {error && <div className="tweet-error">{error}</div>}
      </form>
    </div>
  );
};

export default CreateTweet;
