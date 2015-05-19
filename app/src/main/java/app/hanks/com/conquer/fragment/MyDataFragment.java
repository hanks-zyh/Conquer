package app.hanks.com.conquer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.util.L;
import app.hanks.com.conquer.util.T;
import app.hanks.com.conquer.util.UserDataUtils;

public class MyDataFragment extends BaseFragment {

	private TextView tv_id;
	private TextView et_school, et_dep, et_year, et_city, et_phone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_mydata, container, false);
		init(v);
		return v;
	}

	/**
	 * 初始化
	 * @param v
	 */
	private void init(View v) {
		tv_id = (TextView) v.findViewById(R.id.tv_id);
		et_city = (TextView) v.findViewById(R.id.et_city);
		et_school = (TextView) v.findViewById(R.id.et_school);
		et_dep = (TextView) v.findViewById(R.id.et_dep);
		et_year = (TextView) v.findViewById(R.id.et_year);
		et_phone = (TextView) v.findViewById(R.id.et_phone);
		// et_love_status = (EditText) v.findViewById(R.id.et_love_status);
		initUserData();
	}

	/**
	 * 初始化
	 */
	public void initUserData() {
		if (currentUser != null) {
			L.e("initUserData", currentUser.toString());
			tv_id.setText(currentUser.getUsername());
			et_city.setText(currentUser.getCity());
			et_phone.setText(currentUser.getPhoneNum());
			et_school.setText(currentUser.getSchool());
			et_dep.setText(currentUser.getDep());
			et_year.setText(currentUser.getYear());
			et_city.setText(currentUser.getCity());
			// et_love_status.setText(currentUser.getLoveStatus());
		}
	}

	/**
	 * 保存个人基本信息
	 */
	public void saveMyData() {
		String school = et_school.getText().toString().trim();
		String dep = et_dep.getText().toString().trim();
		String year = et_year.getText().toString().trim();
		String city = et_city.getText().toString().trim();
		String phoneNum = et_phone.getText().toString().trim();
		// String loveStatus = et_love_status.getText().toString().trim();
		if (currentUser != null) {
			currentUser.setSchool(school);
			currentUser.setDep(dep);
			currentUser.setYear(year);
			currentUser.setPhoneNum(phoneNum);
			currentUser.setCity(city);
			// currentUser.setLoveStatus(loveStatus);
			UserDataUtils.UpdateUserData(context, currentUser, new UserDataUtils.UpdateUserDataListener() {
				public void onSuccess() {
					T.show(context, "保存成功");
				}

				public void onFailure(int errorCode, String msg) {
					T.show(context, "保存失败");
				}
			});
		} else {
			// 登录
		}

	}
}
