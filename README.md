# Reddit Clone

- Backend Repository: https://github.com/jainamit130/Reddit-Clone-Backend
- Frontend Repository: https://github.com/jainamit130/Reddit-Clone-Client


# Tutorial

- Deployed on Render and Netlify for exploring the application: https://master--reddit-clone-amit.netlify.app/ 

- User Registration

![First Gif (Login and Sign up) (2) (1)](https://github.com/jainamit130/Reddit-Clone-Backend/assets/87203912/cc6ec0ae-bfa2-4f99-8c54-f5cc13ecbb6a)


- Post and Community Actions

![secondVideo (1)](https://github.com/jainamit130/Reddit-Clone-Backend/assets/87203912/2876205d-ee20-4273-8bab-5c24e46b040d)



- Reddit Search


![video3](https://github.com/jainamit130/Reddit-Clone-Backend/assets/87203912/faec471d-a3bd-4bca-a82e-dc6ac9d01381)



- User Profiles


![fourth](https://github.com/jainamit130/Reddit-Clone-Backend/assets/87203912/4b111eb2-a251-4c8f-923a-ffe39af98fc0)




## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
  - [Clone the Repositories](#clone-the-repositories)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication)
  - [Posts](#posts)
  - [Comments](#comments)

## Introduction
This project is a clone of Reddit, built to practice and demonstrate skills in full-stack development using Angular for the frontend and Spring Boot for the backend. It includes features such as user authentication, creating posts, and commenting on posts.

## Features
- User Authentication (Sign Up, Log In, Log Out)
- Create, Read, Update, Delete (CRUD) Posts
- Upvote and Downvote Posts
- Comment on Posts
- User Profiles

## Technology Stack
- **Frontend**: Angular
- **Backend**: Spring Boot
- **Database**: MySQL
- **Authentication**: JWT

## Installation

### Clone the Repositories

git clone https://github.com/jainamit130/Reddit-Clone-Client.git

git clone https://github.com/jainamit130/Reddit-Clone-Backend.git

###  Backend Setup

1.  Navigate to the backend directory:
    cd Reddit-Clone-Backend

2.  Install dependencies:
    mvn install

3.  Set up environment variables:
    Set up application.properties file in src/main/resources directory and update the following to the respective database:
    spring.datasource.url=jdbc:mysql://localhost:3306/reddit_clone
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    spring.jpa.hibernate.ddl-auto=update
    jwt.secret=your_secret_key

4. Start the backend server:
    Run the application on the local machine

### Frontend Setup
1.  Navigate to the frontend directory:
    cd Reddit-Clone-Client

2.  Install dependencies:
    npm install

3.  Start the frontend server:
    ng serve

4.  Open your browser and navigate:
    http://localhost:4200/.

## Usage
1.  Open your browser and go to http://localhost:4200/ 
2.  Sign up for a new account or log in with existing credentials.
3.  Create new posts, upvote/downvote posts, and comment on posts.

## API Endpoints
### Authentication
1.  POST /api/auth/register - Register a new user
2.  POST /api/auth/login - Log in a user

### Posts
1.  GET /api/posts - Get all posts
2.  POST /api/posts - Create a new post
3.  GET /api/posts/:id - Get a single post by ID
4.  PUT /api/posts/:id - Update a post by ID
5.  DELETE /api/posts/:id - Delete a post by ID

### Comments
1.  POST /api/posts/:postId/comments - Add a comment to a post
2.  GET /api/posts/:postId/comments - Get comments for a post
