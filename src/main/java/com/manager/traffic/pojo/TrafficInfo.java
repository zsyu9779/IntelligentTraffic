package com.manager.traffic.pojo;

import lombok.Data;

import java.util.List;

/**
 * @Author: Zsyu
 * @Date: 19-8-6 上午9:39
 */
@Data
public class TrafficInfo {
    private String uid;

    private String mid;

    private String rid;//路口id

    private String uname;

    private String pwd;

    private String begin;

    private String end;

    private String time;

    private String pos;

    private List<String> list;


}
