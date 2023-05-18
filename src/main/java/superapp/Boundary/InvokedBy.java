package superapp.Boundary;

import org.apache.catalina.User;
import superapp.Boundary.User.UserId;

public class InvokedBy {

    private UserId userId;

    public InvokedBy() {

    }



    public InvokedBy(UserId userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "InvokedBy{" +
                "userId=" + userId +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}