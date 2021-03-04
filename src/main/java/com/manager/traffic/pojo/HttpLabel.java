package com.manager.traffic.pojo;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpLabel {
    private String uri;
    private HttpMethod method;
}
