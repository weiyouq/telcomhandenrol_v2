package cn.xs.telcom.handenrol.bean;

public class User {
	private Long id;

	private String userId;

	private Integer enrolType;

	private String usedUserId;

	private Integer userState;

	public User() {

	}

	public User(Long id, String userId, Integer enrolType, String usedUserId, Integer userState) {
		this.id = id;
		this.userId = userId;
		this.enrolType = enrolType;
		this.usedUserId = usedUserId;
		this.userState = userState;
	}

	public User(String userId, Integer enrolType) {
		this.userId = userId;
		this.enrolType = enrolType;
		this.usedUserId = "";
		this.userState = 4;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getEnrolType() {
		return enrolType;
	}

	public void setEnrolType(Integer enrolType) {
		this.enrolType = enrolType;
	}

	public String getUsedUserId() {
		return usedUserId;
	}

	public void setUsedUserId(String usedUserId) {
		this.usedUserId = usedUserId;
	}
	public String getVerifyId2() {
		return userId + "_2";
		
	}

	public String getVerifyId() {
		switch (userState) {
		case 0:
			return usedUserId;
		case 1:
			return userId + "_1";
		case 2:
			return userId + "_1" + "," + userId + "_2";
		case 3:
			return usedUserId;
		case 4:
			return userId + "_1";
		default:
			return null;
		}
	}

	/**
	 * @return 0 已激活 1 预注册 2 2条预注册 3 验证关注 4未注册
	 */
	public Integer getUserState() {
		return userState;
	}

	/**
	 * @userState 0 已激活 1 预注册 2 2条预注册 3 验证关注 4未註冊
	 */
	public void setUserState(Integer userState) {
		this.userState = userState;
	}

}