package com.glovoapp.backender;

import com.glovoapp.backender.business.CourierNotFoundException;
import com.glovoapp.backender.business.ICore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {
    private final String welcomeMessage;
    private final ICore core;

    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage, ICore core) {
        this.welcomeMessage = welcomeMessage;
        this.core = core;
    }

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return welcomeMessage;
    }

    @RequestMapping("/orders")
    @ResponseBody
    List<OrderVM> orders() {
        return core.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping("/orders/{courierId}")
    @ResponseBody
    List<OrderVM> ordersForCourier(@PathVariable String courierId) {
        try {
            return core.findByCourierId(courierId)
                    .stream()
                    .map(order -> new OrderVM(order.getId(), order.getDescription()))
                    .collect(Collectors.toList());
        }
        catch (CourierNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        SpringApplication.run(API.class);
    }
}
