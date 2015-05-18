package app.hanks.com.conquer.bean;

public class Day {

	private int year;
	private int month;
	private int day;
	private boolean isToday;
	public Day(int year, int month, int day,boolean isToday) {
		this.year = year;
		this.month = month;
		this.day  =day;
		this.isToday = isToday;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public boolean isToday() {
		return isToday;
	}
	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}
}
