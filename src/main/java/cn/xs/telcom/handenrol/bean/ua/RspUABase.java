package cn.xs.telcom.handenrol.bean.ua;

public class RspUABase {
	private int code;
	private String msg;
	
	
	public RspUABase(){}
	
	
	private RspUABase(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public boolean isSuccess() {
		return 0 == this.code;
	}
	
	public static RspUABase error(String msg){
		return new RspUABase(500, msg);
	}
	
	public static RspUABase success(String msg){
		return new RspUABase(0, msg);
	}
	
	public static RspUABase success(){
		return new RspUABase(0, "");
	}

}
