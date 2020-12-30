package app.openconnect.containers;

public class Permissions {
    String type;
    String user;
    String group;
    String other;
    String symlink;
    int permissions;

    public String getSymlink() {
        return this.symlink;
    }

    public String getType() {
        return type;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public String getUserPermissions() {
        return this.user;
    }

    public String getGroupPermissions() {
        return this.group;
    }

    public String getOtherPermissions() {
        return this.other;
    }

    public void setSymlink(String symlink) {
        this.symlink = symlink;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public void setUserPermissions(String user) {
        this.user = user;
    }

    public void setGroupPermissions(String group) {
        this.group = group;
    }

    public void setOtherPermissions(String other) {
        this.other = other;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


}
