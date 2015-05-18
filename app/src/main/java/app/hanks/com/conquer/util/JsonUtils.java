package app.hanks.com.conquer.util;


import app.hanks.com.conquer.bean.User;

public class JsonUtils {

	/**
	 * 将自习邀请转换成Json串
	 * 
	 * @param time
	 * @param zixiName
	 * @return
	 */
	public static String getZixiInviteJson(User fromUser, String time, String zixiName) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"fId:\"" + "\"" + fromUser.getObjectId() + "\",");
		json.append("\"fNick:\"" + "\"" + fromUser.getNick() + "\",");
		json.append("\"time:\"" + "\"" + time + "\",");
		json.append("\"zixiName:\"" + "\"" + zixiName + "\"");
		json.append("}");
		return json.toString();
	}

	public static void main(String[] args) {
		User fromUser = new User();
		fromUser.setObjectId("f9976");
		fromUser.setNick("画虎");
		System.out.println(getZixiInviteJson(fromUser, "2014-11-30 11:20", "大学英语"));
	}
}
