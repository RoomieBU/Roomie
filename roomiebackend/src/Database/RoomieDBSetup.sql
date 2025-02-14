-- Create the Roomie Database
CREATE DATABASE IF NOT EXISTS Roomie;
USE Roomie;

-- Users Table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    about_me TEXT,
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Preferences Table
CREATE TABLE UserPreferences (
    user_id INT PRIMARY KEY,
    preferred_gender VARCHAR(50),
    pet_friendly BOOLEAN,
    personality VARCHAR(255),
    wakeup_time TIME,
    sleep_time TIME,
    quiet_hours VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- User Schedule Table
CREATE TABLE UserSchedule (
    user_id INT PRIMARY KEY,
    class_schedule TEXT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- User Housing Table
CREATE TABLE UserHousing (
    user_id INT PRIMARY KEY,
    location VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- User Ratings Table
CREATE TABLE UserRatings (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    rated_user INT,
    reviewer_user INT,
    rating_value INT CHECK (rating_value BETWEEN 1 AND 5),
    comment TEXT,
    FOREIGN KEY (rated_user) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_user) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- User Matches Table
CREATE TABLE UserMatches (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    user1_id INT,
    user2_id INT,
    match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- User Chat Groups Table
CREATE TABLE UserChatGroups (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Messages Table
CREATE TABLE UserMessages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT,
    group_id INT,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES UserChatGroups(group_id) ON DELETE CASCADE
);

-- User Chat Group Members Table
CREATE TABLE UserChatGroupMembers (
    group_id INT,
    user_id INT,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES UserChatGroups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);