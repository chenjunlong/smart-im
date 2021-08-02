package com.smart.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Controller
@RequestMapping("/p")
public class ViewController {


    /**
     * http://localhost:8003/v1/smart-im/p/meeting/1001
     */
    @GetMapping(value = "/meeting/{meetingId}")
    public String meeting(@PathVariable long meetingId, Model model) {
        model.addAttribute("meetingId", meetingId);
        return "meeting";
    }

}
