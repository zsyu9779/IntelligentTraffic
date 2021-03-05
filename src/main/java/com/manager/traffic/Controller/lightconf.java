package com.manager.traffic.Controller;

import com.alibaba.fastjson.JSON;
import com.manager.traffic.dao.HbaseUtils;
import com.manager.traffic.entity.TrafficInfo;

import java.util.HashMap;

/**
 * @Author: Zsyu
 * @Date: 19-8-5 下午7:12
 */
public class lightconf {
    public String Jsonin;
    public String Jsonout;

    public lightconf(String Jsonin) {
        this.Jsonin = Jsonin;
    }

    public String view() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        TrafficInfo d = JSON.parseObject(Jsonin, TrafficInfo.class);
        String rid = d.getRid();
        HashMap map = (HashMap) hbaseUtils.selectData("conf", rid);
        Jsonout = JSON.toJSONString(map);
        return Jsonout;
    }

    public String add() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        TrafficInfo d = JSON.parseObject(Jsonin, TrafficInfo.class);
        String rid = d.getRid();
        String mid = d.getMid();
        String begin = d.getBegin();
        String end = d.getEnd();
        String time = d.getTime();
        hbaseUtils.insertData("conf", rid, "light", "mid", mid);
        hbaseUtils.insertData("conf", rid, "light", mid + "begin", begin);
        hbaseUtils.insertData("conf", rid, "light", mid + "end", end);
        hbaseUtils.insertData("conf", rid, "light", mid + "time", time);
        Jsonout = "t";
        return Jsonout;
    }

    public String delete() {
        HbaseUtils hbaseUtils = new HbaseUtils();
        TrafficInfo d = JSON.parseObject(Jsonin, TrafficInfo.class);
        String rid = d.getRid();
        String mid = d.getMid();
        hbaseUtils.insertData("conf", rid, "light", "mid", mid);
        hbaseUtils.insertData("conf", rid, "light", mid + "begin", "");
        hbaseUtils.insertData("conf", rid, "light", mid + "end", "");
        hbaseUtils.insertData("conf", rid, "light", mid + "time", "");
        Jsonout = "t";
        return Jsonout;
    }
}
