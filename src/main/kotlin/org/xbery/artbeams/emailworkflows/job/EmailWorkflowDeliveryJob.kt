package org.xbery.artbeams.emailworkflows.job

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.xbery.artbeams.emailworkflows.service.EmailWorkflowDeliveryService

@Component
class EmailWorkflowDeliveryJob(private val deliveryService: EmailWorkflowDeliveryService) {
    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT30S")
    fun deliverDue() = deliveryService.deliverDue()
}
