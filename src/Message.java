

public class Message {
	
	private  int to;
	private int from;
	private String msg;
	private String time;

	private int type;

	private AppServer.SocketThread thread;
	public int getTo() {
		return to;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AppServer.SocketThread getThread() {
		return thread;
	}
	public void setThread(AppServer.SocketThread thread) {
		this.thread = thread;
	}
	
}
