package me.danielpf.springplaykt

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class PlayRequest(
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val time: LocalDateTime
)

data class PlayResponse(
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val originalTime: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val startTime: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val endTime: LocalDateTime
)

@RestController
class LocalDatePlayController {

    @PostMapping("/local_date_play")
    fun play(@RequestBody request: PlayRequest) =
        PlayResponse(
            request.time,
            request.time.truncatedTo(ChronoUnit.DAYS).plusDays(-1),
            request.time.truncatedTo(ChronoUnit.DAYS)
        )

}