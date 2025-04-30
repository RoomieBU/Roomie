package Controller;

import Database.*;
import Tools.UserMatchInteraction;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Continuously check the UserMatchInteractions table for 2 users that have matched with each other,
 * and then add matches to UserMatches.
 *
 * For the sake of consolidation, this class will also check if two people accept each other,
 * and if so, check off their "accepted" column in the GroupChats table.
 */
public class MatchingScannerController extends Thread{
    private final Object lock = new Object();
    private boolean shouldWait = false;

    public void run() {
        try {
            UserMatchInteractionDao dao = new UserMatchInteractionDao(SQLConnection.getConnection());
            while (true) {
                List<UserMatchInteraction> umi = dao.getAllMatchedUsers();


                for (UserMatchInteraction u : umi) {
                    if (!dao.exists(Map.of("email1", u.getUser(), "email2", u.getShownUser()), "UserMatches") &&
                        !dao.exists(Map.of("email1", u.getShownUser(), "email2", u.getUser()), "UserMatches")) {
                        dao.insert(Map.of("email1", u.getUser(), "email2", u.getShownUser()), "UserMatches");
                        dao.insert(Map.of("email1", u.getUser(), "email2", u.getShownUser()), "GroupChats");
                    }
                }

                // Also, check all GroupChats and see if everyone is accepted
                for (GroupChat gc : dao.getAllGroupchats()) {
                    if (dao.isAllAccepted(gc.getGroupchatId())) { // <- SEND EVERYONE TO PHASE 2
                        dao.set(Map.of("confirmed", "true"), gc.getGroupchatId(), "GroupChats");
                        dao.setAllUserStatus();
                    }
                }

                Thread.sleep(15000); // 15 seconds
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