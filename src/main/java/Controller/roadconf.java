package Controller;

import com.alibaba.fastjson.JSON;
import dao.HbaseUtils;
import pojo.Data;

import java.util.HashMap;

/**
 * @Author: Zsyu
 * @Date: 19-8-5 下午7:12
 */
public class roadconf {
    public String Jsonin;
    public String Jsonout;

    public roadconf(String Jsonin) {
        this.Jsonin = Jsonin;
    }

    public String view() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        Data d = JSON.parseObject(Jsonin, Data.class);
        String rid = d.getRid();
        HashMap map = (HashMap) hbaseUtils.selectData("conf", rid);
        Jsonout = JSON.toJSONString(map);
        return Jsonout;
    }

    public String add() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        Data d = JSON.parseObject(Jsonin, Data.class);
        String rid = d.getRid();
        String mid = d.getMid();
        String begin = d.getBegin();
        String end = d.getEnd();
        String pos = d.getPos();
        hbaseUtils.insertData("conf", rid, "road", "mid", mid);
        hbaseUtils.insertData("conf", rid, "road", mid + "begin", begin);
        hbaseUtils.insertData("conf", rid, "road", mid + "end", end);
        hbaseUtils.insertData("conf", rid, "road", mid + "pos", pos);
        Jsonout = "t";
        return Jsonout;
    }

    public String delete() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        Data d = JSON.parseObject(Jsonin, Data.class);
        String rid = d.getRid();
        String mid = d.getMid();
        hbaseUtils.insertData("conf", rid, "road", "mid", mid);
        hbaseUtils.insertData("conf", rid, "road", mid + "begin", "");
        hbaseUtils.insertData("conf", rid, "road", mid + "end", "");
        hbaseUtils.insertData("conf", rid, "road", mid + "pos", "");
        Jsonout = "t";
        return Jsonout;
    }
}
