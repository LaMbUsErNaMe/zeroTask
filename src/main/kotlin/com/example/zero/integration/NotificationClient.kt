package com.example.zero.integration

import com.example.zero.integration.dto.notification.NotificationRequest
import com.example.zero.properties.RestProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NotificationClient(
    @Qualifier("notificationWebClient") webClient: WebClient,
    properties: RestProperties,
) : BaseBlockingClient(webClient) {
    private val path = properties.webClients.notification.path

    fun send(request: NotificationRequest) = postBody(path, request)
}
