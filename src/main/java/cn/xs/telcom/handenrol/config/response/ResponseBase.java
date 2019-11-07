package cn.xs.telcom.handenrol.config.response;

import java.util.HashMap;

/**
 * @author kenny_peng
 * @created 2019/7/25 10:44
 */
public class ResponseBase extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	private static final Integer SUCCESS = 0;
    private static final Integer Failed = 500;

    public ResponseBase(){
        put("code",SUCCESS);
        put("msg","操作成功");
    }


    public static ResponseBase ok(){
        return new ResponseBase();
    }

    public static ResponseBase ok(Object o){
        ResponseBase responseTemplate = new ResponseBase();
        responseTemplate.put("code", SUCCESS);
        responseTemplate.put("msg", o);
        return responseTemplate;
    }

    public static ResponseBase error(){
        return ResponseBase.error("操作失败");
    }

    public static ResponseBase error(Object o){
        ResponseBase rst = new ResponseBase();
        rst.put("code", Failed);
        rst.put("msg", o);
        return rst;
    }

    @Override
    public ResponseBase put(String key, Object value){
        super.put(key, value);
        return this;
    }

}
