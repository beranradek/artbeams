package org.xbery.artbeams.media.repository

import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import org.xbery.artbeams.common.parser.Parsers
import org.xbery.artbeams.media.domain.FileData

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.sql.Types
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.sql.DataSource
import scala.collection.mutable.ListBuffer

/**
  * Media repository.
  * See also https://jdbc.postgresql.org/documentation/80/binary-data.html
  * @author Radek Beran
  */
@Repository
class MediaRepository @Inject()(dataSource: DataSource) {

  /**
    * Stores file in database.
    * @param file
    * @return
    */
  def storeFile(file: MultipartFile, privateAccess: Boolean): Boolean = {
    var result: Boolean = false
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("INSERT INTO media (filename, content_type, size, data, private_access, width, height) VALUES (?, ?, ?, ?, ?, ?, ?)")
      val inputStream = file.getInputStream()
      try {
        val filename = file.getOriginalFilename()
        val size = file.getSize()
        val contentType = file.getContentType
        ps.setString(1, filename)
        ps.setString(2, contentType)
        ps.setLong(3, file.getSize())
        val isImg = isImage(contentType)
        ps.setBoolean(5, privateAccess)
        var width: Option[Int] = None
        var height: Option[Int] = None

        if (isImg) {
          // We will read bytes of image twice:
          // Once for getting image dimensions and second when storing binary data to database
          val baos = new ByteArrayOutputStream()
          IOUtils.copy(inputStream, baos)
          val imgBytes = baos.toByteArray

          val (w, h) = getImageDimensions(imgBytes, contentType)
          width = w
          height = h

          ps.setBinaryStream(4, new ByteArrayInputStream(imgBytes), size)
        } else {
          ps.setBinaryStream(4, inputStream, size)
        }

        if (width.isEmpty) ps.setNull(6, Types.INTEGER) else ps.setInt(6, width.get)
        if (height.isEmpty) ps.setNull(7, Types.INTEGER) else ps.setInt(7, height.get)

        val updatedCount = ps.executeUpdate()
        result = updatedCount == 1
      } finally {
        if (ps != null) {
          ps.close()
        }
        if (inputStream != null) {
          inputStream.close()
        }
      }
    } finally {
      if (conn != null) {
        conn.close()
      }
    }
    result
  }

  /**
    * Deletes file from database.
    * @param filename
    * @return
    */
  def deleteFile(filename: String, size: Option[String]): Boolean = {
    val widthOpt = size.flatMap(Parsers.parseIntOpt)
    var result: Boolean = false
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("DELETE FROM media WHERE filename = ?" + widthOpt.map(width => " AND width = ?").getOrElse(""))
      try {
        ps.setString(1, filename)
        if (widthOpt.isDefined) {
          ps.setInt(2, widthOpt.get)
        }
        val updatedCount = ps.executeUpdate()
        result = updatedCount == 1
      } finally {
        if (ps != null) {
          ps.close()
        }
      }
    } finally {
      if (conn != null) {
        conn.close()
      }
    }
    result
  }

  /**
    * Retrieves file data from database; or None data if not found.
    *
    * @param filename
    * @return
    */
  def findFile(filename: String, size: Option[String]): Option[FileData] = {
    var files = ListBuffer[FileData]()
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("SELECT filename, content_type, size, data, private_access, width, height FROM media WHERE filename = ?")
      ps.setString(1, filename)
      try {
        val rs = ps.executeQuery()
        try {
          while (rs.next()) {
            val filename = rs.getString(1)
            val contentType = rs.getString(2)
            val size = rs.getLong(3)
            val data = rs.getBytes(4)
            val privateAccess = rs.getBoolean(5)
            val width = rs.getInt(6)
            val height = rs.getInt(7)
            files += FileData(
                filename,
                if (contentType == null) MediaType.APPLICATION_OCTET_STREAM_VALUE else contentType,
                size,
                data,
                privateAccess,
                Option(width),
                Option(height),
              )
          }
        } finally {
          if (rs != null) {
            rs.close()
          }
        }
      } finally {
        if (ps != null) {
          ps.close()
        }
      }
    } finally {
      if (conn != null) {
        conn.close()
      }
    }
    selectFile(files.toSeq, size)
  }

  /**
    * Retrieves metadata of files from dababase, does not load their binary data.
    * @param filename
    * @return
    */
  def listFiles(): Seq[FileData] = {
    var files = new ListBuffer[FileData]()
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("SELECT filename, content_type, size, private_access, width, height FROM media")
      try {
        val rs = ps.executeQuery()
        try {
          while (rs.next()) {
            val filename = rs.getString(1)
            val contentType = rs.getString(2)
            val size = rs.getLong(3)
            val privateAccess = rs.getBoolean(4)
            val width = rs.getInt(5)
            val height = rs.getInt(6)
            files += FileData(
                filename,
                if (contentType == null) MediaType.APPLICATION_OCTET_STREAM_VALUE else contentType,
                size,
                Array(),
                privateAccess,
                Option(width),
                Option(height),
              )
          }
        } finally {
          if (rs != null) {
            rs.close()
          }
        }
      } finally {
        if (ps != null) {
          ps.close()
        }
      }
    } finally {
      if (conn != null) {
        conn.close()
      }
    }
    files.toSeq
  }

  private def isImage(contentType: String) = contentType.startsWith("image/")

  private def isWebpImage(contentType: String) = contentType == "image/webp"

  private def getWebpImageDimensions(imgBytes: Array[Byte]): Option[(Int, Int)] = {
    if (imgBytes.length < 30) {
      None
    } else {
      val width = (imgBytes(27).asInstanceOf[Int] & 0xff) << 8 | (imgBytes(26).asInstanceOf[Int] & 0xff)
      val height = (imgBytes(29).asInstanceOf[Int] & 0xff) << 8 | (imgBytes(28).asInstanceOf[Int] & 0xff)
      Some((width, height))
    }
  }

  private def getImageDimensions(imgBytes: Array[Byte], contentType: String): (Option[Int], Option[Int]) = {
    var width: Option[Int] = None
    var height: Option[Int] = None
    // Special support for webp images
    if (isWebpImage(contentType)) {
      val dimensionsOpt = getWebpImageDimensions(imgBytes)
      for {
        (w, h) <- dimensionsOpt
      } {
        width = Some(w)
        height = Some(h)
      }
    } else {
      val bufferedImg = ImageIO.read(new ByteArrayInputStream(imgBytes))
      if (bufferedImg != null) {
        width = Option(bufferedImg.getWidth)
        height = Option(bufferedImg.getHeight)
      }
    }
    (width, height)
  }

  /**
    * Select one of available files based on specified optional size specification.
    * @param files
    * @param size
    * @return
    */
  private def selectFile(files: Seq[FileData], size: Option[String]): Option[FileData] = {
    if (files.isEmpty) None else {
      val widthOpt = size.flatMap(Parsers.parseIntOpt)
      widthOpt match {
        case None => files.headOption
        case Some(width) =>
          files.find(f => f.width.isDefined && f.width.get == width) match {
            case Some(file) => Some(file)
            case None =>
              val minWidth = width / 2
              val maxWidth = width * 2
              files.find(f => f.width.isDefined && valueInInterval(f.width.get, minWidth, maxWidth)) match {
                case Some(file) => Some(file)
                case None =>
                  if (files.forall(f => f.width.isDefined && f.width.get > width)) {
                    // All images are bigger than requested size, choose the smallest
                    Option(files.minBy(f => f.width.get))
                  } else if (files.forall(f => f.width.isDefined && f.width.get < width)) {
                    // All images are smaller than requested size, choose the biggest
                    Option(files.maxBy(f => f.width.get))
                  } else {
                    files.find(f => f.width.isDefined && f.width.get > width) match {
                      case Some(file) => Some(file)
                      case None => files.headOption
                    }
                  }
              }
          }
      }
    }
  }

  private def valueInInterval(value: Int, intMin: Int, intMax: Int): Boolean = value >= intMin && value <= intMax
}
