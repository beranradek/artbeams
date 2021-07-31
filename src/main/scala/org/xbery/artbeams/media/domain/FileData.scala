package org.xbery.artbeams.media.domain

import org.springframework.http.MediaType

/**
  * File within media gallery.
  *
  * @author Radek Beran
  */
case class FileData(filename: String, contentType: String, size: Long, data: Array[Byte], privateAccess: Boolean, width: Option[Int], height: Option[Int]) {

  def getMediaType(): MediaType = {
    if (this.contentType != null) {
      if (this.contentType.contains("/")) {
        val parts = this.contentType.split("/")
        new MediaType(parts(0), parts(1))
      } else {
        new MediaType(this.contentType)
      }
    } else {
      MediaType.APPLICATION_OCTET_STREAM
    }
  }
}
