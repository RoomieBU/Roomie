package Controller;

import Database.Dao;
import Database.SQLConnection;
import Database.User;
import Database.UserDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Actually goes through each user and calculates their similarity score, in a loop, forever...
 */
public class MatchingPriorityController extends Thread{
    private final Object lock = new Object();
    private boolean shouldWait = false;

    public void run() {
        try {
            UserDao userDao = new UserDao(SQLConnection.getConnection());
            Dao dao = new Dao(SQLConnection.getConnection());
            while (true) {
                List<User> allUsers = userDao.getAllUsers();
                for (User u : allUsers) {
                    for (User b : allUsers) {
                        Thread.sleep(50);
                        synchronized (lock) {
                            while (shouldWait) {
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        if (u.getEmail().equals(b.getEmail())) {
                            continue;
                        }
                        dao.insert(
                                Map.of("email1", u.getEmail(),
                                        "email2", b.getEmail(),
                                        "similarity_score", String.valueOf(MatchController.getSimilarity(u.getEmail(), b.getEmail()))),
                                "UserSimilarities");
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void pauseThread() {
        synchronized (lock) {
            shouldWait = true;
        }
    }

    public void resumeThread() {
        synchronized (lock) {
            shouldWait = false;
            lock.notify();
        }
    }
}
