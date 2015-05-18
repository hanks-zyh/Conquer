package app.hanks.com.conquer.bean;

/**
 * 留言板的bean
 * @author zyh
 */
public class MyMsg {
	private User user;
	private String content;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MyMsg() {
	}

	public MyMsg(User user, String content) {
		this.user = user;
		this.content = content;
	}

}
