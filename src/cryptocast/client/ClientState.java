package cryptocast.client;

import java.io.Serializable;

public class ClientState implements Serializable {
    private static final long serialVersionUID = -6740813261198764588L;
    
    private ServerHistory serverHistory = new ServerHistory();
    private String hostname = "";

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public ServerHistory getServerHistory() {
        return serverHistory;
    }

    public void setServerHistory(ServerHistory serverHistory) {
        this.serverHistory = serverHistory;
    }
}
