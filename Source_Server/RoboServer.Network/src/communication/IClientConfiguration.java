package communication;

public interface IClientConfiguration {

	public void setSessionId(int sessionId);
	public int getSessionId();
	
	public void setIpAddress(String ipAddress);
	public String getIpAddress();
	
	public void setPort(int port);
	public int getPort();
}