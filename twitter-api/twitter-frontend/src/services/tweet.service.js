// Service for handling tweet-related operations
import axios from 'axios';
import authService from './auth.service';

const API_URL = 'http://localhost:3000';

class TweetService {
  getAllTweets() {
    return axios.get(`${API_URL}/tweet/all`);
  }

  getTweetsByUserId(userId) {
    return axios.get(`${API_URL}/tweet/findByUserId?userId=${userId}`);
  }

  createTweet(content) {
    return axios.post(
      `${API_URL}/tweet/create`,
      { content },
      { headers: authService.getAuthHeader() }
    );
  }

  likeTweet(tweetId) {
    return axios.post(
      `${API_URL}/like/create`,
      { tweetId },
      { headers: authService.getAuthHeader() }
    );
  }

  retweet(tweetId) {
    return axios.post(
      `${API_URL}/retweet/create`,
      { tweetId },
      { headers: authService.getAuthHeader() }
    );
  }

  addComment(tweetId, content) {
    return axios.post(
      `${API_URL}/comment/create`,
      { tweetId, content },
      { headers: authService.getAuthHeader() }
    );
  }
}

export default new TweetService();
