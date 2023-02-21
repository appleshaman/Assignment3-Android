# 159336 Assignment 2
It is a music player with database

## User instruction

This is the first page when you enter the app. To log in to this app, 
we added two accounts:
 
Both accounts can log into the app. We know sore the plain text of the username and password in the resource code is not ok, but due to the time limit, we did not add a register function to this app. But in the Database Creation part in the Designing Features, we will explain what we did to store the password safely in the database.

 
After you log in, your username will be displayed at the left top corner of this page, and the app will search all the songs in the mobile phone gallery and display them in the list.
 
You can click a song to play it, and when the song is playing, you can either click the song in the list again or click the pause button at the bottom right corner to pause it. To continue playing it, you can do the same action again.  After you select a song, you can click the cover page to enter the third page for more options. If you have not selected a song yet, all the actions above are not available.
 
On this page, you can see the progress bar that displays the duration and progress this song played; you can also drag it to jump to the point of time where you want to listen, just like the other music player people normally use.
  
On the right side of the duration bar, there is a button you can set if you want to make this song play loopy. Thus, if you do not enable it, the song will stop once it is finished, but after enabling this, the song will start again to play after it is finished.

## Design features
#### Activity Creating order and how to save memory
We have 3 pages for this app for general use. The order or direction of use will be:
Login Page -> Main page with music list -> Music page for each single page
But in our design, we first start the Main page instead of the Login Page, and we use the Main page to create the Login page, so the Login page can close after finishing the login, which could save more memory. And the creating order is like this:
Main page with music list -> Login Page -> Music page for each single page
If we create the page just following the order of use, the login page will not be able to close after login, which causes more memory to be used. In every page is finished, the onDestory()  function will stop the broadcast listener/ unbind music service, depending on which page is finished.
  
#### Music play
We use a service to play the music so that we can use control the music in different activities(pages). In the service class, we use a timer to send the music information to the Music page so the page every 0.5 seconds can know the music’s progress and duration.
#### Call back data (how to send data back to the previous page)
In this app, we used both Local broadcasts to send Activity Result Launcher to receive the data; the Local broadcasts are used to send the data needed when the activity is still running, like sending the data about music duration and progress, and the Activity Result Launcher is used to send the data needed when the activity is finished like sent the user name back to the Main page from the Login page after login.
#### Database 
The database implementation of a user login system has used the Android *Room persistent library*.  There is only a single table containing the user login details.
##### 1.	Entity
There is only a single table containing the user login details, which consists of 3 fields. 
| primary | long |	Id (auto generated) |
| ------- | ---- | ---------------------|
|	 |String | username |
|	 |String | password |

##### 2.	DAO
There are three main queries used.<br>

**int initiate()** returns the number of rows present within the user table. It’s main purpose to be called during **OnCreate()** to prepopulate the database
**ifExists(String user)** returns an integer of the number of records wich have the username   user  for which if the user name entered does not exist in the records, the return number will be 0
**ifMatch(String user, String pass)** shows if a record of the username entered exists, then check if the password match with the record corresponding with the username. If matched, then return 1, If not return 0
```java
@Dao
public interface UserDao {
    @Insert
    void insert(User user);
    @Delete
    void deleteUser(User user);
    @Query("DELETE FROM USER")
    void deleteAllUser();
    @Query("SELECT count(*) from user")
    int initiate();
    @Query("SELECT EXISTS(SELECT username FROM user WHERE username = :user)")
    int ifExists(String user);
    @Query("SELECT EXISTS(SELECT user.password FROM user WHERE username = :user AND password = :pass)")
    int ifMatch(String user, String pass);
}
```
#### Database Creation
Before populating the database, the passwords are encrypted using SHA-56 encryption for better security. 
Though due to time limit, we did not make a register function. So the plain passwords are first stored as a String
```java
private static final String username1 = "admin";// first account
private static final String password1 = "159336";
```
**We fixed it, now we can add new users at runtime 22/Feb/2023**

#### Implementation
All implementation is within the login page
Initiate is called during on create to prepopulate the database on first run.
```java
new Thread(() -> {
    count = mDao.initiate();
    Log.i("login", "init: " + count);
}).start();
```

New threads are created to check if username/password matches
Check if user exists 
```java
Thread t1 = new Thread(() -> {
ifUser = mDao.ifExists(user);
});
t1.start();
```

Check if password matches username
```java
Thread t2 = new Thread(() -> { 
    correctness = mDao.ifMatch(user, password});
t2.start();
```
We avoided using a lambda Thread for confirmation and used join() to wait for the query thread to finish before moving forward
```java
try {// search the user name in database
    t1.join();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
**To use the app better, run it on a  real device. The emulator sometimes can not play the music smoothly**
