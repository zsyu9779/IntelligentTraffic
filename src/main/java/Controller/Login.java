package Controller;

import dao.HbaseUtils;
import dao.RedisOperating;
import com.alibaba.fastjson.JSON;
import pojo.User;

import java.util.Map;

/**
 * @Author: Zsyu
 * @Date: 19-8-5 下午7:10
 */
public class Login {
    public String data;
    public String json;

    public Login(String data) {
        this.data = data;
    }

    public String Return(String data) {

        System.out.println("aaa" + data);
        User user = JSON.parseObject(data, User.class);
        String uid = user.getUid();
        String pwd = user.getPwd();
        if (RedisOperating.exits(uid)) {
            if (pwd == RedisOperating.get(uid)) {
                return "t";
            } else
                return "f";
        } else {
            HbaseUtils hbaseUtils = new HbaseUtils();
            Map<String, String> map = hbaseUtils.selectData("user", uid);
            String res = map.get("pwd");
            if (pwd == res)
                return "t";
            else
                return "f";
        }

    }


}
