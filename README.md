# Twitter API Clone

A Twitter-like API built with Spring Boot, featuring tweets, comments, likes, and retweets.

## Project Overview

This project is a Twitter API clone built using Spring Boot, implementing core Twitter functionalities:
- User registration and authentication with JWT
- Creating and managing tweets
- Adding comments to tweets
- Liking and unliking tweets
- Retweeting functionality

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Security with JWT
- PostgreSQL
- JUnit and Mockito for testing

### Frontend (Optional)
- React.js
- Axios for API requests
- CSS for styling

## Project Structure

The project follows a layered architecture:
- **Entity Layer**: Domain models like User, Tweet, Comment, etc.
- **Repository Layer**: Spring Data JPA repositories
- **Service Layer**: Business logic
- **Controller Layer**: REST endpoints
- **Security**: JWT-based authentication and authorization
- **Exception Handling**: Global exception handling

## Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL
- Node.js and npm (for frontend)

### Backend Setup
1. Clone the repository
2. Configure PostgreSQL database in `application.properties`
3. Run the Spring Boot application:
   ```
   cd twitter-api
   ./mvnw spring-boot:run
   ```

The API will be available at http://localhost:3000

### Frontend Setup (Optional)
1. Install dependencies:
   ```
   cd twitter-frontend
   npm install
   ```
2. Run the development server:
   ```
   npm start
   ```
   
The frontend will be available at http://localhost:3200

## API Endpoints

### Authentication
- POST `/register` - Register a new user
- POST `/login` - Login and get JWT token

### Tweets
- POST `/tweet` - Create a new tweet
- GET `/tweet/findByUserId` - Get all tweets by user ID
- GET `/tweet/findById` - Get a tweet by ID
- PUT `/tweet/:id` - Update a tweet
- DELETE `/tweet/:id` - Delete a tweet

### Comments
- POST `/comment/` - Add a comment to a tweet
- PUT `/comment/:id` - Update a comment
- DELETE `/comment/:id` - Delete a comment
- GET `/comment/tweet/:tweetId` - Get all comments for a tweet

### Likes
- POST `/like` - Like a tweet
- POST `/dislike` - Unlike a tweet
- GET `/like/count` - Get like count for a tweet
- GET `/like/check` - Check if a user has liked a tweet

### Retweets
- POST `/retweet/` - Retweet a tweet
- DELETE `/retweet/:id` - Delete a retweet
- GET `/retweet/count` - Get retweet count for a tweet
- GET `/retweet/check` - Check if a user has retweeted a tweet

## Testing

Run the unit tests:
```
cd twitter-api
./mvnw test
```

## CORS Configuration

The backend includes CORS configuration to allow requests from the frontend running on port 3200.

## Contributing

Feel free to submit issues or pull requests for any improvements or bug fixes.
