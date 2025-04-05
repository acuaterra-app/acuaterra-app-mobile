# Acuaterra Mobile App - Session Management

## Implementation Details

### Security Components
The application implements secure session management with the following components:

1. **ApplicationContextProvider** - A custom Application class that provides context to non-activity classes
2. **SessionManager** - A secure session management utility that stores user data using EncryptedSharedPreferences

### Security Features

The SessionManager class provides:

- Encrypted storage of sensitive session data using AndroidX Security library
- Proper validation of user input and session data
- Secure handling of authentication tokens
- User type management (owner/worker roles)
- Clear session data management including secure logout
- Debug-friendly logging with sensitive data masking

### Usage

#### Initialize SessionManager
```java
SessionManager sessionManager = new SessionManager(context);
```

#### Create Login Session
```java
sessionManager.createLoginSession(
    userId,
    userName, 
    userEmail, 
    SessionManager.USER_TYPE_OWNER, // or USER_TYPE_WORKER
    farmId,
    farmName,
    authToken,
    refreshToken,
    tokenExpiryTime
);
```

#### Check Login Status
```java
if (sessionManager.isLoggedIn()) {
    // User is logged in
    if (sessionManager.isOwner()) {
        // User is a farm owner
    } else {
        // User is a farm worker
    }
} else {
    // User is not logged in
}
```

#### Get User Info
```java
int userId = sessionManager.getUserId();
String userName = sessionManager.getUserName();
String userEmail = sessionManager.getUserEmail();
String userType = sessionManager.getUserType();
int farmId = sessionManager.getFarmId();
String farmName = sessionManager.getFarmName();
```

#### Manage Auth Tokens
```java
// Check if token is valid (not expired)
if (sessionManager.isTokenValid()) {
    String token = sessionManager.getAuthToken();
    // Use token for API requests
} else {
    // Token expired, refresh it
    String refreshToken = sessionManager.getRefreshToken();
    // Call API to refresh token
    sessionManager.updateTokens(newAuthToken, newRefreshToken, newExpiryTime);
}
```

#### Log Out
```java
sessionManager.logout();
```

### Dependencies

This implementation requires the AndroidX Security Crypto library:
```kotlin
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

