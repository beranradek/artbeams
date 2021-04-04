package org.xbery.artbeams.media.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import org.xbery.artbeams.media.domain.FileData

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
      val ps = conn.prepareStatement("INSERT INTO media (filename, content_type, size, data, private_access) VALUES (?, ?, ?, ?, ?)")
      val inputStream = file.getInputStream()
      try {
        val filename = file.getOriginalFilename()
        ps.setString(1, filename)
        ps.setString(2, file.getContentType())
        ps.setLong(3, file.getSize())
        ps.setBinaryStream(4, inputStream, file.getSize())
        ps.setBoolean(5, privateAccess)
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
  def deleteFile(filename: String): Boolean = {
    var result: Boolean = false
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("DELETE FROM media WHERE filename = ?")
      try {
        ps.setString(1, filename)
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
    * Retrieves file data from dababase; or None data if not found.
    * @param filename
    * @return
    */
  def findFile(filename: String): Option[FileData] = {
    var fileOpt: Option[FileData] = None
    val conn = dataSource.getConnection()
    try {
      val ps = conn.prepareStatement("SELECT filename, content_type, size, data, private_access FROM media WHERE filename = ?")
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
            fileOpt = Some(
              FileData(
                filename,
                if (contentType == null) MediaType.APPLICATION_OCTET_STREAM_VALUE else contentType,
                size,
                data,
                privateAccess
              )
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
    fileOpt
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
      val ps = conn.prepareStatement("SELECT filename, content_type, size, private_access FROM media")
      try {
        val rs = ps.executeQuery()
        try {
          while (rs.next()) {
            val filename = rs.getString(1)
            val contentType = rs.getString(2)
            val size = rs.getLong(3)
            val privateAccess = rs.getBoolean(4)
            files += FileData(
                filename,
                if (contentType == null) MediaType.APPLICATION_OCTET_STREAM_VALUE else contentType,
                size,
                Array(),
                privateAccess
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
}
