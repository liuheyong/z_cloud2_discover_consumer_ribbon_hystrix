package com.cloud2.dishystrix.web;

import com.cloud2.commons.constants.Constants;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DcController {

    public static final Logger logger = LoggerFactory.getLogger(DcController.class);

    @Autowired
    ConsumerService consumerService;

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping(Constants.CLOUD2 + "/dc")
    @HystrixCommand(fallbackMethod = "fallback")
    public String dc() throws InterruptedException {
        Thread.sleep(5000);
        String services = "Services: " + discoveryClient.getServices();
        logger.info(services);
        return services;
    }

    @GetMapping(Constants.CLOUD2 + "/consumer")
    public String consumer() {
        return consumerService.consumer();
    }

    public String fallback() {
        return "fallback";
    }

    @Service
    class ConsumerService {

        @Autowired
        RestTemplate restTemplate;

        /**
         * @description: 有一点小坑，fallback的参数要和consumer保持一致
         */
        @HystrixCommand(fallbackMethod = "fallback")
        public String consumer() {
            return restTemplate.getForObject("http://localhost:8124/cloud2/dc", String.class);
        }

        public String fallback() {
            return "fallback";
        }

    }

}
