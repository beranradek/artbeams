import java.time.ZoneId

/**
 * @author Radek Beran
 */
object Dates {
    val AppZoneIdString = "Europe/Prague" // usable as constant in annotations
    val AppZoneId = ZoneId.of(AppZoneIdString)
}
