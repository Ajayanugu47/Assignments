package com.example.demo44.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

@RestController
public class FibController {

    @GetMapping("fib/{upto}")
    public List<Integer> printFib(@PathParam("upto") Integer uptoNumber){
        List<Integer> output = Arrays.asList(1,1,2,3,5,7,8);
        return output;
    }
    
}
