package com.tea.common.base.constant;

public enum ProjectName {
	
	//用户系统
	UserApp("1000","UserApp","com.tea","用户服务模块"),
	UserSchedule("1001","UserSchedule","com.tea","用户消息模块"),
	UserWeb("1002","UserWeb","com.tea","用户前端模块"),
	UserAdminWeb("1003","UserAdminWeb","com.tea","用户后台模块"),
	
	Common("8888","Common","com.tea","公用模块"),
	;
	
	private String appId;
	private String appName;
	private String packageName;
	private String projectName;
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private ProjectName(String  appId,String appName,String packageName,String projectName){
		this.appId = appId;
		this.appName = appName;
		this.packageName = packageName;
		this.projectName = projectName;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}
	public String getPackageName() {
		return packageName;
	}

}
