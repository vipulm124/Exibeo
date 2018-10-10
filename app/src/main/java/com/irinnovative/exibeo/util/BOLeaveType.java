package com.irinnovative.exibeo.util;

public class BOLeaveType {

	public BOLeaveType(int id, String LeaveType) {
		LeaveTypeID = id;
		this.LeaveType = LeaveType;
	}

	public int getLeaveTypeID() {
		return LeaveTypeID;
	}

	public void setLeaveTypeID(int leaveTypeID) {
		LeaveTypeID = leaveTypeID;
	}

	public String getLeaveType() {
		return LeaveType;
	}

	public void setLeaveType(String leaveType) {
		LeaveType = leaveType;
	}

	private int LeaveTypeID;
	private String LeaveType;

}
