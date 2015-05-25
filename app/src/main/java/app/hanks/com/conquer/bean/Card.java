package app.hanks.com.conquer.bean;

import cn.bmob.v3.BmobObject;

/**
 * 提醒别人任务，或者陪别人上任务的卡片
 * @author wmf
 */
public class Card extends BmobObject {

	@Override
	public String toString() {
		return "Card [type=" + type + ", fid=" + fid + ", fusername=" + fusername + ", fnick=" + fnick + ", fphotoUrl=" + favatar + ", tId="
				+ tId + ", zixiName=" + zixiName + ", time=" + time + ", content=" + content + ", audioUrl=" + audioUrl + ", imgUrl=" + imgUrl
				+ "]";
	}

	/** 卡片类型 0.提醒卡 1.请求陪人任务卡（勾搭卡） */
	private int type;
	/** 卡片发起人的Id */
	private String fid;
	/** 卡片发起人的username */
	private String fusername;
	/** 卡片发起人的nickname */
	private String fnick;
	/** 卡片发起人的photoUrl */
	private String favatar;

	public String getFavatar() {
		return favatar;
	}

	public void setFavatar(String favatar) {
		this.favatar = favatar;
	}

	/** 卡片接受人的Id */
	private String tId;

	/** 任务设置的Id */
	private int zixiId;
	/** 任务设置的时间 */
	private String zixiName;
	/** 任务设置的时间 */
	private long time;
	/** 卡片的鼓励的话 */
	private String content;
	/** 卡片的图片的url */
	private String audioUrl;
	/** 卡片的音频的url */
	private String imgUrl;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getFusername() {
		return fusername;
	}

	public void setFusername(String fusername) {
		this.fusername = fusername;
	}

	public String getFnick() {
		return fnick;
	}

	public void setFnick(String fnick) {
		this.fnick = fnick;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getZixiName() {
		return zixiName;
	}

	public void setZixiName(String zixiName) {
		this.zixiName = zixiName;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public int getZixiId() {
		return zixiId;
	}

	public void setZixiId(int zixiId) {
		this.zixiId = zixiId;
	}



}
