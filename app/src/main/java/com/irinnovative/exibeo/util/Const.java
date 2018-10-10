package com.irinnovative.exibeo.util;

public class Const {

	public static final String DEBUG_TAG = "Exibeo";
	public static final int RESPONSE_100 = 100;
	public static final int RESPONSE_FOCUS_ACKNOWLEDGED_SUCCESS = 120;

	public static String URL_API_BASE = "http://api.exibeo.co.za/ExibeoService.svc/";
	public static String URL_SEPARATOR = "/";

	public static String URL_LATESTVERSION = URL_API_BASE
			+ "LatestVersion" + URL_SEPARATOR;
	public static String URL_API_LOGIN = URL_API_BASE + "UserLogin/";
	public static final String URL_API_GET_FOCUS_POINT = URL_API_BASE
			+ "FocusPoint/12-12-12" + URL_SEPARATOR;
	public static final String URL_API_ACKNOWLEDGE_FOCUS_POINT = URL_API_BASE
			+ "Acknowledgment" + URL_SEPARATOR;
	public static final String URL_API_APPLY_LEAVE = URL_API_BASE
			+ "InsertLeaveV2" + URL_SEPARATOR;

	public static final String URL_API_SUBMIT_AUDIT = URL_API_BASE
			+ "InsertAnswer" + URL_SEPARATOR;
	public static final String URL_API_SELECT_STORE = URL_API_BASE
			+ "AuditStores" + URL_SEPARATOR;
	public static final String URL_API_STORE_VISIT_START = URL_API_BASE
			+ "VIsitStartNew" + URL_SEPARATOR;
	public static final String URL_API_STORE_VISIT_END = URL_API_BASE
			+ "VIsitEndNew" + URL_SEPARATOR;
	public static final String URL_API_INSERT_ANSWER_NEW = URL_API_BASE
			+ "InsertAnswernew" + URL_SEPARATOR;

	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_REPORT_TO = "ReportTo";
	public static final String EXTRA_COMPANY = "Store";
	public static final String EXTRA_TOKEN = "token";
	public static final String EXTRA_EMAIL_ID = "emailID";
	public static final String EXTRA_STORE_ID = "store";
	public static final String EXTRA_STORE_LIST = "storeList";
	public static final String EXTRA_STORE_LIST_IDS = "storeListIds";
	public static final String EXTRA_VISIT_ID = "visitID";

	public static final boolean CONST_IS_TEST = false;
	public static final String TEST_EMAIL = "area@gmail.com";
	public static final String TEST_TOKEN = "WtZSt2Xlrb";

	public static final String PREF_TOKEN = "prefToken";
	public static final String PREF_SHOULD_REMEMBER = "prefRemember";
	public static final String PREF_EMAIL = "prefEmail";
	public static final String PREF_NAME = "prefName";
	public static final String PREF_POSITION = "prefPosition";
	public static final String PREF_REPORT_TO = "prefReportTo";
	public static final String PREF_COMPANY = "prefCompany";

	public static final int RESPONSE_FOCUS_ALREADY_SEEN = 220;
	public static final int RESPONSE_TOKEN_NOT_MATCHED = 235;
	public static final int RESPONSE_FAILED_200 = 200;
	public static final int RESPONSE_AUDIT_SUCCESS = 106;
	public static final String URL_API_GET_QUESTION = URL_API_BASE
			+ "Questions" + URL_SEPARATOR;
	public static final String PREF_COUNT = "prefCount";
	public static final String URL_API_LOGOUT = URL_API_BASE + "Logout"
			+ URL_SEPARATOR;
	public static final int RESPONSE_LOGOUT_SUCCESS = 103;
	public static final int RESPONSE_FAILED_250 = 250;

}
