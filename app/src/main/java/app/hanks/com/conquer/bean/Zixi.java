package app.hanks.com.conquer.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 自习的实体类【 2014-11-23 19:59:34】
 * @author zyh
 */
public class Zixi extends BmobObject {

	private int id;
	private User user;
	/**
	 * 课程的名字
	 */
	private String name;

	/**
	 * 自习的时间
	 */
	private long time;

	/**
	 * 自习的标签
	 */
	private String label;
	/**
	 * 是否提醒
	 */
	private boolean isShare;
	/**
	 * 自习的笔记
	 */
	private String note;
	/**
	 * 卡片背景网络路径
	 */
	private String cardBgUrl;
	/**
	 * at的好友列表，存放好友
	 */
	private List<String> atFriends;
	/**
	 * 录音的网络路径
	 */
	private String audioUrl;
	/**
	 * 是否已经被提醒过
	 */
	private boolean hasAlerted;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isShare() {
		return isShare;
	}

	public void setShare(boolean isShare) {
		this.isShare = isShare;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCardBgUrl() {
		return cardBgUrl;
	}

	public void setCardBgUrl(String cardBgUrl) {
		this.cardBgUrl = cardBgUrl;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public List<String> getAtFriends() {
		return atFriends;
	}

	public void setAtFriends(List<String> atFriends) {
		this.atFriends = atFriends;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isHasAlerted() {
		return hasAlerted;
	}

	public void setHasAlerted(boolean hasAlerted) {
		this.hasAlerted = hasAlerted;
	}

	@Override
	public String toString() {
		return "Zixi [id=" + id + ", user=" + user + ", name=" + name + ", time=" + time + ", label=" + label + ", isShare=" + isShare
				+ ", note=" + note + ", cardBgUrl=" + cardBgUrl + ", atFriends=" + atFriends + ", audioUrl=" + audioUrl + ", hasAlerted="
				+ hasAlerted + "]";
	}

	
}
