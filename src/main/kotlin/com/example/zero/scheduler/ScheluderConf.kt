package com.example.zero.scheduler

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "app", name = ["schedule.enabled"], havingValue = "true")
class ScheluderConf
